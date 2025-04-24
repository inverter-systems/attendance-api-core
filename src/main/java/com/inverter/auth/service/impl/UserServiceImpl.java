package com.inverter.auth.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inverter.auth.entity.ActivationToken;
import com.inverter.auth.entity.User;
import com.inverter.auth.exception.SecurityException;
import com.inverter.auth.exception.SecurityRuntimeException;
import com.inverter.auth.repository.ActivationTokenRepository;
import com.inverter.auth.repository.UserRepository;
import com.inverter.auth.service.EmailService;
import com.inverter.auth.service.MessageService;
import com.inverter.auth.service.TokenService;
import com.inverter.auth.service.UserService;
import com.inverter.auth.util.Bcrypt;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

	private UserRepository repo;
	private ActivationTokenRepository repoActivationToken;
	private TokenService tokenService;
	private EmailService emailService;
	private MessageService msg;
	
	@Value("${zone.off.set}")
	private String zoneOffSet;

	public UserServiceImpl(UserRepository repo, TokenService tokenService,
			ActivationTokenRepository repoActivationToken, EmailService emailService, MessageService msg) {
		this.repo = repo;
		this.tokenService = tokenService;
		this.repoActivationToken = repoActivationToken;
		this.emailService = emailService;
		this.msg = msg;
	}

	@Transactional
	public User create(User u) throws SecurityException {
		// Verifca se ja existe o cadastro com email e username
		repo.findByUsername(u.getUsername()).ifPresent(s -> {
			throw new SecurityRuntimeException(msg.get("user.auth.create.user.error.same.username"));
		});
		repo.findByEmail(u.getEmail()).ifPresent(s -> {
			throw new SecurityRuntimeException(msg.get("user.auth.create.user.error.same.email"));
		});

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
		} catch (Exception e) {
			throw new SecurityException(
					msg.get("user.auth.email.activation.send.error", new Object[] { e.getMessage() }));
		}
	}

	@Transactional
	public User activationAcount(String token) throws SecurityException {
		try {
			var activationToken = repoActivationToken.findById(token)
					.orElseThrow(() -> new SecurityException(msg.get("user.auth.token.ativacao.invalid.error")));

			if (tokenService.getExpiresActivationAccount(token)
					.isBefore(LocalDateTime.now().toInstant(ZoneOffset.of(zoneOffSet)))) {
				throw new SecurityException(msg.get("user.auth.token.ativacao.expired.error"));
			}

			var user = repo.findById(activationToken.getUser().getId())
					.orElseThrow(() -> new SecurityException(msg.get("user.auth.token.ativacao.find.user.error")));

			user.setActive(true);
			user = repo.save(user);

			repoActivationToken.delete(activationToken);

			return user;
		} catch (Exception e) {
			throw new SecurityException(msg.get("user.auth.token.ativacao.generic.error", new Object[] { e.getMessage() }));
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
