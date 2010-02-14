package org.motech.svc;

import java.util.Date;
import java.util.List;

import org.motech.annotation.RunAsAdminUser;
import org.motech.annotation.RunAsUser;
import org.motech.annotation.RunAsUserParam;
import org.motech.annotation.RunWithPrivileges;
import org.motech.model.HIVStatus;
import org.motech.model.Log;
import org.motech.model.MessageProgramEnrollment;
import org.motech.model.ScheduledMessage;
import org.motech.model.WhoRegistered;
import org.motech.model.WhyInterested;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.DeliveredBy;
import org.motechproject.ws.Gender;
import org.motechproject.ws.LogType;
import org.motechproject.ws.MediaType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
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

	@RunWithPrivileges( { OpenmrsConstants.PRIV_VIEW_PATIENTS,
			OpenmrsConstants.PRIV_VIEW_IDENTIFIER_TYPES })
	public Patient getPatientBySerial(String serialId);

	@RunWithPrivileges( { OpenmrsConstants.PRIV_VIEW_USERS,
			OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES })
	public User getNurseByCHPSId(String chpsId);

	@RunAsUser
	public Patient registerChild(
			@RunAsUserParam(resolverBean = "verbatimUserResolver") User nurse,
			Date regDate, Patient mother, String childRegNum, Date childDob,
			Gender childGender, String childFirstName, String nhis,
			Date nhisExpires);

	public void registerChild(String firstName, String middleName,
			String lastName, String prefName, Date birthDate,
			Boolean birthDateEst, Gender sex, String motherRegNumberGHS,
			Boolean registeredGHS, String regNumberGHS, Boolean insured,
			String nhis, Date nhisExpDate, String region, String district,
			String community, String address, Integer clinic,
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

	public void registerPregnantMother(String firstName, String middleName,
			String lastName, String prefName, Date birthDate,
			Boolean birthDateEst, Boolean registeredGHS, String regNumberGHS,
			Boolean insured, String nhis, Date nhisExpDate, String region,
			String district, String community, String address, Integer clinic,
			Date dueDate, Boolean dueDateConfirmed, Integer gravida,
			Integer parity, HIVStatus hivStatus, Boolean registerPregProgram,
			String primaryPhone, ContactNumberType primaryPhoneType,
			String secondaryPhone, ContactNumberType secondaryPhoneType,
			MediaType mediaTypeInfo, MediaType mediaTypeReminder,
			String languageVoice, String languageText,
			WhoRegistered whoRegistered, String religion, String occupation);

	public void demoRegisterPatient(String firstName, String middleName,
			String lastName, String prefName, Date birthDate,
			Boolean birthDateEst, Gender sex, Boolean registeredGHS,
			String regNumberGHS, Boolean insured, String nhis,
			Date nhisExpDate, String region, String district, String community,
			String address, Integer clinic, Boolean registerPregProgram,
			String primaryPhone, ContactNumberType primaryPhoneType,
			String secondaryPhone, ContactNumberType secondaryPhoneType,
			MediaType mediaTypeInfo, MediaType mediaTypeReminder,
			String languageVoice, String languageText,
			WhoRegistered whoRegistered, String religion, String occupation);

	public void registerPerson(String firstName, String middleName,
			String lastName, String prefName, Date birthDate,
			Boolean birthDateEst, Gender sex, String region, String district,
			String community, String address, Integer clinic,
			Boolean registerPregProgram, Integer messagesStartWeek,
			String primaryPhone, ContactNumberType primaryPhoneType,
			String secondaryPhone, ContactNumberType secondaryPhoneType,
			MediaType mediaTypeInfo, MediaType mediaTypeReminder,
			String languageVoice, String languageText, String howLearned,
			String religion, String occupation, WhyInterested whyInterested);

	public void editPatient(Integer id, String firstName, String lastName,
			String prefName, Date birthDate, Boolean birthDateEst, Gender sex,
			Boolean registeredGHS, String regNumberGHS, Boolean insured,
			String nhis, Date nhisExpDate, String region, String district,
			String community, String address, Integer clinic,
			String primaryPhone, ContactNumberType primaryPhoneType,
			String secondaryPhone, ContactNumberType secondaryPhoneType,
			MediaType mediaTypeInfo, MediaType mediaTypeReminder,
			String languageVoice, String languageText, String religion,
			String occupation, HIVStatus hivStatus);

	@RunAsUser
	public void editPatient(
			@RunAsUserParam(resolverBean = "verbatimUserResolver") User nurse,
			Patient patient, String primaryPhone,
			ContactNumberType primaryPhoneType, String secondaryPhone,
			ContactNumberType secondaryPhoneType, String nhis, Date nhisExpires);

	@RunAsUser
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
	public void recordMotherANCVisit(String facilityId, Date date,
			Patient patient, Integer visitNumber, Integer ttDose,
			Integer iptDose, Boolean itnUse,
			org.motechproject.ws.HIVStatus hivStatus);

	@RunAsAdminUser
	public void recordPregnancyTermination(String facilityId, Date date,
			Patient patient, Integer abortionType, Integer complication);

	@RunAsAdminUser
	public void recordPregnancyDelivery(String facilityId, Date date,
			Patient patient, Integer method, Integer outcome, Integer location,
			DeliveredBy deliveredBy, Boolean maternalDeath, Integer cause,
			BirthOutcomeChild[] outcomes);

	@RunAsAdminUser
	public void recordMotherPPCVisit(String facilityId, Date date,
			Patient patient, Integer visitNumber, Boolean vitaminA,
			Integer ttDose);

	@RunAsAdminUser
	public void recordDeath(String facilityId, Date date, Patient patient,
			Integer cause);

	@RunAsAdminUser
	public void recordChildPNCVisit(String facilityId, Date date,
			Patient patient, Boolean bcg, Integer opvDose, Integer pentaDose,
			Boolean yellowFever, Boolean csm, Boolean ipti, Boolean vitaminA);

	public void recordGeneralVisit(String facilityId, Date date,
			String serialNumber, Gender sex, Date birthDate, Boolean insured,
			Boolean newCase, Integer diagnosis, Integer secondaryDiagnosis,
			Boolean referral);

	@RunAsAdminUser
	public void recordChildVisit(String facilityId, Date date, Patient patient,
			String serialNumber, Boolean newCase, Integer diagnosis,
			Integer secondDiagnosis, Boolean referral);

	@RunAsAdminUser
	public void recordMotherVisit(String facilityId, Date date,
			Patient patient, String serialNumber, Boolean newCase,
			Integer diagnosis, Integer secondDiagnosis, Boolean referral);

	public void log(LogType type, String message);

	@RunWithPrivileges( { OpenmrsConstants.PRIV_VIEW_PERSONS,
			OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES })
	public void setMessageStatus(String messageId, Boolean success);

	public User getUserByPhoneNumber(String phoneNumber);

	public List<Location> getAllLocations();

	public List<User> getAllNurses();

	public List<Patient> getAllPatients();

	public List<Person> getMatchingPeople(String firstName, String lastName,
			Date birthDate, String community, String phoneNumber,
			String patientId, String nhisNumber);

	public List<Obs> getAllPregnancies();

	public Obs getActivePregnancy(Integer patientId);

	public List<ScheduledMessage> getAllScheduledMessages();

	public List<Log> getAllLogs();

	public Date getPatientBirthDate(Integer patientId);

	public int getNumberOfObs(Integer personId, String conceptName,
			String conceptValue);

	public Date getLastObsCreationDate(Integer personId, String conceptName,
			String conceptValue);

	public Date getLastObsDate(Integer personId, String conceptName,
			String conceptValue);

	public Date getLastObsValue(Integer personId, String conceptName);

	public Date getObsValue(Integer obsId);

	public void removeMessageProgramEnrollment(
			MessageProgramEnrollment enrollment);

	public void scheduleMessage(String messageKey,
			MessageProgramEnrollment enrollment, Date messageDate,
			boolean userPreferenceBased);

	public void removeAllUnsentMessages(MessageProgramEnrollment enrollment);

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
