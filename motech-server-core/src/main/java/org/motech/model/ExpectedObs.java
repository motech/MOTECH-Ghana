package org.motech.model;

import java.util.Date;

import org.openmrs.Concept;
import org.openmrs.Person;

public class ExpectedObs {

	private Long id;
	private Person person;
	private Concept concept;
	private Concept valueCoded;
	private Double valueNumeric;
	private Date minObsDatetime;
	private Date dueObsDatetime;
	private Date lateObsDatetime;
	private Date maxObsDatetime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
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

	@Override
	public String toString() {
		return "Expected Obs: [" + "id: " + id + ", concept: "
				+ (concept != null ? concept.getName() : "null") + ", person: "
				+ (person != null ? person.getPersonId() : "null")
				+ ", valueConcept: "
				+ (valueCoded != null ? valueCoded.getName() : "null")
				+ ", valueNumeric: " + valueNumeric + ", min: "
				+ minObsDatetime + ", due: " + dueObsDatetime + ", late: "
				+ lateObsDatetime + ", max: " + maxObsDatetime + "]";
	}

}
