package org.motechproject.server.omod.filters;

import org.motechproject.server.model.DefaultedExpectedObsAlert;
import org.motechproject.server.model.ExpectedObs;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.MotechService;

import java.util.ArrayList;
import java.util.List;

public class ExpectedObsMaxAlertsFilter implements Filter<ExpectedObs> {

    private ContextService contextService;

    public List<ExpectedObs> on(List<ExpectedObs> expectedObservations) {
        List<ExpectedObs> toBeRemoved = new ArrayList<ExpectedObs>();

        for (ExpectedObs expectedObs : expectedObservations) {
            DefaultedExpectedObsAlert alert = motechService().getDefaultedObsAlertFor(expectedObs);
            if (alert != null && !alert.canBeSent()) {
                toBeRemoved.add(expectedObs);
            }
        }
        expectedObservations.removeAll(toBeRemoved);
        return expectedObservations;
    }

    private MotechService motechService() {
        return contextService.getMotechService();
    }

    public void setContextService(ContextService contextService) {
        this.contextService = contextService;
    }
}
