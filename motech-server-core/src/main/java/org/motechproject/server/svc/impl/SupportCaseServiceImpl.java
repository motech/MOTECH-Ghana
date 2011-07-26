package org.motechproject.server.svc.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.model.Email;
import org.motechproject.server.model.IncomingMessage;
import org.motechproject.server.model.MailTemplate;
import org.motechproject.server.model.SupportCase;
import org.motechproject.server.service.ContextService;
import org.motechproject.server.strategy.MessageContentExtractionStrategy;
import org.motechproject.server.svc.MailingService;
import org.motechproject.server.svc.OpenmrsBean;
import org.motechproject.server.svc.SupportCaseService;
import org.motechproject.server.util.MailingConstants;
import org.motechproject.server.util.MotechConstants;
import org.motechproject.ws.Response;
import org.openmrs.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashMap;
import java.util.Map;

public class SupportCaseServiceImpl implements SupportCaseService {

    private static Log log = LogFactory.getLog(SupportCaseServiceImpl.class);


    @Autowired
    private MailingService mailingService;

    @Autowired
    private MessageContentExtractionStrategy messageContentExtractionStrategy;

    @Autowired
    private MailTemplate mailTemplate;

    @Autowired
    private ContextService contextService;

    @Autowired
    @Qualifier("registrarBeanProxy")
    private OpenmrsBean openmrsBean;


    public Response mailToSupport(IncomingMessage incomingMessage) {
        log.info("Received support case request " + incomingMessage);
        try {
            if (incomingMessage.hasSufficientInformation() && incomingMessage.isFor("SUPPORT")) {
                return sendSupportMail(incomingMessage);
            }
        } catch (Exception ex) {
            log.fatal("Could not raise support mail for " + incomingMessage, ex);
            return new Response(MailingConstants.FAILED_TO_RAISE_SUPPORT_CASE);
        }
        return new Response(MailingConstants.INSUFFICIENT_INFO_FOR_SUPPORT_CASE);
    }

    private Response sendSupportMail(IncomingMessage incomingMessage) {
        SupportCase supportCase = (SupportCase) messageContentExtractionStrategy.extractFrom(incomingMessage);
        User staff = openmrsBean.getStaffBySystemId(supportCase.getRaisedBy());
        Map data = new HashMap();
        data.put("staff", staff);
        data.put("case", supportCase);
        mailingService.send(new Email(to(), from(), mailTemplate.subject(data), mailTemplate.text(data)));
        log.info("mail sent successfully");
        return new Response(MailingConstants.SUPPORT_CASE_CREATION_ACKNOWLEDGEMENT);
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

    public void setOpenmrsBean(OpenmrsBean openmrsBean) {
        this.openmrsBean = openmrsBean;
    }

    private String to() {
        return contextService.getAdministrationService().getGlobalProperty(MotechConstants.GLOBAL_PROPERTY_SUPPORT_CASE_MAIL_TO);
    }

    private String from() {
        return contextService.getAdministrationService().getGlobalProperty(MotechConstants.GLOBAL_PROPERTY_SUPPORT_CASE_MAIL_FROM);
    }
}
