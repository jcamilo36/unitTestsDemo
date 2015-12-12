package com.mycompany.impl.services;

import com.mycompany.api.client.ICreditFirmABC;
import com.mycompany.api.dto.ClientDTO;
import com.mycompany.api.dto.CreditRequestDTO;
import com.mycompany.api.dto.IDTypeEnum;
import com.mycompany.api.dto.PeopleDTO;
import com.mycompany.api.dto.RiskTypeEnum;
import com.mycompany.impl.client.CreditFirmABCImpl;
import common.ObjectBuilder;
import common.TestContextConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

/**
 * Test the method evaluate credit using mocking for external services.
 * Created by jcortes on 12/11/15.
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestContextConfiguration.class)
@WebAppConfiguration
@PrepareForTest({CreditEvalServiceImpl.class})
@PowerMockIgnore({"javax.management.*", "javax.net.ssl.*", "org.springframework.jmx.*"})
public class EvaluateCreditRequestMocking {

    /**
     * Object builder.
     */
    @Autowired
    private ObjectBuilder objectBuilder;

    /**
     * CreditEvalService implementation to be tested.
     */
    private CreditEvalServiceImpl creditEvalService;

    /**
     * JDBC client.
     */
    private JdbcTemplate jdbcTemplate;

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
     * Create a test scenario with a client and a credit.
     */
    private void setupScenario1() {
        setupScenario0();
        jdbcTemplate.execute(
                "INSERT INTO CREDIT (clientId, creditValue, approvalDate, paidDate) "
                        + "VALUES (1, 200000, DATE '2015/01/01', NULL)");
    }

    /**
     * Setup common tests configurations.
     */
    @Before
    public void setupTests() {
        MockitoAnnotations.initMocks(this);
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
     * Test evaluate credit request method mocking the public method 'getRiskByClient'
     * from CreditFirmABCImpl.
     */
    @Test
    public void evaluateCreditRequestTest1() {
        setupScenario1();
        ICreditFirmABC creditFirmABC = Mockito.mock(CreditFirmABCImpl.class);
        Mockito.doReturn(RiskTypeEnum.NONE).when(creditFirmABC)
                .getRiskByClient(any(IDTypeEnum.class), Matchers.anyString());
        setInternalState(creditEvalService, "creditFirmABC", creditFirmABC);

        PeopleDTO peopleDTO = new PeopleDTO(IDTypeEnum.PASSPORT, "ABC123", "Homer",
                "Simpson", "homer@springfield.com");
        CreditRequestDTO creditRequestDTO =
                new CreditRequestDTO(peopleDTO, new BigDecimal(2000000));
        creditEvalService.evaluateCreditRequest(creditRequestDTO);

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT creditValue FROM CREDIT WHERE DATE(approvalDate) = DATE(NOW()) ");
        assertFalse(rows.isEmpty());
        Map<String, Object> map = rows.get(0);
        BigDecimal creditValue = (BigDecimal) map.get("creditValue");
        assertThat(creditValue, is(new BigDecimal(5800000)));

        Mockito.verify(creditFirmABC).getRiskByClient(IDTypeEnum.PASSPORT, "ABC123");
    }

    /**
     * Test evaluate credit request method mocking the private method
     * 'performRiskAnalysis' from CreditEvalServiceImpl.
     * @throws Exception if there is an exception with the mocking.
     */
    @Test
    public void evaluateCreditRequestTest2() throws Exception {
        setupScenario1();
        CreditEvalServiceImpl spyBean = PowerMockito.spy(creditEvalService);
        CreditEvalServiceImpl.RiskAnalysisDTO riskAnalysisDTO =
                new CreditEvalServiceImpl.RiskAnalysisDTO(
                        RiskTypeEnum.IN_ARREARS_3_MONTHS,
                        new BigDecimal(5000000), new BigDecimal(200000));
        PowerMockito.doReturn(riskAnalysisDTO)
                .when(spyBean, "performRiskAnalysis", any(ClientDTO.class));

        PeopleDTO peopleDTO = new PeopleDTO(IDTypeEnum.PASSPORT, "ABC123", "Homer",
                "Simpson", "homer@springfield.com");
        CreditRequestDTO creditRequestDTO =
                new CreditRequestDTO(peopleDTO, new BigDecimal(2000000));
        spyBean.evaluateCreditRequest(creditRequestDTO);

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT creditValue FROM CREDIT WHERE DATE(approvalDate) = DATE(NOW()) ");
        assertFalse(rows.isEmpty());
        Map<String, Object> map = rows.get(0);
        BigDecimal creditValue = (BigDecimal) map.get("creditValue");
        assertThat(creditValue, is(new BigDecimal(6800000)));

        PowerMockito.verifyPrivate(spyBean).invoke("performRiskAnalysis", any(ClientDTO.class));
    }
}
