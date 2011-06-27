package org.motechproject.server.strategy;

import org.apache.commons.lang.StringUtils;
import org.motechproject.server.model.IncomingMessage;
import org.motechproject.server.model.SupportCase;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SupportCaseExtractionStrategy implements MessageContentExtractionStrategy {

    private final Pattern SENDER_PATTERN = Pattern.compile("\\s+\\d+\\s+");
    private static final String UTF_8 = "UTF-8";
    private static final String SINGLE_SPACE = " ";
    private static final String MULTIPLE_SPACES = "\\s+";

    public SupportCase extractFrom(IncomingMessage message) {
        try {
            String dateRaisedOn = message.extractDateWith(this);
            String phoneNumber = message.extractPhoneNumberWith(this);
            String sender = message.extractSenderWith(this);
            String description = message.extractDescriptionWith(this);
            return new SupportCase(sender,phoneNumber,dateRaisedOn,description);
        } catch (Exception ex){
          return new SupportCase();
        }
    }

    public String extractDateFrom(String time) throws UnsupportedEncodingException, ParseException {
        return decode(time);
    }

    public String extractPhoneNumberFrom(String phoneNumber) throws UnsupportedEncodingException {
        return decode(phoneNumber);
    }

    public String extractSenderFrom(String text) throws UnsupportedEncodingException {
        Matcher matcher = SENDER_PATTERN.matcher(decode(text));
        if(matcher.find()){
            return matcher.group().trim();
        }
        return StringUtils.EMPTY;
    }

    public String extractDescriptionFrom(String text) throws UnsupportedEncodingException {
        text = decode(text);
        String sender = extractSenderFrom(text);
        if(StringUtils.isNotBlank(sender)){
            int begin = text.indexOf(sender) + sender.length();
            return text.substring(begin).trim();
        }
        return StringUtils.EMPTY;
    }

    private String decode(String value) throws UnsupportedEncodingException {
        return URLDecoder.decode(value, UTF_8).replaceAll(MULTIPLE_SPACES, SINGLE_SPACE);
    }
}
