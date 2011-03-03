package org.motechproject.server.model.rct;

import org.motechproject.ws.rct.ControlGroup;

public class ControlGroupAssignment {
   private Integer id ;
   private Integer assignmentNumber ;
   private ControlGroup controlGroup;

    public boolean hasAssignmentNumber(Integer nextAssignment) {
        return assignmentNumber.equals(nextAssignment) ;
    }

    public ControlGroup group() {
        return controlGroup;
    }
}
