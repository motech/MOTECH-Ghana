package org.motech.svc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.motech.messaging.Message;
import org.motech.messaging.MessageNotFoundException;
import org.motech.messaging.MessageStatus;
import org.motech.model.GenderTypeConverter;
import org.motech.model.TroubledPhone;
import org.motech.openmrs.module.ContextService;
import org.motech.openmrs.module.MotechService;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.DeliveryTime;
import org.motechproject.ws.Gender;
import org.motechproject.ws.LogType;
import org.motechproject.ws.MediaType;
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

/**
 * An implementation of the RegistrarBean interface, implemented using a mix of
 * OpenMRS and module defined services.
 */
public class RegistrarBeanImpl implements RegistrarBean {

	private ContextService contextService;

	public void registerClinic(String name) {

		LocationService locationService = contextService.getLocationService();

		contextService.authenticate("admin", "test");

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
		contextService.authenticate("admin", "test");

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
				.getPersonAttributeTypeByName("Phone Number");
		nurse
				.addAttribute(new PersonAttribute(phoneNumberAttrType,
						phoneNumber));

		// TODO: Create Nurse role with proper privileges
		Role role = userService.getRole("Provider");
		nurse.addRole(role);

		// TODO: Clinic not used, no connection currently between Nurse and
		// Clinic
		Location clinicLocation = locationService.getLocation(clinic);
		PersonAttributeType clinicType = personService
				.getPersonAttributeTypeByName("Health Center");
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
		MotechService motechService = contextService.getMotechService();
		PersonService personService = contextService.getPersonService();
		LocationService locationService = contextService.getLocationService();

		contextService.authenticate("admin", "test");

		Patient patient = new Patient();

		// Must be created previously through API or UI to lookup
		PatientIdentifierType serialIdType = patientService
				.getPatientIdentifierTypeByName("Ghana Clinic Id");

		User nurse = motechService.getUserByPhoneNumber(nursePhoneNumber);

		PersonAttribute clinic = nurse.getAttribute(personService
				.getPersonAttributeTypeByName("Health Center"));
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
				.getPersonAttributeTypeByName("NHIS Number");
		patient
				.addAttribute(new PersonAttribute(nhisAttrType, nhis.toString()));

		// Must be created previously through API or UI to lookup
		PersonAttributeType phoneNumberAttrType = personService
				.getPersonAttributeTypeByName("Phone Number");
		patient.addAttribute(new PersonAttribute(phoneNumberAttrType,
				phoneNumber));

		PersonAttributeType phoneTypeAttrType = personService
				.getPersonAttributeTypeByName("Phone Type");
		patient.addAttribute(new PersonAttribute(phoneTypeAttrType,
				contactNumberType.toString()));

		PersonAttributeType languageAttrType = personService
				.getPersonAttributeTypeByName("Language");
		patient.addAttribute(new PersonAttribute(languageAttrType, language));

		PersonAttributeType mediaTypeAttrType = personService
				.getPersonAttributeTypeByName("Media Type");
		patient.addAttribute(new PersonAttribute(mediaTypeAttrType, mediaType
				.toString()));

		PersonAttributeType deliveryAttrType = personService
				.getPersonAttributeTypeByName("Delivery Time");
		patient.addAttribute(new PersonAttribute(deliveryAttrType, deliveryTime
				.toString()));

		patientService.savePatient(patient);
	}

	public void recordMaternalVisit(String nursePhoneNumber, Date date,
			String serialId, Boolean tetanus, Boolean ipt, Boolean itn,
			Integer visitNumber, Boolean onARV, Boolean prePMTCT,
			Boolean testPMTCT, Boolean postPMTCT, Double hemoglobinAt36Weeks) {

		PatientService patientService = contextService.getPatientService();
		MotechService motechService = contextService.getMotechService();
		PersonService personService = contextService.getPersonService();
		LocationService locationService = contextService.getLocationService();
		EncounterService encounterService = contextService
				.getEncounterService();
		ObsService obsService = contextService.getObsService();
		ConceptService conceptService = contextService.getConceptService();

		contextService.authenticate("admin", "test");

		PatientIdentifierType serialIdType = patientService
				.getPatientIdentifierTypeByName("Ghana Clinic Id");
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

		User nurse = motechService.getUserByPhoneNumber(nursePhoneNumber);
		encounter.setProvider(nurse);

		PersonAttribute clinic = nurse.getAttribute(personService
				.getPersonAttributeTypeByName("Health Center"));
		Integer clinicId = Integer.valueOf(clinic.getValue());
		Location clinicLocation = locationService.getLocation(clinicId);

		// Encounter types must be created previously
		EncounterType encounterType = encounterService
				.getEncounterType("MATERNALVISIT");
		encounter.setEncounterType(encounterType);
		encounter.setLocation(clinicLocation);
		encounter = encounterService.saveEncounter(encounter);

		if (tetanus) {
			Obs tetanusObs = new Obs();
			tetanusObs.setObsDatetime(visitDate);
			tetanusObs.setConcept(conceptService
					.getConcept("IMMUNIZATIONS ORDERED"));
			tetanusObs.setPerson(patient);
			tetanusObs.setLocation(clinicLocation);
			tetanusObs.setEncounter(encounter);
			tetanusObs.setValueCoded(conceptService
					.getConcept("TETANUS BOOSTER"));
			obsService.saveObs(tetanusObs, null);
		}

		// TODO: Add IPT to proper Concept as an Answer, not an immunization
		if (ipt) {
			Obs iptObs = new Obs();
			iptObs.setObsDatetime(visitDate);
			iptObs.setConcept(conceptService
					.getConcept("IMMUNIZATIONS ORDERED"));
			iptObs.setPerson(patient);
			iptObs.setLocation(clinicLocation);
			iptObs.setEncounter(encounter);
			iptObs.setValueCoded(conceptService
					.getConcept("INTERMITTENT PREVENTATIVE TREATMENT"));
			obsService.saveObs(iptObs, null);
		}

		if (itn) {
			Obs itnObs = new Obs();
			itnObs.setObsDatetime(visitDate);
			itnObs.setConcept(conceptService
					.getConcept("INSECTICIDE-TREATED NET USAGE"));
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
				.getConcept("PREGNANCY VISIT NUMBER"));
		visitNumberObs.setPerson(patient);
		visitNumberObs.setLocation(clinicLocation);
		visitNumberObs.setEncounter(encounter);
		visitNumberObs.setValueNumeric(new Double(visitNumber));
		obsService.saveObs(visitNumberObs, null);

		if (onARV) {
			Obs arvObs = new Obs();
			arvObs.setObsDatetime(visitDate);
			arvObs.setConcept(conceptService
					.getConcept("ANTIRETROVIRAL USE DURING PREGNANCY"));
			arvObs.setPerson(patient);
			arvObs.setLocation(clinicLocation);
			arvObs.setEncounter(encounter);
			arvObs.setValueCoded(conceptService
					.getConcept("ON ANTIRETROVIRAL THERAPY"));
			obsService.saveObs(arvObs, null);
		}

		if (prePMTCT) {
			Obs prePmtctObs = new Obs();
			prePmtctObs.setObsDatetime(visitDate);
			prePmtctObs
					.setConcept(conceptService
							.getConcept("PRE PREVENTING MATERNAL TO CHILD TRANSMISSION"));
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
							.getConcept("TEST PREVENTING MATERNAL TO CHILD TRANSMISSION"));
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
							.getConcept("POST PREVENTING MATERNAL TO CHILD TRANSMISSION"));
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
				.getConcept("HEMOGLOBIN AT 36 WEEKS"));
		hemoglobinObs.setPerson(patient);
		hemoglobinObs.setLocation(clinicLocation);
		hemoglobinObs.setEncounter(encounter);
		hemoglobinObs.setValueNumeric(hemoglobinAt36Weeks);
		obsService.saveObs(hemoglobinObs, null);
	}

	public void registerPregnancy(String nursePhoneNumber, Date date,
			String serialId, Date dueDate, Integer parity, Double hemoglobin) {

		PatientService patientService = contextService.getPatientService();
		MotechService motechService = contextService.getMotechService();
		PersonService personService = contextService.getPersonService();
		LocationService locationService = contextService.getLocationService();
		EncounterService encounterService = contextService
				.getEncounterService();
		ObsService obsService = contextService.getObsService();
		ConceptService conceptService = contextService.getConceptService();

		contextService.authenticate("admin", "test");

		PatientIdentifierType serialIdType = patientService
				.getPatientIdentifierTypeByName("Ghana Clinic Id");
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

		User nurse = motechService.getUserByPhoneNumber(nursePhoneNumber);
		encounter.setProvider(nurse);

		PersonAttribute clinic = nurse.getAttribute(personService
				.getPersonAttributeTypeByName("Health Center"));
		Integer clinicId = Integer.valueOf(clinic.getValue());
		Location clinicLocation = locationService.getLocation(clinicId);

		// Encounter types must be created previously
		EncounterType encounterType = encounterService
				.getEncounterType("PREGNANCYVISIT");
		encounter.setEncounterType(encounterType);
		encounter.setLocation(clinicLocation);
		encounter = encounterService.saveEncounter(encounter);

		Obs pregSatusObs = new Obs();
		pregSatusObs.setObsDatetime(visitDate);
		pregSatusObs.setConcept(conceptService.getConcept("PREGNANCY STATUS"));
		pregSatusObs.setPerson(patient);
		pregSatusObs.setLocation(clinicLocation);
		pregSatusObs.setEncounter(encounter);
		pregSatusObs.setValueNumeric(new Double(1)); // Boolean currently stored
		// as Numeric 1 or 0
		obsService.saveObs(pregSatusObs, null);

		Obs dueDateObs = new Obs();
		dueDateObs.setObsDatetime(visitDate);
		dueDateObs.setConcept(conceptService
				.getConcept("ESTIMATED DATE OF CONFINEMENT"));
		dueDateObs.setPerson(patient);
		dueDateObs.setLocation(clinicLocation);
		dueDateObs.setEncounter(encounter);
		dueDateObs.setValueDatetime(dueDate);
		obsService.saveObs(dueDateObs, null);

		Obs parityObs = new Obs();
		parityObs.setObsDatetime(visitDate);
		parityObs.setConcept(conceptService.getConcept("GRAVIDA"));
		parityObs.setPerson(patient);
		parityObs.setLocation(clinicLocation);
		parityObs.setEncounter(encounter);
		parityObs.setValueNumeric(new Double(parity));
		obsService.saveObs(parityObs, null);

		Obs hemoglobinObs = new Obs();
		hemoglobinObs.setObsDatetime(visitDate);
		hemoglobinObs.setConcept(conceptService.getConcept("HEMOGLOBIN"));
		hemoglobinObs.setPerson(patient);
		hemoglobinObs.setLocation(clinicLocation);
		hemoglobinObs.setEncounter(encounter);
		hemoglobinObs.setValueNumeric(hemoglobin);
		obsService.saveObs(hemoglobinObs, null);
	}

	public void log(LogType type, String message) {

		MotechService motechService = contextService.getMotechService();

		org.motech.model.Log log = new org.motech.model.Log();
		log.setDate(new Date());
		log.setType(type);
		log.setMessage(message);
		motechService.saveLog(log);
	}

	public void setContextService(ContextService contextService) {
		this.contextService = contextService;
	}

	public void setMessageStatus(String messageId, Boolean success) {

		MotechService motechService = contextService.getMotechService();
		PersonService personService = contextService.getPersonService();

		Message message = motechService.getMessage(messageId);
		if (message == null) {
			throw new MessageNotFoundException();
		}

		Integer recipientId = message.getSchedule().getRecipientId();
		Person messageRecipient = personService.getPerson(recipientId);
		PersonAttributeType phoneNumberAttrType = personService
				.getPersonAttributeTypeByName("Phone Number");
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

}
