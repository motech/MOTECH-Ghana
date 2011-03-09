package org.motechproject.server.exception;

import org.motechproject.server.util.RCTError;

public class RCTControlGroupNotFoundException extends RCTRegistrationException {
    public RCTControlGroupNotFoundException() {
        super(RCTError.CONTROL_GROUP_NOT_FOUND);
    }
}
