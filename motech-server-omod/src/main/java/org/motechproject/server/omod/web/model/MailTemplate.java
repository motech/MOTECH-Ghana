package org.motechproject.server.omod.web.model;

import java.util.Map;

public interface MailTemplate {
    public String subject(Map data);

    public String text(Map data);
}
