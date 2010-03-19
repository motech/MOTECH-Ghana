package org.motech.model;

import java.util.Date;

import org.openmrs.EncounterType;
import org.openmrs.Patient;

public class ExpectedEncounter {

	private Long id;
	private Patient patient;
	private EncounterType encounterType;
	private Date minEncounterDatetime;
	private Date dueEncounterDatetime;
	private Date lateEncounterDatetime;
	private Date maxEncounterDatetime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public EncounterType getEncounterType() {
		return encounterType;
	}

	public void setEncounterType(EncounterType encounterType) {
		this.encounterType = encounterType;
	}

	public Date getMinEncounterDatetime() {
		return minEncounterDatetime;
	}

	public void setMinEncounterDatetime(Date minEncounterDatetime) {
		this.minEncounterDatetime = minEncounterDatetime;
	}

	public Date getDueEncounterDatetime() {
		return dueEncounterDatetime;
	}

	public void setDueEncounterDatetime(Date dueEncounterDatetime) {
		this.dueEncounterDatetime = dueEncounterDatetime;
	}

	public Date getLateEncounterDatetime() {
		return lateEncounterDatetime;
	}

	public void setLateEncounterDatetime(Date lateEncounterDatetime) {
		this.lateEncounterDatetime = lateEncounterDatetime;
	}

	public Date getMaxEncounterDatetime() {
		return maxEncounterDatetime;
	}

	public void setMaxEncounterDatetime(Date maxEncounterDatetime) {
		this.maxEncounterDatetime = maxEncounterDatetime;
	}

	@Override
	public String toString() {
		return "Expected Encounter: [" + "id: " + id + ", type: "
				+ (encounterType != null ? encounterType.getName() : "null")
				+ ", patient: "
				+ (patient != null ? patient.getPatientId() : "null")
				+ ", min: " + minEncounterDatetime + ", due: "
				+ dueEncounterDatetime + ", late: " + lateEncounterDatetime
				+ ", max: " + maxEncounterDatetime + "]";
	}

}
