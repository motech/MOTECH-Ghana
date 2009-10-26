/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.motech.openmrs.module;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motech.tasks.NotificationTask;
import org.motech.tasks.RegimenUpdateTask;
import org.motech.util.MotechConstants;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNameTag;
import org.openmrs.EncounterType;
import org.openmrs.GlobalProperty;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.Activator;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.util.OpenmrsConstants;

/**
 * This class contains the logic that is run every time this module is either
 * started or shutdown. It initializes an OpenMRS installation with the
 * necessary 'stuff' that our module expects to operate in the OpenMRS
 * environment. It does things like adding Concepts for things like a patient
 * phone number, as this is required for sending them SMS messages.
 */
public class MotechModuleActivator implements Activator {

	private Log log = LogFactory.getLog(this.getClass());

	/**
	 * @see org.openmrs.module.Activator#startup()
	 */
	public void startup() {
		log.info("Starting Motech Module");

		Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_USERS);

		Context
				.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES);
		Context
				.addProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_PERSON_ATTRIBUTE_TYPES);

		Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_IDENTIFIER_TYPES);
		Context
				.addProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_IDENTIFIER_TYPES);

		Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_LOCATIONS);
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_LOCATIONS);

		Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_ENCOUNTER_TYPES);
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_ENCOUNTER_TYPES);

		Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_CONCEPTS);
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_CONCEPTS);
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_CONCEPT_DATATYPES);
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_CONCEPT_CLASSES);

		Context.addProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_SCHEDULER);

		Context
				.addProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_GLOBAL_PROPERTIES);

		try {
			User admin = Context.getUserService().getUser(1);

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
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_USERS);

			Context
					.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES);
			Context
					.removeProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_PERSON_ATTRIBUTE_TYPES);

			Context
					.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_IDENTIFIER_TYPES);
			Context
					.removeProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_IDENTIFIER_TYPES);

			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_LOCATIONS);
			Context
					.removeProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_LOCATIONS);

			Context
					.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_ENCOUNTER_TYPES);
			Context
					.removeProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_ENCOUNTER_TYPES);

			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_CONCEPTS);
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_CONCEPTS);
			Context
					.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_CONCEPT_DATATYPES);
			Context
					.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_CONCEPT_CLASSES);

			Context
					.removeProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_SCHEDULER);

			Context
					.removeProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_GLOBAL_PROPERTIES);
		}
	}

	private void createPersonAttributeType(String name, String description,
			String format, User creator) {
		PersonAttributeType attrType = Context.getPersonService()
				.getPersonAttributeTypeByName(name);
		if (attrType == null) {
			log.info(name + " PersonAttributeType Does Not Exist - Creating");
			attrType = new PersonAttributeType();
			attrType.setName(name);
			attrType.setDescription(description);
			attrType.setFormat(format);
			attrType.setCreator(creator);
			Context.getPersonService().savePersonAttributeType(attrType);
		}
	}

	private void createPatientIdentifierType(String name, String description,
			User creator) {
		PatientIdentifierType idType = Context.getPatientService()
				.getPatientIdentifierTypeByName(name);
		if (idType == null) {
			log.info(name + " PatientIdentifierType Does Not Exist - Creating");
			idType = new PatientIdentifierType();
			idType.setName(name);
			idType.setDescription(description);
			idType.setCreator(creator);
			Context.getPatientService().savePatientIdentifierType(idType);
		}
	}

	private void createLocation(String name, String description, User creator) {
		Location location = Context.getLocationService().getLocation(name);
		if (location == null) {
			log.info(name + " Location Does Not Exist - Creating");
			location = new Location();
			location.setName(name);
			location.setDescription(description);
			location.setCreator(creator);
			Context.getLocationService().saveLocation(location);
		}
	}

	private void createEncounterType(String name, String description,
			User creator) {
		EncounterType encType = Context.getEncounterService().getEncounterType(
				name);
		if (encType == null) {
			log.info(name + " EncounterType Does Not Exist - Creating");
			encType = new EncounterType();
			encType.setName(name);
			encType.setDescription(description);
			encType.setCreator(creator);
			Context.getEncounterService().saveEncounterType(encType);
		}
	}

	private Concept createConcept(String name, String description,
			String className, String dataTypeName, User creator) {
		// Default "en" Locale matching other existing concepts
		Locale defaultLocale = Locale.ENGLISH;
		Concept concept = Context.getConceptService().getConcept(name);
		ConceptNameTag prefTag = Context.getConceptService()
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
			concept.setConceptClass(Context.getConceptService()
					.getConceptClassByName(className));
			concept.setDatatype(Context.getConceptService()
					.getConceptDatatypeByName(dataTypeName));
			concept.setCreator(creator);
			concept = Context.getConceptService().saveConcept(concept);
		} else {
			log.info(name + " Concept Exists");
		}
		return concept;
	}

	private void addConceptAnswers(String conceptName, String[] answerNames,
			User creator) {

		Concept concept = Context.getConceptService().getConcept(conceptName);
		Set<Integer> currentAnswerIds = new HashSet<Integer>();
		for (ConceptAnswer answer : concept.getAnswers()) {
			currentAnswerIds.add(answer.getAnswerConcept().getConceptId());
		}
		boolean changed = false;
		for (String answerName : answerNames) {
			Concept answer = Context.getConceptService().getConcept(answerName);
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
			Context.getConceptService().saveConcept(concept);
		}
	}

	private void createTask(String name, String description, Date startDate,
			Long repeatSeconds, Boolean startOnStartup, String taskClass,
			User creator, Map<String, String> properties) {
		TaskDefinition task = Context.getSchedulerService().getTaskByName(name);
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
			Context.getSchedulerService().saveTask(task);
			task = Context.getSchedulerService().getTaskByName(name);
		}

		try {
			Context.getSchedulerService().scheduleTask(task);
		} catch (SchedulerException e) {
			log.error("Cannot schedule task" + name, e);
		}

	}

	private void createGlobalProperty(String name, String value,
			String description) {
		GlobalProperty property = Context.getAdministrationService()
				.getGlobalPropertyObject(name);
		if (property == null) {
			property = new GlobalProperty(name, value, description);
			Context.getAdministrationService().saveGlobalProperty(property);
		}
	}

	private void removeTask(String name) {
		TaskDefinition task = Context.getSchedulerService().getTaskByName(name);
		if (task != null) {
			// Only shutdown if task has not already been shutdown
			if (task.getStarted()) {
				try {
					Context.getSchedulerService().shutdownTask(task);
				} catch (SchedulerException e) {
					log.error("Cannot shutdown task: " + name, e);
				}
			}
			Context.getSchedulerService().deleteTask(task.getId());
		}
	}

	/**
	 * @see org.openmrs.module.Activator#shutdown()
	 */
	public void shutdown() {
		log.info("Shutting down Motech Module");

		log.info("Removing Scheduled Tasks");

		Context.openSession();

		Context.addProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_SCHEDULER);
		try {
			removeTask(MotechConstants.TASK_IMMEDIATE_NOTIFICATION);
			removeTask(MotechConstants.TASK_DAILY_NOTIFICATION);
			removeTask(MotechConstants.TASK_REGIMEN_UPDATE);
		} finally {
			Context
					.removeProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_SCHEDULER);
			Context.closeSession();
		}
	}
}
