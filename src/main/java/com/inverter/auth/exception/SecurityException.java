package com.inverter.auth.exception;

public class SecurityException extends RuntimeException {
	private static final long serialVersionUID = -2107184658843282904L;

    public SecurityException(String message) {
       super(message);
    }
}