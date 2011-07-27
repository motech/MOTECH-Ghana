package org.motechproject.server.svc.impl;

import org.junit.Ignore;
import org.junit.Test;
import org.motechproject.ws.Response;

import static org.junit.Assert.assertEquals;

public class HttpClientTest {

    @Ignore("Should be an integration test")
    @Test
    public void shouldMakeAHttpGetRequest() {
        HttpClient client = new HttpClient();
        Response response = client.get("http://localhost:8080/motech-mobile-webapp/incomingmessage?" +
                "text=support+465+Connection+Broken&number=%2B233123456789" +
                "&key=support&code=1982&time=2011-01-01+10:10:10");
        assertEquals("Welcome to MOTECH. Your message has been submitted to the support team"
                ,response.getContent());
    }
}
