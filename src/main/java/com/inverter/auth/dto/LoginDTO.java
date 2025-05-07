package com.inverter.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.inverter.auth.entity.User;

import jakarta.validation.constraints.NotEmpty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LoginDTO(
		
	@NotEmpty(message = "{user.auth.user.error.invalid.username}")
	String username,
	@NotEmpty(message = "{user.auth.user.error.invalid.password}") 
	@JsonProperty(access = Access.WRITE_ONLY)
	String password,
	@JsonProperty(access = Access.READ_ONLY)
	User user) {
}