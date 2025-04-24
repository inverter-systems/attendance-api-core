package com.inverter.auth.service.impl;

import org.springframework.beans.factory.annotation.Value;
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
    
    public EmailServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine, MessageService msg) {
    	this.mailSender = mailSender;
    	this.templateEngine = templateEngine;
    	this.msg = msg;
    } 
    
    public void sendActivationEmail(String to, String token) throws SecurityException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(msg.get("user.auth.email.activation.title"));
            
            Context context = new Context();
            context.setVariable("activationLink", baseUrl + "/activate?token=" + token);
            context.setVariable("userName", to);
            
            String content = templateEngine.process("activationEmail", context);
            helper.setText(content, true);
            
            mailSender.send(message);
            
        } catch (MessagingException e) {
        	 throw new SecurityException(msg.get("user.auth.email.activation.send.error", new Object[]{e.getMessage()}));
        }
    }
}