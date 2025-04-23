package com.inverter.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inverter.auth.dto.LoginDTO;
import com.inverter.auth.entity.User;
import com.inverter.auth.repository.UserRepository;
import com.inverter.auth.service.TokenService;
import com.inverter.auth.util.Response;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class AuthController {
	
	private final AuthenticationManager authManager;
    private TokenService tokenService;
	private UserRepository userRepo;
	
	public AuthController(AuthenticationManager authManager, TokenService tokenService, UserRepository userRepo) {
		this.authManager = authManager;
		this.tokenService = tokenService;
		this.userRepo = userRepo;
	}

	@PostMapping("/auth")
    public ResponseEntity<Response<LoginDTO>> auth(@Valid @RequestBody LoginDTO login, BindingResult result) {
		var response = new Response<LoginDTO>();
		var user = this.userRepo.findByUsername(login.username());
		
		if (result.hasErrors()) {
			result.getAllErrors().forEach(e -> response.getErrors().add(e.getDefaultMessage()));
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		
		try {
			var userToken = new UsernamePasswordAuthenticationToken(login.username(), login.password());
			var authenticate = authManager.authenticate(userToken);
			var usuario = (User) authenticate.getPrincipal();
			
			var token = tokenService.gerarToken(usuario);
			var expiresAt = tokenService.getExpires(token);
			
			response.setData(new LoginDTO(login.username(), login.password(), token, expiresAt, usuario));
		} catch (Exception e) {
			if (user.isEmpty()) {
				response.getErrors().add("Access denied, user not found!");
			} else {
				response.getErrors().add(e.getMessage());
			}
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }	
}
