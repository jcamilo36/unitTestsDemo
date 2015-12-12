package com.mycompany.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO that stores a client information.
 * Created by jcortes on 12/10/15.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDTO implements Serializable {

    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = 62768009081550065L;

    /**
     * Database identifier.
     */
    private Long id;

    /**
     * Type of id, e.g passport or national id.
     */
    private String typeId;

    /**
     * Client identifier.
     */
    private String clientId;

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
