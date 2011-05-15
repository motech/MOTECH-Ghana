package org.motechproject.server.model.db.hibernate;

import org.motechproject.server.time.TimePeriod;

public class TimePeriodEnumType extends EnumUserType<TimePeriod> {

    public TimePeriodEnumType() {
        super(TimePeriod.class);
    }
}
