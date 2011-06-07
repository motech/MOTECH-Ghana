package org.motechproject.server.omod.filters;

import org.motechproject.server.model.DefaultedExpectedEncounterAlert;
import org.motechproject.server.model.ExpectedEncounter;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.MotechService;

import java.util.ArrayList;
import java.util.List;

public class ExpectedEncounterMaxAlertsFilter implements Filter<ExpectedEncounter> {
    private ContextService contextService;

    public List<ExpectedEncounter> on(List<ExpectedEncounter> expectedEncounters) {
        List<ExpectedEncounter> toBeRemoved = new ArrayList<ExpectedEncounter>();
        for (ExpectedEncounter expectedEncounter : expectedEncounters) {
            DefaultedExpectedEncounterAlert alert = motechService().getDefaultedEncounterAlertFor(expectedEncounter);
            if (alert != null && !alert.canBeSent()) {
                toBeRemoved.add(expectedEncounter);
            }
        }
        expectedEncounters.removeAll(toBeRemoved);
        return expectedEncounters;
    }

    private MotechService motechService() {
        return contextService.getMotechService();
    }

    public void setContextService(ContextService contextService) {
        this.contextService = contextService;
    }
}
