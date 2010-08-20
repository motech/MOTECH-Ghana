package org.motechproject.server.svc;

import java.util.Date;
import java.util.List;

import org.motechproject.server.annotation.RunAsAdminUser;
import org.motechproject.server.annotation.RunAsUserParam;
import org.motechproject.server.annotation.RunWithPrivileges;
import org.motechproject.server.model.Community;
import org.motechproject.server.model.ExpectedEncounter;
import org.motechproject.server.model.ExpectedObs;
import org.motechproject.server.model.Facility;
import org.motechproject.server.model.MessageProgramEnrollment;
import org.motechproject.server.model.ScheduledMessage;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.DayOfWeek;
import org.motechproject.ws.Gender;
import org.motechproject.ws.HIVResult;
import org.motechproject.ws.HowLearned;
import org.motechproject.ws.InterestReason;
import org.motechproject.ws.MediaType;
import org.motechproject.ws.RegistrantType;
import org.motechproject.ws.RegistrationMode;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.util.OpenmrsConstants;

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
			Boolean insured, String nhis, Date nhisExpires,
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
	public void recordMotherANCVisit(@RunAsUserParam User staff,
			Location facility, Date date, Patient patient, Integer visitNumber,
			Integer ancLocation, String house, String community,
			Date estDeliveryDate, Integer bpSystolic, Integer bpDiastolic,
			Double weight, Integer ttDose, Integer iptDose,
			Boolean iptReactive, Boolean itnUse, Double fht, Integer fhr,
			Boolean urineTestProteinPositive, Boolean urineTestGlucosePositive,
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
			Boolean lochiaAmountExcess, Double temperature, Double fht,
			String comments);

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
			Boolean csm, Boolean ipti, Boolean vitaminA, Boolean dewormer,
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
			String preferredName, Date birthDate, Integer communityId,
			String phoneNumber, String nhisNumber, String motechId);

	@RunWithPrivileges( { OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES,
			OpenmrsConstants.PRIV_VIEW_IDENTIFIER_TYPES })
	public List<Patient> getDuplicatePatients(String firstName,
			String lastName, String preferredName, Date birthDate,
			Integer communityId, String phoneNumber, String nhisNumber,
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

	public Integer getObsId(Integer personId, String conceptName,
			String conceptValue, Date earliest, Date latest);

	public Integer getObsId(Integer personId, String conceptName,
			Integer doseNumber, Date earliest, Date latest);

	public Integer getEncounterId(Integer patientId, String encounterType,
			Date earliest, Date latest);

	public void removeMessageProgramEnrollment(
			MessageProgramEnrollment enrollment);

	public String[] getActiveMessageProgramEnrollmentNames(Patient patient);

	public void scheduleInfoMessages(String messageKey, String messageKeyA,
			String messageKeyB, String messageKeyC,
			MessageProgramEnrollment enrollment, Date messageDate,
			boolean userPreferenceBased);

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

	@RunWithPrivileges( { OpenmrsConstants.PRIV_VIEW_USERS,
			OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES,
			OpenmrsConstants.PRIV_MANAGE_PERSON_ATTRIBUTE_TYPES,
			OpenmrsConstants.PRIV_VIEW_IDENTIFIER_TYPES,
			OpenmrsConstants.PRIV_MANAGE_IDENTIFIER_TYPES,
			OpenmrsConstants.PRIV_VIEW_LOCATIONS,
			OpenmrsConstants.PRIV_MANAGE_LOCATIONS,
			OpenmrsConstants.PRIV_VIEW_ENCOUNTER_TYPES,
			OpenmrsConstants.PRIV_MANAGE_ENCOUNTER_TYPES,
			OpenmrsConstants.PRIV_VIEW_CONCEPTS,
			OpenmrsConstants.PRIV_MANAGE_CONCEPTS,
			OpenmrsConstants.PRIV_VIEW_CONCEPT_DATATYPES,
			OpenmrsConstants.PRIV_VIEW_CONCEPT_CLASSES,
			OpenmrsConstants.PRIV_MANAGE_SCHEDULER,
			OpenmrsConstants.PRIV_MANAGE_GLOBAL_PROPERTIES })
	public void addInitialData();

	@RunWithPrivileges( { OpenmrsConstants.PRIV_MANAGE_SCHEDULER })
	public void removeAllTasks();

	@RunWithPrivileges( { OpenmrsConstants.PRIV_VIEW_PATIENTS,
			OpenmrsConstants.PRIV_VIEW_CONCEPTS })
	public void updateMessageProgramState(Integer personId, String conceptName);

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
			OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES })
	public void updateAllMessageProgramsState();

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
}
