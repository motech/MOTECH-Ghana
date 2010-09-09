package org.motechproject.server.ws;

import java.util.List;

import org.motechproject.server.model.ExpectedEncounter;
import org.motechproject.server.model.ExpectedObs;
import org.motechproject.ws.Care;
import org.motechproject.ws.Patient;
import org.openmrs.Encounter;
import org.openmrs.Obs;

public interface WebServiceModelConverter {

	Patient patientToWebService(org.openmrs.Patient patient, boolean minimal);

	Patient[] patientToWebService(List<org.openmrs.Patient> patients,
			boolean minimal);

	Patient[] deliveriesToWebServicePatients(List<Encounter> deliveries);

	Patient[] dueDatesToWebServicePatients(List<Obs> dueDates);

	Care[] upcomingObsToWebServiceCares(List<ExpectedObs> upcomingObs);

	Care[] upcomingEncountersToWebServiceCares(
			List<ExpectedEncounter> upcomingEncounters);

	Care[] upcomingToWebServiceCares(
			List<ExpectedEncounter> upcomingEncounters,
			List<ExpectedObs> upcomingObs);

	Care[] defaultedObsToWebServiceCares(List<ExpectedObs> defaultedObs);

	Care[] defaultedEncountersToWebServiceCares(
			List<ExpectedEncounter> defaultedEncounters);

	Care[] defaultedToWebServiceCares(
			List<ExpectedEncounter> defaultedEncounters,
			List<ExpectedObs> defaultedObs);

	Patient upcomingObsToWebServicePatient(ExpectedObs upcomingObs);

	Patient upcomingEncounterToWebServicePatient(
			ExpectedEncounter upcomingEncounter);
}
