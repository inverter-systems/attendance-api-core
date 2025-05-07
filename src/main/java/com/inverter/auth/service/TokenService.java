package com.inverter.auth.service;

import java.time.Instant;

import com.inverter.auth.entity.User;
import com.inverter.auth.enums.IssueEnum;

public interface TokenService {
			
    public String buildUserToken(User usuario);
	public String buildActivationToken(User usuario);
	public String buildPasswordResetToken(User usuario);
	public String getSubject(String token, IssueEnum issue);
    public Instant getExpires(String token, IssueEnum issue);
    
}