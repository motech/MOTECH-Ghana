package org.motechproject.server.ws;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.server.model.IncomingMessage;
import org.motechproject.server.model.MessageProcessorURL;
import org.motechproject.server.model.db.MessageProcessorDAO;
import org.motechproject.server.svc.IncomingMessageProcessor;
import org.motechproject.server.svc.WebClient;
import org.motechproject.server.svc.impl.IncomingMessageProcessorImpl;
import org.motechproject.server.util.MailingConstants;
import org.motechproject.ws.Response;

import java.io.UnsupportedEncodingException;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

public class IncomingMessageProcessorTest {


    private IncomingMessageProcessor processor;

    @Before
    public void setUp(){
       processor = new IncomingMessageProcessorImpl();
    }


    @Test
    public void shouldSendDataToMappedUrl() throws UnsupportedEncodingException {

        MessageProcessorDAO dao = createMock(MessageProcessorDAO.class);
        WebClient webClient = createMock(WebClient.class);
        processor.setDao(dao);
        processor.setWebClient(webClient);

        MessageProcessorURL url = new MessageProcessorURL("SUPPORT", "http://test.org");

        IncomingMessage message = new IncomingMessage();
        message.setKey("SUPPORT");
        message.setText("Hi hello");

        dao.save(message);
        expectLastCall();

        expect(dao.urlFor("SUPPORT")).andReturn(url);

        expect(webClient.get(eq("http://test.org?text=Hi+hello&key=SUPPORT"))).andReturn(new Response("something"));

        replay(dao,webClient);

        Response response = processor.process(message);

        verify(dao,webClient);

    }

    @Test
        public void shouldSendErrorMessageIfKeyWordNotFound() throws UnsupportedEncodingException {

            MessageProcessorDAO dao = createMock(MessageProcessorDAO.class);
            WebClient webClient = createMock(WebClient.class);
            processor.setDao(dao);
            processor.setWebClient(webClient);

            IncomingMessage message = new IncomingMessage();
            message.setKey("TEST");
            message.setText("Hi");

            dao.save(message);
            expectLastCall();

            expect(dao.urlFor("TEST")).andReturn(null);


            replay(dao);

            Response response = processor.process(message);

            verify(dao);

            assertEquals(MailingConstants.KEY_NOT_SUPPORTED,response.getContent());

        }

    
}
