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

import java.util.Date;
import java.util.List;

import org.motech.model.Blackout;
import org.motech.model.ExpectedEncounter;
import org.motech.model.ExpectedObs;
import org.motech.model.GeneralPatientEncounter;
import org.motech.model.Log;
import org.motech.model.Message;
import org.motech.model.MessageAttribute;
import org.motech.model.MessageDefinition;
import org.motech.model.MessageProgramEnrollment;
import org.motech.model.MessageStatus;
import org.motech.model.ScheduledMessage;
import org.motech.model.Service;
import org.motech.model.ServiceStatus;
import org.motech.model.TroubledPhone;
import org.motech.svc.RegistrarBean;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.OpenmrsService;
import org.springframework.transaction.annotation.Transactional;

/**
 * A service interface for much of 'real work' for the motech server OpenMRS
 * module.
 */
public interface MotechService extends OpenmrsService {

	RegistrarBean getRegistrarBean();

	@Transactional
	ScheduledMessage saveScheduledMessage(ScheduledMessage scheduledMessage);

	@Transactional
	Message saveMessage(Message message);

	@Transactional
	MessageDefinition saveMessageDefinition(MessageDefinition messageDefinition);

	@Transactional
	MessageAttribute saveMessageAttribute(MessageAttribute messageAttribute);

	@Transactional
	Log saveLog(Log log);

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
	List<Log> getAllLogs();

	@Transactional(readOnly = true)
	List<Integer> getUserIdsByPersonAttribute(
			PersonAttributeType personAttributeType, String value);

	@Transactional(readOnly = true)
	List<MessageProgramEnrollment> getAllActiveMessageProgramEnrollments();

	@Transactional(readOnly = true)
	List<MessageProgramEnrollment> getActiveMessageProgramEnrollments(
			Integer personId);

	@Transactional(readOnly = true)
	List<MessageProgramEnrollment> getActiveMessageProgramEnrollments(
			Integer personId, String program);

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
	GeneralPatientEncounter saveGeneralPatientEncounter(
			GeneralPatientEncounter encounter);

	@Transactional(readOnly = true)
	List<Location> getAllCountries();

	@Transactional(readOnly = true)
	List<Location> getAllRegions();

	@Transactional(readOnly = true)
	List<Location> getRegions(String country);

	@Transactional(readOnly = true)
	List<Location> getAllDistricts();

	@Transactional(readOnly = true)
	List<Location> getDistricts(String country, String region);

	@Transactional(readOnly = true)
	List<Location> getAllCommunities();

	@Transactional(readOnly = true)
	List<Location> getCommunities(String country, String region, String district);

	@Transactional(readOnly = true)
	List<Location> getAllClinics();

	@Transactional(readOnly = true)
	List<Location> getClinics(String country, String region, String district,
			String community);

	@Transactional(readOnly = true)
	List<Integer> getMatchingPeople(String firstName, String lastName,
			Date birthDate, String community, String phoneNumber,
			Integer primaryPhoneNumberAttrTypeId,
			Integer secondaryPhoneNumberAttrTypeId, String patientId,
			String nhisNumber, Integer nhisAttrTypeId);

	List<Obs> getActivePregnancies(Integer patientId, Concept pregnancyConcept,
			Concept pregnancyStatusConcept);

	@Transactional
	Service saveService(Service service);

	@Transactional(readOnly = true)
	List<Service> getServices(Integer patientId, String sequence,
			ServiceStatus status);

	@Transactional(readOnly = true)
	List<Service> getServices(Integer patientId, String service,
			String sequence, ServiceStatus status);

	@Transactional
	ExpectedObs saveExpectedObs(ExpectedObs expectedObs);

	@Transactional
	void removeExpectedObs(ExpectedObs expectedObs);

	@Transactional(readOnly = true)
	List<ExpectedObs> getExpectedObs(Person person, Concept concept,
			Concept valueCoded, Double valueNumeric, Date obsDatetime);

	@Transactional
	ExpectedEncounter saveExpectedEncounter(ExpectedEncounter expectedEncounter);

	@Transactional
	void removeExpectedEncounter(ExpectedEncounter expectedEncounter);

	@Transactional(readOnly = true)
	List<ExpectedEncounter> getExpectedEncounter(Patient patient,
			EncounterType encounterType, Date encounterDatetime);
}
