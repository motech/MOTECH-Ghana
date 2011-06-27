package org.motechproject.server.svc;

import org.motechproject.server.model.Email;

public interface MailingService {

    public void send(Email mail);

}
