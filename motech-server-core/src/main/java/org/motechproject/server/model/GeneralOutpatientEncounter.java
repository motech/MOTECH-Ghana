/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) ${year} The Trustees of Columbia University in the City of
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

import org.motechproject.ws.Gender;

public class GeneralOutpatientEncounter {

	private Long id;
	private Date date;
	private Integer staffId;
	private Integer facilityId;
	private String serialNumber;
	private Gender sex;
	private Date birthDate;
	private Boolean insured;
	private Boolean newCase;
	private Integer diagnosis;
	private Integer secondaryDiagnosis;
	private Boolean referred;
	private Boolean rdtGiven;
	private Boolean rdtPositive;
	private Boolean actTreated;
	private String comments;

	public GeneralOutpatientEncounter() {
	}

	public GeneralOutpatientEncounter(Date date, Integer staffId,
			Integer facilityId, String serialNumber, Gender sex,
			Date birthDate, Boolean insured, Boolean newCase,
			Integer diagnosis, Integer secondaryDiagnosis, Boolean referred,
			Boolean rdtGiven, Boolean rdtPositive, Boolean actTreated,
			String comments) {
		this.date = date;
		this.staffId = staffId;
		this.facilityId = facilityId;
		this.serialNumber = serialNumber;
		this.sex = sex;
		this.birthDate = birthDate;
		this.insured = insured;
		this.newCase = newCase;
		this.diagnosis = diagnosis;
		this.secondaryDiagnosis = secondaryDiagnosis;
		this.referred = referred;
		this.rdtGiven = rdtGiven;
		this.rdtPositive = rdtPositive;
		this.actTreated = actTreated;
		this.comments = comments;
	}

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

	public Integer getStaffId() {
		return staffId;
	}

	public void setStaffId(Integer staffId) {
		this.staffId = staffId;
	}

	public Integer getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(Integer facilityId) {
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

	public Boolean getRdtGiven() {
		return rdtGiven;
	}

	public void setRdtGiven(Boolean rdtGiven) {
		this.rdtGiven = rdtGiven;
	}

	public Boolean getRdtPositive() {
		return rdtPositive;
	}

	public void setRdtPositive(Boolean rdtPositive) {
		this.rdtPositive = rdtPositive;
	}

	public Boolean getActTreated() {
		return actTreated;
	}

	public void setActTreated(Boolean actTreated) {
		this.actTreated = actTreated;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	@Override
	public String toString() {
		return "General Outpatient Encounter: [date: " + date + ", staffID: "
				+ staffId + ", facilityID: " + facilityId + ", serial: "
				+ serialNumber + ", sex: " + sex + ", birthdate: " + birthDate
				+ ", insured: " + insured + ", newcase: " + newCase
				+ ", diagnosis1: " + diagnosis + ", diagnosis2: "
				+ secondaryDiagnosis + ", referred: " + referred
				+ ", rdtGiven: " + rdtGiven + ", rdtPositive: " + rdtPositive
				+ ", actTreated: " + actTreated + ", comments: " + comments
				+ "]";
	}

}
