package com.pvmeira.wildwest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender emailSender;

    @Autowired
    public MailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void send(String email, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@wildwest.com");
        message.setTo(email);
        message.setSubject("Sua Conta foi criada com sucesso.");
        message.setText("Sua nova senha é [ " + password + " ]");
        emailSender.send(message);

    }

}
