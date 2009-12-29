package org.motech.svc.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motech.event.MessageProgram;
import org.motech.messaging.MessageNotFoundException;
import org.motech.model.GeneralPatientEncounter;
import org.motech.model.HIVStatus;
import org.motech.model.Message;
import org.motech.model.MessageAttribute;
import org.motech.model.MessageDefinition;
import org.motech.model.MessageProgramEnrollment;
import org.motech.model.MessageStatus;
import org.motech.model.MessageType;
import org.motech.model.ScheduledMessage;
import org.motech.model.TroubledPhone;
import org.motech.model.WhoRegistered;
import org.motech.openmrs.module.ContextService;
import org.motech.openmrs.module.MotechService;
import org.motech.openmrs.module.tasks.MessageProgramUpdateTask;
import org.motech.openmrs.module.tasks.NotificationTask;
import org.motech.svc.RegistrarBean;
import org.motech.util.GenderTypeConverter;
import org.motech.util.MotechConstants;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.DeliveryTime;
import org.motechproject.ws.Gender;
import org.motechproject.ws.LogType;
import org.motechproject.ws.MediaType;
import org.motechproject.ws.NameValuePair;
import org.motechproject.ws.mobile.MessageService;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNameTag;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.LocationService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.SchedulerService;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.util.OpenmrsConstants;

/**
 * An implementation of the RegistrarBean interface, implemented using a mix of
 * OpenMRS and module defined services.
 */
public class RegistrarBeanImpl implements RegistrarBean {

	private static Log log = LogFactory.getLog(RegistrarBeanImpl.class);

	private ContextService contextService;
	public MessageService mobileService;
	private Map<String, MessageProgram> messagePrograms;

	public void setContextService(ContextService contextService) {
		this.contextService = contextService;
	}

	public void setMobileService(MessageService mobileService) {
		this.mobileService = mobileService;
	}

	public void setMessagePrograms(Map<String, MessageProgram> messagePrograms) {
		this.messagePrograms = messagePrograms;
	}

	public MessageProgram getMessageProgram(String programName) {
		return messagePrograms.get(programName);
	}

	public void registerChild(User nurse, Date regDate, Patient mother,
			String childRegNum, Date childDob, Gender childGender,
			String childFirstName, String nhis, Date nhisExpires) {

		PatientService patientService = contextService.getPatientService();

		PatientIdentifierType patientIdType = getGhanaPatientIdType();

		Location motherLocation = mother.getPatientIdentifier().getLocation();

		Patient child = new Patient();
		PatientIdentifier childIdObj = new PatientIdentifier(childRegNum,
				patientIdType, motherLocation);
		child.addIdentifier(childIdObj);

		child.setBirthdate(childDob);
		child.setGender(GenderTypeConverter.toOpenMRSString(childGender));
		child.addName(new PersonName(childFirstName, null, mother
				.getFamilyName()));

		PersonAttributeType nhisAttrType = getNHISNumberAttributeType();
		PersonAttribute nhisAttr = new PersonAttribute(nhisAttrType, nhis);
		child.addAttribute(nhisAttr);

		PersonAttributeType nhisExprAttrType = getNHISExpirationDateAttributeType();
		PersonAttribute nhisExprAttr = new PersonAttribute(nhisExprAttrType,
				nhisExpires.toString());
		child.addAttribute(nhisExprAttr);

		PersonAttributeType whoRegisteredAttrType = getWhoRegisteredAttributeType();
		child.addAttribute(new PersonAttribute(whoRegisteredAttrType,
				WhoRegistered.CHPS_STAFF.name()));

		PersonAddress motherAddress = mother.getPersonAddress();
		if (motherAddress != null) {
			PersonAddress childAddress = new PersonAddress();
			// TODO: Consider factoring this out to a copy utility
			childAddress.setRegion(motherAddress.getRegion());
			childAddress.setCountyDistrict(motherAddress.getCountyDistrict());
			childAddress.setAddress1(motherAddress.getAddress1());
			childAddress.setCityVillage(motherAddress.getCityVillage());
			child.addAddress(childAddress);
		}

		patientService.savePatient(child);
	}

	public void registerClinic(String name, Integer parentId) {

		LocationService locationService = contextService.getLocationService();

		Location clinic = new Location();
		clinic.setName(name);
		clinic.setDescription("A Ghana Clinic Location");
		clinic = locationService.saveLocation(clinic);

		if (parentId != null) {
			Location parent = locationService.getLocation(parentId);
			parent.addChildLocation(clinic);
			locationService.saveLocation(parent);
		}
	}

	public void registerNurse(String name, String nurseId, String phoneNumber,
			String clinicName) {
		LocationService locationService = contextService.getLocationService();
		Location clinic = locationService.getLocation(clinicName);

		registerNurse(name, nurseId, phoneNumber, clinic);
	}

	public void registerNurse(String name, String nurseId, String phoneNumber,
			Integer clinicId) {
		LocationService locationService = contextService.getLocationService();
		Location clinic = locationService.getLocation(clinicId);

		registerNurse(name, nurseId, phoneNumber, clinic);
	}

	private void registerNurse(String name, String nurseId, String phoneNumber,
			Location clinic) {

		UserService userService = contextService.getUserService();
		PersonService personService = contextService.getPersonService();

		// User creating other users must have atleast the Privileges to be
		// given

		// TODO: Create nurses as person and use same User for all actions ?
		User nurse = new User();
		nurse.setUsername(name);

		// TODO: Nurse gender hardcoded, required for Person
		nurse.setGender(GenderTypeConverter.toOpenMRSString(Gender.FEMALE));

		nurse.addName(personService.parsePersonName(name));

		PersonAttributeType nurseIdAttrType = getNurseIdAttributeType();
		nurse.addAttribute(new PersonAttribute(nurseIdAttrType, nurseId));

		// Must be created previously through API or UI to lookup
		PersonAttributeType phoneNumberAttrType = getPrimaryPhoneNumberAttributeType();
		nurse
				.addAttribute(new PersonAttribute(phoneNumberAttrType,
						phoneNumber));

		// TODO: Create Nurse role with proper privileges
		Role role = userService.getRole(OpenmrsConstants.PROVIDER_ROLE);
		nurse.addRole(role);

		// TODO: Clinic not used, no connection currently between Nurse and
		// Clinic
		PersonAttributeType clinicType = getClinicAttributeType();
		nurse.addAttribute(new PersonAttribute(clinicType, clinic
				.getLocationId().toString()));

		userService.saveUser(nurse, "password");
	}

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
			WhoRegistered whoRegistered, String religion, String occupation) {

		PatientService patientService = contextService.getPatientService();
		EncounterService encounterService = contextService
				.getEncounterService();
		ObsService obsService = contextService.getObsService();

		User nurse = contextService.getAuthenticatedUser();

		Patient mother = new Patient();

		PatientIdentifierType ghanaSerialIdType = getGhanaPatientIdType();

		Location ghanaLocation = getGhanaLocation();
		mother.addIdentifier(new PatientIdentifier(regNumberGHS,
				ghanaSerialIdType, ghanaLocation));

		// Storing preferred name as middle name
		mother.addName(new PersonName(firstName, prefName, lastName));
		mother.setGender(GenderTypeConverter.toOpenMRSString(Gender.FEMALE));
		mother.setBirthdate(birthDate);
		mother.setBirthdateEstimated(birthDateEst);

		PersonAddress motherAddress = new PersonAddress();
		motherAddress.setAddress1(address);
		motherAddress.setCityVillage(community);
		motherAddress.setCountyDistrict(district);
		motherAddress.setRegion(region);
		mother.addAddress(motherAddress);

		PersonAttributeType registeredGHSAttrType = getGHSRegisteredAttributeType();
		mother.addAttribute(new PersonAttribute(registeredGHSAttrType,
				registeredGHS.toString()));

		PersonAttributeType ancRegNumAttrType = getANCRegistrationNumberAttributeType();
		mother
				.addAttribute(new PersonAttribute(ancRegNumAttrType,
						regNumberGHS));

		PersonAttributeType insuredAttrType = getInsuredAttributeType();
		mother.addAttribute(new PersonAttribute(insuredAttrType, insured
				.toString()));

		PersonAttributeType nhisAttrType = getNHISNumberAttributeType();
		mother.addAttribute(new PersonAttribute(nhisAttrType, nhis.toString()));

		PersonAttributeType nhisExpDateAttrType = getNHISExpirationDateAttributeType();
		mother.addAttribute(new PersonAttribute(nhisExpDateAttrType,
				nhisExpDate.toString()));

		PersonAttributeType clinicAttrType = getClinicAttributeType();
		mother.addAttribute(new PersonAttribute(clinicAttrType, clinic
				.toString()));

		PersonAttributeType hivStatusAttrType = this
				.getHIVStatusAttributeType();
		mother.addAttribute(new PersonAttribute(hivStatusAttrType, hivStatus
				.name()));

		PersonAttributeType primaryPhoneAttrType = getPrimaryPhoneNumberAttributeType();
		mother.addAttribute(new PersonAttribute(primaryPhoneAttrType,
				primaryPhone));

		PersonAttributeType primaryPhoneTypeAttrType = getPrimaryPhoneTypeAttributeType();
		mother.addAttribute(new PersonAttribute(primaryPhoneTypeAttrType,
				primaryPhoneType.name()));

		PersonAttributeType secondaryPhoneAttrType = getSecondaryPhoneNumberAttributeType();
		mother.addAttribute(new PersonAttribute(secondaryPhoneAttrType,
				secondaryPhone));

		PersonAttributeType secondaryPhoneTypeAttrType = getSecondaryPhoneTypeAttributeType();
		mother.addAttribute(new PersonAttribute(secondaryPhoneTypeAttrType,
				secondaryPhoneType.name()));

		PersonAttributeType mediaTypeInfoAttrType = getMediaTypeInformationalAttributeType();
		mother.addAttribute(new PersonAttribute(mediaTypeInfoAttrType,
				mediaTypeInfo.name()));

		PersonAttributeType mediaTypeReminderAttrType = getMediaTypeReminderAttributeType();
		mother.addAttribute(new PersonAttribute(mediaTypeReminderAttrType,
				mediaTypeReminder.name()));

		PersonAttributeType languageTextAttrType = getLanguageTextAttributeType();
		mother.addAttribute(new PersonAttribute(languageTextAttrType,
				languageText));

		PersonAttributeType languageVoiceAttrType = getLanguageVoiceAttributeType();
		mother.addAttribute(new PersonAttribute(languageVoiceAttrType,
				languageVoice));

		PersonAttributeType whoRegisteredAttrType = this
				.getWhoRegisteredAttributeType();
		mother.addAttribute(new PersonAttribute(whoRegisteredAttrType,
				whoRegistered.name()));

		PersonAttributeType religionAttrType = this.getReligionAttributeType();
		mother.addAttribute(new PersonAttribute(religionAttrType, religion));

		PersonAttributeType occupationAttrType = this
				.getOccupationAttributeType();
		mother
				.addAttribute(new PersonAttribute(occupationAttrType,
						occupation));

		mother = patientService.savePatient(mother);

		if (registerPregProgram) {
			addMessageProgramEnrollment(mother.getPatientId(),
					"Weekly Pregnancy Message Program");
		}

		Encounter pregnancy = new Encounter();
		// Date of encounter is current date
		Date encounterDate = new Date();

		pregnancy.setEncounterDatetime(encounterDate);
		pregnancy.setPatient(mother);
		// Nurse on encounter is current authenticated user
		pregnancy.setProvider(nurse);

		EncounterType pregEncounterType = getPregnancyVisitEncounterType();
		pregnancy.setEncounterType(pregEncounterType);
		// Location on encounter and observations is ghana
		pregnancy.setLocation(ghanaLocation);
		pregnancy = encounterService.saveEncounter(pregnancy);

		Obs dueDateObs = createDateValueObs(encounterDate, getDueDateConcept(),
				mother, ghanaLocation, dueDate, pregnancy, null);
		obsService.saveObs(dueDateObs, null);

		Obs dueDateConfirmedObs = createBooleanValueObs(encounterDate,
				getDueDateConfirmedConcept(), mother, ghanaLocation,
				dueDateConfirmed, pregnancy, null);
		obsService.saveObs(dueDateConfirmedObs, null);

		Obs gravidaObs = createNumericValueObs(encounterDate,
				getGravidaConcept(), mother, ghanaLocation,
				new Double(gravida), pregnancy, null);
		obsService.saveObs(gravidaObs, null);

		Obs parityObs = createNumericValueObs(encounterDate,
				getParityConcept(), mother, ghanaLocation, new Double(parity),
				pregnancy, null);
		obsService.saveObs(parityObs, null);
	}

	public void registerPatient(String nursePhoneNumber, String serialId,
			String name, String community, String location, Date dateOfBirth,
			Gender gender, Integer nhis, String phoneNumber,
			ContactNumberType contactNumberType, String language,
			MediaType mediaType, DeliveryTime deliveryTime,
			String[] messagePrograms) {

		User nurse = this.getUserByPhoneNumber(nursePhoneNumber);
		registerPatient(nurse, serialId, name, community, location,
				dateOfBirth, gender, nhis, phoneNumber, contactNumberType,
				language, mediaType, deliveryTime, messagePrograms);
	}

	public void registerPatient(Integer nurseId, String serialId, String name,
			String community, String location, Date dateOfBirth, Gender gender,
			Integer nhis, String phoneNumber,
			ContactNumberType contactNumberType, String language,
			MediaType mediaType, DeliveryTime deliveryTime,
			String[] messagePrograms) {

		UserService userService = contextService.getUserService();
		User nurse = userService.getUser(nurseId);
		registerPatient(nurse, serialId, name, community, location,
				dateOfBirth, gender, nhis, phoneNumber, contactNumberType,
				language, mediaType, deliveryTime, messagePrograms);
	}

	private void registerPatient(User nurse, String serialId, String name,
			String community, String location, Date dateOfBirth, Gender gender,
			Integer nhis, String phoneNumber,
			ContactNumberType contactNumberType, String language,
			MediaType mediaType, DeliveryTime deliveryTime,
			String[] messagePrograms) {

		PatientService patientService = contextService.getPatientService();
		PersonService personService = contextService.getPersonService();

		Patient patient = new Patient();

		// Must be created previously through API or UI to lookup
		PatientIdentifierType serialIdType = getGhanaPatientIdType();

		Location clinicLocation = getNurseClinicLocation(nurse);
		patient.addIdentifier(new PatientIdentifier(serialId, serialIdType,
				clinicLocation));

		patient.addName(personService.parsePersonName(name));

		PersonAddress address = new PersonAddress();
		address.setAddress1(location);
		address.setCityVillage(community);
		patient.addAddress(address);

		patient.setBirthdate(dateOfBirth);

		// Should be "M" or "F"
		patient.setGender(GenderTypeConverter.toOpenMRSString(gender));

		// Must be created previously through API or UI to lookup
		PersonAttributeType nhisAttrType = getNHISNumberAttributeType();
		patient
				.addAttribute(new PersonAttribute(nhisAttrType, nhis.toString()));

		// Must be created previously through API or UI to lookup
		PersonAttributeType phoneNumberAttrType = getPrimaryPhoneNumberAttributeType();
		patient.addAttribute(new PersonAttribute(phoneNumberAttrType,
				phoneNumber));

		PersonAttributeType phoneTypeAttrType = getPrimaryPhoneTypeAttributeType();

		patient.addAttribute(new PersonAttribute(phoneTypeAttrType,
				contactNumberType.toString()));

		PersonAttributeType languageTextAttrType = getLanguageTextAttributeType();
		patient
				.addAttribute(new PersonAttribute(languageTextAttrType,
						language));

		PersonAttributeType languageVoiceAttrType = getLanguageVoiceAttributeType();
		patient.addAttribute(new PersonAttribute(languageVoiceAttrType,
				language));

		PersonAttributeType mediaTypeInfoAttrType = getMediaTypeInformationalAttributeType();
		patient.addAttribute(new PersonAttribute(mediaTypeInfoAttrType,
				mediaType.toString()));

		PersonAttributeType mediaTypeReminderAttrType = getMediaTypeReminderAttributeType();
		patient.addAttribute(new PersonAttribute(mediaTypeReminderAttrType,
				mediaType.toString()));

		PersonAttributeType deliveryAttrType = getDeliveryTimeAttributeType();
		patient.addAttribute(new PersonAttribute(deliveryAttrType, deliveryTime
				.toString()));

		patient = patientService.savePatient(patient);

		for (String programName : messagePrograms) {
			addMessageProgramEnrollment(patient.getPatientId(), programName);
		}
	}

	public void editPatient(User nurse, Patient patient, String primaryPhone,
			ContactNumberType primaryPhoneType, String secondaryPhone,
			ContactNumberType secondaryPhoneType, String nhis, Date nhisExpires) {

		PatientService patientService = contextService.getPatientService();

		if (primaryPhone != null) {
			PersonAttributeType primaryPhoneAttrType = getPrimaryPhoneNumberAttributeType();
			patient.addAttribute(new PersonAttribute(primaryPhoneAttrType,
					primaryPhone));
		}

		if (primaryPhoneType != null) {
			PersonAttributeType primaryPhoneTypeAttrType = getPrimaryPhoneTypeAttributeType();
			patient.addAttribute(new PersonAttribute(primaryPhoneTypeAttrType,
					primaryPhoneType.toString()));
		}

		if (secondaryPhone != null) {
			PersonAttributeType secondaryPhoneAttrType = getSecondaryPhoneNumberAttributeType();
			patient.addAttribute(new PersonAttribute(secondaryPhoneAttrType,
					secondaryPhone));
		}

		if (secondaryPhoneType != null) {
			PersonAttributeType secondaryPhoneTypeAttrType = getSecondaryPhoneTypeAttributeType();
			patient.addAttribute(new PersonAttribute(
					secondaryPhoneTypeAttrType, secondaryPhoneType.toString()));
		}

		if (nhis != null) {
			PersonAttributeType nhisAttrType = getNHISNumberAttributeType();
			patient.addAttribute(new PersonAttribute(nhisAttrType, nhis));
		}

		if (nhisExpires != null) {
			PersonAttributeType nhisExpDateAttrType = getNHISExpirationDateAttributeType();
			patient.addAttribute(new PersonAttribute(nhisExpDateAttrType,
					nhisExpires.toString()));
		}

		patientService.savePatient(patient);
	}

	public void stopPregnancyProgram(User nurse, Patient patient) {

		String[] pregnancyPrograms = { "Weekly Pregnancy Message Program",
				"Weekly Info Pregnancy Message Program" };

		Integer patientId = patient.getPatientId();

		for (String programName : pregnancyPrograms) {
			removeMessageProgramEnrollment(patientId, programName);
		}
	}

	public void recordMaternalVisit(String nursePhoneNumber, Date date,
			String serialId, Boolean tetanus, Boolean ipt, Boolean itn,
			Integer visitNumber, Boolean onARV, Boolean prePMTCT,
			Boolean testPMTCT, Boolean postPMTCT, Double hemoglobinAt36Weeks) {

		User nurse = this.getUserByPhoneNumber(nursePhoneNumber);
		Patient patient = getPatientBySerial(serialId);
		recordMaternalVisit(nurse, date, patient, tetanus, ipt, itn,
				visitNumber, onARV, prePMTCT, testPMTCT, postPMTCT,
				hemoglobinAt36Weeks);
	}

	public void recordMaternalVisit(Integer nurseId, Date date,
			Integer patientId, Boolean tetanus, Boolean ipt, Boolean itn,
			Integer visitNumber, Boolean onARV, Boolean prePMTCT,
			Boolean testPMTCT, Boolean postPMTCT, Double hemoglobinAt36Weeks) {

		UserService userService = contextService.getUserService();
		PatientService patientService = contextService.getPatientService();

		User nurse = userService.getUser(nurseId);
		Patient patient = patientService.getPatient(patientId);
		recordMaternalVisit(nurse, date, patient, tetanus, ipt, itn,
				visitNumber, onARV, prePMTCT, testPMTCT, postPMTCT,
				hemoglobinAt36Weeks);
	}

	private void recordMaternalVisit(User nurse, Date date, Patient patient,
			Boolean tetanus, Boolean ipt, Boolean itn, Integer visitNumber,
			Boolean onARV, Boolean prePMTCT, Boolean testPMTCT,
			Boolean postPMTCT, Double hemoglobinAt36Weeks) {

		EncounterService encounterService = contextService
				.getEncounterService();
		ObsService obsService = contextService.getObsService();

		Date visitDate = date;
		if (visitDate == null) {
			visitDate = new Date();
		}

		Encounter encounter = new Encounter();
		encounter.setEncounterDatetime(visitDate);
		encounter.setPatient(patient);
		encounter.setProvider(nurse);

		Location clinicLocation = getNurseClinicLocation(nurse);

		// Encounter types must be created previously
		EncounterType encounterType = getMaternalVisitEncounterType();
		encounter.setEncounterType(encounterType);
		encounter.setLocation(clinicLocation);
		encounter = encounterService.saveEncounter(encounter);

		if (tetanus) {
			Obs tetanusObs = createConceptValueObs(visitDate,
					getImmunizationsOrderedConcept(), patient, clinicLocation,
					getTetanusConcept(), encounter, null);
			obsService.saveObs(tetanusObs, null);
		}

		// TODO: Add IPT to proper Concept as an Answer, not an immunization
		if (ipt) {
			Obs iptObs = createConceptValueObs(visitDate,
					getImmunizationsOrderedConcept(), patient, clinicLocation,
					getIPTConcept(), encounter, null);
			obsService.saveObs(iptObs, null);
		}

		if (itn) {
			Obs itnObs = createBooleanValueObs(visitDate, getITNConcept(),
					patient, clinicLocation, itn, encounter, null);
			obsService.saveObs(itnObs, null);
		}

		Obs visitNumberObs = createNumericValueObs(visitDate,
				getPregnancyVisitNumberConcept(), patient, clinicLocation,
				new Double(visitNumber), encounter, null);
		obsService.saveObs(visitNumberObs, null);

		if (onARV) {
			Obs arvObs = createConceptValueObs(visitDate, getARVConcept(),
					patient, clinicLocation, getOnARVConcept(), encounter, null);
			obsService.saveObs(arvObs, null);
		}

		if (prePMTCT) {
			Obs prePmtctObs = createBooleanValueObs(visitDate,
					getPrePMTCTConcept(), patient, clinicLocation, prePMTCT,
					encounter, null);
			obsService.saveObs(prePmtctObs, null);
		}

		if (testPMTCT) {
			Obs testPmtctObs = createBooleanValueObs(visitDate,
					getTestPMTCTConcept(), patient, clinicLocation, testPMTCT,
					encounter, null);
			obsService.saveObs(testPmtctObs, null);
		}

		if (postPMTCT) {
			Obs postPmtctObs = createBooleanValueObs(visitDate,
					getPostPMTCTConcept(), patient, clinicLocation, postPMTCT,
					encounter, null);
			obsService.saveObs(postPmtctObs, null);
		}

		Obs hemoglobinObs = createNumericValueObs(visitDate,
				getHemoglobin36WeeksConcept(), patient, clinicLocation,
				hemoglobinAt36Weeks, encounter, null);
		obsService.saveObs(hemoglobinObs, null);
	}

	public void registerPregnancy(String nursePhoneNumber, Date date,
			String serialId, Date dueDate, Integer parity, Double hemoglobin) {

		User nurse = this.getUserByPhoneNumber(nursePhoneNumber);
		Patient patient = getPatientBySerial(serialId);
		registerPregnancy(nurse, date, patient, dueDate, parity, hemoglobin);
	}

	public void registerPregnancy(Integer nurseId, Date date,
			Integer patientId, Date dueDate, Integer parity, Double hemoglobin) {

		UserService userService = contextService.getUserService();
		PatientService patientService = contextService.getPatientService();

		User nurse = userService.getUser(nurseId);
		Patient patient = patientService.getPatient(patientId);
		registerPregnancy(nurse, date, patient, dueDate, parity, hemoglobin);
	}

	private void registerPregnancy(User nurse, Date date, Patient patient,
			Date dueDate, Integer parity, Double hemoglobin) {

		EncounterService encounterService = contextService
				.getEncounterService();
		ObsService obsService = contextService.getObsService();

		Date visitDate = date;
		if (visitDate == null) {
			visitDate = new Date();
		}

		Encounter encounter = new Encounter();
		encounter.setEncounterDatetime(visitDate);
		encounter.setPatient(patient);
		encounter.setProvider(nurse);

		Location clinicLocation = getNurseClinicLocation(nurse);

		// Encounter types must be created previously
		EncounterType encounterType = getPregnancyVisitEncounterType();
		encounter.setEncounterType(encounterType);
		encounter.setLocation(clinicLocation);
		encounter = encounterService.saveEncounter(encounter);

		Obs pregSatusObs = createBooleanValueObs(visitDate,
				getPregnancyStatusConcept(), patient, clinicLocation,
				Boolean.TRUE, encounter, null);
		obsService.saveObs(pregSatusObs, null);

		Obs dueDateObs = createDateValueObs(visitDate, getDueDateConcept(),
				patient, clinicLocation, dueDate, encounter, null);
		obsService.saveObs(dueDateObs, null);

		Obs parityObs = createNumericValueObs(visitDate, getParityConcept(),
				patient, clinicLocation, new Double(parity), encounter, null);
		obsService.saveObs(parityObs, null);

		Obs hemoglobinObs = createNumericValueObs(visitDate,
				getHemoglobinConcept(), patient, clinicLocation, hemoglobin,
				encounter, null);
		obsService.saveObs(hemoglobinObs, null);
	}

	public void recordGeneralVisit(Integer clinicId, Date visitDate,
			String patientSerial, Gender patientGender, Date patientBirthDate,
			Integer patientDiagnosis, Boolean patientReferral) {

		log.debug("Date: " + visitDate + ", Clinic: " + clinicId + ", Serial: "
				+ patientSerial + ", Gender: " + patientGender
				+ ", Birthdate: " + patientBirthDate + ", Diagnosis: "
				+ patientDiagnosis + ", Referral: " + patientReferral);

		MotechService motechService = contextService.getMotechService();

		GeneralPatientEncounter encounter = new GeneralPatientEncounter();
		encounter.setClinicId(clinicId);
		encounter.setPatientSerial(patientSerial);
		encounter.setPatientGender(patientGender);
		encounter.setPatientBirthDate(patientBirthDate);
		encounter.setPatientDiagnosis(patientDiagnosis);
		encounter.setPatientReferral(patientReferral);
		encounter.setEncounterDate(visitDate);

		motechService.saveGeneralPatientEncounter(encounter);
	}

	public void log(LogType type, String message) {

		log.debug("log WS: type: " + type + ", message: " + message);

		String limitedMessage = message;
		if (limitedMessage.length() > 255) {
			limitedMessage = limitedMessage.substring(0, 255);
			log.debug("log WS: trimmed message: " + limitedMessage);
		}

		MotechService motechService = contextService.getMotechService();

		org.motech.model.Log log = new org.motech.model.Log();
		log.setDate(new Date());
		log.setType(type);
		log.setMessage(limitedMessage);
		motechService.saveLog(log);
	}

	public void setMessageStatus(String messageId, Boolean success) {

		log.debug("setMessageStatus WS: messageId: " + messageId
				+ ", success: " + success);

		MotechService motechService = contextService.getMotechService();
		PersonService personService = contextService.getPersonService();

		Message message = motechService.getMessage(messageId);
		if (message == null) {
			throw new MessageNotFoundException();
		}

		Integer recipientId = message.getSchedule().getRecipientId();
		Person messageRecipient = personService.getPerson(recipientId);
		String phoneNumber = getPrimaryPersonPhoneNumber(messageRecipient);
		TroubledPhone troubledPhone = motechService
				.getTroubledPhone(phoneNumber);

		if (success) {
			message.setAttemptStatus(MessageStatus.DELIVERED);

			if (troubledPhone != null) {
				motechService.removeTroubledPhone(phoneNumber);
			}
		} else {
			message.setAttemptStatus(MessageStatus.ATTEMPT_FAIL);

			if (troubledPhone == null) {
				motechService.addTroubledPhone(phoneNumber);
			} else {
				Integer sendFailures = troubledPhone.getSendFailures() + 1;
				troubledPhone.setSendFailures(sendFailures);
				motechService.saveTroubledPhone(troubledPhone);
			}
		}
		motechService.saveMessage(message);
	}

	/* MotechService methods start */
	public List<String> getActiveMessageProgramEnrollments(Integer personId) {
		MotechService motechService = contextService.getMotechService();

		return motechService.getActiveMessageProgramEnrollments(personId);
	}

	public User getUserByPhoneNumber(String phoneNumber) {
		MotechService motechService = contextService.getMotechService();
		UserService userService = contextService.getUserService();

		PersonAttributeType phoneAttributeType = getPrimaryPhoneNumberAttributeType();
		List<Integer> matchingUsers = motechService
				.getUserIdsByPersonAttribute(phoneAttributeType, phoneNumber);
		if (matchingUsers.size() > 0) {
			if (matchingUsers.size() > 1) {
				log.warn("Multiple Nurses found for phone number: "
						+ phoneNumber);
			}
			// If more than one user matches phone number, first user in list is
			// returned
			Integer userId = matchingUsers.get(0);
			return userService.getUser(userId);
		}
		log.warn("No Nurse found for phone number: " + phoneNumber);
		return null;
	}

	/* MotechService methods end */

	/* Controller methods start */
	public List<Location> getAllClinics() {
		LocationService locationService = contextService.getLocationService();
		return locationService.getAllLocations();
	}

	public List<User> getAllNurses() {
		UserService userService = contextService.getUserService();
		return userService.getAllUsers();
	}

	public List<Patient> getAllPatients() {
		PatientService patientService = contextService.getPatientService();
		List<PatientIdentifierType> ghanaPatientIdType = new ArrayList<PatientIdentifierType>();
		ghanaPatientIdType.add(getGhanaPatientIdType());
		return patientService
				.getPatients(null, null, ghanaPatientIdType, false);
	}

	public List<Encounter> getAllPregnancyVisits() {
		EncounterService encounterService = contextService
				.getEncounterService();
		List<EncounterType> pregnancyType = new ArrayList<EncounterType>();
		pregnancyType.add(getPregnancyVisitEncounterType());
		return encounterService.getEncounters(null, null, null, null, null,
				pregnancyType, null, false);
	}

	public List<Encounter> getAllMaternalVisits() {
		EncounterService encounterService = contextService
				.getEncounterService();
		List<EncounterType> maternalVisitType = new ArrayList<EncounterType>();
		maternalVisitType.add(getMaternalVisitEncounterType());
		return encounterService.getEncounters(null, null, null, null, null,
				maternalVisitType, null, false);
	}

	public List<ScheduledMessage> getAllScheduledMessages() {
		MotechService motechService = contextService.getMotechService();
		return motechService.getAllScheduledMessages();
	}

	public List<org.motech.model.Log> getAllLogs() {
		MotechService motechService = contextService.getMotechService();
		return motechService.getAllLogs();
	}

	/* Controller methods end */

	/* PatientObsService methods start */
	public Date getPatientBirthDate(Integer patientId) {
		PatientService patientService = contextService.getPatientService();
		Patient patient = patientService.getPatient(patientId);
		return patient.getBirthdate();
	}

	public Date getMessageProgramStartDate(Integer personId, String program) {
		MotechService motechService = contextService.getMotechService();
		MessageProgramEnrollment enrollment = motechService
				.getActiveMessageProgramEnrollment(personId, program);
		return enrollment.getStartDate();
	}

	private List<Obs> getMatchingObs(Person person, Concept question,
			Concept answer) {

		ObsService obsService = contextService.getObsService();

		List<Concept> questions = null;
		if (question != null) {
			questions = new ArrayList<Concept>();
			questions.add(question);
		}

		List<Concept> answers = null;
		if (answer != null) {
			answers = new ArrayList<Concept>();
			answers.add(answer);
		}

		List<Person> whom = new ArrayList<Person>();
		whom.add(person);

		// patients, encounters, questions, answers, persontype, locations,
		// sort, max returned, group id, from date, to date, include voided
		List<Obs> obsList = obsService.getObservations(whom, null, questions,
				answers, null, null, null, null, null, null, null, false);

		return obsList;
	}

	public int getNumberOfObs(Integer personId, String conceptName,
			String conceptValue) {

		ConceptService conceptService = contextService.getConceptService();
		PersonService personService = contextService.getPersonService();
		return getNumberOfObs(personService.getPerson(personId), conceptService
				.getConcept(conceptName), conceptService
				.getConcept(conceptValue));
	}

	public Date getLastObsDate(Integer personId, String conceptName,
			String conceptValue) {

		ConceptService conceptService = contextService.getConceptService();
		PersonService personService = contextService.getPersonService();
		return getLastObsDate(personService.getPerson(personId), conceptService
				.getConcept(conceptName), conceptService
				.getConcept(conceptValue));
	}

	public Date getLastObsValue(Integer personId, String conceptName) {
		ConceptService conceptService = contextService.getConceptService();
		PersonService personService = contextService.getPersonService();
		return getLastObsValue(personService.getPerson(personId),
				conceptService.getConcept(conceptName));
	}

	public int getNumberOfObs(Person person, Concept concept, Concept value) {

		List<Obs> obsList = getMatchingObs(person, concept, value);
		return obsList.size();
	}

	public Date getLastObsDate(Person person, Concept concept, Concept value) {

		Date latestObsDate = null;

		// List default sorted by Obs datetime
		List<Obs> obsList = getMatchingObs(person, concept, value);

		if (obsList.size() > 0) {
			latestObsDate = obsList.get(0).getObsDatetime();
		} else if (log.isDebugEnabled()) {
			log.debug("No matching Obs: person id: " + person.getPersonId()
					+ ", concept: " + concept.getName().getName() + ", value: "
					+ value.getName().getName());
		}
		return latestObsDate;
	}

	public Date getLastObsValue(Person person, Concept concept) {
		Date lastestObsValue = null;

		List<Obs> obsList = getMatchingObs(person, concept, null);
		if (obsList.size() > 0) {
			lastestObsValue = obsList.get(0).getValueDatetime();
		} else if (log.isDebugEnabled()) {
			log.debug("No matching Obs: person id: " + person.getPersonId()
					+ ", concept: " + concept.getName().getName());
		}
		return lastestObsValue;
	}

	/* PatientObsService methods end */

	/* MessageDefinition methods start */
	public NameValuePair[] getNameValueContent(
			MessageDefinition messageDefinition, Integer messageRecipientId) {

		List<NameValuePair> nameValueList = new ArrayList<NameValuePair>();
		for (MessageAttribute attribute : messageDefinition
				.getMessageAttributes()) {
			NameValuePair pair = new NameValuePair();
			pair.setName(attribute.getName());
			if (attribute.getName().equals("PatientFirstName")) {
				pair.setValue(getPatientFirstName(messageRecipientId));
			} else if (attribute.getName().equals("DueDate")) {
				pair.setValue(getPatientDueDate(messageRecipientId));
			}
			nameValueList.add(pair);
		}
		return nameValueList.toArray(new NameValuePair[nameValueList.size()]);
	}

	public String getPatientFirstName(Integer patientId) {
		PersonService personService = contextService.getPersonService();
		Person person = personService.getPerson(patientId);
		return person.getGivenName();
	}

	public String getPatientDueDate(Integer patientId) {
		PatientService patientService = contextService.getPatientService();
		Patient patient = patientService.getPatient(patientId);
		Date dueDate = this.getLastObsValue(patient, getDueDateConcept());
		return dueDate.toString();
	}

	/* MessageDefinition methods end */

	/* MessageSchedulerImpl methods start */
	public void scheduleMessage(String messageKey, String messageGroup,
			Integer messageRecipientId, Date messageDate,
			boolean userPreferenceBased) {
		// Return existing message definition
		MessageDefinition messageDefinition = this
				.getMessageDefinition(messageKey);

		Date scheduledMessageDate;
		if (!userPreferenceBased) {
			scheduledMessageDate = messageDate;
		} else {
			scheduledMessageDate = this.determineUserPreferredMessageDate(
					messageRecipientId, messageDate);
		}

		// Cancel any unsent messages for the same group, unless matching the
		// message to schedule
		this.removeUnsentMessages(messageRecipientId, messageGroup,
				messageDefinition, scheduledMessageDate);

		// Create new scheduled message (with pending attempt) for group
		// if none matching already exist
		this.createScheduledMessage(messageRecipientId, messageDefinition,
				messageGroup, scheduledMessageDate);
	}

	private MessageDefinition getMessageDefinition(String messageKey) {
		MotechService motechService = contextService.getMotechService();
		MessageDefinition messageDefinition = motechService
				.getMessageDefinition(messageKey);
		return messageDefinition;
	}

	protected void removeUnsentMessages(Integer recipientId,
			String messageGroup, MessageDefinition messageDefinition,
			Date messageDate) {
		MotechService motechService = contextService.getMotechService();
		List<Message> unsentMessages = motechService.getMessages(recipientId,
				messageGroup, MessageStatus.SHOULD_ATTEMPT);
		log.debug("Unsent messages found: " + unsentMessages.size());

		for (Message unsentMessage : unsentMessages) {
			ScheduledMessage messageSchedule = unsentMessage.getSchedule();

			if (log.isDebugEnabled()) {
				log.debug("Found message definition id: "
						+ messageSchedule.getMessage().getId()
						+ ", schedule date: "
						+ messageSchedule.getScheduledFor()
						+ ", New message definintion id: "
						+ messageDefinition.getId() + ", new schedule date: "
						+ messageDate);
			}

			if (!messageDefinition.getId().equals(
					messageSchedule.getMessage().getId())
					|| !messageDate.equals(messageSchedule.getScheduledFor())) {

				unsentMessage.setAttemptStatus(MessageStatus.CANCELLED);
				motechService.saveMessage(unsentMessage);

				log.debug("Message cancelled: Id: " + unsentMessage.getId());
			}
		}
	}

	public void removeAllUnsentMessages(Integer recipientId, String messageGroup) {
		MotechService motechService = contextService.getMotechService();
		List<Message> unsentMessages = motechService.getMessages(recipientId,
				messageGroup, MessageStatus.SHOULD_ATTEMPT);
		log.debug("Unsent messages found: " + unsentMessages.size());

		for (Message unsentMessage : unsentMessages) {

			unsentMessage.setAttemptStatus(MessageStatus.CANCELLED);
			motechService.saveMessage(unsentMessage);

			log.debug("Message cancelled: Id: " + unsentMessage.getId());
		}
	}

	private Date determineUserPreferredMessageDate(Integer recipientId,
			Date messageDate) {
		Person recipient = contextService.getPersonService().getPerson(
				recipientId);
		DeliveryTime deliveryTime = getPersonDeliveryTime(recipient);

		return determineMessageStartDate(deliveryTime, messageDate);
	}

	private void createScheduledMessage(Integer recipientId,
			MessageDefinition messageDefinition, String messageGroup,
			Date messageDate) {
		MotechService motechService = contextService.getMotechService();

		List<ScheduledMessage> scheduledMessages = motechService
				.getScheduledMessages(recipientId, messageDefinition.getId(),
						messageDate);
		if (scheduledMessages.size() == 0) {
			log.info(recipientId + ", " + messageDefinition.getId() + ", "
					+ messageDate
					+ " - ScheduledMessage Does Not Exist - Creating");

			ScheduledMessage scheduledMessage = new ScheduledMessage();
			scheduledMessage.setScheduledFor(messageDate);
			scheduledMessage.setRecipientId(recipientId);
			scheduledMessage.setMessage(messageDefinition);
			scheduledMessage.getGroupIds().add(messageGroup);

			Message message = messageDefinition.createMessage(scheduledMessage);
			message.setAttemptDate(messageDate);
			scheduledMessage.getMessageAttempts().add(message);

			motechService.saveScheduledMessage(scheduledMessage);
		}
	}

	/* MessageSchedulerImpl methods end */

	/* Activator methods start */
	public void addInitialData() {

		UserService userService = contextService.getUserService();
		User admin = userService.getUser(1);

		log.info("Verifying Person Attributes Exist");
		createPersonAttributeType(MotechConstants.PERSON_ATTRIBUTE_CHPS_ID,
				"A nurse's CHPS ID.", String.class.getName(), admin);
		createPersonAttributeType(
				MotechConstants.PERSON_ATTRIBUTE_PRIMARY_PHONE_NUMBER,
				"A person's primary phone number.", String.class.getName(),
				admin);
		createPersonAttributeType(
				MotechConstants.PERSON_ATTRIBUTE_SECONDARY_PHONE_NUMBER,
				"A person's seconadary phone number.", String.class.getName(),
				admin);
		createPersonAttributeType(MotechConstants.PERSON_ATTRIBUTE_NHIS_NUMBER,
				"A person's NHIS number.", String.class.getName(), admin);
		// TODO: Use AttributableDate? Create Attributable types for Enums?
		createPersonAttributeType(
				MotechConstants.PERSON_ATTRIBUTE_NHIS_EXP_DATE,
				"A person's NHIS expiration date.", Date.class.getName(), admin);
		createPersonAttributeType(
				MotechConstants.PERSON_ATTRIBUTE_LANGUAGE_TEXT,
				"A person's language preference for text messages.",
				String.class.getName(), admin);
		createPersonAttributeType(
				MotechConstants.PERSON_ATTRIBUTE_LANGUAGE_VOICE,
				"A person's language preference for voice messages.",
				String.class.getName(), admin);
		createPersonAttributeType(
				MotechConstants.PERSON_ATTRIBUTE_PRIMARY_PHONE_TYPE,
				"A person's primary phone type (PERSONAL, HOUSEHOLD, or PUBLIC).",
				String.class.getName(), admin);
		createPersonAttributeType(
				MotechConstants.PERSON_ATTRIBUTE_SECONDARY_PHONE_TYPE,
				"A person's secondary phone type (PERSONAL, HOUSEHOLD, or PUBLIC).",
				String.class.getName(), admin);
		createPersonAttributeType(
				MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE_INFORMATIONAL,
				"A person's preferred phone media type for info messages (TEXT or VOICE).",
				String.class.getName(), admin);
		createPersonAttributeType(
				MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE_REMINDER,
				"A person's preferred phone media type for reminder messages (TEXT or VOICE).",
				String.class.getName(), admin);
		createPersonAttributeType(
				MotechConstants.PERSON_ATTRIBUTE_DELIVERY_TIME,
				"A person's preferred delivery time (ANYTIME, MORNING, AFTERNOON, or EVENING).",
				String.class.getName(), admin);
		createPersonAttributeType(
				MotechConstants.PERSON_ATTRIBUTE_GHS_ANC_REG_NUMBER,
				"A mother's GHS ANC Registration number.", String.class
						.getName(), admin);
		createPersonAttributeType(
				MotechConstants.PERSON_ATTRIBUTE_GHS_CWC_REG_NUMBER,
				"A child's GHS CWC Registration number.", String.class
						.getName(), admin);
		createPersonAttributeType(
				MotechConstants.PERSON_ATTRIBUTE_GHS_REGISTERED,
				"Is person registered with GHS? (TRUE OR FALSE)", String.class
						.getName(), admin);
		createPersonAttributeType(MotechConstants.PERSON_ATTRIBUTE_INSURED,
				"Is person insured? (TRUE OR FALSE)", String.class.getName(),
				admin);
		createPersonAttributeType(MotechConstants.PERSON_ATTRIBUTE_HIV_STATUS,
				"A person's HIV status (POSITIVE, NEGATIVE, or UNKNOWN).",
				String.class.getName(), admin);
		createPersonAttributeType(
				MotechConstants.PERSON_ATTRIBUTE_WHO_REGISTERED,
				"Who registered person? (MOTHER, FATHER, FAMILY_MEMBER, CHPS_STAFF, or OTHER)",
				String.class.getName(), admin);
		createPersonAttributeType(MotechConstants.PERSON_ATTRIBUTE_RELIGION,
				"A person's religion.", String.class.getName(), admin);
		createPersonAttributeType(MotechConstants.PERSON_ATTRIBUTE_OCCUPATION,
				"A person's occupation.", String.class.getName(), admin);

		log.info("Verifying Patient Identifier Exist");
		createPatientIdentifierType(
				MotechConstants.PATIENT_IDENTIFIER_GHANA_CLINIC_ID,
				"Patient Id for Ghana Clinics.", admin);

		log.info("Verifying Locations Exist");
		Location ghana = createLocation(MotechConstants.LOCATION_GHANA,
				"Republic of Ghana, Country, Root in hierarchy", null, admin);
		Location upperEast = createLocation(
				MotechConstants.LOCATION_UPPER_EAST,
				"Upper East Region in Ghana", ghana, admin);
		createLocation(MotechConstants.LOCATION_KASSENA_NANKANA,
				"Kassena-Nankana District in Upper East Region, Ghana",
				upperEast, admin);
		createLocation(MotechConstants.LOCATION_KASSENA_NANKANA_WEST,
				"Kassena-Nankana West District in Upper East Region, Ghana",
				upperEast, admin);

		log.info("Verifying Encounter Types Exist");
		createEncounterType(MotechConstants.ENCOUNTER_TYPE_MATERNALVISIT,
				"Ghana Maternal Visit", admin);
		createEncounterType(MotechConstants.ENCOUNTER_TYPE_PREGNANCYVISIT,
				"Ghana Pregnancy Registration or Delivery Visit", admin);
		createEncounterType(MotechConstants.ENCOUNTER_TYPE_IMMUNIZVISIT,
				"Ghana Immunization Visit", admin);
		createEncounterType(MotechConstants.ENCOUNTER_TYPE_GENERALVISIT,
				"Ghana General Visit", admin);

		log.info("Verifying Concepts Exist");
		createConcept(MotechConstants.CONCEPT_PREGNANCY_VISIT_NUMBER,
				"Visit Number for Pregnancy",
				MotechConstants.CONCEPT_CLASS_MISC,
				MotechConstants.CONCEPT_DATATYPE_NUMERIC, admin);
		createConcept(
				MotechConstants.CONCEPT_INTERMITTENT_PREVENTATIVE_TREATMENT,
				"Treatment for Malaria", MotechConstants.CONCEPT_CLASS_DRUG,
				MotechConstants.CONCEPT_DATATYPE_N_A, admin);
		createConcept(
				MotechConstants.CONCEPT_INSECTICIDE_TREATED_NET_USAGE,
				"Question on encounter form: \"Does the patient use insecticide-treated nets?\"",
				MotechConstants.CONCEPT_CLASS_QUESTION,
				MotechConstants.CONCEPT_DATATYPE_BOOLEAN, admin);
		createConcept(MotechConstants.CONCEPT_PENTA_VACCINATION,
				"Vaccination booster for infants.",
				MotechConstants.CONCEPT_CLASS_DRUG,
				MotechConstants.CONCEPT_DATATYPE_N_A, admin);
		createConcept(
				MotechConstants.CONCEPT_CEREBRO_SPINAL_MENINGITIS_VACCINATION,
				"Vaccination against Cerebro-Spinal Meningitis.",
				MotechConstants.CONCEPT_CLASS_DRUG,
				MotechConstants.CONCEPT_DATATYPE_N_A, admin);
		createConcept(MotechConstants.CONCEPT_VITAMIN_A,
				"Supplement for Vitamin A.",
				MotechConstants.CONCEPT_CLASS_DRUG,
				MotechConstants.CONCEPT_DATATYPE_N_A, admin);
		createConcept(
				MotechConstants.CONCEPT_PRE_PREVENTING_MATERNAL_TO_CHILD_TRANSMISSION,
				"Question on encounter form: \"Did the patient receive Pre Counseling for Preventing Mother-to-Child Transmission (PMTCT) of HIV\"",
				MotechConstants.CONCEPT_CLASS_QUESTION,
				MotechConstants.CONCEPT_DATATYPE_BOOLEAN, admin);
		createConcept(
				MotechConstants.CONCEPT_TEST_PREVENTING_MATERNAL_TO_CHILD_TRANSMISSION,
				"Question on encounter form: \"Did the patient receive Testing for Preventing Mother-to-Child Transmission (PMTCT) of HIV\"",
				MotechConstants.CONCEPT_CLASS_QUESTION,
				MotechConstants.CONCEPT_DATATYPE_BOOLEAN, admin);
		createConcept(
				MotechConstants.CONCEPT_POST_PREVENTING_MATERNAL_TO_CHILD_TRANSMISSION,
				"Question on encounter form: \"Did the patient receive Post Counseling for Preventing Mother-to-Child Transmission (PMTCT) of HIV\"",
				MotechConstants.CONCEPT_CLASS_QUESTION,
				MotechConstants.CONCEPT_DATATYPE_BOOLEAN, admin);
		createConcept(MotechConstants.CONCEPT_HEMOGLOBIN_AT_36_WEEKS,
				"Hemoglobin level at 36 weeks of Pregnancy",
				MotechConstants.CONCEPT_CLASS_TEST,
				MotechConstants.CONCEPT_DATATYPE_NUMERIC, admin);
		createConcept(
				MotechConstants.CONCEPT_DATE_OF_CONFINEMENT_CONFIRMED,
				"Question: \"Is the pregnancy due date confirmed by the CHW?\"",
				MotechConstants.CONCEPT_CLASS_QUESTION,
				MotechConstants.CONCEPT_DATATYPE_BOOLEAN, admin);

		log.info("Verifying Concepts Exist as Answers");
		// TODO: Add IPT to proper Concept as an Answer, not an immunization
		addConceptAnswers(
				MotechConstants.CONCEPT_IMMUNIZATIONS_ORDERED,
				new String[] {
						MotechConstants.CONCEPT_TETANUS_BOOSTER,
						MotechConstants.CONCEPT_YELLOW_FEVER_VACCINATION,
						MotechConstants.CONCEPT_INTERMITTENT_PREVENTATIVE_TREATMENT,
						MotechConstants.CONCEPT_PENTA_VACCINATION,
						MotechConstants.CONCEPT_CEREBRO_SPINAL_MENINGITIS_VACCINATION },
				admin);

		log.info("Verifying Task Exists and is Scheduled");
		// TODO: Task should start automatically on startup, Boolean.TRUE
		Map<String, String> immProps = new HashMap<String, String>();
		immProps.put(MotechConstants.TASK_PROPERTY_SEND_IMMEDIATE, Boolean.TRUE
				.toString());
		createTask(MotechConstants.TASK_IMMEDIATE_NOTIFICATION,
				"Task to send out immediate SMS notifications", new Date(),
				new Long(30), Boolean.FALSE, NotificationTask.class.getName(),
				admin, immProps);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Map<String, String> dailyProps = new HashMap<String, String>();
		dailyProps.put(MotechConstants.TASK_PROPERTY_TIME_OFFSET,
				new Long(3600).toString());
		createTask(MotechConstants.TASK_DAILY_NOTIFICATION,
				"Task to send out SMS notifications for next day", calendar
						.getTime(), new Long(86400), Boolean.FALSE,
				NotificationTask.class.getName(), admin, dailyProps);
		createTask(MotechConstants.TASK_MESSAGEPROGRAM_UPDATE,
				"Task to update message program state for patients",
				new Date(), new Long(30), Boolean.FALSE,
				MessageProgramUpdateTask.class.getName(), admin, null);
	}

	private void createPersonAttributeType(String name, String description,
			String format, User creator) {
		PersonService personService = contextService.getPersonService();
		PersonAttributeType attrType = personService
				.getPersonAttributeTypeByName(name);
		if (attrType == null) {
			log.info(name + " PersonAttributeType Does Not Exist - Creating");
			attrType = new PersonAttributeType();
			attrType.setName(name);
			attrType.setDescription(description);
			attrType.setFormat(format);
			attrType.setCreator(creator);
			personService.savePersonAttributeType(attrType);
		}
	}

	private void createPatientIdentifierType(String name, String description,
			User creator) {
		PatientService patientService = contextService.getPatientService();
		PatientIdentifierType idType = patientService
				.getPatientIdentifierTypeByName(name);
		if (idType == null) {
			log.info(name + " PatientIdentifierType Does Not Exist - Creating");
			idType = new PatientIdentifierType();
			idType.setName(name);
			idType.setDescription(description);
			idType.setCreator(creator);
			patientService.savePatientIdentifierType(idType);
		}
	}

	private Location createLocation(String name, String description,
			Location parent, User creator) {
		LocationService locationService = contextService.getLocationService();
		Location location = locationService.getLocation(name);
		if (location == null) {
			log.info(name + " Location Does Not Exist - Creating");
			location = new Location();
			location.setName(name);
			location.setDescription(description);
			location.setCreator(creator);
			location = locationService.saveLocation(location);

			if (parent != null) {
				parent.addChildLocation(location);
				locationService.saveLocation(parent);
			}
		}
		return location;
	}

	private void createEncounterType(String name, String description,
			User creator) {
		EncounterService encounterService = contextService
				.getEncounterService();
		EncounterType encType = encounterService.getEncounterType(name);
		if (encType == null) {
			log.info(name + " EncounterType Does Not Exist - Creating");
			encType = new EncounterType();
			encType.setName(name);
			encType.setDescription(description);
			encType.setCreator(creator);
			encounterService.saveEncounterType(encType);
		}
	}

	private Concept createConcept(String name, String description,
			String className, String dataTypeName, User creator) {
		ConceptService conceptService = contextService.getConceptService();
		// Default "en" Locale matching other existing concepts
		Locale defaultLocale = Locale.ENGLISH;
		Concept concept = conceptService.getConcept(name);
		ConceptNameTag prefTag = conceptService
				.getConceptNameTagByName(ConceptNameTag.PREFERRED);
		if (concept == null) {
			log.info(name + " Concept Does Not Exist - Creating");
			concept = new Concept();
			ConceptName conceptName = new ConceptName(name, defaultLocale);
			conceptName.addTag(prefTag);
			conceptName.setCreator(creator);
			// AddTag is workaround since the following results in
			// "preferred_en" instead of "preferred"
			// itn.setPreferredName(defaultLocale, conceptName)
			concept.addName(conceptName);
			ConceptDescription conceptDescription = new ConceptDescription(
					description, defaultLocale);
			conceptDescription.setCreator(creator);
			concept.addDescription(conceptDescription);
			concept.setConceptClass(conceptService
					.getConceptClassByName(className));
			concept.setDatatype(conceptService
					.getConceptDatatypeByName(dataTypeName));
			concept.setCreator(creator);
			concept = conceptService.saveConcept(concept);
		} else {
			log.info(name + " Concept Exists");
		}
		return concept;
	}

	private void addConceptAnswers(String conceptName, String[] answerNames,
			User creator) {
		ConceptService conceptService = contextService.getConceptService();
		Concept concept = conceptService.getConcept(conceptName);
		Set<Integer> currentAnswerIds = new HashSet<Integer>();
		for (ConceptAnswer answer : concept.getAnswers()) {
			currentAnswerIds.add(answer.getAnswerConcept().getConceptId());
		}
		boolean changed = false;
		for (String answerName : answerNames) {
			Concept answer = conceptService.getConcept(answerName);
			if (!currentAnswerIds.contains(answer.getConceptId())) {
				log.info("Adding Concept Answer " + answerName + " to "
						+ conceptName);
				changed = true;
				ConceptAnswer conceptAnswer = new ConceptAnswer(answer);
				conceptAnswer.setCreator(creator);
				conceptAnswer.setDateCreated(new Date());
				concept.addAnswer(conceptAnswer);
			}
		}
		if (changed) {
			conceptService.saveConcept(concept);
		}
	}

	private void createTask(String name, String description, Date startDate,
			Long repeatSeconds, Boolean startOnStartup, String taskClass,
			User creator, Map<String, String> properties) {
		SchedulerService schedulerService = contextService
				.getSchedulerService();
		TaskDefinition task = schedulerService.getTaskByName(name);
		if (task == null) {
			task = new TaskDefinition();
			task.setName(name);
			task.setDescription(description);
			task.setStartTime(startDate);
			task.setRepeatInterval(repeatSeconds);
			if (properties != null)
				task.setProperties(properties);
			task.setTaskClass(taskClass);
			task.setStartOnStartup(startOnStartup);
			task.setCreator(creator);
			schedulerService.saveTask(task);
			task = schedulerService.getTaskByName(name);
		}

		try {
			schedulerService.scheduleTask(task);
		} catch (SchedulerException e) {
			log.error("Cannot schedule task" + name, e);
		}

	}

	private void removeTask(String name) {
		SchedulerService schedulerService = contextService
				.getSchedulerService();
		TaskDefinition task = schedulerService.getTaskByName(name);
		if (task != null) {
			// Only shutdown if task has not already been shutdown
			if (task.getStarted()) {
				try {
					schedulerService.shutdownTask(task);
				} catch (SchedulerException e) {
					log.error("Cannot shutdown task: " + name, e);
				}
			}
			schedulerService.deleteTask(task.getId());
		}
	}

	public void removeAllTasks() {
		log.info("Removing Scheduled Tasks");

		removeTask(MotechConstants.TASK_IMMEDIATE_NOTIFICATION);
		removeTask(MotechConstants.TASK_DAILY_NOTIFICATION);
		removeTask(MotechConstants.TASK_MESSAGEPROGRAM_UPDATE);
	}

	/* Activator methods end */

	/* SaveObsAdvice method */
	public void updateMessageProgramState(Integer personId, String conceptName) {

		// Only determine message program state for enrolled programs
		// concerned with an observed concept
		// and matching the concept of this obs

		List<String> patientPrograms = this
				.getActiveMessageProgramEnrollments(personId);

		for (String programName : patientPrograms) {
			MessageProgram program = this.getMessageProgram(programName);

			if (program.getConceptName() != null) {
				if (program.getConceptName().equals(conceptName)) {
					log
							.debug("Save Obs - Obs matches Program concept, update Program: "
									+ programName);

					program.determineState(personId);
				}
			}
		}
	}

	/* MessageProgramUpdateTask method */
	public void updateAllMessageProgramsState() {

		// Get all Patients with the Ghana Clinic Id Type
		List<Patient> patients = getAllPatients();

		// Update Message Program state for enrolled Programs of all
		// matching
		// patients
		for (Patient patient : patients) {
			List<String> patientPrograms = this
					.getActiveMessageProgramEnrollments(patient.getPatientId());

			for (String programName : patientPrograms) {
				MessageProgram program = this.getMessageProgram(programName);

				log.debug("MessageProgram Update - Update State: program: "
						+ programName + ", patient: " + patient.getPatientId());

				program.determineState(patient.getPatientId());
			}
		}
	}

	/* NotificationTask methods start */
	public void sendMessages(Date startDate, Date endDate, boolean sendImmediate) {
		try {
			MotechService motechService = contextService.getMotechService();

			List<Message> shouldAttemptMessages = motechService.getMessages(
					startDate, endDate, MessageStatus.SHOULD_ATTEMPT);

			if (log.isDebugEnabled()) {
				log
						.debug("Notification Task executed, Should Attempt Messages found: "
								+ shouldAttemptMessages.size());
			}

			for (Message shouldAttemptMessage : shouldAttemptMessages) {
				sendMessage(shouldAttemptMessage, sendImmediate);
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

	public void sendMessage(Message message, boolean sendImmediate) {
		MotechService motechService = contextService.getMotechService();
		PersonService personService = contextService.getPersonService();
		PatientService patientService = contextService.getPatientService();
		UserService userService = contextService.getUserService();

		org.motech.model.Log motechLog = new org.motech.model.Log();
		motechLog.setDate(new Date());

		Long notificationType = message.getSchedule().getMessage()
				.getPublicId();
		MessageType messageType = message.getSchedule().getMessage()
				.getMessageType();
		Integer recipientId = message.getSchedule().getRecipientId();
		Person person = personService.getPerson(recipientId);

		String phoneNumber = getPrimaryPersonPhoneNumber(person);

		// Cancel message if phone number is considered troubled
		if (isPhoneTroubled(phoneNumber)) {
			motechLog.setMessage("Attempt to send to Troubled Phone, Phone: "
					+ phoneNumber + ", Notification cancelled: "
					+ notificationType);
			motechLog.setType(LogType.FAILURE);

			message.setAttemptStatus(MessageStatus.CANCELLED);

		} else {
			motechLog.setMessage("Scheduled Message Notification, Phone: "
					+ phoneNumber + ", Notification: " + notificationType);

			String messageId = message.getPublicId();
			MediaType mediaType = getPersonMediaType(person, messageType);
			String languageCode = getPersonLanguageCode(person, mediaType);
			NameValuePair[] personalInfo = this.getNameValueContent(message
					.getSchedule().getMessage(), recipientId);

			Date messageStartDate = null;
			Date messageEndDate = null;

			if (!sendImmediate) {
				messageStartDate = message.getAttemptDate();

				DeliveryTime deliveryTime = getPersonDeliveryTime(person);
				messageEndDate = determineMessageEndDate(deliveryTime,
						messageStartDate);
			}

			Patient patient = patientService.getPatient(recipientId);
			User nurse = userService.getUser(recipientId);

			boolean sendMessageSuccess = false;
			if (patient != null) {
				ContactNumberType contactNumberType = getPrimaryPersonPhoneType(person);

				sendMessageSuccess = sendPatientMessage(messageId,
						personalInfo, phoneNumber, languageCode, mediaType,
						notificationType, messageStartDate, messageEndDate,
						contactNumberType);
			} else if (nurse != null) {
				org.motechproject.ws.Patient[] patients = new org.motechproject.ws.Patient[0];

				sendMessageSuccess = sendNurseMessage(messageId, personalInfo,
						phoneNumber, languageCode, mediaType, notificationType,
						messageStartDate, messageEndDate, patients);
			} else {
				log.error("Attempt to send to Person not patient or nurse: "
						+ recipientId);
			}
			if (sendMessageSuccess) {
				message.setAttemptStatus(MessageStatus.ATTEMPT_PENDING);
				motechLog.setType(LogType.SUCCESS);
			} else {
				message.setAttemptStatus(MessageStatus.ATTEMPT_FAIL);
				motechLog.setType(LogType.FAILURE);
			}
		}
		motechService.saveLog(motechLog);

		motechService.saveMessage(message);
	}

	public boolean sendPatientMessage(String messageId,
			NameValuePair[] personalInfo, String phoneNumber,
			String languageCode, MediaType mediaType, Long notificationType,
			Date messageStartDate, Date messageEndDate,
			ContactNumberType contactType) {

		try {
			org.motechproject.ws.MessageStatus messageStatus = mobileService
					.sendPatientMessage(messageId, personalInfo, phoneNumber,
							contactType, languageCode, mediaType,
							notificationType, messageStartDate, messageEndDate);

			return messageStatus != org.motechproject.ws.MessageStatus.FAILED;
		} catch (Exception e) {
			log.error("Mobile WS patient message failure", e);
			return false;
		}
	}

	public boolean sendNurseMessage(String messageId,
			NameValuePair[] personalInfo, String phoneNumber,
			String languageCode, MediaType mediaType, Long notificationType,
			Date messageStartDate, Date messageEndDate,
			org.motechproject.ws.Patient[] patients) {

		try {
			org.motechproject.ws.MessageStatus messageStatus = mobileService
					.sendCHPSMessage(messageId, personalInfo, phoneNumber,
							patients, languageCode, mediaType,
							notificationType, messageStartDate, messageEndDate);

			return messageStatus != org.motechproject.ws.MessageStatus.FAILED;
		} catch (Exception e) {
			log.error("Mobile WS nurse message failure", e);
			return false;
		}
	}

	/* NotificationTask methods end */

	/* Factored out methods start */
	public void addMessageProgramEnrollment(Integer personId, String program) {
		MotechService motechService = contextService.getMotechService();

		MessageProgramEnrollment enrollment = motechService
				.getActiveMessageProgramEnrollment(personId, program);
		if (enrollment == null) {
			enrollment = new MessageProgramEnrollment();
			enrollment.setPersonId(personId);
			enrollment.setProgram(program);
			enrollment.setStartDate(new Date());
			motechService.saveMessageProgramEnrollment(enrollment);
		}
	}

	public void removeMessageProgramEnrollment(Integer personId, String program) {
		MotechService motechService = contextService.getMotechService();

		MessageProgramEnrollment enrollment = motechService
				.getActiveMessageProgramEnrollment(personId, program);
		if (enrollment != null) {
			enrollment.setEndDate(new Date());
			motechService.saveMessageProgramEnrollment(enrollment);
		}
	}

	public Obs createNumericValueObs(Date date, Concept concept, Person person,
			Location location, Double value, Encounter encounter, User creator) {

		Obs obs = createObs(date, concept, person, location, encounter, creator);
		obs.setValueNumeric(value);
		return obs;
	}

	public Obs createBooleanValueObs(Date date, Concept concept, Person person,
			Location location, Boolean value, Encounter encounter, User creator) {

		Double doubleValue = null;
		// Boolean currently stored as Numeric 1 or 0
		if (value) {
			doubleValue = new Double(1);
		} else {
			doubleValue = new Double(0);
		}
		return createNumericValueObs(date, concept, person, location,
				doubleValue, encounter, creator);
	}

	public Obs createDateValueObs(Date date, Concept concept, Person person,
			Location location, Date value, Encounter encounter, User creator) {

		Obs obs = createObs(date, concept, person, location, encounter, creator);
		obs.setValueDatetime(value);
		return obs;
	}

	public Obs createConceptValueObs(Date date, Concept concept, Person person,
			Location location, Concept value, Encounter encounter, User creator) {

		Obs obs = createObs(date, concept, person, location, encounter, creator);
		obs.setValueCoded(value);
		return obs;
	}

	public Obs createTextValueObs(Date date, Concept concept, Person person,
			Location location, String value, Encounter encounter, User creator) {

		Obs obs = createObs(date, concept, person, location, encounter, creator);
		obs.setValueText(value);
		return obs;
	}

	public Obs createObs(Date date, Concept concept, Person person,
			Location location, Encounter encounter, User creator) {

		Obs obs = new Obs();
		obs.setObsDatetime(date);
		obs.setConcept(concept);
		obs.setPerson(person);
		obs.setLocation(location);
		if (encounter != null) {
			obs.setEncounter(encounter);
		}
		if (creator != null) {
			obs.setCreator(creator);
		}
		return obs;
	}

	public Patient getPatientBySerial(String serialId) {
		PatientService patientService = contextService.getPatientService();
		PatientIdentifierType serialIdType = getGhanaPatientIdType();
		List<PatientIdentifierType> idTypes = new ArrayList<PatientIdentifierType>();
		idTypes.add(serialIdType);

		// Parameters are Name, Id, Id type, match exactly boolean
		List<Patient> patients = patientService.getPatients(null, serialId,
				idTypes, true);
		if (patients.size() > 0) {
			if (patients.size() > 1) {
				log.warn("Multiple Patients found for serial: " + serialId);
			}
			return patients.get(0);
		}
		return null;
	}

	public User getNurseByCHPSId(String chpsId) {
		MotechService motechService = contextService.getMotechService();
		UserService userService = contextService.getUserService();

		PersonAttributeType chpsAttributeType = getNurseIdAttributeType();
		List<Integer> matchingUsers = motechService
				.getUserIdsByPersonAttribute(chpsAttributeType, chpsId);
		if (matchingUsers.size() > 0) {
			if (matchingUsers.size() > 1) {
				log.warn("Multiple Nurses found for CHPS ID: " + chpsId);
			}
			// If more than one user matches chps ID, first user in list is
			// returned
			Integer userId = matchingUsers.get(0);
			return userService.getUser(userId);
		}
		log.warn("No Nurse found for CHPS ID: " + chpsId);
		return null;
	}

	public Location getNurseClinicLocation(User nurse) {
		LocationService locationService = contextService.getLocationService();
		PersonAttributeType clinicAttrType = getClinicAttributeType();
		PersonAttribute clinicAttr = nurse.getAttribute(clinicAttrType);
		if (clinicAttr != null && clinicAttr.getValue() != null) {
			Integer clinicId = Integer.valueOf(clinicAttr.getValue());
			return locationService.getLocation(clinicId);
		}
		log.warn("No Clinic found for Nurse id: " + nurse.getUserId());
		return null;
	}

	public String getPrimaryPersonPhoneNumber(Person person) {
		PersonAttributeType phoneNumberAttrType = getPrimaryPhoneNumberAttributeType();
		PersonAttribute phoneNumberAttr = person
				.getAttribute(phoneNumberAttrType);
		if (phoneNumberAttr != null) {
			return phoneNumberAttr.getValue();
		}
		log
				.warn("No phone number found for Person id: "
						+ person.getPersonId());
		return null;
	}

	public String getPersonLanguageCode(Person person, MediaType mediaType) {
		PersonAttributeType languageAttrType = null;
		switch (mediaType) {
		case TEXT:
			languageAttrType = getLanguageTextAttributeType();
			break;
		case VOICE:
			languageAttrType = getLanguageVoiceAttributeType();
			break;
		default:
			log.error("Unhandled media type for language: " + mediaType);
			return null;
		}

		PersonAttribute languageAttr = person.getAttribute(languageAttrType);
		if (languageAttr != null) {
			return languageAttr.getValue();
		}
		log.debug("No language found for Person id: " + person.getPersonId());
		return null;
	}

	public ContactNumberType getPrimaryPersonPhoneType(Person person) {
		PersonAttributeType phoneTypeAttrType = getPrimaryPhoneTypeAttributeType();
		PersonAttribute phoneTypeAttr = person.getAttribute(phoneTypeAttrType);
		if (phoneTypeAttr != null && phoneTypeAttr.getValue() != null) {
			return ContactNumberType.valueOf(phoneTypeAttr.getValue());
		}
		log.debug("No contact number type found for Person id: "
				+ person.getPersonId());
		return null;
	}

	public MediaType getPersonMediaType(Person person, MessageType messageType) {
		PersonAttributeType mediaAttrType = null;
		switch (messageType) {
		case INFORMATIONAL:
			mediaAttrType = getMediaTypeInformationalAttributeType();
			break;
		case REMINDER:
			mediaAttrType = getMediaTypeReminderAttributeType();
			break;
		default:
			log.error("Unhandled message type for media type: " + messageType);
			return null;
		}

		PersonAttribute mediaTypeAttr = person.getAttribute(mediaAttrType);
		if (mediaTypeAttr != null && mediaTypeAttr.getValue() != null) {
			return MediaType.valueOf(mediaTypeAttr.getValue());
		}
		log.debug("No " + messageType + " media type found for Person id: "
				+ person.getPersonId());
		return null;
	}

	public DeliveryTime getPersonDeliveryTime(Person person) {
		PersonAttributeType deliveryTimeType = getDeliveryTimeAttributeType();
		PersonAttribute deliveryTimeAttr = person
				.getAttribute(deliveryTimeType);
		if (deliveryTimeAttr != null && deliveryTimeAttr.getValue() != null) {
			return DeliveryTime.valueOf(deliveryTimeAttr.getValue());
		} else {
			log.debug("No delivery time found for Person id: "
					+ person.getPersonId() + ", defaulting to anytime");
			return DeliveryTime.ANYTIME;
		}
	}

	public boolean isPhoneTroubled(String phoneNumber) {
		TroubledPhone troubledPhone = contextService.getMotechService()
				.getTroubledPhone(phoneNumber);
		Integer maxFailures = getMaxPhoneNumberFailures();
		if (maxFailures == null) {
			return false;
		}
		return troubledPhone != null
				&& troubledPhone.getSendFailures() >= maxFailures;
	}

	public Integer getMaxPhoneNumberFailures() {
		String troubledPhoneProperty = getTroubledPhoneProperty();
		if (troubledPhoneProperty != null) {
			return Integer.parseInt(troubledPhoneProperty);
		}
		log.error("Troubled Phone Property not found");
		return null;
	}

	public Date determineMessageStartDate(DeliveryTime deliveryTime,
			Date messageDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(messageDate);
		switch (deliveryTime) {
		case MORNING:
			calendar.set(Calendar.HOUR_OF_DAY, 9);
			break;
		case AFTERNOON:
			calendar.set(Calendar.HOUR_OF_DAY, 13);
			break;
		case EVENING:
			calendar.set(Calendar.HOUR_OF_DAY, 18);
			break;
		default:
			calendar.set(Calendar.HOUR_OF_DAY, 9);
			break;
		}
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

	public Date determineMessageEndDate(DeliveryTime deliveryTime,
			Date startDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		switch (deliveryTime) {
		case MORNING:
			calendar.set(Calendar.HOUR_OF_DAY, 12);
			break;
		case AFTERNOON:
			calendar.set(Calendar.HOUR_OF_DAY, 17);
			break;
		case EVENING:
			calendar.set(Calendar.HOUR_OF_DAY, 21);
			break;
		default:
			calendar.set(Calendar.HOUR_OF_DAY, 21);
			break;
		}
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

	public PatientIdentifierType getGhanaPatientIdType() {
		return contextService.getPatientService()
				.getPatientIdentifierTypeByName(
						MotechConstants.PATIENT_IDENTIFIER_GHANA_CLINIC_ID);
	}

	public PersonAttributeType getNurseIdAttributeType() {
		return contextService.getPersonService().getPersonAttributeTypeByName(
				MotechConstants.PERSON_ATTRIBUTE_CHPS_ID);
	}

	public PersonAttributeType getClinicAttributeType() {
		return contextService.getPersonService().getPersonAttributeTypeByName(
				MotechConstants.PERSON_ATTRIBUTE_HEALTH_CENTER);
	}

	public PersonAttributeType getPrimaryPhoneNumberAttributeType() {
		return contextService.getPersonService().getPersonAttributeTypeByName(
				MotechConstants.PERSON_ATTRIBUTE_PRIMARY_PHONE_NUMBER);
	}

	public PersonAttributeType getSecondaryPhoneNumberAttributeType() {
		return contextService.getPersonService().getPersonAttributeTypeByName(
				MotechConstants.PERSON_ATTRIBUTE_SECONDARY_PHONE_NUMBER);
	}

	public PersonAttributeType getNHISNumberAttributeType() {
		return contextService.getPersonService().getPersonAttributeTypeByName(
				MotechConstants.PERSON_ATTRIBUTE_NHIS_NUMBER);
	}

	public PersonAttributeType getNHISExpirationDateAttributeType() {
		return contextService.getPersonService().getPersonAttributeTypeByName(
				MotechConstants.PERSON_ATTRIBUTE_NHIS_EXP_DATE);
	}

	public PersonAttributeType getPrimaryPhoneTypeAttributeType() {
		return contextService.getPersonService().getPersonAttributeTypeByName(
				MotechConstants.PERSON_ATTRIBUTE_PRIMARY_PHONE_TYPE);
	}

	public PersonAttributeType getSecondaryPhoneTypeAttributeType() {
		return contextService.getPersonService().getPersonAttributeTypeByName(
				MotechConstants.PERSON_ATTRIBUTE_SECONDARY_PHONE_TYPE);
	}

	public PersonAttributeType getLanguageTextAttributeType() {
		return contextService.getPersonService().getPersonAttributeTypeByName(
				MotechConstants.PERSON_ATTRIBUTE_LANGUAGE_TEXT);
	}

	public PersonAttributeType getLanguageVoiceAttributeType() {
		return contextService.getPersonService().getPersonAttributeTypeByName(
				MotechConstants.PERSON_ATTRIBUTE_LANGUAGE_VOICE);
	}

	public PersonAttributeType getMediaTypeInformationalAttributeType() {
		return contextService.getPersonService().getPersonAttributeTypeByName(
				MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE_INFORMATIONAL);
	}

	public PersonAttributeType getMediaTypeReminderAttributeType() {
		return contextService.getPersonService().getPersonAttributeTypeByName(
				MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE_REMINDER);
	}

	public PersonAttributeType getDeliveryTimeAttributeType() {
		return contextService.getPersonService().getPersonAttributeTypeByName(
				MotechConstants.PERSON_ATTRIBUTE_DELIVERY_TIME);
	}

	public PersonAttributeType getANCRegistrationNumberAttributeType() {
		return contextService.getPersonService().getPersonAttributeTypeByName(
				MotechConstants.PERSON_ATTRIBUTE_GHS_ANC_REG_NUMBER);
	}

	public PersonAttributeType getCWCRegistrationNumberAttributeType() {
		return contextService.getPersonService().getPersonAttributeTypeByName(
				MotechConstants.PERSON_ATTRIBUTE_GHS_CWC_REG_NUMBER);
	}

	public PersonAttributeType getGHSRegisteredAttributeType() {
		return contextService.getPersonService().getPersonAttributeTypeByName(
				MotechConstants.PERSON_ATTRIBUTE_GHS_REGISTERED);
	}

	public PersonAttributeType getInsuredAttributeType() {
		return contextService.getPersonService().getPersonAttributeTypeByName(
				MotechConstants.PERSON_ATTRIBUTE_INSURED);
	}

	public PersonAttributeType getHIVStatusAttributeType() {
		return contextService.getPersonService().getPersonAttributeTypeByName(
				MotechConstants.PERSON_ATTRIBUTE_HIV_STATUS);
	}

	public PersonAttributeType getWhoRegisteredAttributeType() {
		return contextService.getPersonService().getPersonAttributeTypeByName(
				MotechConstants.PERSON_ATTRIBUTE_WHO_REGISTERED);
	}

	public PersonAttributeType getReligionAttributeType() {
		return contextService.getPersonService().getPersonAttributeTypeByName(
				MotechConstants.PERSON_ATTRIBUTE_RELIGION);
	}

	public PersonAttributeType getOccupationAttributeType() {
		return contextService.getPersonService().getPersonAttributeTypeByName(
				MotechConstants.PERSON_ATTRIBUTE_OCCUPATION);
	}

	public Location getGhanaLocation() {
		return contextService.getLocationService().getLocation(
				MotechConstants.LOCATION_GHANA);
	}

	public EncounterType getMaternalVisitEncounterType() {
		return contextService.getEncounterService().getEncounterType(
				MotechConstants.ENCOUNTER_TYPE_MATERNALVISIT);
	}

	public EncounterType getPregnancyVisitEncounterType() {
		return contextService.getEncounterService().getEncounterType(
				MotechConstants.ENCOUNTER_TYPE_PREGNANCYVISIT);
	}

	public Concept getImmunizationsOrderedConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_IMMUNIZATIONS_ORDERED);
	}

	public Concept getTetanusConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_TETANUS_BOOSTER);
	}

	public Concept getIPTConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_INTERMITTENT_PREVENTATIVE_TREATMENT);
	}

	public Concept getITNConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_INSECTICIDE_TREATED_NET_USAGE);
	}

	public Concept getPregnancyVisitNumberConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_PREGNANCY_VISIT_NUMBER);
	}

	public Concept getARVConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_ANTIRETROVIRAL_USE_DURING_PREGNANCY);
	}

	public Concept getOnARVConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_ON_ANTIRETROVIRAL_THERAPY);
	}

	public Concept getPrePMTCTConcept() {
		return contextService
				.getConceptService()
				.getConcept(
						MotechConstants.CONCEPT_PRE_PREVENTING_MATERNAL_TO_CHILD_TRANSMISSION);
	}

	public Concept getTestPMTCTConcept() {
		return contextService
				.getConceptService()
				.getConcept(
						MotechConstants.CONCEPT_TEST_PREVENTING_MATERNAL_TO_CHILD_TRANSMISSION);
	}

	public Concept getPostPMTCTConcept() {
		return contextService
				.getConceptService()
				.getConcept(
						MotechConstants.CONCEPT_POST_PREVENTING_MATERNAL_TO_CHILD_TRANSMISSION);
	}

	public Concept getHemoglobin36WeeksConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_HEMOGLOBIN_AT_36_WEEKS);
	}

	public Concept getPregnancyStatusConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_PREGNANCY_STATUS);
	}

	public Concept getDueDateConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_ESTIMATED_DATE_OF_CONFINEMENT);
	}

	public Concept getParityConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_PARITY);
	}

	public Concept getGravidaConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_GRAVIDA);
	}

	public Concept getHemoglobinConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_HEMOGLOBIN);
	}

	public Concept getDueDateConfirmedConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_DATE_OF_CONFINEMENT_CONFIRMED);
	}

	public String getTroubledPhoneProperty() {
		return contextService.getAdministrationService().getGlobalProperty(
				MotechConstants.GLOBAL_PROPERTY_TROUBLED_PHONE);
	}
	/* Factored out methods end */
}
