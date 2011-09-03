package org.motechproject.server.filters.condition;

import org.motechproject.server.model.ExpectedObs;
import org.motechproject.server.util.DateUtil;

import java.util.Date;

public class TT1Condition implements Condition<ExpectedObs> {

    public static final String TT_1 = "TT1";

    public Boolean metBy(ExpectedObs expectedObs) {
        DateUtil dateUtil = new DateUtil();
        return expectedObs.getName().equalsIgnoreCase(TT_1) &&
                (expectedObs.getLateObsDatetime() == null || !dateUtil.isSameYear(new Date(), expectedObs.getLateObsDatetime()));
    }
}