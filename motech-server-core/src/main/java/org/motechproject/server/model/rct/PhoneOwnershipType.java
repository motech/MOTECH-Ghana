package org.motechproject.server.model.rct;

import org.motechproject.ws.ContactNumberType;

public enum PhoneOwnershipType {
    PERSONAL,OTHER;

    public static PhoneOwnershipType mapTo(ContactNumberType contactNumberType) {
        if(contactNumberType.equals(ContactNumberType.PERSONAL))return PERSONAL;
        return OTHER;
    }
}
