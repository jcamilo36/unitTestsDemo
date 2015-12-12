package com.mycompany.api.dto;

/**
 * Enum with types of financial risk.
 * Created by jcortes on 12/9/15.
 */
public enum RiskTypeEnum {

    /**
     * No risk
     */
    NONE,

    /**
     * There was a debt in arrears that was paid after 3 months.
     */
    IN_ARREARS_3_MONTHS,

    /**
     * There was a debt in arrears that was paid after 6 months.
     */
    IN_ARREARS_6_MONTHS,

    /**
     * There is an amount of money in pledge.
     */
    PLEDGE,

    /**
     * Loan was defaulted and the debtor goods were seized.
     */
    SEIZED;
}
