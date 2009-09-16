package org.motech.event;

import java.util.Date;

import org.openmrs.Patient;

public interface PatientObsService {

	int getNumberOfObs(Patient patient, String conceptName);

	Date getLastObsDate(Patient patient, String conceptName);
}
