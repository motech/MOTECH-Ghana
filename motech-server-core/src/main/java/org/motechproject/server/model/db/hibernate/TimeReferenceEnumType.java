package org.motechproject.server.model.db.hibernate;

import org.motechproject.server.time.TimeReference;

public class TimeReferenceEnumType extends EnumUserType<TimeReference>{
    protected TimeReferenceEnumType() {
        super(TimeReference.class);
    }
}
