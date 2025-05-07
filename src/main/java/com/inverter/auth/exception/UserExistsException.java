package com.inverter.auth.exception;

public class UserExistsException extends RuntimeException {

	private static final long serialVersionUID = 502221851903572869L;

	public UserExistsException(String message) {
       super(message);
    }
}