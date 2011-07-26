package org.motechproject.server.svc;

import org.motechproject.server.model.IncomingMessage;
import org.motechproject.server.model.db.MessageProcessorDAO;
import org.motechproject.ws.Response;

public interface IncomingMessageProcessor {
    Response process(IncomingMessage incomingMessage);

    void setDao(MessageProcessorDAO dao);

    void setSupportCaseService(SupportCaseService supportCaseService);

    void setWebClient(WebClient webClient);
}
