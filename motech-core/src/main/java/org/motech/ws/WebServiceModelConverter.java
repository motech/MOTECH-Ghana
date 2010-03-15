package org.motech.ws;

import java.util.List;

import org.motechproject.ws.Patient;
import org.openmrs.Encounter;

public interface WebServiceModelConverter {

	Patient patientToWebService(org.openmrs.Patient patient, boolean minimal);

	Patient[] patientToWebService(List<org.openmrs.Patient> patients,
			boolean minimal);

	Patient[] deliveriesToWebServicePatients(List<Encounter> deliveries);
}
