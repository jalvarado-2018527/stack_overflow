package com.is4tech.base.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender){this.mailSender = mailSender;}

    public void send(String subject, String email, String body, String from){
        logger.info("Sending emaill sampli {} to: {}", subject, email);
        try {
            var message = mailSender.createMimeMessage();
            var messageHelper = new MimeMessageHelper(message, true, "UTF-8");
            messageHelper.setFrom(from);
            messageHelper.setTo(email);
            messageHelper.setSubject(subject);
            messageHelper.setText(body, true);
            mailSender.send(message);
        }catch (Exception e){
            logger.error("Error send email sample " + subject + "to: " + email, e);
        }
    }

}
