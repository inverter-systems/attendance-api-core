package com.inverter.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inverter.auth.dto.UserDTO;
import com.inverter.auth.entity.User;
import com.inverter.auth.exception.SecurityException;
import com.inverter.auth.service.UserService;
import com.inverter.auth.util.Response;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/auth/user")
public class UserController {

	private UserService service;

	public UserController(UserService service) {
		this.service = service;
	}

	@PostMapping
	public ResponseEntity<Response<UserDTO>> create(@Valid @RequestBody UserDTO userDto, BindingResult result) {
		Response<UserDTO> response = new Response<>();

		if (result.hasErrors()) {
			result.getAllErrors().forEach(e -> response.getErrors().add(e.getDefaultMessage()));
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		try {
			User user = service.create(userDto.buildUser());
			response.setData(userDto.buildUserDTO(user));
		} catch (Exception e) {
			response.getErrors().add(e.getMessage());
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@GetMapping("/activate")
    public ResponseEntity<String> activateAccount(@RequestParam String token) {
        try {
            User user = service.activationAcount(token);
            
            return ResponseEntity.ok(String.format("Conta ativada com sucesso, %s! Agora você pode fazer login.", user.getUsername()));
        } catch (RuntimeException | SecurityException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
