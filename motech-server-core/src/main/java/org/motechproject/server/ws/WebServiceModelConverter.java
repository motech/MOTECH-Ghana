package org.motechproject.server.ws;

import java.util.List;

import org.motechproject.ws.Patient;
import org.openmrs.Encounter;
import org.openmrs.Obs;

public interface WebServiceModelConverter {

	Patient patientToWebService(org.openmrs.Patient patient, boolean minimal);

	Patient[] patientToWebService(List<org.openmrs.Patient> patients,
			boolean minimal);

	Patient[] deliveriesToWebServicePatients(List<Encounter> deliveries);

	Patient[] dueDatesToWebServicePatients(List<Obs> dueDates);
}
