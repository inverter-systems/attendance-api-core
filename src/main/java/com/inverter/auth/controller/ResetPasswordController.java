package com.inverter.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.inverter.auth.exception.SecurityException;
import com.inverter.auth.service.MessageService;
import com.inverter.auth.service.UserService;

@Controller
@RequestMapping("api/auth/user")
public class ResetPasswordController {
	
	private UserService service;
	private MessageService msg;

	public ResetPasswordController(UserService service, MessageService msg) {
		this.service = service;
		this.msg = msg;
	}
	
	@GetMapping("/reset-password-ui")
    public String exibirTelaReset(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "reset-password";
    }

	@PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        try {
			service.resetPassword(token, newPassword);
			
			return ResponseEntity.ok(msg.get("user.auth.token.reset.password.sucess"));
		} catch (SecurityException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
    }

}
