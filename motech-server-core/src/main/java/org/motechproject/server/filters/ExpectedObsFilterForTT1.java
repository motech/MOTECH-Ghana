package org.motechproject.server.filters;

import org.motechproject.server.filters.condition.Condition;
import org.motechproject.server.model.ExpectedObs;

import java.util.ArrayList;
import java.util.List;

public class ExpectedObsFilterForTT1 implements Filter<ExpectedObs> {
    private Condition condition;

    public List<ExpectedObs> on(List<ExpectedObs> expectedObservations) {
        List<ExpectedObs> toBeRemoved = new ArrayList<ExpectedObs>();
        for (ExpectedObs expectedObservation : expectedObservations) {
            if (condition.metBy(expectedObservation))
                toBeRemoved.add(expectedObservation);
        }
        expectedObservations.removeAll(toBeRemoved);
        return expectedObservations;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }
}
