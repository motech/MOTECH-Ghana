package org.motechproject.server.model;

import java.util.Date;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;

public class ExpectedObs {

	private Long id;
	private Patient patient;
	private Concept concept;
	private Concept valueCoded;
	private Double valueNumeric;
	private Date minObsDatetime;
	private Date dueObsDatetime;
	private Date lateObsDatetime;
	private Date maxObsDatetime;
	private Obs obs;
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

	public Concept getConcept() {
		return concept;
	}

	public void setConcept(Concept concept) {
		this.concept = concept;
	}

	public Concept getValueCoded() {
		return valueCoded;
	}

	public void setValueCoded(Concept valueCoded) {
		this.valueCoded = valueCoded;
	}

	public Double getValueNumeric() {
		return valueNumeric;
	}

	public void setValueNumeric(Double valueNumeric) {
		this.valueNumeric = valueNumeric;
	}

	public Date getMinObsDatetime() {
		return minObsDatetime;
	}

	public void setMinObsDatetime(Date minObsDatetime) {
		this.minObsDatetime = minObsDatetime;
	}

	public Date getDueObsDatetime() {
		return dueObsDatetime;
	}

	public void setDueObsDatetime(Date dueObsDatetime) {
		this.dueObsDatetime = dueObsDatetime;
	}

	public Date getLateObsDatetime() {
		return lateObsDatetime;
	}

	public void setLateObsDatetime(Date lateObsDatetime) {
		this.lateObsDatetime = lateObsDatetime;
	}

	public Date getMaxObsDatetime() {
		return maxObsDatetime;
	}

	public void setMaxObsDatetime(Date maxObsDatetime) {
		this.maxObsDatetime = maxObsDatetime;
	}

	public Obs getObs() {
		return obs;
	}

	public void setObs(Obs obs) {
		this.obs = obs;
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
		return "Expected Obs: [" + "id: " + id + ", concept: "
				+ (concept != null ? concept.getConceptId() : "null")
				+ ", person: "
				+ (patient != null ? patient.getPatientId() : "null")
				+ ", valueConcept: "
				+ (valueCoded != null ? valueCoded.getConceptId() : "null")
				+ ", valueNumeric: " + valueNumeric + ", min: "
				+ minObsDatetime + ", due: " + dueObsDatetime + ", late: "
				+ lateObsDatetime + ", max: " + maxObsDatetime + ", obs: "
				+ (obs != null ? obs.getObsId() : "null") + ", name: " + name
				+ ", group: " + group + ", void: " + voided + "]";
	}
}
