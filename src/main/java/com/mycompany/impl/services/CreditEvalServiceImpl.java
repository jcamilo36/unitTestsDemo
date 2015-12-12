package com.mycompany.impl.services;

import com.mycompany.api.client.ICreditFirmABC;
import com.mycompany.api.dto.ClientDTO;
import com.mycompany.api.dto.CreditRequestDTO;
import com.mycompany.api.dto.EvaluateQuotaInDTO;
import com.mycompany.api.dto.IDTypeEnum;
import com.mycompany.api.dto.RiskTypeEnum;
import com.mycompany.api.services.ICreditEvalService;
import com.mycompany.api.services.IPeopleService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Types;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Implementation for the credit evaluation service.
 * Created by jcortes on 12/9/15.
 */
public class CreditEvalServiceImpl implements ICreditEvalService {

    /**
     * SQL query to create a credit.
     */
    private static final String CREATE_CREDIT =
            "INSERT INTO CREDIT (clientId, creditValue, approvalDate) VALUES (?, ?, ?)";

    /**
     * SQL query to recover the maximum past loan given to a client.
     */
    private static final String RECOVER_MAX_PAST_LOAN =
            "SELECT COALESCE (MAX(creditValue), 0) FROM CREDIT " +
                    "WHERE paidDate IS NOT NULL AND clientId = ?";

    /**
     * SQL query to recover the total amount of loans given to a client currently.
     */
    private static final String RECOVER_TOTAL_CURRENT_LOANS =
            "SELECT COALESCE (SUM(creditValue), 0) FROM CREDIT " +
                    "WHERE paidDate IS NULL AND clientId = ?";

    /**
     * People service implementation.
     */
    @Autowired
    private IPeopleService peopleService;

    /**
     * Credit firm client service.
     */
    @Autowired
    private ICreditFirmABC creditFirmABC;

    /**
     * JDBC client.
     */
    private JdbcTemplate jdbcTemplate;

    /**
     * Sets data source.
     *
     * @param dataSource the data source.
     */
    @Autowired
    public void setDataSource(final DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Evaluate a credit loan request
     * @param requestDTO DTO with request data.
     */
    public void evaluateCreditRequest(CreditRequestDTO requestDTO) {
        if (!peopleService.isClient(requestDTO.getPeopleDTO().getIdType(),
                requestDTO.getPeopleDTO().getId())) {
            peopleService.createClient(requestDTO.getPeopleDTO());
        }
        Optional<ClientDTO> opt = peopleService.getClient(requestDTO.getPeopleDTO()
                        .getIdType(), requestDTO.getPeopleDTO().getId());
        opt.ifPresent((ClientDTO clientDTO) -> {
            RiskAnalysisDTO riskAnalysisDTO = performRiskAnalysis(clientDTO);
            EvaluateQuotaInDTO evaluateQuotaInDTO =
                    new EvaluateQuotaInDTO(riskAnalysisDTO.maxPastCredit,
                            riskAnalysisDTO.totalCurrentLoans,
                            requestDTO.getCurrentSalary(),
                            riskAnalysisDTO.riskTypeEnum);
            BigDecimal quota = evaluateCreditAmount(evaluateQuotaInDTO);
            assignCredit(clientDTO, quota);
        });
    }

    /**
     * Perform a risk analysis given a client.
     * @param clientDTO DTO with client data.
     * @return a DTO with risk analysis results.
     */
    private RiskAnalysisDTO performRiskAnalysis(ClientDTO clientDTO) {
        RiskAnalysisDTO riskAnalysisDTO = new RiskAnalysisDTO();
        riskAnalysisDTO.maxPastCredit = getMaxPastCredit(clientDTO.getId());
        riskAnalysisDTO.totalCurrentLoans = getTotalCurrentLoans(clientDTO.getId());
        RiskTypeEnum risk = creditFirmABC.getRiskByClient(IDTypeEnum.forValue(clientDTO
                        .getTypeId()), clientDTO.getClientId());
        riskAnalysisDTO.riskTypeEnum = risk;
        return riskAnalysisDTO;
    }

    /**
     * Assign a credit to a client.
     * @param clientDTO DTO with client data.
     * @param amount Credit amount given.
     */
    private void assignCredit(ClientDTO clientDTO, BigDecimal amount) {
        Object[] params = new Object[]{clientDTO.getId(), amount, LocalDate.now()};
        int[] types = new int[]{Types.NUMERIC, Types.DECIMAL, Types.DATE};
        jdbcTemplate.update(CREATE_CREDIT, params, types);
    }

    /**
     * Evaluate the credit amount that can be given to a client.
     * @param inDTO DTO with data to assess a possible credit card quota.
     * @return the credit amount.
     */
    public BigDecimal evaluateCreditAmount(EvaluateQuotaInDTO inDTO) {
        BigDecimal amount = BigDecimal.ZERO;
        BigDecimal maxPastCredit = inDTO.getMaxPastCredit();
        BigDecimal totalCurrentCredit = inDTO.getTotalCurrentCredit();
        BigDecimal currentSalary = inDTO.getCurrentSalary();

        if (maxPastCredit == null || maxPastCredit.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Past credit amount should not be "
                    + "negative.");
        }
        if (totalCurrentCredit == null
                || totalCurrentCredit.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Current credit loan should not be "
                    + "negative.");
        }
        if (currentSalary == null || currentSalary.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Current salary should not be "
                    + "negative.");
        }

        if (inDTO.getRiskTypeEnum() == RiskTypeEnum.NONE) {
            amount = new BigDecimal(Math.max(currentSalary.doubleValue() * 3,
                    maxPastCredit.doubleValue())).subtract(totalCurrentCredit);
        } else if (inDTO.getRiskTypeEnum() == RiskTypeEnum.IN_ARREARS_3_MONTHS) {
            amount = new BigDecimal(Math.max(currentSalary.doubleValue() * 3.5,
                    maxPastCredit.doubleValue())).subtract(totalCurrentCredit);
        } else if (inDTO.getRiskTypeEnum() == RiskTypeEnum.IN_ARREARS_6_MONTHS) {
            amount = new BigDecimal(Math.max(currentSalary.doubleValue() * 4,
                    maxPastCredit.doubleValue())).subtract(totalCurrentCredit);
        } else if (inDTO.getRiskTypeEnum() == RiskTypeEnum.PLEDGE) {
            amount = new BigDecimal(Math.max(currentSalary.doubleValue(),
                    maxPastCredit.doubleValue())).subtract(totalCurrentCredit);
        }
        return amount;
    }

    /**
     * Return the maximum past credit amount given a client.
     * @param clientId Client internal identifier.
     * @return the maximum credit amount.
     */
    private BigDecimal getMaxPastCredit(Long clientId) {
        Object[] params = new Object[]{clientId};
        int[] types = new int[]{Types.NUMERIC};
        BigDecimal maxPastCredit = jdbcTemplate.queryForObject(RECOVER_MAX_PAST_LOAN,
                params, types, BigDecimal.class);
        return maxPastCredit;
    }

    /**
     * Return the total amount of loans given a client.
     * @param clientId Client internal identifier.
     * @return the total current loans.
     */
    private BigDecimal getTotalCurrentLoans(Long clientId) {
        Object[] params = new Object[]{clientId};
        int[] types = new int[]{Types.NUMERIC};
        BigDecimal total = jdbcTemplate.queryForObject(RECOVER_TOTAL_CURRENT_LOANS,
                params, types, BigDecimal.class);
        return total;
    }

    /**
     * DTO with result from risk analysis.
     */
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    static class RiskAnalysisDTO {
        RiskTypeEnum riskTypeEnum;
        BigDecimal maxPastCredit;
        BigDecimal totalCurrentLoans;
    }
}