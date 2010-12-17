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
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.model.Community;
import org.motechproject.server.model.GeneralOutpatientEncounter;
import org.motechproject.server.model.MessageProgramEnrollment;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.MotechService;
import org.motechproject.server.svc.BirthOutcomeChild;
import org.motechproject.server.svc.IdBean;
import org.motechproject.server.svc.MessageBean;
import org.motechproject.server.svc.OpenmrsBean;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.server.util.GenderTypeConverter;
import org.motechproject.server.util.MotechConstants;
import org.motechproject.ws.BirthOutcome;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.DayOfWeek;
import org.motechproject.ws.Gender;
import org.motechproject.ws.HIVResult;
import org.motechproject.ws.HowLearned;
import org.motechproject.ws.InterestReason;
import org.motechproject.ws.MediaType;
import org.motechproject.ws.RegistrantType;
import org.motechproject.ws.RegistrationMode;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.transaction.annotation.Transactional;

/**
 * An implementation of the RegistrarBean interface, implemented using a mix of
 * OpenMRS and module defined services.
 */
public class RegistrarBeanImpl implements RegistrarBean {

	private static Log log = LogFactory.getLog(RegistrarBeanImpl.class);

	private ContextService contextService;
	private OpenmrsBean openmrsBean;
	private IdBean idBean;
	private MessageBean messageBean;

	public void setContextService(ContextService contextService) {
		this.contextService = contextService;
	}

	public void setOpenmrsBean(OpenmrsBean openmrsBean) {
		this.openmrsBean = openmrsBean;
	}

	public void setIdBean(IdBean idBean) {
		this.idBean = idBean;
	}

	public void setMessageBean(MessageBean messageBean) {
		this.messageBean = messageBean;
	}

	public User registerStaff(String firstName, String lastName, String phone,
			String staffType) {

		UserService userService = contextService.getUserService();

		User staff = new User();

		// Set staff id to generated id check digit
		staff.setSystemId(idBean.generateStaffId());

		staff.setGender(MotechConstants.GENDER_UNKNOWN_OPENMRS);

		PersonName name = new PersonName(firstName, null, lastName);
		staff.addName(name);

		if (phone != null) {
			PersonAttributeType phoneNumberAttrType = openmrsBean
					.getPhoneNumberAttributeType();
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

		Location facility = openmrsBean.getGhanaLocation();
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
				community = openmrsBean.getCommunityByPatient(mother);
			}
			if (phoneNumber == null) {
				phoneNumber = openmrsBean.getPersonPhoneNumber(mother);
			}
			if (ownership == null) {
				ownership = openmrsBean.getPersonPhoneType(mother);
			}
			if (format == null) {
				format = openmrsBean.getPersonMediaType(mother);
			}
			if (language == null) {
				language = openmrsBean.getPersonLanguageCode(mother);
			}
			if (dayOfWeek == null) {
				dayOfWeek = openmrsBean.getPersonMessageDayOfWeek(mother);
			}
			if (timeOfDay == null) {
				timeOfDay = openmrsBean.getPersonMessageTimeOfDay(mother);
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
				messageBean.addMessageProgramEnrollment(patient.getPatientId(),
						infoMessageProgramName, referenceDateObsId);
			}

			// Lookup patient community if not provided
			// Only enroll patient in care messages if in KNDW district
			if (community == null) {
				community = openmrsBean.getCommunityByPatient(patient);
			}
			if (community != null
					&& community.getFacility() != null
					&& community.getFacility().getLocation() != null
					&& MotechConstants.LOCATION_KASSENA_NANKANA_WEST
							.equals(community.getFacility().getLocation()
									.getCountyDistrict())) {

				messageBean.addMessageProgramEnrollment(patient.getPatientId(),
						"Expected Care Message Program", null);
			}
		}
	}

	private Integer storeMessagesWeekObs(Patient patient,
			Integer messagesStartWeek) {
		ObsService obsService = contextService.getObsService();

		Location ghanaLocation = openmrsBean.getGhanaLocation();
		Date currentDate = new Date();

		Calendar calendar = Calendar.getInstance();
		// Convert weeks to days, plus one day
		calendar.add(Calendar.DATE, (messagesStartWeek * -7) + 1);
		Date referenceDate = calendar.getTime();

		Obs refDateObs = createDateValueObs(currentDate, openmrsBean
				.getEnrollmentReferenceDateConcept(), patient, ghanaLocation,
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
			messageBean.addMessageProgramEnrollment(patient.getPatientId(),
					"Demo Minute Message Program", null);
		}
	}

	@Transactional
	public void demoEnrollPatient(Patient patient) {
		messageBean.addMessageProgramEnrollment(patient.getPersonId(),
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
			motechIdString = idBean.generateMotechId();
		} else {
			motechIdString = motechId.toString();
			idBean.excludeMotechId(staff, motechIdString);
		}

		patient.addIdentifier(new PatientIdentifier(motechIdString, openmrsBean
				.getMotechPatientIdType(), openmrsBean.getGhanaLocation()));

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
		encounter.setEncounterType(openmrsBean
				.getPatientRegistrationEncounterType());
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
			attrs.add(new PersonAttribute(openmrsBean
					.getPhoneNumberAttributeType(), phoneNumber));
		}

		if (phoneType != null) {
			attrs.add(new PersonAttribute(openmrsBean
					.getPhoneTypeAttributeType(), phoneType.name()));
		}

		if (mediaType != null) {
			attrs.add(new PersonAttribute(openmrsBean
					.getMediaTypeAttributeType(), mediaType.name()));
		}

		if (language != null) {
			attrs.add(new PersonAttribute(openmrsBean
					.getLanguageAttributeType(), language));
		}

		if (dayOfWeek != null) {
			attrs.add(new PersonAttribute(openmrsBean
					.getDeliveryDayAttributeType(), dayOfWeek.name()));
		}

		if (timeOfDay != null) {
			SimpleDateFormat formatter = new SimpleDateFormat(
					MotechConstants.TIME_FORMAT_DELIVERY_TIME);
			attrs.add(new PersonAttribute(openmrsBean
					.getDeliveryTimeAttributeType(), formatter
					.format(timeOfDay)));
		}

		if (howLearned != null) {
			attrs.add(new PersonAttribute(openmrsBean
					.getHowLearnedAttributeType(), howLearned.name()));
		}

		if (interestReason != null) {
			attrs.add(new PersonAttribute(openmrsBean
					.getInterestReasonAttributeType(), interestReason.name()));
		}

		if (insured != null) {
			attrs.add(new PersonAttribute(
					openmrsBean.getInsuredAttributeType(), insured.toString()));
		}

		if (nhis != null) {
			attrs.add(new PersonAttribute(openmrsBean
					.getNHISNumberAttributeType(), nhis));
		}

		if (nhisExpDate != null) {
			SimpleDateFormat formatter = new SimpleDateFormat(
					MotechConstants.DATE_FORMAT);
			attrs.add(new PersonAttribute(openmrsBean
					.getNHISExpirationDateAttributeType(), formatter
					.format(nhisExpDate)));
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
			messageBean.removeAllMessageProgramEnrollments(patient
					.getPatientId());
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

		Relationship motherRelationship = openmrsBean
				.getMotherRelationship(patient);
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

		Community currentCommunity = openmrsBean.getCommunityByPatient(patient);
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
		if (community != null
				&& openmrsBean.getCommunityByPatient(patient) == null) {
			community.getResidents().add(patient);
			currentCommunity = community;
		}

		setPatientAttributes(patient, phoneNumber, ownership, format, language,
				dayOfWeek, timeOfDay, null, null, insured, nhis, nhisExpires);

		patientService.savePatient(patient);

		Integer dueDateObsId = null;
		if (expDeliveryDate != null) {
			Obs pregnancy = openmrsBean.getActivePregnancy(patient
					.getPatientId());
			Obs dueDateObs = openmrsBean.getActivePregnancyDueDateObs(patient
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
			messageBean.removeAllMessageProgramEnrollments(patient
					.getPatientId());
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

		Location facility = openmrsBean.getGhanaLocation();
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
		encounter.setEncounterType(openmrsBean
				.getPregnancyRegistrationVisitEncounterType());
		encounter.setEncounterDatetime(date);
		encounter.setPatient(patient);
		encounter.setLocation(facility);
		encounter.setProvider(staff);
		encounter = encounterService.saveEncounter(encounter);

		Obs pregnancyObs = createObs(date, openmrsBean.getPregnancyConcept(),
				patient, facility, encounter, null);

		Obs pregnancyStatusObs = createBooleanValueObs(date, openmrsBean
				.getPregnancyStatusConcept(), patient, facility, Boolean.TRUE,
				encounter, null);
		pregnancyObs.addGroupMember(pregnancyStatusObs);

		Obs dueDateObs = null;
		if (dueDate != null) {
			dueDateObs = createDateValueObs(date, openmrsBean
					.getDueDateConcept(), patient, facility, dueDate,
					encounter, null);
			pregnancyObs.addGroupMember(dueDateObs);
		}

		if (dueDateConfirmed != null) {
			Obs dueDateConfirmedObs = createBooleanValueObs(date, openmrsBean
					.getDueDateConfirmedConcept(), patient, facility,
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
		Obs pregnancyObs = openmrsBean.getActivePregnancy(patient
				.getPatientId());

		Integer pregnancyDueDateObsId = null;
		if (pregnancyObs != null) {
			log.warn("Entering Pregnancy for patient with active pregnancy, "
					+ "patient id=" + patient.getPatientId());

			Obs pregnancyDueDateObs = openmrsBean.getActivePregnancyDueDateObs(
					patient.getPatientId(), pregnancyObs);
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
		Location ghanaLocation = openmrsBean.getGhanaLocation();

		Encounter historyEncounter = new Encounter();
		historyEncounter.setEncounterType(openmrsBean
				.getPatientHistoryEncounterType());
		historyEncounter.setEncounterDatetime(date);
		historyEncounter.setPatient(patient);
		historyEncounter.setLocation(ghanaLocation);
		historyEncounter.setProvider(staff);

		if (lastIPT != null && lastIPTDate != null) {
			Obs iptDoseObs = createNumericValueObs(lastIPTDate, openmrsBean
					.getIPTDoseConcept(), patient, ghanaLocation, lastIPT,
					historyEncounter, null);
			historyEncounter.addObs(iptDoseObs);
		}
		if (lastTT != null && lastTTDate != null) {
			Obs ttDoseObs = createNumericValueObs(lastTTDate, openmrsBean
					.getTetanusDoseConcept(), patient, ghanaLocation, lastTT,
					historyEncounter, null);
			historyEncounter.addObs(ttDoseObs);
		}
		if (bcgDate != null) {
			Obs bcgObs = createConceptValueObs(bcgDate, openmrsBean
					.getImmunizationsOrderedConcept(), patient, ghanaLocation,
					openmrsBean.getBCGConcept(), historyEncounter, null);
			historyEncounter.addObs(bcgObs);
		}
		if (lastOPV != null && lastOPVDate != null) {
			Obs opvDoseObs = createNumericValueObs(lastOPVDate, openmrsBean
					.getOPVDoseConcept(), patient, ghanaLocation, lastOPV,
					historyEncounter, null);
			historyEncounter.addObs(opvDoseObs);
		}
		if (lastPenta != null && lastPentaDate != null) {
			Obs pentaDoseObs = createNumericValueObs(lastPentaDate, openmrsBean
					.getPentaDoseConcept(), patient, ghanaLocation, lastPenta,
					historyEncounter, null);
			historyEncounter.addObs(pentaDoseObs);
		}
		if (measlesDate != null) {
			Obs measlesObs = createConceptValueObs(measlesDate, openmrsBean
					.getImmunizationsOrderedConcept(), patient, ghanaLocation,
					openmrsBean.getMeaslesConcept(), historyEncounter, null);
			historyEncounter.addObs(measlesObs);
		}
		if (yellowFeverDate != null) {
			Obs yellowFeverObs = createConceptValueObs(yellowFeverDate,
					openmrsBean.getImmunizationsOrderedConcept(), patient,
					ghanaLocation, openmrsBean.getYellowFeverConcept(),
					historyEncounter, null);
			historyEncounter.addObs(yellowFeverObs);
		}
		if (lastIPTI != null && lastIPTIDate != null) {
			Obs iptiObs = createNumericValueObs(lastIPTIDate, openmrsBean
					.getIPTiDoseConcept(), patient, ghanaLocation, lastIPTI,
					historyEncounter, null);
			historyEncounter.addObs(iptiObs);
		}
		if (lastVitaminADate != null) {
			Obs vitaminAObs = createConceptValueObs(lastVitaminADate,
					openmrsBean.getImmunizationsOrderedConcept(), patient,
					ghanaLocation, openmrsBean.getVitaminAConcept(),
					historyEncounter, null);
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
		encounter.setEncounterType(openmrsBean
				.getANCRegistrationEncounterType());
		encounter.setEncounterDatetime(date);
		encounter.setPatient(patient);
		encounter.setLocation(facility);
		encounter.setProvider(staff);

		if (ancRegNumber != null) {
			Obs ancRegNumObs = createTextValueObs(date, openmrsBean
					.getANCRegistrationNumberConcept(), patient, facility,
					ancRegNumber, encounter, null);
			encounter.addObs(ancRegNumObs);
		}
		if (gravida != null) {
			Obs gravidaObs = createNumericValueObs(date, openmrsBean
					.getGravidaConcept(), patient, facility, gravida,
					encounter, null);
			encounter.addObs(gravidaObs);
		}

		if (parity != null) {
			Obs parityObs = createNumericValueObs(date, openmrsBean
					.getParityConcept(), patient, facility, parity, encounter,
					null);
			encounter.addObs(parityObs);
		}

		if (height != null) {
			Obs heightObs = createNumericValueObs(date, openmrsBean
					.getHeightConcept(), patient, facility, height, encounter,
					null);
			encounter.addObs(heightObs);
		}
		encounterService.saveEncounter(encounter);

		Integer pregnancyDueDateObsId = null;
		Obs pregnancyObs = openmrsBean.getActivePregnancy(patient
				.getPatientId());
		if (pregnancyObs == null) {
			pregnancyDueDateObsId = registerPregnancy(staff, facility, date,
					patient, estDeliveryDate, null);
		} else {
			Obs pregnancyDueDateObs = openmrsBean.getActivePregnancyDueDateObs(
					patient.getPatientId(), pregnancyObs);
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
		encounter.setEncounterType(openmrsBean
				.getCWCRegistrationEncounterType());
		encounter.setEncounterDatetime(date);
		encounter.setPatient(patient);
		encounter.setLocation(facility);
		encounter.setProvider(staff);

		if (cwcRegNumber != null) {
			Obs cwcRegNumObs = createTextValueObs(date, openmrsBean
					.getCWCRegistrationNumberConcept(), patient, facility,
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
		encounter.setEncounterType(openmrsBean.getANCVisitEncounterType());
		encounter.setEncounterDatetime(date);
		encounter.setPatient(patient);
		encounter.setLocation(facility);
		encounter.setProvider(staff);

		Obs pregnancyObs = openmrsBean.getActivePregnancy(patient
				.getPatientId());
		if (pregnancyObs == null) {
			log.warn("Entered ANC visit for patient without active pregnancy, "
					+ "patient id=" + patient.getPatientId());
		}

		if (visitNumber != null) {
			Obs visitNumberObs = createNumericValueObs(date, openmrsBean
					.getVisitNumberConcept(), patient, facility, visitNumber,
					encounter, null);
			encounter.addObs(visitNumberObs);
		}
		if (ancLocation != null) {
			Obs ancLocationObs = createNumericValueObs(date, openmrsBean
					.getANCPNCLocationConcept(), patient, facility,
					ancLocation, encounter, null);
			encounter.addObs(ancLocationObs);
		}
		if (house != null) {
			Obs houseObs = createTextValueObs(date, openmrsBean
					.getANCPNCLocationConcept(), patient, facility, house,
					encounter, null);
			encounter.addObs(houseObs);
		}
		if (community != null) {
			Obs communityObs = createTextValueObs(date, openmrsBean
					.getANCPNCLocationConcept(), patient, facility, community,
					encounter, null);
			encounter.addObs(communityObs);
		}
		if (bpSystolic != null) {
			Obs bpSystolicObs = createNumericValueObs(date, openmrsBean
					.getSystolicBloodPressureConcept(), patient, facility,
					bpSystolic, encounter, null);
			encounter.addObs(bpSystolicObs);
		}
		if (bpDiastolic != null) {
			Obs bpDiastolicObs = createNumericValueObs(date, openmrsBean
					.getDiastolicBloodPressureConcept(), patient, facility,
					bpDiastolic, encounter, null);
			encounter.addObs(bpDiastolicObs);
		}
		if (weight != null) {
			Obs weightObs = createNumericValueObs(date, openmrsBean
					.getWeightConcept(), patient, facility, weight, encounter,
					null);
			encounter.addObs(weightObs);
		}
		if (ttDose != null) {
			Obs ttDoseObs = createNumericValueObs(date, openmrsBean
					.getTetanusDoseConcept(), patient, facility, ttDose,
					encounter, null);
			encounter.addObs(ttDoseObs);
		}
		if (iptDose != null) {
			Obs iptDoseObs = createNumericValueObs(date, openmrsBean
					.getIPTDoseConcept(), patient, facility, iptDose,
					encounter, null);
			encounter.addObs(iptDoseObs);
		}
		if (iptReactive != null) {
			Concept iptReactionValueConcept = null;
			if (Boolean.TRUE.equals(iptReactive)) {
				iptReactionValueConcept = openmrsBean.getReactiveConcept();
			} else {
				iptReactionValueConcept = openmrsBean.getNonReactiveConcept();
			}
			Obs iptReactiveObs = createConceptValueObs(date, openmrsBean
					.getIPTReactionConcept(), patient, facility,
					iptReactionValueConcept, encounter, null);
			encounter.addObs(iptReactiveObs);
		}
		if (itnUse != null) {
			Obs itnUseObs = createBooleanValueObs(date, openmrsBean
					.getITNConcept(), patient, facility, itnUse, encounter,
					null);
			encounter.addObs(itnUseObs);
		}
		if (fht != null) {
			Obs fhtObs = createNumericValueObs(date, openmrsBean
					.getFundalHeightConcept(), patient, facility, fht,
					encounter, null);
			encounter.addObs(fhtObs);
		}
		if (fhr != null) {
			Obs fhrObs = createNumericValueObs(date, openmrsBean
					.getFetalHeartRateConcept(), patient, facility, fhr,
					encounter, null);
			encounter.addObs(fhrObs);
		}
		if (urineTestProtein != null) {
			Concept urineProteinTestValueConcept = null;
			switch (urineTestProtein) {
			case 0:
				urineProteinTestValueConcept = openmrsBean.getNegativeConcept();
				break;
			case 1:
				urineProteinTestValueConcept = openmrsBean.getPositiveConcept();
				break;
			case 2:
				urineProteinTestValueConcept = openmrsBean.getTraceConcept();
				break;
			}
			if (urineProteinTestValueConcept != null) {
				Obs urineTestProteinPositiveObs = createConceptValueObs(date,
						openmrsBean.getUrineProteinTestConcept(), patient,
						facility, urineProteinTestValueConcept, encounter, null);
				encounter.addObs(urineTestProteinPositiveObs);
			}
		}
		if (urineTestGlucose != null) {
			Concept urineGlucoseTestValueConcept = null;
			switch (urineTestGlucose) {
			case 0:
				urineGlucoseTestValueConcept = openmrsBean.getNegativeConcept();
				break;
			case 1:
				urineGlucoseTestValueConcept = openmrsBean.getPositiveConcept();
				break;
			case 2:
				urineGlucoseTestValueConcept = openmrsBean.getTraceConcept();
				break;
			}
			if (urineGlucoseTestValueConcept != null) {
				Obs urineTestProteinPositiveObs = createConceptValueObs(date,
						openmrsBean.getUrineGlucoseTestConcept(), patient,
						facility, urineGlucoseTestValueConcept, encounter, null);
				encounter.addObs(urineTestProteinPositiveObs);
			}
		}
		if (hemoglobin != null) {
			Obs hemoglobinObs = createNumericValueObs(date, openmrsBean
					.getHemoglobinConcept(), patient, facility, hemoglobin,
					encounter, null);
			encounter.addObs(hemoglobinObs);
		}
		if (vdrlReactive != null) {
			Concept vdrlValueConcept = null;
			if (Boolean.TRUE.equals(vdrlReactive)) {
				vdrlValueConcept = openmrsBean.getReactiveConcept();
			} else {
				vdrlValueConcept = openmrsBean.getNonReactiveConcept();
			}
			Obs vdrlReactiveObs = createConceptValueObs(date, openmrsBean
					.getVDRLConcept(), patient, facility, vdrlValueConcept,
					encounter, null);
			encounter.addObs(vdrlReactiveObs);
		}
		if (vdrlTreatment != null) {
			Obs vdrlTreatmentObs = createBooleanValueObs(date, openmrsBean
					.getVDRLTreatmentConcept(), patient, facility,
					vdrlTreatment, encounter, null);
			encounter.addObs(vdrlTreatmentObs);
		}
		if (dewormer != null) {
			Obs dewormerObs = createBooleanValueObs(date, openmrsBean
					.getDewormerConcept(), patient, facility, dewormer,
					encounter, null);
			encounter.addObs(dewormerObs);
		}
		if (maleInvolved != null) {
			Obs maleInvolvedObs = createBooleanValueObs(date, openmrsBean
					.getMaleInvolvementConcept(), patient, facility,
					maleInvolved, encounter, null);
			encounter.addObs(maleInvolvedObs);
		}
		if (pmtct != null) {
			Obs pmtctObs = createBooleanValueObs(date, openmrsBean
					.getPMTCTConcept(), patient, facility, pmtct, encounter,
					null);
			encounter.addObs(pmtctObs);
		}
		if (preTestCounseled != null) {
			Obs preTestCounseledObs = createBooleanValueObs(date, openmrsBean
					.getPreHIVTestCounselingConcept(), patient, facility,
					preTestCounseled, encounter, null);
			encounter.addObs(preTestCounseledObs);
		}
		if (hivTestResult != null) {
			Obs hivResultObs = createTextValueObs(date, openmrsBean
					.getHIVTestResultConcept(), patient, facility,
					hivTestResult.name(), encounter, null);
			encounter.addObs(hivResultObs);
		}
		if (postTestCounseled != null) {
			Obs postTestCounseledObs = createBooleanValueObs(date, openmrsBean
					.getPostHIVTestCounselingConcept(), patient, facility,
					postTestCounseled, encounter, null);
			encounter.addObs(postTestCounseledObs);
		}
		if (pmtctTreatment != null) {
			Obs pmtctTreatmentObs = createBooleanValueObs(date, openmrsBean
					.getPMTCTTreatmentConcept(), patient, facility,
					pmtctTreatment, encounter, null);
			encounter.addObs(pmtctTreatmentObs);
		}
		if (referred != null) {
			Obs referredObs = createBooleanValueObs(date, openmrsBean
					.getReferredConcept(), patient, facility, referred,
					encounter, null);
			encounter.addObs(referredObs);
		}
		if (nextANCDate != null) {
			Obs nextANCDateObs = createDateValueObs(date, openmrsBean
					.getNextANCDateConcept(), patient, facility, nextANCDate,
					encounter, null);
			encounter.addObs(nextANCDateObs);
		}
		if (comments != null) {
			Obs commentsObs = createTextValueObs(date, openmrsBean
					.getCommentsConcept(), patient, facility, comments,
					encounter, null);
			encounter.addObs(commentsObs);
		}

		encounter = encounterService.saveEncounter(encounter);

		if (estDeliveryDate != null) {
			Obs pregnancyDueDateObs = openmrsBean.getActivePregnancyDueDateObs(
					patient.getPatientId(), pregnancyObs);
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
		encounter.setEncounterType(openmrsBean
				.getPregnancyTerminationVisitEncounterType());
		encounter.setEncounterDatetime(date);
		encounter.setPatient(patient);
		encounter.setLocation(facility);
		encounter.setProvider(staff);

		Obs pregnancyObs = openmrsBean.getActivePregnancy(patient
				.getPatientId());
		if (pregnancyObs == null) {
			log.warn("Entered Pregnancy termination "
					+ "for patient without active pregnancy, patient id="
					+ patient.getPatientId());
		}

		if (terminationType != null) {
			Obs terminationTypeObs = createNumericValueObs(date, openmrsBean
					.getTerminationTypeConcept(), patient, facility,
					terminationType, encounter, null);
			encounter.addObs(terminationTypeObs);
		}
		if (procedure != null) {
			Obs procedureObs = createNumericValueObs(date, openmrsBean
					.getTerminationProcedureConcept(), patient, facility,
					procedure, encounter, null);
			encounter.addObs(procedureObs);
		}
		if (complications != null) {
			for (Integer complication : complications) {
				Obs complicationObs = createNumericValueObs(date, openmrsBean
						.getTerminationComplicationConcept(), patient,
						facility, complication, encounter, null);
				encounter.addObs(complicationObs);
			}
		}
		if (maternalDeath != null) {
			Obs maternalDeathObs = createBooleanValueObs(date, openmrsBean
					.getMaternalDeathConcept(), patient, facility,
					maternalDeath, encounter, null);
			encounter.addObs(maternalDeathObs);
		}
		if (referred != null) {
			Obs referredObs = createBooleanValueObs(date, openmrsBean
					.getReferredConcept(), patient, facility, referred,
					encounter, null);
			encounter.addObs(referredObs);
		}
		if (postAbortionFPCounseled != null) {
			Obs postCounseledObs = createBooleanValueObs(date, openmrsBean
					.getPostAbortionFPCounselingConcept(), patient, facility,
					postAbortionFPCounseled, encounter, null);
			encounter.addObs(postCounseledObs);
		}
		if (postAbortionFPAccepted != null) {
			Obs postAcceptedObs = createBooleanValueObs(date, openmrsBean
					.getPostAbortionFPAcceptedConcept(), patient, facility,
					postAbortionFPAccepted, encounter, null);
			encounter.addObs(postAcceptedObs);
		}
		if (comments != null) {
			Obs commentsObs = createTextValueObs(date, openmrsBean
					.getCommentsConcept(), patient, facility, comments,
					encounter, null);
			encounter.addObs(commentsObs);
		}

		Obs pregnancyStatusObs = createBooleanValueObs(date, openmrsBean
				.getPregnancyStatusConcept(), patient, facility, Boolean.FALSE,
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
		encounter.setEncounterType(openmrsBean
				.getPregnancyDeliveryVisitEncounterType());
		encounter.setEncounterDatetime(datetime);
		encounter.setPatient(patient);
		encounter.setLocation(facility);
		encounter.setProvider(staff);

		Obs pregnancyObs = openmrsBean.getActivePregnancy(patient
				.getPatientId());
		if (pregnancyObs == null) {
			log.warn("Entered Pregnancy delivery "
					+ "for patient without active pregnancy, patient id="
					+ patient.getPatientId());
		}

		if (mode != null) {
			Obs modeObs = createNumericValueObs(datetime, openmrsBean
					.getDeliveryModeConcept(), patient, facility, mode,
					encounter, null);
			encounter.addObs(modeObs);
		}
		if (outcome != null) {
			Obs outcomeObs = createNumericValueObs(datetime, openmrsBean
					.getDeliveryOutcomeConcept(), patient, facility, outcome,
					encounter, null);
			encounter.addObs(outcomeObs);
		}
		if (deliveryLocation != null) {
			Obs locationObs = createNumericValueObs(datetime, openmrsBean
					.getDeliveryLocationConcept(), patient, facility,
					deliveryLocation, encounter, null);
			encounter.addObs(locationObs);
		}
		if (deliveredBy != null) {
			Obs deliveredByObs = createNumericValueObs(datetime, openmrsBean
					.getDeliveredByConcept(), patient, facility, deliveredBy,
					encounter, null);
			encounter.addObs(deliveredByObs);
		}
		if (maleInvolved != null) {
			Obs maleInvolvedObs = createBooleanValueObs(datetime, openmrsBean
					.getMaleInvolvementConcept(), patient, facility,
					maleInvolved, encounter, null);
			encounter.addObs(maleInvolvedObs);
		}
		if (complications != null) {
			for (Integer complication : complications) {
				Obs complicationObs = createNumericValueObs(datetime,
						openmrsBean.getDeliveryComplicationConcept(), patient,
						facility, complication, encounter, null);
				encounter.addObs(complicationObs);
			}
		}
		if (vvf != null) {
			Obs vvfObs = createNumericValueObs(datetime, openmrsBean
					.getVVFRepairConcept(), patient, facility, vvf, encounter,
					null);
			encounter.addObs(vvfObs);
		}
		if (maternalDeath != null) {
			Obs maternalDeathObs = createBooleanValueObs(datetime, openmrsBean
					.getMaternalDeathConcept(), patient, facility,
					maternalDeath, encounter, null);
			encounter.addObs(maternalDeathObs);
		}
		if (comments != null) {
			Obs commentsObs = createTextValueObs(datetime, openmrsBean
					.getCommentsConcept(), patient, facility, comments,
					encounter, null);
			encounter.addObs(commentsObs);
		}

		Obs pregnancyStatusObs = createBooleanValueObs(datetime, openmrsBean
				.getPregnancyStatusConcept(), patient, facility, Boolean.FALSE,
				encounter, null);
		pregnancyStatusObs.setObsGroup(pregnancyObs);
		encounter.addObs(pregnancyStatusObs);

		List<Patient> childPatients = new ArrayList<Patient>();

		for (BirthOutcomeChild childOutcome : outcomes) {
			if (childOutcome.getOutcome() == null) {
				// Skip child outcomes missing required outcome
				continue;
			}
			Obs childOutcomeObs = createTextValueObs(datetime, openmrsBean
					.getBirthOutcomeConcept(), patient, facility, childOutcome
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
		encounter.setEncounterType(openmrsBean.getBirthEncounterType());
		encounter.setEncounterDatetime(datetime);
		encounter.setPatient(child);
		encounter.setLocation(facility);
		encounter.setProvider(staff);

		if (weight != null) {
			Obs weightObs = createNumericValueObs(datetime, openmrsBean
					.getWeightConcept(), child, facility, weight, encounter,
					null);
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
		encounter.setEncounterType(openmrsBean
				.getPregnancyDeliveryNotificationEncounterType());
		encounter.setEncounterDatetime(date);
		encounter.setPatient(patient);
		encounter.setLocation(facility);
		encounter.setProvider(staff);

		Obs pregnancyObs = openmrsBean.getActivePregnancy(patient
				.getPatientId());
		if (pregnancyObs == null) {
			log
					.warn("Entered Pregnancy delivery notification for patient without active pregnancy, "
							+ "patient id=" + patient.getPatientId());
		}

		Obs pregnancyStatusObs = createBooleanValueObs(date, openmrsBean
				.getPregnancyStatusConcept(), patient, facility, Boolean.FALSE,
				encounter, null);
		pregnancyStatusObs.setObsGroup(pregnancyObs);
		encounter.addObs(pregnancyStatusObs);

		encounterService.saveEncounter(encounter);

		// Send message only if closing active pregnancy
		if (pregnancyObs != null) {
			messageBean.sendDeliveryNotification(patient);
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
		encounter
				.setEncounterType(openmrsBean.getMotherPNCVisitEncounterType());
		encounter.setEncounterDatetime(datetime);
		encounter.setPatient(patient);
		encounter.setLocation(facility);
		encounter.setProvider(staff);

		if (visitNumber != null) {
			Obs visitNumberObs = createNumericValueObs(datetime, openmrsBean
					.getVisitNumberConcept(), patient, facility, visitNumber,
					encounter, null);
			encounter.addObs(visitNumberObs);
		}
		if (pncLocation != null) {
			Obs pncLocationObs = createNumericValueObs(datetime, openmrsBean
					.getANCPNCLocationConcept(), patient, facility,
					pncLocation, encounter, null);
			encounter.addObs(pncLocationObs);
		}
		if (house != null) {
			Obs houseObs = createTextValueObs(datetime, openmrsBean
					.getHouseConcept(), patient, facility, house, encounter,
					null);
			encounter.addObs(houseObs);
		}
		if (community != null) {
			Obs communityObs = createTextValueObs(datetime, openmrsBean
					.getCommunityConcept(), patient, facility, community,
					encounter, null);
			encounter.addObs(communityObs);
		}
		if (referred != null) {
			Obs referredObs = createBooleanValueObs(datetime, openmrsBean
					.getReferredConcept(), patient, facility, referred,
					encounter, null);
			encounter.addObs(referredObs);
		}
		if (maleInvolved != null) {
			Obs maleInvolvedObs = createBooleanValueObs(datetime, openmrsBean
					.getMaleInvolvementConcept(), patient, facility,
					maleInvolved, encounter, null);
			encounter.addObs(maleInvolvedObs);
		}
		if (Boolean.TRUE.equals(vitaminA)) {
			Obs vitaminAObs = createConceptValueObs(datetime, openmrsBean
					.getImmunizationsOrderedConcept(), patient, facility,
					openmrsBean.getVitaminAConcept(), encounter, null);
			encounter.addObs(vitaminAObs);
		}
		if (ttDose != null) {
			Obs ttDoseObs = createNumericValueObs(datetime, openmrsBean
					.getTetanusDoseConcept(), patient, facility, ttDose,
					encounter, null);
			encounter.addObs(ttDoseObs);
		}
		if (lochiaColour != null) {
			Obs lochiaColourObs = createNumericValueObs(datetime, openmrsBean
					.getLochiaColourConcept(), patient, facility, lochiaColour,
					encounter, null);
			encounter.addObs(lochiaColourObs);
		}
		if (lochiaOdourFoul != null) {
			Obs lochiaOdourObs = createBooleanValueObs(datetime, openmrsBean
					.getLochiaFoulConcept(), patient, facility,
					lochiaOdourFoul, encounter, null);
			encounter.addObs(lochiaOdourObs);
		}
		if (lochiaAmountExcess != null) {
			Obs lochiaAmountObs = createBooleanValueObs(datetime, openmrsBean
					.getLochiaExcessConcept(), patient, facility,
					lochiaAmountExcess, encounter, null);
			encounter.addObs(lochiaAmountObs);
		}
		if (temperature != null) {
			Obs temperatureObs = createNumericValueObs(datetime, openmrsBean
					.getTemperatureConcept(), patient, facility, temperature,
					encounter, null);
			encounter.addObs(temperatureObs);
		}
		if (fht != null) {
			Obs fhtObs = createNumericValueObs(datetime, openmrsBean
					.getFundalHeightConcept(), patient, facility, fht,
					encounter, null);
			encounter.addObs(fhtObs);
		}
		if (comments != null) {
			Obs commentsObs = createTextValueObs(datetime, openmrsBean
					.getCommentsConcept(), patient, facility, comments,
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
		encounter.setEncounterType(openmrsBean.getChildPNCVisitEncounterType());
		encounter.setEncounterDatetime(datetime);
		encounter.setPatient(patient);
		encounter.setLocation(facility);
		encounter.setProvider(staff);

		if (visitNumber != null) {
			Obs visitNumberObs = createNumericValueObs(datetime, openmrsBean
					.getVisitNumberConcept(), patient, facility, visitNumber,
					encounter, null);
			encounter.addObs(visitNumberObs);
		}
		if (pncLocation != null) {
			Obs pncLocationObs = createNumericValueObs(datetime, openmrsBean
					.getANCPNCLocationConcept(), patient, facility,
					pncLocation, encounter, null);
			encounter.addObs(pncLocationObs);
		}
		if (house != null) {
			Obs houseObs = createTextValueObs(datetime, openmrsBean
					.getHouseConcept(), patient, facility, house, encounter,
					null);
			encounter.addObs(houseObs);
		}
		if (community != null) {
			Obs communityObs = createTextValueObs(datetime, openmrsBean
					.getCommunityConcept(), patient, facility, community,
					encounter, null);
			encounter.addObs(communityObs);
		}
		if (referred != null) {
			Obs referredObs = createBooleanValueObs(datetime, openmrsBean
					.getReferredConcept(), patient, facility, referred,
					encounter, null);
			encounter.addObs(referredObs);
		}
		if (maleInvolved != null) {
			Obs maleInvolvedObs = createBooleanValueObs(datetime, openmrsBean
					.getMaleInvolvementConcept(), patient, facility,
					maleInvolved, encounter, null);
			encounter.addObs(maleInvolvedObs);
		}
		if (weight != null) {
			Obs weightObs = createNumericValueObs(datetime, openmrsBean
					.getWeightConcept(), patient, facility, weight, encounter,
					null);
			encounter.addObs(weightObs);
		}
		if (temperature != null) {
			Obs temperatureObs = createNumericValueObs(datetime, openmrsBean
					.getTemperatureConcept(), patient, facility, temperature,
					encounter, null);
			encounter.addObs(temperatureObs);
		}
		if (Boolean.TRUE.equals(bcg)) {
			Obs bcgObs = createConceptValueObs(datetime, openmrsBean
					.getImmunizationsOrderedConcept(), patient, facility,
					openmrsBean.getBCGConcept(), encounter, null);
			encounter.addObs(bcgObs);
		}
		if (Boolean.TRUE.equals(opv0)) {
			Integer opvDose = 0;
			Obs opvDoseObs = createNumericValueObs(datetime, openmrsBean
					.getOPVDoseConcept(), patient, facility, opvDose,
					encounter, null);
			encounter.addObs(opvDoseObs);
		}
		if (respiration != null) {
			Obs respirationObs = createNumericValueObs(datetime, openmrsBean
					.getRespiratoryRateConcept(), patient, facility,
					respiration, encounter, null);
			encounter.addObs(respirationObs);
		}
		if (cordConditionNormal != null) {
			Obs cordConditionObs = createBooleanValueObs(datetime, openmrsBean
					.getCordConditionConcept(), patient, facility,
					cordConditionNormal, encounter, null);
			encounter.addObs(cordConditionObs);
		}
		if (babyConditionGood != null) {
			Obs babyConditionObs = createBooleanValueObs(datetime, openmrsBean
					.getConditionBabyConcept(), patient, facility,
					babyConditionGood, encounter, null);
			encounter.addObs(babyConditionObs);
		}
		if (comments != null) {
			Obs commentsObs = createTextValueObs(datetime, openmrsBean
					.getCommentsConcept(), patient, facility, comments,
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
		encounter.setEncounterType(openmrsBean.getTTVisitEncounterType());
		encounter.setEncounterDatetime(date);
		encounter.setPatient(patient);
		encounter.setLocation(facility);
		encounter.setProvider(staff);

		if (ttDose != null) {
			Obs ttDoseObs = createNumericValueObs(date, openmrsBean
					.getTetanusDoseConcept(), patient, facility, ttDose,
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
		messageBean.removeAllMessageProgramEnrollments(patient.getPatientId());

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
		encounter.setEncounterType(openmrsBean.getCWCVisitEncounterType());
		encounter.setEncounterDatetime(date);
		encounter.setPatient(patient);
		encounter.setLocation(facility);
		encounter.setProvider(staff);

		if (cwcLocation != null) {
			Obs cwcLocationObs = createNumericValueObs(date, openmrsBean
					.getCWCLocationConcept(), patient, facility, cwcLocation,
					encounter, null);
			encounter.addObs(cwcLocationObs);
		}
		if (house != null) {
			Obs houseObs = createTextValueObs(date, openmrsBean
					.getHouseConcept(), patient, facility, house, encounter,
					null);
			encounter.addObs(houseObs);
		}
		if (community != null) {
			Obs communityObs = createTextValueObs(date, openmrsBean
					.getCommunityConcept(), patient, facility, community,
					encounter, null);
			encounter.addObs(communityObs);
		}
		if (Boolean.TRUE.equals(bcg)) {
			Obs bcgObs = createConceptValueObs(date, openmrsBean
					.getImmunizationsOrderedConcept(), patient, facility,
					openmrsBean.getBCGConcept(), encounter, null);
			encounter.addObs(bcgObs);
		}
		if (opvDose != null) {
			Obs opvDoseObs = createNumericValueObs(date, openmrsBean
					.getOPVDoseConcept(), patient, facility, opvDose,
					encounter, null);
			encounter.addObs(opvDoseObs);
		}
		if (pentaDose != null) {
			Obs pentaDoseObs = createNumericValueObs(date, openmrsBean
					.getPentaDoseConcept(), patient, facility, pentaDose,
					encounter, null);
			encounter.addObs(pentaDoseObs);
		}
		if (Boolean.TRUE.equals(yellowFever)) {
			Obs yellowFeverObs = createConceptValueObs(date, openmrsBean
					.getImmunizationsOrderedConcept(), patient, facility,
					openmrsBean.getYellowFeverConcept(), encounter, null);
			encounter.addObs(yellowFeverObs);
		}
		if (Boolean.TRUE.equals(csm)) {
			Obs csmObs = createConceptValueObs(date, openmrsBean
					.getImmunizationsOrderedConcept(), patient, facility,
					openmrsBean.getCSMConcept(), encounter, null);
			encounter.addObs(csmObs);
		}
		if (Boolean.TRUE.equals(measles)) {
			Obs measlesObs = createConceptValueObs(date, openmrsBean
					.getImmunizationsOrderedConcept(), patient, facility,
					openmrsBean.getMeaslesConcept(), encounter, null);
			encounter.addObs(measlesObs);
		}
		if (iptiDose != null) {
			Obs iptiObs = createNumericValueObs(date, openmrsBean
					.getIPTiDoseConcept(), patient, facility, iptiDose,
					encounter, null);
			encounter.addObs(iptiObs);
		}
		if (Boolean.TRUE.equals(vitaminA)) {
			Obs vitaminAObs = createConceptValueObs(date, openmrsBean
					.getImmunizationsOrderedConcept(), patient, facility,
					openmrsBean.getVitaminAConcept(), encounter, null);
			encounter.addObs(vitaminAObs);
		}
		if (dewormer != null) {
			Obs dewormerObs = createBooleanValueObs(date, openmrsBean
					.getDewormerConcept(), patient, facility, dewormer,
					encounter, null);
			encounter.addObs(dewormerObs);
		}
		if (weight != null) {
			Obs weightObs = createNumericValueObs(date, openmrsBean
					.getWeightConcept(), patient, facility, weight, encounter,
					null);
			encounter.addObs(weightObs);
		}
		if (muac != null) {
			Obs muacObs = createNumericValueObs(date, openmrsBean
					.getMUACConcept(), patient, facility, muac, encounter, null);
			encounter.addObs(muacObs);
		}
		if (height != null) {
			Obs heightObs = createNumericValueObs(date, openmrsBean
					.getHeightConcept(), patient, facility, height, encounter,
					null);
			encounter.addObs(heightObs);
		}
		if (maleInvolved != null) {
			Obs maleInvolvedObs = createBooleanValueObs(date, openmrsBean
					.getMaleInvolvementConcept(), patient, facility,
					maleInvolved, encounter, null);
			encounter.addObs(maleInvolvedObs);
		}
		if (comments != null) {
			Obs commentsObs = createTextValueObs(date, openmrsBean
					.getCommentsConcept(), patient, facility, comments,
					encounter, null);
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
		encounter.setEncounterType(openmrsBean
				.getOutpatientVisitEncounterType());
		encounter.setEncounterDatetime(date);
		encounter.setPatient(patient);
		encounter.setLocation(facility);
		encounter.setProvider(staff);

		if (serialNumber != null) {
			Obs serialNumberObs = createTextValueObs(date, openmrsBean
					.getSerialNumberConcept(), patient, facility, serialNumber,
					encounter, null);
			encounter.addObs(serialNumberObs);
		}
		if (insured != null) {
			Obs insuredObs = createBooleanValueObs(date, openmrsBean
					.getInsuredConcept(), patient, facility, insured,
					encounter, null);
			encounter.addObs(insuredObs);
		}
		if (newCase != null) {
			Obs newCaseObs = createBooleanValueObs(date, openmrsBean
					.getNewCaseConcept(), patient, facility, newCase,
					encounter, null);
			encounter.addObs(newCaseObs);
		}
		if (diagnosis != null) {
			Obs diagnosisObs = createNumericValueObs(date, openmrsBean
					.getPrimaryDiagnosisConcept(), patient, facility,
					diagnosis, encounter, null);
			encounter.addObs(diagnosisObs);
		}
		if (secondDiagnosis != null) {
			Obs secondDiagnosisObs = createNumericValueObs(date, openmrsBean
					.getSecondaryDiagnosisConcept(), patient, facility,
					secondDiagnosis, encounter, null);
			encounter.addObs(secondDiagnosisObs);
		}
		if (referred != null) {
			Obs referredObs = createBooleanValueObs(date, openmrsBean
					.getReferredConcept(), patient, facility, referred,
					encounter, null);
			encounter.addObs(referredObs);
		}
		if (Boolean.TRUE.equals(rdtGiven)) {
			Concept rdtTestValueConcept = null;
			if (Boolean.TRUE.equals(rdtPositive)) {
				rdtTestValueConcept = openmrsBean.getPositiveConcept();
			} else {
				rdtTestValueConcept = openmrsBean.getNegativeConcept();
			}
			Obs rdtTestObs = createConceptValueObs(date, openmrsBean
					.getMalariaRDTConcept(), patient, facility,
					rdtTestValueConcept, encounter, null);
			encounter.addObs(rdtTestObs);
		}
		if (actTreated != null) {
			Obs actTreatedObs = createBooleanValueObs(date, openmrsBean
					.getACTTreatmentConcept(), patient, facility, actTreated,
					encounter, null);
			encounter.addObs(actTreatedObs);
		}
		if (comments != null) {
			Obs commentsObs = createTextValueObs(date, openmrsBean
					.getCommentsConcept(), patient, facility, comments,
					encounter, null);
			encounter.addObs(commentsObs);
		}

		encounterService.saveEncounter(encounter);
	}

	protected Integer updatePregnancyDueDateObs(Obs pregnancyObs,
			Obs dueDateObs, Date newDueDate, Encounter encounter) {
		ObsService obsService = contextService.getObsService();
		MotechService motechService = contextService.getMotechService();

		Integer existingDueDateObsId = dueDateObs.getObsId();

		Obs newDueDateObs = createDateValueObs(
				encounter.getEncounterDatetime(), openmrsBean
						.getDueDateConcept(), encounter.getPatient(), encounter
						.getLocation(), newDueDate, encounter, null);
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

	protected Obs createNumericValueObs(Date date, Concept concept,
			Person person, Location location, Integer value,
			Encounter encounter, User creator) {

		Double doubleValue = new Double(value);
		return createNumericValueObs(date, concept, person, location,
				doubleValue, encounter, creator);
	}

	protected Obs createNumericValueObs(Date date, Concept concept,
			Person person, Location location, Double value,
			Encounter encounter, User creator) {

		Obs obs = createObs(date, concept, person, location, encounter, creator);
		obs.setValueNumeric(value);
		return obs;
	}

	protected Obs createBooleanValueObs(Date date, Concept concept,
			Person person, Location location, Boolean value,
			Encounter encounter, User creator) {

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

	protected Obs createDateValueObs(Date date, Concept concept, Person person,
			Location location, Date value, Encounter encounter, User creator) {

		Obs obs = createObs(date, concept, person, location, encounter, creator);
		obs.setValueDatetime(value);
		return obs;
	}

	protected Obs createConceptValueObs(Date date, Concept concept,
			Person person, Location location, Concept value,
			Encounter encounter, User creator) {

		Obs obs = createObs(date, concept, person, location, encounter, creator);
		obs.setValueCoded(value);
		return obs;
	}

	protected Obs createTextValueObs(Date date, Concept concept, Person person,
			Location location, String value, Encounter encounter, User creator) {

		Obs obs = createObs(date, concept, person, location, encounter, creator);
		obs.setValueText(value);
		return obs;
	}

	protected Obs createObs(Date date, Concept concept, Person person,
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

}
