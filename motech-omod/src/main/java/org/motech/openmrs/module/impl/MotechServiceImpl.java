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
package org.motech.openmrs.module.impl;

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
import org.motech.model.db.MotechDAO;
import org.motech.openmrs.module.MotechService;
import org.motech.svc.RegistrarBean;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.impl.BaseOpenmrsService;

/**
 * An implementation of the MotechService interface using OpenMRS.
 */
public class MotechServiceImpl extends BaseOpenmrsService implements
		MotechService {

	private MotechDAO motechDAO;

	private RegistrarBean registrarBean;

	public MotechDAO getMotechDAO() {
		return motechDAO;
	}

	public void setMotechDAO(MotechDAO motechDAO) {
		this.motechDAO = motechDAO;
	}

	public RegistrarBean getRegistrarBean() {
		return registrarBean;
	}

	public void setRegistrarBean(RegistrarBean registrarBean) {
		this.registrarBean = registrarBean;
	}

	public MessageProgramEnrollment saveMessageProgramEnrollment(
			MessageProgramEnrollment enrollment) {
		return motechDAO.saveMessageProgramEnrollment(enrollment);
	}

	public List<MessageProgramEnrollment> getAllActiveMessageProgramEnrollments() {
		return motechDAO.getAllActiveMessageProgramEnrollments();
	}

	public List<MessageProgramEnrollment> getActiveMessageProgramEnrollments(
			Integer personId) {
		return motechDAO.getActiveMessageProgramEnrollments(personId);
	}

	public List<MessageProgramEnrollment> getActiveMessageProgramEnrollments(
			Integer personId, String program) {
		return motechDAO.getActiveMessageProgramEnrollments(personId, program);
	}

	public List<MessageProgramEnrollment> getActiveMessageProgramEnrollments(
			Integer personId, String program, Integer obsId) {
		return motechDAO.getActiveMessageProgramEnrollments(personId, program,
				obsId);
	}

	public List<Log> getAllLogs() {
		return motechDAO.getLogs();
	}

	public List<ScheduledMessage> getAllScheduledMessages() {
		return motechDAO.getScheduledMessages();
	}

	public List<ScheduledMessage> getScheduledMessages(Date startDate,
			Date endDate) {
		return motechDAO.getScheduledMessages(startDate, endDate);
	}

	public List<Message> getAllMessages() {
		return motechDAO.getMessages();
	}

	public List<Message> getMessages(ScheduledMessage scheduledMessage) {
		return motechDAO.getMessages(scheduledMessage);
	}

	public List<Message> getMessages(Date startDate, Date endDate,
			MessageStatus status) {
		return motechDAO.getMessages(startDate, endDate, status);
	}

	public List<ScheduledMessage> getScheduledMessages(Integer recipientId,
			MessageDefinition definition, MessageProgramEnrollment enrollment,
			Date messageDate) {
		return motechDAO.getScheduledMessages(recipientId, definition,
				enrollment, messageDate);
	}

	public List<Message> getMessages(MessageProgramEnrollment enrollment,
			MessageStatus status) {
		return motechDAO.getMessages(enrollment, status);
	}

	public List<Message> getMessages(Integer recipientId,
			MessageProgramEnrollment enrollment, MessageDefinition definition,
			Date messageDate, MessageStatus status) {
		return motechDAO.getMessages(recipientId, enrollment, definition,
				messageDate, status);
	}

	public Message getMessage(String publicId) {
		return motechDAO.getMessage(publicId);
	}

	public List<MessageDefinition> getAllMessageDefinitions() {
		return motechDAO.getMessageDefinitions();
	}

	public MessageDefinition getMessageDefinition(String messageKey) {
		return motechDAO.getMessageDefinition(messageKey);
	}

	public List<MessageAttribute> getAllMessageAttributes() {
		return motechDAO.getMessageAttributes();
	}

	public Log saveLog(Log log) {
		return motechDAO.saveLog(log);
	}

	public ScheduledMessage saveScheduledMessage(
			ScheduledMessage scheduledMessage) {
		return motechDAO.saveScheduledMessage(scheduledMessage);
	}

	public Message saveMessage(Message message) {
		return motechDAO.saveMessage(message);
	}

	public MessageDefinition saveMessageDefinition(
			MessageDefinition messageDefinition) {
		return motechDAO.saveMessageDefinition(messageDefinition);
	}

	public MessageAttribute saveMessageAttribute(
			MessageAttribute messageAttribute) {
		return motechDAO.saveMessageAttribute(messageAttribute);
	}

	public List<Integer> getUserIdsByPersonAttribute(
			PersonAttributeType personAttributeType, String value) {
		return motechDAO.getUsersByPersonAttribute(personAttributeType
				.getPersonAttributeTypeId(), value);
	}

	public Blackout getBlackoutSettings() {
		return motechDAO.getBlackoutSettings();
	}

	public void setBlackoutSettings(Blackout blackout) {
		motechDAO.setBlackoutSettings(blackout);
	}

	public TroubledPhone getTroubledPhone(String phoneNumber) {
		return motechDAO.getTroubledPhoneByNumber(phoneNumber);
	}

	public void saveTroubledPhone(TroubledPhone troubledPhone) {
		motechDAO.saveTroubledPhone(troubledPhone);
	}

	public void addTroubledPhone(String phoneNumber) {
		TroubledPhone tp = motechDAO.getTroubledPhoneByNumber(phoneNumber);
		if (tp == null) {
			tp = new TroubledPhone();
			tp.setPhoneNumber(phoneNumber);
			motechDAO.saveTroubledPhone(tp);
		}
	}

	public void removeTroubledPhone(String phoneNumber) {
		TroubledPhone tp = motechDAO.getTroubledPhoneByNumber(phoneNumber);
		if (tp != null) {
			motechDAO.removeTroubledPhone(tp.getId());
		}
	}

	public GeneralPatientEncounter saveGeneralPatientEncounter(
			GeneralPatientEncounter encounter) {
		return motechDAO.saveGeneralPatientEncounter(encounter);
	}

	public List<Location> getAllCountries() {
		return motechDAO.getAllCountries();
	}

	public List<Location> getAllRegions() {
		return motechDAO.getAllRegions();
	}

	public List<Location> getRegions(String country) {
		return motechDAO.getRegions(country);
	}

	public List<Location> getAllDistricts() {
		return motechDAO.getAllDistricts();
	}

	public List<Location> getDistricts(String country, String region) {
		return motechDAO.getDistricts(country, region);
	}

	public List<Location> getAllCommunities() {
		return motechDAO.getAllCommunities();
	}

	public List<Location> getCommunities(String country, String region,
			String district) {
		return motechDAO.getCommunities(country, region, district);
	}

	public List<Location> getAllClinics() {
		return motechDAO.getAllClinics();
	}

	public List<Location> getClinics(String country, String region,
			String district, String community) {
		return motechDAO.getClinics(country, region, district, community);
	}

	public List<Integer> getMatchingPeople(String firstName, String lastName,
			Date birthDate, String community, String phoneNumber,
			Integer primaryPhoneNumberAttrTypeId,
			Integer secondaryPhoneNumberAttrTypeId, String patientId,
			String nhisNumber, Integer nhisAttrTypeId) {
		return motechDAO.getMatchingPeople(firstName, lastName, birthDate,
				community, phoneNumber, primaryPhoneNumberAttrTypeId,
				secondaryPhoneNumberAttrTypeId, patientId, nhisNumber,
				nhisAttrTypeId);
	}

	public List<Obs> getActivePregnancies(Integer patientId,
			Concept pregnancyConcept, Concept pregnancyStatusConcept) {
		return motechDAO.getActivePregnancies(patientId, pregnancyConcept,
				pregnancyStatusConcept);
	}

	public Service saveService(Service service) {
		return motechDAO.saveService(service);
	}

	public List<Service> getServices(Integer patientId, String sequence,
			ServiceStatus status) {
		return motechDAO.getServices(patientId, sequence, status);
	}

	public List<Service> getServices(Integer patientId, String service,
			String sequence, ServiceStatus status) {
		return motechDAO.getServices(patientId, service, sequence, status);
	}

	public ExpectedObs saveExpectedObs(ExpectedObs expectedObs) {
		return motechDAO.saveExpectedObs(expectedObs);
	}

	public void removeExpectedObs(ExpectedObs expectedObs) {
		motechDAO.removeExpectedObs(expectedObs);
	}

	public List<ExpectedObs> getExpectedObs(Person person, Concept concept,
			Concept valueCoded, Double valueNumeric, Date obsDatetime) {
		return motechDAO.getExpectedObs(person, concept, valueCoded,
				valueNumeric, obsDatetime);
	}

	public ExpectedEncounter saveExpectedEncounter(
			ExpectedEncounter expectedEncounter) {
		return motechDAO.saveExpectedEncounter(expectedEncounter);
	}

	public void removeExpectedEncounter(ExpectedEncounter expectedEncounter) {
		motechDAO.removeExpectedEncounter(expectedEncounter);
	}

	public List<ExpectedEncounter> getExpectedEncounter(Patient patient,
			EncounterType encounterType, Date encounterDatetime) {
		return motechDAO.getExpectedEncounter(patient, encounterType,
				encounterDatetime);
	}

	public List<Patient> getPatients(String firstName, String lastName,
			String preferredName, Date birthDate, String community,
			String phoneNumber, PersonAttributeType primaryPhoneNumberAttrType,
			PersonAttributeType secondaryPhoneNumberAttrType,
			String nhisNumber, PersonAttributeType nhisAttrType) {
		return motechDAO.getPatients(firstName, lastName, preferredName,
				birthDate, community, phoneNumber, primaryPhoneNumberAttrType,
				secondaryPhoneNumberAttrType, nhisNumber, nhisAttrType);
	}
}
