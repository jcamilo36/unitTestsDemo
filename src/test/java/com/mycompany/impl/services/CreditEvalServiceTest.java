package com.mycompany.impl.services;

import com.mycompany.api.dto.EvaluateQuotaInDTO;
import com.mycompany.api.dto.RiskTypeEnum;
import com.mycompany.exception.AccountBlockedException;
import com.mycompany.exception.AccountDoesNotExistsException;
import com.mycompany.exception.NotEnoughMoneyException;

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
import java.sql.Types;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

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
    	jdbcTemplate.execute("DELETE FROM ACCOUNT");
        jdbcTemplate.execute("DELETE FROM CREDIT");
        jdbcTemplate.execute("DELETE FROM CLIENT");
        
        jdbcTemplate.execute("ALTER TABLE ACCOUNT AUTO_INCREMENT=1");
        jdbcTemplate.execute("ALTER TABLE CREDIT AUTO_INCREMENT=1");
        jdbcTemplate.execute("ALTER TABLE CLIENT AUTO_INCREMENT=1");
    }

    /**
     * Create a test scenario with a random client.
     */
    private void setupScenario0() {
        jdbcTemplate.execute(
                "INSERT INTO CLIENT (id, typeId, clientId, firstName, lastName, email) "
                        + "VALUES (1, 'passport', 'ABC123', 'Homer', 'Simpson', "
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
     * Create a test scenario with a credit and 2 accounts.
     */
    private void setupScenario2() {
    	setupScenario1();
    	jdbcTemplate.execute(
                "INSERT INTO CLIENT (id, typeId, clientId, firstName, lastName, email) "
                        + "VALUES (99, 'national id', '987654321', 'Montgomery', 'Burns', "
                        + "'bank@springfield.com')");
    	jdbcTemplate.execute(
        		"INSERT INTO ACCOUNT (id, number, clientId, total)"
        		+ "VALUES (1,'123',1,1000000)");
    	jdbcTemplate.execute(
        		"INSERT INTO ACCOUNT (id, number, clientId, total)"
        		+ "VALUES (99,'QWE',1,5000000000)");
    }
    
    /**
     * Create a test scenario with a credit and 2 accounts. The client
     * account has no money.
     */
    private void setupScenario3() {
    	setupScenario1();
    	jdbcTemplate.execute(
                "INSERT INTO CLIENT (id, typeId, clientId, firstName, lastName, email) "
                        + "VALUES (99, 'national id', '987654321', 'Montgomery', 'Burns', "
                        + "'bank@springfield.com')");
    	jdbcTemplate.execute(
        		"INSERT INTO ACCOUNT (id, number, clientId, total)"
        		+ "VALUES (1,'123',1,0)");
    	jdbcTemplate.execute(
        		"INSERT INTO ACCOUNT (id, number, clientId, total)"
        		+ "VALUES (99,'QWE',1,5000000000)");
    }
    
    /**
     * Create a test scenario with a credit and 2 accounts. The client account
     * is blocked.
     */
    private void setupScenario4() {
    	setupScenario1();
    	jdbcTemplate.execute(
                "INSERT INTO CLIENT (id, typeId, clientId, firstName, lastName, email) "
                        + "VALUES (99, 'national id', '987654321', 'Montgomery', 'Burns', "
                        + "'bank@springfield.com')");
    	jdbcTemplate.execute(
        		"INSERT INTO ACCOUNT (id, number, clientId, total, blocked)"
        		+ "VALUES (1,'123',1,1000000, true)");
    	jdbcTemplate.execute(
        		"INSERT INTO ACCOUNT (id, number, clientId, total)"
        		+ "VALUES (99,'QWE',1,5000000000)");
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
    
    /**
     * Test credit amount evaluation where risk is SEIZED
     */
    @Test
    public void evaluateCreditAmountTest6() {
        EvaluateQuotaInDTO inDTO = new EvaluateQuotaInDTO();
        inDTO.setCurrentSalary(new BigDecimal(2000000));
        inDTO.setMaxPastCredit(new BigDecimal(6500000));
        inDTO.setTotalCurrentCredit(new BigDecimal(200000));
        inDTO.setRiskTypeEnum(RiskTypeEnum.SEIZED);
        BigDecimal quota = creditEvalService.evaluateCreditAmount(inDTO);
        assertThat(quota, is(BigDecimal.ZERO));
    }
    
//    /**
//     * Test that a credit is marked as paid and the amount
//     * is discounted from the account.
//     */
//    @Test
//    public void paidCreditTest1() {
//    	setupScenario2();
//    	try {
//	    	creditEvalService.payCredit("123", "QWE", 1L);
//	    	
//	    	Object[] params = new Object[]{"123"};
//	        int[] types = new int[]{Types.NUMERIC};
//	    	BigDecimal total = jdbcTemplate.queryForObject(
//	    			"SELECT total FROM ACCOUNT WHERE number = ?",
//	    			params, types, BigDecimal.class);
//	    	assertThat(total, is(new BigDecimal("800000")));
//	    	
//	    	params = new Object[]{"QWE"};
//	        types = new int[]{Types.VARCHAR};
//	    	total = jdbcTemplate.queryForObject(
//	    			"SELECT total FROM ACCOUNT WHERE number = ?",
//	    			params, types, BigDecimal.class);
//	    	assertThat(total, is(new BigDecimal("5000200000")));
//    	} catch (Exception e) {
//    		e.printStackTrace();
//    		fail(e.getMessage());
//    	}
//    }
//    
//    /**
//     * Test that an exception is thrown if the account doesn't
//     * exists.
//     */
//    @Test
//    public void paidCreditTest2() throws Exception {
//    	setupScenario2();
//    	exception.expect(AccountDoesNotExistsException.class);
//    	creditEvalService.payCredit("111", "QWE", 1L);
//    }
//    
//    /**
//     * Test that an exception is thrown if there is no
//     * money enough to pay.
//     */
//    @Test
//    public void paidCreditTest3() throws Exception {
//    	setupScenario3();
//    	exception.expect(NotEnoughMoneyException.class);
//    	creditEvalService.payCredit("123", "QWE", 1L);
//    }
//    
//    /**
//     * Test that an exception is the account is blocked.
//     */
//    @Test
//    public void paidCreditTest4() throws Exception {
//    	setupScenario4();
//    	exception.expect(AccountBlockedException.class);
//    	creditEvalService.payCredit("123", "QWE", 1L);
//    }
}
