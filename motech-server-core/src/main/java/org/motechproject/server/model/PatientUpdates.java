package org.motechproject.server.model;

import org.motechproject.ws.ContactNumberType;

public class PatientUpdates {
    private String phoneNumber;
    private ContactNumberType phoneOwnership;

    public PatientUpdates(String phoneNumber, ContactNumberType phoneOwnership) {
        this.phoneNumber = phoneNumber;
        this.phoneOwnership = phoneOwnership;
    }

    public ContactNumberType contactNumberType() {
        return phoneOwnership;
    }

    public String phoneNumber() {
        return phoneNumber;
    }
}
