package br.com.alura.adopet.api.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String remetente;

    @Autowired
    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void enviarEmail(String to, String subject, String message) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(remetente);
        email.setTo(to);
        email.setSubject(subject);
        email.setText(message);
        emailSender.send(email);
    }
}
