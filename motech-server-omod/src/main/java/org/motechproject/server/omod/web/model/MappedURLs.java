package org.motechproject.server.omod.web.model;

import org.motechproject.server.model.MessageProcessorURL;

import java.util.ArrayList;
import java.util.List;

public class MappedURLs {

    private List<MessageProcessorURL> urls = new ArrayList<MessageProcessorURL>();

    public MappedURLs(List<MessageProcessorURL> messageProcessorURLs) {
        urls.addAll(messageProcessorURLs);
    }

    public Boolean has(MessageProcessorURL messageProcessorURL) {
        return urls.contains(messageProcessorURL);
    }

    public List<MessageProcessorURL> getUrls() {
        return urls;
    }
}
