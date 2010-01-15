package org.motech.svc;

import java.util.Date;
import java.util.List;

import org.motech.annotation.RunAsAdminUser;
import org.motech.annotation.RunAsUser;
import org.motech.annotation.RunAsUserParam;
import org.motech.annotation.RunWithPrivileges;
import org.motech.model.HIVStatus;
import org.motech.model.Log;
import org.motech.model.ScheduledMessage;
import org.motech.model.WhoRegistered;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.DeliveryTime;
import org.motechproject.ws.Gender;
import org.motechproject.ws.LogType;
import org.motechproject.ws.MediaType;
import org.openmrs.Encounter;
import org.openmrs.Location;
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

	@RunWithPrivileges( { OpenmrsConstants.PRIV_VIEW_PATIENTS,
			OpenmrsConstants.PRIV_VIEW_IDENTIFIER_TYPES })
	public Patient getPatientBySerial(String serialId);

	@RunWithPrivileges( { OpenmrsConstants.PRIV_VIEW_USERS,
			OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES })
	public User getNurseByCHPSId(String chpsId);

	@RunAsUser
	public void registerChild(
			@RunAsUserParam(resolverBean = "verbatimUserResolver") User nurse,
			Date regDate, Patient mother, String childRegNum, Date childDob,
			Gender childGender, String childFirstName, String nhis,
			Date nhisExpires);

	public void registerChild(String firstName, String lastName,
			String prefName, Date birthDate, Boolean birthDateEst, Gender sex,
			String motherRegNumberGHS, Boolean registeredGHS,
			String regNumberGHS, Boolean insured, String nhis,
			Date nhisExpDate, String region, String district, String community,
			String address, Integer clinic, Boolean registerPregProgram,
			String primaryPhone, ContactNumberType primaryPhoneType,
			String secondaryPhone, ContactNumberType secondaryPhoneType,
			MediaType mediaTypeInfo, MediaType mediaTypeReminder,
			String languageVoice, String languageText,
			WhoRegistered whoRegistered);

	@RunAsAdminUser
	public void registerClinic(String name, Integer parentId);

	@RunAsAdminUser
	public void registerNurse(String name, String nurseId, String phoneNumber,
			String clinicName);

	public void registerNurse(String name, String nurseId, String phoneNumber,
			Integer clinicId);

	public void registerPregnantMother(String firstName, String lastName,
			String prefName, Date birthDate, Boolean birthDateEst,
			Boolean registeredGHS, String regNumberGHS, Boolean insured,
			String nhis, Date nhisExpDate, String region, String district,
			String community, String address, Integer clinic, Date dueDate,
			Boolean dueDateConfirmed, Integer gravida, Integer parity,
			HIVStatus hivStatus, Boolean registerPregProgram,
			String primaryPhone, ContactNumberType primaryPhoneType,
			String secondaryPhone, ContactNumberType secondaryPhoneType,
			MediaType mediaTypeInfo, MediaType mediaTypeReminder,
			String languageVoice, String languageText,
			WhoRegistered whoRegistered, String religion, String occupation);

	@RunAsAdminUser
	public void registerPatient(String nursePhoneNumber, String serialId,
			String name, String community, String location, Date dateOfBirth,
			Gender gender, Integer nhis, String phoneNumber,
			ContactNumberType contactNumberType, String language,
			MediaType mediaType, DeliveryTime deliveryTime,
			String[] messagePrograms);

	public void registerPatient(Integer nurseId, String serialId, String name,
			String community, String location, Date dateOfBirth, Gender gender,
			Integer nhis, String phoneNumber,
			ContactNumberType contactNumberType, String language,
			MediaType mediaType, DeliveryTime deliveryTime,
			String[] messagePrograms);

	public void editPatient(Integer id, String firstName, String lastName,
			String prefName, Date birthDate, Boolean birthDateEst, Gender sex,
			Boolean registeredGHS, String regNumberGHS, Boolean insured,
			String nhis, Date nhisExpDate, String region, String district,
			String community, String address, Integer clinic,
			String primaryPhone, ContactNumberType primaryPhoneType,
			String secondaryPhone, ContactNumberType secondaryPhoneType,
			MediaType mediaTypeInfo, MediaType mediaTypeReminder,
			String languageVoice, String languageText,
			WhoRegistered whoRegistered);

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

	@RunAsAdminUser
	public void recordMaternalVisit(String nursePhoneNumber, Date date,
			String serialId, Boolean tetanus, Boolean ipt, Boolean itn,
			Integer visitNumber, Boolean onARV, Boolean prePMTCT,
			Boolean testPMTCT, Boolean postPMTCT, Double hemoglobinAt36Weeks);

	public void recordMaternalVisit(Integer nurseId, Date date,
			Integer patientId, Boolean tetanus, Boolean ipt, Boolean itn,
			Integer visitNumber, Boolean onARV, Boolean prePMTCT,
			Boolean testPMTCT, Boolean postPMTCT, Double hemoglobinAt36Weeks);

	@RunAsAdminUser
	public void registerPregnancy(String nursePhoneNumber, Date date,
			String serialId, Date dueDate, Integer parity, Double hemoglobin);

	public void registerPregnancy(Integer nurseId, Date date,
			Integer patientId, Date dueDate, Integer parity, Double hemoglobin);

	public void recordGeneralVisit(Integer clinicId, Date visitDate,
			String patientSerial, Gender patientGender, Date patientBirthDate,
			Integer patientDiagnosis, Boolean patientReferral);

	public void log(LogType type, String message);

	@RunWithPrivileges( { OpenmrsConstants.PRIV_VIEW_PERSONS,
			OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES })
	public void setMessageStatus(String messageId, Boolean success);

	public List<String> getActiveMessageProgramEnrollments(Integer personId);

	public User getUserByPhoneNumber(String phoneNumber);

	public List<Location> getAllClinics();

	public List<User> getAllNurses();

	public List<Patient> getAllPatients();

	public List<Encounter> getAllPregnancyVisits();

	public List<Encounter> getAllMaternalVisits();

	public List<ScheduledMessage> getAllScheduledMessages();

	public List<Log> getAllLogs();

	public Date getPatientBirthDate(Integer patientId);

	public Date getMessageProgramStartDate(Integer personId, String program);

	public int getNumberOfObs(Integer personId, String conceptName,
			String conceptValue);

	public Date getLastObsDate(Integer personId, String conceptName,
			String conceptValue);

	public Date getLastObsValue(Integer personId, String conceptName);

	public void removeMessageProgramEnrollment(Integer personId,
			String programName);

	public void scheduleMessage(String messageKey, String messageGroup,
			Integer messageRecipientId, Date messageDate,
			boolean userPreferenceBased);

	public void removeAllUnsentMessages(Integer recipientId, String messageGroup);

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
}
