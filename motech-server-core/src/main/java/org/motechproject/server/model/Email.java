package org.motechproject.server.model;

public class Email {

    private String to;
    private String from;
    private String subject;
    private String text;
    private static final String DELIMITER = ":";

    public Email(String to, String from, String subject, String text) {
        this.to = to;
        this.from = from ;
        this.subject = subject;
        this.text = text;
    }

    public String to() {
        return to;
    }

    public String from() {
        return from;
    }

    public String subject() {
        return subject;
    }

    public String text() {
        return text;
    }

    @Override
    public String toString() {
       return to + DELIMITER + from + DELIMITER + subject + DELIMITER +text;
    }
}
