package com.inverter.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.inverter.auth.CustomAuthenticationEntryPoint;
import com.inverter.auth.FilterToken;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
	
	 static final String [] ENDPOINTS_WITH_AUTHENTICATION_NOT_REQUIRED = {"/api/auth", "/api/auth/user", "/api/auth/user/activate"};

     static final String [] ENDPOINTS_WITH_AUTHENTICATION_REQUIRED = {"/users/test"};

     static final String [] ENDPOINTS_CUSTOMER = {"/users/test/customer"};

     static final String [] ENDPOINTS_ADMIN = {"/users/test/administrator"};

	@Bean
	SecurityFilterChain securityFilterChain1(HttpSecurity http, FilterToken filter) throws Exception {
		return http
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers(ENDPOINTS_WITH_AUTHENTICATION_NOT_REQUIRED).permitAll()
						.requestMatchers(ENDPOINTS_WITH_AUTHENTICATION_REQUIRED).authenticated()
		                .requestMatchers(ENDPOINTS_ADMIN).hasRole("ADMINISTRATOR") 
		                .requestMatchers(ENDPOINTS_CUSTOMER).hasRole("CUSTOMER")
		                .anyRequest().denyAll())
				.csrf(csrf -> csrf.disable())
				.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
				.exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint()))
				.build();
	}
	
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	AuthenticationEntryPoint authenticationEntryPoint() {
		return new CustomAuthenticationEntryPoint();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();    
	}
}
