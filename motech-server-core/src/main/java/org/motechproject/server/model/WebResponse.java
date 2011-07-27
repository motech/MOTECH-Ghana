package org.motechproject.server.model;

import org.apache.http.HttpResponse;
import org.motechproject.ws.Response;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class WebResponse {
    private HttpResponse httpResponse;

    public WebResponse(HttpResponse httpresponse){
       this.httpResponse = httpresponse ;
    }

    public Response read() throws IOException {
        InputStream content = httpResponse.getEntity().getContent();
        BufferedInputStream bufferedInputStream = new BufferedInputStream(content);
        byte[] data = new byte[1024];
        int bytesRead;
        StringBuilder responseBuilder = new StringBuilder();
        while ((bytesRead = bufferedInputStream.read(data)) != -1){
            responseBuilder.append(new String(data,0,bytesRead));
        }
        Response response = new Response(responseBuilder.toString());
        if(statusOK()){
            response.markSuccess();
        }
        return response;
    }

    public boolean statusOK() {
        return httpResponse.getStatusLine().getStatusCode() == 200;
    }
}
