/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010-11 The Trustees of Columbia University in the City of
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
