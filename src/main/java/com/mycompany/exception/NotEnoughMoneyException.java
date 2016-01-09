package com.mycompany.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class NotEnoughMoneyException extends Exception {

	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = 1375769305376668457L;

	/**
     * Exception message.
     */
    private String message;
    

    /**
     * Cause.
     */
    private Throwable throwable;

}
