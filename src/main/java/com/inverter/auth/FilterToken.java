package com.inverter.auth;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.inverter.auth.repository.UserRepository;
import com.inverter.auth.service.TokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterToken extends OncePerRequestFilter {

	private TokenService tokenService;
	private UserRepository userRepo;

	public FilterToken(TokenService tokenService, UserRepository userRepo) {
		super();
		this.tokenService = tokenService;
		this.userRepo = userRepo;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)	throws ServletException, IOException {
		var authorizationHeader = req.getHeader("Authorization");
		try {
			if (authorizationHeader != null) {
				var token = authorizationHeader.replace("Bearer ", "");
				var subject = tokenService.getSubject(token);
				var user = userRepo.findByUsername(subject);
				
				if (user.isPresent()) { 
					var authentication = new UsernamePasswordAuthenticationToken(user, null, user.get().getAuthorities());
					SecurityContextHolder.getContext().setAuthentication(authentication);	
					req.setAttribute("authentication","authenticated");
				}
			} else {
				req.setAttribute("error", "Access denied! You must be authenticated in the system to access the requested URL");
			}
		} catch (TokenExpiredException e) {
			req.setAttribute("expired", e.getMessage());
		} catch (Exception e) {
			req.setAttribute("error", e.getMessage());
		} 
		chain.doFilter(req, res);
	}
}