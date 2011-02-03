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

package org.motechproject.server.omod.web.model;

import java.util.Date;

import flexjson.JSON;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.DayOfWeek;
import org.motechproject.ws.Gender;
import org.motechproject.ws.HowLearned;
import org.motechproject.ws.InterestReason;
import org.motechproject.ws.MediaType;
import org.motechproject.ws.RegistrantType;
import org.motechproject.ws.RegistrationMode;

public class WebPatient {

	private Integer id;
	private RegistrationMode registrationMode;
	private RegistrantType registrantType;
	private Integer motechId;
	private String firstName;
	private String middleName;
	private String lastName;
	private String prefName;
	private Date birthDate;
	private Boolean birthDateEst;
	private Gender sex;
	private Integer motherMotechId;
	private Boolean insured;
	private String nhis;
	private Date nhisExpDate;
	private String region;
	private String district;
	private Integer communityId;
	private String communityName;
	private String address;
	private Date dueDate;
	private Boolean dueDateConfirmed;
	private Integer gravida;
	private Integer parity;
	private Boolean enroll;
	private Boolean consent;
	private String phoneNumber;
	private ContactNumberType phoneType;
	private MediaType mediaType;
	private String language;
	private DayOfWeek dayOfWeek;
	private Date timeOfDay;
	private HowLearned howLearned;
	private InterestReason interestReason;
	private Integer messagesStartWeek;

	public WebPatient() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

    @JSON(include = false)
	public RegistrationMode getRegistrationMode() {
		return registrationMode;
	}

	public void setRegistrationMode(RegistrationMode registrationMode) {
		this.registrationMode = registrationMode;
	}

    @JSON(include = false)
	public RegistrantType getRegistrantType() {
		return registrantType;
	}

	public void setRegistrantType(RegistrantType registrantType) {
		this.registrantType = registrantType;
	}

	public Integer getMotechId() {
		return motechId;
	}

	public void setMotechId(Integer motechId) {
		this.motechId = motechId;
	}

    @JSON(include = false)
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

    @JSON(include = false)
	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

    @JSON(include = false)
	public String getPrefName() {
		return prefName;
	}

	public void setPrefName(String prefName) {
		this.prefName = prefName;
	}

    @JSON(include = false)
	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

    @JSON(include = false)
	public Boolean getBirthDateEst() {
		return birthDateEst;
	}


	public void setBirthDateEst(Boolean birthDateEst) {
		this.birthDateEst = birthDateEst;
	}

	public Gender getSex() {
		return sex;
	}

	public void setSex(Gender sex) {
		this.sex = sex;
	}

	public Integer getMotherMotechId() {
		return motherMotechId;
	}

	public void setMotherMotechId(Integer motherMotechId) {
		this.motherMotechId = motherMotechId;
	}

	public Boolean getInsured() {
		return insured;
	}

	public void setInsured(Boolean insured) {
		this.insured = insured;
	}

    @JSON(include = false)
	public String getNhis() {
		return nhis;
	}

	public void setNhis(String nhis) {
		this.nhis = nhis;
	}

    @JSON(include = false)
	public Date getNhisExpDate() {
		return nhisExpDate;
	}

	public void setNhisExpDate(Date nhisExpDate) {
		this.nhisExpDate = nhisExpDate;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public Integer getCommunityId() {
		return communityId;
	}

	public void setCommunityId(Integer communityId) {
		this.communityId = communityId;
	}

	public String getCommunityName() {
		return communityName;
	}

	public void setCommunityName(String communityName) {
		this.communityName = communityName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

    @JSON(include = false)
	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

    @JSON(include = false)
	public Boolean getDueDateConfirmed() {
		return dueDateConfirmed;
	}

	public void setDueDateConfirmed(Boolean dueDateConfirmed) {
		this.dueDateConfirmed = dueDateConfirmed;
	}

    @JSON(include = false)
	public Integer getGravida() {
		return gravida;
	}

	public void setGravida(Integer gravida) {
		this.gravida = gravida;
	}

    @JSON(include = false)
	public Integer getParity() {
		return parity;
	}

	public void setParity(Integer parity) {
		this.parity = parity;
	}

	public Boolean getEnroll() {
		return enroll;
	}

	public void setEnroll(Boolean enroll) {
		this.enroll = enroll;
	}

	public Boolean getConsent() {
		return consent;
	}

	public void setConsent(Boolean consent) {
		this.consent = consent;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public ContactNumberType getPhoneType() {
		return phoneType;
	}

	public void setPhoneType(ContactNumberType phoneType) {
		this.phoneType = phoneType;
	}

	public MediaType getMediaType() {
		return mediaType;
	}

	public void setMediaType(MediaType mediaType) {
		this.mediaType = mediaType;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public DayOfWeek getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(DayOfWeek dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public Date getTimeOfDay() {
		return timeOfDay;
	}

	public void setTimeOfDay(Date timeOfDay) {
		this.timeOfDay = timeOfDay;
	}

	public HowLearned getHowLearned() {
		return howLearned;
	}

	public void setHowLearned(HowLearned howLearned) {
		this.howLearned = howLearned;
	}

	public InterestReason getInterestReason() {
		return interestReason;
	}

	public void setInterestReason(InterestReason interestReason) {
		this.interestReason = interestReason;
	}

	public Integer getMessagesStartWeek() {
		return messagesStartWeek;
	}

	public void setMessagesStartWeek(Integer messagesStartWeek) {
		this.messagesStartWeek = messagesStartWeek;
	}

}
