package org.motechproject.server.omod.filters;

import org.motechproject.server.model.DefaultedExpectedEncounterAlert;
import org.motechproject.server.model.ExpectedEncounter;
import org.motechproject.server.omod.MotechService;

import java.util.ArrayList;
import java.util.List;

public class ExpectedEncounterMaxAlertsFilter implements Filter<ExpectedEncounter> {
    private MotechService motechService;

    public List<ExpectedEncounter> filter(List<ExpectedEncounter> expectedEncounters) {
        List<ExpectedEncounter> toBeRemoved = new ArrayList<ExpectedEncounter>();
        for (ExpectedEncounter expectedEncounter : expectedEncounters) {
            DefaultedExpectedEncounterAlert alert = motechService.getDefaultedEncounterAlertFor(expectedEncounter);
            if (alert != null && !alert.canBeSent()) {
                toBeRemoved.add(expectedEncounter);
            }
        }
        expectedEncounters.removeAll(toBeRemoved);
        return expectedEncounters;
    }

    public void setMotechService(MotechService motechService) {
        this.motechService = motechService;
    }
}
