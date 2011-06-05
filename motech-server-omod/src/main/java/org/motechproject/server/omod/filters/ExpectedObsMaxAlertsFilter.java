package org.motechproject.server.omod.filters;

import org.motechproject.server.model.DefaultedExpectedObsAlert;
import org.motechproject.server.model.ExpectedObs;
import org.motechproject.server.omod.MotechService;

import java.util.ArrayList;
import java.util.List;

public class ExpectedObsMaxAlertsFilter implements Filter<ExpectedObs>{

    private MotechService motechService;

    public List<ExpectedObs> filter(List<ExpectedObs> expectedObservations) {
        List<ExpectedObs> toBeRemoved = new ArrayList<ExpectedObs>();

        for (ExpectedObs expectedObs : expectedObservations) {
            DefaultedExpectedObsAlert alert = motechService.getDefaultedObsAlertFor(expectedObs);
            if(alert != null && !alert.canBeSent()){
                toBeRemoved.add(expectedObs);
            }
        }
        expectedObservations.removeAll(toBeRemoved);
        return expectedObservations;
    }

    
    public void setMotechService(MotechService motechService) {
        this.motechService = motechService ;
    }


}
