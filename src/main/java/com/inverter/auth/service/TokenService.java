package com.inverter.auth.service;

import java.time.Instant;

import com.inverter.auth.entity.User;

public interface TokenService {
			
    public String gerarToken(User usuario);
    public String getSubject(String token);
    public Instant getExpires(String token);
    public Instant getExpiresActivationAccount(String token);
	public String gerarActivationToken(User usuario);
    
}