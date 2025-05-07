package com.inverter.auth;

import java.io.IOException;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inverter.auth.dto.ErrorDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Scope(value="request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint  {
 
    @Override
    public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException authException) throws IOException, ServletException {
        res.setContentType("application/json;charset=UTF-8");
        res.setStatus(HttpStatus.UNAUTHORIZED.value());
        
        var error = (String) req.getAttribute("error");
        var mapper = new ObjectMapper();
       
       if (error != null) {
        	res.setStatus(HttpStatus.BAD_REQUEST.value());
        	res.getWriter().write(mapper.writeValueAsString(ErrorDTO.buildError(error)));
        } else {
            res.getWriter().write(mapper.writeValueAsString(ErrorDTO.buildError(authException.getMessage())));
        }    
    }
}