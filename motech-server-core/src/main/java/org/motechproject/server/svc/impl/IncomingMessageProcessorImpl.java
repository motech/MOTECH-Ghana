package org.motechproject.server.svc.impl;

import org.motechproject.server.model.IncomingMessage;
import org.motechproject.server.model.MessageProcessorURL;
import org.motechproject.server.model.db.MessageProcessorDAO;
import org.motechproject.server.svc.IncomingMessageProcessor;
import org.motechproject.server.svc.SupportCaseService;
import org.motechproject.server.svc.WebClient;
import org.motechproject.server.util.MailingConstants;
import org.motechproject.ws.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;

public class IncomingMessageProcessorImpl implements IncomingMessageProcessor {

    @Autowired
    private MessageProcessorDAO dao;

    @Autowired
    private WebClient webClient;

    @Transactional
    public Response process(IncomingMessage incomingMessage) throws UnsupportedEncodingException {
        log(incomingMessage);
        return responseFromMappedURL(incomingMessage);
    }

    private Response responseFromMappedURL(IncomingMessage incomingMessage) throws UnsupportedEncodingException {
        MessageProcessorURL processorURL = dao.urlFor(incomingMessage.getKey());
        if(processorURL != null){
            StringBuilder url = new StringBuilder(processorURL.getUrl());
            url.append(incomingMessage.requestParameters());
            return webClient.get(url.toString());
        }
        return new Response(MailingConstants.KEY_NOT_SUPPORTED);
    }


    private void log(IncomingMessage incomingMessage) {
        dao.save(incomingMessage);
    }

    public void setDao(MessageProcessorDAO dao) {
        this.dao = dao;
    }

    public void setWebClient(WebClient webClient) {
        this.webClient = webClient;
    }
}
