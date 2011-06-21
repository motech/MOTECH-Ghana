package org.motechproject.server.omod.web.model;

import org.junit.Test;
import org.motechproject.server.omod.web.encoder.SpaceEncoder;

import static org.junit.Assert.assertEquals;

public class WebBulkMessageTest {

    @Test
    public void shouldEncodeSpaceInContent() {
        WebBulkMessage message = new WebBulkMessage();
        message.setContent("Hello World");
        assertEquals("Hello%2BWorld",message.content(new SpaceEncoder()));
        message.setContent("Hi Again World");
        assertEquals("Hi%2BAgain%2BWorld",message.content(new SpaceEncoder()));
        message.setContent("Hello  World");
        assertEquals("Hello%2B%2BWorld",message.content(new SpaceEncoder()));
    }
}
