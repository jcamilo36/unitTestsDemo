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
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.web.WebAppConfiguration;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

/**
 * Test evaluate credit amount method from CreditEvalService using junit parameterized.
 * Created by jcortes on 12/10/15.
 */
@RunWith(Parameterized.class)
@ContextConfiguration(classes = TestContextConfiguration.class)
@WebAppConfiguration
public class EvaluateCreditAmountParameterizedTest {

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
     * Test context manager from spring.
     */
    private TestContextManager testContextManager;

    /**
     * JDBC client.
     */
    private JdbcTemplate jdbcTemplate;

    /**
     * Setup common tests configurations.
     */
    @Before
    public void setupTests() throws Exception {
        //Manually setup spring context
        this.testContextManager = new TestContextManager(getClass());
        this.testContextManager.prepareTestInstance(this);

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
        
        jdbcTemplate.execute("ALTER TABLE CREDIT AUTO_INCREMENT=1");
        jdbcTemplate.execute("ALTER TABLE CLIENT AUTO_INCREMENT=1");
    }

    /**
     * Current salary.
     */
    private BigDecimal currentSalary;

    /**
     * Maximum past credit given to the client.
     */
    private BigDecimal maxPastCredit;

    /**
     * Total loans given to the client currently.
     */
    private BigDecimal totalCurrentCredit;

    /**
     * Credit amount.
     */
    private BigDecimal creditAmount;

    /**
     * Risk type.
     */
    private RiskTypeEnum riskTypeEnum;

    /**
     * Flag for exceptions tests.
     */
    private boolean isException;

    /**
     * Default constructor with all fields.
     * @param aCurrentSalary Current salary.
     * @param aMaxPastCredit Max past credit.
     * @param aTotalCurrentCredit Total current loans.
     * @param aCreditAmount Credit amount.
     * @param riskType Risk type.
     * @param flagException Flag exception.
     */
    public EvaluateCreditAmountParameterizedTest(BigDecimal aCurrentSalary,
                                                 BigDecimal aMaxPastCredit,
                                                 BigDecimal aTotalCurrentCredit,
                                                 BigDecimal aCreditAmount,
                                                 RiskTypeEnum riskType,
                                                 boolean flagException) {
        currentSalary = aCurrentSalary;
        maxPastCredit = aMaxPastCredit;
        totalCurrentCredit = aTotalCurrentCredit;
        creditAmount = aCreditAmount;
        riskTypeEnum = riskType;
        isException = flagException;
    }

    /**
     * Test data generator.
     * @return Collection with test data.
     */
    @Parameterized.Parameters
    public static Collection<Object[]> generateData() {
        return Arrays.asList(new Object[][] {
            {new BigDecimal(2000000), new BigDecimal(6500000),
                    new BigDecimal(200000), new BigDecimal(6300000),
                    RiskTypeEnum.NONE, false},
            {new BigDecimal(2000000), new BigDecimal(5000000),
                    new BigDecimal(200000), new BigDecimal(6800000),
                    RiskTypeEnum.IN_ARREARS_3_MONTHS, false},
            {new BigDecimal(2000000), new BigDecimal(5000000),
                    new BigDecimal(200000), new BigDecimal(7800000),
                    RiskTypeEnum.IN_ARREARS_6_MONTHS, false},
            {new BigDecimal(2000000), new BigDecimal(5000000),
                    new BigDecimal(200000), new BigDecimal(4800000),
                    RiskTypeEnum.PLEDGE, false},
            {new BigDecimal(2000000), new BigDecimal(6500000),
                        new BigDecimal(200000), BigDecimal.ZERO,
                        RiskTypeEnum.SEIZED, false},
            {new BigDecimal(-1), new BigDecimal(5000000),
                    new BigDecimal(200000), null, RiskTypeEnum.NONE, true},
            {new BigDecimal(2000000), new BigDecimal(-1),
                    new BigDecimal(200000), null, RiskTypeEnum.NONE, true},
            {new BigDecimal(2000000), new BigDecimal(6500000),
                    new BigDecimal(-1), null, RiskTypeEnum.NONE, true}
        });
    }

    /**
     * Test evaluate credit amount with different inputs.
     */
    @Test
    public void evaluateCreditAmountTest() {
        EvaluateQuotaInDTO inDTO = new EvaluateQuotaInDTO(maxPastCredit,
                totalCurrentCredit, currentSalary, riskTypeEnum);
        if (!isException) {
            BigDecimal response = creditEvalService.evaluateCreditAmount(inDTO);
            assertEquals(creditAmount, response);
        } else {
            exception.expect(IllegalArgumentException.class);
            creditEvalService.evaluateCreditAmount(inDTO);
        }
    }
}
