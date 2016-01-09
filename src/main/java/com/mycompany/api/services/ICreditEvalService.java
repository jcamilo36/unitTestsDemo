package com.mycompany.api.services;

import com.mycompany.api.dto.EvaluateQuotaInDTO;
import com.mycompany.exception.AccountBlockedException;
import com.mycompany.exception.AccountDoesNotExistsException;
import com.mycompany.exception.NotEnoughMoneyException;

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
    
//    /**
//     * Pay a credit by making a debit to account origin and a credit
//     * to account target.
//     * @param accountOrigin Account from the client.
//     * @param accountTarget Account from the bank.
//     * @param creditId Credit identifier.
//     * @throws AccountBlockedException if the account is blocked
//     * @throws AccountDoesNotExistsException if the account doesn't exist.
//     * @throws NotEnoughMoneyException if there is no money enough to pay the credit.
//     */
//    void payCredit(String accountOrigin, String accountTarget, Long creditId)
//    	throws AccountBlockedException, AccountDoesNotExistsException, NotEnoughMoneyException;
}
