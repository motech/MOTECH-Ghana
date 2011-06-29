package org.motechproject.server.model;

public class MessageProcessorURL {
    private Integer id;
    private String key;
    private String url;

    public MessageProcessorURL() {
    }

    public MessageProcessorURL(String key, String url) {
        this.key = key;
        this.url = url;
    }

    @Override
    public boolean equals(Object other) {
        MessageProcessorURL otherURL = (MessageProcessorURL) other;
        return key.equals(otherURL.key) && url.equals(otherURL.url);
    }

    public String getUrl() {
        return url;
    }

    public String getKey() {
        return key;
    }

    @Override
    public int hashCode() {
        return key != null ? key.hashCode() : 0;
    }

    @Override
    public String toString() {
        return url;
    }
}
