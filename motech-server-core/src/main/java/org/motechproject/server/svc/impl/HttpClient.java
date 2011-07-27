package org.motechproject.server.svc.impl;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.motechproject.server.model.WebResponse;
import org.motechproject.server.svc.WebClient;
import org.motechproject.server.util.MailingConstants;
import org.motechproject.ws.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class HttpClient implements WebClient {

    public Response get(String fromUrl) {
        DefaultHttpClient client = new DefaultHttpClient();
        try {
            HttpResponse httpResponse = client.execute(new HttpGet(fromUrl));
            return new WebResponse(httpResponse).read();
        } catch (IOException e) {
            return new Response(MailingConstants.MESSAGE_PROCESSING_FAILED);
        }
    }

}
