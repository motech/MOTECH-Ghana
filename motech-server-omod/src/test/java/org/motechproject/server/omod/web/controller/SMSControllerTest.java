package org.motechproject.server.omod.web.controller;

import org.junit.Test;
import org.motechproject.server.omod.web.model.WebBulkMessage;
import org.motechproject.server.omod.web.model.WebResponse;
import org.motechproject.ws.MessageStatus;
import org.motechproject.ws.mobile.MessageService;
import org.springframework.web.servlet.ModelAndView;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class SMSControllerTest {

    @Test
    public void shouldRenderTheCorrectPage() {
        SMSController controller = new SMSController();
        ModelAndView modelAndView = controller.render();
        assertNotNull(modelAndView);
        assertEquals("/module/motechmodule/sms", modelAndView.getViewName());
        WebBulkMessage message = (WebBulkMessage) modelAndView.getModelMap().get("bulkMessage");
        assertNotNull(message);
    }

    @Test
    public void shouldSendSMS() {
        MessageService messageService = createMock(MessageService.class);
        SMSController controller = new SMSController();
        WebBulkMessage message = new WebBulkMessage();
        message.setRecipients("0123456788,0123456789");
        message.setContent("Hello");
        controller.setMessageService(messageService);
        expect(messageService.sendMessage("Hello", "0123456788,0123456789")).andReturn(MessageStatus.DELIVERED);

        replay(messageService);

        ModelAndView modelAndView = controller.send(message);

        verify(messageService);

        assertEquals("/module/motechmodule/sms", modelAndView.getViewName());
        WebResponse response = (WebResponse) modelAndView.getModelMap().get("response");
        assertTrue(response.getSuccess());
        assertEquals("Message delivered successfully to 0123456788,0123456789",response.getText());
    }
}
