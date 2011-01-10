package org.motechproject.server.omod;

import org.openmrs.User;

public interface AuthenticationService {


    public User getAuthenticatedUser();
}
