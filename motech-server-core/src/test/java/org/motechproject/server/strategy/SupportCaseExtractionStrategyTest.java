package org.motechproject.server.strategy;

import org.junit.Test;
import org.motechproject.server.exception.MessageContentExtractionException;
import org.motechproject.server.model.IncomingMessage;
import org.motechproject.server.model.SupportCase;

import java.text.ParseException;

import static org.junit.Assert.assertEquals;

public class SupportCaseExtractionStrategyTest {

    @Test
    public void shouldExtractMessageContent() throws ParseException, MessageContentExtractionException {
        SupportCaseExtractionStrategy strategy = new SupportCaseExtractionStrategy();
        IncomingMessage message = new IncomingMessage();
        message.setKey(" SUPPORT");
        message.setTime("2011-06-25 09:30:29 ");
        message.setText("SUPPORT 123 Cannot Upload  Forms ");
        message.setNumber("+233123456789 ");
        SupportCase supportCase = strategy.extractFrom(message);
        assertEquals("123",supportCase.getRaisedBy());
        assertEquals("+233123456789",supportCase.getPhoneNumber());
        assertEquals("2011-06-25 09:30:29",supportCase.getDateRaisedOn());
        assertEquals("Cannot Upload Forms",supportCase.getDescription());
    }

}
