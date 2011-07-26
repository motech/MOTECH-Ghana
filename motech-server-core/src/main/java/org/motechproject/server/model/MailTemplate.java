package org.motechproject.server.model;

import java.util.Map;

public interface MailTemplate {
    public String subject(Map data);

    public String text(Map data);
}
