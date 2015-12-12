package com.mycompany.api.services;

import com.mycompany.api.dto.ClientDTO;
import com.mycompany.api.dto.IDTypeEnum;
import com.mycompany.api.dto.PeopleDTO;
import com.mycompany.exception.EntityExistsException;

import java.util.Optional;

/**
 * Created by jcortes on 12/7/15.
 */
public interface IPeopleService {

    /**
     * Check if the given person is a client.
     * @param idType Type of id.
     * @param id Id value.
     * @return true if the person is a client or false otherwise.
     */
    Boolean isClient(IDTypeEnum idType, String id);

    /**
     * Create a new client.
     * @param peopleDTO DTO with new client data.
     */
    void createClient(PeopleDTO peopleDTO) throws EntityExistsException;

    Optional<ClientDTO> getClient(IDTypeEnum idType, String id);
}
