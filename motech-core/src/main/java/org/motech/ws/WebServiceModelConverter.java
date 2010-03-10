package org.motech.ws;

import org.motechproject.ws.Patient;

public interface WebServiceModelConverter {

	Patient patientToWebService(org.openmrs.Patient patient, boolean minimal);
}
