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
package org.motechproject.server.omod;

import java.util.Date;
import java.util.List;

import org.motechproject.server.model.Blackout;
import org.motechproject.server.model.Community;
import org.motechproject.server.model.ExpectedEncounter;
import org.motechproject.server.model.ExpectedObs;
import org.motechproject.server.model.Facility;
import org.motechproject.server.model.GeneralOutpatientEncounter;
import org.motechproject.server.model.Message;
import org.motechproject.server.model.MessageAttribute;
import org.motechproject.server.model.MessageDefinition;
import org.motechproject.server.model.MessageProgramEnrollment;
import org.motechproject.server.model.MessageStatus;
import org.motechproject.server.model.ScheduledMessage;
import org.motechproject.server.model.TroubledPhone;
import org.motechproject.server.omod.sdsched.ScheduleMaintService;
import org.motechproject.server.svc.OpenmrsBean;
import org.motechproject.server.svc.RegistrarBean;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.OpenmrsService;
import org.springframework.transaction.annotation.Transactional;

/**
 * A service interface for much of 'real work' for the motech server OpenMRS
 * module.
 */
public interface MotechService extends OpenmrsService {

	RegistrarBean getRegistrarBean();

	OpenmrsBean getOpenmrsBean();

	ScheduleMaintService getScheduleMaintService();

	@Transactional
	ScheduledMessage saveScheduledMessage(ScheduledMessage scheduledMessage);

	@Transactional
	Message saveMessage(Message message);

	@Transactional
	MessageDefinition saveMessageDefinition(MessageDefinition messageDefinition);

	@Transactional
	MessageAttribute saveMessageAttribute(MessageAttribute messageAttribute);

	@Transactional(readOnly = true)
	List<ScheduledMessage> getAllScheduledMessages();

	@Transactional(readOnly = true)
	List<ScheduledMessage> getScheduledMessages(Date startDate, Date endDate);

	@Transactional(readOnly = true)
	public List<ScheduledMessage> getScheduledMessages(Integer recipientId,
			MessageDefinition definition, MessageProgramEnrollment enrollment,
			Date messageDate);

	@Transactional(readOnly = true)
	List<Message> getAllMessages();

	@Transactional(readOnly = true)
	List<Message> getMessages(ScheduledMessage scheduledMessage);

	@Transactional(readOnly = true)
	List<Message> getMessages(Date startDate, Date endDate, MessageStatus status);

	@Transactional(readOnly = true)
	List<Message> getMessages(MessageProgramEnrollment enrollment,
			MessageStatus status);

	@Transactional(readOnly = true)
	List<Message> getMessages(Integer recipientId,
			MessageProgramEnrollment enrollment, MessageDefinition definition,
			Date messageDate, MessageStatus status);

	@Transactional(readOnly = true)
	Message getMessage(String publicId);

	@Transactional(readOnly = true)
	List<MessageDefinition> getAllMessageDefinitions();

	@Transactional(readOnly = true)
	MessageDefinition getMessageDefinition(String messageKey);

	@Transactional(readOnly = true)
	List<MessageAttribute> getAllMessageAttributes();

	@Transactional(readOnly = true)
	List<Integer> getUserIdsByPersonAttribute(
			PersonAttributeType personAttributeType, String value);

	@Transactional(readOnly = true)
	List<MessageProgramEnrollment> getActiveMessageProgramEnrollments(
			Integer personId, String program, Integer obsId);

	@Transactional
	MessageProgramEnrollment saveMessageProgramEnrollment(
			MessageProgramEnrollment enrollment);

	@Transactional(readOnly = true)
	Blackout getBlackoutSettings();

	@Transactional
	void setBlackoutSettings(Blackout blackout);

	@Transactional(readOnly = true)
	TroubledPhone getTroubledPhone(String phoneNumber);

	@Transactional
	void saveTroubledPhone(TroubledPhone troubledPhone);

	@Transactional
	void addTroubledPhone(String phoneNumber);

	@Transactional
	void removeTroubledPhone(String phoneNumber);

	@Transactional
	GeneralOutpatientEncounter saveGeneralOutpatientEncounter(
			GeneralOutpatientEncounter encounter);

	@Transactional(readOnly = true)
	List<String> getAllCountries();

	@Transactional(readOnly = true)
	List<String> getAllRegions();

	@Transactional(readOnly = true)
	List<String> getRegions(String country);

	@Transactional(readOnly = true)
	List<String> getAllDistricts();

	@Transactional(readOnly = true)
	List<String> getDistricts(String country, String region);

	@Transactional(readOnly = true)
	List<Obs> getActivePregnancies(Integer patientId, Concept pregnancyConcept,
			Concept pregnancyStatusConcept);

	@Transactional(readOnly = true)
	List<Obs> getActivePregnanciesDueDateObs(Facility facility,
			Date fromDueDate, Date toDueDate, Concept pregnancyDueDateConcept,
			Concept pregnancyConcept, Concept pregnancyStatusConcept);

	@Transactional(readOnly = true)
	List<Encounter> getEncounters(Facility facility,
			EncounterType encounterType, Date fromDate, Date toDate);

	@Transactional
	ExpectedObs saveExpectedObs(ExpectedObs expectedObs);

	@Transactional(readOnly = true)
	List<ExpectedObs> getExpectedObs(Patient patient, Facility facility,
			String[] groups, Date minDueDate, Date maxDueDate,
			Date maxLateDate, Date minMaxDate, boolean nameOrdering);

	@Transactional
	ExpectedEncounter saveExpectedEncounter(ExpectedEncounter expectedEncounter);

	@Transactional
	Facility saveFacility(Facility facility);

	@Transactional(readOnly = true)
	List<ExpectedEncounter> getExpectedEncounter(Patient patient,
			Facility facility, String[] groups, Date minDueDate,
			Date maxDueDate, Date maxLateDate, Date minMaxDate,
			boolean nameOrdering);

	@Transactional(readOnly = true)
	List<Patient> getPatients(String firstName, String lastName,
			String preferredName, Date birthDate, Integer communityId,
			String phoneNumber, PersonAttributeType phoneNumberAttrType,
			String nhisNumber, PersonAttributeType nhisAttrType,
			String patientId, PatientIdentifierType patientIdType);

	@Transactional(readOnly = true)
	List<Patient> getDuplicatePatients(String firstName, String lastName,
			String preferredName, Date birthDate, Integer communityId,
			String phoneNumber, PersonAttributeType phoneNumberAttrType,
			String nhisNumber, PersonAttributeType nhisAttrType,
			String patientId, PatientIdentifierType patientIdType);

	@Transactional(readOnly = true)
	Facility getFacilityById(Integer facilityId);

	@Transactional(readOnly = true)
	List<Facility> getAllFacilities();

	@Transactional(readOnly = true)
	Community getCommunityById(Integer communityId);

	@Transactional(readOnly = true)
	List<Community> getAllCommunities();

	@Transactional(readOnly = true)
	List<Community> getCommunities(String country, String region,
			String district);

	@Transactional(readOnly = true)
	Community getCommunityByPatient(Patient patient);
}
