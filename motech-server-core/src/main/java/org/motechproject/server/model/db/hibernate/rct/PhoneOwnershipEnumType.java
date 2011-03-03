package org.motechproject.server.model.db.hibernate.rct;

import org.motechproject.server.model.db.hibernate.EnumUserType;
import org.motechproject.server.model.rct.PhoneOwnershipType;

public class PhoneOwnershipEnumType extends EnumUserType<PhoneOwnershipType> {

    public PhoneOwnershipEnumType(){
        super(PhoneOwnershipType.class);
    }
}
