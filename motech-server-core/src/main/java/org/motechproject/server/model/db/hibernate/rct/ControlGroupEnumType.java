package org.motechproject.server.model.db.hibernate.rct;

import org.motechproject.server.model.db.hibernate.EnumUserType;
import org.motechproject.ws.rct.ControlGroup;

public class ControlGroupEnumType extends EnumUserType<ControlGroup> {

    public ControlGroupEnumType(){
        super(ControlGroup.class);
    }
}
