package org.motechproject.server.svc;

import org.motechproject.server.model.IncomingMessage;
import org.motechproject.server.model.db.MessageProcessorDAO;
import org.motechproject.ws.Response;

import java.io.UnsupportedEncodingException;

public interface IncomingMessageProcessor {
    Response process(IncomingMessage incomingMessage) throws UnsupportedEncodingException;

    void setDao(MessageProcessorDAO dao);

    void setWebClient(WebClient webClient);
}
