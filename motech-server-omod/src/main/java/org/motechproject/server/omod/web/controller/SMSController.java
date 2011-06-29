package org.motechproject.server.omod.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.omod.web.encoder.SpaceEncoder;
import org.motechproject.server.omod.web.model.JSONLocationSerializer;
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

import java.util.Arrays;

import static org.apache.commons.lang.StringUtils.join;

@Controller
public class SMSController {

    private static final String VIEW = "/module/motechmodule/sms";

    private static Log log = LogFactory.getLog(SMSController.class);

    @Autowired
    private JSONLocationSerializer locationSerializer;

    @Autowired
    @Qualifier("mobileClient")
    private MessageService messageService;


    @RequestMapping(value = VIEW, method = RequestMethod.GET)
    public ModelAndView render() {
        ModelAndView modelAndView = new ModelAndView(VIEW, "bulkMessage", new WebBulkMessage());
        locationSerializer.populateJavascriptMaps(modelAndView.getModelMap());
        return modelAndView;
    }

    public void setLocationSerializer(JSONLocationSerializer locationSerializer) {
        this.locationSerializer = locationSerializer;
    }

    @RequestMapping(value = VIEW, method = RequestMethod.POST)
    public ModelAndView send(@ModelAttribute WebBulkMessage bulkMessage) {
        String recipients = bulkMessage.getRecipients();
        String[] individualRecipients = recipients.split(",");
        MessageStatus statusOfAllBatches = MessageStatus.REJECTED;
        int i = 0;

        for (i = 0; i < (individualRecipients.length) / 10; i++) {
            String[] tenRecipients = Arrays.copyOfRange(individualRecipients, i  * 10 , (i  * 10) + 10);
            statusOfAllBatches = sendMessageToAllRecipients(bulkMessage, tenRecipients);

        }

        int leftOutRecipientsStartingIndex = i * 10;
        String[] remainingRecipients = Arrays.copyOfRange(individualRecipients, leftOutRecipientsStartingIndex, individualRecipients.length);
        statusOfAllBatches = sendMessageToAllRecipients(bulkMessage, remainingRecipients);


        ModelAndView modelAndView = new ModelAndView(VIEW);
        bulkMessage.setRecipients(recipients);
        WebResponse response = createResponse(statusOfAllBatches, bulkMessage);
        modelAndView.addObject("response", response);
        bulkMessage.setRecipients("");
        modelAndView.addObject("bulkMessage", bulkMessage);
        locationSerializer.populateJavascriptMaps(modelAndView.getModelMap());

        return modelAndView;
    }

    private MessageStatus sendMessageToAllRecipients(WebBulkMessage bulkMessage, String[] tenRecipients) {
        String recipientsTogether = join(tenRecipients, ",");
        bulkMessage.setRecipients(recipientsTogether);
        MessageStatus result =  messageService.sendMessage(bulkMessage.content(new SpaceEncoder()), bulkMessage.recipients());
        log.info("Message to " + recipientsTogether  + " " + result.name());
        return result;
    }

    private WebResponse createResponse(MessageStatus status, WebBulkMessage message) {

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
