package org.motech.event.impl;

import org.motech.event.PatientObsService;
import org.openmrs.Patient;

public class RegimenStateTransitionExpectedNumImpl extends
		RegimenStateTransitionImpl {

	private PatientObsService patientObsService;
	private int expectedNumber;

	@Override
	public boolean evaluate(Patient patient) {
		String conceptName = prevState.getRegimen().getConceptName();
		String conceptValue = prevState.getRegimen().getConceptValue();
		int obsNum = patientObsService.getNumberOfObs(patient, conceptName,
				conceptValue);

		if (prevState.equals(nextState)) {
			return obsNum == expectedNumber;
		} else {
			return obsNum >= expectedNumber;
		}
	}

	public PatientObsService getPatientObsService() {
		return patientObsService;
	}

	public void setPatientObsService(PatientObsService patientObsService) {
		this.patientObsService = patientObsService;
	}

	public int getExpectedNumber() {
		return expectedNumber;
	}

	public void setExpectedNumber(int expectedNumber) {
		this.expectedNumber = expectedNumber;
	}

}