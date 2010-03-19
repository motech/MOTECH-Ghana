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
import org.motech.model.Service;
import org.motech.model.ServiceStatus;
import org.motech.model.TroubledPhone;
import org.motech.model.WhoRegistered;
import org.motech.model.WhyInterested;
import org.motech.openmrs.module.ContextService;
import org.motech.openmrs.module.MotechService;
import org.motech.openmrs.module.tasks.MessageProgramUpdateTask;
import org.motech.openmrs.module.tasks.NotificationTask;
import org.motech.svc.BirthOutcomeChild;
import org.motech.svc.RegistrarBean;
import org.motech.util.GenderTypeConverter;
import org.motech.util.MotechConstants;
import org.motechproject.ws.BirthOutcome;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.DeliveredBy;
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
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
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

	public Patient registerChild(User nurse, Patient mother, String childId,
			Date birthDate, Gender sex, String firstName, String nhis,
			Date nhisExpires) {

		PatientService patientService = contextService.getPatientService();

		PersonAddress motherAddress = mother.getPersonAddress();

		String region = null;
		String district = null;
		String community = null;
		String address = null;

		if (motherAddress != null) {
			region = motherAddress.getRegion();
			district = motherAddress.getCountyDistrict();
			community = motherAddress.getCityVillage();
			address = motherAddress.getAddress1();
		}

		Patient child = createPatient(childId, firstName, null, mother
				.getFamilyName(), null, birthDate, false, sex, null, childId,
				null, null, nhis, nhisExpires, null, region, district,
				community, address, null, null, null, null, null, null, null,
				null, null, null, null, WhoRegistered.CHPS_STAFF);

		return patientService.savePatient(child);
	}

	public void registerChild(String firstName, String middleName,
			String lastName, String prefName, Date birthDate,
			Boolean birthDateEst, Gender sex, String motherMotechId,
			Boolean registeredGHS, String regNumberGHS, Boolean insured,
			String nhis, Date nhisExpDate, String region, String district,
			String community, String address, Integer clinic,
			Boolean registerPregProgram, String primaryPhone,
			ContactNumberType primaryPhoneType, String secondaryPhone,
			ContactNumberType secondaryPhoneType, MediaType mediaTypeInfo,
			MediaType mediaTypeReminder, String languageVoice,
			String languageText, WhoRegistered whoRegistered) {

		PatientService patientService = contextService.getPatientService();
		PersonService personService = contextService.getPersonService();

		Patient child = createPatient(regNumberGHS, firstName, middleName,
				lastName, prefName, birthDate, birthDateEst, sex,
				registeredGHS, regNumberGHS, null, insured, nhis, nhisExpDate,
				null, region, district, community, address, clinic,
				primaryPhone, primaryPhoneType, secondaryPhone,
				secondaryPhoneType, mediaTypeInfo, mediaTypeReminder,
				languageVoice, languageText, null, null, whoRegistered);

		child = patientService.savePatient(child);

		if (motherMotechId != null) {

			Patient mother = getPatientByMotechId(motherMotechId);

			if (mother != null) {
				RelationshipType parentChildRelationshipType = personService
						.getRelationshipTypeByName(MotechConstants.RELATIONSHIP_TYPE_PARENT_CHILD);
				Relationship motherRelationship = new Relationship(mother,
						child, parentChildRelationshipType);
				personService.saveRelationship(motherRelationship);
			}
		}

		if (registerPregProgram) {
			addMessageProgramEnrollment(child.getPatientId(),
					"Weekly Info Child Message Program", null);
		}
	}

	private Patient createPatient(String motechId, String firstName,
			String middleName, String lastName, String prefName,
			Date birthDate, Boolean birthDateEst, Gender sex,
			Boolean registeredGHS, String regNumberCWC, String regNumberANC,
			Boolean insured, String nhis, Date nhisExpDate,
			HIVStatus hivStatus, String region, String district,
			String community, String address, Integer clinic,
			String primaryPhone, ContactNumberType primaryPhoneType,
			String secondaryPhone, ContactNumberType secondaryPhoneType,
			MediaType mediaTypeInfo, MediaType mediaTypeReminder,
			String languageVoice, String languageText, String religion,
			String occupation, WhoRegistered whoRegistered) {

		Person person = createPerson(firstName, middleName, lastName, prefName,
				birthDate, birthDateEst, sex, region, district, community,
				address, clinic, primaryPhone, primaryPhoneType,
				secondaryPhone, secondaryPhoneType, mediaTypeInfo,
				mediaTypeReminder, languageVoice, languageText, religion,
				occupation, null, null, whoRegistered, registeredGHS,
				regNumberCWC, regNumberANC, hivStatus, insured, nhis,
				nhisExpDate);

		Patient patient = new Patient(person);

		patient.addIdentifier(new PatientIdentifier(motechId,
				getMotechPatientIdType(), getGhanaLocation()));

		return patient;
	}

	public void registerClinic(String name, Integer parentId) {

		LocationService locationService = contextService.getLocationService();

		Location clinic = new Location();
		clinic.setName(name);
		clinic.setDescription("A Ghana Clinic Location");
		locationService.saveLocation(clinic);

		if (parentId != null) {
			Location parent = locationService.getLocation(parentId);
			parent.addChildLocation(clinic);
			locationService.saveLocation(parent);

			copyParentHierarchy(parent, clinic);
			locationService.saveLocation(clinic);
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
			WhoRegistered whoRegistered, String religion, String occupation) {

		PatientService patientService = contextService.getPatientService();

		Patient mother = createPatient(regNumberGHS, firstName, middleName,
				lastName, prefName, birthDate, birthDateEst, Gender.FEMALE,
				registeredGHS, null, regNumberGHS, insured, nhis, nhisExpDate,
				hivStatus, region, district, community, address, clinic,
				primaryPhone, primaryPhoneType, secondaryPhone,
				secondaryPhoneType, mediaTypeInfo, mediaTypeReminder,
				languageVoice, languageText, religion, occupation,
				whoRegistered);

		mother = patientService.savePatient(mother);

		Integer dueDateObsId = registerPregnancy(mother, dueDate,
				dueDateConfirmed, gravida, parity);

		if (registerPregProgram) {
			addMessageProgramEnrollment(mother.getPatientId(),
					"Weekly Pregnancy Message Program", dueDateObsId);
		}
	}

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
			WhoRegistered whoRegistered, String religion, String occupation) {

		PatientService patientService = contextService.getPatientService();

		Patient patient = createPatient(regNumberGHS, firstName, middleName,
				lastName, prefName, birthDate, birthDateEst, sex,
				registeredGHS, null, null, insured, nhis, nhisExpDate, null,
				region, district, community, address, clinic, primaryPhone,
				primaryPhoneType, secondaryPhone, secondaryPhoneType,
				mediaTypeInfo, mediaTypeReminder, languageVoice, languageText,
				religion, occupation, whoRegistered);

		patient = patientService.savePatient(patient);

		if (registerPregProgram) {
			addMessageProgramEnrollment(patient.getPatientId(),
					"Demo Minute Message Program", null);
		}
	}

	public void demoEnrollPatient(String regNumGHS) {
		Patient patient = getPatientByMotechId(regNumGHS);
		addMessageProgramEnrollment(patient.getPersonId(),
				"Input Demo Message Program", null);
	}

	public void registerPerson(String firstName, String middleName,
			String lastName, String prefName, Date birthDate,
			Boolean birthDateEst, Gender sex, String region, String district,
			String community, String address, Integer clinic,
			Boolean registerPregProgram, Integer messagesStartWeek,
			String primaryPhone, ContactNumberType primaryPhoneType,
			String secondaryPhone, ContactNumberType secondaryPhoneType,
			MediaType mediaTypeInfo, MediaType mediaTypeReminder,
			String languageVoice, String languageText, String howLearned,
			String religion, String occupation, WhyInterested whyInterested) {

		PersonService personService = contextService.getPersonService();

		Person person = createPerson(firstName, middleName, lastName, prefName,
				birthDate, birthDateEst, sex, region, district, community,
				address, clinic, primaryPhone, primaryPhoneType,
				secondaryPhone, secondaryPhoneType, mediaTypeInfo,
				mediaTypeReminder, languageVoice, languageText, religion,
				occupation, howLearned, whyInterested, null, null, null, null,
				null, null, null, null);

		person = personService.savePerson(person);

		Integer refDateObsId = null;

		if (messagesStartWeek != null) {
			ObsService obsService = contextService.getObsService();

			Location ghanaLocation = getGhanaLocation();
			Date currentDate = new Date();

			Calendar calendar = Calendar.getInstance();
			// Convert weeks to days, plus one day
			calendar.add(Calendar.DATE, (messagesStartWeek * -7) + 1);
			Date referenceDate = calendar.getTime();

			Obs refDateObs = createDateValueObs(currentDate,
					getEnrollmentReferenceDateConcept(), person, ghanaLocation,
					referenceDate, null, null);

			obsService.saveObs(refDateObs, null);

			refDateObsId = refDateObs.getObsId();
		}

		if (registerPregProgram) {
			addMessageProgramEnrollment(person.getPersonId(),
					"Weekly Info Pregnancy Message Program", refDateObsId);
		}
	}

	private Person createPerson(String firstName, String middleName,
			String lastName, String prefName, Date birthDate,
			Boolean birthDateEst, Gender sex, String region, String district,
			String community, String address, Integer clinic,
			String primaryPhone, ContactNumberType primaryPhoneType,
			String secondaryPhone, ContactNumberType secondaryPhoneType,
			MediaType mediaTypeInfo, MediaType mediaTypeReminder,
			String languageVoice, String languageText, String religion,
			String occupation, String howLearned, WhyInterested whyInterested,
			WhoRegistered whoRegistered, Boolean registeredGHS,
			String regNumberCWC, String regNumberANC, HIVStatus hivStatus,
			Boolean insured, String nhis, Date nhisExpDate) {

		Person person = new Person();

		person.addName(new PersonName(firstName, middleName, lastName));

		if (prefName != null) {
			PersonName preferredPersonName = new PersonName(prefName,
					middleName, lastName);
			preferredPersonName.setPreferred(true);
			person.addName(preferredPersonName);
		}

		person.setGender(GenderTypeConverter.toOpenMRSString(sex));
		person.setBirthdate(birthDate);
		person.setBirthdateEstimated(birthDateEst);

		PersonAddress personAddress = new PersonAddress();
		personAddress.setAddress1(address);
		personAddress.setCityVillage(community);
		personAddress.setCountyDistrict(district);
		personAddress.setRegion(region);
		personAddress.setCountry(MotechConstants.LOCATION_GHANA);
		person.addAddress(personAddress);

		setPersonAttributes(person, clinic, primaryPhone, primaryPhoneType,
				secondaryPhone, secondaryPhoneType, mediaTypeInfo,
				mediaTypeReminder, languageVoice, languageText, religion,
				occupation, howLearned, whyInterested, whoRegistered,
				registeredGHS, regNumberCWC, regNumberANC, hivStatus, insured,
				nhis, nhisExpDate);

		return person;
	}

	private void setPersonAttributes(Person person, Integer clinic,
			String primaryPhone, ContactNumberType primaryPhoneType,
			String secondaryPhone, ContactNumberType secondaryPhoneType,
			MediaType mediaTypeInfo, MediaType mediaTypeReminder,
			String languageVoice, String languageText, String religion,
			String occupation, String howLearned, WhyInterested whyInterested,
			WhoRegistered whoRegistered, Boolean registeredGHS,
			String regNumberCWC, String regNumberANC, HIVStatus hivStatus,
			Boolean insured, String nhis, Date nhisExpDate) {

		if (clinic != null) {
			person.addAttribute(new PersonAttribute(getClinicAttributeType(),
					clinic.toString()));
		}

		if (primaryPhone != null) {
			person.addAttribute(new PersonAttribute(
					getPrimaryPhoneNumberAttributeType(), primaryPhone));
		}

		if (primaryPhoneType != null) {
			person
					.addAttribute(new PersonAttribute(
							getPrimaryPhoneTypeAttributeType(),
							primaryPhoneType.name()));
		}

		if (secondaryPhone != null) {
			person.addAttribute(new PersonAttribute(
					getSecondaryPhoneNumberAttributeType(), secondaryPhone));
		}

		if (secondaryPhoneType != null) {
			person.addAttribute(new PersonAttribute(
					getSecondaryPhoneTypeAttributeType(), secondaryPhoneType
							.name()));
		}

		if (mediaTypeInfo != null) {
			person.addAttribute(new PersonAttribute(
					getMediaTypeInformationalAttributeType(), mediaTypeInfo
							.name()));
		}

		if (mediaTypeReminder != null) {
			person.addAttribute(new PersonAttribute(
					getMediaTypeReminderAttributeType(), mediaTypeReminder
							.name()));
		}

		if (languageText != null) {
			person.addAttribute(new PersonAttribute(
					getLanguageTextAttributeType(), languageText));
		}

		if (languageVoice != null) {
			person.addAttribute(new PersonAttribute(
					getLanguageVoiceAttributeType(), languageVoice));
		}

		if (religion != null) {
			person.addAttribute(new PersonAttribute(getReligionAttributeType(),
					religion));
		}

		if (occupation != null) {
			person.addAttribute(new PersonAttribute(
					getOccupationAttributeType(), occupation));
		}

		if (howLearned != null) {
			person.addAttribute(new PersonAttribute(
					getHowLearnedAttributeType(), howLearned));
		}

		if (whyInterested != null) {
			person.addAttribute(new PersonAttribute(
					getWhyInterestedAttributeType(), whyInterested.name()));
		}

		if (whoRegistered != null) {
			person.addAttribute(new PersonAttribute(
					getWhoRegisteredAttributeType(), whoRegistered.name()));
		}

		if (registeredGHS != null) {
			person.addAttribute(new PersonAttribute(
					getGHSRegisteredAttributeType(), registeredGHS.toString()));
		}

		if (regNumberCWC != null) {
			person.addAttribute(new PersonAttribute(
					getCWCRegistrationNumberAttributeType(), regNumberCWC));
		}

		if (regNumberANC != null) {
			person.addAttribute(new PersonAttribute(
					getANCRegistrationNumberAttributeType(), regNumberANC));
		}

		if (hivStatus != null) {
			person.addAttribute(new PersonAttribute(
					getHIVStatusAttributeType(), hivStatus.name()));
		}

		if (insured != null) {
			person.addAttribute(new PersonAttribute(getInsuredAttributeType(),
					insured.toString()));
		}

		if (nhis != null) {
			person.addAttribute(new PersonAttribute(
					getNHISNumberAttributeType(), nhis));
		}

		if (nhisExpDate != null) {
			person.addAttribute(new PersonAttribute(
					getNHISExpirationDateAttributeType(), nhisExpDate
							.toString()));
		}
	}

	public void editPatient(User nurse, Patient patient, String primaryPhone,
			ContactNumberType primaryPhoneType, String secondaryPhone,
			ContactNumberType secondaryPhoneType, String nhis, Date nhisExpires) {

		PatientService patientService = contextService.getPatientService();

		setPersonAttributes(patient, null, primaryPhone, primaryPhoneType,
				secondaryPhone, secondaryPhoneType, null, null, null, null,
				null, null, null, null, null, null, null, null, null, null,
				nhis, nhisExpires);

		patientService.savePatient(patient);
	}

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
			HIVStatus hivStatus) {

		PatientService patientService = contextService.getPatientService();

		Patient patient = patientService.getPatient(id);
		if (patient == null) {
			log.error("No matching patient for id: " + id);
			return;
		}

		patient.setBirthdate(birthDate);
		patient.setBirthdateEstimated(birthDateEst);
		patient.setGender(GenderTypeConverter.toOpenMRSString(sex));

		Set<PersonName> patientNames = patient.getNames();
		if (patientNames.isEmpty()) {
			patient.addName(new PersonName(firstName, middleName, lastName));
			if (prefName != null) {
				PersonName preferredPersonName = new PersonName(prefName,
						middleName, lastName);
				preferredPersonName.setPreferred(true);
				patient.addName(preferredPersonName);
			}
		} else {
			for (PersonName name : patient.getNames()) {
				if (name.isPreferred()) {
					if (prefName != null) {
						name.setGivenName(prefName);
						name.setFamilyName(lastName);
						name.setMiddleName(middleName);
					} else {
						patient.removeName(name);
					}
				} else {
					name.setGivenName(firstName);
					name.setMiddleName(middleName);
					name.setFamilyName(lastName);
				}
			}
		}

		PersonAddress patientAddress = patient.getPersonAddress();
		if (patientAddress == null) {
			patientAddress = new PersonAddress();
			patientAddress.setRegion(region);
			patientAddress.setCountyDistrict(district);
			patientAddress.setCityVillage(community);
			patientAddress.setAddress1(address);
			patient.addAddress(patientAddress);
		} else {
			patientAddress.setRegion(region);
			patientAddress.setCountyDistrict(district);
			patientAddress.setCityVillage(community);
			patientAddress.setAddress1(address);
		}

		PatientIdentifier patientId = patient.getPatientIdentifier();
		if (patientId == null) {
			patientId = new PatientIdentifier();
			patientId.setIdentifierType(getMotechPatientIdType());
			patientId.setLocation(getGhanaLocation());
			patientId.setIdentifier(regNumberGHS);
			patient.addIdentifier(patientId);
		} else {
			patientId.setIdentifier(regNumberGHS);
		}

		setPersonAttributes(patient, clinic, primaryPhone, primaryPhoneType,
				secondaryPhone, secondaryPhoneType, mediaTypeInfo,
				mediaTypeReminder, languageVoice, languageText, religion,
				occupation, null, null, null, registeredGHS, null, null,
				hivStatus, insured, nhis, nhisExpDate);

		patientService.savePatient(patient);
	}

	public void stopPregnancyProgram(User nurse, Patient patient) {

		String[] pregnancyPrograms = { "Weekly Pregnancy Message Program",
				"Weekly Info Pregnancy Message Program" };

		Integer patientId = patient.getPatientId();

		for (String programName : pregnancyPrograms) {
			removeMessageProgramEnrollment(patientId, programName, null);
		}
	}

	public void registerPregnancy(Integer id, Date dueDate,
			Boolean dueDateConfirmed, Boolean registerPregProgram,
			String primaryPhone, ContactNumberType primaryPhoneType,
			String secondaryPhone, ContactNumberType secondaryPhoneType,
			MediaType mediaTypeInfo, MediaType mediaTypeReminder,
			String languageVoice, String languageText,
			WhoRegistered whoRegistered, String howLearned) {

		PatientService patientService = contextService.getPatientService();

		Patient patient = patientService.getPatient(id);
		if (patient == null) {
			log.error("No matching patient for id: " + id);
			return;
		}

		setPersonAttributes(patient, null, primaryPhone, primaryPhoneType,
				secondaryPhone, secondaryPhoneType, mediaTypeInfo,
				mediaTypeReminder, languageVoice, languageText, null, null,
				howLearned, null, whoRegistered, null, null, null, null, null,
				null, null);

		patientService.savePatient(patient);

		Integer dueDateObsId = registerPregnancy(patient, dueDate,
				dueDateConfirmed, null, null);

		if (registerPregProgram) {
			addMessageProgramEnrollment(patient.getPatientId(),
					"Weekly Pregnancy Message Program", dueDateObsId);
		}
	}

	private Integer registerPregnancy(Patient patient, Date dueDate,
			Boolean dueDateConfirmed, Integer gravida, Integer parity) {

		EncounterService encounterService = contextService
				.getEncounterService();
		ObsService obsService = contextService.getObsService();

		Date currentDate = new Date();
		Location ghanaLocation = getGhanaLocation();

		Encounter encounter = new Encounter();
		encounter
				.setEncounterType(getPregnancyRegistrationVisitEncounterType());
		encounter.setEncounterDatetime(currentDate);
		encounter.setPatient(patient);
		encounter.setLocation(ghanaLocation);
		encounter.setProvider(contextService.getAuthenticatedUser());
		encounter = encounterService.saveEncounter(encounter);

		Obs pregnancyObs = createObs(currentDate, getPregnancyConcept(),
				patient, ghanaLocation, encounter, null);

		Obs pregnancyStatusObs = createBooleanValueObs(currentDate,
				getPregnancyStatusConcept(), patient, ghanaLocation,
				Boolean.TRUE, encounter, null);
		pregnancyObs.addGroupMember(pregnancyStatusObs);

		Obs dueDateObs = null;
		if (dueDate != null) {
			dueDateObs = createDateValueObs(currentDate, getDueDateConcept(),
					patient, ghanaLocation, dueDate, encounter, null);
			pregnancyObs.addGroupMember(dueDateObs);
		}

		if (dueDateConfirmed != null) {
			Obs dueDateConfirmedObs = createBooleanValueObs(currentDate,
					getDueDateConfirmedConcept(), patient, ghanaLocation,
					dueDateConfirmed, encounter, null);
			pregnancyObs.addGroupMember(dueDateConfirmedObs);
		}

		if (gravida != null) {
			Obs gravidaObs = createNumericValueObs(currentDate,
					getGravidaConcept(), patient, ghanaLocation, gravida,
					encounter, null);
			pregnancyObs.addGroupMember(gravidaObs);
		}

		if (parity != null) {
			Obs parityObs = createNumericValueObs(currentDate,
					getParityConcept(), patient, ghanaLocation, parity,
					encounter, null);
			pregnancyObs.addGroupMember(parityObs);
		}

		obsService.saveObs(pregnancyObs, null);

		if (dueDateObs != null) {
			return dueDateObs.getObsId();
		}
		return null;
	}

	public void recordMotherANCVisit(User nurse, Date date, Patient patient,
			Integer visitNumber, Integer ttDose, Integer iptDose,
			Boolean itnUse, org.motechproject.ws.HIVStatus hivStatus) {

		EncounterService encounterService = contextService
				.getEncounterService();
		ObsService obsService = contextService.getObsService();

		Location location = getGhanaLocation();

		Encounter encounter = new Encounter();
		encounter.setEncounterType(getANCVisitEncounterType());
		encounter.setEncounterDatetime(date);
		encounter.setPatient(patient);
		encounter.setLocation(location);
		encounter.setProvider(contextService.getAuthenticatedUser());
		encounter = encounterService.saveEncounter(encounter);

		Obs pregnancyObs = getActivePregnancy(patient.getPatientId());

		if (visitNumber != null) {
			Obs visitNumberObs = createNumericValueObs(date,
					getVisitNumberConcept(), patient, location, visitNumber,
					encounter, null);
			visitNumberObs.setObsGroup(pregnancyObs);
			obsService.saveObs(visitNumberObs, null);
		}
		if (ttDose != null) {
			Obs ttDoseObs = createNumericValueObs(date,
					getTetanusDoseConcept(), patient, location, ttDose,
					encounter, null);
			ttDoseObs.setObsGroup(pregnancyObs);
			obsService.saveObs(ttDoseObs, null);
		}
		if (iptDose != null) {
			Obs iptDoseObs = createNumericValueObs(date, getIPTDoseConcept(),
					patient, location, iptDose, encounter, null);
			iptDoseObs.setObsGroup(pregnancyObs);
			obsService.saveObs(iptDoseObs, null);
		}
		if (itnUse != null) {
			Obs itnUseObs = createBooleanValueObs(date, getITNConcept(),
					patient, location, itnUse, encounter, null);
			itnUseObs.setObsGroup(pregnancyObs);
			obsService.saveObs(itnUseObs, null);
		}
		if (hivStatus != null) {
			Obs hivStatusObs = createTextValueObs(date, getHIVStatusConcept(),
					patient, location, hivStatus.name(), encounter, null);
			hivStatusObs.setObsGroup(pregnancyObs);
			obsService.saveObs(hivStatusObs, null);
		}
	}

	public void recordPregnancyTermination(User nurse, Date date,
			Patient patient, Integer abortionType, Integer complication) {

		EncounterService encounterService = contextService
				.getEncounterService();
		ObsService obsService = contextService.getObsService();

		Location location = getGhanaLocation();

		Encounter encounter = new Encounter();
		encounter.setEncounterType(getPregnancyTerminationVisitEncounterType());
		encounter.setEncounterDatetime(date);
		encounter.setPatient(patient);
		encounter.setLocation(location);
		encounter.setProvider(contextService.getAuthenticatedUser());
		encounter = encounterService.saveEncounter(encounter);

		Obs pregnancyObs = getActivePregnancy(patient.getPatientId());

		if (abortionType != null) {
			Obs abortionTypeObs = createNumericValueObs(date,
					getAbortionTypeConcept(), patient, location, abortionType,
					encounter, null);
			abortionTypeObs.setObsGroup(pregnancyObs);
			obsService.saveObs(abortionTypeObs, null);
		}
		if (complication != null) {
			Obs complicationObs = createNumericValueObs(date,
					getComplicationConcept(), patient, location, complication,
					encounter, null);
			complicationObs.setObsGroup(pregnancyObs);
			obsService.saveObs(complicationObs, null);
		}
		Obs pregnancyStatusObs = createBooleanValueObs(date,
				getPregnancyStatusConcept(), patient, location, Boolean.FALSE,
				encounter, null);
		pregnancyStatusObs.setObsGroup(pregnancyObs);
		obsService.saveObs(pregnancyStatusObs, null);
	}

	public void recordPregnancyDelivery(User nurse, Date date, Patient patient,
			Integer method, Integer outcome, Integer location,
			DeliveredBy deliveredBy, Boolean maternalDeath, Integer cause,
			BirthOutcomeChild[] outcomes) {

		EncounterService encounterService = contextService
				.getEncounterService();
		ObsService obsService = contextService.getObsService();

		Location encounterLocation = getGhanaLocation();

		Encounter encounter = new Encounter();
		encounter.setEncounterType(getPregnancyDeliveryVisitEncounterType());
		encounter.setEncounterDatetime(date);
		encounter.setPatient(patient);
		encounter.setLocation(encounterLocation);
		encounter.setProvider(contextService.getAuthenticatedUser());
		encounter = encounterService.saveEncounter(encounter);

		Obs pregnancyObs = getActivePregnancy(patient.getPatientId());

		if (method != null) {
			Obs methodObs = createNumericValueObs(date,
					getDeliveryMethodConcept(), patient, encounterLocation,
					method, encounter, null);
			obsService.saveObs(methodObs, null);
		}
		if (outcome != null) {
			Obs outcomeObs = createNumericValueObs(date,
					getDeliveryOutcomeConcept(), patient, encounterLocation,
					outcome, encounter, null);
			obsService.saveObs(outcomeObs, null);
		}
		if (location != null) {
			Obs locationObs = createNumericValueObs(date,
					getDeliveryLocationConcept(), patient, encounterLocation,
					location, encounter, null);
			obsService.saveObs(locationObs, null);
		}
		if (deliveredBy != null) {
			Obs deliveredByObs = createTextValueObs(date,
					getDeliveredByConcept(), patient, encounterLocation,
					deliveredBy.name(), encounter, null);
			obsService.saveObs(deliveredByObs, null);
		}

		Obs pregnancyStatusObs = createBooleanValueObs(date,
				getPregnancyStatusConcept(), patient, encounterLocation,
				Boolean.FALSE, encounter, null);
		pregnancyStatusObs.setObsGroup(pregnancyObs);
		obsService.saveObs(pregnancyStatusObs, null);

		for (BirthOutcomeChild childOutcome : outcomes) {
			if (childOutcome.getOutcome() != null) {
				Obs childOutcomeObs = createTextValueObs(date,
						getBirthOutcomeConcept(), patient, encounterLocation,
						childOutcome.getOutcome().name(), encounter, null);
				obsService.saveObs(childOutcomeObs, null);
			}

			Patient child = registerChild(null, patient, childOutcome
					.getPatientId(), date, childOutcome.getSex(), childOutcome
					.getFirstName(), null, null);

			Integer opvDose = null;
			if (Boolean.TRUE.equals(childOutcome.getOpv())) {
				opvDose = 0;
			}

			if (Boolean.TRUE.equals(childOutcome.getBcg()) || opvDose != null) {
				recordChildPNCVisit(nurse, date, child, childOutcome.getBcg(),
						opvDose, null, null, null, null, null, null);
			}

			if (BirthOutcome.A != childOutcome.getOutcome()) {
				processPatientDeath(child, date);
			}
		}

		if (Boolean.TRUE.equals(maternalDeath)) {
			if (cause != null) {
				Obs maternalDeathCauseObs = createNumericValueObs(date,
						getMaternalDeathCauseConcept(), patient,
						encounterLocation, cause, encounter, null);
				obsService.saveObs(maternalDeathCauseObs, null);
			}

			processPatientDeath(patient, date);
		}
	}

	public void recordMotherPPCVisit(User nurse, Date date, Patient patient,
			Integer visitNumber, Boolean vitaminA, Integer ttDose) {

		EncounterService encounterService = contextService
				.getEncounterService();
		ObsService obsService = contextService.getObsService();

		Location location = getGhanaLocation();

		Encounter encounter = new Encounter();
		encounter.setEncounterType(getPPCVisitEncounterType());
		encounter.setEncounterDatetime(date);
		encounter.setPatient(patient);
		encounter.setLocation(location);
		encounter.setProvider(contextService.getAuthenticatedUser());
		encounter = encounterService.saveEncounter(encounter);

		if (visitNumber != null) {
			Obs visitNumberObs = createNumericValueObs(date,
					getVisitNumberConcept(), patient, location, visitNumber,
					encounter, null);
			obsService.saveObs(visitNumberObs, null);
		}
		if (Boolean.TRUE.equals(vitaminA)) {
			Obs vitaminAObs = createConceptValueObs(date,
					getImmunizationsOrderedConcept(), patient, location,
					getVitaminAConcept(), encounter, null);
			obsService.saveObs(vitaminAObs, null);
		}
		if (ttDose != null) {
			Obs ttDoseObs = createNumericValueObs(date,
					getTetanusDoseConcept(), patient, location, ttDose,
					encounter, null);
			obsService.saveObs(ttDoseObs, null);
		}
	}

	public void recordDeath(User nurse, Date date, Patient patient,
			Integer cause) {

		ObsService obsService = contextService.getObsService();

		Location location = getGhanaLocation();

		if (cause != null) {
			Obs deathCauseObs = createNumericValueObs(date,
					getDeathCauseConcept(), patient, location, cause, null,
					null);
			obsService.saveObs(deathCauseObs, null);
		}

		processPatientDeath(patient, date);
	}

	private void processPatientDeath(Patient patient, Date date) {
		PatientService patientService = contextService.getPatientService();
		PersonService personService = contextService.getPersonService();

		// Stop all messages and remove all message program enrollments
		removeAllMessageProgramEnrollments(patient.getPatientId());

		patient.setDead(true);
		patient.setDeathDate(date);
		patient = patientService.savePatient(patient);

		personService.voidPerson(patient, "Deceased");
	}

	public void recordChildPNCVisit(User nurse, Date date, Patient patient,
			Boolean bcg, Integer opvDose, Integer pentaDose,
			Boolean yellowFever, Boolean csm, Boolean measles, Boolean ipti,
			Boolean vitaminA) {

		EncounterService encounterService = contextService
				.getEncounterService();
		ObsService obsService = contextService.getObsService();

		Location location = getGhanaLocation();

		Encounter encounter = new Encounter();
		encounter.setEncounterType(getPNCVisitEncounterType());
		encounter.setEncounterDatetime(date);
		encounter.setPatient(patient);
		encounter.setLocation(location);
		encounter.setProvider(contextService.getAuthenticatedUser());
		encounter = encounterService.saveEncounter(encounter);

		if (Boolean.TRUE.equals(bcg)) {
			Obs bcgObs = createConceptValueObs(date,
					getImmunizationsOrderedConcept(), patient, location,
					getBCGConcept(), encounter, null);
			obsService.saveObs(bcgObs, null);
		}
		if (opvDose != null) {
			Obs opvDoseObs = createNumericValueObs(date, getOPVDoseConcept(),
					patient, location, opvDose, encounter, null);
			obsService.saveObs(opvDoseObs, null);
		}
		if (pentaDose != null) {
			Obs pentaDoseObs = createNumericValueObs(date,
					getPentaDoseConcept(), patient, location, pentaDose,
					encounter, null);
			obsService.saveObs(pentaDoseObs, null);
		}
		if (Boolean.TRUE.equals(yellowFever)) {
			Obs yellowFeverObs = createConceptValueObs(date,
					getImmunizationsOrderedConcept(), patient, location,
					getYellowFeverConcept(), encounter, null);
			obsService.saveObs(yellowFeverObs, null);
		}
		if (Boolean.TRUE.equals(csm)) {
			Obs csmObs = createConceptValueObs(date,
					getImmunizationsOrderedConcept(), patient, location,
					getCSMConcept(), encounter, null);
			obsService.saveObs(csmObs, null);
		}
		if (Boolean.TRUE.equals(measles)) {
			Obs measlesObs = createConceptValueObs(date,
					getImmunizationsOrderedConcept(), patient, location,
					getMeaslesConcept(), encounter, null);
			obsService.saveObs(measlesObs, null);
		}
		if (Boolean.TRUE.equals(ipti)) {
			Obs iptiObs = createConceptValueObs(date,
					getImmunizationsOrderedConcept(), patient, location,
					getIPTiConcept(), encounter, null);
			obsService.saveObs(iptiObs, null);
		}
		if (Boolean.TRUE.equals(vitaminA)) {
			Obs vitaminAObs = createConceptValueObs(date,
					getImmunizationsOrderedConcept(), patient, location,
					getVitaminAConcept(), encounter, null);
			obsService.saveObs(vitaminAObs, null);
		}
	}

	public void recordGeneralVisit(String chpsId, Date date,
			String serialNumber, Gender sex, Date birthDate, Boolean insured,
			Boolean newCase, Integer diagnosis, Integer secondaryDiagnosis,
			Boolean referral) {

		log.debug("Date: " + date + ", CHPS Id: " + chpsId + ", Serial: "
				+ serialNumber + ", Sex: " + sex + ", Birthdate: " + birthDate
				+ ", Insured: " + insured + ", New Case: " + newCase
				+ ", Diagnosis: " + diagnosis + ", Sec Diagnosis: "
				+ secondaryDiagnosis + ", Referral: " + referral);

		MotechService motechService = contextService.getMotechService();

		GeneralPatientEncounter encounter = new GeneralPatientEncounter();
		encounter.setFacilityId(chpsId);
		encounter.setDate(date);
		encounter.setSerialNumber(serialNumber);
		encounter.setSex(sex);
		encounter.setBirthDate(birthDate);
		encounter.setInsured(insured);
		encounter.setNewCase(newCase);
		encounter.setDiagnosis(diagnosis);
		encounter.setSecondaryDiagnosis(secondaryDiagnosis);
		encounter.setReferral(referral);

		motechService.saveGeneralPatientEncounter(encounter);
	}

	public void recordChildVisit(User nurse, Date date, Patient patient,
			String serialNumber, Boolean newCase, Integer diagnosis,
			Integer secondDiagnosis, Boolean referral) {

		recordMotherChildGeneralVisit(nurse, date, patient, serialNumber,
				newCase, diagnosis, secondDiagnosis, referral);
	}

	public void recordMotherVisit(User nurse, Date date, Patient patient,
			String serialNumber, Boolean newCase, Integer diagnosis,
			Integer secondDiagnosis, Boolean referral) {

		recordMotherChildGeneralVisit(nurse, date, patient, serialNumber,
				newCase, diagnosis, secondDiagnosis, referral);
	}

	private void recordMotherChildGeneralVisit(User nurse, Date date,
			Patient patient, String serialNumber, Boolean newCase,
			Integer diagnosis, Integer secondDiagnosis, Boolean referral) {

		EncounterService encounterService = contextService
				.getEncounterService();
		ObsService obsService = contextService.getObsService();

		Location location = getGhanaLocation();

		Encounter encounter = new Encounter();
		encounter.setEncounterType(getGeneralVisitEncounterType());
		encounter.setEncounterDatetime(date);
		encounter.setPatient(patient);
		encounter.setLocation(location);
		encounter.setProvider(contextService.getAuthenticatedUser());
		encounter = encounterService.saveEncounter(encounter);

		if (serialNumber != null) {
			Obs serialNumberObs = createTextValueObs(date,
					getSerialNumberConcept(), patient, location, serialNumber,
					encounter, null);
			obsService.saveObs(serialNumberObs, null);
		}
		if (newCase != null) {
			Obs newCaseObs = createBooleanValueObs(date, getNewCaseConcept(),
					patient, location, newCase, encounter, null);
			obsService.saveObs(newCaseObs, null);
		}
		if (diagnosis != null) {
			Obs diagnosisObs = createNumericValueObs(date,
					getPrimaryDiagnosisConcept(), patient, location, diagnosis,
					encounter, null);
			obsService.saveObs(diagnosisObs, null);
		}
		if (secondDiagnosis != null) {
			Obs secondDiagnosisObs = createNumericValueObs(date,
					getSecondaryDiagnosisConcept(), patient, location,
					secondDiagnosis, encounter, null);
			obsService.saveObs(secondDiagnosisObs, null);
		}
		if (referral != null) {
			Obs referralObs = createBooleanValueObs(date, getReferralConcept(),
					patient, location, referral, encounter, null);
			obsService.saveObs(referralObs, null);
		}
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
	public List<Location> getAllLocations() {
		LocationService locationService = contextService.getLocationService();
		return locationService.getAllLocations();
	}

	public List<User> getAllNurses() {
		UserService userService = contextService.getUserService();
		return userService.getAllUsers();
	}

	public List<Patient> getAllPatients() {
		PatientService patientService = contextService.getPatientService();
		List<PatientIdentifierType> motechPatientIdType = new ArrayList<PatientIdentifierType>();
		motechPatientIdType.add(getMotechPatientIdType());
		return patientService.getPatients(null, null, motechPatientIdType,
				false);
	}

	public List<Patient> getPatients(String firstName, String lastName,
			String preferredName, Date birthDate, String community,
			String phoneNumber, String nhisNumber) {

		MotechService motechService = contextService.getMotechService();

		PersonAttributeType primaryPhoneNumberAttrType = getPrimaryPhoneNumberAttributeType();
		PersonAttributeType secondaryPhoneNumberAttrType = getSecondaryPhoneNumberAttributeType();
		PersonAttributeType nhisAttrType = getNHISNumberAttributeType();

		return motechService.getPatients(firstName, lastName, preferredName,
				birthDate, community, phoneNumber, primaryPhoneNumberAttrType,
				secondaryPhoneNumberAttrType, nhisNumber, nhisAttrType);
	}

	public List<Person> getMatchingPeople(String firstName, String lastName,
			Date birthDate, String community, String phoneNumber,
			String patientId, String nhisNumber) {

		MotechService motechService = contextService.getMotechService();
		PersonService personService = contextService.getPersonService();

		Integer primaryPhoneNumberAttrTypeId = getPrimaryPhoneNumberAttributeType()
				.getPersonAttributeTypeId();
		Integer secondaryPhoneNumberAttrTypeId = getSecondaryPhoneNumberAttributeType()
				.getPersonAttributeTypeId();
		Integer nhisAttrTypeId = getNHISNumberAttributeType()
				.getPersonAttributeTypeId();

		List<Integer> personIds = motechService.getMatchingPeople(firstName,
				lastName, birthDate, community, phoneNumber,
				primaryPhoneNumberAttrTypeId, secondaryPhoneNumberAttrTypeId,
				patientId, nhisNumber, nhisAttrTypeId);
		List<Person> matchingPeople = new ArrayList<Person>();
		for (Integer personId : personIds) {
			matchingPeople.add(personService.getPerson(personId));
		}
		return matchingPeople;
	}

	public List<Obs> getAllPregnancies() {
		ObsService obsService = contextService.getObsService();
		List<Concept> pregnancyConcept = new ArrayList<Concept>();
		pregnancyConcept.add(getPregnancyConcept());
		return obsService.getObservations(null, null, pregnancyConcept, null,
				null, null, null, null, null, null, null, false);
	}

	public List<Encounter> getRecentDeliveries() {
		EncounterService encounterService = contextService
				.getEncounterService();

		List<EncounterType> deliveryEncounterType = new ArrayList<EncounterType>();
		deliveryEncounterType.add(getPregnancyDeliveryVisitEncounterType());

		Calendar calendar = Calendar.getInstance();
		Date currentDate = calendar.getTime();
		calendar.add(Calendar.DATE, 2 * -7);
		Date twoWeeksPriorDate = calendar.getTime();

		return encounterService.getEncounters(null, null, twoWeeksPriorDate,
				currentDate, null, deliveryEncounterType, null, false);
	}

	public List<Obs> getUpcomingPregnanciesDueDate() {
		Calendar calendar = Calendar.getInstance();
		Date currentDate = calendar.getTime();
		calendar.add(Calendar.DATE, 2 * 7);
		Date twoWeeksLaterDate = calendar.getTime();

		return getActivePregnanciesDueDateObs(currentDate, twoWeeksLaterDate);
	}

	public List<Obs> getOverduePregnanciesDueDate() {
		Date currentDate = new Date();
		return getActivePregnanciesDueDateObs(null, currentDate);
	}

	private List<Obs> getActivePregnanciesDueDateObs(Date fromDueDate,
			Date toDueDate) {
		MotechService motechService = contextService.getMotechService();

		Concept pregnancyDueDateConcept = getDueDateConcept();
		Concept pregnancyConcept = getPregnancyConcept();
		Concept pregnancyStatusConcept = getPregnancyStatusConcept();

		return motechService.getActivePregnanciesDueDateObs(fromDueDate,
				toDueDate, pregnancyDueDateConcept, pregnancyConcept,
				pregnancyStatusConcept);
	}

	public Patient getPatientById(Integer patientId) {
		PatientService patientService = contextService.getPatientService();
		return patientService.getPatient(patientId);
	}

	public Obs getActivePregnancy(Integer patientId) {
		MotechService motechService = contextService.getMotechService();

		List<Obs> pregnancies = motechService.getActivePregnancies(patientId,
				getPregnancyConcept(), getPregnancyStatusConcept());
		if (pregnancies.isEmpty()) {
			return null;
		} else if (pregnancies.size() > 1) {
			log.warn("More than 1 active pregnancy found for patient: "
					+ patientId);
		}
		return pregnancies.get(0);
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

	private List<Obs> getMatchingObs(Person person, Concept question,
			Concept answer, Integer obsGroupId, Date from, Date to) {

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
				answers, null, null, null, null, obsGroupId, from, to, false);

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

	public Date getLastObsCreationDate(Integer personId, String conceptName,
			String conceptValue) {

		ConceptService conceptService = contextService.getConceptService();
		PersonService personService = contextService.getPersonService();
		return getLastObsCreationDate(personService.getPerson(personId),
				conceptService.getConcept(conceptName), conceptService
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

	public Date getLastDoseObsDate(Integer personId, String conceptName,
			Integer doseNumber) {
		ConceptService conceptService = contextService.getConceptService();
		PersonService personService = contextService.getPersonService();
		ObsService obsService = contextService.getObsService();
		List<Obs> matchingObs = obsService.getObservationsByPersonAndConcept(
				personService.getPerson(personId), conceptService
						.getConcept(conceptName));
		for (Obs obs : matchingObs) {
			Double value = obs.getValueNumeric();
			if (value != null && doseNumber.intValue() == value.intValue()) {
				return obs.getObsDatetime();
			}
		}
		return null;
	}

	public Date getLastDoseObsDateInActivePregnancy(Integer patientId,
			String conceptName, Integer doseNumber) {
		PersonService personService = contextService.getPersonService();
		ConceptService conceptService = contextService.getConceptService();
		Obs pregnancy = getActivePregnancy(patientId);
		if (pregnancy != null) {
			Integer pregnancyObsId = pregnancy.getObsId();
			List<Obs> matchingObs = getMatchingObs(personService
					.getPerson(patientId), conceptService
					.getConcept(conceptName), null, pregnancyObsId, null, null);
			for (Obs obs : matchingObs) {
				Double value = obs.getValueNumeric();
				if (value != null && doseNumber.intValue() == value.intValue()) {
					return obs.getObsDatetime();
				}
			}
		}
		return null;
	}

	public Date getActivePregnancyDueDate(Integer patientId) {
		PersonService personService = contextService.getPersonService();
		Obs pregnancy = getActivePregnancy(patientId);
		if (pregnancy != null) {
			Integer pregnancyObsId = pregnancy.getObsId();
			List<Obs> dueDateObsList = getMatchingObs(personService
					.getPerson(patientId), getDueDateConcept(), null,
					pregnancyObsId, null, null);
			if (dueDateObsList.size() > 0) {
				return dueDateObsList.get(0).getValueDatetime();
			}
		}
		return null;
	}

	public Date getLastPregnancyEndDate(Integer patientId) {
		PersonService personService = contextService.getPersonService();
		List<Obs> pregnancyStatusObsList = getMatchingObs(personService
				.getPerson(patientId), getPregnancyStatusConcept(), null, null,
				null, null);
		for (Obs pregnancyStatusObs : pregnancyStatusObsList) {
			Boolean status = pregnancyStatusObs.getValueAsBoolean();
			if (Boolean.FALSE.equals(status)) {
				return pregnancyStatusObs.getObsDatetime();
			}
		}
		return null;
	}

	public Date getLastObsValue(Integer personId, String conceptName) {
		ConceptService conceptService = contextService.getConceptService();
		PersonService personService = contextService.getPersonService();
		return getLastObsValue(personService.getPerson(personId),
				conceptService.getConcept(conceptName));
	}

	public int getNumberOfObs(Person person, Concept concept, Concept value) {

		List<Obs> obsList = getMatchingObs(person, concept, value, null, null,
				null);
		return obsList.size();
	}

	public Date getLastObsCreationDate(Person person, Concept concept,
			Concept value) {

		Date latestObsDate = null;

		// List default sorted by Obs datetime
		List<Obs> obsList = getMatchingObs(person, concept, value, null, null,
				null);

		if (obsList.size() > 0) {
			latestObsDate = obsList.get(obsList.size() - 1).getDateCreated();
		} else if (log.isDebugEnabled()) {
			log.debug("No matching Obs: person id: " + person.getPersonId()
					+ ", concept: " + concept.getName().getName() + ", value: "
					+ (value != null ? value.getName().getName() : "null"));
		}
		return latestObsDate;
	}

	public Date getLastObsDate(Person person, Concept concept, Concept value) {

		Date latestObsDate = null;

		// List default sorted by Obs datetime
		List<Obs> obsList = getMatchingObs(person, concept, value, null, null,
				null);

		if (obsList.size() > 0) {
			latestObsDate = obsList.get(0).getObsDatetime();
		} else if (log.isDebugEnabled()) {
			log.debug("No matching Obs: person id: " + person.getPersonId()
					+ ", concept: " + concept.getName().getName() + ", value: "
					+ (value != null ? value.getName().getName() : "null"));
		}
		return latestObsDate;
	}

	public Date getLastObsValue(Person person, Concept concept) {
		Date lastestObsValue = null;

		List<Obs> obsList = getMatchingObs(person, concept, null, null, null,
				null);
		if (obsList.size() > 0) {
			lastestObsValue = obsList.get(0).getValueDatetime();
		} else if (log.isDebugEnabled()) {
			log.debug("No matching Obs: person id: " + person.getPersonId()
					+ ", concept: " + concept.getName().getName());
		}
		return lastestObsValue;
	}

	public Date getObsValue(Integer obsId) {
		ObsService obsService = contextService.getObsService();

		Date result = null;
		if (obsId != null) {
			Obs obs = obsService.getObs(obsId);
			if (obs != null) {
				result = obs.getValueDatetime();
			}
		}
		return result;
	}

	public Integer getObsId(Integer personId, String conceptName,
			String conceptValue, Date earliest, Date latest) {
		PersonService personService = contextService.getPersonService();
		ConceptService conceptService = contextService.getConceptService();

		List<Obs> observations = getMatchingObs(personService
				.getPerson(personId), conceptService.getConcept(conceptName),
				conceptService.getConcept(conceptValue), null, earliest, latest);
		if (observations.size() > 0) {
			observations.get(0).getObsId();
		}
		return null;
	}

	public Integer getObsId(Integer personId, String conceptName,
			Integer doseNumber, Date earliest, Date latest) {
		PersonService personService = contextService.getPersonService();
		ConceptService conceptService = contextService.getConceptService();

		List<Obs> observations = getMatchingObs(personService
				.getPerson(personId), conceptService.getConcept(conceptName),
				null, null, earliest, latest);
		for (Obs obs : observations) {
			Double value = obs.getValueNumeric();
			if (value != null && value.intValue() >= doseNumber.intValue()) {
				return obs.getObsId();
			}
		}
		return null;
	}

	public Integer getEncounterId(Integer patientId, String encounterType,
			Date earliest, Date latest) {
		PatientService patientService = contextService.getPatientService();
		EncounterService encounterService = contextService
				.getEncounterService();

		List<EncounterType> encounterTypes = new ArrayList<EncounterType>();
		encounterTypes.add(encounterService.getEncounterType(encounterType));

		List<Encounter> encounters = encounterService.getEncounters(
				patientService.getPatient(patientId), null, earliest, latest,
				null, encounterTypes, null, false);
		if (encounters.size() > 0) {
			return encounters.get(0).getEncounterId();
		}
		return null;
	}

	/* PatientObsService methods end */

	public void saveService(Service service) {
		MotechService motechService = contextService.getMotechService();
		motechService.saveService(service);
	}

	public List<Service> getIncompleteServices(Integer patientId,
			String sequence) {
		MotechService motechService = contextService.getMotechService();
		return motechService.getServices(patientId, sequence,
				ServiceStatus.INCOMPLETE);
	}

	public Map<String, Service> getIncompleteServicesMap(Integer patientId,
			String sequence) {
		List<Service> services = getIncompleteServices(patientId, sequence);
		Map<String, Service> servicesMap = new HashMap<String, Service>();
		for (Service service : services) {
			servicesMap.put(service.getService(), service);
		}
		return servicesMap;
	}

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
	public void scheduleMessage(String messageKey,
			MessageProgramEnrollment enrollment, Date messageDate,
			boolean userPreferenceBased) {
		// Return existing message definition
		MessageDefinition messageDefinition = this
				.getMessageDefinition(messageKey);

		// TODO: Assumes recipient is person in enrollment
		Integer messageRecipientId = enrollment.getPersonId();

		Date scheduledMessageDate;
		if (!userPreferenceBased) {
			scheduledMessageDate = messageDate;
		} else {
			scheduledMessageDate = this.determineUserPreferredMessageDate(
					messageRecipientId, messageDate);
		}

		// Cancel any unsent messages for the same enrollment and not matching
		// the message to schedule
		this.removeUnsentMessages(messageRecipientId, enrollment,
				messageDefinition, scheduledMessageDate);

		// Create new scheduled message (with pending attempt) for enrollment
		// if none matching already exist
		this.createScheduledMessage(messageRecipientId, messageDefinition,
				enrollment, scheduledMessageDate);
	}

	private MessageDefinition getMessageDefinition(String messageKey) {
		MotechService motechService = contextService.getMotechService();
		MessageDefinition messageDefinition = motechService
				.getMessageDefinition(messageKey);
		if (messageDefinition == null) {
			log.error("Invalid message key for message definition: "
					+ messageKey);
		}
		return messageDefinition;
	}

	protected void removeUnsentMessages(Integer recipientId,
			MessageProgramEnrollment enrollment,
			MessageDefinition messageDefinition, Date messageDate) {
		MotechService motechService = contextService.getMotechService();
		// Get Messages matching the recipient, enrollment, and status, but
		// not matching the message definition and message date
		List<Message> unsentMessages = motechService.getMessages(recipientId,
				enrollment, messageDefinition, messageDate,
				MessageStatus.SHOULD_ATTEMPT);
		log.debug("Unsent messages found during scheduling: "
				+ unsentMessages.size());

		for (Message unsentMessage : unsentMessages) {
			unsentMessage.setAttemptStatus(MessageStatus.CANCELLED);
			motechService.saveMessage(unsentMessage);

			log.debug("Message cancelled to schedule new: Id: "
					+ unsentMessage.getId());
		}
	}

	public void removeAllUnsentMessages(MessageProgramEnrollment enrollment) {
		MotechService motechService = contextService.getMotechService();
		List<Message> unsentMessages = motechService.getMessages(enrollment,
				MessageStatus.SHOULD_ATTEMPT);
		log.debug("Unsent messages found to cancel: " + unsentMessages.size()
				+ ", for enrollment: " + enrollment.getId());

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
			MessageDefinition messageDefinition,
			MessageProgramEnrollment enrollment, Date messageDate) {

		MotechService motechService = contextService.getMotechService();

		List<ScheduledMessage> scheduledMessages = motechService
				.getScheduledMessages(recipientId, messageDefinition,
						enrollment, messageDate);

		if (scheduledMessages.size() == 0) {
			if (log.isDebugEnabled()) {
				log.debug("Creating ScheduledMessage: recipient: "
						+ recipientId + ", enrollment: " + enrollment.getId()
						+ ", message key: " + messageDefinition.getMessageKey()
						+ ", date: " + messageDate);
			}

			ScheduledMessage scheduledMessage = new ScheduledMessage();
			scheduledMessage.setScheduledFor(messageDate);
			scheduledMessage.setRecipientId(recipientId);
			scheduledMessage.setMessage(messageDefinition);
			scheduledMessage.setEnrollment(enrollment);

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
		createPersonAttributeType(MotechConstants.PERSON_ATTRIBUTE_HOW_LEARNED,
				"How person found out about services.", String.class.getName(),
				admin);
		createPersonAttributeType(
				MotechConstants.PERSON_ATTRIBUTE_WHY_INTERESTED,
				"Why person is interested in services "
						+ "(IN_HOUSEHOLD_PREGNANCY, OUT_HOUSEHOLD_PREGNANCY, or IN_HOUSEHOLD_BIRTH).",
				String.class.getName(), admin);

		log.info("Verifying Patient Identifier Exist");
		createPatientIdentifierType(
				MotechConstants.PATIENT_IDENTIFIER_MOTECH_ID,
				"Patient Id for MoTeCH system.", admin);

		log.info("Verifying Locations Exist");
		Location ghana = createLocation(MotechConstants.LOCATION_GHANA,
				"Republic of Ghana, Country, Root in hierarchy", null, admin);
		Location upperEast = createLocation(
				MotechConstants.LOCATION_UPPER_EAST,
				"Upper East Region in Ghana", ghana, admin);
		createLocation(MotechConstants.LOCATION_KASSENA_NANKANA,
				"Kassena-Nankana District in Upper East Region, Ghana",
				upperEast, admin);
		Location kassenaNankanaWest = createLocation(
				MotechConstants.LOCATION_KASSENA_NANKANA_WEST,
				"Kassena-Nankana West District in Upper East Region, Ghana",
				upperEast, admin);
		Location westTest = createLocation(
				"West Test Community",
				"Test Community in Kassena-Nankana West District, Upper East Region, Ghana",
				kassenaNankanaWest, admin);
		createLocation(
				"West Test Clinic",
				"Test Clinic in West Test Community, Kassena-Nankana West District, Upper East Region, Ghana",
				westTest, admin);

		log.info("Verifying Encounter Types Exist");
		createEncounterType(MotechConstants.ENCOUNTER_TYPE_ANCVISIT,
				"Ghana Antenatal Care (ANC) Visit", admin);
		createEncounterType(MotechConstants.ENCOUNTER_TYPE_PPCVISIT,
				"Ghana Postpartum Care (PPC) Visit", admin);
		createEncounterType(MotechConstants.ENCOUNTER_TYPE_PREGREGVISIT,
				"Ghana Pregnancy Registration Visit", admin);
		createEncounterType(MotechConstants.ENCOUNTER_TYPE_PREGTERMVISIT,
				"Ghana Pregnancy Termination Visit", admin);
		createEncounterType(MotechConstants.ENCOUNTER_TYPE_PREGDELVISIT,
				"Ghana Pregnancy Delivery Visit", admin);
		createEncounterType(MotechConstants.ENCOUNTER_TYPE_PNCVISIT,
				"Ghana Postnatal Care (PNC) Visit", admin);
		createEncounterType(MotechConstants.ENCOUNTER_TYPE_GENERALVISIT,
				"Ghana General Visit", admin);

		log.info("Verifying Concepts Exist");
		createConcept(MotechConstants.CONCEPT_VISIT_NUMBER, "Visit Number",
				MotechConstants.CONCEPT_CLASS_MISC,
				MotechConstants.CONCEPT_DATATYPE_NUMERIC, admin);
		createConcept(MotechConstants.CONCEPT_TETANUS_TOXOID_DOSE,
				"Dose Number for Tetanus Toxoid Vaccination",
				MotechConstants.CONCEPT_CLASS_DRUG,
				MotechConstants.CONCEPT_DATATYPE_NUMERIC, admin);
		createConcept(
				MotechConstants.CONCEPT_INTERMITTENT_PREVENTATIVE_TREATMENT_DOSE,
				"Dose Number for Malaria Treatment",
				MotechConstants.CONCEPT_CLASS_DRUG,
				MotechConstants.CONCEPT_DATATYPE_NUMERIC, admin);
		createConcept(MotechConstants.CONCEPT_HIV_STATUS,
				"Question: \"What is the patient's text coded HIV status?\"",
				MotechConstants.CONCEPT_CLASS_QUESTION,
				MotechConstants.CONCEPT_DATATYPE_TEXT, admin);
		createConcept(MotechConstants.CONCEPT_ABORTIONTYPE,
				"Numeric coded pregnancy termination reason",
				MotechConstants.CONCEPT_CLASS_FINDING,
				MotechConstants.CONCEPT_DATATYPE_NUMERIC, admin);
		createConcept(MotechConstants.CONCEPT_COMPLICATION,
				"Numeric coded pregnancy termination complication",
				MotechConstants.CONCEPT_CLASS_FINDING,
				MotechConstants.CONCEPT_DATATYPE_NUMERIC, admin);
		createConcept(
				MotechConstants.CONCEPT_INTERMITTENT_PREVENTATIVE_TREATMENT_INFANTS,
				"Malaria Treatment for infants.",
				MotechConstants.CONCEPT_CLASS_DRUG,
				MotechConstants.CONCEPT_DATATYPE_N_A, admin);
		createConcept(
				MotechConstants.CONCEPT_INSECTICIDE_TREATED_NET_USAGE,
				"Question on encounter form: \"Does the patient use insecticide-treated nets?\"",
				MotechConstants.CONCEPT_CLASS_QUESTION,
				MotechConstants.CONCEPT_DATATYPE_BOOLEAN, admin);
		createConcept(MotechConstants.CONCEPT_ORAL_POLIO_VACCINATION_DOSE,
				"Dose Number for child Oral Polio vaccination.",
				MotechConstants.CONCEPT_CLASS_DRUG,
				MotechConstants.CONCEPT_DATATYPE_NUMERIC, admin);
		createConcept(MotechConstants.CONCEPT_PENTA_VACCINATION_DOSE,
				"Dose Number for child Penta vaccination.",
				MotechConstants.CONCEPT_CLASS_DRUG,
				MotechConstants.CONCEPT_DATATYPE_NUMERIC, admin);
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
				MotechConstants.CONCEPT_DATE_OF_CONFINEMENT_CONFIRMED,
				"Question: \"Is the pregnancy due date confirmed by the CHW?\"",
				MotechConstants.CONCEPT_CLASS_QUESTION,
				MotechConstants.CONCEPT_DATATYPE_BOOLEAN, admin);
		createConcept(MotechConstants.CONCEPT_ENROLLMENT_REFERENCE_DATE,
				"Reference Date for Message Program Enrollment",
				MotechConstants.CONCEPT_CLASS_MISC,
				MotechConstants.CONCEPT_DATATYPE_DATETIME, admin);
		createConcept(MotechConstants.CONCEPT_CAUSE_OF_DEATH,
				"Numeric coded cause of patient death",
				MotechConstants.CONCEPT_CLASS_DIAGNOSIS,
				MotechConstants.CONCEPT_DATATYPE_NUMERIC, admin);
		createConcept(MotechConstants.CONCEPT_MATERNAL_CAUSE_OF_DEATH,
				"Numeric coded maternal cause of patient death",
				MotechConstants.CONCEPT_CLASS_DIAGNOSIS,
				MotechConstants.CONCEPT_DATATYPE_NUMERIC, admin);
		createConcept(MotechConstants.CONCEPT_SERIAL_NUMBER,
				"Patient register serial number",
				MotechConstants.CONCEPT_CLASS_MISC,
				MotechConstants.CONCEPT_DATATYPE_TEXT, admin);
		createConcept(MotechConstants.CONCEPT_NEW_CASE,
				"Question: \"Is this a new case?\"",
				MotechConstants.CONCEPT_CLASS_QUESTION,
				MotechConstants.CONCEPT_DATATYPE_BOOLEAN, admin);
		createConcept(MotechConstants.CONCEPT_REFERRAL,
				"Question: \"Was patient referred?\"",
				MotechConstants.CONCEPT_CLASS_QUESTION,
				MotechConstants.CONCEPT_DATATYPE_BOOLEAN, admin);
		createConcept(MotechConstants.CONCEPT_PRIMARY_DIAGNOSIS,
				"Numeric coded primary diagnosis",
				MotechConstants.CONCEPT_CLASS_DIAGNOSIS,
				MotechConstants.CONCEPT_DATATYPE_NUMERIC, admin);
		createConcept(MotechConstants.CONCEPT_SECONDARY_DIAGNOSIS,
				"Numeric coded secondary diagnosis",
				MotechConstants.CONCEPT_CLASS_DIAGNOSIS,
				MotechConstants.CONCEPT_DATATYPE_NUMERIC, admin);
		createConcept(MotechConstants.CONCEPT_DELIVERY_METHOD,
				"Numeric coded method of delivery",
				MotechConstants.CONCEPT_CLASS_FINDING,
				MotechConstants.CONCEPT_DATATYPE_NUMERIC, admin);
		createConcept(MotechConstants.CONCEPT_DELIVERY_LOCATION,
				"Numeric coded place of delivery",
				MotechConstants.CONCEPT_CLASS_MISC,
				MotechConstants.CONCEPT_DATATYPE_NUMERIC, admin);
		createConcept(MotechConstants.CONCEPT_DELIVERED_BY,
				"Numeric coded who performed delivery",
				MotechConstants.CONCEPT_CLASS_MISC,
				MotechConstants.CONCEPT_DATATYPE_TEXT, admin);
		createConcept(MotechConstants.CONCEPT_DELIVERY_OUTCOME,
				"Numeric coded outcome of delivery",
				MotechConstants.CONCEPT_CLASS_FINDING,
				MotechConstants.CONCEPT_DATATYPE_NUMERIC, admin);
		createConcept(MotechConstants.CONCEPT_BIRTH_OUTCOME,
				"Text coded birth outcome",
				MotechConstants.CONCEPT_CLASS_FINDING,
				MotechConstants.CONCEPT_DATATYPE_TEXT, admin);

		log.info("Verifying Concepts Exist as Answers");
		// TODO: Add IPT to proper Concept as an Answer, not an immunization
		addConceptAnswers(
				MotechConstants.CONCEPT_IMMUNIZATIONS_ORDERED,
				new String[] {
						MotechConstants.CONCEPT_YELLOW_FEVER_VACCINATION,
						MotechConstants.CONCEPT_INTERMITTENT_PREVENTATIVE_TREATMENT_INFANTS,
						MotechConstants.CONCEPT_VITAMIN_A,
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
		}
		location.setName(name);
		location.setDescription(description);
		location.setCreator(creator);

		copyParentHierarchy(parent, location);

		locationService.saveLocation(location);

		if (parent != null
				&& (parent.getChildLocations() == null || !parent
						.getChildLocations().contains(location))) {
			parent.addChildLocation(location);
			locationService.saveLocation(parent);
		}
		return location;
	}

	private void copyParentHierarchy(Location parent, Location child) {
		child.setCountry(null);
		child.setRegion(null);
		child.setCountyDistrict(null);
		child.setCityVillage(null);
		child.setNeighborhoodCell(null);

		if (parent == null) {
			child.setCountry(child.getName());
		} else {
			String country = parent.getCountry();
			String region = parent.getRegion();
			String district = parent.getCountyDistrict();
			String community = parent.getCityVillage();

			if (country != null) {
				child.setCountry(country);
				if (region != null) {
					child.setRegion(region);
					if (district != null) {
						child.setCountyDistrict(district);
						if (community != null) {
							child.setCityVillage(community);
							child.setNeighborhoodCell(child.getName());
						} else {
							child.setCityVillage(child.getName());
						}
					} else {
						child.setCountyDistrict(child.getName());
					}
				} else {
					child.setRegion(child.getName());
				}
			} else {
				child.setCountry(child.getName());
			}
		}
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

		// Only determine message program state for active enrolled programs
		// concerned with an observed concept and matching the concept of this
		// obs

		MotechService motechService = contextService.getMotechService();

		List<MessageProgramEnrollment> patientActiveEnrollments = motechService
				.getActiveMessageProgramEnrollments(personId);

		for (MessageProgramEnrollment enrollment : patientActiveEnrollments) {
			MessageProgram program = this.getMessageProgram(enrollment
					.getProgram());

			if (program.getConceptName() != null) {
				if (program.getConceptName().equals(conceptName)) {
					log
							.debug("Save Obs - Obs matches Program concept, update Program: "
									+ enrollment.getProgram());

					program.determineState(enrollment);
				}
			}
		}
	}

	/* MessageProgramUpdateTask method */
	public void updateAllMessageProgramsState() {

		MotechService motechService = contextService.getMotechService();

		List<MessageProgramEnrollment> activeEnrollments = motechService
				.getAllActiveMessageProgramEnrollments();

		for (MessageProgramEnrollment enrollment : activeEnrollments) {
			MessageProgram program = this.getMessageProgram(enrollment
					.getProgram());

			log.debug("MessageProgram Update - Update State: enrollment: "
					+ enrollment.getId());

			program.determineState(enrollment);
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
	public void addMessageProgramEnrollment(Integer personId, String program,
			Integer obsId) {
		MotechService motechService = contextService.getMotechService();

		List<MessageProgramEnrollment> enrollments = null;
		if (obsId == null) {
			enrollments = motechService.getActiveMessageProgramEnrollments(
					personId, program);
		} else {
			enrollments = motechService.getActiveMessageProgramEnrollments(
					personId, program, obsId);
		}
		if (enrollments.size() == 0) {
			MessageProgramEnrollment enrollment = new MessageProgramEnrollment();
			enrollment.setPersonId(personId);
			enrollment.setProgram(program);
			enrollment.setStartDate(new Date());
			enrollment.setObsId(obsId);
			motechService.saveMessageProgramEnrollment(enrollment);
		}
	}

	public void removeMessageProgramEnrollment(
			MessageProgramEnrollment enrollment) {

		MotechService motechService = contextService.getMotechService();
		removeAllUnsentMessages(enrollment);
		if (enrollment.getEndDate() == null) {
			enrollment.setEndDate(new Date());
			motechService.saveMessageProgramEnrollment(enrollment);
		}
	}

	public void removeMessageProgramEnrollment(Integer personId,
			String program, Integer obsId) {
		MotechService motechService = contextService.getMotechService();

		List<MessageProgramEnrollment> enrollments = null;
		if (obsId == null) {
			enrollments = motechService.getActiveMessageProgramEnrollments(
					personId, program);
		} else {
			enrollments = motechService.getActiveMessageProgramEnrollments(
					personId, program, obsId);
		}
		for (MessageProgramEnrollment enrollment : enrollments) {
			removeMessageProgramEnrollment(enrollment);
		}
	}

	private void removeAllMessageProgramEnrollments(Integer personId) {
		MotechService motechService = contextService.getMotechService();

		List<MessageProgramEnrollment> enrollments = motechService
				.getActiveMessageProgramEnrollments(personId);

		for (MessageProgramEnrollment enrollment : enrollments) {
			removeMessageProgramEnrollment(enrollment);
		}
	}

	public Obs createNumericValueObs(Date date, Concept concept, Person person,
			Location location, Integer value, Encounter encounter, User creator) {

		Obs obs = createObs(date, concept, person, location, encounter, creator);
		obs.setValueNumeric(new Double(value));
		return obs;
	}

	public Obs createBooleanValueObs(Date date, Concept concept, Person person,
			Location location, Boolean value, Encounter encounter, User creator) {

		Integer intValue = null;
		// Boolean currently stored as Numeric 1 or 0
		if (value) {
			intValue = 1;
		} else {
			intValue = 0;
		}
		return createNumericValueObs(date, concept, person, location, intValue,
				encounter, creator);
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

	public Patient getPatientByMotechId(String motechId) {
		PatientService patientService = contextService.getPatientService();
		PatientIdentifierType motechIdType = getMotechPatientIdType();
		List<PatientIdentifierType> idTypes = new ArrayList<PatientIdentifierType>();
		idTypes.add(motechIdType);

		// Parameters are Name, Id, Id type, match exactly boolean
		List<Patient> patients = patientService.getPatients(null, motechId,
				idTypes, true);
		if (patients.size() > 0) {
			if (patients.size() > 1) {
				log.warn("Multiple Patients found for Motech ID: " + motechId);
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

	public PatientIdentifierType getMotechPatientIdType() {
		return contextService.getPatientService()
				.getPatientIdentifierTypeByName(
						MotechConstants.PATIENT_IDENTIFIER_MOTECH_ID);
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

	public PersonAttributeType getHowLearnedAttributeType() {
		return contextService.getPersonService().getPersonAttributeTypeByName(
				MotechConstants.PERSON_ATTRIBUTE_HOW_LEARNED);
	}

	public PersonAttributeType getWhyInterestedAttributeType() {
		return contextService.getPersonService().getPersonAttributeTypeByName(
				MotechConstants.PERSON_ATTRIBUTE_WHY_INTERESTED);
	}

	public Location getGhanaLocation() {
		return contextService.getLocationService().getLocation(
				MotechConstants.LOCATION_GHANA);
	}

	public EncounterType getANCVisitEncounterType() {
		return contextService.getEncounterService().getEncounterType(
				MotechConstants.ENCOUNTER_TYPE_ANCVISIT);
	}

	public EncounterType getPPCVisitEncounterType() {
		return contextService.getEncounterService().getEncounterType(
				MotechConstants.ENCOUNTER_TYPE_PPCVISIT);
	}

	public EncounterType getPregnancyRegistrationVisitEncounterType() {
		return contextService.getEncounterService().getEncounterType(
				MotechConstants.ENCOUNTER_TYPE_PREGREGVISIT);
	}

	public EncounterType getPregnancyTerminationVisitEncounterType() {
		return contextService.getEncounterService().getEncounterType(
				MotechConstants.ENCOUNTER_TYPE_PREGTERMVISIT);
	}

	public EncounterType getPregnancyDeliveryVisitEncounterType() {
		return contextService.getEncounterService().getEncounterType(
				MotechConstants.ENCOUNTER_TYPE_PREGDELVISIT);
	}

	public EncounterType getPNCVisitEncounterType() {
		return contextService.getEncounterService().getEncounterType(
				MotechConstants.ENCOUNTER_TYPE_PNCVISIT);
	}

	public EncounterType getGeneralVisitEncounterType() {
		return contextService.getEncounterService().getEncounterType(
				MotechConstants.ENCOUNTER_TYPE_GENERALVISIT);
	}

	public Concept getImmunizationsOrderedConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_IMMUNIZATIONS_ORDERED);
	}

	public Concept getTetanusDoseConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_TETANUS_TOXOID_DOSE);
	}

	public Concept getIPTDoseConcept() {
		return contextService
				.getConceptService()
				.getConcept(
						MotechConstants.CONCEPT_INTERMITTENT_PREVENTATIVE_TREATMENT_DOSE);
	}

	public Concept getHIVStatusConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_HIV_STATUS);
	}

	public Concept getAbortionTypeConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_ABORTIONTYPE);
	}

	public Concept getComplicationConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_COMPLICATION);
	}

	public Concept getVitaminAConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_VITAMIN_A);
	}

	public Concept getITNConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_INSECTICIDE_TREATED_NET_USAGE);
	}

	public Concept getVisitNumberConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_VISIT_NUMBER);
	}

	public Concept getPregnancyConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_PREGNANCY);
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

	public Concept getDueDateConfirmedConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_DATE_OF_CONFINEMENT_CONFIRMED);
	}

	public Concept getEnrollmentReferenceDateConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_ENROLLMENT_REFERENCE_DATE);
	}

	public Concept getDeathCauseConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_CAUSE_OF_DEATH);
	}

	public Concept getMaternalDeathCauseConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_MATERNAL_CAUSE_OF_DEATH);
	}

	public Concept getBCGConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_BCG_VACCINATION);
	}

	public Concept getOPVDoseConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_ORAL_POLIO_VACCINATION_DOSE);
	}

	public Concept getPentaDoseConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_PENTA_VACCINATION_DOSE);
	}

	public Concept getYellowFeverConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_YELLOW_FEVER_VACCINATION);
	}

	public Concept getCSMConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_CEREBRO_SPINAL_MENINGITIS_VACCINATION);
	}

	public Concept getMeaslesConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_MEASLES_VACCINATION);
	}

	public Concept getIPTiConcept() {
		return contextService
				.getConceptService()
				.getConcept(
						MotechConstants.CONCEPT_INTERMITTENT_PREVENTATIVE_TREATMENT_INFANTS);
	}

	public Concept getSerialNumberConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_SERIAL_NUMBER);
	}

	public Concept getNewCaseConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_NEW_CASE);
	}

	public Concept getReferralConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_REFERRAL);
	}

	public Concept getPrimaryDiagnosisConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_PRIMARY_DIAGNOSIS);
	}

	public Concept getSecondaryDiagnosisConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_SECONDARY_DIAGNOSIS);
	}

	public Concept getDeliveryMethodConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_DELIVERY_METHOD);
	}

	public Concept getDeliveryLocationConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_DELIVERY_LOCATION);
	}

	public Concept getDeliveredByConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_DELIVERED_BY);
	}

	public Concept getDeliveryOutcomeConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_DELIVERY_OUTCOME);
	}

	public Concept getBirthOutcomeConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_BIRTH_OUTCOME);
	}

	public String getTroubledPhoneProperty() {
		return contextService.getAdministrationService().getGlobalProperty(
				MotechConstants.GLOBAL_PROPERTY_TROUBLED_PHONE);
	}
	/* Factored out methods end */

}
