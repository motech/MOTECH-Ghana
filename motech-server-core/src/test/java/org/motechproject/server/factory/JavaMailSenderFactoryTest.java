package org.motechproject.server.factory;

import org.junit.Test;
import org.motechproject.server.util.MailingConstants;
import org.openmrs.api.AdministrationService;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.Session;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JavaMailSenderFactoryTest {

    @Test
    public void shouldCreateJavaMailSenderImpl() {
        AdministrationService administrationService = createMock(AdministrationService.class);
        JavaMailSenderFactory mailSenderFactory = new JavaMailSenderFactory();
        mailSenderFactory.setAdministrationService(administrationService);
        
        expect(administrationService.getGlobalProperty(MailingConstants.SMTP_HOST)).andReturn("127.0.0.1");
        expect(administrationService.getGlobalProperty(MailingConstants.SMTP_PORT)).andReturn("26");
        expect(administrationService.getGlobalProperty(MailingConstants.SMTP_AUTH)).andReturn("true").times(2);
        expect(administrationService.getGlobalProperty(MailingConstants.SMTP_USER)).andReturn("user");
        expect(administrationService.getGlobalProperty(MailingConstants.SMTP_PASSWORD)).andReturn("password");

        replay(administrationService);

        JavaMailSenderImpl mailSender = (JavaMailSenderImpl) mailSenderFactory.mailSender();

        verify(administrationService);

        Session session = mailSender.getSession();
        assertNotNull(session);
        assertEquals("127.0.0.1",session.getProperty("mail.smtp.host"));
        assertEquals("26",session.getProperty("mail.smtp.port"));
        assertEquals("true",session.getProperty("mail.smtp.auth"));
    }

}
