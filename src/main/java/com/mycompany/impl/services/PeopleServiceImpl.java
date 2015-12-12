package com.mycompany.impl.services;

import com.mycompany.api.dto.ClientDTO;
import com.mycompany.api.dto.IDTypeEnum;
import com.mycompany.api.dto.PeopleDTO;
import com.mycompany.api.services.IPeopleService;

import com.mycompany.exception.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation for people service.
 * Created by jcortes on 12/9/15.
 */
public class PeopleServiceImpl implements IPeopleService {

    /**
     * SQL query to check if a client exists or not.
     */
    private static final String IS_CLIENT =
            "SELECT clientId FROM CLIENT WHERE typeId = ? AND clientId = ?";

    /**
     * SQL query to create a client.
     */
    private static final String CREATE_CLIENT =
            "INSERT INTO CLIENT (typeId, clientId, firstName, lastName, email) "
                    + "VALUES (?, ?, ?, ?, ?)";

    /**
     * SQL query to select a client.
     */
    private static final String SELECT_CLIENT =
            "SELECT id, typeid, clientId, firstName, lastName, email FROM CLIENT "
                + "WHERE typeId = ? AND clientId = ?";

    /**
     * JDBC client.
     */
    private JdbcTemplate jdbcTemplate;

    /**
     * Sets data source.
     *
     * @param dataSource the data source
     */
    @Autowired
    public void setDataSource(final DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Check if a client exists in the database or not.
     * @param idType Type of id.
     * @param id Client National id.
     * @return true if the client exists or false otherwise.
     */
    @Override
    public Boolean isClient(IDTypeEnum idType, String id) {
        boolean exists = false;
        List<Map<String, Object>> rows =
            jdbcTemplate.queryForList(IS_CLIENT, idType.toValue(), id);
        if (rows.size() == 1) {
            exists = true;
        }
        return exists;
    }

    /**
     * Create a client given its data.
     * @param peopleDTO DTO with new client data.
     * @throws EntityExistsException if the client already exists.
     */
    @Override
    public void createClient(PeopleDTO peopleDTO) throws EntityExistsException {
        if (!isClient(peopleDTO.getIdType(), peopleDTO.getId())) {
            Object[] params = new Object[]{peopleDTO.getIdType(), peopleDTO.getId(),
                    peopleDTO.getFirstName(), peopleDTO.getLastName(), peopleDTO.getEmail()};

            int[] types = new int[]{Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
                    Types.VARCHAR, Types.VARCHAR};
            jdbcTemplate.update(CREATE_CLIENT, params, types);
        } else {
            throw new EntityExistsException(
                    "Client [" + peopleDTO.getIdType().toValue() + ", "
                            + peopleDTO.getId() + "] " + "already exists.", null);
        }
    }

    /**
     * Return a client given its id.
     * @param idType Type of id.
     * @param id National identifier, e.g. passport.
     * @return an optional DTO with all data.
     */
    @Override
    public Optional<ClientDTO> getClient(IDTypeEnum idType, String id) {
        List<Map<String, Object>> rows =
                jdbcTemplate.queryForList(SELECT_CLIENT, idType.toValue(), id);
        if (rows.size() == 1) {
            Map<String, Object> map = rows.get(0);
            ClientDTO clientDTO = new ClientDTO(
                    (Long) map.get("id"),
                    (String) map.get("typeId"),
                    (String) map.get("clientId"),(String) map.get("firstName"),
                    (String) map.get("lastName"), (String) map.get("email"));
            return Optional.ofNullable(clientDTO);
        }
        return Optional.empty();
    }
}
