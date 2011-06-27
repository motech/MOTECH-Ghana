package org.motechproject.server.omod.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.model.Email;
import org.motechproject.server.model.IncomingMessage;
import org.motechproject.server.model.SupportCase;
import org.motechproject.server.omod.web.model.MailTemplate;
import org.motechproject.server.omod.web.model.WebStaff;
import org.motechproject.server.service.ContextService;
import org.motechproject.server.strategy.MessageContentExtractionStrategy;
import org.motechproject.server.svc.MailingService;
import org.motechproject.server.util.MotechConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

import static org.motechproject.server.util.MailingConstants.*;

@Controller
public class SupportCaseController {

    private static Log log = LogFactory.getLog(SupportCaseController.class);

    private static final String VIEW = "/module/motechmodule/response";
    private static final String RESPONSE = "response";

    @Autowired
    private MailingService mailingService;

    @Autowired
    private MessageContentExtractionStrategy messageContentExtractionStrategy;

    @Autowired
    private MailTemplate mailTemplate;

    @Autowired
    private ContextService contextService;


    @RequestMapping(value = "/module/motechmodule/supportcase", method = RequestMethod.GET)
    public ModelAndView mailToSupport(@ModelAttribute IncomingMessage incomingMessage) {
        log.info("Received support case request " + incomingMessage);
        ModelAndView response = new ModelAndView(VIEW);
        try {
            if (incomingMessage.hasInformation()) {
                return sendSupportMail(incomingMessage);
            }
            response.addObject(RESPONSE, INSUFFICIENT_INFO_FOR_SUPPORT_CASE);

        } catch (Exception ex) {
            log.fatal("Could not raise support mail for " + incomingMessage, ex);
            response.addObject(RESPONSE, FAILED_TO_RAISE_SUPPORT_CASE);
        }
        return response;
    }

    private ModelAndView sendSupportMail(IncomingMessage incomingMessage) {
        ModelAndView response = new ModelAndView(VIEW);
        SupportCase supportCase = (SupportCase) messageContentExtractionStrategy.extractFrom(incomingMessage);
        WebStaff staff = new WebStaff(contextService.getUserService().getUserByUsername(supportCase.getRaisedBy()));
        Map data = new HashMap();
        data.put("staff", staff);
        data.put("case", supportCase);
        mailingService.send(new Email(to(), from(), mailTemplate.subject(data), mailTemplate.text(data)));
        response.addObject(RESPONSE, SUPPORT_CASE_CREATION_ACKNOWLEDGEMENT);
        log.info("mail sent successfully");
        return response;
    }

    public void setMailingService(MailingService mailingService) {
        this.mailingService = mailingService;
    }

    public void setMessageContentExtractionStrategy(MessageContentExtractionStrategy messageContentExtractionStrategy) {
        this.messageContentExtractionStrategy = messageContentExtractionStrategy;
    }

    public void setMailTemplate(MailTemplate mailTemplate) {
        this.mailTemplate = mailTemplate;
    }

    public void setContextService(ContextService contextService) {
        this.contextService = contextService;
    }

    private String to() {
        return contextService.getAdministrationService().getGlobalProperty(MotechConstants.GLOBAL_PROPERTY_SUPPORT_CASE_MAIL_TO);
    }

    private String from() {
        return contextService.getAdministrationService().getGlobalProperty(MotechConstants.GLOBAL_PROPERTY_SUPPORT_CASE_MAIL_FROM);
    }
}
