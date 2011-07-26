package org.motechproject.server.ws;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.server.model.IncomingMessage;
import org.motechproject.server.model.MessageProcessorURL;
import org.motechproject.server.model.db.MessageProcessorDAO;
import org.motechproject.server.svc.IncomingMessageProcessor;
import org.motechproject.server.svc.SupportCaseService;
import org.motechproject.server.svc.WebClient;
import org.motechproject.server.svc.impl.IncomingMessageProcessorImpl;
import org.motechproject.ws.Response;

import java.io.UnsupportedEncodingException;

import static org.easymock.EasyMock.*;

public class IncomingMessageProcessorTest {


    private IncomingMessageProcessor processor;

    @Before
    public void setUp(){
       processor = new IncomingMessageProcessorImpl();
    }


    @Test
    public void shouldRaiseSupportCaseIfKeyIsSupport() throws UnsupportedEncodingException {

        MessageProcessorDAO dao = createMock(MessageProcessorDAO.class);
        SupportCaseService supportCaseService = createMock(SupportCaseService.class);

        processor.setDao(dao);
        processor.setSupportCaseService(supportCaseService);

        IncomingMessage message = new IncomingMessage();
        message.setText("Hi");
        message.setNumber("+233123456789");
        message.setKey("SUPPORT");
        message.setTime("2011-03-03 10:10:10");
        message.setCode("1982");

        dao.save(message);
        expectLastCall();

        expect(supportCaseService.mailToSupport(eq(message))).andReturn(new Response("Success"));

        replay(dao,supportCaseService);

        Response response = processor.process(message);

        verify(dao,supportCaseService);

    }

    @Test
    public void shouldSendDataToMappedUrlIfKeyWordIsNotSupport() throws UnsupportedEncodingException {

        MessageProcessorDAO dao = createMock(MessageProcessorDAO.class);
        WebClient webClient = createMock(WebClient.class);
        processor.setDao(dao);
        processor.setWebClient(webClient);

        MessageProcessorURL url = new MessageProcessorURL("TEST", "http://test.org");

        IncomingMessage message = new IncomingMessage();
        message.setKey("TEST");
        message.setText("Hi");

        dao.save(message);
        expectLastCall();

        expect(dao.urlFor("TEST")).andReturn(url);

        expect(webClient.sendDataTo(url)).andReturn(new Response());

        replay(dao,webClient);

        Response response = processor.process(message);

        verify(dao,webClient);

    }

    
}
