package com.mycompany.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by jcortes on 12/10/15.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class EntityExistsException extends RuntimeException {

    /**
     * Exception message.
     */
    private String message;

    /**
     * Cause.
     */
    private Throwable throwable;
}
