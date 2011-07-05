package org.motechproject.server.model;

import org.openmrs.Person;
import org.openmrs.User;

public class MotechUser extends User {

    private MotechUserType motechUserType;

    public MotechUser() {
    }

    public MotechUser(Integer userId) {
        super(userId);
    }

    public MotechUser(Person person) {
        super(person);
    }

    public MotechUserType getMotechUserType() {
        return motechUserType;
    }

    public void setMotechUserType(MotechUserType motechUserType) {
        this.motechUserType = motechUserType;
    }
}
