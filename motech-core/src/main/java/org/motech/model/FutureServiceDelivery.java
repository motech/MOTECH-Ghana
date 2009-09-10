/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.motech.model;

import java.util.Date;

import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.User;

public class FutureServiceDelivery {

	private Integer id;
	private Patient patient;
	private User user;
	private Date date;
	private Concept service;
	private Date patientNotifiedDate;
	private Date userNotifiedDate;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Concept getService() {
		return service;
	}

	public void setService(Concept service) {
		this.service = service;
	}

	public Date getPatientNotifiedDate() {
		return patientNotifiedDate;
	}

	public void setPatientNotifiedDate(Date patientNotifiedDate) {
		this.patientNotifiedDate = patientNotifiedDate;
	}

	public Date getUserNotifiedDate() {
		return userNotifiedDate;
	}

	public void setUserNotifiedDate(Date userNotifiedDate) {
		this.userNotifiedDate = userNotifiedDate;
	}

}
