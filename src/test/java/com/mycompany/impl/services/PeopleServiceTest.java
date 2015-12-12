package com.mycompany.impl.services;

import com.mycompany.api.dto.ClientDTO;
import com.mycompany.api.dto.IDTypeEnum;
import com.mycompany.api.dto.PeopleDTO;
import com.mycompany.exception.EntityExistsException;
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

import java.util.Optional;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

/**
 * Test people service implementation.
 * Created by jcortes on 12/10/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestContextConfiguration.class)
@WebAppConfiguration
public class PeopleServiceTest {

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
     * People service implementation.
     */
    public PeopleServiceImpl peopleService;

    /**
     * JDBC client.
     */
    private JdbcTemplate jdbcTemplate;

    /**
     * Setup common tests configurations.
     */
    @Before
    public void setupTests() {
        peopleService = objectBuilder.buildPeopleServiceImpl();
        jdbcTemplate = new JdbcTemplate(objectBuilder.getTestDataSource());
    }

    /**
     * Clean data.
     */
    @After
    public void tearDownDB() {
        jdbcTemplate.execute("DELETE FROM CREDIT");
        jdbcTemplate.execute("DELETE FROM CLIENT");
    }

    /**
     * Setup a test scenario with a random client.
     */
    private void setupScenario1() {
        jdbcTemplate.execute(
                "INSERT INTO CLIENT (id, typeId, clientId, firstName, lastName, email) "
                        + "VALUES (1, 'Passport', 'ABC123', 'Homer', 'Simpson', "
                        + "'homer@springfield.com')");
    }

    /**
     * Test 'isClient' method when the client exists.
     */
    @Test
    public void isClientTest1() {
        setupScenario1();
        Boolean exists = peopleService.isClient(IDTypeEnum.PASSPORT, "ABC123");
        assertTrue(exists);
    }

    /**
     * Test 'isClient' method when the client doesn't exist.
     */
    @Test
    public void isClientTest2() {
        Boolean exists = peopleService.isClient(IDTypeEnum.PASSPORT, "ABC123");
        assertFalse(exists);
    }

    /**
     * Test a proper client creation.
     */
    @Test
    public void createClientTest1() {
        PeopleDTO peopleDTO = new PeopleDTO(IDTypeEnum.NATIONAL_ID, "7890", "Luke",
                "Skywalker", "luke@tatooine.com");
        peopleService.createClient(peopleDTO);
    }

    /**
     * Test that an exception is thrown when the client already exists.
     */
    @Test
    public void createClientTest2() {
        setupScenario1();
        PeopleDTO peopleDTO = new PeopleDTO(IDTypeEnum.PASSPORT, "ABC123", "Luke",
                "Skywalker", "luke@tatooine.com");
        exception.expect(EntityExistsException.class);
        peopleService.createClient(peopleDTO);
    }

    /**
     * Test 'getClient' method when the client exists.
     */
    @Test
     public void getClientTest1() {
        setupScenario1();
        Optional<ClientDTO> opt = peopleService.getClient(IDTypeEnum.PASSPORT, "ABC123");
        assertTrue(opt.isPresent());
        ClientDTO clientDTO = opt.get();
        assertThat(clientDTO, is(equalTo(new ClientDTO(1L, "Passport", "ABC123", "Homer",
                "Simpson", "homer@springfield.com"))));
    }

    /**
     * Test 'getClient' method when the client doesn't exist.
     */
    @Test
    public void getClientTest2() {
        Optional<ClientDTO> opt = peopleService.getClient(IDTypeEnum.PASSPORT, "000");
        assertFalse(opt.isPresent());
    }
}
