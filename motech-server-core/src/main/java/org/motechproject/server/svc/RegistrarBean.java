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

package org.motechproject.server.svc;

import org.motechproject.server.annotation.RunAsAdminUser;
import org.motechproject.server.annotation.RunAsUserParam;
import org.motechproject.server.annotation.RunWithPrivileges;
import org.motechproject.server.model.*;
import org.motechproject.ws.*;
import org.openmrs.*;
import org.openmrs.Patient;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.util.OpenmrsConstants;

import java.util.Date;
import java.util.List;

/**
 * The major internal service interface for the motech server project at this
 * point, the RegistrarBean interface includes all major operations necessary to
 * support the current project requirements. It is intended that these
 * operations are transactional, and we fully intend to refactor this interface
 * into multiple (more appropriate) service interfaces if/when it becomes
 * necessary. The name is a bit of a misnomer, as it is a vestige of the early
 * prototypes that handled only registration.
 */
public interface RegistrarBean {

	@RunAsAdminUser
	public User registerStaff(String firstName, String lastName, String phone,
                              String staffType, String staffId);

	@RunAsAdminUser
	public Patient registerPatient(@RunAsUserParam User staff,
			Facility facility, Date date, RegistrationMode registrationMode,
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
                                   Community community, Facility facility, String address, String phoneNumber,
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
                            DayOfWeek dayOfWeek, Date timeOfDay, Facility facility);

	@RunAsAdminUser
	public void editPatient(@RunAsUserParam User staff, Date date,
			Patient patient, Patient mother,String phoneNumber,
			ContactNumberType phoneOwnership, String nhis, Date nhisExpires, Date expectedDeliveryDate,
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
                                     Integer lastIPTI, Date lastIPTIDate, Date lastVitaminADate, Integer whyNoHistory);

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
			Facility facility, Date datetime, Patient patient, Integer mode,
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
			Boolean actTreated, Boolean newCase, Boolean newPatient,Boolean referred,
			String comments);

	@RunAsAdminUser
	public void recordOutpatientVisit(@RunAsUserParam User staff,
			Location facility, Date date, Patient patient, String serialNumber,
			Boolean insured, Integer diagnosis, Integer secondDiagnosis,
			Boolean rdtGiven, Boolean rdtPositive, Boolean actTreated,
			Boolean newCase, Boolean newPatient, Boolean referred, String comments);

	@RunWithPrivileges( { OpenmrsConstants.PRIV_VIEW_PERSONS,
			OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES })
	public void setMessageStatus(String messageId, Boolean success);

	public User getUserByPhoneNumber(String phoneNumber);

	public List<Location> getAllLocations();

	public List<User> getAllStaff();

	public List<String> getStaffTypes();

	public List<Patient> getAllPatients();

	@RunWithPrivileges( { OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES,
			OpenmrsConstants.PRIV_VIEW_IDENTIFIER_TYPES })
	public List<Patient> getPatients(String firstName, String lastName,
			String preferredName, Date birthDate, Integer facilityId,
			String phoneNumber, String nhisNumber, Integer communityId, String motechId);

	@RunWithPrivileges( { OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES,
			OpenmrsConstants.PRIV_VIEW_IDENTIFIER_TYPES })
	public List<Patient> getDuplicatePatients(String firstName,
			String lastName, String preferredName, Date birthDate,
			Integer facilityId, String phoneNumber, String nhisNumber,
			String motechId);

	public List<Obs> getAllPregnancies();

	public List<ExpectedEncounter> getUpcomingExpectedEncounters(Patient patient);

	public List<ExpectedObs> getUpcomingExpectedObs(Patient patient);

	public List<ExpectedEncounter> getDefaultedExpectedEncounters(
			Facility facility, String[] groups);

	public List<ExpectedObs> getDefaultedExpectedObs(Facility facility,
			String[] groups);

	public List<ExpectedEncounter> getExpectedEncounters(Patient patient);

	public List<ExpectedObs> getExpectedObs(Patient patient);

	public List<ExpectedEncounter> getExpectedEncounters(Patient patient,
			String group);

	public List<ExpectedObs> getExpectedObs(Patient patient, String group);

	@RunWithPrivileges( { OpenmrsConstants.PRIV_VIEW_ENCOUNTER_TYPES,
			OpenmrsConstants.PRIV_VIEW_ENCOUNTERS })
	public List<Encounter> getRecentDeliveries(Facility facility);

	@RunWithPrivileges( { OpenmrsConstants.PRIV_VIEW_ENCOUNTER_TYPES,
			OpenmrsConstants.PRIV_VIEW_ENCOUNTERS })
	public Date getCurrentDeliveryDate(Patient patient);

	@RunWithPrivileges( { OpenmrsConstants.PRIV_VIEW_CONCEPTS })
	public List<Obs> getUpcomingPregnanciesDueDate(Facility facility);

	@RunWithPrivileges( { OpenmrsConstants.PRIV_VIEW_CONCEPTS })
	public List<Obs> getOverduePregnanciesDueDate(Facility facility);

	@RunWithPrivileges( { OpenmrsConstants.PRIV_VIEW_PATIENTS })
	public Patient getPatientById(Integer patientId);

	public Obs getActivePregnancy(Integer patientId);

	public List<ScheduledMessage> getAllScheduledMessages();

	public List<ScheduledMessage> getScheduledMessages(
			MessageProgramEnrollment enrollment);

	public Date getPatientBirthDate(Integer patientId);

	public List<Obs> getObs(Patient patient, String conceptName,
			String valueConceptName, Date minDate);

	public ExpectedObs createExpectedObs(Patient patient, String conceptName,
			String valueConceptName, Integer value, Date minDate, Date dueDate,
			Date lateDate, Date maxDate, String name, String group);

	public ExpectedObs saveExpectedObs(ExpectedObs expectedObs);

    @RunAsAdminUser
    public List<Encounter> getEncounters(Patient patient,
			String encounterTypeName, Date minDate);

	public ExpectedEncounter createExpectedEncounter(Patient patient,
			String encounterTypeName, Date minDate, Date dueDate,
			Date lateDate, Date maxDate, String name, String group);

	public ExpectedEncounter saveExpectedEncounter(
			ExpectedEncounter expectedEncounter);

	public int getNumberOfObs(Integer personId, String conceptName,
			String conceptValue);

	public Date getLastObsCreationDate(Integer personId, String conceptName,
			String conceptValue);

	public Date getLastObsDate(Integer personId, String conceptName,
			String conceptValue);

	public Date getLastDoseObsDate(Integer personId, String conceptName,
			Integer doseNumber);

	public Date getLastDoseObsDateInActivePregnancy(Integer patientId,
			String conceptName, Integer doseNumber);

	@RunWithPrivileges( { OpenmrsConstants.PRIV_VIEW_CONCEPTS,
			OpenmrsConstants.PRIV_VIEW_OBS, OpenmrsConstants.PRIV_VIEW_PERSONS })
	public Date getActivePregnancyDueDate(Integer patientId);

	public Date getLastPregnancyEndDate(Integer patientId);

	public Date getLastObsValue(Integer personId, String conceptName);

	public Date getObsValue(Integer obsId);

	public void removeMessageProgramEnrollment(
			MessageProgramEnrollment enrollment);

	public String[] getActiveMessageProgramEnrollmentNames(Patient patient);

	public void scheduleInfoMessages(String messageKey, String messageKeyA,
			String messageKeyB, String messageKeyC,
			MessageProgramEnrollment enrollment, Date messageDate,
			boolean userPreferenceBased, Date currentDate);

	public ScheduledMessage scheduleCareMessage(String messageKey,
			MessageProgramEnrollment enrollment, Date messageDate,
			boolean userPreferenceBased, String care, Date currentDate);

	public void removeAllUnsentMessages(MessageProgramEnrollment enrollment);

	public void removeUnsentMessages(List<ScheduledMessage> scheduledMessages);

	public void addMessageAttempt(ScheduledMessage scheduledMessage,
			Date attemptDate, Date maxAttemptDate, boolean userPreferenceBased,
			Date currentDate);

	public Date determineUserPreferredMessageDate(Integer recipientId,
			Date messageDate);

	public void verifyMessageAttemptDate(ScheduledMessage scheduledMessage,
			boolean userPreferenceBased, Date currentDate);

	public Integer getMaxPatientCareReminders();

	@RunWithPrivileges( { OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES,
			OpenmrsConstants.PRIV_VIEW_IDENTIFIER_TYPES,
			OpenmrsConstants.PRIV_VIEW_PATIENTS,
			OpenmrsConstants.PRIV_VIEW_USERS,
			OpenmrsConstants.PRIV_VIEW_PERSONS,
			OpenmrsConstants.PRIV_VIEW_CONCEPTS, OpenmrsConstants.PRIV_VIEW_OBS })
	public void sendMessages(Date startDate, Date endDate, boolean sendImmediate);

	@RunWithPrivileges( { OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES,
			OpenmrsConstants.PRIV_VIEW_USERS })
	public void sendStaffCareMessages(Date startDate, Date endDate,
			Date deliveryDate, Date deliveryTime, String[] careGroups,
			boolean sendUpcoming, boolean avoidBlackout);

	@RunWithPrivileges( { OpenmrsConstants.PRIV_VIEW_IDENTIFIER_TYPES,
			OpenmrsConstants.PRIV_VIEW_PATIENTS,
			OpenmrsConstants.PRIV_VIEW_CONCEPTS,
			OpenmrsConstants.PRIV_VIEW_OBS, OpenmrsConstants.PRIV_VIEW_PERSONS,
			OpenmrsConstants.PRIV_VIEW_LOCATIONS,
			OpenmrsConstants.PRIV_ADD_OBS, OpenmrsConstants.PRIV_VIEW_USERS,
			OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES,
			OpenmrsConstants.PRIV_MANAGE_SCHEDULER })
	public TaskDefinition updateAllMessageProgramsState(Integer batchSize,
                                                        Long batchPreviousId);

	@RunWithPrivileges( { OpenmrsConstants.PRIV_VIEW_PATIENTS,
			OpenmrsConstants.PRIV_VIEW_IDENTIFIER_TYPES,
			OpenmrsConstants.PRIV_VIEW_CONCEPTS,
			OpenmrsConstants.PRIV_VIEW_ENCOUNTER_TYPES,
			OpenmrsConstants.PRIV_VIEW_OBS,
			OpenmrsConstants.PRIV_VIEW_ENCOUNTERS,
			OpenmrsConstants.PRIV_VIEW_PERSONS,
			OpenmrsConstants.PRIV_VIEW_LOCATIONS,
			OpenmrsConstants.PRIV_VIEW_USERS,
			OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES,
			OpenmrsConstants.PRIV_EDIT_PATIENTS })
	public void updateAllCareSchedules();

	public void demoEnrollPatient(Patient patient);

	public Facility getFacilityById(Integer facilityId);

	public Community getCommunityById(Integer communityId);

	public Community getCommunityByPatient(Patient patient);

	public boolean isValidMotechIdCheckDigit(Integer motechId);

	public boolean isValidIdCheckDigit(Integer idWithCheckDigit);

	Community saveCommunity(Community community);

	Facility saveNewFacility(Facility facility);

    public void stopEnrollmentFor(Integer patientId);

    Facility getFacilityByPatient(Patient patient);

    public Date getChildRegistrationDate();

    Facility getUnknownFacility();
}
