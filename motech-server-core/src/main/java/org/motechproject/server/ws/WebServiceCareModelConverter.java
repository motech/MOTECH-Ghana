package org.motechproject.server.ws;

import org.motechproject.server.model.ExpectedEncounter;
import org.motechproject.server.model.ExpectedObs;
import org.motechproject.ws.Care;

import java.util.List;

public interface WebServiceCareModelConverter {

    Care[] upcomingObsToWebServiceCares(List<ExpectedObs> upcomingObs);

	Care[] upcomingEncountersToWebServiceCares(
			List<ExpectedEncounter> upcomingEncounters);

	Care[] upcomingToWebServiceCares(
			List<ExpectedEncounter> upcomingEncounters,
			List<ExpectedObs> upcomingObs, boolean includePatient);

	Care[] defaultedObsToWebServiceCares(List<ExpectedObs> defaultedObs);

	Care[] defaultedEncountersToWebServiceCares(
			List<ExpectedEncounter> defaultedEncounters);

	Care[] defaultedToWebServiceCares(
			List<ExpectedEncounter> defaultedEncounters,
			List<ExpectedObs> defaultedObs);

}
