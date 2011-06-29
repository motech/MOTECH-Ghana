package org.motechproject.server.model;

import org.apache.commons.lang.StringUtils;

import static org.apache.commons.lang.StringUtils.isBlank;

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

    public String getUrl() {
        return url;
    }

    public String getKey() {
        return key;
    }

    public Integer getId() {
        return id;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int hashCode() {
        return key != null ? key.hashCode() : 0;
    }

    @Override
    public String toString() {
        return url;
    }

    public Boolean isEmpty() {
        return id == null  && isBlank(key) && isBlank(url);
    }

    @Override
    public boolean equals(Object other) {
        MessageProcessorURL otherURL = (MessageProcessorURL) other;
        return key.equals(otherURL.key) && url.equals(otherURL.url);
    }

    public void updateWith(MessageProcessorURL newURL) {
        if(StringUtils.isNotBlank(newURL.url)){
           this.url = newURL.url;
        }
    }
}
