package com.videoplatform.videoplatform.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private static final Logger LOGGER=Logger.getLogger(EmailService.class.getName());

    @Autowired
    public EmailService(JavaMailSender mailSender){

        this.mailSender=mailSender;

    }
    public void SendEmail(String to, String subject, String text){
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            LOGGER.info("Email sent successfully to " + to);
        }
        catch (MailSendException e){
            LOGGER.log(Level.SEVERE,"Failed to send email: " +e.getMessage(),e);
            throw new RuntimeException("Failed to send email: " +e.getMessage(), e);

        }
    }

}
