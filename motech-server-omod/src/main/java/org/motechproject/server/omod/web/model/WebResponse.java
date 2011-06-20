package org.motechproject.server.omod.web.model;

public class WebResponse {
    private String text;
    private Boolean success;

    public WebResponse(String message) {
        this.text = message;
    }

    public void markSuccess() {
        this.success = true;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
