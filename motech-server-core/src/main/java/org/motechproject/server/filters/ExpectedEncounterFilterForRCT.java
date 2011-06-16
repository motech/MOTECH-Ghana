package org.motechproject.server.filters;

import org.motechproject.server.model.ExpectedEncounter;
import org.motechproject.server.filters.condition.Condition;
import org.openmrs.Patient;

import java.util.ArrayList;
import java.util.List;

public class ExpectedEncounterFilterForRCT implements Filter<ExpectedEncounter> {

    private Condition condition;

    public List<ExpectedEncounter> on(List<ExpectedEncounter> expectedEncounters) {
        List<ExpectedEncounter> toBeRemoved = new ArrayList<ExpectedEncounter>();
        for (ExpectedEncounter expectedEncounter : expectedEncounters) {
            Patient patient = expectedEncounter.getPatient();
            if (condition.metBy(patient)) {
                toBeRemoved.add(expectedEncounter);
            }
        }
        expectedEncounters.removeAll(toBeRemoved);
        return expectedEncounters;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }
}
