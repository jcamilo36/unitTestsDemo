package common;

import com.mycompany.impl.services.CreditEvalServiceImpl;
import com.mycompany.impl.services.PeopleServiceImpl;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

/**
 * Build object implementation for unit tests.
 * Created by jcortes on 12/9/15.
 */
@Component
public class ObjectBuilder {

    @Value("${database.name}")
    private String databaseName;

    /**
     * Build default implementation for ICreditEvalService.
     * @return CreditEvalServiceImpl object.
     */
    public CreditEvalServiceImpl buildCreditEvalServiceImpl() {
        CreditEvalServiceImpl creditEvalService = new CreditEvalServiceImpl();
        setInternalState(creditEvalService, "peopleService", buildPeopleServiceImpl());
        DataSource dataSource = getTestDataSource();
        creditEvalService.setDataSource(dataSource);
        return creditEvalService;
    }

    /**
     * Build default implemenation for IPeopleService.
     * @return PeopleServiceImpl object.
     */
    public PeopleServiceImpl buildPeopleServiceImpl() {
        PeopleServiceImpl peopleService = new PeopleServiceImpl();
        DataSource dataSource = getTestDataSource();
        peopleService.setDataSource(dataSource);
        return peopleService;
    }

    /**
     * @return Data source to a local mysql database.
     */
    public DataSource getTestDataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/" + databaseName);
        dataSource.setUsername("test");
        dataSource.setPassword("test");
        return dataSource;
    }
}
