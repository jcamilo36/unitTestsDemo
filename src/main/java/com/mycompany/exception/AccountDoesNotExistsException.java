package com.mycompany.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AccountDoesNotExistsException extends Exception {
	
	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = -1839657339515604061L;

	/**
     * Exception message.
     */
    private String message;

    /**
     * Cause.
     */
    private Throwable throwable;
}
