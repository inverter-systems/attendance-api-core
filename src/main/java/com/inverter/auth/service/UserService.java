package com.inverter.auth.service;

import java.util.Optional;

import com.inverter.auth.entity.User;
import com.inverter.auth.exception.SecurityException; 

public interface UserService {
	
	User create(User u) throws SecurityException;
	Optional<User> findByEmail(String email);
	Optional<User> findByUsername(String name);
	User activationAcount(String token) throws SecurityException;
	
}
	