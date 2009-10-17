package org.motech.event.impl;

import org.motech.event.Command;
import org.motech.event.PatientObsService;

public class RemoveRegimenEnrollmentCommand implements Command {

	Integer personId;
	String regimenName;
	PatientObsService patientObsService;

	public String getRegimenName() {
		return regimenName;
	}

	public void setRegimenName(String regimenName) {
		this.regimenName = regimenName;
	}

	public Integer getPersonId() {
		return personId;
	}

	public void setPersonId(Integer personId) {
		this.personId = personId;
	}

	public PatientObsService getPatientObsService() {
		return patientObsService;
	}

	public void setPatientObsService(PatientObsService patientObsService) {
		this.patientObsService = patientObsService;
	}

	public void execute() {
		patientObsService.removeRegimen(personId, regimenName);
	}

}
