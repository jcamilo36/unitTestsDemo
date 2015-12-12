package com.mycompany.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO to evaluate the credit card quota.
 * Created by jcortes on 12/9/15.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluateQuotaInDTO implements Serializable {

    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = -1122744540626352454L;

    /**
     * Maximum credit given to the credit in the past.
     */
    private BigDecimal maxPastCredit;

    /**
     * Total amount of loans given to the client currently.
     */
    private BigDecimal totalCurrentCredit;

    /**
     * Client current salary.
     */
    private BigDecimal currentSalary;

    /**
     * Client financial risk.
     */
    private RiskTypeEnum riskTypeEnum;
}
