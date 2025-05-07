package com.inverter.auth.service;

import com.inverter.auth.exception.SecurityException;

public interface EmailService {

	void sendActivationEmail(String email, String token) throws SecurityException;
	void sendPasswordReset(String to, String token) throws SecurityException; 

}
