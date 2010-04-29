package org.motechproject.server.svc;

import java.util.Date;
import java.util.List;

import org.motechproject.server.annotation.RunAsAdminUser;
import org.motechproject.server.annotation.RunAsUserParam;
import org.motechproject.server.annotation.RunWithPrivileges;
import org.motechproject.server.model.ExpectedEncounter;
import org.motechproject.server.model.ExpectedObs;
import org.motechproject.server.model.HIVStatus;
import org.motechproject.server.model.Log;
import org.motechproject.server.model.MessageProgramEnrollment;
import org.motechproject.server.model.ScheduledMessage;
import org.motechproject.server.model.WhoRegistered;
import org.motechproject.server.model.WhyInterested;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.Gender;
import org.motechproject.ws.HIVResult;
import org.motechproject.ws.LogType;
import org.motechproject.ws.MediaType;
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
	public Patient registerChild(
			@RunAsUserParam(resolverBean = "verbatimUserResolver") User nurse,
			Patient mother, String childId, Date birthDate, Gender sex,
			String firstName, String nhis, Date nhisExpires);

	public void registerChild(String motechId, String firstName,
			String middleName, String lastName, String prefName,
			Date birthDate, Boolean birthDateEst, Gender sex,
			String motherMotechId, Boolean registeredGHS, String regNumberGHS,
			Boolean insured, String nhis, Date nhisExpDate, String region,
			String district, String community, String address, Integer clinic,
			Boolean registerPregProgram, String primaryPhone,
			ContactNumberType primaryPhoneType, String secondaryPhone,
			ContactNumberType secondaryPhoneType, MediaType mediaTypeInfo,
			MediaType mediaTypeReminder, String languageVoice,
			String languageText, WhoRegistered whoRegistered);

	@RunAsAdminUser
	public void registerClinic(String name, Integer parentId);

	@RunAsAdminUser
	public void registerNurse(String name, String nurseId, String phoneNumber,
			String clinicName);

	public void registerNurse(String name, String nurseId, String phoneNumber,
			Integer clinicId);

	public void registerPregnantMother(String motechId, String firstName,
			String middleName, String lastName, String prefName,
			Date birthDate, Boolean birthDateEst, Boolean registeredGHS,
			String regNumberGHS, Boolean insured, String nhis,
			Date nhisExpDate, String region, String district, String community,
			String address, Integer clinic, Date dueDate,
			Boolean dueDateConfirmed, Integer gravida, Integer parity,
			HIVStatus hivStatus, Boolean registerPregProgram,
			String primaryPhone, ContactNumberType primaryPhoneType,
			String secondaryPhone, ContactNumberType secondaryPhoneType,
			MediaType mediaTypeInfo, MediaType mediaTypeReminder,
			String languageVoice, String languageText,
			WhoRegistered whoRegistered, String religion, String occupation);

	public void demoRegisterPatient(String motechId, String firstName,
			String middleName, String lastName, String prefName,
			Date birthDate, Boolean birthDateEst, Gender sex,
			Boolean registeredGHS, String regNumberGHS, Boolean insured,
			String nhis, Date nhisExpDate, String region, String district,
			String community, String address, Integer clinic,
			Boolean registerPregProgram, String primaryPhone,
			ContactNumberType primaryPhoneType, String secondaryPhone,
			ContactNumberType secondaryPhoneType, MediaType mediaTypeInfo,
			MediaType mediaTypeReminder, String languageVoice,
			String languageText, WhoRegistered whoRegistered, String religion,
			String occupation);

	public void registerPerson(String motechId, String firstName,
			String middleName, String lastName, String prefName,
			Date birthDate, Boolean birthDateEst, Gender sex, String region,
			String district, String community, String address, Integer clinic,
			Boolean registerPregProgram, Integer messagesStartWeek,
			String primaryPhone, ContactNumberType primaryPhoneType,
			String secondaryPhone, ContactNumberType secondaryPhoneType,
			MediaType mediaTypeInfo, MediaType mediaTypeReminder,
			String languageVoice, String languageText, String howLearned,
			String religion, String occupation, WhyInterested whyInterested);

	public void editPatient(Integer id, String firstName, String middleName,
			String lastName, String prefName, Date birthDate,
			Boolean birthDateEst, Gender sex, Boolean registeredGHS,
			String regNumberGHS, Boolean insured, String nhis,
			Date nhisExpDate, String region, String district, String community,
			String address, Integer clinic, String primaryPhone,
			ContactNumberType primaryPhoneType, String secondaryPhone,
			ContactNumberType secondaryPhoneType, MediaType mediaTypeInfo,
			MediaType mediaTypeReminder, String languageVoice,
			String languageText, String religion, String occupation,
			HIVStatus hivStatus);

	@RunAsAdminUser
	public void editPatient(
			@RunAsUserParam(resolverBean = "verbatimUserResolver") User nurse,
			Patient patient, String primaryPhone,
			ContactNumberType primaryPhoneType, String secondaryPhone,
			ContactNumberType secondaryPhoneType, String nhis, Date nhisExpires);

	@RunAsAdminUser
	public void stopPregnancyProgram(
			@RunAsUserParam(resolverBean = "verbatimUserResolver") User nurse,
			Patient patient);

	public void registerPregnancy(Integer id, Date dueDate,
			Boolean dueDateConfirmed, Boolean registerPregProgram,
			String primaryPhone, ContactNumberType primaryPhoneType,
			String secondaryPhone, ContactNumberType secondaryPhoneType,
			MediaType mediaTypeInfo, MediaType mediaTypeReminder,
			String languageVoice, String languageText,
			WhoRegistered whoRegistered, String howLearned);

	@RunAsAdminUser
	public void recordMotherANCVisit(
			@RunAsUserParam(resolverBean = "verbatimUserResolver") User nurse,
			Date date, Patient patient, Integer visitNumber, Integer ttDose,
			Integer iptDose, Boolean itnUse, HIVResult hivResult);

	@RunAsAdminUser
	public void recordPregnancyTermination(
			@RunAsUserParam(resolverBean = "verbatimUserResolver") User nurse,
			Date date, Patient patient, Integer abortionType,
			Integer complication);

	@RunAsAdminUser
	public void recordPregnancyDelivery(
			@RunAsUserParam(resolverBean = "verbatimUserResolver") User nurse,
			Date date, Patient patient, Integer method, Integer outcome,
			Integer location, Integer deliveredBy, Boolean maternalDeath,
			Integer cause, List<BirthOutcomeChild> outcomes);

	@RunAsAdminUser
	public void recordMotherPPCVisit(
			@RunAsUserParam(resolverBean = "verbatimUserResolver") User nurse,
			Date date, Patient patient, Integer visitNumber, Boolean vitaminA,
			Integer ttDose);

	@RunAsAdminUser
	public void recordDeath(
			@RunAsUserParam(resolverBean = "verbatimUserResolver") User nurse,
			Date date, Patient patient, Integer cause);

	@RunAsAdminUser
	public void recordChildPNCVisit(
			@RunAsUserParam(resolverBean = "verbatimUserResolver") User nurse,
			Date date, Patient patient, Boolean bcg, Integer opvDose,
			Integer pentaDose, Boolean yellowFever, Boolean csm,
			Boolean measles, Boolean ipti, Boolean vitaminA);

	public void recordGeneralVisit(String chpsId, Date date,
			String serialNumber, Gender sex, Date birthDate, Boolean insured,
			Boolean newCase, Integer diagnosis, Integer secondaryDiagnosis,
			Boolean referred);

	@RunAsAdminUser
	public void recordChildVisit(
			@RunAsUserParam(resolverBean = "verbatimUserResolver") User nurse,
			Date date, Patient patient, String serialNumber, Boolean newCase,
			Integer diagnosis, Integer secondDiagnosis, Boolean referred);

	@RunAsAdminUser
	public void recordMotherVisit(
			@RunAsUserParam(resolverBean = "verbatimUserResolver") User nurse,
			Date date, Patient patient, String serialNumber, Boolean newCase,
			Integer diagnosis, Integer secondDiagnosis, Boolean referred);

	public void log(LogType type, String message);

	@RunWithPrivileges( { OpenmrsConstants.PRIV_VIEW_PERSONS,
			OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES })
	public void setMessageStatus(String messageId, Boolean success);

	public User getUserByPhoneNumber(String phoneNumber);

	public List<Location> getAllLocations();

	public List<User> getAllNurses();

	public List<Patient> getAllPatients();

	@RunWithPrivileges( { OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES,
			OpenmrsConstants.PRIV_VIEW_IDENTIFIER_TYPES })
	public List<Patient> getPatients(String firstName, String lastName,
			String preferredName, Date birthDate, String community,
			String phoneNumber, String nhisNumber, String motechId);

	public List<Obs> getAllPregnancies();

	public List<ExpectedEncounter> getUpcomingExpectedEncounters(Patient patient);

	public List<ExpectedObs> getUpcomingExpectedObs(Patient patient);

	public List<ExpectedEncounter> getDefaultedExpectedEncounters(
			String[] groups);

	public List<ExpectedObs> getDefaultedExpectedObs(String[] groups);

	public List<ExpectedEncounter> getUpcomingExpectedEncounters(
			String[] groups, Date fromDate, Date toDate);

	public List<ExpectedObs> getUpcomingExpectedObs(String[] groups,
			Date fromDate, Date toDate);

	public List<ExpectedEncounter> getDefaultedExpectedEncounters(
			String[] groups, Date forDate);

	public List<ExpectedObs> getDefaultedExpectedObs(String[] groups,
			Date forDate);

	public List<ExpectedEncounter> getExpectedEncounters(Patient patient);

	public List<ExpectedObs> getExpectedObs(Patient patient);

	@RunWithPrivileges( { OpenmrsConstants.PRIV_VIEW_ENCOUNTER_TYPES,
			OpenmrsConstants.PRIV_VIEW_ENCOUNTERS })
	public List<Encounter> getRecentDeliveries();

	@RunWithPrivileges( { OpenmrsConstants.PRIV_VIEW_ENCOUNTER_TYPES,
			OpenmrsConstants.PRIV_VIEW_ENCOUNTERS })
	public Date getCurrentDeliveryDate(Patient patient);

	@RunWithPrivileges( { OpenmrsConstants.PRIV_VIEW_CONCEPTS })
	public List<Obs> getUpcomingPregnanciesDueDate();

	@RunWithPrivileges( { OpenmrsConstants.PRIV_VIEW_CONCEPTS })
	public List<Obs> getOverduePregnanciesDueDate();

	@RunWithPrivileges( { OpenmrsConstants.PRIV_VIEW_PATIENTS })
	public Patient getPatientById(Integer patientId);

	public Obs getActivePregnancy(Integer patientId);

	public List<ScheduledMessage> getAllScheduledMessages();

	public List<ScheduledMessage> getScheduledMessages(
			MessageProgramEnrollment enrollment);

	public List<Log> getAllLogs();

	public Date getPatientBirthDate(Integer patientId);

	public List<Obs> getObs(Patient patient, String conceptName,
			String valueConceptName, Date minDate);

	public List<ExpectedObs> getExpectedObs(Patient patient, String group);

	public ExpectedObs createExpectedObs(Patient patient, String conceptName,
			String valueConceptName, Integer value, Date minDate, Date dueDate,
			Date lateDate, Date maxDate, String name, String group);

	public ExpectedObs saveExpectedObs(ExpectedObs expectedObs);

	public List<Encounter> getEncounters(Patient patient,
			String encounterTypeName, Date minDate);

	public List<ExpectedEncounter> getExpectedEncounters(Patient patient,
			String group);

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

	public void scheduleMessage(String messageKey,
			MessageProgramEnrollment enrollment, Date messageDate,
			boolean userPreferenceBased);

	public ScheduledMessage scheduleCareMessage(String messageKey,
			MessageProgramEnrollment enrollment, Date messageDate,
			boolean userPreferenceBased, String care);

	public void removeAllUnsentMessages(MessageProgramEnrollment enrollment);

	public void removeUnsentMessages(List<ScheduledMessage> scheduledMessages);

	public void addMessageAttempt(ScheduledMessage scheduledMessage,
			Date attemptDate);

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
	public void sendNurseCareMessages(Date startDate, Date endDate,
			Date deliveryDate, String[] careGroups, boolean sendUpcoming);

	@RunWithPrivileges( { OpenmrsConstants.PRIV_VIEW_IDENTIFIER_TYPES,
			OpenmrsConstants.PRIV_VIEW_PATIENTS,
			OpenmrsConstants.PRIV_VIEW_CONCEPTS,
			OpenmrsConstants.PRIV_VIEW_OBS, OpenmrsConstants.PRIV_VIEW_PERSONS,
			OpenmrsConstants.PRIV_VIEW_LOCATIONS,
			OpenmrsConstants.PRIV_ADD_OBS, OpenmrsConstants.PRIV_VIEW_USERS,
			OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES })
	public void updateAllMessageProgramsState();

	public void demoEnrollPatient(String regNumberGHS);
}
