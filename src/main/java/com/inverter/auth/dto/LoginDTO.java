package com.inverter.auth.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.inverter.auth.entity.User;

import jakarta.validation.constraints.NotEmpty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LoginDTO(
		
	@NotEmpty(message = "Invalid Login! Username cannot be empty or null!")
	String username,
	@NotEmpty(message = "Invalid Login! Password cannot be empty or null!")
	@JsonProperty(access = Access.WRITE_ONLY)
	String password,
	String token,
	Instant expiresAt,
	@JsonProperty(access = Access.READ_ONLY)
	User user) {
}