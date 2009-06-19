package org.motech.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "futureservicedeliveries")
@NamedQueries( { @NamedQuery(name = "findAllFutureServiceDeliveries", query = "select f from FutureServiceDelivery f") })
public class FutureServiceDelivery {

	private Long id;
	private Patient patient;
	private Nurse nurse;
	private Date date;
	private String service;
	private Date patientNotifiedDate;
	private Date nurseNotifiedDate;

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name = "patient_id")
	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	@ManyToOne
	@JoinColumn(name = "nurse_id")
	public Nurse getNurse() {
		return nurse;
	}

	public void setNurse(Nurse nurse) {
		this.nurse = nurse;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public Date getPatientNotifiedDate() {
		return patientNotifiedDate;
	}

	public void setPatientNotifiedDate(Date patientNotifiedDate) {
		this.patientNotifiedDate = patientNotifiedDate;
	}

	public Date getNurseNotifiedDate() {
		return nurseNotifiedDate;
	}

	public void setNurseNotifiedDate(Date nurseNotifiedDate) {
		this.nurseNotifiedDate = nurseNotifiedDate;
	}

}
