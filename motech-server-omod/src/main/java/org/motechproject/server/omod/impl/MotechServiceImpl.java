/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010-11 The Trustees of Columbia University in the City of
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

package org.motechproject.server.omod.impl;

import org.motechproject.server.messaging.MessageDefDate;
import org.motechproject.server.model.*;
import org.motechproject.server.model.db.MotechDAO;
import org.motechproject.server.omod.MotechService;
import org.motechproject.server.omod.sdsched.ScheduleMaintService;
import org.motechproject.server.svc.OpenmrsBean;
import org.motechproject.server.svc.RCTService;
import org.motechproject.server.svc.RegistrarBean;
import org.openmrs.*;
import org.openmrs.api.impl.BaseOpenmrsService;

import java.util.Date;
import java.util.List;

/**
 * An implementation of the MotechService interface using OpenMRS.
 */
public class MotechServiceImpl extends BaseOpenmrsService implements
		MotechService {

	private MotechDAO motechDAO;

	private RegistrarBean registrarBean;

	private OpenmrsBean openmrsBean;

	private ScheduleMaintService scheduleService;
    
    private RCTService rctService;

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
            Long minExclusiveId, Integer maxResults) {
		return motechDAO.getActiveMessageProgramEnrollments(personId, program,
                obsId, minExclusiveId, maxResults);
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

    public Community saveCommunity(Community community) {
        return  motechDAO.saveCommunity(community);
    }

    public Community getCommunityByFacilityIdAndName(Integer facilityId, String name) {
        return motechDAO.getCommunityByFacilityIdAndName(facilityId, name);
    }
    
    public List<Patient> getAllDuplicatePatients() {
        return motechDAO.getAllDuplicatePatients();
    }
    public Location getLocationByName(String name) {
        return motechDAO.getLocationByName(name);
    }

    public void deletePatientIdentifier(Integer patientId) {
        motechDAO.deletePatientIdentifier(patientId);
    }

    public void stopEnrollmentFor(Integer patientId) {
        registrarBean.stopEnrollmentFor(patientId);
    }

    public Facility facilityFor(Patient patient) {
        return motechDAO.facilityFor(patient);
    }

    public RCTService getRctService(){
        return rctService;
    }

    public void setRctService(RCTService rctService) {
        this.rctService = rctService;
    }
}
