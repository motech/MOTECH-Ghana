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
package org.motechproject.server.omod.impl;

import java.util.Date;
import java.util.List;

import org.motechproject.server.model.Blackout;
import org.motechproject.server.model.ExpectedEncounter;
import org.motechproject.server.model.ExpectedObs;
import org.motechproject.server.model.GeneralPatientEncounter;
import org.motechproject.server.model.Log;
import org.motechproject.server.model.Message;
import org.motechproject.server.model.MessageAttribute;
import org.motechproject.server.model.MessageDefinition;
import org.motechproject.server.model.MessageProgramEnrollment;
import org.motechproject.server.model.MessageStatus;
import org.motechproject.server.model.ScheduledMessage;
import org.motechproject.server.model.TroubledPhone;
import org.motechproject.server.model.db.MotechDAO;
import org.motechproject.server.omod.MotechService;
import org.motechproject.server.omod.sdsched.ScheduleMaintService;
import org.motechproject.server.svc.OpenmrsBean;
import org.motechproject.server.svc.RegistrarBean;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.impl.BaseOpenmrsService;

/**
 * An implementation of the MotechService interface using OpenMRS.
 */
public class MotechServiceImpl extends BaseOpenmrsService implements
		MotechService {

	private MotechDAO motechDAO;

	private RegistrarBean registrarBean;
	
	private OpenmrsBean openmrsBean;

	private ScheduleMaintService scheduleService;

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

	public OpenmrsBean getOpenmrsBean() {
		return openmrsBean;
	}

	public void setOpenmrsBean(OpenmrsBean openmrsBean) {
		this.openmrsBean = openmrsBean;
	}

	public ScheduleMaintService getScheduleService() {
		return scheduleService;
	}

	public void setScheduleService(ScheduleMaintService scheduleService) {
		this.scheduleService = scheduleService;
	}

	public void setScheduleMaintService(ScheduleMaintService scheduleService) {
		this.scheduleService = scheduleService;
	}

	public ScheduleMaintService getScheduleMaintService() {
		return scheduleService;
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

	public List<Obs> getActivePregnanciesDueDateObs(Date fromDueDate,
			Date toDueDate, Concept pregnancyDueDateConcept,
			Concept pregnancyConcept, Concept pregnancyStatusConcept) {
		return motechDAO.getActivePregnanciesDueDateObs(fromDueDate, toDueDate,
				pregnancyDueDateConcept, pregnancyConcept,
				pregnancyStatusConcept);
	}

	public ExpectedObs saveExpectedObs(ExpectedObs expectedObs) {
		return motechDAO.saveExpectedObs(expectedObs);
	}

	public List<ExpectedObs> getExpectedObs(Patient patient, String[] groups,
			Date minDueDate, Date maxDueDate, Date maxLateDate,
			Date minMaxDate, boolean nameOrdering) {
		return motechDAO.getExpectedObs(patient, groups, minDueDate,
				maxDueDate, maxLateDate, minMaxDate, nameOrdering);
	}

	public ExpectedEncounter saveExpectedEncounter(
			ExpectedEncounter expectedEncounter) {
		return motechDAO.saveExpectedEncounter(expectedEncounter);
	}

	public List<ExpectedEncounter> getExpectedEncounter(Patient patient,
			String[] groups, Date minDueDate, Date maxDueDate,
			Date maxLateDate, Date minMaxDate, boolean nameOrdering) {
		return motechDAO.getExpectedEncounter(patient, groups, minDueDate,
				maxDueDate, maxLateDate, minMaxDate, nameOrdering);
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
