package org.motechproject.server.omod.web.model;

import org.openmrs.Patient;

public interface WebModelConverter {

	void patientToWeb(Patient patient, WebPatient webPatient);

}
