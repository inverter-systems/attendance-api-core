package com.inverter.auth.service.impl;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.inverter.auth.exception.SecurityException;
import com.inverter.auth.service.EmailService;
import com.inverter.auth.service.MessageService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

    private JavaMailSender mailSender;
    private TemplateEngine templateEngine;
    private MessageService msg;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.base.url}")
    private String baseUrl;
    
	@Value("${jwt.expiration.user.activation.min}")
	private Integer expirationActivation;
	
	@Value("${jwt.expiration.user.reset.password.min}")
	private Integer expirationResetPassword;
    
    public EmailServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine, MessageService msg) {
    	this.mailSender = mailSender;
    	this.templateEngine = templateEngine;
    	this.msg = msg;
    } 
    
    public void sendActivationEmail(String to, String token) throws SecurityException {
        try {
        	Locale locale = LocaleContextHolder.getLocale();
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(msg.get("user.auth.email.activation.title"));
            
            Context context = new Context(locale);
            context.setVariable("activationLink", baseUrl + "/api/auth/user/activate?lang=" + locale.toLanguageTag() + "&token=" + token);
            context.setVariable("userName", to);
            
            var validFor = msg.get("template.email.activation.account.text.valid.link", new Object[]{expirationActivation, getLabelTime(expirationActivation)});
            context.setVariable("validFor", validFor);
            
            String content = templateEngine.process("activation-email", context);
            helper.setText(content, true);
            
            mailSender.send(message);
            
        } catch (MessagingException e) {
        	 throw new SecurityException(msg.get("user.auth.email.activation.send.error", new Object[]{e.getMessage()}));
        }
    } 
    
    public void sendPasswordReset(String to, String token) throws SecurityException {
        try {
        	Locale locale = LocaleContextHolder.getLocale();
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(msg.get("user.auth.email.reset.password.title"));
            
            Context context = new Context(locale);
            context.setVariable("resetPassword", baseUrl + "/api/auth/user/reset-password-ui?lang=" + locale.toLanguageTag() + "&token=" + token);
            context.setVariable("userName", to);
            
            var validFor = msg.get("template.email.reset.password.account.text.valid.link", new Object[]{expirationResetPassword, getLabelTime(expirationResetPassword)});
            context.setVariable("validFor", validFor);
            
            String content = templateEngine.process("email/reset-password-email", context);
            helper.setText(content, true);
            
            mailSender.send(message);
            
        } catch (MessagingException e) {
        	 throw new SecurityException(msg.get("user.auth.email.reset.password.send.error", new Object[]{e.getMessage()}));
        }
    }

	private String getLabelTime(Integer label) {
		if (label%60 == 0 && label/60 == 1 ) {
			return msg.get("general.key.hour");
		} else if (label%60 == 0 && label/60 > 1 ) {
			return  msg.get("general.key.hours");
		} 
		return  msg.get("general.key.min");
	}
}