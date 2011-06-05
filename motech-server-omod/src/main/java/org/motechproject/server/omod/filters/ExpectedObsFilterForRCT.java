package org.motechproject.server.omod.filters;

import org.motechproject.server.model.ExpectedObs;
import org.motechproject.server.omod.filters.condition.Condition;

import java.util.ArrayList;
import java.util.List;

public class ExpectedObsFilterForRCT implements Filter<ExpectedObs>{
    private Condition condition;

    public List<ExpectedObs> on(List<ExpectedObs> expectedObservations) {
        List<ExpectedObs> toBeRemoved = new ArrayList<ExpectedObs>();
        for (ExpectedObs expectedObservation : expectedObservations) {
            if(condition.metBy(expectedObservation.getPatient())){
                toBeRemoved.add(expectedObservation);
            }
        }
        expectedObservations.removeAll(toBeRemoved);
        return expectedObservations;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }
}
