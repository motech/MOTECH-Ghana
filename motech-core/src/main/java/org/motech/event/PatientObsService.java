package org.motech.event;

import java.util.Date;

import org.openmrs.Patient;

public interface PatientObsService {

	int getNumberOfObs(Patient patient, String conceptName, String conceptValue);

	Date getLastObsDate(Patient patient, String conceptName, String conceptValue);

	Date getLastObsValue(Patient patient, String conceptName);
}
