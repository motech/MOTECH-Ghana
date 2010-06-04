package org.motechproject.server.svc.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.event.MessageProgram;
import org.motechproject.server.messaging.MessageNotFoundException;
import org.motechproject.server.model.Blackout;
import org.motechproject.server.model.Community;
import org.motechproject.server.model.ExpectedEncounter;
import org.motechproject.server.model.ExpectedObs;
import org.motechproject.server.model.Facility;
import org.motechproject.server.model.GeneralOutpatientEncounter;
import org.motechproject.server.model.Message;
import org.motechproject.server.model.MessageDefinition;
import org.motechproject.server.model.MessageProgramEnrollment;
import org.motechproject.server.model.MessageStatus;
import org.motechproject.server.model.ScheduledMessage;
import org.motechproject.server.model.TroubledPhone;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.MotechIdVerhoeffValidator;
import org.motechproject.server.omod.MotechService;
import org.motechproject.server.omod.tasks.MessageProgramUpdateTask;
import org.motechproject.server.omod.tasks.NotificationTask;
import org.motechproject.server.omod.tasks.StaffCareMessagingTask;
import org.motechproject.server.svc.BirthOutcomeChild;
import org.motechproject.server.svc.OpenmrsBean;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.server.util.GenderTypeConverter;
import org.motechproject.server.util.MotechConstants;
import org.motechproject.server.ws.WebServiceModelConverterImpl;
import org.motechproject.ws.BirthOutcome;
import org.motechproject.ws.Care;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.DayOfWeek;
import org.motechproject.ws.Gender;
import org.motechproject.ws.HIVResult;
import org.motechproject.ws.HowLearned;
import org.motechproject.ws.InterestReason;
import org.motechproject.ws.MediaType;
import org.motechproject.ws.NameValuePair;
import org.motechproject.ws.RegistrantType;
import org.motechproject.ws.RegistrationMode;
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
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.LogEntry;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.SchedulerService;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.util.AttributableDate;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.transaction.annotation.Transactional;

/**
 * An implementation of the RegistrarBean interface, implemented using a mix of
 * OpenMRS and module defined services.
 */
public class RegistrarBeanImpl implements RegistrarBean, OpenmrsBean {

	private static Log log = LogFactory.getLog(RegistrarBeanImpl.class);

	private ContextService contextService;
	public MessageService mobileService;
	private Map<String, MessageProgram> messagePrograms;
	private List<String> staffTypes;

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

	public User registerStaff(String firstName, String lastName, String phone,
			String staffType) {

		UserService userService = contextService.getUserService();

		// User creating other users must have atleast the Privileges to be
		// given

		// TODO: Create staff as person and use same User for all actions ?
		User staff = new User();

		// TODO: Remove this uber-hack with something more correct/efficient
		staff.setSystemId(generateSystemId());

		staff.setGender(MotechConstants.GENDER_UNKNOWN_OPENMRS);

		PersonName name = new PersonName(firstName, null, lastName);
		staff.addName(name);

		// Must be created previously through API or UI to lookup
		PersonAttributeType phoneNumberAttrType = getPhoneNumberAttributeType();
		staff.addAttribute(new PersonAttribute(phoneNumberAttrType, phone));

		// TODO: Create staff role with proper privileges
		Role role = userService.getRole(OpenmrsConstants.PROVIDER_ROLE);
		staff.addRole(role);

		return userService.saveUser(staff, generatePassword(8));
	}

	private String generateSystemId() {
		UserService userService = contextService.getUserService();
		return Integer.toString(userService.getAllUsers().size() + 1);
	}

	private char[] PASSCHARS = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
			'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
			'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
			'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
			'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8',
			'9', '0' };

	private String generatePassword(int length) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			int charIndex = (int) (Math.random() * PASSCHARS.length);
			sb.append(PASSCHARS[charIndex]);
		}
		return sb.toString();
	}

	@Transactional
	public Patient registerPatient(User staff, Location facility, Date date,
			RegistrationMode registrationMode, Integer motechId,
			RegistrantType registrantType, String firstName, String middleName,
			String lastName, String preferredName, Date dateOfBirth,
			Boolean estimatedBirthDate, Gender sex, Boolean insured,
			String nhis, Date nhisExpires, Patient mother, Community community,
			String address, String phoneNumber, Date expDeliveryDate,
			Boolean deliveryDateConfirmed, Boolean enroll, Boolean consent,
			ContactNumberType ownership, MediaType format, String language,
			DayOfWeek dayOfWeek, Date timeOfDay, InterestReason reason,
			HowLearned howLearned, Integer messagesStartWeek) {

		return registerPatient(staff, facility, registrationMode, motechId,
				registrantType, firstName, middleName, lastName, preferredName,
				dateOfBirth, estimatedBirthDate, sex, insured, nhis,
				nhisExpires, mother, community, address, phoneNumber,
				expDeliveryDate, deliveryDateConfirmed, enroll, consent,
				ownership, format, language, dayOfWeek, timeOfDay, reason,
				howLearned, messagesStartWeek);
	}

	@Transactional
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
			Integer messagesStartWeek) {

		Location facility = getGhanaLocation();
		User staff = contextService.getAuthenticatedUser();

		return registerPatient(staff, facility, registrationMode, motechId,
				registrantType, firstName, middleName, lastName, preferredName,
				dateOfBirth, estimatedBirthDate, sex, insured, nhis,
				nhisExpires, mother, community, address, phoneNumber,
				expDeliveryDate, deliveryDateConfirmed, enroll, consent,
				ownership, format, language, dayOfWeek, timeOfDay, reason,
				howLearned, messagesStartWeek);
	}

	private Patient registerPatient(User staff, Location facility,
			RegistrationMode registrationMode, Integer motechId,
			RegistrantType registrantType, String firstName, String middleName,
			String lastName, String preferredName, Date dateOfBirth,
			Boolean estimatedBirthDate, Gender sex, Boolean insured,
			String nhis, Date nhisExpires, Patient mother, Community community,
			String address, String phoneNumber, Date expDeliveryDate,
			Boolean deliveryDateConfirmed, Boolean enroll, Boolean consent,
			ContactNumberType ownership, MediaType format, String language,
			DayOfWeek dayOfWeek, Date timeOfDay, InterestReason reason,
			HowLearned howLearned, Integer messagesStartWeek) {

		PatientService patientService = contextService.getPatientService();
		PersonService personService = contextService.getPersonService();

		Patient patient = createPatient(staff, motechId, firstName, middleName,
				lastName, preferredName, dateOfBirth, estimatedBirthDate, sex,
				insured, nhis, nhisExpires, address, phoneNumber, ownership,
				format, language, dayOfWeek, timeOfDay, howLearned, reason);

		patient = patientService.savePatient(patient);

		if (community != null) {
			community.getResidents().add(patient);
		}

		if (mother != null) {
			RelationshipType parentChildRelationshipType = personService
					.getRelationshipTypeByName(MotechConstants.RELATIONSHIP_TYPE_PARENT_CHILD);
			Relationship motherRelationship = new Relationship(mother, patient,
					parentChildRelationshipType);
			personService.saveRelationship(motherRelationship);
		}

		Integer pregnancyDueDateObsId = null;
		if (registrantType == RegistrantType.PREGNANT_MOTHER) {
			pregnancyDueDateObsId = registerPregnancy(staff, facility, patient,
					expDeliveryDate, deliveryDateConfirmed, null, null, null);
		}

		enrollPatient(patient, community, enroll, consent, messagesStartWeek,
				pregnancyDueDateObsId);

		return patient;
	}

	private void enrollPatientWithAttributes(Patient patient,
			Community community, Boolean enroll, Boolean consent,
			ContactNumberType ownership, String phoneNumber, MediaType format,
			String language, DayOfWeek dayOfWeek, Date timeOfDay,
			InterestReason reason, HowLearned howLearned,
			Integer messagesStartWeek, Integer pregnancyDueDateObsId) {

		PatientService patientService = contextService.getPatientService();

		setPatientAttributes(patient, phoneNumber, ownership, format, language,
				dayOfWeek, timeOfDay, howLearned, reason, null, null, null);

		patientService.savePatient(patient);

		enrollPatient(patient, community, enroll, consent, messagesStartWeek,
				pregnancyDueDateObsId);
	}

	private void enrollPatient(Patient patient, Community community,
			Boolean enroll, Boolean consent, Integer messagesStartWeek,
			Integer pregnancyDueDateObsId) {

		boolean enrollPatient = Boolean.TRUE.equals(enroll)
				&& Boolean.TRUE.equals(consent);

		Integer referenceDateObsId = null;
		String infoMessageProgramName = null;

		if (pregnancyDueDateObsId != null) {
			infoMessageProgramName = "Weekly Pregnancy Message Program";

			referenceDateObsId = pregnancyDueDateObsId;

		} else if (patient.getAge() != null && patient.getAge() < 5) {
			infoMessageProgramName = "Weekly Info Child Message Program";

			// TODO: If mother specified, Remove mother's pregnancy message
			// enrollment

		} else if (messagesStartWeek != null) {
			infoMessageProgramName = "Weekly Info Pregnancy Message Program";

			if (messagesStartWeek != null && enrollPatient) {
				ObsService obsService = contextService.getObsService();

				Location ghanaLocation = getGhanaLocation();
				Date currentDate = new Date();

				Calendar calendar = Calendar.getInstance();
				// Convert weeks to days, plus one day
				calendar.add(Calendar.DATE, (messagesStartWeek * -7) + 1);
				Date referenceDate = calendar.getTime();

				Obs refDateObs = createDateValueObs(currentDate,
						getEnrollmentReferenceDateConcept(), patient,
						ghanaLocation, referenceDate, null, null);

				refDateObs = obsService.saveObs(refDateObs, null);
				referenceDateObsId = refDateObs.getObsId();
			}
		}

		if (enrollPatient && infoMessageProgramName != null) {

			addMessageProgramEnrollment(patient.getPatientId(),
					infoMessageProgramName, referenceDateObsId);

			// Lookup patient community if not provided
			// Only enroll patient in care messages if in KNDW district
			if (community == null) {
				community = getCommunityByPatient(patient);
			}
			if (community != null
					&& community.getFacility() != null
					&& community.getFacility().getLocation() != null
					&& MotechConstants.LOCATION_KASSENA_NANKANA_WEST
							.equals(community.getFacility().getLocation()
									.getCountyDistrict())) {

				addMessageProgramEnrollment(patient.getPatientId(),
						"Expected Care Message Program", null);
			}
		}
	}

	@Transactional
	public void demoRegisterPatient(RegistrationMode registrationMode,
			Integer motechId, String firstName, String middleName,
			String lastName, String preferredName, Date dateOfBirth,
			Boolean estimatedBirthDate, Gender sex, Boolean insured,
			String nhis, Date nhisExpires, Community community, String address,
			String phoneNumber, Boolean enroll, Boolean consent,
			ContactNumberType ownership, MediaType format, String language,
			DayOfWeek dayOfWeek, Date timeOfDay, InterestReason reason,
			HowLearned howLearned) {

		PatientService patientService = contextService.getPatientService();

		User staff = contextService.getAuthenticatedUser();

		Patient patient = createPatient(staff, motechId, firstName, middleName,
				lastName, preferredName, dateOfBirth, estimatedBirthDate, sex,
				insured, nhis, nhisExpires, address, phoneNumber, ownership,
				format, language, dayOfWeek, timeOfDay, howLearned, reason);

		patient = patientService.savePatient(patient);

		if (Boolean.TRUE.equals(enroll) && Boolean.TRUE.equals(consent)) {
			addMessageProgramEnrollment(patient.getPatientId(),
					"Demo Minute Message Program", null);
		}
	}

	@Transactional
	public void demoEnrollPatient(Patient patient) {
		addMessageProgramEnrollment(patient.getPersonId(),
				"Input Demo Message Program", null);
	}

	@Transactional
	private Patient createPatient(User staff, Integer motechId,
			String firstName, String middleName, String lastName,
			String prefName, Date birthDate, Boolean birthDateEst, Gender sex,
			Boolean insured, String nhis, Date nhisExpDate, String address,
			String phoneNumber, ContactNumberType phoneType,
			MediaType mediaType, String language, DayOfWeek dayOfWeek,
			Date timeOfDay, HowLearned howLearned, InterestReason interestReason) {

		Patient patient = new Patient();

		String motechIdString = null;
		if (motechId == null) {
			motechIdString = generateMotechId();
		} else {
			motechIdString = motechId.toString();
			excludeIdForGenerator(staff, motechIdString);
		}

		patient.addIdentifier(new PatientIdentifier(motechIdString,
				getMotechPatientIdType(), getGhanaLocation()));

		patient.addName(new PersonName(firstName, middleName, lastName));

		if (prefName != null) {
			PersonName preferredPersonName = new PersonName(prefName,
					middleName, lastName);
			preferredPersonName.setPreferred(true);
			patient.addName(preferredPersonName);
		}

		patient.setGender(GenderTypeConverter.toOpenMRSString(sex));
		patient.setBirthdate(birthDate);
		patient.setBirthdateEstimated(birthDateEst);

		if (address != null) {
			PersonAddress personAddress = new PersonAddress();
			personAddress.setAddress1(address);
			patient.addAddress(personAddress);
		}

		setPatientAttributes(patient, phoneNumber, phoneType, mediaType,
				language, dayOfWeek, timeOfDay, howLearned, interestReason,
				insured, nhis, nhisExpDate);

		return patient;
	}

	private void setPatientAttributes(Patient patient, String phoneNumber,
			ContactNumberType phoneType, MediaType mediaType, String language,
			DayOfWeek dayOfWeek, Date timeOfDay, HowLearned howLearned,
			InterestReason interestReason, Boolean insured, String nhis,
			Date nhisExpDate) {

		List<PersonAttribute> attrs = new ArrayList<PersonAttribute>();

		if (phoneNumber != null) {
			attrs.add(new PersonAttribute(getPhoneNumberAttributeType(),
					phoneNumber));
		}

		if (phoneType != null) {
			attrs.add(new PersonAttribute(getPhoneTypeAttributeType(),
					phoneType.name()));
		}

		if (mediaType != null) {
			attrs.add(new PersonAttribute(getMediaTypeAttributeType(),
					mediaType.name()));
		}

		if (language != null) {
			attrs
					.add(new PersonAttribute(getLanguageAttributeType(),
							language));
		}

		if (dayOfWeek != null) {
			attrs.add(new PersonAttribute(getDeliveryDayAttributeType(),
					dayOfWeek.name()));
		}

		if (timeOfDay != null) {
			SimpleDateFormat formatter = new SimpleDateFormat(
					MotechConstants.TIME_FORMAT_DELIVERY_TIME);
			attrs.add(new PersonAttribute(getDeliveryTimeAttributeType(),
					formatter.format(timeOfDay)));
		}

		if (howLearned != null) {
			attrs.add(new PersonAttribute(getHowLearnedAttributeType(),
					howLearned.name()));
		}

		if (interestReason != null) {
			attrs.add(new PersonAttribute(getInterestReasonAttributeType(),
					interestReason.name()));
		}

		if (insured != null) {
			attrs.add(new PersonAttribute(getInsuredAttributeType(), insured
					.toString()));
		}

		if (nhis != null) {
			attrs.add(new PersonAttribute(getNHISNumberAttributeType(), nhis));
		}

		if (nhisExpDate != null) {
			SimpleDateFormat formatter = new SimpleDateFormat(
					MotechConstants.DATE_FORMAT);
			attrs.add(new PersonAttribute(getNHISExpirationDateAttributeType(),
					formatter.format(nhisExpDate)));
		}

		for (PersonAttribute attr : attrs)
			patient.addAttribute(attr);
	}

	@Transactional
	public void editPatient(User staff, Date date, Patient patient,
			String phoneNumber, ContactNumberType phoneOwnership, String nhis,
			Date nhisExpires, Boolean stopEnrollment) {

		PatientService patientService = contextService.getPatientService();

		setPatientAttributes(patient, phoneNumber, phoneOwnership, null, null,
				null, null, null, null, null, nhis, nhisExpires);

		patientService.savePatient(patient);

		if (Boolean.TRUE.equals(stopEnrollment)) {
			removeAllMessageProgramEnrollments(patient.getPatientId());
		}
	}

	@Transactional
	public void editPatient(Patient patient, String firstName,
			String middleName, String lastName, String preferredName,
			Date dateOfBirth, Boolean estimatedBirthDate, Gender sex,
			Boolean insured, String nhis, Date nhisExpires,
			Community community, String address, String phoneNumber,
			Date expDeliveryDate, Boolean enroll, Boolean consent,
			ContactNumberType ownership, MediaType format, String language,
			DayOfWeek dayOfWeek, Date timeOfDay) {

		PatientService patientService = contextService.getPatientService();

		patient.setBirthdate(dateOfBirth);
		patient.setBirthdateEstimated(estimatedBirthDate);
		patient.setGender(GenderTypeConverter.toOpenMRSString(sex));

		Set<PersonName> patientNames = patient.getNames();
		if (patientNames.isEmpty()) {
			patient.addName(new PersonName(firstName, middleName, lastName));
			if (preferredName != null) {
				PersonName preferredPersonName = new PersonName(preferredName,
						middleName, lastName);
				preferredPersonName.setPreferred(true);
				patient.addName(preferredPersonName);
			}
		} else {
			for (PersonName name : patient.getNames()) {
				if (name.isPreferred()) {
					if (preferredName != null) {
						name.setGivenName(preferredName);
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
			patientAddress.setAddress1(address);
			patient.addAddress(patientAddress);
		} else {
			patientAddress.setAddress1(address);
		}

		Community currentCommunity = getCommunityByPatient(patient);
		if (currentCommunity != null
				&& currentCommunity.getCommunityId() != null
				&& community != null
				&& community.getCommunityId() != null
				&& !currentCommunity.getCommunityId().equals(
						community.getCommunityId())) {
			currentCommunity.getResidents().remove(patient);
			// Query flushes session
			if (getCommunityByPatient(patient) == null) {
				community.getResidents().add(patient);
				currentCommunity = community;
			}
		}

		setPatientAttributes(patient, phoneNumber, ownership, format, language,
				dayOfWeek, timeOfDay, null, null, insured, nhis, nhisExpires);

		patientService.savePatient(patient);

		Integer dueDateObsId = null;
		if (expDeliveryDate != null) {
			Obs pregnancy = getActivePregnancy(patient.getPatientId());
			Obs dueDateObs = getActivePregnancyDueDateObs(patient
					.getPatientId(), pregnancy);
			if (dueDateObs != null) {
				dueDateObsId = dueDateObs.getObsId();
				if (!expDeliveryDate.equals(dueDateObs.getValueDatetime())) {
					dueDateObsId = updatePregnancyDueDateObs(pregnancy,
							dueDateObs, expDeliveryDate, dueDateObs
									.getEncounter());
				}
			}
		}

		if (Boolean.FALSE.equals(enroll)) {
			removeAllMessageProgramEnrollments(patient.getPatientId());
		} else {
			enrollPatient(patient, currentCommunity, enroll, consent, null,
					dueDateObsId);
		}
	}

	@Transactional
	public void registerPregnancy(Patient patient, Date expDeliveryDate,
			Boolean deliveryDateConfirmed, Boolean enroll, Boolean consent,
			String phoneNumber, ContactNumberType ownership, MediaType format,
			String language, DayOfWeek dayOfWeek, Date timeOfDay,
			InterestReason reason, HowLearned howLearned) {

		Integer pregnancyDueDateObsId = checkExistingPregnancy(patient);

		Location facility = getGhanaLocation();
		User staff = contextService.getAuthenticatedUser();

		if (pregnancyDueDateObsId == null) {
			pregnancyDueDateObsId = registerPregnancy(staff, facility, patient,
					expDeliveryDate, deliveryDateConfirmed, null, null, null);
		}

		enrollPatientWithAttributes(patient, null, enroll, consent, ownership,
				phoneNumber, format, language, dayOfWeek, timeOfDay, reason,
				howLearned, null, pregnancyDueDateObsId);
	}

	private Integer registerPregnancy(User staff, Location facility,
			Patient patient, Date dueDate, Boolean dueDateConfirmed,
			Integer gravida, Integer parity, Integer height) {

		EncounterService encounterService = contextService
				.getEncounterService();
		ObsService obsService = contextService.getObsService();

		Date currentDate = new Date();

		Encounter encounter = new Encounter();
		encounter
				.setEncounterType(getPregnancyRegistrationVisitEncounterType());
		encounter.setEncounterDatetime(currentDate);
		encounter.setPatient(patient);
		encounter.setLocation(facility);
		encounter.setProvider(staff);
		encounter = encounterService.saveEncounter(encounter);

		Obs pregnancyObs = createObs(currentDate, getPregnancyConcept(),
				patient, facility, encounter, null);

		Obs pregnancyStatusObs = createBooleanValueObs(currentDate,
				getPregnancyStatusConcept(), patient, facility, Boolean.TRUE,
				encounter, null);
		pregnancyObs.addGroupMember(pregnancyStatusObs);

		Obs dueDateObs = null;
		if (dueDate != null) {
			dueDateObs = createDateValueObs(currentDate, getDueDateConcept(),
					patient, facility, dueDate, encounter, null);
			pregnancyObs.addGroupMember(dueDateObs);
		}

		if (dueDateConfirmed != null) {
			Obs dueDateConfirmedObs = createBooleanValueObs(currentDate,
					getDueDateConfirmedConcept(), patient, facility,
					dueDateConfirmed, encounter, null);
			pregnancyObs.addGroupMember(dueDateConfirmedObs);
		}

		if (gravida != null) {
			Obs gravidaObs = createNumericValueObs(currentDate,
					getGravidaConcept(), patient, facility, gravida, encounter,
					null);
			pregnancyObs.addGroupMember(gravidaObs);
		}

		if (parity != null) {
			Obs parityObs = createNumericValueObs(currentDate,
					getParityConcept(), patient, facility, parity, encounter,
					null);
			pregnancyObs.addGroupMember(parityObs);
		}

		if (height != null) {
			Obs heightObs = createNumericValueObs(currentDate,
					getHeightConcept(), patient, facility, height, encounter,
					null);
			pregnancyObs.addGroupMember(heightObs);
		}

		obsService.saveObs(pregnancyObs, null);

		if (dueDateObs != null) {
			return dueDateObs.getObsId();
		}
		return null;
	}

	@Transactional
	public void registerPregnancy(User staff, Location facility, Date date,
			Patient patient, Date estDeliveryDate, Boolean enroll,
			Boolean consent, ContactNumberType ownership, String phoneNumber,
			MediaType format, String language, DayOfWeek dayOfWeek,
			Date timeOfDay, HowLearned howLearned) {

		Integer pregnancyDueDateObsId = checkExistingPregnancy(patient);

		if (pregnancyDueDateObsId == null) {
			pregnancyDueDateObsId = registerPregnancy(staff, facility, patient,
					estDeliveryDate, null, null, null, null);
		}

		enrollPatientWithAttributes(patient, null, enroll, consent, ownership,
				phoneNumber, format, language, dayOfWeek, timeOfDay, null,
				howLearned, null, pregnancyDueDateObsId);
	}

	private Integer checkExistingPregnancy(Patient patient) {
		Obs pregnancyObs = getActivePregnancy(patient.getPatientId());

		Integer pregnancyDueDateObsId = null;
		if (pregnancyObs != null) {
			log.warn("Entering Pregnancy for patient with active pregnancy, "
					+ "patient id=" + patient.getPatientId());

			Obs pregnancyDueDateObs = getActivePregnancyDueDateObs(patient
					.getPatientId(), pregnancyObs);
			if (pregnancyDueDateObs != null) {
				pregnancyDueDateObsId = pregnancyDueDateObs.getObsId();
			} else {
				log.warn("No due date found for active pregnancy, patient id="
						+ patient.getPatientId());
			}
		}
		return pregnancyDueDateObsId;
	}

	@Transactional
	public void registerANCMother(User staff, Location facility, Date date,
			Patient patient, String ancRegNumber, Date estDeliveryDate,
			Integer height, Integer gravida, Integer parity, Boolean enroll,
			Boolean consent, ContactNumberType ownership, String phoneNumber,
			MediaType format, String language, DayOfWeek dayOfWeek,
			Date timeOfDay, HowLearned howLearned) {

		EncounterService encounterService = contextService
				.getEncounterService();

		Integer pregnancyDueDateObsId = checkExistingPregnancy(patient);
		if (pregnancyDueDateObsId == null) {
			pregnancyDueDateObsId = registerPregnancy(staff, facility, patient,
					estDeliveryDate, null, gravida, parity, height);
		}

		enrollPatientWithAttributes(patient, null, enroll, consent, ownership,
				phoneNumber, format, language, dayOfWeek, timeOfDay, null,
				howLearned, null, pregnancyDueDateObsId);

		Encounter encounter = new Encounter();
		encounter.setEncounterType(getANCRegistrationEncounterType());
		encounter.setEncounterDatetime(date);
		encounter.setPatient(patient);
		encounter.setLocation(facility);
		encounter.setProvider(staff);

		Obs ancRegNumObs = createTextValueObs(date,
				getANCRegistrationNumberConcept(), patient, facility,
				ancRegNumber, encounter, null);
		encounter.addObs(ancRegNumObs);

		encounterService.saveEncounter(encounter);
	}

	@Transactional
	public void registerCWCChild(User staff, Location facility, Date date,
			Patient patient, String cwcRegNumber, Boolean enroll,
			Boolean consent, ContactNumberType ownership, String phoneNumber,
			MediaType format, String language, DayOfWeek dayOfWeek,
			Date timeOfDay, HowLearned howLearned) {

		enrollPatientWithAttributes(patient, null, enroll, consent, ownership,
				phoneNumber, format, language, dayOfWeek, timeOfDay, null,
				howLearned, null, null);

		EncounterService encounterService = contextService
				.getEncounterService();

		Encounter encounter = new Encounter();
		encounter.setEncounterType(getCWCRegistrationEncounterType());
		encounter.setEncounterDatetime(date);
		encounter.setPatient(patient);
		encounter.setLocation(facility);
		encounter.setProvider(staff);

		Obs cwcRegNumObs = createTextValueObs(date,
				getCWCRegistrationNumberConcept(), patient, facility,
				cwcRegNumber, encounter, null);
		encounter.addObs(cwcRegNumObs);

		encounterService.saveEncounter(encounter);
	}

	@Transactional
	public void recordMotherANCVisit(User staff, Location facility, Date date,
			Patient patient, Integer visitNumber, Integer ancLocation,
			String house, String community, Date estDeliveryDate,
			Integer bpSystolic, Integer bpDiastolic, Double weight,
			Integer ttDose, Integer iptDose, Boolean iptReactive,
			Boolean itnUse, Integer fht, Integer fhr,
			Boolean urineTestProteinPositive, Boolean urineTestGlucosePositive,
			Double hemoglobin, Boolean vdrlReactive, Boolean vdrlTreatment,
			Boolean dewormer, Boolean maleInvolved, Boolean pmtct,
			Boolean preTestCounseled, HIVResult hivTestResult,
			Boolean postTestCounseled, Boolean pmtctTreatment,
			Boolean referred, Date nextANCDate, String comments) {

		EncounterService encounterService = contextService
				.getEncounterService();

		Encounter encounter = new Encounter();
		encounter.setEncounterType(getANCVisitEncounterType());
		encounter.setEncounterDatetime(date);
		encounter.setPatient(patient);
		encounter.setLocation(facility);
		encounter.setProvider(staff);

		Obs pregnancyObs = getActivePregnancy(patient.getPatientId());
		if (pregnancyObs == null) {
			log.warn("Entered ANC visit for patient without active pregnancy, "
					+ "patient id=" + patient.getPatientId());
		}

		if (visitNumber != null) {
			Obs visitNumberObs = createNumericValueObs(date,
					getVisitNumberConcept(), patient, facility, visitNumber,
					encounter, null);
			encounter.addObs(visitNumberObs);
		}
		if (ancLocation != null) {
			Obs ancLocationObs = createNumericValueObs(date,
					getANCPNCLocationConcept(), patient, facility, ancLocation,
					encounter, null);
			encounter.addObs(ancLocationObs);
		}
		if (house != null) {
			Obs houseObs = createTextValueObs(date, getANCPNCLocationConcept(),
					patient, facility, house, encounter, null);
			encounter.addObs(houseObs);
		}
		if (community != null) {
			Obs communityObs = createTextValueObs(date,
					getANCPNCLocationConcept(), patient, facility, community,
					encounter, null);
			encounter.addObs(communityObs);
		}
		if (bpSystolic != null) {
			Obs bpSystolicObs = createNumericValueObs(date,
					getSystolicBloodPressureConcept(), patient, facility,
					bpSystolic, encounter, null);
			encounter.addObs(bpSystolicObs);
		}
		if (bpDiastolic != null) {
			Obs bpDiastolicObs = createNumericValueObs(date,
					getDiastolicBloodPressureConcept(), patient, facility,
					bpDiastolic, encounter, null);
			encounter.addObs(bpDiastolicObs);
		}
		if (weight != null) {
			Obs weightObs = createNumericValueObs(date, getWeightConcept(),
					patient, facility, weight, encounter, null);
			encounter.addObs(weightObs);
		}
		if (ttDose != null) {
			Obs ttDoseObs = createNumericValueObs(date,
					getTetanusDoseConcept(), patient, facility, ttDose,
					encounter, null);
			encounter.addObs(ttDoseObs);
		}
		if (iptDose != null) {
			Obs iptDoseObs = createNumericValueObs(date, getIPTDoseConcept(),
					patient, facility, iptDose, encounter, null);
			encounter.addObs(iptDoseObs);
		}
		if (iptReactive != null) {
			Concept iptReactionValueConcept = null;
			if (Boolean.TRUE.equals(iptReactive)) {
				iptReactionValueConcept = getReactiveConcept();
			} else {
				iptReactionValueConcept = getNonReactiveConcept();
			}
			Obs iptReactiveObs = createConceptValueObs(date,
					getIPTReactionConcept(), patient, facility,
					iptReactionValueConcept, encounter, null);
			encounter.addObs(iptReactiveObs);
		}
		if (itnUse != null) {
			Obs itnUseObs = createBooleanValueObs(date, getITNConcept(),
					patient, facility, itnUse, encounter, null);
			encounter.addObs(itnUseObs);
		}
		if (fht != null) {
			Obs fhtObs = createNumericValueObs(date, getFundalHeightConcept(),
					patient, facility, fht, encounter, null);
			encounter.addObs(fhtObs);
		}
		if (fhr != null) {
			Obs fhrObs = createNumericValueObs(date,
					getFetalHeartRateConcept(), patient, facility, fhr,
					encounter, null);
			encounter.addObs(fhrObs);
		}
		if (urineTestProteinPositive != null) {
			Concept urineProteinTestValueConcept = null;
			if (Boolean.TRUE.equals(urineTestProteinPositive)) {
				urineProteinTestValueConcept = getPositiveConcept();
			} else {
				urineProteinTestValueConcept = getNegativeConcept();
			}
			Obs urineTestProteinPositiveObs = createConceptValueObs(date,
					getUrineProteinTestConcept(), patient, facility,
					urineProteinTestValueConcept, encounter, null);
			encounter.addObs(urineTestProteinPositiveObs);
		}
		if (urineTestGlucosePositive != null) {
			Concept urineGlucoseTestValueConcept = null;
			if (Boolean.TRUE.equals(urineTestGlucosePositive)) {
				urineGlucoseTestValueConcept = getPositiveConcept();
			} else {
				urineGlucoseTestValueConcept = getNegativeConcept();
			}
			Obs urineTestProteinPositiveObs = createConceptValueObs(date,
					getUrineGlucoseTestConcept(), patient, facility,
					urineGlucoseTestValueConcept, encounter, null);
			encounter.addObs(urineTestProteinPositiveObs);
		}
		if (hemoglobin != null) {
			Obs hemoglobinObs = createNumericValueObs(date,
					getHemoglobinConcept(), patient, facility, hemoglobin,
					encounter, null);
			encounter.addObs(hemoglobinObs);
		}
		if (vdrlReactive != null) {
			Concept vdrlValueConcept = null;
			if (Boolean.TRUE.equals(vdrlReactive)) {
				vdrlValueConcept = getReactiveConcept();
			} else {
				vdrlValueConcept = getNonReactiveConcept();
			}
			Obs vdrlReactiveObs = createConceptValueObs(date, getVDRLConcept(),
					patient, facility, vdrlValueConcept, encounter, null);
			encounter.addObs(vdrlReactiveObs);
		}
		if (vdrlTreatment != null) {
			Obs vdrlTreatmentObs = createBooleanValueObs(date,
					getVDRLTreatmentConcept(), patient, facility,
					vdrlTreatment, encounter, null);
			encounter.addObs(vdrlTreatmentObs);
		}
		if (dewormer != null) {
			Obs dewormerObs = createBooleanValueObs(date, getDewormerConcept(),
					patient, facility, dewormer, encounter, null);
			encounter.addObs(dewormerObs);
		}
		if (maleInvolved != null) {
			Obs maleInvolvedObs = createBooleanValueObs(date,
					getMaleInvolvementConcept(), patient, facility,
					maleInvolved, encounter, null);
			encounter.addObs(maleInvolvedObs);
		}
		if (pmtct != null) {
			Obs pmtctObs = createBooleanValueObs(date, getPMTCTConcept(),
					patient, facility, pmtct, encounter, null);
			encounter.addObs(pmtctObs);
		}
		if (preTestCounseled != null) {
			Obs preTestCounseledObs = createBooleanValueObs(date,
					getPreHIVTestCounselingConcept(), patient, facility,
					preTestCounseled, encounter, null);
			encounter.addObs(preTestCounseledObs);
		}
		if (hivTestResult != null) {
			Obs hivResultObs = createTextValueObs(date,
					getHIVTestResultConcept(), patient, facility, hivTestResult
							.name(), encounter, null);
			encounter.addObs(hivResultObs);
		}
		if (postTestCounseled != null) {
			Obs postTestCounseledObs = createBooleanValueObs(date,
					getPostHIVTestCounselingConcept(), patient, facility,
					postTestCounseled, encounter, null);
			encounter.addObs(postTestCounseledObs);
		}
		if (pmtctTreatment != null) {
			Obs pmtctTreatmentObs = createBooleanValueObs(date,
					getPMTCTTreatmentConcept(), patient, facility,
					pmtctTreatment, encounter, null);
			encounter.addObs(pmtctTreatmentObs);
		}
		if (referred != null) {
			Obs referredObs = createBooleanValueObs(date, getReferredConcept(),
					patient, facility, referred, encounter, null);
			encounter.addObs(referredObs);
		}
		if (nextANCDate != null) {
			Obs nextANCDateObs = createDateValueObs(date,
					getNextANCDateConcept(), patient, facility, nextANCDate,
					encounter, null);
			encounter.addObs(nextANCDateObs);
		}
		if (comments != null) {
			Obs commentsObs = createTextValueObs(date, getCommentsConcept(),
					patient, facility, comments, encounter, null);
			encounter.addObs(commentsObs);
		}

		encounter = encounterService.saveEncounter(encounter);

		if (estDeliveryDate != null) {
			Obs pregnancyDueDateObs = getActivePregnancyDueDateObs(patient
					.getPatientId(), pregnancyObs);
			if (pregnancyDueDateObs != null) {
				updatePregnancyDueDateObs(pregnancyObs, pregnancyDueDateObs,
						estDeliveryDate, encounter);
			} else {
				log.warn("Cannot update pregnancy due date, "
						+ "no active pregnancy due date found, patient id="
						+ patient.getPatientId());
			}
		}
	}

	@Transactional
	public void recordPregnancyTermination(User staff, Location facility,
			Date date, Patient patient, Integer terminationType,
			Integer procedure, Integer[] complications, Boolean maternalDeath,
			Boolean referred, Boolean postAbortionFPCounseled,
			Boolean postAbortionFPAccepted, String comments) {

		EncounterService encounterService = contextService
				.getEncounterService();

		Encounter encounter = new Encounter();
		encounter.setEncounterType(getPregnancyTerminationVisitEncounterType());
		encounter.setEncounterDatetime(date);
		encounter.setPatient(patient);
		encounter.setLocation(facility);
		encounter.setProvider(staff);

		Obs pregnancyObs = getActivePregnancy(patient.getPatientId());
		if (pregnancyObs == null) {
			log.warn("Entered Pregnancy termination "
					+ "for patient without active pregnancy, patient id="
					+ patient.getPatientId());
		}

		if (terminationType != null) {
			Obs terminationTypeObs = createNumericValueObs(date,
					getTerminationTypeConcept(), patient, facility,
					terminationType, encounter, null);
			encounter.addObs(terminationTypeObs);
		}
		if (procedure != null) {
			Obs procedureObs = createNumericValueObs(date,
					getTerminationProcedureConcept(), patient, facility,
					procedure, encounter, null);
			encounter.addObs(procedureObs);
		}
		if (complications != null) {
			for (Integer complication : complications) {
				Obs complicationObs = createNumericValueObs(date,
						getTerminationComplicationConcept(), patient, facility,
						complication, encounter, null);
				encounter.addObs(complicationObs);
			}
		}
		if (maternalDeath != null) {
			Obs maternalDeathObs = createBooleanValueObs(date,
					getMaternalDeathConcept(), patient, facility,
					maternalDeath, encounter, null);
			encounter.addObs(maternalDeathObs);
		}
		if (referred != null) {
			Obs referredObs = createBooleanValueObs(date, getReferredConcept(),
					patient, facility, referred, encounter, null);
			encounter.addObs(referredObs);
		}
		if (postAbortionFPCounseled != null) {
			Obs postCounseledObs = createBooleanValueObs(date,
					getPostAbortionFPCounselingConcept(), patient, facility,
					postAbortionFPCounseled, encounter, null);
			encounter.addObs(postCounseledObs);
		}
		if (postAbortionFPAccepted != null) {
			Obs postAcceptedObs = createBooleanValueObs(date,
					getPostAbortionFPAcceptedConcept(), patient, facility,
					postAbortionFPAccepted, encounter, null);
			encounter.addObs(postAcceptedObs);
		}
		if (comments != null) {
			Obs commentsObs = createTextValueObs(date, getCommentsConcept(),
					patient, facility, comments, encounter, null);
			encounter.addObs(commentsObs);
		}

		Obs pregnancyStatusObs = createBooleanValueObs(date,
				getPregnancyStatusConcept(), patient, facility, Boolean.FALSE,
				encounter, null);
		pregnancyStatusObs.setObsGroup(pregnancyObs);
		encounter.addObs(pregnancyStatusObs);

		encounterService.saveEncounter(encounter);

		if (Boolean.TRUE.equals(maternalDeath)) {
			processPatientDeath(patient, date);
		}
	}

	@Transactional
	public List<Patient> recordPregnancyDelivery(User staff, Location facility,
			Date datetime, Patient patient, Integer mode, Integer outcome,
			Integer deliveryLocation, Integer deliveredBy,
			Boolean maleInvolved, Integer[] complications, Integer vvf,
			Boolean maternalDeath, String comments,
			List<BirthOutcomeChild> outcomes) {

		EncounterService encounterService = contextService
				.getEncounterService();

		Encounter encounter = new Encounter();
		encounter.setEncounterType(getPregnancyDeliveryVisitEncounterType());
		encounter.setEncounterDatetime(datetime);
		encounter.setPatient(patient);
		encounter.setLocation(facility);
		encounter.setProvider(staff);

		Obs pregnancyObs = getActivePregnancy(patient.getPatientId());
		if (pregnancyObs == null) {
			log.warn("Entered Pregnancy delivery "
					+ "for patient without active pregnancy, patient id="
					+ patient.getPatientId());
		}

		if (mode != null) {
			Obs modeObs = createNumericValueObs(datetime,
					getDeliveryModeConcept(), patient, facility, mode,
					encounter, null);
			encounter.addObs(modeObs);
		}
		if (outcome != null) {
			Obs outcomeObs = createNumericValueObs(datetime,
					getDeliveryOutcomeConcept(), patient, facility, outcome,
					encounter, null);
			encounter.addObs(outcomeObs);
		}
		if (deliveryLocation != null) {
			Obs locationObs = createNumericValueObs(datetime,
					getDeliveryLocationConcept(), patient, facility,
					deliveryLocation, encounter, null);
			encounter.addObs(locationObs);
		}
		if (deliveredBy != null) {
			Obs deliveredByObs = createNumericValueObs(datetime,
					getDeliveredByConcept(), patient, facility, deliveredBy,
					encounter, null);
			encounter.addObs(deliveredByObs);
		}
		if (maleInvolved != null) {
			Obs maleInvolvedObs = createBooleanValueObs(datetime,
					getMaleInvolvementConcept(), patient, facility,
					maleInvolved, encounter, null);
			encounter.addObs(maleInvolvedObs);
		}
		if (complications != null) {
			for (Integer complication : complications) {
				Obs complicationObs = createNumericValueObs(datetime,
						getDeliveryComplicationConcept(), patient, facility,
						complication, encounter, null);
				encounter.addObs(complicationObs);
			}
		}
		if (vvf != null) {
			Obs vvfObs = createNumericValueObs(datetime, getVVFRepairConcept(),
					patient, facility, vvf, encounter, null);
			encounter.addObs(vvfObs);
		}
		if (maternalDeath != null) {
			Obs maternalDeathObs = createBooleanValueObs(datetime,
					getMaternalDeathConcept(), patient, facility,
					maternalDeath, encounter, null);
			encounter.addObs(maternalDeathObs);
		}
		if (comments != null) {
			Obs commentsObs = createTextValueObs(datetime,
					getCommentsConcept(), patient, facility, comments,
					encounter, null);
			encounter.addObs(commentsObs);
		}

		Obs pregnancyStatusObs = createBooleanValueObs(datetime,
				getPregnancyStatusConcept(), patient, facility, Boolean.FALSE,
				encounter, null);
		pregnancyStatusObs.setObsGroup(pregnancyObs);
		encounter.addObs(pregnancyStatusObs);

		List<Patient> aliveChildPatients = new ArrayList<Patient>();

		for (BirthOutcomeChild childOutcome : outcomes) {
			if (childOutcome.getOutcome() == null
					|| childOutcome.getSex() == null) {
				// Skip child outcomes missing required outcome or sex
				continue;
			}
			Obs childOutcomeObs = createTextValueObs(datetime,
					getBirthOutcomeConcept(), patient, facility, childOutcome
							.getOutcome().name(), encounter, null);
			encounter.addObs(childOutcomeObs);

			Patient child = registerPatient(staff, facility, childOutcome
					.getIdMode(), childOutcome.getMotechId(),
					RegistrantType.CHILD_UNDER_FIVE, childOutcome
							.getFirstName(), null, null, null, datetime, false,
					childOutcome.getSex(), null, null, null, patient, null,
					null, null, null, null, null, null, null, null, null, null,
					null, null, null, null);

			if (childOutcome.getWeight() != null) {
				recordBirthData(staff, facility, child, datetime, childOutcome
						.getWeight());
			}

			if (BirthOutcome.A != childOutcome.getOutcome()) {
				processPatientDeath(child, datetime);
			} else {
				aliveChildPatients.add(child);
			}
		}

		encounterService.saveEncounter(encounter);

		if (Boolean.TRUE.equals(maternalDeath)) {
			processPatientDeath(patient, datetime);
		}

		return aliveChildPatients;
	}

	private void recordBirthData(User staff, Location facility, Patient child,
			Date datetime, Double weight) {
		EncounterService encounterService = contextService
				.getEncounterService();

		Encounter encounter = new Encounter();
		encounter.setEncounterType(getBirthEncounterType());
		encounter.setEncounterDatetime(datetime);
		encounter.setPatient(child);
		encounter.setLocation(facility);
		encounter.setProvider(staff);

		if (weight != null) {
			Obs weightObs = createNumericValueObs(datetime, getWeightConcept(),
					child, facility, weight, encounter, null);
			encounter.addObs(weightObs);
		}

		encounterService.saveEncounter(encounter);
	}

	@Transactional
	public void recordPregnancyDeliveryNotification(User staff,
			Location facility, Date date, Patient patient) {

		EncounterService encounterService = contextService
				.getEncounterService();

		Encounter encounter = new Encounter();
		encounter
				.setEncounterType(getPregnancyDeliveryNotificationEncounterType());
		encounter.setEncounterDatetime(date);
		encounter.setPatient(patient);
		encounter.setLocation(facility);
		encounter.setProvider(staff);

		Obs pregnancyObs = getActivePregnancy(patient.getPatientId());
		if (pregnancyObs == null) {
			log
					.warn("Entered Pregnancy delivery notification for patient without active pregnancy, "
							+ "patient id=" + patient.getPatientId());
		}

		Obs pregnancyStatusObs = createBooleanValueObs(date,
				getPregnancyStatusConcept(), patient, facility, Boolean.FALSE,
				encounter, null);
		pregnancyStatusObs.setObsGroup(pregnancyObs);
		encounter.addObs(pregnancyStatusObs);

		encounterService.saveEncounter(encounter);

		// Send message only if closing active pregnancy
		if (pregnancyObs != null) {
			sendDeliveryNotification(patient);
		}
	}

	private void sendDeliveryNotification(Patient patient) {
		// Send message to phone number of facility serving patient's community
		Community community = getCommunityByPatient(patient);
		if (community != null && community.getFacility() != null) {
			String phoneNumber = community.getFacility().getPhoneNumber();
			if (phoneNumber != null) {

				MessageDefinition messageDef = getMessageDefinition("pregnancy.notification");
				if (messageDef == null) {
					log.error("Pregnancy delivery notification message "
							+ "does not exist");
					return;
				}

				String messageId = null;
				NameValuePair[] nameValues = new NameValuePair[0];
				MediaType mediaType = MediaType.TEXT;
				String languageCode = "en";

				// Send immediately if not during blackout,
				// otherwise adjust time to after the blackout period
				Date currentDate = new Date();
				Date messageStartDate = adjustForBlackout(currentDate);
				if (currentDate.equals(messageStartDate)) {
					messageStartDate = null;
				}

				WebServiceModelConverterImpl wsModelConverter = new WebServiceModelConverterImpl();
				wsModelConverter.setRegistrarBean(this);
				org.motechproject.ws.Patient wsPatient = wsModelConverter
						.patientToWebService(patient, true);
				org.motechproject.ws.Patient[] wsPatients = new org.motechproject.ws.Patient[] { wsPatient };

				sendStaffMessage(messageId, nameValues, phoneNumber,
						languageCode, mediaType, messageDef.getPublicId(),
						messageStartDate, null, wsPatients);
			}
		}
	}

	@Transactional
	public void recordMotherPNCVisit(User staff, Location facility,
			Date datetime, Patient patient, Integer visitNumber,
			Integer pncLocation, String house, String community,
			Boolean referred, Boolean maleInvolved, Boolean vitaminA,
			Integer ttDose, Integer lochiaColour, Boolean lochiaAmountExcess,
			Integer temperature, Integer fht, String comments) {

		EncounterService encounterService = contextService
				.getEncounterService();

		Encounter encounter = new Encounter();
		encounter.setEncounterType(getMotherPNCVisitEncounterType());
		encounter.setEncounterDatetime(datetime);
		encounter.setPatient(patient);
		encounter.setLocation(facility);
		encounter.setProvider(staff);

		if (visitNumber != null) {
			Obs visitNumberObs = createNumericValueObs(datetime,
					getVisitNumberConcept(), patient, facility, visitNumber,
					encounter, null);
			encounter.addObs(visitNumberObs);
		}
		if (pncLocation != null) {
			Obs pncLocationObs = createNumericValueObs(datetime,
					getANCPNCLocationConcept(), patient, facility, pncLocation,
					encounter, null);
			encounter.addObs(pncLocationObs);
		}
		if (house != null) {
			Obs houseObs = createTextValueObs(datetime, getHouseConcept(),
					patient, facility, house, encounter, null);
			encounter.addObs(houseObs);
		}
		if (community != null) {
			Obs communityObs = createTextValueObs(datetime,
					getCommunityConcept(), patient, facility, community,
					encounter, null);
			encounter.addObs(communityObs);
		}
		if (referred != null) {
			Obs referredObs = createBooleanValueObs(datetime,
					getReferredConcept(), patient, facility, referred,
					encounter, null);
			encounter.addObs(referredObs);
		}
		if (maleInvolved != null) {
			Obs maleInvolvedObs = createBooleanValueObs(datetime,
					getMaleInvolvementConcept(), patient, facility,
					maleInvolved, encounter, null);
			encounter.addObs(maleInvolvedObs);
		}
		if (Boolean.TRUE.equals(vitaminA)) {
			Obs vitaminAObs = createConceptValueObs(datetime,
					getImmunizationsOrderedConcept(), patient, facility,
					getVitaminAConcept(), encounter, null);
			encounter.addObs(vitaminAObs);
		}
		if (ttDose != null) {
			Obs ttDoseObs = createNumericValueObs(datetime,
					getTetanusDoseConcept(), patient, facility, ttDose,
					encounter, null);
			encounter.addObs(ttDoseObs);
		}
		if (lochiaColour != null) {
			Obs lochiaColourObs = createNumericValueObs(datetime,
					getLochiaColourConcept(), patient, facility, lochiaColour,
					encounter, null);
			encounter.addObs(lochiaColourObs);
		}
		if (lochiaAmountExcess != null) {
			Obs lochiaAmountObs = createBooleanValueObs(datetime,
					getLochiaExcessConcept(), patient, facility,
					lochiaAmountExcess, encounter, null);
			encounter.addObs(lochiaAmountObs);
		}
		if (temperature != null) {
			Obs temperatureObs = createNumericValueObs(datetime,
					getTemperatureConcept(), patient, facility, temperature,
					encounter, null);
			encounter.addObs(temperatureObs);
		}
		if (fht != null) {
			Obs fhtObs = createNumericValueObs(datetime,
					getFundalHeightConcept(), patient, facility, fht,
					encounter, null);
			encounter.addObs(fhtObs);
		}
		if (comments != null) {
			Obs commentsObs = createTextValueObs(datetime,
					getCommentsConcept(), patient, facility, comments,
					encounter, null);
			encounter.addObs(commentsObs);
		}

		encounterService.saveEncounter(encounter);
	}

	@Transactional
	public void recordChildPNCVisit(User staff, Location facility,
			Date datetime, Patient patient, Integer visitNumber,
			Integer pncLocation, String house, String community,
			Boolean referred, Boolean maleInvolved, Double weight,
			Integer temperature, Boolean bcg, Boolean opv0,
			Integer respiration, Boolean cordConditionNormal,
			Boolean babyConditionGood, String comments) {

		EncounterService encounterService = contextService
				.getEncounterService();

		Encounter encounter = new Encounter();
		encounter.setEncounterType(getChildPNCVisitEncounterType());
		encounter.setEncounterDatetime(datetime);
		encounter.setPatient(patient);
		encounter.setLocation(facility);
		encounter.setProvider(staff);

		if (visitNumber != null) {
			Obs visitNumberObs = createNumericValueObs(datetime,
					getVisitNumberConcept(), patient, facility, visitNumber,
					encounter, null);
			encounter.addObs(visitNumberObs);
		}
		if (pncLocation != null) {
			Obs pncLocationObs = createNumericValueObs(datetime,
					getANCPNCLocationConcept(), patient, facility, pncLocation,
					encounter, null);
			encounter.addObs(pncLocationObs);
		}
		if (house != null) {
			Obs houseObs = createTextValueObs(datetime, getHouseConcept(),
					patient, facility, house, encounter, null);
			encounter.addObs(houseObs);
		}
		if (community != null) {
			Obs communityObs = createTextValueObs(datetime,
					getCommunityConcept(), patient, facility, community,
					encounter, null);
			encounter.addObs(communityObs);
		}
		if (referred != null) {
			Obs referredObs = createBooleanValueObs(datetime,
					getReferredConcept(), patient, facility, referred,
					encounter, null);
			encounter.addObs(referredObs);
		}
		if (maleInvolved != null) {
			Obs maleInvolvedObs = createBooleanValueObs(datetime,
					getMaleInvolvementConcept(), patient, facility,
					maleInvolved, encounter, null);
			encounter.addObs(maleInvolvedObs);
		}
		if (weight != null) {
			Obs weightObs = createNumericValueObs(datetime, getWeightConcept(),
					patient, facility, weight, encounter, null);
			encounter.addObs(weightObs);
		}
		if (temperature != null) {
			Obs temperatureObs = createNumericValueObs(datetime,
					getTemperatureConcept(), patient, facility, temperature,
					encounter, null);
			encounter.addObs(temperatureObs);
		}
		if (Boolean.TRUE.equals(bcg)) {
			Obs bcgObs = createConceptValueObs(datetime,
					getImmunizationsOrderedConcept(), patient, facility,
					getBCGConcept(), encounter, null);
			encounter.addObs(bcgObs);
		}
		if (Boolean.TRUE.equals(opv0)) {
			Integer opvDose = 0;
			Obs opvDoseObs = createNumericValueObs(datetime,
					getOPVDoseConcept(), patient, facility, opvDose, encounter,
					null);
			encounter.addObs(opvDoseObs);
		}
		if (respiration != null) {
			Obs respirationObs = createNumericValueObs(datetime,
					getRespiratoryRateConcept(), patient, facility,
					respiration, encounter, null);
			encounter.addObs(respirationObs);
		}
		if (cordConditionNormal != null) {
			Obs cordConditionObs = createBooleanValueObs(datetime,
					getCordConditionConcept(), patient, facility,
					cordConditionNormal, encounter, null);
			encounter.addObs(cordConditionObs);
		}
		if (babyConditionGood != null) {
			Obs babyConditionObs = createBooleanValueObs(datetime,
					getConditionBabyConcept(), patient, facility,
					babyConditionGood, encounter, null);
			encounter.addObs(babyConditionObs);
		}
		if (comments != null) {
			Obs commentsObs = createTextValueObs(datetime,
					getCommentsConcept(), patient, facility, comments,
					encounter, null);
			encounter.addObs(commentsObs);
		}

		encounterService.saveEncounter(encounter);
	}

	@Transactional
	public void recordTTVisit(User staff, Location facility, Date date,
			Patient patient, Integer ttDose) {

		EncounterService encounterService = contextService
				.getEncounterService();

		Encounter encounter = new Encounter();
		encounter.setEncounterType(getTTVisitEncounterType());
		encounter.setEncounterDatetime(date);
		encounter.setPatient(patient);
		encounter.setLocation(facility);
		encounter.setProvider(staff);

		if (ttDose != null) {
			Obs ttDoseObs = createNumericValueObs(date,
					getTetanusDoseConcept(), patient, facility, ttDose,
					encounter, null);
			encounter.addObs(ttDoseObs);
		}
		encounterService.saveEncounter(encounter);
	}

	@Transactional
	public void recordDeath(User staff, Location facility, Date date,
			Patient patient) {

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

	@Transactional
	public void recordChildCWCVisit(User staff, Location facility, Date date,
			Patient patient, Integer cwcLocation, String house,
			String community, Boolean bcg, Integer opvDose, Integer pentaDose,
			Boolean measles, Boolean yellowFever, Boolean csm, Boolean ipti,
			Boolean vitaminA, Boolean dewormer, Double weight, Integer muac,
			Integer height, Boolean maleInvolved, String comments) {

		EncounterService encounterService = contextService
				.getEncounterService();

		Encounter encounter = new Encounter();
		encounter.setEncounterType(getCWCVisitEncounterType());
		encounter.setEncounterDatetime(date);
		encounter.setPatient(patient);
		encounter.setLocation(facility);
		encounter.setProvider(staff);

		if (cwcLocation != null) {
			Obs cwcLocationObs = createNumericValueObs(date,
					getCWCLocationConcept(), patient, facility, cwcLocation,
					encounter, null);
			encounter.addObs(cwcLocationObs);
		}
		if (house != null) {
			Obs houseObs = createTextValueObs(date, getHouseConcept(), patient,
					facility, house, encounter, null);
			encounter.addObs(houseObs);
		}
		if (community != null) {
			Obs communityObs = createTextValueObs(date, getCommunityConcept(),
					patient, facility, community, encounter, null);
			encounter.addObs(communityObs);
		}
		if (Boolean.TRUE.equals(bcg)) {
			Obs bcgObs = createConceptValueObs(date,
					getImmunizationsOrderedConcept(), patient, facility,
					getBCGConcept(), encounter, null);
			encounter.addObs(bcgObs);
		}
		if (opvDose != null) {
			Obs opvDoseObs = createNumericValueObs(date, getOPVDoseConcept(),
					patient, facility, opvDose, encounter, null);
			encounter.addObs(opvDoseObs);
		}
		if (pentaDose != null) {
			Obs pentaDoseObs = createNumericValueObs(date,
					getPentaDoseConcept(), patient, facility, pentaDose,
					encounter, null);
			encounter.addObs(pentaDoseObs);
		}
		if (Boolean.TRUE.equals(yellowFever)) {
			Obs yellowFeverObs = createConceptValueObs(date,
					getImmunizationsOrderedConcept(), patient, facility,
					getYellowFeverConcept(), encounter, null);
			encounter.addObs(yellowFeverObs);
		}
		if (Boolean.TRUE.equals(csm)) {
			Obs csmObs = createConceptValueObs(date,
					getImmunizationsOrderedConcept(), patient, facility,
					getCSMConcept(), encounter, null);
			encounter.addObs(csmObs);
		}
		if (Boolean.TRUE.equals(measles)) {
			Obs measlesObs = createConceptValueObs(date,
					getImmunizationsOrderedConcept(), patient, facility,
					getMeaslesConcept(), encounter, null);
			encounter.addObs(measlesObs);
		}
		if (Boolean.TRUE.equals(ipti)) {
			Obs iptiObs = createConceptValueObs(date,
					getImmunizationsOrderedConcept(), patient, facility,
					getIPTiConcept(), encounter, null);
			encounter.addObs(iptiObs);
		}
		if (Boolean.TRUE.equals(vitaminA)) {
			Obs vitaminAObs = createConceptValueObs(date,
					getImmunizationsOrderedConcept(), patient, facility,
					getVitaminAConcept(), encounter, null);
			encounter.addObs(vitaminAObs);
		}
		if (dewormer != null) {
			Obs dewormerObs = createBooleanValueObs(date, getDewormerConcept(),
					patient, facility, dewormer, encounter, null);
			encounter.addObs(dewormerObs);
		}
		if (weight != null) {
			Obs weightObs = createNumericValueObs(date, getWeightConcept(),
					patient, facility, weight, encounter, null);
			encounter.addObs(weightObs);
		}
		if (muac != null) {
			Obs muacObs = createNumericValueObs(date, getMUACConcept(),
					patient, facility, muac, encounter, null);
			encounter.addObs(muacObs);
		}
		if (height != null) {
			Obs heightObs = createNumericValueObs(date, getHeightConcept(),
					patient, facility, height, encounter, null);
			encounter.addObs(heightObs);
		}
		if (maleInvolved != null) {
			Obs maleInvolvedObs = createBooleanValueObs(date,
					getMaleInvolvementConcept(), patient, facility,
					maleInvolved, encounter, null);
			encounter.addObs(maleInvolvedObs);
		}
		if (comments != null) {
			Obs commentsObs = createTextValueObs(date, getCommentsConcept(),
					patient, facility, comments, encounter, null);
			encounter.addObs(commentsObs);
		}

		encounterService.saveEncounter(encounter);
	}

	@Transactional
	public void recordGeneralOutpatientVisit(Integer staffId,
			Integer facilityId, Date date, String serialNumber, Gender sex,
			Date dateOfBirth, Boolean insured, Integer diagnosis,
			Integer secondDiagnosis, Boolean rdtGiven, Boolean rdtPositive,
			Boolean actTreated, Boolean newCase, Boolean referred,
			String comments) {

		MotechService motechService = contextService.getMotechService();

		GeneralOutpatientEncounter encounter = new GeneralOutpatientEncounter(
				date, staffId, facilityId, serialNumber, sex, dateOfBirth,
				insured, newCase, diagnosis, secondDiagnosis, referred,
				rdtGiven, rdtPositive, actTreated, comments);

		if (log.isDebugEnabled()) {
			log.debug(encounter.toString());
		}

		motechService.saveGeneralOutpatientEncounter(encounter);
	}

	@Transactional
	public void recordOutpatientVisit(User staff, Location facility, Date date,
			Patient patient, String serialNumber, Integer diagnosis,
			Integer secondDiagnosis, Boolean rdtGiven, Boolean rdtPositive,
			Boolean actTreated, Boolean newCase, Boolean referred,
			String comments) {

		EncounterService encounterService = contextService
				.getEncounterService();

		Encounter encounter = new Encounter();
		encounter.setEncounterType(getOutpatientVisitEncounterType());
		encounter.setEncounterDatetime(date);
		encounter.setPatient(patient);
		encounter.setLocation(facility);
		encounter.setProvider(staff);

		if (serialNumber != null) {
			Obs serialNumberObs = createTextValueObs(date,
					getSerialNumberConcept(), patient, facility, serialNumber,
					encounter, null);
			encounter.addObs(serialNumberObs);
		}
		if (newCase != null) {
			Obs newCaseObs = createBooleanValueObs(date, getNewCaseConcept(),
					patient, facility, newCase, encounter, null);
			encounter.addObs(newCaseObs);
		}
		if (diagnosis != null) {
			Obs diagnosisObs = createNumericValueObs(date,
					getPrimaryDiagnosisConcept(), patient, facility, diagnosis,
					encounter, null);
			encounter.addObs(diagnosisObs);
		}
		if (secondDiagnosis != null) {
			Obs secondDiagnosisObs = createNumericValueObs(date,
					getSecondaryDiagnosisConcept(), patient, facility,
					secondDiagnosis, encounter, null);
			encounter.addObs(secondDiagnosisObs);
		}
		if (referred != null) {
			Obs referredObs = createBooleanValueObs(date, getReferredConcept(),
					patient, facility, referred, encounter, null);
			encounter.addObs(referredObs);
		}
		if (Boolean.TRUE.equals(rdtGiven)) {
			Concept rdtTestValueConcept = null;
			if (Boolean.TRUE.equals(rdtPositive)) {
				rdtTestValueConcept = getPositiveConcept();
			} else {
				rdtTestValueConcept = getNegativeConcept();
			}
			Obs rdtTestObs = createConceptValueObs(date,
					getMalariaRDTConcept(), patient, facility,
					rdtTestValueConcept, encounter, null);
			encounter.addObs(rdtTestObs);
		}
		if (actTreated != null) {
			Obs actTreatedObs = createBooleanValueObs(date,
					getACTTreatmentConcept(), patient, facility, actTreated,
					encounter, null);
			encounter.addObs(actTreatedObs);
		}
		if (comments != null) {
			Obs commentsObs = createTextValueObs(date, getCommentsConcept(),
					patient, facility, comments, encounter, null);
			encounter.addObs(commentsObs);
		}

		encounterService.saveEncounter(encounter);
	}

	@Transactional
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
		String phoneNumber = getPersonPhoneNumber(messageRecipient);
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

		PersonAttributeType phoneAttributeType = getPhoneNumberAttributeType();
		List<Integer> matchingUsers = motechService
				.getUserIdsByPersonAttribute(phoneAttributeType, phoneNumber);
		if (matchingUsers.size() > 0) {
			if (matchingUsers.size() > 1) {
				log.warn("Multiple staff found for phone number: "
						+ phoneNumber);
			}
			// If more than one user matches phone number, first user in list is
			// returned
			Integer userId = matchingUsers.get(0);
			return userService.getUser(userId);
		}
		log.warn("No staff found for phone number: " + phoneNumber);
		return null;
	}

	/* MotechService methods end */

	/* Controller methods start */
	public List<Location> getAllLocations() {
		LocationService locationService = contextService.getLocationService();
		return locationService.getAllLocations();
	}

	public List<User> getAllStaff() {
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
			String preferredName, Date birthDate, Integer communityId,
			String phoneNumber, String nhisNumber, String motechId) {

		MotechService motechService = contextService.getMotechService();

		PersonAttributeType phoneNumberAttrType = getPhoneNumberAttributeType();
		PersonAttributeType nhisAttrType = getNHISNumberAttributeType();
		PatientIdentifierType motechIdType = getMotechPatientIdType();

		return motechService.getPatients(firstName, lastName, preferredName,
				birthDate, communityId, phoneNumber, phoneNumberAttrType,
				nhisNumber, nhisAttrType, motechId, motechIdType);
	}

	public List<Patient> getDuplicatePatients(String firstName,
			String lastName, String preferredName, Date birthDate,
			Integer communityId, String phoneNumber, String nhisNumber,
			String motechId) {

		MotechService motechService = contextService.getMotechService();

		PersonAttributeType phoneNumberAttrType = getPhoneNumberAttributeType();
		PersonAttributeType nhisAttrType = getNHISNumberAttributeType();
		PatientIdentifierType motechIdType = getMotechPatientIdType();

		return motechService.getDuplicatePatients(firstName, lastName,
				preferredName, birthDate, communityId, phoneNumber,
				phoneNumberAttrType, nhisNumber, nhisAttrType, motechId,
				motechIdType);
	}

	public List<Obs> getAllPregnancies() {
		ObsService obsService = contextService.getObsService();
		List<Concept> pregnancyConcept = new ArrayList<Concept>();
		pregnancyConcept.add(getPregnancyConcept());
		return obsService.getObservations(null, null, pregnancyConcept, null,
				null, null, null, null, null, null, null, false);
	}

	public List<ExpectedEncounter> getUpcomingExpectedEncounters(Patient patient) {
		MotechService motechService = contextService.getMotechService();
		Calendar calendar = Calendar.getInstance();
		Date currentDate = calendar.getTime();
		calendar.add(Calendar.DATE, 7);
		Date oneWeekLaterDate = calendar.getTime();
		return motechService.getExpectedEncounter(patient, null, null, null,
				oneWeekLaterDate, null, currentDate, false);
	}

	public List<ExpectedObs> getUpcomingExpectedObs(Patient patient) {
		MotechService motechService = contextService.getMotechService();
		Calendar calendar = Calendar.getInstance();
		Date currentDate = calendar.getTime();
		calendar.add(Calendar.DATE, 7);
		Date oneWeekLaterDate = calendar.getTime();
		return motechService.getExpectedObs(patient, null, null, null,
				oneWeekLaterDate, null, currentDate, false);
	}

	public List<ExpectedEncounter> getDefaultedExpectedEncounters(
			Facility facility, String[] groups) {
		MotechService motechService = contextService.getMotechService();
		Date currentDate = new Date();
		return motechService.getExpectedEncounter(null, facility, groups, null,
				null, currentDate, currentDate, true);
	}

	public List<ExpectedObs> getDefaultedExpectedObs(Facility facility,
			String[] groups) {
		MotechService motechService = contextService.getMotechService();
		Date currentDate = new Date();
		return motechService.getExpectedObs(null, facility, groups, null, null,
				currentDate, currentDate, true);
	}

	private List<ExpectedEncounter> getUpcomingExpectedEncounters(
			Facility facility, String[] groups, Date fromDate, Date toDate) {
		MotechService motechService = contextService.getMotechService();
		return motechService.getExpectedEncounter(null, facility, groups,
				fromDate, toDate, null, fromDate, false);
	}

	private List<ExpectedObs> getUpcomingExpectedObs(Facility facility,
			String[] groups, Date fromDate, Date toDate) {
		MotechService motechService = contextService.getMotechService();
		return motechService.getExpectedObs(null, facility, groups, fromDate,
				toDate, null, fromDate, false);
	}

	private List<ExpectedEncounter> getDefaultedExpectedEncounters(
			Facility facility, String[] groups, Date forDate) {
		MotechService motechService = contextService.getMotechService();
		return motechService.getExpectedEncounter(null, facility, groups, null,
				null, forDate, forDate, true);
	}

	private List<ExpectedObs> getDefaultedExpectedObs(Facility facility,
			String[] groups, Date forDate) {
		MotechService motechService = contextService.getMotechService();
		return motechService.getExpectedObs(null, facility, groups, null, null,
				forDate, forDate, true);
	}

	public List<ExpectedEncounter> getExpectedEncounters(Patient patient) {
		MotechService motechService = contextService.getMotechService();
		Date currentDate = new Date();
		return motechService.getExpectedEncounter(patient, null, null, null,
				null, null, currentDate, true);
	}

	public List<ExpectedObs> getExpectedObs(Patient patient) {
		MotechService motechService = contextService.getMotechService();
		Date currentDate = new Date();
		return motechService.getExpectedObs(patient, null, null, null, null,
				null, currentDate, true);
	}

	public List<ExpectedEncounter> getExpectedEncounters(Patient patient,
			String group) {
		MotechService motechService = contextService.getMotechService();
		return motechService.getExpectedEncounter(patient, null,
				new String[] { group }, null, null, null, null, false);
	}

	public List<ExpectedObs> getExpectedObs(Patient patient, String group) {
		MotechService motechService = contextService.getMotechService();
		return motechService.getExpectedObs(patient, null,
				new String[] { group }, null, null, null, null, false);
	}

	public List<Encounter> getRecentDeliveries(Facility facility) {
		MotechService motechService = contextService.getMotechService();

		EncounterType deliveryEncounterType = getPregnancyDeliveryVisitEncounterType();

		Calendar calendar = Calendar.getInstance();
		Date currentDate = calendar.getTime();
		calendar.add(Calendar.DATE, 2 * -7);
		Date twoWeeksPriorDate = calendar.getTime();

		return motechService.getEncounters(facility, deliveryEncounterType,
				twoWeeksPriorDate, currentDate);
	}

	public Date getCurrentDeliveryDate(Patient patient) {
		EncounterService encounterService = contextService
				.getEncounterService();

		List<EncounterType> deliveryEncounterType = new ArrayList<EncounterType>();
		deliveryEncounterType.add(getPregnancyDeliveryVisitEncounterType());

		List<Encounter> deliveries = encounterService.getEncounters(patient,
				null, null, null, null, deliveryEncounterType, null, false);

		if (!deliveries.isEmpty()) {
			// List is ascending by date, get last match to get most recent
			return deliveries.get(deliveries.size() - 1).getEncounterDatetime();
		}
		return null;
	}

	public List<Obs> getUpcomingPregnanciesDueDate(Facility facility) {
		Calendar calendar = Calendar.getInstance();
		Date currentDate = calendar.getTime();
		calendar.add(Calendar.DATE, 2 * 7);
		Date twoWeeksLaterDate = calendar.getTime();

		return getActivePregnanciesDueDateObs(facility, currentDate,
				twoWeeksLaterDate);
	}

	public List<Obs> getOverduePregnanciesDueDate(Facility facility) {
		Date currentDate = new Date();
		return getActivePregnanciesDueDateObs(facility, null, currentDate);
	}

	private List<Obs> getActivePregnanciesDueDateObs(Facility facility,
			Date fromDueDate, Date toDueDate) {
		MotechService motechService = contextService.getMotechService();

		Concept pregnancyDueDateConcept = getDueDateConcept();
		Concept pregnancyConcept = getPregnancyConcept();
		Concept pregnancyStatusConcept = getPregnancyStatusConcept();

		return motechService.getActivePregnanciesDueDateObs(facility,
				fromDueDate, toDueDate, pregnancyDueDateConcept,
				pregnancyConcept, pregnancyStatusConcept);
	}

	private Integer updatePregnancyDueDateObs(Obs pregnancyObs, Obs dueDateObs,
			Date newDueDate, Encounter encounter) {
		ObsService obsService = contextService.getObsService();
		MotechService motechService = contextService.getMotechService();

		Integer existingDueDateObsId = dueDateObs.getObsId();

		Obs newDueDateObs = createDateValueObs(
				encounter.getEncounterDatetime(), getDueDateConcept(),
				encounter.getPatient(), encounter.getLocation(), newDueDate,
				encounter, null);
		newDueDateObs.setObsGroup(pregnancyObs);
		newDueDateObs = obsService.saveObs(newDueDateObs, null);

		obsService.voidObs(dueDateObs, "Replaced by new EDD value Obs: "
				+ newDueDateObs.getObsId());

		// Update enrollments using duedate Obs to reference new duedate Obs
		List<MessageProgramEnrollment> enrollments = motechService
				.getActiveMessageProgramEnrollments(null, null,
						existingDueDateObsId);
		for (MessageProgramEnrollment enrollment : enrollments) {
			enrollment.setObsId(newDueDateObs.getObsId());
			motechService.saveMessageProgramEnrollment(enrollment);
		}
		return newDueDateObs.getObsId();
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

	public List<ScheduledMessage> getScheduledMessages(
			MessageProgramEnrollment enrollment) {
		MotechService motechService = contextService.getMotechService();
		return motechService.getScheduledMessages(null, null, enrollment, null);
	}

	/* Controller methods end */

	public List<Obs> getObs(Patient patient, String conceptName,
			String valueConceptName, Date minDate) {
		ObsService obsService = contextService.getObsService();
		ConceptService conceptService = contextService.getConceptService();

		Concept concept = conceptService.getConcept(conceptName);
		Concept value = conceptService.getConcept(valueConceptName);

		List<Concept> questions = new ArrayList<Concept>();
		questions.add(concept);

		List<Concept> answers = null;
		if (value != null) {
			answers = new ArrayList<Concept>();
			answers.add(value);
		}

		List<Person> whom = new ArrayList<Person>();
		whom.add(patient);

		return obsService.getObservations(whom, null, questions, answers, null,
				null, null, null, null, minDate, null, false);
	}

	public ExpectedObs createExpectedObs(Patient patient, String conceptName,
			String valueConceptName, Integer value, Date minDate, Date dueDate,
			Date lateDate, Date maxDate, String name, String group) {
		ConceptService conceptService = contextService.getConceptService();

		Concept concept = conceptService.getConcept(conceptName);
		Concept valueConcept = conceptService.getConcept(valueConceptName);

		ExpectedObs expectedObs = new ExpectedObs();
		expectedObs.setPatient(patient);
		expectedObs.setConcept(concept);
		expectedObs.setValueCoded(valueConcept);
		if (value != null) {
			expectedObs.setValueNumeric(new Double(value));
		}
		expectedObs.setMinObsDatetime(minDate);
		expectedObs.setDueObsDatetime(dueDate);
		expectedObs.setLateObsDatetime(lateDate);
		expectedObs.setMaxObsDatetime(maxDate);
		expectedObs.setName(name);
		expectedObs.setGroup(group);

		return saveExpectedObs(expectedObs);
	}

	public ExpectedObs saveExpectedObs(ExpectedObs expectedObs) {
		if (log.isDebugEnabled()) {
			log.debug("Saving schedule update: " + expectedObs.toString());
		}
		if (expectedObs.getDueObsDatetime() != null
				&& expectedObs.getLateObsDatetime() != null) {

			MotechService motechService = contextService.getMotechService();
			return motechService.saveExpectedObs(expectedObs);
		} else {
			log
					.error("Attempt to store ExpectedObs with null due or late date");
			return null;
		}
	}

	public List<Encounter> getEncounters(Patient patient,
			String encounterTypeName, Date minDate) {
		EncounterService encounterService = contextService
				.getEncounterService();

		EncounterType encounterType = encounterService
				.getEncounterType(encounterTypeName);

		List<EncounterType> encounterTypes = new ArrayList<EncounterType>();
		encounterTypes.add(encounterType);

		return encounterService.getEncounters(patient, null, minDate, null,
				null, encounterTypes, null, false);
	}

	public ExpectedEncounter createExpectedEncounter(Patient patient,
			String encounterTypeName, Date minDate, Date dueDate,
			Date lateDate, Date maxDate, String name, String group) {
		EncounterService encounterService = contextService
				.getEncounterService();

		EncounterType encounterType = encounterService
				.getEncounterType(encounterTypeName);

		ExpectedEncounter expectedEncounter = new ExpectedEncounter();
		expectedEncounter.setPatient(patient);
		expectedEncounter.setEncounterType(encounterType);
		expectedEncounter.setMinEncounterDatetime(minDate);
		expectedEncounter.setDueEncounterDatetime(dueDate);
		expectedEncounter.setLateEncounterDatetime(lateDate);
		expectedEncounter.setMaxEncounterDatetime(maxDate);
		expectedEncounter.setName(name);
		expectedEncounter.setGroup(group);

		return saveExpectedEncounter(expectedEncounter);
	}

	public ExpectedEncounter saveExpectedEncounter(
			ExpectedEncounter expectedEncounter) {
		if (log.isDebugEnabled()) {
			log
					.debug("Saving schedule update: "
							+ expectedEncounter.toString());
		}
		if (expectedEncounter.getDueEncounterDatetime() != null
				&& expectedEncounter.getLateEncounterDatetime() != null) {

			MotechService motechService = contextService.getMotechService();
			return motechService.saveExpectedEncounter(expectedEncounter);
		} else {
			log
					.error("Attempt to store ExpectedEncounter with null due or late date");
			return null;
		}
	}

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

	public Obs getActivePregnancyDueDateObs(Integer patientId, Obs pregnancy) {
		PersonService personService = contextService.getPersonService();
		if (pregnancy != null) {
			Integer pregnancyObsId = pregnancy.getObsId();
			List<Obs> dueDateObsList = getMatchingObs(personService
					.getPerson(patientId), getDueDateConcept(), null,
					pregnancyObsId, null, null);
			if (dueDateObsList.size() > 0) {
				return dueDateObsList.get(0);
			}
		}
		return null;
	}

	public Date getActivePregnancyDueDate(Integer patientId) {
		Obs pregnancy = getActivePregnancy(patientId);
		Obs dueDateObs = getActivePregnancyDueDateObs(patientId, pregnancy);
		if (dueDateObs != null) {
			return dueDateObs.getValueDatetime();
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
					+ ", concept: " + concept.getConceptId() + ", value: "
					+ (value != null ? value.getConceptId() : "null"));
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
					+ ", concept: " + concept.getConceptId() + ", value: "
					+ (value != null ? value.getConceptId() : "null"));
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
					+ ", concept: " + concept.getConceptId());
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

	/* MessageSchedulerImpl methods start */
	public void scheduleMessage(String messageKey,
			MessageProgramEnrollment enrollment, Date messageDate,
			boolean userPreferenceBased) {
		// Return existing message definition
		MessageDefinition messageDefinition = this
				.getMessageDefinition(messageKey);

		// TODO: Assumes recipient is person in enrollment
		Integer messageRecipientId = enrollment.getPersonId();

		// Expecting message date to already be preference adjusted

		// Cancel any unsent messages for the same enrollment and not matching
		// the message to schedule
		this.removeUnsentMessages(messageRecipientId, enrollment,
				messageDefinition, messageDate);

		// Create new scheduled message (with pending attempt) for enrollment
		// if none matching already exist
		this.createScheduledMessage(messageRecipientId, messageDefinition,
				enrollment, messageDate);
	}

	public ScheduledMessage scheduleCareMessage(String messageKey,
			MessageProgramEnrollment enrollment, Date messageDate,
			boolean userPreferenceBased, String care) {
		// Return existing message definition
		MessageDefinition messageDefinition = this
				.getMessageDefinition(messageKey);

		// TODO: Assumes recipient is person in enrollment
		Integer messageRecipientId = enrollment.getPersonId();

		// Create new scheduled message (with pending attempt) for enrollment
		// Does not check if one already exists
		return this.createCareScheduledMessage(messageRecipientId,
				messageDefinition, enrollment, messageDate, care,
				userPreferenceBased);
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

	public void removeUnsentMessages(List<ScheduledMessage> scheduledMessages) {
		MotechService motechService = contextService.getMotechService();

		for (ScheduledMessage scheduledMessage : scheduledMessages) {
			for (Message unsentMessage : scheduledMessage.getMessageAttempts()) {
				if (MessageStatus.SHOULD_ATTEMPT == unsentMessage
						.getAttemptStatus()) {

					unsentMessage.setAttemptStatus(MessageStatus.CANCELLED);
					motechService.saveMessage(unsentMessage);

					log
							.debug("Message cancelled: Id: "
									+ unsentMessage.getId());
				}
			}
		}
	}

	public void addMessageAttempt(ScheduledMessage scheduledMessage,
			Date attemptDate, Date maxAttemptDate, boolean userPreferenceBased) {

		MotechService motechService = contextService.getMotechService();
		PersonService personService = contextService.getPersonService();

		MessageDefinition messageDefinition = scheduledMessage.getMessage();
		Person recipient = personService.getPerson(scheduledMessage
				.getRecipientId());

		Date adjustedMessageDate = adjustCareMessageDate(recipient,
				attemptDate, userPreferenceBased);
		// Prevent scheduling reminders too far in future
		// Only schedule one reminder ahead
		if (!adjustedMessageDate.after(maxAttemptDate)) {
			Message message = messageDefinition.createMessage(scheduledMessage);
			message.setAttemptDate(attemptDate);
			scheduledMessage.getMessageAttempts().add(message);

			if (log.isDebugEnabled()) {
				log.debug("Added ScheduledMessage Attempt: recipient: "
						+ scheduledMessage.getRecipientId() + ", message key: "
						+ messageDefinition.getMessageKey() + ", date: "
						+ adjustedMessageDate);
			}

			motechService.saveScheduledMessage(scheduledMessage);
		}
	}

	public void verifyMessageAttemptDate(ScheduledMessage scheduledMessage,
			boolean userPreferenceBased) {

		MotechService motechService = contextService.getMotechService();
		PersonService personService = contextService.getPersonService();
		Person recipient = personService.getPerson(scheduledMessage
				.getRecipientId());

		List<Message> messages = scheduledMessage.getMessageAttempts();
		if (!messages.isEmpty()) {
			Message recentMessage = messages.get(0);
			if (recentMessage.getAttemptStatus() == MessageStatus.SHOULD_ATTEMPT) {
				Date attemptDate = recentMessage.getAttemptDate();
				// Check if current message date is valid for user
				// preferences or blackout incase these have changed
				if (userPreferenceBased) {
					attemptDate = determinePreferredMessageDate(recipient,
							attemptDate);
				} else {
					attemptDate = adjustForBlackout(attemptDate);
				}
				if (!attemptDate.equals(recentMessage.getAttemptDate())) {
					// Recompute from original scheduled message date
					// Allows possibly adjusting to an earlier week or day
					Date adjustedMessageDate = adjustCareMessageDate(recipient,
							scheduledMessage.getScheduledFor(),
							userPreferenceBased);

					if (log.isDebugEnabled()) {
						log.debug("Updating message id="
								+ recentMessage.getId() + " date from="
								+ recentMessage.getAttemptDate() + " to="
								+ adjustedMessageDate);
					}

					recentMessage.setAttemptDate(adjustedMessageDate);
					scheduledMessage.getMessageAttempts().set(0, recentMessage);
					motechService.saveScheduledMessage(scheduledMessage);
				}
			}
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

	public Date determineUserPreferredMessageDate(Integer recipientId,
			Date messageDate) {
		PersonService personService = contextService.getPersonService();
		Person recipient = personService.getPerson(recipientId);

		return determinePreferredMessageDate(recipient, messageDate);
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

	private ScheduledMessage createCareScheduledMessage(Integer recipientId,
			MessageDefinition messageDefinition,
			MessageProgramEnrollment enrollment, Date messageDate, String care,
			boolean userPreferenceBased) {

		MotechService motechService = contextService.getMotechService();
		PersonService personService = contextService.getPersonService();

		ScheduledMessage scheduledMessage = new ScheduledMessage();
		scheduledMessage.setScheduledFor(messageDate);
		scheduledMessage.setRecipientId(recipientId);
		scheduledMessage.setMessage(messageDefinition);
		scheduledMessage.setEnrollment(enrollment);
		// Set care field on scheduled message (not set on informational
		// messages)
		scheduledMessage.setCare(care);

		Person recipient = personService.getPerson(recipientId);
		Date adjustedMessageDate = adjustCareMessageDate(recipient,
				messageDate, userPreferenceBased);

		Message message = messageDefinition.createMessage(scheduledMessage);
		message.setAttemptDate(adjustedMessageDate);
		scheduledMessage.getMessageAttempts().add(message);

		if (log.isDebugEnabled()) {
			log.debug("Creating ScheduledMessage: recipient: " + recipientId
					+ ", enrollment: " + enrollment.getId() + ", message key: "
					+ messageDefinition.getMessageKey() + ", date: "
					+ adjustedMessageDate);
		}

		return motechService.saveScheduledMessage(scheduledMessage);
	}

	Date adjustCareMessageDate(Person person, Date messageDate,
			boolean userPreferenceBased) {
		Date adjustedDate = verifyFutureDate(messageDate);
		if (userPreferenceBased) {
			adjustedDate = determinePreferredMessageDate(person, adjustedDate);
		} else {
			adjustedDate = adjustForBlackout(adjustedDate);
		}
		return adjustedDate;
	}

	Date verifyFutureDate(Date messageDate) {
		Calendar calendar = Calendar.getInstance();
		if (calendar.getTime().after(messageDate)) {
			// If date in past, return date 10 minutes in future
			calendar.add(Calendar.MINUTE, 10);
			return calendar.getTime();
		}
		return messageDate;
	}

	/* MessageSchedulerImpl methods end */

	/* Activator methods start */
	public void addInitialData() {

		UserService userService = contextService.getUserService();
		User admin = userService.getUser(1);

		log.info("Verifying Person Attributes Exist");
		createPersonAttributeType(
				MotechConstants.PERSON_ATTRIBUTE_PHONE_NUMBER,
				"A person's phone number.", String.class.getName(), admin);
		createPersonAttributeType(MotechConstants.PERSON_ATTRIBUTE_NHIS_NUMBER,
				"A person's NHIS number.", String.class.getName(), admin);
		createPersonAttributeType(
				MotechConstants.PERSON_ATTRIBUTE_NHIS_EXP_DATE,
				"A person's NHIS expiration date.", AttributableDate.class
						.getName(), admin);
		createPersonAttributeType(MotechConstants.PERSON_ATTRIBUTE_LANGUAGE,
				"A person's language preference for messages.", String.class
						.getName(), admin);
		createPersonAttributeType(
				MotechConstants.PERSON_ATTRIBUTE_PHONE_TYPE,
				"A person's phone ownership type (PERSONAL, HOUSEHOLD, or PUBLIC).",
				String.class.getName(), admin);
		createPersonAttributeType(MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE,
				"A person's media type preference for messages.", String.class
						.getName(), admin);
		createPersonAttributeType(
				MotechConstants.PERSON_ATTRIBUTE_DELIVERY_DAY,
				"A person's preferred delivery day (SUNDAY to SATURDAY).",
				String.class.getName(), admin);
		createPersonAttributeType(
				MotechConstants.PERSON_ATTRIBUTE_DELIVERY_TIME,
				"A person's preferred delivery time (HH:mm).", String.class
						.getName(), admin);
		createPersonAttributeType(MotechConstants.PERSON_ATTRIBUTE_INSURED,
				"Is person insured? (TRUE OR FALSE)", String.class.getName(),
				admin);
		createPersonAttributeType(MotechConstants.PERSON_ATTRIBUTE_HOW_LEARNED,
				"How person found out about services.", String.class.getName(),
				admin);
		createPersonAttributeType(
				MotechConstants.PERSON_ATTRIBUTE_INTEREST_REASON,
				"Reason person is interested in services.", String.class
						.getName(), admin);

		log.info("Verifying Patient Identifier Exist");
		PatientIdentifierType motechIDType = createPatientIdentifierType(
				MotechConstants.PATIENT_IDENTIFIER_MOTECH_ID,
				"Patient Id for MoTeCH system.",
				MotechIdVerhoeffValidator.class.getName(), admin);

		log.info("Verifying Patient Identifier Generator Exists");
		createSequentialPatientIdentifierGenerator(
				MotechConstants.IDGEN_SEQ_ID_GEN_MOTECH_ID, motechIDType,
				MotechIdVerhoeffValidator.ALLOWED_CHARS,
				MotechConstants.IDGEN_SEQ_ID_GEN_FIRST_MOTECH_ID_BASE,
				MotechIdVerhoeffValidator.VERHOEFF_ID_LENGTH);

		log.info("Verifying Locations Exist");
		createLocation(MotechConstants.LOCATION_GHANA,
				"Republic of Ghana, Country, Root in hierarchy", null, admin);

		log.info("Verifying Encounter Types Exist");
		createEncounterType(MotechConstants.ENCOUNTER_TYPE_ANCVISIT,
				"Ghana Antenatal Care (ANC) Visit", admin);
		createEncounterType(MotechConstants.ENCOUNTER_TYPE_PREGREGVISIT,
				"Ghana Pregnancy Registration Visit", admin);
		createEncounterType(MotechConstants.ENCOUNTER_TYPE_PREGTERMVISIT,
				"Ghana Pregnancy Termination Visit", admin);
		createEncounterType(MotechConstants.ENCOUNTER_TYPE_PREGDELVISIT,
				"Ghana Pregnancy Delivery Visit", admin);
		createEncounterType(MotechConstants.ENCOUNTER_TYPE_OUTPATIENTVISIT,
				"Ghana Outpatient Visit", admin);
		createEncounterType(MotechConstants.ENCOUNTER_TYPE_TTVISIT,
				"Ghana Tetanus outside Pregnancy Visit", admin);
		createEncounterType(MotechConstants.ENCOUNTER_TYPE_CWCVISIT,
				"Ghana Child Immunization Visit", admin);
		createEncounterType(MotechConstants.ENCOUNTER_TYPE_PNCMOTHERVISIT,
				"Ghana Mother Postnatal Care (PNC) Visit", admin);
		createEncounterType(MotechConstants.ENCOUNTER_TYPE_PNCCHILDVISIT,
				"Ghana Child Postnatal Care (PNC) Visit", admin);
		createEncounterType(MotechConstants.ENCOUNTER_TYPE_PREGDELNOTIFYVISIT,
				"Ghana Pregnancy Delivery Notification", admin);
		createEncounterType(MotechConstants.ENCOUNTER_TYPE_ANCREGVISIT,
				"Ghana Antental Care (ANC) Registration", admin);
		createEncounterType(MotechConstants.ENCOUNTER_TYPE_CWCREGVISIT,
				"Ghana Child Immunization Registration", admin);
		createEncounterType(MotechConstants.ENCOUNTER_TYPE_BIRTHVISIT,
				"Ghana Child Birth Visit", admin);

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
		createConcept(
				MotechConstants.CONCEPT_HIV_TEST_RESULT,
				"Question: \"What is the patient's text coded HIV test result?\"",
				MotechConstants.CONCEPT_CLASS_QUESTION,
				MotechConstants.CONCEPT_DATATYPE_TEXT, admin);
		createConcept(MotechConstants.CONCEPT_TERMINATION_TYPE,
				"Numeric coded pregnancy termination reason",
				MotechConstants.CONCEPT_CLASS_FINDING,
				MotechConstants.CONCEPT_DATATYPE_NUMERIC, admin);
		createConcept(MotechConstants.CONCEPT_TERMINATION_COMPLICATION,
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
		createConcept(MotechConstants.CONCEPT_SERIAL_NUMBER,
				"Patient register serial number",
				MotechConstants.CONCEPT_CLASS_MISC,
				MotechConstants.CONCEPT_DATATYPE_TEXT, admin);
		createConcept(MotechConstants.CONCEPT_NEW_CASE,
				"Question: \"Is this a new case?\"",
				MotechConstants.CONCEPT_CLASS_QUESTION,
				MotechConstants.CONCEPT_DATATYPE_BOOLEAN, admin);
		createConcept(MotechConstants.CONCEPT_REFERRED,
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
		createConcept(MotechConstants.CONCEPT_DELIVERY_MODE,
				"Numeric coded mode of delivery",
				MotechConstants.CONCEPT_CLASS_FINDING,
				MotechConstants.CONCEPT_DATATYPE_NUMERIC, admin);
		createConcept(MotechConstants.CONCEPT_DELIVERY_LOCATION,
				"Numeric coded place of delivery",
				MotechConstants.CONCEPT_CLASS_MISC,
				MotechConstants.CONCEPT_DATATYPE_NUMERIC, admin);
		createConcept(MotechConstants.CONCEPT_DELIVERED_BY,
				"Numeric coded who performed delivery",
				MotechConstants.CONCEPT_CLASS_MISC,
				MotechConstants.CONCEPT_DATATYPE_NUMERIC, admin);
		createConcept(MotechConstants.CONCEPT_DELIVERY_OUTCOME,
				"Numeric coded outcome of delivery",
				MotechConstants.CONCEPT_CLASS_FINDING,
				MotechConstants.CONCEPT_DATATYPE_NUMERIC, admin);
		createConcept(MotechConstants.CONCEPT_BIRTH_OUTCOME,
				"Text coded birth outcome",
				MotechConstants.CONCEPT_CLASS_FINDING,
				MotechConstants.CONCEPT_DATATYPE_TEXT, admin);
		createConcept(MotechConstants.CONCEPT_MALARIA_RAPID_TEST,
				"Rapid diagnostic test for malaria",
				MotechConstants.CONCEPT_CLASS_TEST,
				MotechConstants.CONCEPT_DATATYPE_CODED, admin);
		createConcept(
				MotechConstants.CONCEPT_VDRL_TREATMENT,
				"Question on encounter form: \"Was the patient given treatment for syphilis?\"",
				MotechConstants.CONCEPT_CLASS_QUESTION,
				MotechConstants.CONCEPT_DATATYPE_BOOLEAN, admin);
		createConcept(MotechConstants.CONCEPT_URINE_PROTEIN_TEST,
				"Test for protein in urine",
				MotechConstants.CONCEPT_CLASS_TEST,
				MotechConstants.CONCEPT_DATATYPE_CODED, admin);
		createConcept(MotechConstants.CONCEPT_URINE_GLUCOSE_TEST,
				"Test for glucose in urine",
				MotechConstants.CONCEPT_CLASS_TEST,
				MotechConstants.CONCEPT_DATATYPE_CODED, admin);
		createConcept(MotechConstants.CONCEPT_FETAL_HEART_RATE,
				"Fetal heart rate (in bpm)",
				MotechConstants.CONCEPT_CLASS_TEST,
				MotechConstants.CONCEPT_DATATYPE_NUMERIC, admin);
		createConcept(MotechConstants.CONCEPT_FUNDAL_HEIGHT,
				"Measurement of uterus (in cm)",
				MotechConstants.CONCEPT_CLASS_TEST,
				MotechConstants.CONCEPT_DATATYPE_NUMERIC, admin);
		createConcept(MotechConstants.CONCEPT_VVF_REPAIR,
				"Numeric coded value for Vesico Vaginal Fistula (VVF) repair",
				MotechConstants.CONCEPT_CLASS_FINDING,
				MotechConstants.CONCEPT_DATATYPE_NUMERIC, admin);
		createConcept(
				MotechConstants.CONCEPT_DEWORMER,
				"Question on encounter form: \"Was dewormer given to patient?\"",
				MotechConstants.CONCEPT_CLASS_QUESTION,
				MotechConstants.CONCEPT_DATATYPE_BOOLEAN, admin);
		createConcept(MotechConstants.CONCEPT_PMTCT,
				"Preventing Mother-to-Child Transmission (PMTCT) of HIV",
				MotechConstants.CONCEPT_CLASS_QUESTION,
				MotechConstants.CONCEPT_DATATYPE_BOOLEAN, admin);
		createConcept(
				MotechConstants.CONCEPT_PMTCT_TREATMENT,
				"Question on encounter form: \"Was PMTCT HIV treatment given to patient?\"",
				MotechConstants.CONCEPT_CLASS_QUESTION,
				MotechConstants.CONCEPT_DATATYPE_BOOLEAN, admin);
		createConcept(
				MotechConstants.CONCEPT_ACT_TREATMENT,
				"Question on encounter form: \"Was Artemisinin-based Combination Therapy (ACT) for Malaria given to patient?\"",
				MotechConstants.CONCEPT_CLASS_QUESTION,
				MotechConstants.CONCEPT_DATATYPE_BOOLEAN, admin);
		createConcept(
				MotechConstants.CONCEPT_HIV_PRE_TEST_COUNSELING,
				"Question on encounter form: \"Was counseling done before HIV test?\"",
				MotechConstants.CONCEPT_CLASS_QUESTION,
				MotechConstants.CONCEPT_DATATYPE_BOOLEAN, admin);
		createConcept(
				MotechConstants.CONCEPT_HIV_POST_TEST_COUNSELING,
				"Question on encounter form: \"Was counseling done after HIV test?\"",
				MotechConstants.CONCEPT_CLASS_QUESTION,
				MotechConstants.CONCEPT_DATATYPE_BOOLEAN, admin);
		createConcept(MotechConstants.CONCEPT_DELIVERY_COMPLICATION,
				"Numeric coded value for delivery or post-partum complication",
				MotechConstants.CONCEPT_CLASS_FINDING,
				MotechConstants.CONCEPT_DATATYPE_NUMERIC, admin);
		createConcept(
				MotechConstants.CONCEPT_POST_ABORTION_FP_COUNSELING,
				"Question on encounter form: \"Was family planning counseling done after abortion?\"",
				MotechConstants.CONCEPT_CLASS_QUESTION,
				MotechConstants.CONCEPT_DATATYPE_BOOLEAN, admin);
		createConcept(
				MotechConstants.CONCEPT_POST_ABORTION_FP_ACCEPTED,
				"Question on encounter form: \"Did the patient accept family planning after abortion?\"",
				MotechConstants.CONCEPT_CLASS_QUESTION,
				MotechConstants.CONCEPT_DATATYPE_BOOLEAN, admin);
		createConcept(
				MotechConstants.CONCEPT_IPT_REACTION,
				"Intermittent Preventive Treatment (IPT) with Sulphadoxine-Pyrimethamine (SP)",
				MotechConstants.CONCEPT_CLASS_TEST,
				MotechConstants.CONCEPT_DATATYPE_CODED, admin);
		createConcept(MotechConstants.CONCEPT_LOCHIA_COLOUR,
				"Numeric coded colour of post-partum vaginal discharge",
				MotechConstants.CONCEPT_CLASS_FINDING,
				MotechConstants.CONCEPT_DATATYPE_NUMERIC, admin);
		createConcept(MotechConstants.CONCEPT_LOCHIA_EXCESS_AMOUNT,
				"Is amount of post-partum vaginal discharge in excess?",
				MotechConstants.CONCEPT_CLASS_FINDING,
				MotechConstants.CONCEPT_DATATYPE_BOOLEAN, admin);
		createConcept(MotechConstants.CONCEPT_MIDDLE_UPPER_ARM_CIRCUMFERENCE,
				"Circumference of Middle Upper Arm (MUAC) in cm",
				MotechConstants.CONCEPT_CLASS_FINDING,
				MotechConstants.CONCEPT_DATATYPE_NUMERIC, admin);
		createConcept(
				MotechConstants.CONCEPT_MATERNAL_DEATH,
				"Question on encounter form: \"Death of patient during delivery or abortion procedure?\"",
				MotechConstants.CONCEPT_CLASS_QUESTION,
				MotechConstants.CONCEPT_DATATYPE_BOOLEAN, admin);
		createConcept(MotechConstants.CONCEPT_TERMINATION_PROCEDURE,
				"Numeric coded procedure used to terminate pregnancy",
				MotechConstants.CONCEPT_CLASS_PROCEDURE,
				MotechConstants.CONCEPT_DATATYPE_NUMERIC, admin);
		createConcept(
				MotechConstants.CONCEPT_CORD_CONDITION,
				"Question on encounter form: \"Is the condition of the umbilical cord normal?\"",
				MotechConstants.CONCEPT_CLASS_QUESTION,
				MotechConstants.CONCEPT_DATATYPE_BOOLEAN, admin);
		createConcept(
				MotechConstants.CONCEPT_CONDITION_OF_BABY,
				"Question on encounter form: \"Is the condition of the baby good?\"",
				MotechConstants.CONCEPT_CLASS_QUESTION,
				MotechConstants.CONCEPT_DATATYPE_BOOLEAN, admin);
		createConcept(
				MotechConstants.CONCEPT_NEXT_ANC_DATE,
				"Question on encounter form: \"What is the date of the next antenatal care visit for the patient?\"",
				MotechConstants.CONCEPT_CLASS_QUESTION,
				MotechConstants.CONCEPT_DATATYPE_DATETIME, admin);
		createConcept(
				MotechConstants.CONCEPT_MALE_INVOLVEMENT,
				"Question on encounter form: \"Was the male household member involved in patient's care?\"",
				MotechConstants.CONCEPT_CLASS_QUESTION,
				MotechConstants.CONCEPT_DATATYPE_BOOLEAN, admin);
		createConcept(MotechConstants.CONCEPT_COMMUNITY,
				"Community details on the location of care",
				MotechConstants.CONCEPT_CLASS_MISC,
				MotechConstants.CONCEPT_DATATYPE_TEXT, admin);
		createConcept(MotechConstants.CONCEPT_HOUSE,
				"House details on the facility of care",
				MotechConstants.CONCEPT_CLASS_MISC,
				MotechConstants.CONCEPT_DATATYPE_TEXT, admin);
		createConcept(MotechConstants.CONCEPT_ANC_PNC_LOCATION,
				"Numeric coded location of ANC or PNC care",
				MotechConstants.CONCEPT_CLASS_MISC,
				MotechConstants.CONCEPT_DATATYPE_NUMERIC, admin);
		createConcept(MotechConstants.CONCEPT_CWC_LOCATION,
				"Numeric coded location of CWC care",
				MotechConstants.CONCEPT_CLASS_MISC,
				MotechConstants.CONCEPT_DATATYPE_NUMERIC, admin);
		createConcept(MotechConstants.CONCEPT_COMMENTS, "Comments",
				MotechConstants.CONCEPT_CLASS_MISC,
				MotechConstants.CONCEPT_DATATYPE_TEXT, admin);
		createConcept(MotechConstants.CONCEPT_ANC_REG_NUMBER,
				"Ghana ANC Registration Number",
				MotechConstants.CONCEPT_CLASS_MISC,
				MotechConstants.CONCEPT_DATATYPE_TEXT, admin);
		createConcept(MotechConstants.CONCEPT_CWC_REG_NUMBER,
				"Ghana CWC Registration Number",
				MotechConstants.CONCEPT_CLASS_MISC,
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
		addConceptAnswers(MotechConstants.CONCEPT_MALARIA_RAPID_TEST,
				new String[] { MotechConstants.CONCEPT_POSITIVE,
						MotechConstants.CONCEPT_NEGATIVE }, admin);
		addConceptAnswers(MotechConstants.CONCEPT_URINE_PROTEIN_TEST,
				new String[] { MotechConstants.CONCEPT_POSITIVE,
						MotechConstants.CONCEPT_NEGATIVE }, admin);
		addConceptAnswers(MotechConstants.CONCEPT_URINE_GLUCOSE_TEST,
				new String[] { MotechConstants.CONCEPT_POSITIVE,
						MotechConstants.CONCEPT_NEGATIVE }, admin);
		addConceptAnswers(MotechConstants.CONCEPT_IPT_REACTION, new String[] {
				MotechConstants.CONCEPT_REACTIVE,
				MotechConstants.CONCEPT_NON_REACTIVE }, admin);

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

		calendar = Calendar.getInstance();
		Map<String, String> dailyStaffProps = new HashMap<String, String>();
		dailyStaffProps.put(MotechConstants.TASK_PROPERTY_SEND_UPCOMING,
				Boolean.TRUE.toString());
		String[] dailyGroups = { "PNC(mother)", "PNC(baby)" };
		String dailyGroupsProperty = StringUtils.join(dailyGroups,
				MotechConstants.TASK_PROPERTY_CARE_GROUPS_DELIMITER);
		dailyStaffProps.put(MotechConstants.TASK_PROPERTY_CARE_GROUPS,
				dailyGroupsProperty);
		dailyStaffProps.put(MotechConstants.TASK_PROPERTY_AVOID_BLACKOUT,
				Boolean.TRUE.toString());
		createTask(MotechConstants.TASK_DAILY_NURSE_CARE_MESSAGING,
				"Task to send out staff SMS care messages for next day",
				calendar.getTime(), new Long(3600), Boolean.FALSE,
				StaffCareMessagingTask.class.getName(), admin, dailyStaffProps);

		calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Map<String, String> weeklyStaffProps = new HashMap<String, String>();
		String[] weeklyGroups = { "ANC", "TT", "IPT", "BCG", "OPV", "Penta",
				"YellowFever", "Measles", "IPTI", "VitaA" };
		String weeklyGroupsProperty = StringUtils.join(weeklyGroups,
				MotechConstants.TASK_PROPERTY_CARE_GROUPS_DELIMITER);
		weeklyStaffProps.put(MotechConstants.TASK_PROPERTY_CARE_GROUPS,
				weeklyGroupsProperty);
		weeklyStaffProps.put(MotechConstants.TASK_PROPERTY_DELIVERY_TIME,
				"08:00");
		createTask(MotechConstants.TASK_WEEKLY_NURSE_CARE_MESSAGING,
				"Task to send out staff SMS care messages for week", calendar
						.getTime(), new Long(604800), Boolean.FALSE,
				StaffCareMessagingTask.class.getName(), admin, weeklyStaffProps);
	}

	private void createPersonAttributeType(String name, String description,
			String format, User creator) {
		PersonService personService = contextService.getPersonService();
		PersonAttributeType attrType = personService
				.getPersonAttributeTypeByName(name);
		if (attrType == null) {
			log.info(name + " PersonAttributeType Does Not Exist - Creating");
			attrType = new PersonAttributeType();
		}
		attrType.setName(name);
		attrType.setDescription(description);
		attrType.setFormat(format);
		attrType.setCreator(creator);
		personService.savePersonAttributeType(attrType);
	}

	private String generateMotechId() {
		String motechId = null;
		PatientIdentifierType motechIdType = getMotechPatientIdType();
		try {
			IdentifierSourceService idSourceService = contextService
					.getIdentifierSourceService();

			SequentialIdentifierGenerator idGenerator = getSeqIdGenerator(
					MotechConstants.IDGEN_SEQ_ID_GEN_MOTECH_ID, motechIdType);
			motechId = idSourceService.generateIdentifier(idGenerator,
					MotechConstants.IDGEN_SEQ_ID_GEN_MOTECH_ID_GEN_COMMENT);

		} catch (Exception e) {
			log.error("Error generating Motech Id using Idgen module", e);
		}
		return motechId;
	}

	private void excludeIdForGenerator(User staff, String motechId) {
		PatientIdentifierType motechIdType = getMotechPatientIdType();
		try {
			IdentifierSourceService idSourceService = contextService
					.getIdentifierSourceService();

			SequentialIdentifierGenerator idGenerator = getSeqIdGenerator(
					MotechConstants.IDGEN_SEQ_ID_GEN_MOTECH_ID, motechIdType);

			// Persisted only if match for source and id doesn't already exist
			LogEntry newLog = new LogEntry();
			newLog.setSource(idGenerator);
			newLog.setIdentifier(motechId);
			newLog.setDateGenerated(new Date());
			newLog.setGeneratedBy(staff);
			newLog
					.setComment(MotechConstants.IDGEN_SEQ_ID_GEN_MOTECH_ID_MANUAL_COMMENT);
			idSourceService.saveLogEntry(newLog);

		} catch (Exception e) {
			log.error("Error verifying Motech Id in Log of Idgen module", e);
		}
	}

	private SequentialIdentifierGenerator getSeqIdGenerator(String name,
			PatientIdentifierType identifierType) {

		SequentialIdentifierGenerator idGenerator = null;
		try {
			IdentifierSourceService idSourceService = contextService
					.getIdentifierSourceService();

			List<IdentifierSource> idSources = idSourceService
					.getAllIdentifierSources(false);

			for (IdentifierSource idSource : idSources) {
				if (idSource instanceof SequentialIdentifierGenerator
						&& idSource.getName().equals(name)
						&& idSource.getIdentifierType().equals(identifierType)) {
					idGenerator = (SequentialIdentifierGenerator) idSource;
					break;
				}
			}
		} catch (Exception e) {
			log.error("Error retrieving Patient Id generator in Idgen module",
					e);
		}
		return idGenerator;
	}

	private void createSequentialPatientIdentifierGenerator(String name,
			PatientIdentifierType identifierType, String baseCharacterSet,
			String firstIdentifierBase, Integer length) {
		try {
			IdentifierSourceService idSourceService = contextService
					.getIdentifierSourceService();

			SequentialIdentifierGenerator idGenerator = getSeqIdGenerator(name,
					identifierType);

			if (idGenerator == null) {
				idGenerator = new SequentialIdentifierGenerator();
				log
						.info(name
								+ " Sequential Patient Id Generator Does Not Exist - Creating");
			}

			idGenerator.setName(name);
			idGenerator.setIdentifierType(identifierType);
			idGenerator.setBaseCharacterSet(baseCharacterSet);
			idGenerator.setFirstIdentifierBase(firstIdentifierBase);
			idGenerator.setLength(length);

			idSourceService.saveIdentifierSource(idGenerator);

		} catch (Exception e) {
			log.error("Error creating Patient Id generator in Idgen module", e);
		}
	}

	private PatientIdentifierType createPatientIdentifierType(String name,
			String description, String validator, User creator) {
		PatientService patientService = contextService.getPatientService();
		PatientIdentifierType idType = patientService
				.getPatientIdentifierTypeByName(name);
		if (idType == null) {
			log.info(name + " PatientIdentifierType Does Not Exist - Creating");
			idType = new PatientIdentifierType();
		}
		idType.setName(name);
		idType.setDescription(description);
		idType.setCreator(creator);
		idType.setValidator(validator);
		return patientService.savePatientIdentifierType(idType);
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
		removeTask(MotechConstants.TASK_DAILY_NURSE_CARE_MESSAGING);
		removeTask(MotechConstants.TASK_WEEKLY_NURSE_CARE_MESSAGING);
	}

	/* Activator methods end */

	/* SaveObsAdvice method */
	public void updateMessageProgramState(Integer personId, String conceptName) {

		// Only determine message program state for active enrolled programs
		// concerned with an observed concept and matching the concept of this
		// obs

		MotechService motechService = contextService.getMotechService();

		List<MessageProgramEnrollment> patientActiveEnrollments = motechService
				.getActiveMessageProgramEnrollments(personId, null, null);

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
				.getActiveMessageProgramEnrollments(null, null, null);

		for (MessageProgramEnrollment enrollment : activeEnrollments) {
			MessageProgram program = this.getMessageProgram(enrollment
					.getProgram());

			log.debug("MessageProgram Update - Update State: enrollment: "
					+ enrollment.getId());

			program.determineState(enrollment);
		}
	}

	public void sendStaffCareMessages(Date startDate, Date endDate,
			Date deliveryDate, Date deliveryTime, String[] careGroups,
			boolean sendUpcoming, boolean avoidBlackout) {

		if (avoidBlackout && isDuringBlackout(deliveryDate)) {
			log.debug("Cancelling nurse messages during blackout");
			return;
		}

		MotechService motechService = contextService.getMotechService();
		List<Facility> facilities = motechService.getAllFacilities();

		// All staff messages sent as SMS
		MediaType mediaType = MediaType.TEXT;
		// No corresponding message stored for staff care messages
		String messageId = null;
		// Set the time on the delivery date if needed
		deliveryDate = adjustTime(deliveryDate, deliveryTime);

		WebServiceModelConverterImpl modelConverter = new WebServiceModelConverterImpl();
		modelConverter.setRegistrarBean(this);

		for (Facility facility : facilities) {
			String phoneNumber = facility.getPhoneNumber();
			Location facilityLocation = facility.getLocation();
			if (phoneNumber == null
					|| facilityLocation == null
					|| !MotechConstants.LOCATION_KASSENA_NANKANA_WEST
							.equals(facilityLocation.getCountyDistrict())) {
				// Skip facilities without a phone number or
				// not in KNDW district
				continue;
			}

			// Send Defaulted Care Message
			List<ExpectedEncounter> defaultedEncounters = getDefaultedExpectedEncounters(
					facility, careGroups, startDate);
			List<ExpectedObs> defaultedObs = getDefaultedExpectedObs(facility,
					careGroups, startDate);
			if (!defaultedEncounters.isEmpty() || !defaultedObs.isEmpty()) {
				Care[] defaultedCares = modelConverter
						.defaultedToWebServiceCares(defaultedEncounters,
								defaultedObs);
				sendStaffDefaultedCareMessage(messageId, phoneNumber,
						mediaType, deliveryDate, null, defaultedCares);
			}

			if (sendUpcoming) {
				// Send Upcoming Care Messages
				List<ExpectedEncounter> upcomingEncounters = getUpcomingExpectedEncounters(
						facility, careGroups, startDate, endDate);
				for (ExpectedEncounter upcomingEncounter : upcomingEncounters) {
					org.motechproject.ws.Patient patient = modelConverter
							.upcomingEncounterToWebServicePatient(upcomingEncounter);

					sendStaffUpcomingCareMessage(messageId, phoneNumber,
							mediaType, deliveryDate, null, patient);
				}
				List<ExpectedObs> upcomingObs = getUpcomingExpectedObs(
						facility, careGroups, startDate, endDate);
				for (ExpectedObs upcomingObservation : upcomingObs) {
					org.motechproject.ws.Patient patient = modelConverter
							.upcomingObsToWebServicePatient(upcomingObservation);

					sendStaffUpcomingCareMessage(messageId, phoneNumber,
							mediaType, deliveryDate, null, patient);
				}
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

		Long notificationType = message.getSchedule().getMessage()
				.getPublicId();
		Integer recipientId = message.getSchedule().getRecipientId();
		Person person = personService.getPerson(recipientId);

		String phoneNumber = getPersonPhoneNumber(person);

		// Cancel message if phone number is considered troubled
		if (isPhoneTroubled(phoneNumber)) {
			if (log.isDebugEnabled()) {
				log.debug("Attempt to send to Troubled Phone, Phone: "
						+ phoneNumber + ", Notification cancelled: "
						+ notificationType);
			}

			message.setAttemptStatus(MessageStatus.CANCELLED);

		} else {
			if (log.isDebugEnabled()) {
				log.debug("Scheduled Message, Phone: " + phoneNumber
						+ ", Notification: " + notificationType);
			}

			String messageId = message.getPublicId();
			MediaType mediaType = getPersonMediaType(person);
			String languageCode = getPersonLanguageCode(person);
			NameValuePair[] personalInfo = new NameValuePair[0];

			Date messageStartDate = null;
			Date messageEndDate = null;

			if (!sendImmediate) {
				messageStartDate = message.getAttemptDate();
			}

			Patient patient = patientService.getPatient(recipientId);
			User staff = userService.getUser(recipientId);

			boolean sendMessageSuccess = false;
			if (patient != null) {
				ContactNumberType contactNumberType = getPersonPhoneType(person);
				String motechId = patient.getPatientIdentifier(
						MotechConstants.PATIENT_IDENTIFIER_MOTECH_ID)
						.getIdentifier();

				sendMessageSuccess = sendPatientMessage(messageId,
						personalInfo, motechId, phoneNumber, languageCode,
						mediaType, notificationType, messageStartDate,
						messageEndDate, contactNumberType);
			} else if (staff != null) {
				org.motechproject.ws.Patient[] patients = new org.motechproject.ws.Patient[0];

				sendMessageSuccess = sendStaffMessage(messageId, personalInfo,
						phoneNumber, languageCode, mediaType, notificationType,
						messageStartDate, messageEndDate, patients);
			} else {
				log.error("Attempt to send to Person not patient or staff: "
						+ recipientId);
			}
			if (sendMessageSuccess) {
				message.setAttemptStatus(MessageStatus.ATTEMPT_PENDING);
			} else {
				message.setAttemptStatus(MessageStatus.ATTEMPT_FAIL);
			}
		}

		motechService.saveMessage(message);
	}

	public boolean sendPatientMessage(String messageId,
			NameValuePair[] personalInfo, String motechId, String phoneNumber,
			String languageCode, MediaType mediaType, Long notificationType,
			Date messageStartDate, Date messageEndDate,
			ContactNumberType contactType) {

		try {
			org.motechproject.ws.MessageStatus messageStatus = mobileService
					.sendPatientMessage(messageId, personalInfo, phoneNumber,
							contactType, languageCode, mediaType,
							notificationType, messageStartDate, messageEndDate,
							motechId);

			return messageStatus != org.motechproject.ws.MessageStatus.FAILED;
		} catch (Exception e) {
			log.error("Mobile WS patient message failure", e);
			return false;
		}
	}

	public boolean sendStaffMessage(String messageId,
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
			log.error("Mobile WS staff message failure", e);
			return false;
		}
	}

	public boolean sendStaffDefaultedCareMessage(String messageId,
			String phoneNumber, MediaType mediaType, Date messageStartDate,
			Date messageEndDate, Care[] cares) {

		try {
			org.motechproject.ws.MessageStatus messageStatus = mobileService
					.sendDefaulterMessage(messageId, phoneNumber, cares,
							mediaType, messageStartDate, messageEndDate);

			return messageStatus != org.motechproject.ws.MessageStatus.FAILED;
		} catch (Exception e) {
			log.error("Mobile WS staff defaulted care message failure", e);
			return false;
		}
	}

	public boolean sendStaffUpcomingCareMessage(String messageId,
			String phoneNumber, MediaType mediaType, Date messageStartDate,
			Date messageEndDate, org.motechproject.ws.Patient patient) {

		try {
			org.motechproject.ws.MessageStatus messageStatus = mobileService
					.sendUpcomingCaresMessage(messageId, phoneNumber, patient,
							mediaType, messageStartDate, messageEndDate);

			return messageStatus != org.motechproject.ws.MessageStatus.FAILED;
		} catch (Exception e) {
			log.error("Mobile WS staff upcoming care message failure", e);
			return false;
		}
	}

	/* NotificationTask methods end */

	/* Factored out methods start */
	public String[] getActiveMessageProgramEnrollmentNames(Patient patient) {
		MotechService motechService = contextService.getMotechService();

		List<MessageProgramEnrollment> enrollments = motechService
				.getActiveMessageProgramEnrollments(patient.getPatientId(),
						null, null);

		List<String> enrollmentNames = new ArrayList<String>();
		for (MessageProgramEnrollment enrollment : enrollments) {
			enrollmentNames.add(enrollment.getProgram());
		}
		return enrollmentNames.toArray(new String[enrollmentNames.size()]);
	}

	public void addMessageProgramEnrollment(Integer personId, String program,
			Integer obsId) {
		MotechService motechService = contextService.getMotechService();

		List<MessageProgramEnrollment> enrollments = motechService
				.getActiveMessageProgramEnrollments(personId, program, obsId);
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

		List<MessageProgramEnrollment> enrollments = motechService
				.getActiveMessageProgramEnrollments(personId, program, obsId);
		for (MessageProgramEnrollment enrollment : enrollments) {
			removeMessageProgramEnrollment(enrollment);
		}
	}

	private void removeAllMessageProgramEnrollments(Integer personId) {
		MotechService motechService = contextService.getMotechService();

		List<MessageProgramEnrollment> enrollments = motechService
				.getActiveMessageProgramEnrollments(personId, null, null);

		for (MessageProgramEnrollment enrollment : enrollments) {
			removeMessageProgramEnrollment(enrollment);
		}
	}

	public Obs createNumericValueObs(Date date, Concept concept, Person person,
			Location location, Integer value, Encounter encounter, User creator) {

		Double doubleValue = new Double(value);
		return createNumericValueObs(date, concept, person, location,
				doubleValue, encounter, creator);
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
		if (Boolean.TRUE.equals(value)) {
			doubleValue = 1.0;
		} else {
			doubleValue = 0.0;
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

	public User getStaffBySystemId(String systemId) {
		UserService userService = contextService.getUserService();
		return userService.getUserByUsername(systemId);
	}

	public String getPersonPhoneNumber(Person person) {
		PersonAttribute phoneNumberAttr = person
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_PHONE_NUMBER);
		if (phoneNumberAttr != null) {
			return phoneNumberAttr.getValue();
		}
		log
				.warn("No phone number found for Person id: "
						+ person.getPersonId());
		return null;
	}

	public String getPersonLanguageCode(Person person) {
		PersonAttribute languageAttr = person
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_LANGUAGE);
		if (languageAttr != null) {
			return languageAttr.getValue();
		}
		log.debug("No language found for Person id: " + person.getPersonId());
		return null;
	}

	public ContactNumberType getPersonPhoneType(Person person) {
		PersonAttribute phoneTypeAttr = person
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_PHONE_TYPE);
		if (phoneTypeAttr != null && phoneTypeAttr.getValue() != null) {
			return ContactNumberType.valueOf(phoneTypeAttr.getValue());
		}
		log.debug("No contact number type found for Person id: "
				+ person.getPersonId());
		return null;
	}

	public MediaType getPersonMediaType(Person person) {
		PersonAttribute mediaTypeAttr = person
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE);
		if (mediaTypeAttr != null && mediaTypeAttr.getValue() != null) {
			return MediaType.valueOf(mediaTypeAttr.getValue());
		}
		log.debug("No media type found for Person id: " + person.getPersonId());
		return null;
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

	public Integer getMaxPatientCareReminders() {
		String careRemindersProperty = getPatientCareRemindersProperty();
		if (careRemindersProperty != null) {
			return Integer.parseInt(careRemindersProperty);
		}
		log.error("Patient Care Reminders Property not found");
		return null;
	}

	public DayOfWeek getPersonMessageDayOfWeek(Person person) {
		PersonAttribute dayAttr = person
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_DELIVERY_DAY);
		DayOfWeek day = null;
		if (dayAttr != null && dayAttr.getValue() != null) {
			try {
				day = DayOfWeek.valueOf(dayAttr.getValue());
			} catch (Exception e) {
				log.error("Invalid Patient Day of Week Attribute: "
						+ dayAttr.getValue(), e);
			}
		} else {
			log.debug("No day of week found for Person id: "
					+ person.getPersonId());
		}
		return day;
	}

	public Date getPersonMessageTimeOfDay(Person person) {
		PersonAttribute timeAttr = person
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_DELIVERY_TIME);
		Date time = null;
		if (timeAttr != null && timeAttr.getValue() != null) {
			SimpleDateFormat timeFormat = new SimpleDateFormat(
					MotechConstants.TIME_FORMAT_DELIVERY_TIME);
			try {
				time = timeFormat.parse(timeAttr.getValue());
			} catch (Exception e) {
				log.error("Invalid Patient Time of Day Attribute: "
						+ timeAttr.getValue(), e);
			}
		} else {
			log.debug("No time of day found for Person id: "
					+ person.getPersonId());
		}
		return time;
	}

	public DayOfWeek getDefaultPatientDayOfWeek() {
		String dayProperty = getPatientDayOfWeekProperty();
		DayOfWeek day = null;
		try {
			day = DayOfWeek.valueOf(dayProperty);
		} catch (Exception e) {
			log
					.error("Invalid Patient Day of Week Property: "
							+ dayProperty, e);
		}
		return day;
	}

	public Date getDefaultPatientTimeOfDay() {
		String timeProperty = getPatientTimeOfDayProperty();
		SimpleDateFormat timeFormat = new SimpleDateFormat(
				MotechConstants.TIME_FORMAT_DELIVERY_TIME);
		Date time = null;
		try {
			time = timeFormat.parse(timeProperty);
		} catch (Exception e) {
			log.error("Invalid Patient Time of Day Property: " + timeProperty,
					e);
		}
		return time;
	}

	public Date determinePreferredMessageDate(Person person, Date messageDate) {
		Calendar calendar = Calendar.getInstance();
		Date currentDate = calendar.getTime();
		calendar.setTime(messageDate);

		Date time = getPersonMessageTimeOfDay(person);
		if (time == null) {
			time = getDefaultPatientTimeOfDay();
		}
		if (time != null) {
			Calendar timeCalendar = Calendar.getInstance();
			timeCalendar.setTime(time);
			calendar.set(Calendar.HOUR_OF_DAY, timeCalendar
					.get(Calendar.HOUR_OF_DAY));
			calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
		}
		calendar.set(Calendar.SECOND, 0);

		DayOfWeek day = getPersonMessageDayOfWeek(person);
		if (day == null) {
			day = getDefaultPatientDayOfWeek();
		}
		if (day != null) {
			calendar.set(Calendar.DAY_OF_WEEK, day.getCalendarValue());
			if (calendar.getTime().before(currentDate)) {
				// Add a week if date in past after setting the day of week
				calendar.add(Calendar.DATE, 7);
			}
		}

		return calendar.getTime();
	}

	Date adjustTime(Date date, Date time) {
		if (date == null || time == null) {
			return date;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		Calendar timeCalendar = Calendar.getInstance();
		timeCalendar.setTime(time);
		calendar.set(Calendar.HOUR_OF_DAY, timeCalendar
				.get(Calendar.HOUR_OF_DAY));
		calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
		calendar.set(Calendar.SECOND, 0);
		if (calendar.getTime().before(date)) {
			// Add a day if before original date
			// after setting the time of day
			calendar.add(Calendar.DATE, 1);
		}
		return calendar.getTime();
	}

	Date adjustForBlackout(Date date) {
		if (date == null) {
			return date;
		}
		MotechService motechService = contextService.getMotechService();
		Blackout blackout = motechService.getBlackoutSettings();
		if (blackout == null) {
			return date;
		}

		Calendar blackoutCalendar = Calendar.getInstance();
		blackoutCalendar.setTime(date);

		Calendar timeCalendar = Calendar.getInstance();

		timeCalendar.setTime(blackout.getStartTime());
		blackoutCalendar.set(Calendar.HOUR_OF_DAY, timeCalendar
				.get(Calendar.HOUR_OF_DAY));
		blackoutCalendar
				.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
		blackoutCalendar
				.set(Calendar.SECOND, timeCalendar.get(Calendar.SECOND));
		Date blackoutStart = blackoutCalendar.getTime();

		timeCalendar.setTime(blackout.getEndTime());
		blackoutCalendar.set(Calendar.HOUR_OF_DAY, timeCalendar
				.get(Calendar.HOUR_OF_DAY));
		blackoutCalendar
				.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
		blackoutCalendar
				.set(Calendar.SECOND, timeCalendar.get(Calendar.SECOND));
		if (blackoutCalendar.getTime().before(blackoutStart)) {
			// Add a day if blackout end date before start date
			// after setting time
			blackoutCalendar.add(Calendar.DATE, 1);
		}
		Date blackoutEnd = blackoutCalendar.getTime();

		if (date.after(blackoutStart) && date.before(blackoutEnd)) {
			return blackoutEnd;
		}
		return date;
	}

	boolean isDuringBlackout(Date date) {
		if (date == null) {
			// If date is missing, checks if current date is during blackout
			date = new Date();
		}
		MotechService motechService = contextService.getMotechService();
		Blackout blackout = motechService.getBlackoutSettings();
		if (blackout == null) {
			return false;
		}

		Calendar blackoutCalendar = Calendar.getInstance();
		blackoutCalendar.setTime(date);

		Calendar timeCalendar = Calendar.getInstance();

		timeCalendar.setTime(blackout.getStartTime());
		blackoutCalendar.set(Calendar.HOUR_OF_DAY, timeCalendar
				.get(Calendar.HOUR_OF_DAY));
		blackoutCalendar
				.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
		blackoutCalendar
				.set(Calendar.SECOND, timeCalendar.get(Calendar.SECOND));
		Date blackoutStart = blackoutCalendar.getTime();

		timeCalendar.setTime(blackout.getEndTime());
		blackoutCalendar.set(Calendar.HOUR_OF_DAY, timeCalendar
				.get(Calendar.HOUR_OF_DAY));
		blackoutCalendar
				.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
		blackoutCalendar
				.set(Calendar.SECOND, timeCalendar.get(Calendar.SECOND));
		if (blackoutCalendar.getTime().before(blackoutStart)) {
			// Add a day if blackout end date before start date
			// after setting time
			blackoutCalendar.add(Calendar.DATE, 1);
		}
		Date blackoutEnd = blackoutCalendar.getTime();

		if (date.after(blackoutStart) && date.before(blackoutEnd)) {
			return true;
		}
		return false;
	}

	public PatientIdentifierType getMotechPatientIdType() {
		return contextService.getPatientService()
				.getPatientIdentifierTypeByName(
						MotechConstants.PATIENT_IDENTIFIER_MOTECH_ID);
	}

	public PersonAttributeType getPhoneNumberAttributeType() {
		return contextService.getPersonService().getPersonAttributeTypeByName(
				MotechConstants.PERSON_ATTRIBUTE_PHONE_NUMBER);
	}

	public PersonAttributeType getNHISNumberAttributeType() {
		return contextService.getPersonService().getPersonAttributeTypeByName(
				MotechConstants.PERSON_ATTRIBUTE_NHIS_NUMBER);
	}

	public PersonAttributeType getNHISExpirationDateAttributeType() {
		return contextService.getPersonService().getPersonAttributeTypeByName(
				MotechConstants.PERSON_ATTRIBUTE_NHIS_EXP_DATE);
	}

	public PersonAttributeType getPhoneTypeAttributeType() {
		return contextService.getPersonService().getPersonAttributeTypeByName(
				MotechConstants.PERSON_ATTRIBUTE_PHONE_TYPE);
	}

	public PersonAttributeType getLanguageAttributeType() {
		return contextService.getPersonService().getPersonAttributeTypeByName(
				MotechConstants.PERSON_ATTRIBUTE_LANGUAGE);
	}

	public PersonAttributeType getMediaTypeAttributeType() {
		return contextService.getPersonService().getPersonAttributeTypeByName(
				MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE);
	}

	public PersonAttributeType getDeliveryTimeAttributeType() {
		return contextService.getPersonService().getPersonAttributeTypeByName(
				MotechConstants.PERSON_ATTRIBUTE_DELIVERY_TIME);
	}

	public PersonAttributeType getInsuredAttributeType() {
		return contextService.getPersonService().getPersonAttributeTypeByName(
				MotechConstants.PERSON_ATTRIBUTE_INSURED);
	}

	public PersonAttributeType getHowLearnedAttributeType() {
		return contextService.getPersonService().getPersonAttributeTypeByName(
				MotechConstants.PERSON_ATTRIBUTE_HOW_LEARNED);
	}

	public PersonAttributeType getInterestReasonAttributeType() {
		return contextService.getPersonService().getPersonAttributeTypeByName(
				MotechConstants.PERSON_ATTRIBUTE_INTEREST_REASON);
	}

	public PersonAttributeType getDeliveryDayAttributeType() {
		return contextService.getPersonService().getPersonAttributeTypeByName(
				MotechConstants.PERSON_ATTRIBUTE_DELIVERY_DAY);
	}

	public Location getGhanaLocation() {
		return contextService.getLocationService().getLocation(
				MotechConstants.LOCATION_GHANA);
	}

	public EncounterType getANCVisitEncounterType() {
		return contextService.getEncounterService().getEncounterType(
				MotechConstants.ENCOUNTER_TYPE_ANCVISIT);
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

	public EncounterType getPregnancyDeliveryNotificationEncounterType() {
		return contextService.getEncounterService().getEncounterType(
				MotechConstants.ENCOUNTER_TYPE_PREGDELNOTIFYVISIT);
	}

	public EncounterType getOutpatientVisitEncounterType() {
		return contextService.getEncounterService().getEncounterType(
				MotechConstants.ENCOUNTER_TYPE_OUTPATIENTVISIT);
	}

	public EncounterType getTTVisitEncounterType() {
		return contextService.getEncounterService().getEncounterType(
				MotechConstants.ENCOUNTER_TYPE_TTVISIT);
	}

	public EncounterType getCWCVisitEncounterType() {
		return contextService.getEncounterService().getEncounterType(
				MotechConstants.ENCOUNTER_TYPE_CWCVISIT);
	}

	public EncounterType getMotherPNCVisitEncounterType() {
		return contextService.getEncounterService().getEncounterType(
				MotechConstants.ENCOUNTER_TYPE_PNCMOTHERVISIT);
	}

	public EncounterType getChildPNCVisitEncounterType() {
		return contextService.getEncounterService().getEncounterType(
				MotechConstants.ENCOUNTER_TYPE_PNCCHILDVISIT);
	}

	public EncounterType getANCRegistrationEncounterType() {
		return contextService.getEncounterService().getEncounterType(
				MotechConstants.ENCOUNTER_TYPE_ANCREGVISIT);
	}

	public EncounterType getCWCRegistrationEncounterType() {
		return contextService.getEncounterService().getEncounterType(
				MotechConstants.ENCOUNTER_TYPE_CWCREGVISIT);
	}

	public EncounterType getBirthEncounterType() {
		return contextService.getEncounterService().getEncounterType(
				MotechConstants.ENCOUNTER_TYPE_BIRTHVISIT);
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

	public Concept getHIVTestResultConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_HIV_TEST_RESULT);
	}

	public Concept getTerminationTypeConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_TERMINATION_TYPE);
	}

	public Concept getTerminationComplicationConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_TERMINATION_COMPLICATION);
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

	public Concept getReferredConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_REFERRED);
	}

	public Concept getPrimaryDiagnosisConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_PRIMARY_DIAGNOSIS);
	}

	public Concept getSecondaryDiagnosisConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_SECONDARY_DIAGNOSIS);
	}

	public Concept getDeliveryModeConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_DELIVERY_MODE);
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

	public Concept getMalariaRDTConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_MALARIA_RAPID_TEST);
	}

	public Concept getVDRLTreatmentConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_VDRL_TREATMENT);
	}

	public Concept getUrineProteinTestConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_URINE_PROTEIN_TEST);
	}

	public Concept getUrineGlucoseTestConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_URINE_GLUCOSE_TEST);
	}

	public Concept getFetalHeartRateConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_FETAL_HEART_RATE);
	}

	public Concept getFundalHeightConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_FUNDAL_HEIGHT);
	}

	public Concept getVVFRepairConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_VVF_REPAIR);
	}

	public Concept getDewormerConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_DEWORMER);
	}

	public Concept getPMTCTConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_PMTCT);
	}

	public Concept getPMTCTTreatmentConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_PMTCT_TREATMENT);
	}

	public Concept getACTTreatmentConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_ACT_TREATMENT);
	}

	public Concept getPreHIVTestCounselingConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_HIV_PRE_TEST_COUNSELING);
	}

	public Concept getPostHIVTestCounselingConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_HIV_POST_TEST_COUNSELING);
	}

	public Concept getDeliveryComplicationConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_DELIVERY_COMPLICATION);
	}

	public Concept getPostAbortionFPCounselingConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_POST_ABORTION_FP_COUNSELING);
	}

	public Concept getPostAbortionFPAcceptedConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_POST_ABORTION_FP_ACCEPTED);
	}

	public Concept getIPTReactionConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_IPT_REACTION);
	}

	public Concept getLochiaColourConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_LOCHIA_COLOUR);
	}

	public Concept getLochiaExcessConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_LOCHIA_EXCESS_AMOUNT);
	}

	public Concept getMUACConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_MIDDLE_UPPER_ARM_CIRCUMFERENCE);
	}

	public Concept getMaternalDeathConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_MATERNAL_DEATH);
	}

	public Concept getTerminationProcedureConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_TERMINATION_PROCEDURE);
	}

	public Concept getCordConditionConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_CORD_CONDITION);
	}

	public Concept getConditionBabyConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_CONDITION_OF_BABY);
	}

	public Concept getNextANCDateConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_NEXT_ANC_DATE);
	}

	public Concept getMaleInvolvementConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_MALE_INVOLVEMENT);
	}

	public Concept getCommunityConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_COMMUNITY);
	}

	public Concept getHouseConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_HOUSE);
	}

	public Concept getANCPNCLocationConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_ANC_PNC_LOCATION);
	}

	public Concept getCWCLocationConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_CWC_LOCATION);
	}

	public Concept getCommentsConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_COMMENTS);
	}

	public Concept getVDRLConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_VDRL);
	}

	public Concept getRespiratoryRateConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_RESPIRATORY_RATE);
	}

	public Concept getDiastolicBloodPressureConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_DIASTOLIC_BLOOD_PRESSURE);
	}

	public Concept getSystolicBloodPressureConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_SYSTOLIC_BLOOD_PRESSURE);
	}

	public Concept getHemoglobinConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_HEMOGLOBIN);
	}

	public Concept getWeightConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_WEIGHT);
	}

	public Concept getHeightConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_HEIGHT);
	}

	public Concept getTemperatureConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_TEMPERATURE);
	}

	public Concept getReactiveConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_REACTIVE);
	}

	public Concept getNonReactiveConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_NON_REACTIVE);
	}

	public Concept getPositiveConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_POSITIVE);
	}

	public Concept getNegativeConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_NEGATIVE);
	}

	public Concept getANCRegistrationNumberConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_ANC_REG_NUMBER);
	}

	public Concept getCWCRegistrationNumberConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_CWC_REG_NUMBER);
	}

	public String getTroubledPhoneProperty() {
		return contextService.getAdministrationService().getGlobalProperty(
				MotechConstants.GLOBAL_PROPERTY_TROUBLED_PHONE);
	}

	public String getPatientCareRemindersProperty() {
		return contextService.getAdministrationService().getGlobalProperty(
				MotechConstants.GLOBAL_PROPERTY_CARE_REMINDERS);
	}

	public String getPatientDayOfWeekProperty() {
		return contextService.getAdministrationService().getGlobalProperty(
				MotechConstants.GLOBAL_PROPERTY_DAY_OF_WEEK);
	}

	public String getPatientTimeOfDayProperty() {
		return contextService.getAdministrationService().getGlobalProperty(
				MotechConstants.GLOBAL_PROPERTY_TIME_OF_DAY);
	}

	/* Factored out methods end */

	public Facility getFacilityById(Integer facilityId) {
		return contextService.getMotechService().getFacilityById(facilityId);
	}

	public Community getCommunityById(Integer communityId) {
		return contextService.getMotechService().getCommunityById(communityId);
	}

	public Community getCommunityByPatient(Patient patient) {
		return contextService.getMotechService().getCommunityByPatient(patient);
	}

	public List<String> getStaffTypes() {
		return staffTypes;
	}

	public void setStaffTypes(List<String> staffTypes) {
		this.staffTypes = staffTypes;
	}

}
