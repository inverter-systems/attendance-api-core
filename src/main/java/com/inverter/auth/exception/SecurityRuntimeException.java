package com.inverter.auth.exception;

public class SecurityRuntimeException extends RuntimeException {
	private static final long serialVersionUID = -2107184658843282904L;

    public SecurityRuntimeException(String message) {
       super(message);
    }
}