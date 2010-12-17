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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.model.Community;
import org.motechproject.server.model.Facility;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.MotechService;
import org.motechproject.server.svc.OpenmrsBean;
import org.motechproject.server.util.MotechConstants;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.DayOfWeek;
import org.motechproject.ws.MediaType;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.User;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;

/**
 * An implementation of the OpenmrsBean interface.
 */
public class OpenmrsBeanImpl implements OpenmrsBean {

	private static Log log = LogFactory.getLog(RegistrarBeanImpl.class);

	private ContextService contextService;

	public void setContextService(ContextService contextService) {
		this.contextService = contextService;
	}

	public Patient getPatientById(Integer patientId) {
		PatientService patientService = contextService.getPatientService();
		return patientService.getPatient(patientId);
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

	public Integer getMotherMotechId(Patient patient) {
		Relationship motherRelation = getMotherRelationship(patient);
		if (motherRelation != null) {
			Person mother = motherRelation.getPersonA();
			return getMotechId(mother.getPersonId());
		}
		return null;
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

	public Community getCommunityByPatient(Patient patient) {
		return contextService.getMotechService().getCommunityByPatient(patient);
	}

}
