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

import org.motechproject.server.messaging.MessageDefDate;
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
import org.motechproject.server.model.db.MotechDAO;
import org.motechproject.server.omod.MotechService;
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

	public List<MessageProgramEnrollment> getActiveMessageProgramEnrollments(
			Integer personId, String program, Integer obsId,
			Long minExclusiveId, Long maxInclusiveId, Integer maxResults) {
		return motechDAO.getActiveMessageProgramEnrollments(personId, program,
				obsId, minExclusiveId, maxInclusiveId, maxResults);
	}

	public Long getMaxMessageProgramEnrollmentId() {
		return motechDAO.getMaxMessageProgramEnrollmentId();
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

	public List<Message> getMessages(Integer recipientId,
			MessageProgramEnrollment enrollment,
			MessageDefDate[] messageDefDates, MessageStatus status) {
		return motechDAO.getMessages(recipientId, enrollment, messageDefDates,
				status);
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

	public GeneralOutpatientEncounter saveGeneralOutpatientEncounter(
			GeneralOutpatientEncounter encounter) {
		return motechDAO.saveGeneralOutpatientEncounter(encounter);
	}

	public List<String> getAllCountries() {
		return motechDAO.getAllCountries();
	}

	public List<String> getAllRegions() {
		return motechDAO.getAllRegions();
	}

	public List<String> getRegions(String country) {
		return motechDAO.getRegions(country);
	}

	public List<String> getAllDistricts() {
		return motechDAO.getAllDistricts();
	}

	public List<String> getDistricts(String country, String region) {
		return motechDAO.getDistricts(country, region);
	}

	public List<Obs> getActivePregnancies(Integer patientId,
			Concept pregnancyConcept, Concept pregnancyStatusConcept) {
		return motechDAO.getActivePregnancies(patientId, pregnancyConcept,
				pregnancyStatusConcept);
	}

	public List<Obs> getActivePregnanciesDueDateObs(Facility facility,
			Date fromDueDate, Date toDueDate, Concept pregnancyDueDateConcept,
			Concept pregnancyConcept, Concept pregnancyStatusConcept,
			Integer maxResults) {
		return motechDAO.getActivePregnanciesDueDateObs(facility, fromDueDate,
				toDueDate, pregnancyDueDateConcept, pregnancyConcept,
				pregnancyStatusConcept, maxResults);
	}

	public List<Encounter> getEncounters(Facility facility,
			EncounterType encounterType, Date fromDate, Date toDate,
			Integer maxResults) {
		return motechDAO.getEncounters(facility, encounterType, fromDate,
				toDate, maxResults);
	}

	public ExpectedObs saveExpectedObs(ExpectedObs expectedObs) {
		return motechDAO.saveExpectedObs(expectedObs);
	}

	public List<ExpectedObs> getExpectedObs(Patient patient, Facility facility,
			String[] groups, Date minDueDate, Date maxDueDate,
			Date maxLateDate, Date minMaxDate, Integer maxResults) {
		return motechDAO.getExpectedObs(patient, facility, groups, minDueDate,
				maxDueDate, maxLateDate, minMaxDate, maxResults);
	}

	public ExpectedEncounter saveExpectedEncounter(
			ExpectedEncounter expectedEncounter) {
		return motechDAO.saveExpectedEncounter(expectedEncounter);
	}

	public List<ExpectedEncounter> getExpectedEncounter(Patient patient,
			Facility facility, String[] groups, Date minDueDate,
			Date maxDueDate, Date maxLateDate, Date minMaxDate,
			Integer maxResults) {
		return motechDAO.getExpectedEncounter(patient, facility, groups,
				minDueDate, maxDueDate, maxLateDate, minMaxDate, maxResults);
	}

	public Facility saveFacility(Facility facility) {
		return motechDAO.saveFacility(facility);
	}

	public List<Patient> getPatients(String firstName, String lastName,
			String preferredName, Date birthDate, Integer communityId,
			String phoneNumber, PersonAttributeType phoneNumberAttrType,
			String nhisNumber, PersonAttributeType nhisAttrType,
			String patientId, PatientIdentifierType patientIdType,
			Integer maxResults) {
		return motechDAO.getPatients(firstName, lastName, preferredName,
				birthDate, communityId, phoneNumber, phoneNumberAttrType,
				nhisNumber, nhisAttrType, patientId, patientIdType, maxResults);
	}

	public List<Patient> getDuplicatePatients(String firstName,
			String lastName, String preferredName, Date birthDate,
			Integer communityId, String phoneNumber,
			PersonAttributeType phoneNumberAttrType, String nhisNumber,
			PersonAttributeType nhisAttrType, String patientId,
			PatientIdentifierType patientIdType, Integer maxResults) {
		return motechDAO.getDuplicatePatients(firstName, lastName,
				preferredName, birthDate, communityId, phoneNumber,
				phoneNumberAttrType, nhisNumber, nhisAttrType, patientId,
				patientIdType, maxResults);
	}

	public Facility getFacilityById(Integer facilityId) {
		return motechDAO.getFacilityByFacilityId(facilityId);
	}

	public List<Facility> getAllFacilities() {
		return motechDAO.getAllFacilities();
	}

	public Community getCommunityById(Integer communityId) {
		return motechDAO.getCommunityByCommunityId(communityId);
	}

	public List<Community> getAllCommunities(boolean includeRetired) {
		return motechDAO.getAllCommunities(includeRetired);
	}

	public List<Community> getCommunities(String country, String region,
			String district, boolean includeRetired) {
		return motechDAO.getCommunities(country, region, district,
				includeRetired);
	}

	public Community getCommunityByPatient(Patient patient) {
		return motechDAO.getCommunityByPatient(patient);
	}

}
