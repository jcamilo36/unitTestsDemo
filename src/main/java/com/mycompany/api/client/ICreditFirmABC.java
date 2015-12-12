package com.mycompany.api.client;

import com.mycompany.api.dto.IDTypeEnum;
import com.mycompany.api.dto.RiskTypeEnum;

/**
 * Interface to handle communication with Credit Analysis Firm ABC.
 * Created by jcortes on 12/7/15.
 */
public interface ICreditFirmABC {

    /**
     * Return a risk analysis result given a person.
     * @param idType Type of id, e.g passport or national id.
     * @param clientId Client identifier.
     * @return a risk status.
     */
    RiskTypeEnum getRiskByClient(IDTypeEnum idType, String clientId);
}
