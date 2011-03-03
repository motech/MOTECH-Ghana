package org.motechproject.server.model.db.hibernate.rct;

import org.motechproject.server.model.db.hibernate.EnumUserType;
import org.motechproject.ws.rct.PregnancyTrimester;

public class PregnancyTrimesterEnumType extends EnumUserType<PregnancyTrimester>{

    public PregnancyTrimesterEnumType(){
        super(PregnancyTrimester.class);
    }
}
