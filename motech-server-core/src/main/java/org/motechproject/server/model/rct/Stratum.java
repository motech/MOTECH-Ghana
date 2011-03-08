package org.motechproject.server.model.rct;

import org.motechproject.server.exception.RCTControlGroupNotFoundException;
import org.motechproject.server.exception.RCTRegistrationException;
import org.motechproject.ws.rct.ControlGroup;
import org.motechproject.ws.rct.PregnancyTrimester;

import java.util.Set;

public class Stratum {

    private Integer id;
    private RCTFacility facility;
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

    public ControlGroup groupAssigned() throws RCTRegistrationException {
        for (ControlGroupAssignment controlGroupAssignment : assignments) {
            if (controlGroupAssignment.hasAssignmentNumber(nextAssignment)) {
                return controlGroupAssignment.group();
            }
        }
        throw new RCTControlGroupNotFoundException("rct.no.control.group");
    }

    public void determineNextAssignment() {
        nextAssignment = (nextAssignment.equals(size)) ? 1 : (nextAssignment + 1);
    }

    public RCTFacility getRctFacility(){
        return facility;
    }
}
