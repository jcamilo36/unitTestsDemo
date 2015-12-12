package com.mycompany.api.services;

import com.mycompany.api.dto.EvaluateQuotaInDTO;

import java.math.BigDecimal;

/**
 * Interface that defines all operations for a credit evaluation service.
 * Created by jcortes on 12/9/15.
 */
public interface ICreditEvalService {

    /**
     * Evaluate a credit card quota.
     * @param inDTO DTO with data to assess a possible credit card quota.
     * @return the quota.
     */
    BigDecimal evaluateCreditAmount(EvaluateQuotaInDTO inDTO);
}
