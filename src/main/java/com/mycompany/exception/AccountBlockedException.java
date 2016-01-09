package com.mycompany.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AccountBlockedException extends Exception {

	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = -2701823876610983652L;

	/**
     * Exception message.
     */
    private String message;

    /**
     * Cause.
     */
    private Throwable throwable;
}
