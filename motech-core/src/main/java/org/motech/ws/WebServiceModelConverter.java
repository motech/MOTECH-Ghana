package org.motech.ws;

import java.util.List;

import org.motechproject.ws.Patient;

public interface WebServiceModelConverter {

	Patient patientToWebService(org.openmrs.Patient patient, boolean minimal);

	Patient[] patientToWebService(List<org.openmrs.Patient> patients,
			boolean minimal);
}
