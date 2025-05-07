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
import com.inverter.auth.service.MessageService;
import com.inverter.auth.service.UserService;
import com.inverter.auth.util.Response;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/auth/user")
public class UserController {

	private UserService service;
	private MessageService msg;

	public UserController(UserService service, MessageService msg) {
		this.service = service;
		this.msg = msg;
	}

	@PostMapping
	public ResponseEntity<Response<UserDTO>> create(@Valid @RequestBody UserDTO userDto, HttpServletRequest req, BindingResult result) {
		Response<UserDTO> resp = new Response<>();

		if (result.hasErrors()) {
			result.getAllErrors().forEach(e -> resp.getErrors().add(e.getDefaultMessage()));
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
		}
		
		try {
			User user = service.create(userDto.buildUser());
			resp.setData(userDto.buildUserDTO(user));
			
		} catch (Exception e) {
			resp.getErrors().add(e.getMessage());
			return ResponseEntity.status(HttpStatus.CONFLICT).body(resp);
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(resp);
	}
	
	@GetMapping("/activate")
    public ResponseEntity<String> activateAccount(@RequestParam String token) {
        try {
            User user = service.activationAcount(token);
            
            return ResponseEntity.ok(String.format(msg.get("template.email.activation.account.sucess"), user.getUsername()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
	
	@PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
		try {
			service.resetPasswordEmailToken(email);
			
			return ResponseEntity.ok(msg.get("user.auth.token.reset.password.email.send.sucess"));
		} catch (SecurityException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
    }
}
