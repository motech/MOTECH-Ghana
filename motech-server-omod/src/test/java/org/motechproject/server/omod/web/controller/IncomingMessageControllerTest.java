package org.motechproject.server.omod.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.server.model.IncomingMessage;
import org.motechproject.server.model.MessageProcessorURL;
import org.motechproject.server.model.db.MessageProcessorDAO;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.UnsupportedEncodingException;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

public class IncomingMessageControllerTest extends BaseModuleContextSensitiveTest{


    @Autowired
    private IncomingMessageController controller;


    @Before
    @SkipBaseSetup
    public void setUp() throws Exception {
       executeDataSet("incoming-message-data.xml");
    }


    @Test
    public void shouldRedirectAccordingToKeyword() throws UnsupportedEncodingException {

        MessageProcessorDAO dao = createMock(MessageProcessorDAO.class);
        controller.setDao(dao);

        IncomingMessage message = new IncomingMessage();
        message.setText("Hi");
        message.setNumber("+233123456789");
        message.setKey("SUPPORT");
        message.setTime("2011-03-03 10:10:10");
        message.setCode("1982");

        dao.save(message);
        expectLastCall();

        expect(dao.urlFor("SUPPORT"))
                .andReturn(new MessageProcessorURL("SUPPORT","/module/motechmodule/supportcase.form"));

        replay(dao);

        String redirectedUrl = controller.redirect(message);

        verify(dao);

        assertEquals("redirect:/module/motechmodule/supportcase.form?text=Hi&number=%2B233123456789&key=SUPPORT&time=2011-03-03+10%3A10%3A10&code=1982",redirectedUrl);
    }

}
