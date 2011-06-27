package org.motechproject.server.strategy;

import org.motechproject.server.model.ExtractedMessage;
import org.motechproject.server.model.IncomingMessage;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Date;

public interface  MessageContentExtractionStrategy {
    public ExtractedMessage extractFrom(IncomingMessage message);

    String extractDateFrom(String time) throws UnsupportedEncodingException, ParseException;

    String extractPhoneNumberFrom(String phoneNumber) throws UnsupportedEncodingException;

    String extractSenderFrom(String text) throws UnsupportedEncodingException;

    String extractDescriptionFrom(String text) throws UnsupportedEncodingException;
}
