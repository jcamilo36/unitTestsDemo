package com.mycompany.impl.services;

import com.mycompany.api.dto.EvaluateQuotaInDTO;
import com.mycompany.api.dto.RiskTypeEnum;
import common.ObjectBuilder;
import common.TestContextConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests credit evaluation service.
 * Created by jcortes on 12/9/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestContextConfiguration.class)
@WebAppConfiguration
public class CreditEvalServiceTest {

    /**
     * Object builder.
     */
    @Autowired
    private ObjectBuilder objectBuilder;

    /**
     * Expected exception.
     */
    @Rule
    public ExpectedException exception = ExpectedException.none();

    /**
     * CreditEvalService implementation to be tested.
     */
    private CreditEvalServiceImpl creditEvalService;

    /**
     * JDBC client.
     */
    private JdbcTemplate jdbcTemplate;

    /**
     * Setup common tests configurations.
     */
    @Before
    public void setupTests() {
        creditEvalService = objectBuilder.buildCreditEvalServiceImpl();
        jdbcTemplate = new JdbcTemplate(objectBuilder.getTestDataSource());
    }

    /**
     * Clean database data.
     */
    @After
    public void tearDownDB() {
        jdbcTemplate.execute("DELETE FROM CREDIT");
        jdbcTemplate.execute("DELETE FROM CLIENT");
    }

    /**
     * Create a test scenario with a random client.
     */
    private void setupScenario0() {
        jdbcTemplate.execute(
                "INSERT INTO CLIENT (id, typeId, clientId, firstName, lastName, email) "
                        + "VALUES (1, 'Passport', 'ABC123', 'Homer', 'Simpson', "
                        + "'homer@springfield.com')");
    }

    /**
     * Create a test scenario with a random client and a credit.
     */
    private void setupScenario1() {
        setupScenario0();
        jdbcTemplate.execute(
                "INSERT INTO CREDIT (clientId, creditValue, approvalDate, paidDate) "
                    + "VALUES (1, 200000, DATE '2015/01/01', NULL)");
    }

    /**
     * Test credit amount evaluation when there is NO RISK.
     */
    @Test
    public void evaluateCreditAmountTest1() {
        EvaluateQuotaInDTO inDTO = new EvaluateQuotaInDTO();
        inDTO.setCurrentSalary(new BigDecimal(2000000));
        inDTO.setMaxPastCredit(new BigDecimal(6500000));
        inDTO.setTotalCurrentCredit(new BigDecimal(200000));
        inDTO.setRiskTypeEnum(RiskTypeEnum.NONE);
        BigDecimal quota = creditEvalService.evaluateCreditAmount(inDTO);
        assertThat(quota, is(new BigDecimal(6300000)));
    }

    /**
     * Test credit amount evaluation where risk is IN_ARREARS_3_MONTHS
     */
    @Test
    public void evaluateCreditAmountTest2() {
        EvaluateQuotaInDTO inDTO = new EvaluateQuotaInDTO();
        inDTO.setCurrentSalary(new BigDecimal(2000000));
        inDTO.setMaxPastCredit(new BigDecimal(5000000));
        inDTO.setTotalCurrentCredit(new BigDecimal(200000));
        inDTO.setRiskTypeEnum(RiskTypeEnum.IN_ARREARS_3_MONTHS);
        BigDecimal quota = creditEvalService.evaluateCreditAmount(inDTO);
        assertThat(quota, is(new BigDecimal(6800000)));
    }

    /**
     * Test credit amount evaluation where risk is IN_ARREARS_6_MONTHS
     */
    @Test
    public void evaluateCreditAmountTest3() {
        EvaluateQuotaInDTO inDTO = new EvaluateQuotaInDTO();
        inDTO.setCurrentSalary(new BigDecimal(2000000));
        inDTO.setMaxPastCredit(new BigDecimal(5000000));
        inDTO.setTotalCurrentCredit(new BigDecimal(200000));
        inDTO.setRiskTypeEnum(RiskTypeEnum.IN_ARREARS_6_MONTHS);
        BigDecimal quota = creditEvalService.evaluateCreditAmount(inDTO);
        assertThat(quota, is(new BigDecimal((7800000))));
    }

    /**
     * Test credit amount evaluation where risk is PLEDGE
     */
    @Test
    public void evaluateCreditAmountTest4() {
        EvaluateQuotaInDTO inDTO = new EvaluateQuotaInDTO();
        inDTO.setCurrentSalary(new BigDecimal(2000000));
        inDTO.setMaxPastCredit(new BigDecimal(5000000));
        inDTO.setTotalCurrentCredit(new BigDecimal(200000));
        inDTO.setRiskTypeEnum(RiskTypeEnum.PLEDGE);
        BigDecimal quota = creditEvalService.evaluateCreditAmount(inDTO);
        assertThat(quota, is(new BigDecimal((4800000))));
    }

    /**
     * Test that an invalid current salary throws an exception.
     */
    @Test
    public void evaluateCreditAmountTest5() {
        EvaluateQuotaInDTO inDTO = new EvaluateQuotaInDTO();
        inDTO.setCurrentSalary(new BigDecimal(-1));
        inDTO.setMaxPastCredit(new BigDecimal(5000000));
        inDTO.setTotalCurrentCredit(new BigDecimal(200000));
        inDTO.setRiskTypeEnum(RiskTypeEnum.PLEDGE);
        exception.expect(IllegalArgumentException.class);
        creditEvalService.evaluateCreditAmount(inDTO);
    }
}
