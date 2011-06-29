package org.motechproject.server.strategy;

import org.apache.commons.lang.StringUtils;
import org.motechproject.server.model.IncomingMessage;
import org.motechproject.server.model.SupportCase;

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

    public String extractDateFrom(String time)  {
        return collapseSpaces(time);
    }

    public String extractPhoneNumberFrom(String phoneNumber) {
        return collapseSpaces(phoneNumber);
    }

    public String extractSenderFrom(String text){
        Matcher matcher = SENDER_PATTERN.matcher(collapseSpaces(text));
        if(matcher.find()){
            return matcher.group().trim();
        }
        return StringUtils.EMPTY;
    }

    public String extractDescriptionFrom(String text){
        text = collapseSpaces(text);
        String sender = extractSenderFrom(text);
        if(StringUtils.isNotBlank(sender)){
            int begin = text.indexOf(sender) + sender.length();
            return text.substring(begin).trim();
        }
        return StringUtils.EMPTY;
    }

    private String collapseSpaces(String value) {
        return value.replaceAll(MULTIPLE_SPACES, SINGLE_SPACE).trim();
    }
}
