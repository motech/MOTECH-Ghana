package org.motechproject.server.model.rct;

import org.motechproject.server.model.Facility;
import org.motechproject.ws.rct.ControlGroup;
import org.motechproject.ws.rct.PregnancyTrimester;

import java.util.Set;

public class Stratum {

    private Integer id;
    private Facility facility;
    private PregnancyTrimester pregnancyTrimester;
    private PhoneOwnershipType phoneOwnership;
    private Integer size;
    private Set<ControlGroupAssignment> assignments;
    private Integer nextAssignment;
    private Boolean isActive;

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public ControlGroup groupAssigned() {
        for (ControlGroupAssignment controlGroupAssignment : assignments) {
            if (controlGroupAssignment.hasAssignmentNumber(nextAssignment)) {
                return controlGroupAssignment.group();
            }
        }
        //TODO : Create a new runtime exception to handle RCT conditions
        throw new RuntimeException("No group assignment found");
    }

    public void determineNextAssignment() {
        nextAssignment = (nextAssignment.equals(size)) ? 1 : (nextAssignment + 1);
    }
}
