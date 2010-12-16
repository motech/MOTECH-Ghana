/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010 The Trustees of Columbia University in the City of
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

package org.motechproject.server.svc.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.event.MessageProgram;
import org.motechproject.server.messaging.MessageDefDate;
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
import org.motechproject.server.omod.VerhoeffValidator;
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
import org.motechproject.ws.PatientMessage;
import org.motechproject.ws.RegistrantType;
import org.motechproject.ws.RegistrationMode;
import org.motechproject.ws.mobile.MessageService;
import org.openmrs.Concept;
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
import org.openmrs.scheduler.SchedulerService;
import org.openmrs.scheduler.TaskDefinition;
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

		User staff = new User();

		// Set staff id to generated id check digit
		staff.setSystemId(generateStaffId());

		staff.setGender(MotechConstants.GENDER_UNKNOWN_OPENMRS);

		PersonName name = new PersonName(firstName, null, lastName);
		staff.addName(name);

		if (phone != null) {
			PersonAttributeType phoneNumberAttrType = getPhoneNumberAttributeType();
			staff.addAttribute(new PersonAttribute(phoneNumberAttrType, phone));
		}

		// No privileges given to staff user
		Role role = userService.getRole(OpenmrsConstants.PROVIDER_ROLE);
		staff.addRole(role);

		// Generate random password for new staff user
		return userService.saveUser(staff, generatePassword(8));
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
		Date date = new Date();

		return registerPatient(staff, facility, date, registrationMode,
				motechId, registrantType, firstName, middleName, lastName,
				preferredName, dateOfBirth, estimatedBirthDate, sex, insured,
				nhis, nhisExpires, mother, community, address, phoneNumber,
				expDeliveryDate, deliveryDateConfirmed, enroll, consent,
				ownership, format, language, dayOfWeek, timeOfDay, reason,
				howLearned, messagesStartWeek);
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

		PatientService patientService = contextService.getPatientService();
		PersonService personService = contextService.getPersonService();
		MotechService motechService = contextService.getMotechService();

		// Inherit values from Mother's record including
		// last name, address, messaging preferences and enrollment
		if (mother != null) {
			PersonName personName = mother.getPersonName();
			if (lastName == null && personName != null) {
				lastName = personName.getFamilyName();
			}
			PersonAddress personAddress = mother.getPersonAddress();
			if (address == null && personAddress != null) {
				address = personAddress.getAddress1();
			}
			if (community == null) {
				community = getCommunityByPatient(mother);
			}
			if (phoneNumber == null) {
				phoneNumber = getPersonPhoneNumber(mother);
			}
			if (ownership == null) {
				ownership = getPersonPhoneType(mother);
			}
			if (format == null) {
				format = getPersonMediaType(mother);
			}
			if (language == null) {
				language = getPersonLanguageCode(mother);
			}
			if (dayOfWeek == null) {
				dayOfWeek = getPersonMessageDayOfWeek(mother);
			}
			if (timeOfDay == null) {
				timeOfDay = getPersonMessageTimeOfDay(mother);
			}
			if (enroll == null && consent == null) {
				List<MessageProgramEnrollment> enrollments = motechService
						.getActiveMessageProgramEnrollments(mother
								.getPatientId(), null, null, null, null, null);
				if (enrollments != null && !enrollments.isEmpty()) {
					enroll = true;
					consent = true;
				}
			}
		}

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
			pregnancyDueDateObsId = registerPregnancy(staff, facility, date,
					patient, expDeliveryDate, deliveryDateConfirmed);
		}

		enrollPatient(patient, community, enroll, consent, messagesStartWeek,
				pregnancyDueDateObsId);

		recordPatientRegistration(staff, facility, date, patient);

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

			if (enrollPatient) {
				referenceDateObsId = storeMessagesWeekObs(patient,
						messagesStartWeek);
			}
		}

		if (enrollPatient) {
			if (infoMessageProgramName != null) {
				addMessageProgramEnrollment(patient.getPatientId(),
						infoMessageProgramName, referenceDateObsId);
			}

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

	private Integer storeMessagesWeekObs(Patient patient,
			Integer messagesStartWeek) {
		ObsService obsService = contextService.getObsService();

		Location ghanaLocation = getGhanaLocation();
		Date currentDate = new Date();

		Calendar calendar = Calendar.getInstance();
		// Convert weeks to days, plus one day
		calendar.add(Calendar.DATE, (messagesStartWeek * -7) + 1);
		Date referenceDate = calendar.getTime();

		Obs refDateObs = createDateValueObs(currentDate,
				getEnrollmentReferenceDateConcept(), patient, ghanaLocation,
				referenceDate, null, null);

		refDateObs = obsService.saveObs(refDateObs, null);
		return refDateObs.getObsId();
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

	private void recordPatientRegistration(User staff, Location facility,
			Date date, Patient patient) {

		EncounterService encounterService = contextService
				.getEncounterService();

		Encounter encounter = new Encounter();
		encounter.setEncounterType(getPatientRegistrationEncounterType());
		encounter.setEncounterDatetime(date);
		encounter.setPatient(patient);
		encounter.setLocation(facility);
		encounter.setProvider(staff);

		encounterService.saveEncounter(encounter);
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
			Boolean insured, String nhis, Date nhisExpires, Patient mother,
			Community community, String address, String phoneNumber,
			Date expDeliveryDate, Boolean enroll, Boolean consent,
			ContactNumberType ownership, MediaType format, String language,
			DayOfWeek dayOfWeek, Date timeOfDay) {

		PatientService patientService = contextService.getPatientService();
		PersonService personService = contextService.getPersonService();

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

		Relationship motherRelationship = getMotherRelationship(patient);
		if (mother != null) {
			if (motherRelationship != null) {
				Person currentMother = motherRelationship.getPersonA();
				if (!currentMother.getPersonId().equals(mother.getPatientId())) {
					motherRelationship.setPersonA(mother);
					personService.saveRelationship(motherRelationship);
				}
			} else {
				RelationshipType parentChildRelationshipType = personService
						.getRelationshipTypeByName(MotechConstants.RELATIONSHIP_TYPE_PARENT_CHILD);
				motherRelationship = new Relationship(mother, patient,
						parentChildRelationshipType);
				personService.saveRelationship(motherRelationship);
			}
		} else if (motherRelationship != null) {
			motherRelationship = personService.voidRelationship(
					motherRelationship, "Removed in web form");
			// Saving relationship since voidRelationship will not save with
			// required advice and void handler
			personService.saveRelationship(motherRelationship);
		}

		Community currentCommunity = getCommunityByPatient(patient);
		if (currentCommunity != null
				&& currentCommunity.getCommunityId() != null
				&& community != null
				&& community.getCommunityId() != null
				&& !currentCommunity.getCommunityId().equals(
						community.getCommunityId())) {
			currentCommunity.getResidents().remove(patient);
		}
		// Query flushes session
		// Only add if no Community currently associated
		if (community != null && getCommunityByPatient(patient) == null) {
			community.getResidents().add(patient);
			currentCommunity = community;
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
		Date date = new Date();

		if (pregnancyDueDateObsId == null) {
			pregnancyDueDateObsId = registerPregnancy(staff, facility, date,
					patient, expDeliveryDate, deliveryDateConfirmed);
		}

		enrollPatientWithAttributes(patient, null, enroll, consent, ownership,
				phoneNumber, format, language, dayOfWeek, timeOfDay, reason,
				howLearned, null, pregnancyDueDateObsId);
	}

	private Integer registerPregnancy(User staff, Location facility, Date date,
			Patient patient, Date dueDate, Boolean dueDateConfirmed) {

		EncounterService encounterService = contextService
				.getEncounterService();
		ObsService obsService = contextService.getObsService();

		Encounter encounter = new Encounter();
		encounter
				.setEncounterType(getPregnancyRegistrationVisitEncounterType());
		encounter.setEncounterDatetime(date);
		encounter.setPatient(patient);
		encounter.setLocation(facility);
		encounter.setProvider(staff);
		encounter = encounterService.saveEncounter(encounter);

		Obs pregnancyObs = createObs(date, getPregnancyConcept(), patient,
				facility, encounter, null);

		Obs pregnancyStatusObs = createBooleanValueObs(date,
				getPregnancyStatusConcept(), patient, facility, Boolean.TRUE,
				encounter, null);
		pregnancyObs.addGroupMember(pregnancyStatusObs);

		Obs dueDateObs = null;
		if (dueDate != null) {
			dueDateObs = createDateValueObs(date, getDueDateConcept(), patient,
					facility, dueDate, encounter, null);
			pregnancyObs.addGroupMember(dueDateObs);
		}

		if (dueDateConfirmed != null) {
			Obs dueDateConfirmedObs = createBooleanValueObs(date,
					getDueDateConfirmedConcept(), patient, facility,
					dueDateConfirmed, encounter, null);
			pregnancyObs.addGroupMember(dueDateConfirmedObs);
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
			pregnancyDueDateObsId = registerPregnancy(staff, facility, date,
					patient, estDeliveryDate, null);
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

	public void recordPatientHistory(User staff, Location facility, Date date,
			Patient patient, Integer lastIPT, Date lastIPTDate, Integer lastTT,
			Date lastTTDate, Date bcgDate, Integer lastOPV, Date lastOPVDate,
			Integer lastPenta, Date lastPentaDate, Date measlesDate,
			Date yellowFeverDate, Integer lastIPTI, Date lastIPTIDate,
			Date lastVitaminADate) {

		EncounterService encounterService = contextService
				.getEncounterService();

		// Not associating historical data with any facility
		Location ghanaLocation = getGhanaLocation();

		Encounter historyEncounter = new Encounter();
		historyEncounter.setEncounterType(getPatientHistoryEncounterType());
		historyEncounter.setEncounterDatetime(date);
		historyEncounter.setPatient(patient);
		historyEncounter.setLocation(ghanaLocation);
		historyEncounter.setProvider(staff);

		if (lastIPT != null && lastIPTDate != null) {
			Obs iptDoseObs = createNumericValueObs(lastIPTDate,
					getIPTDoseConcept(), patient, ghanaLocation, lastIPT,
					historyEncounter, null);
			historyEncounter.addObs(iptDoseObs);
		}
		if (lastTT != null && lastTTDate != null) {
			Obs ttDoseObs = createNumericValueObs(lastTTDate,
					getTetanusDoseConcept(), patient, ghanaLocation, lastTT,
					historyEncounter, null);
			historyEncounter.addObs(ttDoseObs);
		}
		if (bcgDate != null) {
			Obs bcgObs = createConceptValueObs(bcgDate,
					getImmunizationsOrderedConcept(), patient, ghanaLocation,
					getBCGConcept(), historyEncounter, null);
			historyEncounter.addObs(bcgObs);
		}
		if (lastOPV != null && lastOPVDate != null) {
			Obs opvDoseObs = createNumericValueObs(lastOPVDate,
					getOPVDoseConcept(), patient, ghanaLocation, lastOPV,
					historyEncounter, null);
			historyEncounter.addObs(opvDoseObs);
		}
		if (lastPenta != null && lastPentaDate != null) {
			Obs pentaDoseObs = createNumericValueObs(lastPentaDate,
					getPentaDoseConcept(), patient, ghanaLocation, lastPenta,
					historyEncounter, null);
			historyEncounter.addObs(pentaDoseObs);
		}
		if (measlesDate != null) {
			Obs measlesObs = createConceptValueObs(measlesDate,
					getImmunizationsOrderedConcept(), patient, ghanaLocation,
					getMeaslesConcept(), historyEncounter, null);
			historyEncounter.addObs(measlesObs);
		}
		if (yellowFeverDate != null) {
			Obs yellowFeverObs = createConceptValueObs(yellowFeverDate,
					getImmunizationsOrderedConcept(), patient, ghanaLocation,
					getYellowFeverConcept(), historyEncounter, null);
			historyEncounter.addObs(yellowFeverObs);
		}
		if (lastIPTI != null && lastIPTIDate != null) {
			Obs iptiObs = createNumericValueObs(lastIPTIDate,
					getIPTiDoseConcept(), patient, ghanaLocation, lastIPTI,
					historyEncounter, null);
			historyEncounter.addObs(iptiObs);
		}
		if (lastVitaminADate != null) {
			Obs vitaminAObs = createConceptValueObs(lastVitaminADate,
					getImmunizationsOrderedConcept(), patient, ghanaLocation,
					getVitaminAConcept(), historyEncounter, null);
			historyEncounter.addObs(vitaminAObs);
		}
		if (!historyEncounter.getAllObs().isEmpty()) {
			encounterService.saveEncounter(historyEncounter);
		}
	}

	@Transactional
	public void registerANCMother(User staff, Location facility, Date date,
			Patient patient, String ancRegNumber, Date estDeliveryDate,
			Double height, Integer gravida, Integer parity, Boolean enroll,
			Boolean consent, ContactNumberType ownership, String phoneNumber,
			MediaType format, String language, DayOfWeek dayOfWeek,
			Date timeOfDay, HowLearned howLearned) {

		EncounterService encounterService = contextService
				.getEncounterService();

		Encounter encounter = new Encounter();
		encounter.setEncounterType(getANCRegistrationEncounterType());
		encounter.setEncounterDatetime(date);
		encounter.setPatient(patient);
		encounter.setLocation(facility);
		encounter.setProvider(staff);

		if (ancRegNumber != null) {
			Obs ancRegNumObs = createTextValueObs(date,
					getANCRegistrationNumberConcept(), patient, facility,
					ancRegNumber, encounter, null);
			encounter.addObs(ancRegNumObs);
		}
		if (gravida != null) {
			Obs gravidaObs = createNumericValueObs(date, getGravidaConcept(),
					patient, facility, gravida, encounter, null);
			encounter.addObs(gravidaObs);
		}

		if (parity != null) {
			Obs parityObs = createNumericValueObs(date, getParityConcept(),
					patient, facility, parity, encounter, null);
			encounter.addObs(parityObs);
		}

		if (height != null) {
			Obs heightObs = createNumericValueObs(date, getHeightConcept(),
					patient, facility, height, encounter, null);
			encounter.addObs(heightObs);
		}
		encounterService.saveEncounter(encounter);

		Integer pregnancyDueDateObsId = null;
		Obs pregnancyObs = getActivePregnancy(patient.getPatientId());
		if (pregnancyObs == null) {
			pregnancyDueDateObsId = registerPregnancy(staff, facility, date,
					patient, estDeliveryDate, null);
		} else {
			Obs pregnancyDueDateObs = getActivePregnancyDueDateObs(patient
					.getPatientId(), pregnancyObs);
			if (pregnancyDueDateObs != null) {
				pregnancyDueDateObsId = pregnancyDueDateObs.getObsId();
				if (estDeliveryDate != null) {
					pregnancyDueDateObsId = updatePregnancyDueDateObs(
							pregnancyObs, pregnancyDueDateObs, estDeliveryDate,
							encounter);
				}
			} else if (estDeliveryDate != null) {
				log.warn("Cannot update pregnancy due date, "
						+ "no active pregnancy due date found, patient id="
						+ patient.getPatientId());
			}
		}

		enrollPatientWithAttributes(patient, null, enroll, consent, ownership,
				phoneNumber, format, language, dayOfWeek, timeOfDay, null,
				howLearned, null, pregnancyDueDateObsId);
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

		if (cwcRegNumber != null) {
			Obs cwcRegNumObs = createTextValueObs(date,
					getCWCRegistrationNumberConcept(), patient, facility,
					cwcRegNumber, encounter, null);
			encounter.addObs(cwcRegNumObs);
		}
		encounterService.saveEncounter(encounter);
	}

	@Transactional
	public void recordMotherANCVisit(User staff, Location facility, Date date,
			Patient patient, Integer visitNumber, Integer ancLocation,
			String house, String community, Date estDeliveryDate,
			Integer bpSystolic, Integer bpDiastolic, Double weight,
			Integer ttDose, Integer iptDose, Boolean iptReactive,
			Boolean itnUse, Double fht, Integer fhr, Integer urineTestProtein,
			Integer urineTestGlucose, Double hemoglobin, Boolean vdrlReactive,
			Boolean vdrlTreatment, Boolean dewormer, Boolean maleInvolved,
			Boolean pmtct, Boolean preTestCounseled, HIVResult hivTestResult,
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
		if (urineTestProtein != null) {
			Concept urineProteinTestValueConcept = null;
			switch (urineTestProtein) {
			case 0:
				urineProteinTestValueConcept = getNegativeConcept();
				break;
			case 1:
				urineProteinTestValueConcept = getPositiveConcept();
				break;
			case 2:
				urineProteinTestValueConcept = getTraceConcept();
				break;
			}
			if (urineProteinTestValueConcept != null) {
				Obs urineTestProteinPositiveObs = createConceptValueObs(date,
						getUrineProteinTestConcept(), patient, facility,
						urineProteinTestValueConcept, encounter, null);
				encounter.addObs(urineTestProteinPositiveObs);
			}
		}
		if (urineTestGlucose != null) {
			Concept urineGlucoseTestValueConcept = null;
			switch (urineTestGlucose) {
			case 0:
				urineGlucoseTestValueConcept = getNegativeConcept();
				break;
			case 1:
				urineGlucoseTestValueConcept = getPositiveConcept();
				break;
			case 2:
				urineGlucoseTestValueConcept = getTraceConcept();
				break;
			}
			if (urineGlucoseTestValueConcept != null) {
				Obs urineTestProteinPositiveObs = createConceptValueObs(date,
						getUrineGlucoseTestConcept(), patient, facility,
						urineGlucoseTestValueConcept, encounter, null);
				encounter.addObs(urineTestProteinPositiveObs);
			}
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

		List<Patient> childPatients = new ArrayList<Patient>();

		for (BirthOutcomeChild childOutcome : outcomes) {
			if (childOutcome.getOutcome() == null) {
				// Skip child outcomes missing required outcome
				continue;
			}
			Obs childOutcomeObs = createTextValueObs(datetime,
					getBirthOutcomeConcept(), patient, facility, childOutcome
							.getOutcome().name(), encounter, null);
			encounter.addObs(childOutcomeObs);

			if (BirthOutcome.A == childOutcome.getOutcome()) {
				Patient child = registerPatient(staff, facility, datetime,
						childOutcome.getIdMode(), childOutcome.getMotechId(),
						RegistrantType.CHILD_UNDER_FIVE, childOutcome
								.getFirstName(), null, null, null, datetime,
						false, childOutcome.getSex(), null, null, null,
						patient, null, null, null, null, null, null, null,
						null, null, null, null, null, null, null, null);

				if (childOutcome.getWeight() != null) {
					recordBirthData(staff, facility, child, datetime,
							childOutcome.getWeight());
				}

				childPatients.add(child);
			}
		}

		encounterService.saveEncounter(encounter);

		if (Boolean.TRUE.equals(maternalDeath)) {
			processPatientDeath(patient, datetime);
		}

		return childPatients;
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
			Boolean lochiaOdourFoul, Double temperature, Double fht,
			String comments) {

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
		if (lochiaOdourFoul != null) {
			Obs lochiaOdourObs = createBooleanValueObs(datetime,
					getLochiaFoulConcept(), patient, facility, lochiaOdourFoul,
					encounter, null);
			encounter.addObs(lochiaOdourObs);
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
			Double temperature, Boolean bcg, Boolean opv0, Integer respiration,
			Boolean cordConditionNormal, Boolean babyConditionGood,
			String comments) {

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
			Boolean measles, Boolean yellowFever, Boolean csm,
			Integer iptiDose, Boolean vitaminA, Boolean dewormer,
			Double weight, Double muac, Double height, Boolean maleInvolved,
			String comments) {

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
		if (iptiDose != null) {
			Obs iptiObs = createNumericValueObs(date, getIPTiDoseConcept(),
					patient, facility, iptiDose, encounter, null);
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
			Patient patient, String serialNumber, Boolean insured,
			Integer diagnosis, Integer secondDiagnosis, Boolean rdtGiven,
			Boolean rdtPositive, Boolean actTreated, Boolean newCase,
			Boolean referred, String comments) {

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
		if (insured != null) {
			Obs insuredObs = createBooleanValueObs(date, getInsuredConcept(),
					patient, facility, insured, encounter, null);
			encounter.addObs(insuredObs);
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
		Integer maxResults = getMaxQueryResults();

		return motechService.getPatients(firstName, lastName, preferredName,
				birthDate, communityId, phoneNumber, phoneNumberAttrType,
				nhisNumber, nhisAttrType, motechId, motechIdType, maxResults);
	}

	public List<Patient> getDuplicatePatients(String firstName,
			String lastName, String preferredName, Date birthDate,
			Integer communityId, String phoneNumber, String nhisNumber,
			String motechId) {

		MotechService motechService = contextService.getMotechService();

		PersonAttributeType phoneNumberAttrType = getPhoneNumberAttributeType();
		PersonAttributeType nhisAttrType = getNHISNumberAttributeType();
		PatientIdentifierType motechIdType = getMotechPatientIdType();
		Integer maxResults = getMaxQueryResults();

		return motechService.getDuplicatePatients(firstName, lastName,
				preferredName, birthDate, communityId, phoneNumber,
				phoneNumberAttrType, nhisNumber, nhisAttrType, motechId,
				motechIdType, maxResults);
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
		Integer maxResults = getMaxQueryResults();
		return motechService.getExpectedEncounter(patient, null, null, null,
				oneWeekLaterDate, null, currentDate, maxResults);
	}

	public List<ExpectedObs> getUpcomingExpectedObs(Patient patient) {
		MotechService motechService = contextService.getMotechService();
		Calendar calendar = Calendar.getInstance();
		Date currentDate = calendar.getTime();
		calendar.add(Calendar.DATE, 7);
		Date oneWeekLaterDate = calendar.getTime();
		Integer maxResults = getMaxQueryResults();
		return motechService.getExpectedObs(patient, null, null, null,
				oneWeekLaterDate, null, currentDate, maxResults);
	}

	public List<ExpectedEncounter> getDefaultedExpectedEncounters(
			Facility facility, String[] groups) {
		MotechService motechService = contextService.getMotechService();
		Date currentDate = new Date();
		Integer maxResults = getMaxQueryResults();
		return motechService.getExpectedEncounter(null, facility, groups, null,
				null, currentDate, currentDate, maxResults);
	}

	public List<ExpectedObs> getDefaultedExpectedObs(Facility facility,
			String[] groups) {
		MotechService motechService = contextService.getMotechService();
		Date currentDate = new Date();
		Integer maxResults = getMaxQueryResults();
		return motechService.getExpectedObs(null, facility, groups, null, null,
				currentDate, currentDate, maxResults);
	}

	private List<ExpectedEncounter> getUpcomingExpectedEncounters(
			Facility facility, String[] groups, Date fromDate, Date toDate) {
		MotechService motechService = contextService.getMotechService();
		Integer maxResults = getMaxQueryResults();
		return motechService.getExpectedEncounter(null, facility, groups, null,
				toDate, null, fromDate, maxResults);
	}

	private List<ExpectedObs> getUpcomingExpectedObs(Facility facility,
			String[] groups, Date fromDate, Date toDate) {
		MotechService motechService = contextService.getMotechService();
		Integer maxResults = getMaxQueryResults();
		return motechService.getExpectedObs(null, facility, groups, null,
				toDate, null, fromDate, maxResults);
	}

	private List<ExpectedEncounter> getDefaultedExpectedEncounters(
			Facility facility, String[] groups, Date forDate) {
		MotechService motechService = contextService.getMotechService();
		Integer maxResults = getMaxQueryResults();
		return motechService.getExpectedEncounter(null, facility, groups, null,
				null, forDate, forDate, maxResults);
	}

	private List<ExpectedObs> getDefaultedExpectedObs(Facility facility,
			String[] groups, Date forDate) {
		MotechService motechService = contextService.getMotechService();
		Integer maxResults = getMaxQueryResults();
		return motechService.getExpectedObs(null, facility, groups, null, null,
				forDate, forDate, maxResults);
	}

	public List<ExpectedEncounter> getExpectedEncounters(Patient patient) {
		MotechService motechService = contextService.getMotechService();
		Date currentDate = new Date();
		return motechService.getExpectedEncounter(patient, null, null, null,
				null, null, currentDate, null);
	}

	public List<ExpectedObs> getExpectedObs(Patient patient) {
		MotechService motechService = contextService.getMotechService();
		Date currentDate = new Date();
		return motechService.getExpectedObs(patient, null, null, null, null,
				null, currentDate, null);
	}

	public List<ExpectedEncounter> getExpectedEncounters(Patient patient,
			String group) {
		MotechService motechService = contextService.getMotechService();
		return motechService.getExpectedEncounter(patient, null,
				new String[] { group }, null, null, null, null, null);
	}

	public List<ExpectedObs> getExpectedObs(Patient patient, String group) {
		MotechService motechService = contextService.getMotechService();
		return motechService.getExpectedObs(patient, null,
				new String[] { group }, null, null, null, null, null);
	}

	public List<Encounter> getRecentDeliveries(Facility facility) {
		MotechService motechService = contextService.getMotechService();

		EncounterType deliveryEncounterType = getPregnancyDeliveryVisitEncounterType();

		Calendar calendar = Calendar.getInstance();
		Date currentDate = calendar.getTime();
		calendar.add(Calendar.DATE, 2 * -7);
		Date twoWeeksPriorDate = calendar.getTime();

		Integer maxResults = getMaxQueryResults();

		return motechService.getEncounters(facility, deliveryEncounterType,
				twoWeeksPriorDate, currentDate, maxResults);
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
		Integer maxResults = getMaxQueryResults();

		return motechService.getActivePregnanciesDueDateObs(facility,
				fromDueDate, toDueDate, pregnancyDueDateConcept,
				pregnancyConcept, pregnancyStatusConcept, maxResults);
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
						existingDueDateObsId, null, null, null);
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
	public void scheduleInfoMessages(String messageKey, String messageKeyA,
			String messageKeyB, String messageKeyC,
			MessageProgramEnrollment enrollment, Date messageDate,
			boolean userPreferenceBased, Date currentDate) {

		PersonService personService = contextService.getPersonService();

		// TODO: Assumes recipient is person in enrollment
		Integer messageRecipientId = enrollment.getPersonId();
		Person recipient = personService.getPerson(messageRecipientId);
		MediaType mediaType = getPersonMediaType(recipient);

		// Schedule multiple messages if media type preference is text, or no
		// preference exists, using A/B/C message keys
		if (mediaType == MediaType.TEXT) {
			scheduleMultipleInfoMessages(messageKeyA, messageKeyB, messageKeyC,
					enrollment, messageDate, userPreferenceBased, currentDate);
		} else {
			scheduleSingleInfoMessage(messageKey, enrollment, messageDate,
					userPreferenceBased, currentDate);
		}
	}

	void scheduleMultipleInfoMessages(String messageKeyA, String messageKeyB,
			String messageKeyC, MessageProgramEnrollment enrollment,
			Date messageDate, boolean userPreferenceBased, Date currentDate) {
		// Return existing message definitions
		MessageDefinition messageDefinitionA = this
				.getMessageDefinition(messageKeyA);
		MessageDefinition messageDefinitionB = this
				.getMessageDefinition(messageKeyB);
		MessageDefinition messageDefinitionC = this
				.getMessageDefinition(messageKeyC);

		// TODO: Assumes recipient is person in enrollment
		Integer messageRecipientId = enrollment.getPersonId();

		// Expecting message date to already be preference adjusted
		// Determine dates for second and third messages
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(messageDate);
		calendar.add(Calendar.DATE, 2);
		Date messageDateB = calendar.getTime();
		calendar.add(Calendar.DATE, 2);
		Date messageDateC = calendar.getTime();

		MessageDefDate messageA = new MessageDefDate(messageDefinitionA,
				messageDate);
		MessageDefDate messageB = new MessageDefDate(messageDefinitionB,
				messageDateB);
		MessageDefDate messageC = new MessageDefDate(messageDefinitionC,
				messageDateC);
		MessageDefDate[] messageDefDates = { messageA, messageB, messageC };

		// Cancel any unsent messages for the same enrollment and not matching
		// the messages to schedule
		this.removeUnsentMessages(messageRecipientId, enrollment,
				messageDefDates);

		// Create new scheduled message (with pending attempt) for all 3
		// messages, for this enrollment, if no matching message already exist
		this.createScheduledMessage(messageRecipientId, messageDefinitionA,
				enrollment, messageDate, currentDate);
		this.createScheduledMessage(messageRecipientId, messageDefinitionB,
				enrollment, messageDateB, currentDate);
		this.createScheduledMessage(messageRecipientId, messageDefinitionC,
				enrollment, messageDateC, currentDate);
	}

	void scheduleSingleInfoMessage(String messageKey,
			MessageProgramEnrollment enrollment, Date messageDate,
			boolean userPreferenceBased, Date currentDate) {

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
				enrollment, messageDate, currentDate);
	}

	public ScheduledMessage scheduleCareMessage(String messageKey,
			MessageProgramEnrollment enrollment, Date messageDate,
			boolean userPreferenceBased, String care, Date currentDate) {
		// Return existing message definition
		MessageDefinition messageDefinition = this
				.getMessageDefinition(messageKey);

		// TODO: Assumes recipient is person in enrollment
		Integer messageRecipientId = enrollment.getPersonId();

		// Create new scheduled message (with pending attempt) for enrollment
		// Does not check if one already exists
		return this.createCareScheduledMessage(messageRecipientId,
				messageDefinition, enrollment, messageDate, care,
				userPreferenceBased, currentDate);
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
			MessageDefDate[] messageDefDates) {
		MotechService motechService = contextService.getMotechService();
		// Get Messages matching the recipient, enrollment, and status, but
		// not matching the list of message definitions and message dates
		List<Message> unsentMessages = motechService.getMessages(recipientId,
				enrollment, messageDefDates, MessageStatus.SHOULD_ATTEMPT);
		log.debug("Unsent messages found during scheduling: "
				+ unsentMessages.size());

		for (Message unsentMessage : unsentMessages) {
			unsentMessage.setAttemptStatus(MessageStatus.CANCELLED);
			motechService.saveMessage(unsentMessage);

			log.debug("Message cancelled to schedule new: Id: "
					+ unsentMessage.getId());
		}
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
			Date attemptDate, Date maxAttemptDate, boolean userPreferenceBased,
			Date currentDate) {

		MotechService motechService = contextService.getMotechService();
		PersonService personService = contextService.getPersonService();

		MessageDefinition messageDefinition = scheduledMessage.getMessage();
		Person recipient = personService.getPerson(scheduledMessage
				.getRecipientId());

		Date adjustedMessageDate = adjustCareMessageDate(recipient,
				attemptDate, userPreferenceBased, currentDate);
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
			boolean userPreferenceBased, Date currentDate) {

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
							attemptDate, currentDate, true);
				} else {
					attemptDate = adjustForBlackout(attemptDate);
				}
				if (!attemptDate.equals(recentMessage.getAttemptDate())) {
					// Recompute from original scheduled message date
					// Allows possibly adjusting to an earlier week or day
					Date adjustedMessageDate = adjustCareMessageDate(recipient,
							scheduledMessage.getScheduledFor(),
							userPreferenceBased, currentDate);

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

		return determinePreferredMessageDate(recipient, messageDate, null,
				false);
	}

	private void createScheduledMessage(Integer recipientId,
			MessageDefinition messageDefinition,
			MessageProgramEnrollment enrollment, Date messageDate,
			Date currentDate) {

		MotechService motechService = contextService.getMotechService();

		List<ScheduledMessage> scheduledMessages = motechService
				.getScheduledMessages(recipientId, messageDefinition,
						enrollment, messageDate);

		// Add scheduled message and message attempt is none matching exists
		if (scheduledMessages.isEmpty()) {
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
		} else {
			if (scheduledMessages.size() > 1 && log.isWarnEnabled()) {
				log.warn("Multiple matching scheduled messages: recipient: "
						+ recipientId + ", enrollment: " + enrollment.getId()
						+ ", message key: " + messageDefinition.getMessageKey()
						+ ", date: " + messageDate);
			}
			// Add message attempt to existing scheduled message if not exist
			boolean matchFound = false;
			ScheduledMessage scheduledMessage = scheduledMessages.get(0);
			for (Message message : scheduledMessage.getMessageAttempts()) {
				if ((MessageStatus.SHOULD_ATTEMPT == message.getAttemptStatus()
						|| MessageStatus.ATTEMPT_PENDING == message
								.getAttemptStatus()
						|| MessageStatus.DELIVERED == message
								.getAttemptStatus() || MessageStatus.REJECTED == message
						.getAttemptStatus())
						&& messageDate.equals(message.getAttemptDate())) {
					matchFound = true;
					break;
				}
			}
			if (!matchFound && !currentDate.after(messageDate)) {
				if (log.isDebugEnabled()) {
					log.debug("Creating Message: recipient: " + recipientId
							+ ", enrollment: " + enrollment.getId()
							+ ", message key: "
							+ messageDefinition.getMessageKey() + ", date: "
							+ messageDate);
				}

				Message message = messageDefinition
						.createMessage(scheduledMessage);
				message.setAttemptDate(messageDate);
				scheduledMessage.getMessageAttempts().add(message);

				motechService.saveScheduledMessage(scheduledMessage);
			}
		}
	}

	private ScheduledMessage createCareScheduledMessage(Integer recipientId,
			MessageDefinition messageDefinition,
			MessageProgramEnrollment enrollment, Date messageDate, String care,
			boolean userPreferenceBased, Date currentDate) {

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
				messageDate, userPreferenceBased, currentDate);

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
			boolean userPreferenceBased, Date currentDate) {
		Date adjustedDate = verifyFutureDate(messageDate);
		if (userPreferenceBased) {
			adjustedDate = determinePreferredMessageDate(person, adjustedDate,
					currentDate, true);
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

	private String generateMotechId() {
		PatientIdentifierType motechIdType = getMotechPatientIdType();
		return generateId(MotechConstants.IDGEN_SEQ_ID_GEN_MOTECH_ID,
				motechIdType);
	}

	private String generateStaffId() {
		PatientIdentifierType staffIdType = getStaffPatientIdType();
		return generateId(MotechConstants.IDGEN_SEQ_ID_GEN_STAFF_ID,
				staffIdType);
	}

	private String generateCommunityId() {
		PatientIdentifierType communityIdType = getCommunityPatientIdType();
		return generateId(MotechConstants.IDGEN_SEQ_ID_GEN_COMMUNITY_ID,
				communityIdType);
	}

	private String generateFacilityId() {
		PatientIdentifierType facilityIdType = getFacilityPatientIdType();
		return generateId(MotechConstants.IDGEN_SEQ_ID_GEN_FACILITY_ID,
				facilityIdType);
	}

	private String generateId(String generatorName,
			PatientIdentifierType identifierType) {
		String id = null;
		if (generatorName == null || identifierType == null) {
			log.error("Unable to generate ID using " + generatorName + " for "
					+ identifierType);
			return null;
		}
		try {
			IdentifierSourceService idSourceService = contextService
					.getIdentifierSourceService();

			SequentialIdentifierGenerator idGenerator = getSeqIdGenerator(
					generatorName, identifierType);
			id = idSourceService.generateIdentifier(idGenerator,
					MotechConstants.IDGEN_SEQ_ID_GEN_MOTECH_ID_GEN_COMMENT);

		} catch (Exception e) {
			log.error("Error generating " + identifierType + " using "
					+ generatorName + " in Idgen module", e);
		}
		return id;
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
			log.error("Error retrieving " + name + " for " + identifierType
					+ " in Idgen module", e);
		}
		return idGenerator;
	}

	/* SaveObsAdvice method */
	public void updateMessageProgramState(Integer personId, String conceptName) {

		// Only determine message program state for active enrolled programs
		// concerned with an observed concept and matching the concept of this
		// obs

		MotechService motechService = contextService.getMotechService();

		List<MessageProgramEnrollment> patientActiveEnrollments = motechService
				.getActiveMessageProgramEnrollments(personId, null, null, null,
						null, null);

		Date currentDate = new Date();

		for (MessageProgramEnrollment enrollment : patientActiveEnrollments) {
			MessageProgram program = this.getMessageProgram(enrollment
					.getProgram());

			if (program.getConceptName() != null) {
				if (program.getConceptName().equals(conceptName)) {
					log
							.debug("Save Obs - Obs matches Program concept, update Program: "
									+ enrollment.getProgram());

					program.determineState(enrollment, currentDate);
				}
			}
		}
	}

	/* MessageProgramUpdateTask method */
	public TaskDefinition updateAllMessageProgramsState(Integer batchSize,
			Long batchPreviousId, Long batchMaxId) {

		MotechService motechService = contextService.getMotechService();
		SchedulerService schedulerService = contextService
				.getSchedulerService();

		if (batchMaxId == null) {
			batchMaxId = motechService.getMaxMessageProgramEnrollmentId();
		}

		List<MessageProgramEnrollment> activeEnrollments = motechService
				.getActiveMessageProgramEnrollments(null, null, null,
						batchPreviousId, batchMaxId, batchSize);

		Date currentDate = new Date();

		for (MessageProgramEnrollment enrollment : activeEnrollments) {
			MessageProgram program = this.getMessageProgram(enrollment
					.getProgram());

			log.debug("MessageProgram Update - Update State: enrollment: "
					+ enrollment.getId());

			program.determineState(enrollment, currentDate);

			batchPreviousId = enrollment.getId();
			if (batchPreviousId >= batchMaxId) {
				log.info("Completed updating all enrollments up to max: "
						+ batchMaxId);
				batchMaxId = null;
				batchPreviousId = null;
				break;
			}
		}

		// Update task properties
		TaskDefinition task = schedulerService
				.getTaskByName(MotechConstants.TASK_MESSAGEPROGRAM_UPDATE);
		if (task != null) {
			Map<String, String> properties = task.getProperties();
			if (batchPreviousId != null) {
				properties.put(MotechConstants.TASK_PROPERTY_BATCH_PREVIOUS_ID,
						batchPreviousId.toString());
			} else {
				properties
						.remove(MotechConstants.TASK_PROPERTY_BATCH_PREVIOUS_ID);
			}
			if (batchMaxId != null) {
				properties.put(MotechConstants.TASK_PROPERTY_BATCH_MAX_ID,
						batchMaxId.toString());
			} else {
				properties.remove(MotechConstants.TASK_PROPERTY_BATCH_MAX_ID);
			}
			schedulerService.saveTask(task);
		}
		return task;
	}

	public void updateAllCareSchedules() {
		PatientService patientService = contextService.getPatientService();
		List<Patient> patients = patientService.getAllPatients();
		log
				.info("Updating care schedules for " + patients.size()
						+ " patients");

		for (Patient patient : patients) {
			// Adds patient to transaction synchronization using advice
			patientService.savePatient(patient);
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
				List<ExpectedObs> upcomingObs = getUpcomingExpectedObs(
						facility, careGroups, startDate, endDate);
				if (!upcomingEncounters.isEmpty() || !upcomingObs.isEmpty()) {
					Care[] upcomingCares = modelConverter
							.upcomingToWebServiceCares(upcomingEncounters,
									upcomingObs, true);

					sendStaffUpcomingCareMessage(messageId, phoneNumber,
							mediaType, deliveryDate, null, upcomingCares);
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

			if (!shouldAttemptMessages.isEmpty()) {
				PatientMessage[] messages = constructPatientMessages(
						shouldAttemptMessages, sendImmediate);

				if (messages.length > 0) {
					mobileService.sendPatientMessages(messages);
				}
			}
		} catch (Exception e) {
			log.error("Failure to send patient messages", e);
		}
	}

	public PatientMessage[] constructPatientMessages(List<Message> messages,
			boolean sendImmediate) {
		MotechService motechService = contextService.getMotechService();

		List<PatientMessage> patientMessages = new ArrayList<PatientMessage>();

		for (Message message : messages) {
			PatientMessage patientMessage = constructPatientMessage(message);
			if (patientMessage != null) {
				if (sendImmediate) {
					patientMessage.setStartDate(null);
					patientMessage.setEndDate(null);
				}
				patientMessages.add(patientMessage);

				message.setAttemptStatus(MessageStatus.ATTEMPT_PENDING);
			} else {
				message.setAttemptStatus(MessageStatus.REJECTED);
			}
			motechService.saveMessage(message);
		}
		return patientMessages.toArray(new PatientMessage[patientMessages
				.size()]);
	}

	public PatientMessage constructPatientMessage(Message message) {
		try {
			PersonService personService = contextService.getPersonService();
			PatientService patientService = contextService.getPatientService();

			Long notificationType = message.getSchedule().getMessage()
					.getPublicId();
			Integer recipientId = message.getSchedule().getRecipientId();
			Person person = personService.getPerson(recipientId);

			String phoneNumber = getPersonPhoneNumber(person);

			// Cancel message if phone number is considered troubled
			if (isPhoneTroubled(phoneNumber)) {
				if (log.isDebugEnabled()) {
					log.debug("Attempt to send to Troubled Phone, Phone: "
							+ phoneNumber + ", Notification: "
							+ notificationType);
				}
			} else {
				if (log.isDebugEnabled()) {
					log.debug("Scheduled Message, Phone: " + phoneNumber
							+ ", Notification: " + notificationType);
				}

				String messageId = message.getPublicId();
				MediaType mediaType = getPersonMediaType(person);
				String languageCode = getPersonLanguageCode(person);
				NameValuePair[] personalInfo = new NameValuePair[0];

				Date messageStartDate = message.getAttemptDate();
				Date messageEndDate = null;

				Patient patient = patientService.getPatient(recipientId);

				if (patient != null) {
					ContactNumberType contactNumberType = getPersonPhoneType(person);
					String motechId = patient.getPatientIdentifier(
							MotechConstants.PATIENT_IDENTIFIER_MOTECH_ID)
							.getIdentifier();

					PatientMessage patientMessage = new PatientMessage();
					patientMessage.setMessageId(messageId);
					patientMessage.setPersonalInfo(personalInfo);
					patientMessage.setPatientNumber(phoneNumber);
					patientMessage.setPatientNumberType(contactNumberType);
					patientMessage.setLangCode(languageCode);
					patientMessage.setMediaType(mediaType);
					patientMessage.setNotificationType(notificationType);
					patientMessage.setStartDate(messageStartDate);
					patientMessage.setEndDate(messageEndDate);
					patientMessage.setRecipientId(motechId);
					return patientMessage;

				} else {
					log
							.error("Attempt to send message to non-existent Patient: "
									+ recipientId);
				}
			}
		} catch (Exception e) {
			log.error("Error creating patient message", e);
		}
		return null;
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
			Date messageEndDate, Care[] cares) {

		try {
			org.motechproject.ws.MessageStatus messageStatus = mobileService
					.sendBulkCaresMessage(messageId, phoneNumber, cares,
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
						null, null, null, null, null);

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
				.getActiveMessageProgramEnrollments(personId, program, obsId,
						null, null, null);
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
				.getActiveMessageProgramEnrollments(personId, program, obsId,
						null, null, null);
		for (MessageProgramEnrollment enrollment : enrollments) {
			removeMessageProgramEnrollment(enrollment);
		}
	}

	private void removeAllMessageProgramEnrollments(Integer personId) {
		MotechService motechService = contextService.getMotechService();

		List<MessageProgramEnrollment> enrollments = motechService
				.getActiveMessageProgramEnrollments(personId, null, null, null,
						null, null);

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
		if (phoneNumberAttr != null
				&& StringUtils.isNotEmpty(phoneNumberAttr.getValue())) {
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
		if (languageAttr != null
				&& StringUtils.isNotEmpty(languageAttr.getValue())) {
			return languageAttr.getValue();
		}
		log.debug("No language found for Person id: " + person.getPersonId());
		return null;
	}

	public ContactNumberType getPersonPhoneType(Person person) {
		PersonAttribute phoneTypeAttr = person
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_PHONE_TYPE);
		if (phoneTypeAttr != null
				&& StringUtils.isNotEmpty(phoneTypeAttr.getValue())) {
			try {
				return ContactNumberType.valueOf(phoneTypeAttr.getValue());
			} catch (Exception e) {
				log.error("Unable to parse phone type: "
						+ phoneTypeAttr.getValue() + ", for Person ID:"
						+ person.getPersonId(), e);
			}
		}
		log.debug("No contact number type found for Person id: "
				+ person.getPersonId());
		return null;
	}

	public MediaType getPersonMediaType(Person person) {
		PersonAttribute mediaTypeAttr = person
				.getAttribute(MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE);
		if (mediaTypeAttr != null
				&& StringUtils.isNotEmpty(mediaTypeAttr.getValue())) {
			try {
				return MediaType.valueOf(mediaTypeAttr.getValue());
			} catch (Exception e) {
				log.error("Unable to parse media type: "
						+ mediaTypeAttr.getValue() + ", for Person ID:"
						+ person.getPersonId(), e);
			}
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
		if (dayAttr != null && StringUtils.isNotEmpty(dayAttr.getValue())) {
			try {
				day = DayOfWeek.valueOf(dayAttr.getValue());
			} catch (Exception e) {
				log.error("Unable to parse day of week: " + dayAttr.getValue()
						+ ", for Person ID:" + person.getPersonId(), e);
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
		if (timeAttr != null && StringUtils.isNotEmpty(timeAttr.getValue())) {
			SimpleDateFormat timeFormat = new SimpleDateFormat(
					MotechConstants.TIME_FORMAT_DELIVERY_TIME);
			try {
				time = timeFormat.parse(timeAttr.getValue());
			} catch (Exception e) {
				log.error("Unable to parse time of day: " + timeAttr.getValue()
						+ ", for Person ID:" + person.getPersonId(), e);
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

	public Integer getMaxQueryResults() {
		String maxResultsProperty = getMaxQueryResultsProperty();
		if (maxResultsProperty != null) {
			return Integer.parseInt(maxResultsProperty);
		}
		log.error("Max Query Results Property not found");
		return null;
	}

	public Date determinePreferredMessageDate(Person person, Date messageDate,
			Date currentDate, boolean checkInFuture) {
		Calendar calendar = Calendar.getInstance();
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
			if (checkInFuture && calendar.getTime().before(currentDate)) {
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
		if (date.before(blackoutCalendar.getTime())) {
			// Remove a day if blackout start date before the message date
			blackoutCalendar.add(Calendar.DATE, -1);
		}
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
		if (date.before(blackoutCalendar.getTime())) {
			// Remove a day if blackout start date before the message date
			blackoutCalendar.add(Calendar.DATE, -1);
		}
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

	public Integer getMotherMotechId(Patient patient) {
		Relationship motherRelation = getMotherRelationship(patient);
		if (motherRelation != null) {
			Person mother = motherRelation.getPersonA();
			return getMotechId(mother.getPersonId());
		}
		return null;
	}

	public Community saveCommunity(Community community) {
		if (community.getCommunityId() == null) {
			community.setCommunityId(Integer.parseInt(generateCommunityId()));
		}

		return contextService.getMotechService().saveCommunity(community);
	}

	public Facility saveNewFacility(Facility facility) {
		facility.setFacilityId(Integer.parseInt(generateFacilityId()));
		return contextService.getMotechService().saveFacility(facility);
	}

	public Relationship getMotherRelationship(Patient patient) {
		PersonService personService = contextService.getPersonService();
		RelationshipType parentChildtype = personService
				.getRelationshipTypeByName(MotechConstants.RELATIONSHIP_TYPE_PARENT_CHILD);
		List<Relationship> parentRelations = personService.getRelationships(
				null, patient, parentChildtype);
		if (!parentRelations.isEmpty()) {
			if (parentRelations.size() > 1) {
				log.warn("Multiple parent relationships found for id: "
						+ patient.getPersonId());
			}
			return parentRelations.get(0);
		}
		return null;
	}

	public Integer getMotechId(Integer patientId) {
		PatientService patientService = contextService.getPatientService();
		Patient patient = patientService.getPatient(patientId);
		if (patient == null) {
			return null;
		}
		PatientIdentifier motechPatientId = patient
				.getPatientIdentifier(MotechConstants.PATIENT_IDENTIFIER_MOTECH_ID);
		Integer motechId = null;
		if (motechPatientId != null) {
			try {
				motechId = Integer.parseInt(motechPatientId.getIdentifier());
			} catch (Exception e) {
				log.error("Unable to parse Motech ID: "
						+ motechPatientId.getIdentifier() + ", for Patient ID:"
						+ patientId, e);
			}
		}
		return motechId;
	}

	public PatientIdentifierType getMotechPatientIdType() {
		return contextService.getPatientService()
				.getPatientIdentifierTypeByName(
						MotechConstants.PATIENT_IDENTIFIER_MOTECH_ID);
	}

	public PatientIdentifierType getStaffPatientIdType() {
		return contextService.getPatientService()
				.getPatientIdentifierTypeByName(
						MotechConstants.PATIENT_IDENTIFIER_STAFF_ID);
	}

	public PatientIdentifierType getFacilityPatientIdType() {
		return contextService.getPatientService()
				.getPatientIdentifierTypeByName(
						MotechConstants.PATIENT_IDENTIFIER_FACILITY_ID);
	}

	public PatientIdentifierType getCommunityPatientIdType() {
		return contextService.getPatientService()
				.getPatientIdentifierTypeByName(
						MotechConstants.PATIENT_IDENTIFIER_COMMUNITY_ID);
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

	public EncounterType getPatientRegistrationEncounterType() {
		return contextService.getEncounterService().getEncounterType(
				MotechConstants.ENCOUNTER_TYPE_PATIENTREGVISIT);
	}

	public EncounterType getPatientHistoryEncounterType() {
		return contextService.getEncounterService().getEncounterType(
				MotechConstants.ENCOUNTER_TYPE_PATIENTHISTORY);
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

	public Concept getIPTiDoseConcept() {
		return contextService
				.getConceptService()
				.getConcept(
						MotechConstants.CONCEPT_INTERMITTENT_PREVENTATIVE_TREATMENT_INFANTS_DOSE);
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

	public Concept getLochiaFoulConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_LOCHIA_FOUL_ODOUR);
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

	public Concept getTraceConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_TRACE);
	}

	public Concept getANCRegistrationNumberConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_ANC_REG_NUMBER);
	}

	public Concept getCWCRegistrationNumberConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_CWC_REG_NUMBER);
	}

	public Concept getInsuredConcept() {
		return contextService.getConceptService().getConcept(
				MotechConstants.CONCEPT_INSURED);
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

	public String getMaxQueryResultsProperty() {
		return contextService.getAdministrationService().getGlobalProperty(
				MotechConstants.GLOBAL_PROPERTY_MAX_QUERY_RESULTS);
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

	public boolean isValidMotechIdCheckDigit(Integer motechId) {
		if (motechId == null) {
			return false;
		}
		String motechIdString = motechId.toString();
		MotechIdVerhoeffValidator validator = new MotechIdVerhoeffValidator();
		boolean isValid = false;
		try {
			isValid = validator.isValid(motechIdString);
		} catch (Exception e) {
		}
		return isValid;
	}

	public boolean isValidIdCheckDigit(Integer idWithCheckDigit) {
		if (idWithCheckDigit == null) {
			return false;
		}
		String idWithCheckDigitString = idWithCheckDigit.toString();
		VerhoeffValidator validator = new VerhoeffValidator();
		boolean isValid = false;
		try {
			isValid = validator.isValid(idWithCheckDigitString);
		} catch (Exception e) {
		}
		return isValid;
	}
}
