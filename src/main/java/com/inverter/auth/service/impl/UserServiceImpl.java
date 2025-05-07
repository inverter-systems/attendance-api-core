package com.inverter.auth.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.inverter.auth.entity.User;
import com.inverter.auth.enums.IssueEnum;
import com.inverter.auth.exception.SecurityException;
import com.inverter.auth.exception.UserExistsException;
import com.inverter.auth.repository.RoleRepository;
import com.inverter.auth.repository.UserRepository;
import com.inverter.auth.service.EmailService;
import com.inverter.auth.service.MessageService;
import com.inverter.auth.service.TokenService;
import com.inverter.auth.service.UserService;
import com.inverter.auth.util.Bcrypt;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

	private UserRepository repo;
	private RoleRepository repoRole;
	private TokenService tokenService;
	private EmailService emailService;
	private MessageService msg;

	@Value("${zone.off.set}")
	private String zoneOffSet;
	
	final String TOKEN_EXPIRED_ERROR_MSG = msg.get("user.auth.token.error.expired");
    final String TOKEN_DECODE_ERROR_MSG = msg.get("user.auth.token.error.decode");
    final String TOKEN_SIGNATUE_ERROR_MSG = msg.get("user.auth.token.error.signature");

	public UserServiceImpl(UserRepository repo, TokenService tokenService,
			EmailService emailService, MessageService msg,
			RoleRepository repoRole) {
		this.repo = repo;
		this.tokenService = tokenService;
		this.emailService = emailService;
		this.msg = msg;
		this.repoRole = repoRole;
	}

	@Transactional
	public User create(User u) throws UserExistsException {
		// Verifca se ja existe o cadastro com email e username
		repo.findByUsername(u.getUsername()).ifPresent(s -> {
			throw new UserExistsException(msg.get("user.auth.create.user.error.same.username"));
		});

		repo.findByEmail(u.getEmail()).ifPresent(s -> {
			throw new UserExistsException(msg.get("user.auth.create.user.error.same.email"));
		});

		User user;
		u.setPassword(Bcrypt.getHash(u.getPassword()));
		u.setRoles(new ArrayList<>());
		repoRole.findByName("CUSTOMER_ROLE").ifPresent(e -> u.getRoles().add(e));
		user = repo.save(u);

		// Gerar token de ativação
		String token = tokenService.buildActivationToken(u);

		// Enviar email de ativação
		emailService.sendActivationEmail(user.getEmail(), token);
		return user;
	}

	@Transactional
	public User activationAcount(String token) throws SecurityException {
		var expirationToken = tokenService.getExpires(token, IssueEnum.ISSUE_ACTIVATION);
		if (expirationToken.isBefore(LocalDateTime.now().toInstant(ZoneOffset.of(zoneOffSet)))) {
			throw new SecurityException(msg.get("user.auth.token.ativacao.expired.error"));
		}

		var subject = tokenService.getSubject(token, IssueEnum.ISSUE_ACTIVATION);
		
		try {
			
			var user = repo.findByUsername(subject)
					.orElseThrow(() -> new SecurityException(msg.get("user.auth.token.ativacao.find.user.error")));

			user.setActive(true);
			user = repo.save(user);

			return user;
		} catch (Exception e) {
			throw new SecurityException(
					msg.get("user.auth.token.ativacao.generic.error", new Object[] { e.getMessage() }));
		}
	}

	@Transactional
	public User resetPasswordEmailToken(String email) throws SecurityException {
		User user = repo.findByEmail(email).orElseThrow(
				() -> new UsernameNotFoundException(msg.get("user.auth.token.reset.password.email.find.user.error")));

		try {
			var token = tokenService.buildPasswordResetToken(user);

			// Enviar email de ativação
			emailService.sendPasswordReset(user.getEmail(), token);

			return user;
		} catch (SecurityException e) {
			throw new SecurityException(
					msg.get("user.auth.email.reset.password.send.error", new Object[] { e.getMessage() }));
		}
	}

	@Transactional
	public User resetPassword(String token, String newPassword) throws SecurityException {
		try {
			var expirationToken = tokenService.getExpires(token, IssueEnum.ISSUE_RESET_PASSWORD);
			if (expirationToken.isBefore(LocalDateTime.now().toInstant(ZoneOffset.of(zoneOffSet)))) {
				throw new SecurityException(msg.get("user.auth.token.ativacao.expired.error"));
			}
		
			var subject = tokenService.getSubject(token, IssueEnum.ISSUE_RESET_PASSWORD);
			var user = repo.findByUsername(subject)
					.orElseThrow(() -> new SecurityException(msg.get("user.auth.token.reset.password.find.user.error")));
			
			user.setPassword(Bcrypt.getHash(newPassword));
			user = repo.save(user);

			return user;
		} catch (TokenExpiredException e) {
			throw new SecurityException(TOKEN_EXPIRED_ERROR_MSG);	
		} catch (JWTDecodeException e) {
			throw new SecurityException(TOKEN_DECODE_ERROR_MSG);
	    } catch (SignatureVerificationException e) {
	    	throw new SecurityException(TOKEN_SIGNATUE_ERROR_MSG);
	    } catch (Exception e) {
	    	throw new SecurityException(
					msg.get("user.auth.email.reset.password.error", new Object[] { e.getMessage() }));
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
}
