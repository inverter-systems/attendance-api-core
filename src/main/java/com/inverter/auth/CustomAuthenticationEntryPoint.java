package com.inverter.auth;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.inverter.auth.util.Error;

@Scope(value="request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint  {
 
    @Override
    public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException authException) throws IOException, ServletException {
        res.setContentType("application/json;charset=UTF-8");
        res.setStatus(HttpStatus.UNAUTHORIZED.value());
        
        
        var expired = (String) req.getAttribute("expired");
        var error = (String) req.getAttribute("error");
        var auth = req.getAttribute("authentication");
        var mapper = new ObjectMapper();
        
        if (expired != null) {  
            res.getWriter().write(mapper.writeValueAsString(getError(401, expired, req.getServletPath())));
        } else if (auth != null) {
        	res.setStatus(400);
            res.getWriter().write(mapper.writeValueAsString(getError(400, error, req.getServletPath())));
        } else {
            res.getWriter().write(mapper.writeValueAsString(getError(401, authException.getMessage(), req.getServletPath())));
        }    
    }
    
    private Error getError(int status, String msg, String path) {
    	Error e = new Error();
    	e.setStatus(status);
    	e.setMessage(msg);
    	e.setPath(path);
    	e.setTimestamp(LocalDateTime.now().toString());
    	e.setErro(HttpStatus.valueOf(status).getReasonPhrase());
    	return e;
    	
    }
}