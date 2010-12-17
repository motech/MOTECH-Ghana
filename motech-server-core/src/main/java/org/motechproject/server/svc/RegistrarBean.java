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

package org.motechproject.server.svc;

import java.util.Date;
import java.util.List;

import org.motechproject.server.annotation.RunAsAdminUser;
import org.motechproject.server.annotation.RunAsUserParam;
import org.motechproject.server.model.Community;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.DayOfWeek;
import org.motechproject.ws.Gender;
import org.motechproject.ws.HIVResult;
import org.motechproject.ws.HowLearned;
import org.motechproject.ws.InterestReason;
import org.motechproject.ws.MediaType;
import org.motechproject.ws.RegistrantType;
import org.motechproject.ws.RegistrationMode;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.User;

/**
 * The service interface for registration and data entry in the motech server
 * project.
 */
public interface RegistrarBean {

	@RunAsAdminUser
	public User registerStaff(String firstName, String lastName, String phone,
			String staffType);

	@RunAsAdminUser
	public Patient registerPatient(@RunAsUserParam User staff,
			Location facility, Date date, RegistrationMode registrationMode,
			Integer motechId, RegistrantType registrantType, String firstName,
			String middleName, String lastName, String preferredName,
			Date dateOfBirth, Boolean estimatedBirthDate, Gender sex,
			Boolean insured, String nhis, Date nhisExpires, Patient mother,
			Community community, String address, String phoneNumber,
			Date expDeliveryDate, Boolean deliveryDateConfirmed,
			Boolean enroll, Boolean consent, ContactNumberType ownership,
			MediaType format, String language, DayOfWeek dayOfWeek,
			Date timeOfDay, InterestReason reason, HowLearned howLearned,
			Integer messagesStartWeek);

	public Patient registerPatient(RegistrationMode registrationMode,
			Integer motechId, RegistrantType registrantType, String firstName,
			String middleName, String lastName, String preferredName,
			Date dateOfBirth, Boolean estimatedBirthDate, Gender sex,
			Boolean insured, String nhis, Date nhisExpires, Patient mother,
			Community community, String address, String phoneNumber,
			Date expDeliveryDate, Boolean deliveryDateConfirmed,
			Boolean enroll, Boolean consent, ContactNumberType ownership,
			MediaType format, String language, DayOfWeek dayOfWeek,
			Date timeOfDay, InterestReason reason, HowLearned howLearned,
			Integer messagesStartWeek);

	public void demoRegisterPatient(RegistrationMode registrationMode,
			Integer motechId, String firstName, String middleName,
			String lastName, String preferredName, Date dateOfBirth,
			Boolean estimatedBirthDate, Gender sex, Boolean insured,
			String nhis, Date nhisExpires, Community community, String address,
			String phoneNumber, Boolean enroll, Boolean consent,
			ContactNumberType ownership, MediaType format, String language,
			DayOfWeek dayOfWeek, Date timeOfDay, InterestReason reason,
			HowLearned howLearned);

	public void editPatient(Patient patient, String firstName,
			String middleName, String lastName, String preferredName,
			Date dateOfBirth, Boolean estimatedBirthDate, Gender sex,
			Boolean insured, String nhis, Date nhisExpires, Patient mother,
			Community community, String address, String phoneNumber,
			Date expDeliveryDate, Boolean enroll, Boolean consent,
			ContactNumberType ownership, MediaType format, String language,
			DayOfWeek dayOfWeek, Date timeOfDay);

	@RunAsAdminUser
	public void editPatient(@RunAsUserParam User staff, Date date,
			Patient patient, String phoneNumber,
			ContactNumberType phoneOwnership, String nhis, Date nhisExpires,
			Boolean stopEnrollment);

	public void registerPregnancy(Patient patient, Date expDeliveryDate,
			Boolean deliveryDateConfirmed, Boolean enroll, Boolean consent,
			String phoneNumber, ContactNumberType ownership, MediaType format,
			String language, DayOfWeek dayOfWeek, Date timeOfDay,
			InterestReason reason, HowLearned howLearned);

	@RunAsAdminUser
	public void registerPregnancy(@RunAsUserParam User staff,
			Location facility, Date date, Patient patient,
			Date estDeliveryDate, Boolean enroll, Boolean consent,
			ContactNumberType ownership, String phoneNumber, MediaType format,
			String language, DayOfWeek dayOfWeek, Date timeOfDay,
			HowLearned howLearned);

	@RunAsAdminUser
	public void registerANCMother(@RunAsUserParam User staff,
			Location facility, Date date, Patient patient, String ancRegNumber,
			Date estDeliveryDate, Double height, Integer gravida,
			Integer parity, Boolean enroll, Boolean consent,
			ContactNumberType ownership, String phoneNumber, MediaType format,
			String language, DayOfWeek dayOfWeek, Date timeOfDay,
			HowLearned howLearned);

	@RunAsAdminUser
	public void registerCWCChild(@RunAsUserParam User staff, Location facility,
			Date date, Patient patient, String cwcRegNumber, Boolean enroll,
			Boolean consent, ContactNumberType ownership, String phoneNumber,
			MediaType format, String language, DayOfWeek dayOfWeek,
			Date timeOfDay, HowLearned howLearned);

	@RunAsAdminUser
	public void recordPatientHistory(@RunAsUserParam User staff,
			Location facility, Date date, Patient patient, Integer lastIPT,
			Date lastIPTDate, Integer lastTT, Date lastTTDate, Date bcgDate,
			Integer lastOPV, Date lastOPVDate, Integer lastPenta,
			Date lastPentaDate, Date measlesDate, Date yellowFeverDate,
			Integer lastIPTI, Date lastIPTIDate, Date lastVitaminADate);

	@RunAsAdminUser
	public void recordMotherANCVisit(@RunAsUserParam User staff,
			Location facility, Date date, Patient patient, Integer visitNumber,
			Integer ancLocation, String house, String community,
			Date estDeliveryDate, Integer bpSystolic, Integer bpDiastolic,
			Double weight, Integer ttDose, Integer iptDose,
			Boolean iptReactive, Boolean itnUse, Double fht, Integer fhr,
			Integer urineTestProtein, Integer urineTestGlucose,
			Double hemoglobin, Boolean vdrlReactive, Boolean vdrlTreatment,
			Boolean dewormer, Boolean maleInvolved, Boolean pmtct,
			Boolean preTestCounseled, HIVResult hivTestResult,
			Boolean postTestCounseled, Boolean pmtctTreatment,
			Boolean referred, Date nextANCDate, String comments);

	@RunAsAdminUser
	public void recordPregnancyTermination(@RunAsUserParam User staff,
			Location facility, Date date, Patient patient,
			Integer terminationType, Integer procedure,
			Integer[] complications, Boolean maternalDeath, Boolean referred,
			Boolean postAbortionFPCounseled, Boolean postAbortionFPAccepted,
			String comments);

	@RunAsAdminUser
	public List<Patient> recordPregnancyDelivery(@RunAsUserParam User staff,
			Location facility, Date datetime, Patient patient, Integer mode,
			Integer outcome, Integer deliveryLocation, Integer deliveredBy,
			Boolean maleInvolved, Integer[] complications, Integer vvf,
			Boolean maternalDeath, String comments,
			List<BirthOutcomeChild> outcomes);

	@RunAsAdminUser
	public void recordPregnancyDeliveryNotification(@RunAsUserParam User staff,
			Location facility, Date date, Patient patient);

	@RunAsAdminUser
	public void recordMotherPNCVisit(@RunAsUserParam User staff,
			Location facility, Date datetime, Patient patient,
			Integer visitNumber, Integer pncLocation, String house,
			String community, Boolean referred, Boolean maleInvolved,
			Boolean vitaminA, Integer ttDose, Integer lochiaColour,
			Boolean lochiaAmountExcess, Boolean lochiaOdourFoul,
			Double temperature, Double fht, String comments);

	@RunAsAdminUser
	public void recordChildPNCVisit(@RunAsUserParam User staff,
			Location facility, Date datetime, Patient patient,
			Integer visitNumber, Integer pncLocation, String house,
			String community, Boolean referred, Boolean maleInvolved,
			Double weight, Double temperature, Boolean bcg, Boolean opv0,
			Integer respiration, Boolean cordConditionNormal,
			Boolean babyConditionGood, String comments);

	@RunAsAdminUser
	public void recordTTVisit(@RunAsUserParam User staff, Location facility,
			Date date, Patient patient, Integer ttDose);

	@RunAsAdminUser
	public void recordDeath(@RunAsUserParam User staff, Location facility,
			Date date, Patient patient);

	@RunAsAdminUser
	public void recordChildCWCVisit(@RunAsUserParam User staff,
			Location facility, Date date, Patient patient, Integer cwcLocation,
			String house, String community, Boolean bcg, Integer opvDose,
			Integer pentaDose, Boolean measles, Boolean yellowFever,
			Boolean csm, Integer iptiDose, Boolean vitaminA, Boolean dewormer,
			Double weight, Double muac, Double height, Boolean maleInvolved,
			String comments);

	public void recordGeneralOutpatientVisit(Integer staffId,
			Integer facilityId, Date date, String serialNumber, Gender sex,
			Date dateOfBirth, Boolean insured, Integer diagnosis,
			Integer secondDiagnosis, Boolean rdtGiven, Boolean rdtPositive,
			Boolean actTreated, Boolean newCase, Boolean referred,
			String comments);

	@RunAsAdminUser
	public void recordOutpatientVisit(@RunAsUserParam User staff,
			Location facility, Date date, Patient patient, String serialNumber,
			Boolean insured, Integer diagnosis, Integer secondDiagnosis,
			Boolean rdtGiven, Boolean rdtPositive, Boolean actTreated,
			Boolean newCase, Boolean referred, String comments);

	public void demoEnrollPatient(Patient patient);
}
