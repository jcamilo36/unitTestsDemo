package com.mycompany.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO with data to request a credit card.
 * Created by jcortes on 12/10/15.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditRequestDTO implements Serializable {

    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = -4214877041355143385L;

    /**
     * DTO with client data.
     */
    private PeopleDTO peopleDTO;

    /**
     * Client current salary.
     */
    private BigDecimal currentSalary;
}
