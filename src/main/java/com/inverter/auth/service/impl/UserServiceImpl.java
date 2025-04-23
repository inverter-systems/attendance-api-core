package com.inverter.auth.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.inverter.auth.exception.SecurityException;
import com.inverter.auth.entity.ActivationToken;
import com.inverter.auth.entity.User;
import com.inverter.auth.repository.ActivationTokenRepository;
import com.inverter.auth.repository.UserRepository;
import com.inverter.auth.service.EmailService;
import com.inverter.auth.service.TokenService;
import com.inverter.auth.service.UserService;
import com.inverter.auth.util.Bcrypt;

@Service
public class UserServiceImpl implements UserService, UserDetailsService  {
	
	private UserRepository repo;
	private ActivationTokenRepository repoActivationToken;
	private TokenService tokenService;
	private EmailService emailService;
		
	public UserServiceImpl(UserRepository repo, TokenService tokenService, ActivationTokenRepository repoActivationToken, EmailService emailService) {
		this.repo = repo;
		this.tokenService = tokenService;
		this.repoActivationToken = repoActivationToken;
		this.emailService = emailService;
	}

	@Transactional
	public User create(User u) throws SecurityException {
		User user;
		try {
			u.setPassword(Bcrypt.getHash(u.getPassword()));
			user = repo.save(u);
			
			// Gerar token de ativação
	        String token = tokenService.gerarActivationToken(u);
	        repoActivationToken.save(ActivationToken.builder().token(token).user(user).build());
	        
	        // Enviar email de ativação
	        emailService.sendActivationEmail(user.getEmail(), token);
			
			return user;
		} catch (DataIntegrityViolationException e) {
			throw new SecurityException("Usuario já cadastrado com mesmo nome e ou email"); 
		} catch (Exception e) {
			throw new SecurityException("Erro no envio de email de ativação da conta"); 
		}
	} 
	
	@Transactional
	public User activationAcount(String token) throws SecurityException {
		try {
			var activationToken = repoActivationToken.findById(token)
					.orElseThrow(() -> new SecurityException("Token de ativação inválido"));
			
			if (tokenService.getExpiresActivationAccount(token).isBefore(LocalDateTime.now().toInstant(ZoneOffset.of("-03:00")))) {
				throw new SecurityException("Token de ativação expirado");
			}
			
			var user = repo.findById(activationToken.getUser().getId())
					.orElseThrow(() -> new SecurityException("Erro na busca do usuario para ativação"));
			
			user.setActive(true);
			user = repo.save(user);
			
			repoActivationToken.delete(activationToken);
			
			return user;
		} catch (Exception e) {
			throw new SecurityException("Erro na ativação da conta: "+e.getMessage());
		}
	}
	
	public Optional<User> findByEmail(String email) {
		return repo.findByEmail(email);
	}

	public Optional<User> findByUsername(String name) {
		return repo.findByUsername(name);
	}

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		var user = repo.findByUsername(username);
		return user.isPresent() ? user.get() : null;
	}
};
