package com.inverter.auth.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Bcrypt {
	
	private Bcrypt() {}
	
	public static String getHash(String password) {
		if (password == null) {
			return null;
		}
		
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder.encode(password);
	}

}