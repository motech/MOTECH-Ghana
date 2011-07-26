package org.motechproject.server.svc.impl;

import org.motechproject.server.model.IncomingMessage;
import org.motechproject.server.model.MessageProcessorURL;
import org.motechproject.server.model.db.MessageProcessorDAO;
import org.motechproject.server.svc.IncomingMessageProcessor;
import org.motechproject.server.svc.SupportCaseService;
import org.motechproject.server.svc.WebClient;
import org.motechproject.ws.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class IncomingMessageProcessorImpl implements IncomingMessageProcessor {

    @Autowired
    private MessageProcessorDAO dao;

    @Autowired
    private SupportCaseService supportCaseService ;

    @Autowired
    private WebClient webClient;

    @Transactional
    public Response process(IncomingMessage incomingMessage) {
        log(incomingMessage);
        if(incomingMessage.isFor("SUPPORT")){
            return supportCaseService.mailToSupport(incomingMessage);
        }
        return sendDataToExternalURL(incomingMessage);
    }

    private Response sendDataToExternalURL(IncomingMessage incomingMessage) {
        MessageProcessorURL url = processorURL(incomingMessage.getKey());
        return webClient.sendDataTo(url);
    }

    private MessageProcessorURL processorURL(String key) {
        MessageProcessorURL processorURL = dao.urlFor(key);
        if(processorURL == null){
            processorURL = dao.urlFor("DEFAULT");
        }
        return processorURL;
    }


    private void log(IncomingMessage incomingMessage) {
        dao.save(incomingMessage);
    }

    public void setDao(MessageProcessorDAO dao) {
        this.dao = dao;
    }

    public void setSupportCaseService(SupportCaseService supportCaseService) {
        this.supportCaseService = supportCaseService;
    }

    public void setWebClient(WebClient webClient) {
        this.webClient = webClient;
    }
}
