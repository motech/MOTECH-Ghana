package org.motechproject.server.omod.web.model;

import org.motechproject.server.omod.web.encoder.SpaceEncoder;

public class WebBulkMessage {

    private String content;
    private String recipients;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRecipients() {
        return recipients;
    }

    public void setRecipients(String recipients) {
        this.recipients = recipients;
    }

    public String content() {
        return content;
    }

    public String recipients() {
        return recipients;
    }

    public String content(SpaceEncoder spaceEncoder) {
        return spaceEncoder.encode(content);
    }
}
