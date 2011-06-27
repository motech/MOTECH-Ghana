package org.motechproject.server.omod.web.controller;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.easymock.IArgumentMatcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.server.model.Email;
import org.motechproject.server.model.IncomingMessage;
import org.motechproject.server.omod.web.model.MailTemplate;
import org.motechproject.server.service.ContextService;
import org.motechproject.server.strategy.SupportCaseExtractionStrategy;
import org.motechproject.server.svc.MailingService;
import org.motechproject.server.util.MotechConstants;
import org.openmrs.PersonName;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.ModelAndView;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:mail-template-context.xml")
public class SupportCaseControllerTest {

    @Autowired
    private MailTemplate mailTemplate;

    @Test
    public void shouldSendErrorMessageBackIfCompleteInformationNotPresent() {
        SupportCaseController controller = new SupportCaseController();
        IncomingMessage message = new IncomingMessage();
        ModelAndView modelAndView = controller.mailToSupport(message);
        assertEquals("/module/motechmodule/response", modelAndView.getViewName());
        assertEquals("motechmodule.support.case.insufficient.info", modelAndView.getModelMap().get("response"));
    }

    @Test
    public void shouldMailToSupport() {

        MailingService mailingService = createMock(MailingService.class);
        ContextService contextService = createMock(ContextService.class);
        UserService userService = createMock(UserService.class);
        AdministrationService administrationService = createMock(AdministrationService.class);

        SupportCaseController controller = new SupportCaseController();
        controller.setMailingService(mailingService);
        controller.setContextService(contextService);
        controller.setMessageContentExtractionStrategy(new SupportCaseExtractionStrategy());
        controller.setMailTemplate(mailTemplate);

        IncomingMessage message = new IncomingMessage();

        message.setKey("SUPPORT");
        message.setCode("1982");
        message.setNumber("%2B233123456789");
        message.setText("SUPPORT+465+Cannot+Upload+Forms");
        message.setTime("2011-06-25+09:30:29");

        User staff = new User();
        staff.setSystemId("465");
        PersonName name = new PersonName("Joyee", "J", "Jee");
        staff.addName(name);

        expect(contextService.getUserService()).andReturn(userService);
        expect(contextService.getAdministrationService()).andReturn(administrationService).times(2);

        expect(userService.getUserByUsername("465")).andReturn(staff);

        expect(administrationService.getGlobalProperty(MotechConstants.GLOBAL_PROPERTY_SUPPORT_CASE_MAIL_TO)).andReturn("abc@abc.com");
        expect(administrationService.getGlobalProperty(MotechConstants.GLOBAL_PROPERTY_SUPPORT_CASE_MAIL_FROM)).andReturn("xyz@xyz.com");


        String mailSubject = "Support: Case reported by Joyee Jee on 2011-06-25 09:30:29";
        String mailText = "Joyee Jee with Staff id 465 reported the following issue: Cannot Upload Forms";

        mailingService.send(equalsMessage(new Email("abc@abc.com", "xyz@xyz.com", mailSubject, mailText)));
        expectLastCall();

        replay(userService, contextService, administrationService, mailingService);

        ModelAndView modelAndView = controller.mailToSupport(message);

        verify(userService, contextService, administrationService, mailingService);

        assertEquals("/module/motechmodule/response", modelAndView.getViewName());
        assertEquals("motechmodule.support.case.mail.acknowledgement", modelAndView.getModelMap().get("response"));

    }

    private static Email equalsMessage(Email email) {
        reportMatcher(new MailMessageMatcher(email));
        return email;
    }
}

class MailMessageMatcher implements IArgumentMatcher {

    private Email message;

    public MailMessageMatcher(Email message) {
        this.message = message;
    }

    public boolean matches(Object other) {
        Email otherMail = (Email) other;
        return EqualsBuilder.reflectionEquals(message, otherMail);
    }

    public void appendTo(StringBuffer buffer) {
        buffer.append("Expected ").append(message);
    }
}