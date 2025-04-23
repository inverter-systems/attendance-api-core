package com.inverter.auth.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.inverter.auth.entity.User;
import com.inverter.auth.service.TokenService;

@Service
public class TokenServiceImpl implements TokenService {
	
	@Value("${jwt.expiration.min}")
	private Integer expiration;
	
	@Value("${jwt.secret}")
	private String secret;
	
	static final String ISSUE = "inverterApi";
	static final String ISSUE_ACTIVATION = "inverterApiActivationToken";
	
	static final Integer ONE_DAY_MIN = 60*24;

	@Override
    public String gerarToken(User usuario) {
        return JWT.create()
                .withIssuer(ISSUE)
                .withSubject(usuario.getUsername())
                .withClaim("id", usuario.getId())
                .withExpiresAt(LocalDateTime.now()
                        .plusMinutes(expiration)
                        .toInstant(ZoneOffset.of("-03:00"))
                ).sign(Algorithm.HMAC256(secret));
    }
	
	@Override
    public String gerarActivationToken(User usuario) {
        return JWT.create()
                .withIssuer(ISSUE_ACTIVATION)
                .withSubject(usuario.getUsername())
                .withClaim("id", usuario.getId())
                .withExpiresAt(LocalDateTime.now()
                        .plusMinutes(ONE_DAY_MIN)
                        .toInstant(ZoneOffset.of("-03:00"))
                ).sign(Algorithm.HMAC256(secret));
    }

	@Override
    public String getSubject(String token) {
        return JWT.require(Algorithm.HMAC256(secret))
                .withIssuer(ISSUE)
                .build().verify(token).getSubject();

    }
	
	public Instant getExpires(String token) {
		return JWT.require(Algorithm.HMAC256(secret))
				.withIssuer(ISSUE)
				.build().verify(token).getExpiresAtAsInstant();
	}
	
	public Instant getExpiresActivationAccount(String token) {
		return JWT.require(Algorithm.HMAC256(secret))
				.withIssuer(ISSUE_ACTIVATION)
				.build().verify(token).getExpiresAtAsInstant();
	}
}