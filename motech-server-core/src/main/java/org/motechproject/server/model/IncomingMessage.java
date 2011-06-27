package org.motechproject.server.model;

import org.apache.commons.lang.StringUtils;
import org.motechproject.server.strategy.MessageContentExtractionStrategy;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;

public class IncomingMessage {
    private String text;
    private String number;
    private String key;
    private String code;
    private String time;
    private static final String AMP = "&";

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

    public Boolean hasInformation() {
        return isNotBlank(key) && isNotBlank(text) && isNotBlank(code);
    }

    private Boolean isNotBlank(String value) {
        return StringUtils.isNotBlank(value);
    }

    public String extractDateWith(MessageContentExtractionStrategy strategy) throws ParseException, UnsupportedEncodingException {
        return strategy.extractDateFrom(time);
    }

    public String extractPhoneNumberWith(MessageContentExtractionStrategy strategy) throws UnsupportedEncodingException {
        return strategy.extractPhoneNumberFrom(number);
    }

    public String extractSenderWith(MessageContentExtractionStrategy strategy) throws UnsupportedEncodingException {
        return strategy.extractSenderFrom(text);
    }

    public String extractDescriptionWith(MessageContentExtractionStrategy strategy) throws UnsupportedEncodingException {
        return strategy.extractDescriptionFrom(text);
    }

    public String requestParameters() throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder("?");
        builder.append("text=").append(text).append(AMP);
        builder.append("key=").append(key).append(AMP);
        builder.append("code=").append(code).append(AMP);
        builder.append("number=").append(number).append(AMP);
        builder.append("time=").append(time);
        return builder.toString();
    }

    @Override
    public String toString() {
        return "Issue { " + text + " } sent from " + number;
    }
}
