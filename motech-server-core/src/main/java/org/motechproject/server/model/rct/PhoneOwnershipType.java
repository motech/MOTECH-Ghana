package org.motechproject.server.model.rct;

import org.motechproject.ws.ContactNumberType;

public enum PhoneOwnershipType {
    PERSONAL,OTHER;

    public static PhoneOwnershipType mapTo(ContactNumberType contactNumberType) {
        if(ContactNumberType.PERSONAL.equals(contactNumberType))return PERSONAL;
        return OTHER;
    }
}
