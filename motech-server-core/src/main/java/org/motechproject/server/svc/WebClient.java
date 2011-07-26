package org.motechproject.server.svc;

import org.motechproject.server.model.MessageProcessorURL;
import org.motechproject.ws.Response;

public interface WebClient {
    Response sendDataTo(MessageProcessorURL url);
}
