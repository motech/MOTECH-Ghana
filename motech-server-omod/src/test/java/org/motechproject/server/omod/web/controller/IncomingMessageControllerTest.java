package org.motechproject.server.omod.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.server.model.IncomingMessage;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.UnsupportedEncodingException;

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
        IncomingMessage message = new IncomingMessage();
        message.setKey("SUPPORT");
        message.setCode("1982");
        message.setNumber("%2B233123456789");
        message.setText("Hi");
        message.setTime("2011-03-03+10:10:10");
        String redirectedUrl = controller.redirect(message);
        assertEquals("redirect:/module/motechmodule/supportcase.form?text=Hi&key=SUPPORT&code=1982&number=%2B233123456789&time=2011-03-03+10:10:10",redirectedUrl);
    }

}
