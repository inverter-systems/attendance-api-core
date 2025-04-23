package com.inverter.auth.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.inverter.auth.exception.SecurityException;
import com.inverter.auth.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.base.url}")
    private String baseUrl;
    
    public EmailServiceImpl(JavaMailSender mailSender) {
    	this.mailSender = mailSender;
    }
    
    public void sendActivationEmail(String to, String token) throws SecurityException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Ativação de conta - Sistema Attendance");
            
            String activationLink = baseUrl + "/api/auth/user/activate?token=" + token;
            
            String htmlContent = """
                    <h1>Bem-vindo ao Sistema Attendance!</h1>
                    <p>Olá %s,</p>
                    <p>Obrigado por se registrar em nosso sistema. Para ativar sua conta, por favor clique no link abaixo:</p>
                    <p><a href='%s'>%s</a></p>
                    <p>Este link é válido por 24 horas.</p>
                    <p>Se você não solicitou este registro, por favor ignore este email.</p>
                    <p>Atenciosamente,<br/>Equipe Attendance</p>
                    """.formatted(to, activationLink, activationLink);
            
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            
        } catch (MessagingException e) {
            throw new SecurityException("Erro ao enviar email de ativação: "+e.getMessage());
        }
    }
}