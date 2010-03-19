package org.motechproject.server.omod.web.model;

import org.openmrs.Patient;
import org.openmrs.Person;

public interface WebModelConverter {

	void patientToWeb(Patient patient, WebPatient webPatient);

	void personToWeb(Person person, WebPatient webPatient);

}
