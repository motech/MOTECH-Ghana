/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010 The Trustees of Columbia University in the City of
 * New York and Grameen Foundation USA.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Grameen Foundation USA, Columbia University, or
 * their respective contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA, COLUMBIA UNIVERSITY
 * AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION
 * USA, COLUMBIA UNIVERSITY OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
