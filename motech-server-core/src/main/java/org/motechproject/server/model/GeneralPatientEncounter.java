package org.motechproject.server.model;

import java.util.Date;

import org.motechproject.ws.Gender;

public class GeneralPatientEncounter {

	private Long id;
	private Date date;
	private String facilityId;
	private String serialNumber;
	private Gender sex;
	private Date birthDate;
	private Boolean insured;
	private Boolean newCase;
	private Integer diagnosis;
	private Integer secondaryDiagnosis;
	private Boolean referred;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public Gender getSex() {
		return sex;
	}

	public void setSex(Gender sex) {
		this.sex = sex;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public Boolean getInsured() {
		return insured;
	}

	public void setInsured(Boolean insured) {
		this.insured = insured;
	}

	public Boolean getNewCase() {
		return newCase;
	}

	public void setNewCase(Boolean newCase) {
		this.newCase = newCase;
	}

	public Integer getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(Integer diagnosis) {
		this.diagnosis = diagnosis;
	}

	public Integer getSecondaryDiagnosis() {
		return secondaryDiagnosis;
	}

	public void setSecondaryDiagnosis(Integer secondaryDiagnosis) {
		this.secondaryDiagnosis = secondaryDiagnosis;
	}

	public Boolean getReferred() {
		return referred;
	}

	public void setReferred(Boolean referred) {
		this.referred = referred;
	}

}
