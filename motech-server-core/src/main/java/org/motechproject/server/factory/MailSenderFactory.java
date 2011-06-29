package org.motechproject.server.factory;

import org.motechproject.server.annotation.RunWithPrivileges;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.mail.javamail.JavaMailSender;

public interface MailSenderFactory {

    @RunWithPrivileges({OpenmrsConstants.PRIV_VIEW_GLOBAL_PROPERTIES})
    public JavaMailSender mailSender();
}
