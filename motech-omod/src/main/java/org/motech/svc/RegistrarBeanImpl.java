package org.motech.svc;

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
import org.motech.event.Regimen;
import org.motech.messaging.Message;
import org.motech.messaging.MessageAttribute;
import org.motech.messaging.MessageDefinition;
import org.motech.messaging.MessageNotFoundException;
import org.motech.messaging.MessageStatus;
import org.motech.messaging.ScheduledMessage;
import org.motech.model.GenderTypeConverter;
import org.motech.model.TroubledPhone;
import org.motech.openmrs.module.ContextService;
import org.motech.openmrs.module.MotechService;
import org.motech.tasks.NotificationTask;
import org.motech.tasks.RegimenUpdateTask;
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
import org.openmrs.GlobalProperty;
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
import org.openmrs.api.AdministrationService;
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
	private Map<String, Regimen> regimens;

	public void setContextService(ContextService contextService) {
		this.contextService = contextService;
	}

	public void setMobileService(MessageService mobileService) {
		this.mobileService = mobileService;
	}

	public void setRegimens(Map<String, Regimen> regimens) {
		this.regimens = regimens;
	}

	public Regimen getRegimen(String regimenName) {
		return regimens.get(regimenName);
	}

	public void registerClinic(String name) {

		LocationService locationService = contextService.getLocationService();

		contextService.authenticate(MotechConstants.USERNAME_OPENMRS,
				MotechConstants.PASSWORD_OPENMRS);

		Location clinic = new Location();
		clinic.setName(name);
		clinic.setDescription("A Ghana Clinic Location");

		locationService.saveLocation(clinic);
	}

	public void registerNurse(String name, String phoneNumber, String clinic) {

		PersonService personService = contextService.getPersonService();
		UserService userService = contextService.getUserService();
		LocationService locationService = contextService.getLocationService();

		// User creating other users must have atleast the Privileges to be
		// given
		contextService.authenticate(MotechConstants.USERNAME_OPENMRS,
				MotechConstants.PASSWORD_OPENMRS);

		// TODO: Create nurses as person and use same User for all actions ?
		User nurse = new User();
		nurse.setUsername(name);

		// TODO: Nurse gender hardcoded, required for Person
		nurse.setGender(GenderTypeConverter.toOpenMRSString(Gender.FEMALE));

		PersonName personName = new PersonName();
		personName.setGivenName(name);
		// Family name appears required in UI
		personName.setFamilyName(name);
		nurse.addName(personName);

		// Must be created previously through API or UI to lookup
		PersonAttributeType phoneNumberAttrType = personService
				.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_PHONE_NUMBER);
		nurse
				.addAttribute(new PersonAttribute(phoneNumberAttrType,
						phoneNumber));

		// TODO: Create Nurse role with proper privileges
		Role role = userService.getRole(OpenmrsConstants.PROVIDER_ROLE);
		nurse.addRole(role);

		// TODO: Clinic not used, no connection currently between Nurse and
		// Clinic
		Location clinicLocation = locationService.getLocation(clinic);
		PersonAttributeType clinicType = personService
				.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_HEALTH_CENTER);
		nurse.addAttribute(new PersonAttribute(clinicType, clinicLocation
				.getLocationId().toString()));

		userService.saveUser(nurse, "password");
	}

	public void registerPatient(String nursePhoneNumber, String serialId,
			String name, String community, String location, Date dateOfBirth,
			Gender gender, Integer nhis, String phoneNumber,
			ContactNumberType contactNumberType, String language,
			MediaType mediaType, DeliveryTime deliveryTime, String[] regimen) {

		PatientService patientService = contextService.getPatientService();
		PersonService personService = contextService.getPersonService();
		LocationService locationService = contextService.getLocationService();
		ObsService obsService = contextService.getObsService();
		ConceptService conceptService = contextService.getConceptService();

		contextService.authenticate(MotechConstants.USERNAME_OPENMRS,
				MotechConstants.PASSWORD_OPENMRS);

		Patient patient = new Patient();

		// Must be created previously through API or UI to lookup
		PatientIdentifierType serialIdType = patientService
				.getPatientIdentifierTypeByName(MotechConstants.PATIENT_IDENTIFIER_GHANA_CLINIC_ID);

		User nurse = this.getUserByPhoneNumber(nursePhoneNumber);

		PersonAttribute clinic = nurse
				.getAttribute(personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_HEALTH_CENTER));
		Integer clinicId = Integer.valueOf(clinic.getValue());
		Location clinicLocation = locationService.getLocation(clinicId);
		patient.addIdentifier(new PatientIdentifier(serialId, serialIdType,
				clinicLocation));

		PersonName personName = new PersonName();
		personName.setGivenName(name);
		// Family name appears required, PersonName parsePersonName(name)
		personName.setFamilyName(name);
		patient.addName(personName);

		PersonAddress address = new PersonAddress();
		address.setAddress1(location);
		address.setCityVillage(community);
		patient.addAddress(address);

		patient.setBirthdate(dateOfBirth);

		// Should be "M" or "F"
		patient.setGender(GenderTypeConverter.toOpenMRSString(gender));

		// Must be created previously through API or UI to lookup
		PersonAttributeType nhisAttrType = personService
				.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_NHIS_NUMBER);
		patient
				.addAttribute(new PersonAttribute(nhisAttrType, nhis.toString()));

		// Must be created previously through API or UI to lookup
		PersonAttributeType phoneNumberAttrType = personService
				.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_PHONE_NUMBER);
		patient.addAttribute(new PersonAttribute(phoneNumberAttrType,
				phoneNumber));

		PersonAttributeType phoneTypeAttrType = personService
				.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_PHONE_TYPE);
		patient.addAttribute(new PersonAttribute(phoneTypeAttrType,
				contactNumberType.toString()));

		PersonAttributeType languageAttrType = personService
				.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_LANGUAGE);
		patient.addAttribute(new PersonAttribute(languageAttrType, language));

		PersonAttributeType mediaTypeAttrType = personService
				.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE);
		patient.addAttribute(new PersonAttribute(mediaTypeAttrType, mediaType
				.toString()));

		PersonAttributeType deliveryAttrType = personService
				.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_DELIVERY_TIME);
		patient.addAttribute(new PersonAttribute(deliveryAttrType, deliveryTime
				.toString()));

		patient = patientService.savePatient(patient);

		Concept regimenStart = conceptService
				.getConcept(MotechConstants.CONCEPT_REGIMEN_START);
		Location defaultClinic = locationService
				.getLocation(MotechConstants.LOCATION_DEFAULT_GHANA_CLINIC);
		for (String regimenName : regimen) {
			Obs regimenStartObs = new Obs();
			regimenStartObs.setObsDatetime(new Date());
			regimenStartObs.setConcept(regimenStart);
			regimenStartObs.setPerson(patient);
			regimenStartObs.setLocation(defaultClinic);
			regimenStartObs.setValueText(regimenName);
			obsService.saveObs(regimenStartObs, null);
		}
	}

	public void recordMaternalVisit(String nursePhoneNumber, Date date,
			String serialId, Boolean tetanus, Boolean ipt, Boolean itn,
			Integer visitNumber, Boolean onARV, Boolean prePMTCT,
			Boolean testPMTCT, Boolean postPMTCT, Double hemoglobinAt36Weeks) {

		PatientService patientService = contextService.getPatientService();
		PersonService personService = contextService.getPersonService();
		LocationService locationService = contextService.getLocationService();
		EncounterService encounterService = contextService
				.getEncounterService();
		ObsService obsService = contextService.getObsService();
		ConceptService conceptService = contextService.getConceptService();

		contextService.authenticate(MotechConstants.USERNAME_OPENMRS,
				MotechConstants.PASSWORD_OPENMRS);

		PatientIdentifierType serialIdType = patientService
				.getPatientIdentifierTypeByName(MotechConstants.PATIENT_IDENTIFIER_GHANA_CLINIC_ID);
		List<PatientIdentifierType> idTypes = new ArrayList<PatientIdentifierType>();
		idTypes.add(serialIdType);

		// Parameters are Name, Id, Id type, match exactly boolean
		List<Patient> patients = patientService.getPatients(null, serialId,
				idTypes, true);
		Patient patient = patients.get(0);

		Date visitDate = date;
		if (visitDate == null) {
			visitDate = new Date();
		}

		Encounter encounter = new Encounter();
		encounter.setEncounterDatetime(visitDate);
		encounter.setPatient(patient);

		User nurse = this.getUserByPhoneNumber(nursePhoneNumber);
		encounter.setProvider(nurse);

		PersonAttribute clinic = nurse
				.getAttribute(personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_HEALTH_CENTER));
		Integer clinicId = Integer.valueOf(clinic.getValue());
		Location clinicLocation = locationService.getLocation(clinicId);

		// Encounter types must be created previously
		EncounterType encounterType = encounterService
				.getEncounterType(MotechConstants.ENCOUNTER_TYPE_MATERNALVISIT);
		encounter.setEncounterType(encounterType);
		encounter.setLocation(clinicLocation);
		encounter = encounterService.saveEncounter(encounter);

		if (tetanus) {
			Obs tetanusObs = new Obs();
			tetanusObs.setObsDatetime(visitDate);
			tetanusObs.setConcept(conceptService
					.getConcept(MotechConstants.CONCEPT_IMMUNIZATIONS_ORDERED));
			tetanusObs.setPerson(patient);
			tetanusObs.setLocation(clinicLocation);
			tetanusObs.setEncounter(encounter);
			tetanusObs.setValueCoded(conceptService
					.getConcept(MotechConstants.CONCEPT_TETANUS_BOOSTER));
			obsService.saveObs(tetanusObs, null);
		}

		// TODO: Add IPT to proper Concept as an Answer, not an immunization
		if (ipt) {
			Obs iptObs = new Obs();
			iptObs.setObsDatetime(visitDate);
			iptObs.setConcept(conceptService
					.getConcept(MotechConstants.CONCEPT_IMMUNIZATIONS_ORDERED));
			iptObs.setPerson(patient);
			iptObs.setLocation(clinicLocation);
			iptObs.setEncounter(encounter);
			iptObs
					.setValueCoded(conceptService
							.getConcept(MotechConstants.CONCEPT_INTERMITTENT_PREVENTATIVE_TREATMENT));
			obsService.saveObs(iptObs, null);
		}

		if (itn) {
			Obs itnObs = new Obs();
			itnObs.setObsDatetime(visitDate);
			itnObs
					.setConcept(conceptService
							.getConcept(MotechConstants.CONCEPT_INSECTICIDE_TREATED_NET_USAGE));
			itnObs.setPerson(patient);
			itnObs.setLocation(clinicLocation);
			itnObs.setEncounter(encounter);
			itnObs.setValueNumeric(new Double(1)); // Boolean currently stored
			// as Numeric 1 or 0
			obsService.saveObs(itnObs, null);
		}

		Obs visitNumberObs = new Obs();
		visitNumberObs.setObsDatetime(visitDate);
		visitNumberObs.setConcept(conceptService
				.getConcept(MotechConstants.CONCEPT_PREGNANCY_VISIT_NUMBER));
		visitNumberObs.setPerson(patient);
		visitNumberObs.setLocation(clinicLocation);
		visitNumberObs.setEncounter(encounter);
		visitNumberObs.setValueNumeric(new Double(visitNumber));
		obsService.saveObs(visitNumberObs, null);

		if (onARV) {
			Obs arvObs = new Obs();
			arvObs.setObsDatetime(visitDate);
			arvObs
					.setConcept(conceptService
							.getConcept(MotechConstants.CONCEPT_ANTIRETROVIRAL_USE_DURING_PREGNANCY));
			arvObs.setPerson(patient);
			arvObs.setLocation(clinicLocation);
			arvObs.setEncounter(encounter);
			arvObs
					.setValueCoded(conceptService
							.getConcept(MotechConstants.CONCEPT_ON_ANTIRETROVIRAL_THERAPY));
			obsService.saveObs(arvObs, null);
		}

		if (prePMTCT) {
			Obs prePmtctObs = new Obs();
			prePmtctObs.setObsDatetime(visitDate);
			prePmtctObs
					.setConcept(conceptService
							.getConcept(MotechConstants.CONCEPT_PRE_PREVENTING_MATERNAL_TO_CHILD_TRANSMISSION));
			prePmtctObs.setPerson(patient);
			prePmtctObs.setLocation(clinicLocation);
			prePmtctObs.setEncounter(encounter);
			prePmtctObs.setValueNumeric(new Double(1)); // Boolean currently
			// stored as Numeric 1
			// or 0
			obsService.saveObs(prePmtctObs, null);
		}

		if (testPMTCT) {
			Obs testPmtctObs = new Obs();
			testPmtctObs.setObsDatetime(visitDate);
			testPmtctObs
					.setConcept(conceptService
							.getConcept(MotechConstants.CONCEPT_TEST_PREVENTING_MATERNAL_TO_CHILD_TRANSMISSION));
			testPmtctObs.setPerson(patient);
			testPmtctObs.setLocation(clinicLocation);
			testPmtctObs.setEncounter(encounter);
			testPmtctObs.setValueNumeric(new Double(1)); // Boolean currently
			// stored as Numeric
			// 1 or 0
			obsService.saveObs(testPmtctObs, null);
		}

		if (postPMTCT) {
			Obs postPmtctObs = new Obs();
			postPmtctObs.setObsDatetime(visitDate);
			postPmtctObs
					.setConcept(conceptService
							.getConcept(MotechConstants.CONCEPT_POST_PREVENTING_MATERNAL_TO_CHILD_TRANSMISSION));
			postPmtctObs.setPerson(patient);
			postPmtctObs.setLocation(clinicLocation);
			postPmtctObs.setEncounter(encounter);
			postPmtctObs.setValueNumeric(new Double(1)); // Boolean currently
			// stored as Numeric
			// 1 or 0
			obsService.saveObs(postPmtctObs, null);
		}

		Obs hemoglobinObs = new Obs();
		hemoglobinObs.setObsDatetime(visitDate);
		hemoglobinObs.setConcept(conceptService
				.getConcept(MotechConstants.CONCEPT_HEMOGLOBIN_AT_36_WEEKS));
		hemoglobinObs.setPerson(patient);
		hemoglobinObs.setLocation(clinicLocation);
		hemoglobinObs.setEncounter(encounter);
		hemoglobinObs.setValueNumeric(hemoglobinAt36Weeks);
		obsService.saveObs(hemoglobinObs, null);
	}

	public void registerPregnancy(String nursePhoneNumber, Date date,
			String serialId, Date dueDate, Integer parity, Double hemoglobin) {

		PatientService patientService = contextService.getPatientService();
		PersonService personService = contextService.getPersonService();
		LocationService locationService = contextService.getLocationService();
		EncounterService encounterService = contextService
				.getEncounterService();
		ObsService obsService = contextService.getObsService();
		ConceptService conceptService = contextService.getConceptService();

		contextService.authenticate(MotechConstants.USERNAME_OPENMRS,
				MotechConstants.PASSWORD_OPENMRS);

		PatientIdentifierType serialIdType = patientService
				.getPatientIdentifierTypeByName(MotechConstants.PATIENT_IDENTIFIER_GHANA_CLINIC_ID);
		List<PatientIdentifierType> idTypes = new ArrayList<PatientIdentifierType>();
		idTypes.add(serialIdType);

		// Parameters are Name, Id, Id type, match exactly boolean
		List<Patient> patients = patientService.getPatients(null, serialId,
				idTypes, true);
		Patient patient = patients.get(0);

		Date visitDate = date;
		if (visitDate == null) {
			visitDate = new Date();
		}

		Encounter encounter = new Encounter();
		encounter.setEncounterDatetime(visitDate);
		encounter.setPatient(patient);

		User nurse = this.getUserByPhoneNumber(nursePhoneNumber);
		encounter.setProvider(nurse);

		PersonAttribute clinic = nurse
				.getAttribute(personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_HEALTH_CENTER));
		Integer clinicId = Integer.valueOf(clinic.getValue());
		Location clinicLocation = locationService.getLocation(clinicId);

		// Encounter types must be created previously
		EncounterType encounterType = encounterService
				.getEncounterType(MotechConstants.ENCOUNTER_TYPE_PREGNANCYVISIT);
		encounter.setEncounterType(encounterType);
		encounter.setLocation(clinicLocation);
		encounter = encounterService.saveEncounter(encounter);

		Obs pregSatusObs = new Obs();
		pregSatusObs.setObsDatetime(visitDate);
		pregSatusObs.setConcept(conceptService
				.getConcept(MotechConstants.CONCEPT_PREGNANCY_STATUS));
		pregSatusObs.setPerson(patient);
		pregSatusObs.setLocation(clinicLocation);
		pregSatusObs.setEncounter(encounter);
		pregSatusObs.setValueNumeric(new Double(1)); // Boolean currently stored
		// as Numeric 1 or 0
		obsService.saveObs(pregSatusObs, null);

		Obs dueDateObs = new Obs();
		dueDateObs.setObsDatetime(visitDate);
		dueDateObs
				.setConcept(conceptService
						.getConcept(MotechConstants.CONCEPT_ESTIMATED_DATE_OF_CONFINEMENT));
		dueDateObs.setPerson(patient);
		dueDateObs.setLocation(clinicLocation);
		dueDateObs.setEncounter(encounter);
		dueDateObs.setValueDatetime(dueDate);
		obsService.saveObs(dueDateObs, null);

		Obs parityObs = new Obs();
		parityObs.setObsDatetime(visitDate);
		parityObs.setConcept(conceptService
				.getConcept(MotechConstants.CONCEPT_GRAVIDA));
		parityObs.setPerson(patient);
		parityObs.setLocation(clinicLocation);
		parityObs.setEncounter(encounter);
		parityObs.setValueNumeric(new Double(parity));
		obsService.saveObs(parityObs, null);

		Obs hemoglobinObs = new Obs();
		hemoglobinObs.setObsDatetime(visitDate);
		hemoglobinObs.setConcept(conceptService
				.getConcept(MotechConstants.CONCEPT_HEMOGLOBIN));
		hemoglobinObs.setPerson(patient);
		hemoglobinObs.setLocation(clinicLocation);
		hemoglobinObs.setEncounter(encounter);
		hemoglobinObs.setValueNumeric(hemoglobin);
		obsService.saveObs(hemoglobinObs, null);
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

		contextService.authenticate(MotechConstants.USERNAME_OPENMRS,
				MotechConstants.PASSWORD_OPENMRS);
		MotechService motechService = contextService.getMotechService();
		PersonService personService = contextService.getPersonService();

		Message message = motechService.getMessage(messageId);
		if (message == null) {
			throw new MessageNotFoundException();
		}

		Integer recipientId = message.getSchedule().getRecipientId();
		Person messageRecipient = personService.getPerson(recipientId);
		PersonAttributeType phoneNumberAttrType = personService
				.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_PHONE_NUMBER);
		PersonAttribute phoneAttribute = messageRecipient
				.getAttribute(phoneNumberAttrType);
		String phoneNumber = phoneAttribute.getValue();
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
	public List<String> getRegimenEnrollment(Integer personId) {
		ConceptService conceptService = contextService.getConceptService();
		MotechService motechService = contextService.getMotechService();
		Concept startConcept = conceptService
				.getConcept(MotechConstants.CONCEPT_REGIMEN_START);
		Concept endConcept = conceptService
				.getConcept(MotechConstants.CONCEPT_REGIMEN_END);

		return motechService.getObsEnrollment(personId, startConcept,
				endConcept);
	}

	public User getUserByPhoneNumber(String phoneNumber) {
		PersonService personService = contextService.getPersonService();
		MotechService motechService = contextService.getMotechService();
		UserService userService = contextService.getUserService();

		PersonAttributeType phoneAttributeType = personService
				.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_PHONE_NUMBER);
		// If more than one user matches phone number, first user in list is
		// returned
		Integer userId = motechService.getUserIdsByPersonAttribute(
				phoneAttributeType, phoneNumber).get(0);
		return userService.getUser(userId);
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
		ghanaPatientIdType
				.add(patientService
						.getPatientIdentifierTypeByName(MotechConstants.PATIENT_IDENTIFIER_GHANA_CLINIC_ID));
		return patientService
				.getPatients(null, null, ghanaPatientIdType, false);
	}

	public List<Encounter> getAllPregnancyVisits() {
		EncounterService encounterService = contextService
				.getEncounterService();
		List<EncounterType> pregnancyType = new ArrayList<EncounterType>();
		pregnancyType
				.add(encounterService
						.getEncounterType(MotechConstants.ENCOUNTER_TYPE_PREGNANCYVISIT));
		return encounterService.getEncounters(null, null, null, null, null,
				pregnancyType, null, false);
	}

	public List<Encounter> getAllMaternalVisits() {
		EncounterService encounterService = contextService
				.getEncounterService();
		List<EncounterType> maternalVisitType = new ArrayList<EncounterType>();
		maternalVisitType
				.add(encounterService
						.getEncounterType(MotechConstants.ENCOUNTER_TYPE_MATERNALVISIT));
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
	private List<Obs> getMatchingObs(Patient patient, String conceptName,
			String conceptValue) {

		ConceptService conceptService = contextService.getConceptService();
		ObsService obsService = contextService.getObsService();

		List<Concept> questions = null;
		if (conceptName != null) {
			Concept concept = conceptService.getConcept(conceptName);
			questions = new ArrayList<Concept>();
			questions.add(concept);
		}

		List<Concept> answers = null;
		if (conceptValue != null) {
			Concept conceptAnswer = conceptService.getConcept(conceptValue);
			answers = new ArrayList<Concept>();
			answers.add(conceptAnswer);
		}

		List<Person> whom = new ArrayList<Person>();
		whom.add((Person) patient);

		// patients, encounters, questions, answers, persontype, locations,
		// sort, max returned, group id, from date, to date, include voided
		List<Obs> obsList = obsService.getObservations(whom, null, questions,
				answers, null, null, null, null, null, null, null, false);

		return obsList;
	}

	public int getNumberOfObs(Patient patient, String conceptName,
			String conceptValue) {

		List<Obs> obsList = getMatchingObs(patient, conceptName, conceptValue);

		return obsList.size();
	}

	public Date getLastObsDate(Patient patient, String conceptName,
			String conceptValue) {

		Date latestObsDate = null;

		// List default sorted by Obs datetime
		List<Obs> obsList = getMatchingObs(patient, conceptName, conceptValue);

		if (obsList.size() > 0) {
			latestObsDate = obsList.get(0).getObsDatetime();
		}
		return latestObsDate;
	}

	public Date getLastObsValue(Patient patient, String conceptName) {
		Date lastestObsValue = null;

		List<Obs> obsList = getMatchingObs(patient, conceptName, null);
		if (obsList.size() > 0) {
			lastestObsValue = obsList.get(0).getValueDatetime();
		}
		return lastestObsValue;
	}

	public void removeRegimen(Integer personId, String regimenName) {
		ConceptService conceptService = contextService.getConceptService();
		LocationService locationService = contextService.getLocationService();
		PersonService personService = contextService.getPersonService();
		ObsService obsService = contextService.getObsService();
		UserService userService = contextService.getUserService();

		Concept regimenEnd = conceptService
				.getConcept(MotechConstants.CONCEPT_REGIMEN_END);
		Location defaultClinic = locationService
				.getLocation(MotechConstants.LOCATION_DEFAULT_GHANA_CLINIC);
		Person person = personService.getPerson(personId);
		User creator = userService.getUser(1);

		Obs regimenEndObs = new Obs();
		regimenEndObs.setObsDatetime(new Date());
		regimenEndObs.setConcept(regimenEnd);
		regimenEndObs.setPerson(person);
		regimenEndObs.setLocation(defaultClinic);
		regimenEndObs.setCreator(creator);
		regimenEndObs.setValueText(regimenName);
		obsService.saveObs(regimenEndObs, null);
	}

	/* PatientObsService methods end */

	/* MessageDefinition method */
	public NameValuePair[] getNameValueContent(
			MessageDefinition messageDefinition, Integer messageRecipientId) {

		PersonService personService = contextService.getPersonService();
		PatientService patientService = contextService.getPatientService();

		List<NameValuePair> nameValueList = new ArrayList<NameValuePair>();
		for (MessageAttribute attribute : messageDefinition
				.getMessageAttributes()) {
			NameValuePair pair = new NameValuePair();
			pair.setName(attribute.getName());
			if (attribute.getName().equals("PatientFirstName")) {
				Person person = personService.getPerson(messageRecipientId);
				pair.setValue(person.getGivenName());
			} else if (attribute.getName().equals("DueDate")) {
				Patient patient = patientService.getPatient(messageRecipientId);
				Date dueDate = this.getLastObsValue(patient,
						MotechConstants.CONCEPT_ESTIMATED_DATE_OF_CONFINEMENT);
				pair.setValue(dueDate.toString());
			}
			nameValueList.add(pair);
		}
		return nameValueList.toArray(new NameValuePair[nameValueList.size()]);
	}

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
		PersonAttributeType deliveryTimeType = contextService
				.getPersonService().getPersonAttributeTypeByName(
						MotechConstants.PERSON_ATTRIBUTE_DELIVERY_TIME);
		PersonAttribute deliveryTimeAttr = recipient
				.getAttribute(deliveryTimeType);
		String deliveryTimeString = deliveryTimeAttr.getValue();
		DeliveryTime deliveryTime = DeliveryTime.ANYTIME;
		if (deliveryTimeString != null) {
			deliveryTime = DeliveryTime.valueOf(deliveryTimeString);
		}

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
		contextService.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_USERS);

		contextService
				.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES);
		contextService
				.addProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_PERSON_ATTRIBUTE_TYPES);

		contextService
				.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_IDENTIFIER_TYPES);
		contextService
				.addProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_IDENTIFIER_TYPES);

		contextService.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_LOCATIONS);
		contextService
				.addProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_LOCATIONS);

		contextService
				.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_ENCOUNTER_TYPES);
		contextService
				.addProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_ENCOUNTER_TYPES);

		contextService.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_CONCEPTS);
		contextService.addProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_CONCEPTS);
		contextService
				.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_CONCEPT_DATATYPES);
		contextService
				.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_CONCEPT_CLASSES);

		contextService
				.addProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_SCHEDULER);

		contextService
				.addProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_GLOBAL_PROPERTIES);

		try {
			UserService userService = contextService.getUserService();
			User admin = userService.getUser(1);

			log.info("Verifying Person Attributes Exist");
			createPersonAttributeType(
					MotechConstants.PERSON_ATTRIBUTE_PHONE_NUMBER,
					"A person's phone number.", String.class.getName(), admin);
			createPersonAttributeType(
					MotechConstants.PERSON_ATTRIBUTE_NHIS_NUMBER,
					"A person's NHIS number.", String.class.getName(), admin);
			createPersonAttributeType(
					MotechConstants.PERSON_ATTRIBUTE_LANGUAGE,
					"A person's language preference.", String.class.getName(),
					admin);
			createPersonAttributeType(
					MotechConstants.PERSON_ATTRIBUTE_PHONE_TYPE,
					"A person's cell phone type (PERSONAL or SHARED).",
					String.class.getName(), admin);
			createPersonAttributeType(
					MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE,
					"A person's preferred phone media type (TEXT or VOICE).",
					String.class.getName(), admin);
			createPersonAttributeType(
					MotechConstants.PERSON_ATTRIBUTE_DELIVERY_TIME,
					"A person's preferred delivery time (ANYTIME, MORNING, AFTERNOON, or EVENING).",
					String.class.getName(), admin);

			log.info("Verifying Patient Identifier Exist");
			createPatientIdentifierType(
					MotechConstants.PATIENT_IDENTIFIER_GHANA_CLINIC_ID,
					"Patient Id for Ghana Clinics.", admin);

			log.info("Verifying Default Location Exists");
			createLocation(MotechConstants.LOCATION_DEFAULT_GHANA_CLINIC,
					"Default Ghana Clinic Location", admin);

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
					"Treatment for Malaria",
					MotechConstants.CONCEPT_CLASS_DRUG,
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
			createConcept(MotechConstants.CONCEPT_REGIMEN_START,
					"Name of enrolled Regimen",
					MotechConstants.CONCEPT_CLASS_MISC,
					MotechConstants.CONCEPT_DATATYPE_TEXT, admin);
			createConcept(MotechConstants.CONCEPT_REGIMEN_END,
					"Name of completed Regimen",
					MotechConstants.CONCEPT_CLASS_MISC,
					MotechConstants.CONCEPT_DATATYPE_TEXT, admin);

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
			immProps.put(MotechConstants.TASK_PROPERTY_SEND_IMMEDIATE,
					Boolean.TRUE.toString());
			createTask(MotechConstants.TASK_IMMEDIATE_NOTIFICATION,
					"Task to send out immediate SMS notifications", new Date(),
					new Long(30), Boolean.FALSE, NotificationTask.class
							.getName(), admin, immProps);
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			Map<String, String> dailyProps = new HashMap<String, String>();
			dailyProps.put(MotechConstants.TASK_PROPERTY_TIME_OFFSET, new Long(
					3600).toString());
			createTask(MotechConstants.TASK_DAILY_NOTIFICATION,
					"Task to send out SMS notifications for next day", calendar
							.getTime(), new Long(86400), Boolean.FALSE,
					NotificationTask.class.getName(), admin, dailyProps);
			createTask(MotechConstants.TASK_REGIMEN_UPDATE,
					"Task to update regimen state for patients", new Date(),
					new Long(30), Boolean.FALSE, RegimenUpdateTask.class
							.getName(), admin, null);

			log.info("Verifying Global Properties Exist");
			createGlobalProperty(
					MotechConstants.GLOBAL_PROPERTY_TROUBLED_PHONE,
					new Integer(4).toString(),
					"Number of sending failures when phone is considered troubled");

		} finally {
			contextService
					.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_USERS);

			contextService
					.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES);
			contextService
					.removeProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_PERSON_ATTRIBUTE_TYPES);

			contextService
					.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_IDENTIFIER_TYPES);
			contextService
					.removeProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_IDENTIFIER_TYPES);

			contextService
					.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_LOCATIONS);
			contextService
					.removeProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_LOCATIONS);

			contextService
					.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_ENCOUNTER_TYPES);
			contextService
					.removeProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_ENCOUNTER_TYPES);

			contextService
					.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_CONCEPTS);
			contextService
					.removeProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_CONCEPTS);
			contextService
					.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_CONCEPT_DATATYPES);
			contextService
					.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_CONCEPT_CLASSES);

			contextService
					.removeProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_SCHEDULER);

			contextService
					.removeProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_GLOBAL_PROPERTIES);
		}
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

	private void createLocation(String name, String description, User creator) {
		LocationService locationService = contextService.getLocationService();
		Location location = locationService.getLocation(name);
		if (location == null) {
			log.info(name + " Location Does Not Exist - Creating");
			location = new Location();
			location.setName(name);
			location.setDescription(description);
			location.setCreator(creator);
			locationService.saveLocation(location);
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

	private void createGlobalProperty(String name, String value,
			String description) {
		AdministrationService administrationService = contextService
				.getAdministrationService();
		GlobalProperty property = administrationService
				.getGlobalPropertyObject(name);
		if (property == null) {
			property = new GlobalProperty(name, value, description);
			administrationService.saveGlobalProperty(property);
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

		contextService.openSession();

		contextService
				.addProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_SCHEDULER);
		try {
			removeTask(MotechConstants.TASK_IMMEDIATE_NOTIFICATION);
			removeTask(MotechConstants.TASK_DAILY_NOTIFICATION);
			removeTask(MotechConstants.TASK_REGIMEN_UPDATE);
		} finally {
			contextService
					.removeProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_SCHEDULER);
			contextService.closeSession();
		}
	}

	/* Activator methods end */

	/* SaveObsAdvice method */
	public void updateRegimenState(Obs obs) {
		ConceptService conceptService = contextService.getConceptService();
		PatientService patientService = contextService.getPatientService();

		Concept regimenStart = conceptService
				.getConcept(MotechConstants.CONCEPT_REGIMEN_START);
		Integer obsPersonId = obs.getPerson().getPersonId();
		Patient patient = patientService.getPatient(obsPersonId);

		if (regimenStart.equals(obs.getConcept())) {

			String regimenName = obs.getValueText();

			log.debug("Save Obs - Update State for newly enrolled Regimen: "
					+ regimenName);

			Regimen enrolledRegimen = this.getRegimen(regimenName);

			enrolledRegimen.determineState(patient);

		} else {
			// Only determine regimen state for enrolled regimen
			// concerned with an observed concept
			// and matching the concept of this obs

			List<String> patientRegimens = this
					.getRegimenEnrollment(obsPersonId);

			for (String regimenName : patientRegimens) {
				Regimen regimen = this.getRegimen(regimenName);

				Concept regimenConcept = null;
				if (regimen.getConceptName() != null) {
					regimenConcept = conceptService.getConcept(regimen
							.getConceptName());

					if (obs.getConcept().equals(regimenConcept)) {
						log
								.debug("Save Obs - Obs matches Regmen concept, update Regimen: "
										+ regimenName);

						regimen.determineState(patient);
					}
				}
			}
		}
	}

	/* RegimenUpdateTask method */
	public void updateAllRegimenState() {
		try {
			contextService.openSession();
			contextService
					.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_IDENTIFIER_TYPES);
			contextService
					.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PATIENTS);
			contextService
					.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_CONCEPTS);
			contextService.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_OBS);
			contextService
					.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PERSONS);
			contextService
					.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_LOCATIONS);
			contextService.addProxyPrivilege(OpenmrsConstants.PRIV_ADD_OBS);
			contextService.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_USERS);
			contextService
					.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES);

			PatientService patientService = contextService.getPatientService();

			// Get all Patients with the Ghana Clinic Id Type
			PatientIdentifierType serialIdType = patientService
					.getPatientIdentifierTypeByName(MotechConstants.PATIENT_IDENTIFIER_GHANA_CLINIC_ID);
			List<PatientIdentifierType> idTypes = new ArrayList<PatientIdentifierType>();
			idTypes.add(serialIdType);
			List<Patient> patients = patientService.getPatients(null, null,
					idTypes, true);

			// Update Regimen state for enrolled Regimen of all matching
			// patients
			for (Patient patient : patients) {
				List<String> patientRegimens = this
						.getRegimenEnrollment(patient.getPatientId());

				for (String regimenName : patientRegimens) {
					Regimen regimen = this.getRegimen(regimenName);

					log.debug("Regimen Update - Update State: regimen: "
							+ regimenName + ", patient: "
							+ patient.getPatientId());

					regimen.determineState(patient);
				}
			}
		} finally {
			contextService
					.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_IDENTIFIER_TYPES);
			contextService
					.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PATIENTS);
			contextService
					.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_CONCEPTS);
			contextService.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_OBS);
			contextService
					.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PERSONS);
			contextService
					.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_LOCATIONS);
			contextService.removeProxyPrivilege(OpenmrsConstants.PRIV_ADD_OBS);
			contextService
					.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_USERS);
			contextService
					.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES);
			contextService.closeSession();
		}
	}

	/* NotificationTask method */
	public void sendMessages(Date startDate, Date endDate, boolean sendImmediate) {
		try {
			contextService.openSession();
			contextService
					.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES);
			contextService
					.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_IDENTIFIER_TYPES);
			contextService
					.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PATIENTS);
			contextService.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_USERS);
			contextService
					.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PERSONS);
			contextService
					.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_CONCEPTS);
			contextService.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_OBS);

			MotechService motechService = contextService.getMotechService();
			PersonService personService = contextService.getPersonService();
			PatientService patientService = contextService.getPatientService();
			UserService userService = contextService.getUserService();
			AdministrationService administrationService = contextService
					.getAdministrationService();

			List<Message> shouldAttemptMessages = motechService.getMessages(
					startDate, endDate, MessageStatus.SHOULD_ATTEMPT);

			if (log.isDebugEnabled()) {
				log
						.debug("Notification Task executed, Should Attempt Messages found: "
								+ shouldAttemptMessages.size());
			}

			if (shouldAttemptMessages.size() > 0) {
				Date notificationDate = new Date();
				PersonAttributeType phoneNumberType = personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_PHONE_NUMBER);
				PersonAttributeType phoneType = personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_PHONE_TYPE);
				PersonAttributeType languageType = personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_LANGUAGE);
				PersonAttributeType mediaAttrType = personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE);

				for (Message shouldAttemptMessage : shouldAttemptMessages) {

					org.motech.model.Log motechLog = new org.motech.model.Log();
					motechLog.setDate(notificationDate);

					String messageId = shouldAttemptMessage.getPublicId();
					Long notificationType = shouldAttemptMessage.getSchedule()
							.getMessage().getPublicId();
					Integer recipientId = shouldAttemptMessage.getSchedule()
							.getRecipientId();
					Patient patient = patientService.getPatient(recipientId);
					User nurse = userService.getUser(recipientId);

					if (patient != null) {
						String patientPhone = patient.getAttribute(
								phoneNumberType).getValue();
						String phoneTypeString = patient
								.getAttribute(phoneType).getValue();
						String languageCode = patient
								.getAttribute(languageType).getValue();
						String mediaTypeString = patient.getAttribute(
								mediaAttrType).getValue();
						ContactNumberType patientNumberType = ContactNumberType
								.valueOf(phoneTypeString);

						NameValuePair[] personalInfo = this
								.getNameValueContent(shouldAttemptMessage
										.getSchedule().getMessage(),
										recipientId);
						MediaType mediaType = MediaType
								.valueOf(mediaTypeString);
						Date messageStartDate = null;
						Date messageEndDate = null;

						if (!sendImmediate) {

							messageStartDate = shouldAttemptMessage
									.getAttemptDate();

							Person recipient = personService
									.getPerson(recipientId);
							PersonAttributeType deliveryTimeType = personService
									.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_DELIVERY_TIME);
							PersonAttribute deliveryTimeAttr = recipient
									.getAttribute(deliveryTimeType);
							String deliveryTimeString = deliveryTimeAttr
									.getValue();
							DeliveryTime deliveryTime = DeliveryTime.ANYTIME;
							if (deliveryTimeString != null) {
								deliveryTime = DeliveryTime
										.valueOf(deliveryTimeString);
							}

							Calendar calendar = Calendar.getInstance();
							calendar.setTime(messageStartDate);
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
							messageEndDate = calendar.getTime();
						}

						// Cancel message if patient phone is considered
						// troubled
						TroubledPhone troubledPhone = motechService
								.getTroubledPhone(patientPhone);
						Integer maxFailures = Integer
								.parseInt(administrationService
										.getGlobalProperty(MotechConstants.GLOBAL_PROPERTY_TROUBLED_PHONE));
						if (troubledPhone != null
								&& troubledPhone.getSendFailures() >= maxFailures) {
							motechLog
									.setMessage("Attempt to send to Troubled Phone, Patient Phone: "
											+ patientPhone
											+ ", Message cancelled: "
											+ notificationType);
							motechLog.setType(LogType.FAILURE);

							shouldAttemptMessage
									.setAttemptStatus(MessageStatus.CANCELLED);

						} else {
							motechLog
									.setMessage("Scheduled Message Notification, Patient Phone: "
											+ patientPhone
											+ ": "
											+ notificationType);

							try {
								mobileService.sendPatientMessage(messageId,
										personalInfo, patientPhone,
										patientNumberType, languageCode,
										mediaType, notificationType,
										messageStartDate, messageEndDate);
								shouldAttemptMessage
										.setAttemptStatus(MessageStatus.ATTEMPT_PENDING);
								motechLog.setType(LogType.SUCCESS);
							} catch (Exception e) {
								log.error("Mobile patient message failure", e);
								shouldAttemptMessage
										.setAttemptStatus(MessageStatus.ATTEMPT_FAIL);
								motechLog.setType(LogType.FAILURE);
							}
						}

					} else if (nurse != null) {
						String nursePhone = nurse.getAttribute(phoneNumberType)
								.getValue();

						NameValuePair[] personalInfo = this
								.getNameValueContent(shouldAttemptMessage
										.getSchedule().getMessage(),
										recipientId);
						org.motechproject.ws.Patient[] patients = new org.motechproject.ws.Patient[0];
						String langCode = null;
						MediaType mediaType = null;
						Date messageStartDate = null;
						Date messageEndDate = null;

						// Cancel message if nurse phone is considered troubled
						TroubledPhone troubledPhone = motechService
								.getTroubledPhone(nursePhone);
						Integer maxFailures = Integer
								.parseInt(administrationService
										.getGlobalProperty(MotechConstants.GLOBAL_PROPERTY_TROUBLED_PHONE));
						if (troubledPhone != null
								&& troubledPhone.getSendFailures() >= maxFailures) {
							motechLog
									.setMessage("Attempt to send to Troubled Phone, Nurse Phone: "
											+ nursePhone
											+ ", Message cancelled: "
											+ notificationType);
							motechLog.setType(LogType.FAILURE);

							shouldAttemptMessage
									.setAttemptStatus(MessageStatus.CANCELLED);

						} else {
							motechLog
									.setMessage("Scheduled Message Notification, Nurse Phone: "
											+ nursePhone
											+ ": "
											+ notificationType);

							try {
								mobileService.sendCHPSMessage(messageId,
										personalInfo, nursePhone, patients,
										langCode, mediaType, notificationType,
										messageStartDate, messageEndDate);

								shouldAttemptMessage
										.setAttemptStatus(MessageStatus.ATTEMPT_PENDING);
								motechLog.setType(LogType.SUCCESS);
							} catch (Exception e) {
								log.error("Mobile nurse message failure", e);
								shouldAttemptMessage
										.setAttemptStatus(MessageStatus.ATTEMPT_FAIL);
								motechLog.setType(LogType.FAILURE);
							}
						}

					}
					motechService.saveLog(motechLog);

					motechService.saveMessage(shouldAttemptMessage);
				}
			}
		} catch (Exception e) {
			log.error(e);
		} finally {
			contextService
					.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES);
			contextService
					.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_IDENTIFIER_TYPES);
			contextService
					.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PATIENTS);
			contextService
					.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_USERS);
			contextService
					.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PERSONS);
			contextService
					.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_CONCEPTS);
			contextService.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_OBS);
			contextService.closeSession();
		}
	}

}
