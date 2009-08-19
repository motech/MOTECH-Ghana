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
package org.openmrs.module.motechmodule;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNameTag;
import org.openmrs.EncounterType;
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
 * This class contains the logic that is run every time this module is either started or shutdown
 */
public class MotechModuleActivator implements Activator {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @see org.openmrs.module.Activator#startup()
	 */
	public void startup() {
		log.info("Starting Motech Module");
		
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_USERS);
		
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES);
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_PERSON_ATTRIBUTE_TYPES);
		
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_IDENTIFIER_TYPES);
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_IDENTIFIER_TYPES);
		
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_LOCATIONS);
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_LOCATIONS);
		
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_ENCOUNTER_TYPES);
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_ENCOUNTER_TYPES);
		
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_CONCEPTS);
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_CONCEPTS);
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_CONCEPT_DATATYPES);
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_CONCEPT_CLASSES);
		
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_SCHEDULER);
		
		try {
			User admin = Context.getUserService().getUser(1);
			
			log.info("Verifying Person Attributes Exist");
			createPersonAttributeType("Phone Number", "A person's phone number.", "java.lang.String", admin);
			createPersonAttributeType("NHIS Number", "A person's NHIS number.", "java.lang.String", admin);
			createPersonAttributeType("Language", "A person's language preference.", "java.lang.String", admin);
			
			log.info("Verifying Patient Identifier Exist");
			createPatientIdentifierType("Ghana Clinic Id", "Patient Id for Ghana Clinics.", admin);
			
			log.info("Verifying Default Location Exists");
			createLocation("Default Ghana Clinic", "Default Ghana Clinic Location", admin);
			
			log.info("Verifying Encounter Types Exist");
			createEncounterType("MATERNALVISIT", "Ghana Maternal Visit", admin);
			createEncounterType("PREGNANCYVISIT", "Ghana Pregnancy Registration or Delivery Visit", admin);
			createEncounterType("IMMUNIZVISIT", "Ghana Immunization Visit", admin);
			createEncounterType("GENERALVISIT", "Ghana General Visit", admin);
			
			log.info("Verifying Concepts Exist");
			createConcept("PREGNANCY VISIT NUMBER", "Visit Number for Pregnancy", "Misc", "Numeric", admin);
			createConcept("INTERMITTENT PREVENTATIVE TREATMENT", "Treatment for Malaria", "Drug", "N/A", admin);
			createConcept("INSECTICIDE-TREATED NET USAGE",
			    "Question on encounter form: \"Does the patient use insecticide-treated nets?\"", "Question", "Boolean", admin);
			createConcept("PENTA VACCINATION", "Vaccination booster for infants.", "Drug", "N/A", admin);
			createConcept("CEREBRO-SPINAL MENINGITIS VACCINATION", "Vaccination against Cerebro-Spinal Meningitis.", "Drug",
			    "N/A", admin);
			createConcept("VITAMIN A", "Supplement for Vitamin A.", "Drug", "N/A", admin);
			createConcept(
			    "PRE PREVENTING MATERNAL TO CHILD TRANSMISSION",
			    "Question on encounter form: \"Did the patient receive Pre Counseling for Preventing Mother-to-Child Transmission (PMTCT) of HIV\"",
			    "Question", "Boolean", admin);
			createConcept(
			    "TEST PREVENTING MATERNAL TO CHILD TRANSMISSION",
			    "Question on encounter form: \"Did the patient receive Testing for Preventing Mother-to-Child Transmission (PMTCT) of HIV\"",
			    "Question", "Boolean", admin);
			createConcept(
			    "POST PREVENTING MATERNAL TO CHILD TRANSMISSION",
			    "Question on encounter form: \"Did the patient receive Post Counseling for Preventing Mother-to-Child Transmission (PMTCT) of HIV\"",
			    "Question", "Boolean", admin);
			createConcept("HEMOGLOBIN AT 36 WEEKS", "Hemoglobin level at 36 weeks of Pregnancy", "Test", "Numeric", admin);
			
			log.info("Verifying Concepts Exist as Answers");
			// TODO: Add IPT to proper Concept as an Answer, not an immunization
			addConceptAnswers("IMMUNIZATIONS ORDERED", new String[] { "TETANUS BOOSTER", "YELLOW FEVER VACCINATION",
			        "INTERMITTENT PREVENTATIVE TREATMENT", "PENTA VACCINATION", "CEREBRO-SPINAL MENINGITIS VACCINATION" }, admin);
			
			log.info("Verifying Task Exists and is Scheduled");
			// TODO: Task should start automatically on startup, Boolean.TRUE
			createTask("Notification Task", "Task to send out SMS notifications", new Date(), new Long(30), Boolean.FALSE,
			    "org.motech.tasks.NotificationTask", admin);
			
		}
		finally {
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_USERS);
			
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES);
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_PERSON_ATTRIBUTE_TYPES);
			
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_IDENTIFIER_TYPES);
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_IDENTIFIER_TYPES);
			
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_LOCATIONS);
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_LOCATIONS);
			
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_ENCOUNTER_TYPES);
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_ENCOUNTER_TYPES);
			
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_CONCEPTS);
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_CONCEPTS);
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_CONCEPT_DATATYPES);
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_CONCEPT_CLASSES);
			
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_SCHEDULER);
		}
	}
	
	private void createPersonAttributeType(String name, String description, String format, User creator) {
		PersonAttributeType attrType = Context.getPersonService().getPersonAttributeTypeByName(name);
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
	
	private void createPatientIdentifierType(String name, String description, User creator) {
		PatientIdentifierType idType = Context.getPatientService().getPatientIdentifierTypeByName(name);
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
	
	private void createEncounterType(String name, String description, User creator) {
		EncounterType encType = Context.getEncounterService().getEncounterType(name);
		if (encType == null) {
			log.info(name + " EncounterType Does Not Exist - Creating");
			encType = new EncounterType();
			encType.setName(name);
			encType.setDescription(description);
			encType.setCreator(creator);
			Context.getEncounterService().saveEncounterType(encType);
		}
	}
	
	private Concept createConcept(String name, String description, String className, String dataTypeName, User creator) {
		// Default "en" Locale matching other existing concepts
		Locale defaultLocale = Locale.ENGLISH;
		Concept concept = Context.getConceptService().getConcept(name);
		if (concept == null) {
			log.info(name + " Concept Does Not Exist - Creating");
			concept = new Concept();
			ConceptName conceptName = new ConceptName(name, defaultLocale);
			conceptName.addTag(ConceptNameTag.PREFERRED);
			// AddTag is workaround since the following results in "preferred_en" instead of "preferred"
			// itn.setPreferredName(defaultLocale, conceptName) 
			concept.addName(conceptName);
			concept.addDescription(new ConceptDescription(description, defaultLocale));
			concept.setConceptClass(Context.getConceptService().getConceptClassByName(className));
			concept.setDatatype(Context.getConceptService().getConceptDatatypeByName(dataTypeName));
			concept.setCreator(creator);
			concept = Context.getConceptService().saveConcept(concept);
		} else {
			log.info(name + " Concept Exists");
		}
		return concept;
	}
	
	private void addConceptAnswers(String conceptName, String[] answerNames, User creator) {
		Concept concept = Context.getConceptService().getConcept(conceptName);
		Set<Integer> currentAnswerIds = new HashSet<Integer>();
		for (ConceptAnswer answer : concept.getAnswers()) {
			currentAnswerIds.add(answer.getAnswerConcept().getConceptId());
		}
		boolean changed = false;
		for (String answerName : answerNames) {
			Concept answer = Context.getConceptService().getConcept(answerName);
			if (!currentAnswerIds.contains(answer.getConceptId())) {
				log.info("Adding Concept Answer " + answerName + " to " + conceptName);
				changed = true;
				ConceptAnswer conceptAnswer = new ConceptAnswer(answer);
				conceptAnswer.setCreator(creator);
				concept.addAnswer(conceptAnswer);
			}
		}
		if (changed) {
			Context.getConceptService().saveConcept(concept);
		}
	}
	
	private void createTask(String name, String description, Date startDate, Long repeatSeconds, Boolean startOnStartup,
	                        String taskClass, User creator) {
		TaskDefinition task = Context.getSchedulerService().getTaskByName(name);
		if (task == null) {
			task = new TaskDefinition();
			task.setName(name);
			task.setDescription(description);
			task.setStartTime(startDate);
			task.setRepeatInterval(repeatSeconds);
			task.setTaskClass(taskClass);
			task.setStartOnStartup(startOnStartup);
			task.setCreatedBy(creator);
			Context.getSchedulerService().saveTask(task);
		}
		Collection<TaskDefinition> tasks = Context.getSchedulerService().getScheduledTasks();
		boolean isScheduled = false;
		for (TaskDefinition taskDefinition : tasks) {
			if (taskDefinition.getId().equals(task.getId())) {
				isScheduled = true;
				break;
			}
		}
		if (!isScheduled) {
			try {
				Context.getSchedulerService().scheduleTask(task);
			}
			catch (SchedulerException e) {
				log.error("Cannot schedule " + name, e);
			}
		}
	}
	
	/**
	 * @see org.openmrs.module.Activator#shutdown()
	 */
	public void shutdown() {
		log.info("Shutting down Motech Module");
	}
	
}
