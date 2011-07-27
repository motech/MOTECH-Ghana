package org.motechproject.server.omod.web.controller;

import org.easymock.Capture;
import org.junit.Test;
import org.motechproject.server.model.IncomingMessage;
import org.motechproject.server.svc.SupportCaseService;
import org.motechproject.ws.Response;
import org.motechproject.ws.SMS;
import org.springframework.web.servlet.ModelAndView;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

public class SupportCaseControllerTest {

    @Test
    public void shouldInvokeSupportCaseService() {
        SupportCaseService supportCaseService = createMock(SupportCaseService.class);
        SupportCaseController controller = new SupportCaseController();
        controller.setSupportCaseService(supportCaseService);

        SMS sms = new SMS();
        sms.setText("Hi");
        sms.setNumber("+233123456789");
        sms.setKey("SUPPORT");
        sms.setTime("2011-03-03 10:10:10");
        sms.setCode("1982");

        Capture<IncomingMessage> messageCapture = new Capture<IncomingMessage>();

        expect(supportCaseService.mailToSupport(capture(messageCapture))).andReturn(new Response("Tested.OK"));

        replay(supportCaseService);

        ModelAndView modelAndView = controller.raiseSupportCase(sms);

        verify(supportCaseService);

        assertEquals("/module/motechmodule/support_response", modelAndView.getViewName());
        assertEquals("Tested.OK",modelAndView.getModelMap().get("response"));

        IncomingMessage message = messageCapture.getValue();
        assertEquals("Hi",message.getText());
        assertEquals("+233123456789",message.getNumber());
        assertEquals("SUPPORT",message.getKey());
        assertEquals("2011-03-03 10:10:10",message.getTime());
        assertEquals("1982",message.getCode());
    }
}
