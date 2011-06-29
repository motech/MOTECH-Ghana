package org.motechproject.server.omod.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.server.model.MessageLanguage;
import org.motechproject.server.model.ghana.Facility;
import org.motechproject.server.omod.web.model.JSONLocationSerializer;
import org.motechproject.server.omod.web.model.WebBulkMessage;
import org.motechproject.server.omod.web.model.WebResponse;
import org.motechproject.server.service.ContextService;
import org.motechproject.server.service.MotechService;
import org.motechproject.ws.MessageStatus;
import org.motechproject.ws.mobile.MessageService;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class SMSControllerTest {

    private SMSController controller;
    private MessageService messageService;
    private JSONLocationSerializer locationSerializer;
    private ContextService contextService;
    private MotechService motechService;

    @Before
    public void setUp() {
        messageService = createMock(MessageService.class);

        locationSerializer = new JSONLocationSerializer();
        contextService = createMock(ContextService.class);
        locationSerializer.setContextService(contextService);
        motechService = createMock(MotechService.class);

        controller = new SMSController();
        controller.setMessageService(messageService);
        controller.setLocationSerializer(locationSerializer);
    }

    @Test
    public void shouldRenderTheCorrectPage() {

        expect(contextService.getMotechService()).andReturn(motechService);
        expect(motechService.getAllFacilities()).andReturn(Collections.<Facility>emptyList());
        expect(motechService.getAllLanguages()).andReturn(Collections.<MessageLanguage>emptyList());

        replay(contextService, motechService);

        ModelAndView modelAndView = controller.render();
        verify(contextService, motechService);
        assertNotNull(modelAndView);
        assertEquals("/module/motechmodule/sms", modelAndView.getViewName());

        WebBulkMessage message = (WebBulkMessage) modelAndView.getModelMap().get("bulkMessage");
        assertNotNull(message);

        assertNotNull(modelAndView.getModel().get("regionMap"));
        assertNotNull(modelAndView.getModel().get("districtMap"));
        assertNotNull(modelAndView.getModel().get("facilities"));
    }

    @Test
    public void shouldSendSMS() {

        expect(contextService.getMotechService()).andReturn(motechService);
        expect(motechService.getAllFacilities()).andReturn(Collections.<Facility>emptyList());
        expect(motechService.getAllLanguages()).andReturn(Collections.<MessageLanguage>emptyList());

        WebBulkMessage message = new WebBulkMessage();
        message.setRecipients("0123456788,0123456789");
        message.setContent("Hello World");
        expect(messageService.sendMessage("Hello%2BWorld", "0123456788,0123456789")).andReturn(MessageStatus.DELIVERED);

        replay(messageService, contextService, motechService);

        ModelAndView modelAndView = controller.send(message);

        verify(messageService, contextService, motechService);

        assertEquals("/module/motechmodule/sms", modelAndView.getViewName());
        WebResponse response = (WebResponse) modelAndView.getModelMap().get("response");
        assertTrue(response.getSuccess());
        assertEquals("Message delivered successfully to 0123456788,0123456789", response.getText());
    }

    @Test
    public void shouldSendSeparateRequestsForEveryTenSMS() {
        expect(contextService.getMotechService()).andReturn(motechService);
        expect(motechService.getAllFacilities()).andReturn(Collections.<Facility>emptyList());
        expect(motechService.getAllLanguages()).andReturn(Collections.<MessageLanguage>emptyList());

        WebBulkMessage message = new WebBulkMessage();
        message.setRecipients("0123456788,0123456789,0123456788,0123456789,0123456788,0123456789,0123456788,0123456789,0123456788,0123456789,0123456789");
        message.setContent("Hello World");
        expect(messageService.sendMessage("Hello%2BWorld", "0123456788,0123456789,0123456788,0123456789,0123456788,0123456789,0123456788,0123456789,0123456788,0123456789")).andReturn(MessageStatus.DELIVERED);
        expect(messageService.sendMessage("Hello%2BWorld", "0123456789")).andReturn(MessageStatus.DELIVERED);

        replay(messageService, contextService, motechService);

        ModelAndView modelAndView = controller.send(message);

        verify(messageService, contextService, motechService);

        assertEquals("/module/motechmodule/sms", modelAndView.getViewName());
        WebResponse response = (WebResponse) modelAndView.getModelMap().get("response");
        assertTrue(response.getSuccess());
    }

    @Test
    public void ifOneBatchFailsResultIsFailed() {
       expect(contextService.getMotechService()).andReturn(motechService);
        expect(motechService.getAllFacilities()).andReturn(Collections.<Facility>emptyList());
        expect(motechService.getAllLanguages()).andReturn(Collections.<MessageLanguage>emptyList());

        WebBulkMessage message = new WebBulkMessage();
        message.setRecipients("0123456788,0123456789,0123456788,0123456789,0123456788,0123456789,0123456788,0123456789,0123456788,0123456789,0123456789");
        message.setContent("Hello World");
        expect(messageService.sendMessage("Hello%2BWorld", "0123456788,0123456789,0123456788,0123456789,0123456788,0123456789,0123456788,0123456789,0123456788,0123456789")).andReturn(MessageStatus.DELIVERED);
        expect(messageService.sendMessage("Hello%2BWorld", "0123456789")).andReturn(MessageStatus.REJECTED);

        replay(messageService, contextService, motechService);

        ModelAndView modelAndView = controller.send(message);

        verify(messageService, contextService, motechService);

        assertEquals("/module/motechmodule/sms", modelAndView.getViewName());
        WebResponse response = (WebResponse) modelAndView.getModelMap().get("response");
        assertNull(response.getSuccess());
    }
}
