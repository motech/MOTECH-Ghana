package org.motechproject.server.model;

import org.apache.commons.lang.StringUtils;
import org.motechproject.server.strategy.MessageContentExtractionStrategy;
import org.motechproject.ws.RequestParameterBuilder;
import org.motechproject.ws.SMS;

import java.io.UnsupportedEncodingException;

public class IncomingMessage {

    private Long id;
    private String text;
    private String number;
    private String key;
    private String code;
    private String time;
    private static final String AMP = "&";

    public IncomingMessage() {
    }

    public IncomingMessage(SMS sms) {
        text = sms.getText();
        number = sms.getNumber();
        key = sms.getKey();
        code = sms.getCode();
        time = sms.getTime();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Boolean hasSufficientInformation() {
        return isNotBlank(key) && isNotBlank(text) && isNotBlank(code);
    }

    private Boolean isNotBlank(String value) {
        return StringUtils.isNotBlank(value);
    }

    public String extractDateWith(MessageContentExtractionStrategy strategy) {
        return strategy.extractDateFrom(time);
    }

    public String extractPhoneNumberWith(MessageContentExtractionStrategy strategy) {
        return strategy.extractPhoneNumberFrom(number);
    }

    public String extractSenderWith(MessageContentExtractionStrategy strategy) {
        return strategy.extractSenderFrom(text);
    }

    public String extractDescriptionWith(MessageContentExtractionStrategy strategy) {
        return strategy.extractDescriptionFrom(text);
    }

    public String requestParameters() throws UnsupportedEncodingException {
        RequestParameterBuilder builder = new RequestParameterBuilder("?", "UTF-8");
        builder.append("text", text);
        builder.append("number", number);
        builder.append("key", key);
        builder.append("time", time);
        builder.append("code", code);
        return builder.toString();
    }

    @Override
    public String toString() {
        return "Issue { " + text + " } sent from " + number;
    }

    public Boolean isFor(String key) {
        return key.equalsIgnoreCase(this.key);
    }
}
