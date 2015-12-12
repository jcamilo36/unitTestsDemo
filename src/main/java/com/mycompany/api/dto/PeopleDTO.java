package com.mycompany.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO with client data.
 * Created by jcortes on 12/10/15.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PeopleDTO implements Serializable {

    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = 3301662184816389433L;

    /**
     * Type of identifier.
     */
    private IDTypeEnum idType;

    /**
     * Client identifier (in a country).
     */
    private String id;

    /**
     * Client first name.
     */
    private String firstName;

    /**
     * Client last name.
     */
    private String lastName;

    /**
     * Client email.
     */
    private String email;
}
