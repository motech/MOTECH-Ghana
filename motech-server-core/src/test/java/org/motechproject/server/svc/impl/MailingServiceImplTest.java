package org.motechproject.server.svc.impl;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.easymock.IArgumentMatcher;
import org.junit.Test;
import org.motechproject.server.factory.MailSenderFactory;
import org.motechproject.server.model.Email;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.easymock.EasyMock.*;

public class MailingServiceImplTest {

    @Test
    public void shouldEmail() {
        MailSenderFactory mailSenderFactory = createMock(MailSenderFactory.class);
        JavaMailSender sender = createMock(JavaMailSender.class);

        expect(mailSenderFactory.mailSender()).andReturn(sender);

        sender.send(equalsMessage(expectedMessage()));
        expectLastCall();

        Email mail = new Email("abc@abc.com", "xyz@xyz.com", "Hi", "Hello World");

        replay(mailSenderFactory,sender);

        new MailingServiceImpl(mailSenderFactory).send(mail);

        verify(mailSenderFactory,sender);
    }

    private SimpleMailMessage expectedMessage() {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo("abc@abc.com");
        mailMessage.setFrom("xyz@xyz.com");
        mailMessage.setSubject("Hi");
        mailMessage.setText("Hello World");
        return mailMessage;
    }

    private static SimpleMailMessage equalsMessage(SimpleMailMessage message) {
        reportMatcher(new MailMessageMatcher(message));
        return message;
    }

}

class MailMessageMatcher implements IArgumentMatcher {

    private Object message;

    public MailMessageMatcher(Object message) {
        this.message = message;
    }

    public boolean matches(Object other) {
        return EqualsBuilder.reflectionEquals(message, other);
    }

    public void appendTo(StringBuffer buffer) {
        buffer.append("Expected ").append(message);
    }
}

