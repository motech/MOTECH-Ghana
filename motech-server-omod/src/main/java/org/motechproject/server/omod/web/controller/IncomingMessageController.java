package org.motechproject.server.omod.web.controller;

import org.motechproject.server.model.IncomingMessage;
import org.motechproject.server.model.MessageProcessorURL;
import org.motechproject.server.model.db.MessageProcessorDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.UnsupportedEncodingException;

@Controller
public class IncomingMessageController {

    @Autowired
    private MessageProcessorDAO dao;
    private static final String REDIRECT = "redirect:";

    @Transactional
    @RequestMapping(value = "/module/motechmodule/incomingmessage",method = RequestMethod.GET)
    public String redirect(IncomingMessage incomingMessage) throws UnsupportedEncodingException {
        log(incomingMessage);
        MessageProcessorURL url = processorURL(incomingMessage);
        return new StringBuilder().append(REDIRECT).append(url.toString())
                .append(incomingMessage.requestParameters()).toString();
    }

    private MessageProcessorURL processorURL(IncomingMessage incomingMessage) {
        MessageProcessorURL processorURL = dao.urlFor(incomingMessage.getKey());
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
}
