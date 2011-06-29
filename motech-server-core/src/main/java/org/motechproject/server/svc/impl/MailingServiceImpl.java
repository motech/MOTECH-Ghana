package org.motechproject.server.svc.impl;

import org.motechproject.server.factory.MailSenderFactory;
import org.motechproject.server.model.Email;
import org.motechproject.server.svc.MailingService;
import org.springframework.mail.SimpleMailMessage;

public class MailingServiceImpl implements MailingService{

    private MailSenderFactory mailSenderFactory;

    public MailingServiceImpl(MailSenderFactory mailSenderFactory) {
        this.mailSenderFactory = mailSenderFactory;
    }

    public void send(Email mail) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(mail.to());
        mailMessage.setFrom(mail.from());
        mailMessage.setSubject(mail.subject());
        mailMessage.setText(mail.text());
        mailSenderFactory.mailSender().send(mailMessage);
    }
}
