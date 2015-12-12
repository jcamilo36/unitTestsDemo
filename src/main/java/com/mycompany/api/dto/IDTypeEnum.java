package com.mycompany.api.dto;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum with types of (people) identifiers in a country.
 * Created by jcortes on 12/10/15.
 */
@AllArgsConstructor
public enum IDTypeEnum {

    /**
     * National identifier.
     */
    NATIONAL_ID,

    /**
     * Passport number.
     */
    PASSPORT;

    /**
     * Name map for json translation.
     */
    private static Map<String, IDTypeEnum> namesMap = new HashMap<String, IDTypeEnum>();

    /**
     * Static constructor for linking enum with strings
     */
    static {
        namesMap.put("national id", NATIONAL_ID);
        namesMap.put("passport", PASSPORT);
    }

    /**
     * Return an enum given the matching string.
     * @param value string that represent a id type.
     * @return the matching enum
     */
    public static IDTypeEnum forValue(final String value) {
        return namesMap.get(StringUtils.lowerCase(value));
    }

    /**
     * @return this enum text
     */
    public String toValue() {
        for (Map.Entry<String, IDTypeEnum> entry : namesMap.entrySet()) {
            if (entry.getValue() == this) {
                return entry.getKey();
            }
        }
        return null;
    }
}
