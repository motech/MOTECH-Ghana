package org.motechproject.server.svc;

import org.motechproject.server.model.IncomingMessage;
import org.motechproject.ws.Response;

public interface WebClient {
    Response get(String fromUrl);
}
