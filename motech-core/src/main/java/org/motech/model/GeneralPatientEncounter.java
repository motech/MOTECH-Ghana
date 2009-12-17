package org.motech.model;

import java.util.Date;

import org.motechproject.ws.Gender;

public class GeneralPatientEncounter {

	private Long id;
	private Date encounterDate;
	private Integer clinicId;
	private String patientSerial;
	private Gender patientGender;
	private Date patientBirthDate;
	private Integer patientDiagnosis;
	private Boolean patientReferral;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getEncounterDate() {
		return encounterDate;
	}

	public void setEncounterDate(Date encounterDate) {
		this.encounterDate = encounterDate;
	}

	public Integer getClinicId() {
		return clinicId;
	}

	public void setClinicId(Integer clinicId) {
		this.clinicId = clinicId;
	}

	public String getPatientSerial() {
		return patientSerial;
	}

	public void setPatientSerial(String patientSerial) {
		this.patientSerial = patientSerial;
	}

	public Gender getPatientGender() {
		return patientGender;
	}

	public void setPatientGender(Gender patientGender) {
		this.patientGender = patientGender;
	}

	public Date getPatientBirthDate() {
		return patientBirthDate;
	}

	public void setPatientBirthDate(Date patientBirthDate) {
		this.patientBirthDate = patientBirthDate;
	}

	public Integer getPatientDiagnosis() {
		return patientDiagnosis;
	}

	public void setPatientDiagnosis(Integer patientDiagnosis) {
		this.patientDiagnosis = patientDiagnosis;
	}

	public Boolean getPatientReferral() {
		return patientReferral;
	}

	public void setPatientReferral(Boolean patientReferral) {
		this.patientReferral = patientReferral;
	}

}
