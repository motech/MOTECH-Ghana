package org.motechproject.server.omod.web.controller;

import org.motechproject.server.omod.web.model.WebBulkMessage;
import org.motechproject.server.omod.web.model.WebResponse;
import org.motechproject.ws.MessageStatus;
import org.motechproject.ws.mobile.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SMSController {

    private static final String VIEW = "/module/motechmodule/sms";

    @Autowired
    @Qualifier("mobileClient")
    private MessageService messageService;


    @RequestMapping(value = VIEW, method = RequestMethod.GET)
    public ModelAndView render() {
        return new ModelAndView(VIEW, "bulkMessage", new WebBulkMessage());
    }


    @RequestMapping(value = VIEW, method = RequestMethod.POST)
    public ModelAndView send(@ModelAttribute WebBulkMessage bulkMessage) {
        MessageStatus status = messageService.sendMessage(bulkMessage.content(), bulkMessage.recipients());
        ModelAndView modelAndView = new ModelAndView(VIEW);
        WebResponse response = createResponse(status,bulkMessage);
        modelAndView.addObject("response", response);
        modelAndView.addObject("bulkMessage", bulkMessage);

        return modelAndView;
    }

    private WebResponse createResponse(MessageStatus status,WebBulkMessage message) {

        if (MessageStatus.REJECTED.equals(status)) {
            return new WebResponse("Message Delivery Failed");
        }

        WebResponse response = new WebResponse("Message delivered successfully to " + message.recipients());
        response.markSuccess();
        return response;
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }
}
