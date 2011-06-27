package org.motechproject.server.svc.impl;

import org.motechproject.server.model.Email;
import org.motechproject.server.svc.MailingService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class MailingServiceImpl implements MailingService{

    private JavaMailSender messenger;

    public MailingServiceImpl(JavaMailSender messenger) {
        this.messenger = messenger;
    }

    public void send(Email mail) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(mail.to());
        mailMessage.setFrom(mail.from());
        mailMessage.setSubject(mail.subject());
        mailMessage.setText(mail.text());
        messenger.send(mailMessage);
    }
}
