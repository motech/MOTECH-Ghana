package org.motechproject.server.model;

import java.util.Date;

import org.openmrs.Encounter;
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
	private Encounter encounter;
	private String name;
	private String group;
	private Boolean voided = Boolean.FALSE;

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

	public Encounter getEncounter() {
		return encounter;
	}

	public void setEncounter(Encounter encounter) {
		this.encounter = encounter;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public Boolean getVoided() {
		return voided;
	}

	public void setVoided(Boolean voided) {
		this.voided = voided;
	}

	@Override
	public String toString() {
		return "Expected Encounter: [" + "id: " + id + ", type: "
				+ (encounterType != null ? encounterType.getName() : "null")
				+ ", patient: "
				+ (patient != null ? patient.getPatientId() : "null")
				+ ", min: " + minEncounterDatetime + ", due: "
				+ dueEncounterDatetime + ", late: " + lateEncounterDatetime
				+ ", max: " + maxEncounterDatetime + ", encounter: "
				+ (encounter != null ? encounter.getEncounterId() : "null")
				+ ", name: " + name + ", group: " + group + ", void: " + voided
				+ "]";
	}

}
