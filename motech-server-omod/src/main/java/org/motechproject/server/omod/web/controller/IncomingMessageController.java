package org.motechproject.server.omod.web.controller;

import org.motechproject.server.model.IncomingMessage;
import org.motechproject.server.model.MessageProcessorURL;
import org.motechproject.server.model.db.MessageProcessorDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.UnsupportedEncodingException;

@Controller
public class IncomingMessageController {

    @Autowired
    private MessageProcessorDAO messageProcessorDAO;
    private static final String REDIRECT = "redirect:";

    @RequestMapping(value = "/module/motechmodule/incomingmessage",method = RequestMethod.GET)
    public String redirect(IncomingMessage incomingMessage) throws UnsupportedEncodingException {
        MessageProcessorURL url = messageProcessorDAO.urlFor(incomingMessage.getKey());
        return new StringBuilder().append(REDIRECT).append(url.toString())
                .append(incomingMessage.requestParameters()).toString();
    }

    
}
