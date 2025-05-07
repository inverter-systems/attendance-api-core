package com.inverter.auth;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.inverter.auth.enums.IssueEnum;
import com.inverter.auth.repository.UserRepository;
import com.inverter.auth.service.MessageService;
import com.inverter.auth.service.TokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterToken extends OncePerRequestFilter {

	private TokenService tokenService;
	private UserRepository userRepo;
	private MessageService msg;

	public FilterToken(TokenService tokenService, UserRepository userRepo, MessageService msg) {
		this.tokenService = tokenService;
		this.userRepo = userRepo;
		this.msg = msg;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
	        throws ServletException, IOException {

	    final String ERROR_LABEL_ATTRIBUTE = "error";
	    final String TOKEN_EXPIRED_ERROR_MSG = msg.get("user.auth.token.error.expired");
	    final String TOKEN_DECODE_ERROR_MSG = msg.get("user.auth.token.error.decode");
	    final String TOKEN_SIGNATUE_ERROR_MSG = msg.get("user.auth.token.error.signature");

	    try {
	        String token = extractTokenFromCookies(req.getCookies());

	        if (!token.isEmpty()) {
	            authenticateRequest(token, req);
	        }

	    } catch (TokenExpiredException e) {
	        handleTokenException(req, ERROR_LABEL_ATTRIBUTE, String.format(TOKEN_EXPIRED_ERROR_MSG, e.getExpiredOn()), e);
	    } catch (JWTDecodeException e) {
	        handleTokenException(req, ERROR_LABEL_ATTRIBUTE, TOKEN_DECODE_ERROR_MSG, e);
	    } catch (SignatureVerificationException e) {
	        handleTokenException(req, ERROR_LABEL_ATTRIBUTE, TOKEN_SIGNATUE_ERROR_MSG, e);
	    } catch (Exception e) {
	        handleTokenException(req, ERROR_LABEL_ATTRIBUTE, e.getMessage(), e);
	    }

	    chain.doFilter(req, res);
	}

	private String extractTokenFromCookies(Cookie[] cookies) {
	    if (cookies == null) return "";
	    return Arrays.stream(cookies)
	            .filter(c -> "access_token".equals(c.getName()))
	            .map(Cookie::getValue)
	            .findFirst()
	            .orElse("");
	}

	private void authenticateRequest(String token, HttpServletRequest req) {
	    String subject = tokenService.getSubject(token, IssueEnum.ISSUE);
	    var user = userRepo.findByUsername(subject);

	    user.ifPresent(u -> {
	        var authentication = new UsernamePasswordAuthenticationToken(u, null, u.getAuthorities());
	        SecurityContextHolder.getContext().setAuthentication(authentication);
	        req.setAttribute("authentication", "authenticated");
	    });
	}

	private void handleTokenException(HttpServletRequest req, String attr, String message, Exception e) throws ServletException {
	    req.setAttribute(attr, message);
	    throw new ServletException(e.getMessage(), e);
	}
}