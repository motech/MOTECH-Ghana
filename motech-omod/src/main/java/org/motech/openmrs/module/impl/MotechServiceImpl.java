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
import org.motech.model.GeneralPatientEncounter;
import org.motech.model.Log;
import org.motech.model.Message;
import org.motech.model.MessageAttribute;
import org.motech.model.MessageDefinition;
import org.motech.model.MessageProgramEnrollment;
import org.motech.model.MessageStatus;
import org.motech.model.ScheduledMessage;
import org.motech.model.TroubledPhone;
import org.motech.model.db.MotechDAO;
import org.motech.openmrs.module.MotechService;
import org.motech.svc.RegistrarBean;
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

	public List<String> getActiveMessageProgramEnrollments(Integer personId) {
		return motechDAO.getActiveMessageProgramEnrollments(personId);
	}

	public MessageProgramEnrollment getActiveMessageProgramEnrollment(
			Integer personId, String program) {
		return motechDAO.getActiveMessageProgramEnrollment(personId, program);
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
			Long messageDefinitionId, Date messageDate) {
		return motechDAO.getScheduledMessages(recipientId, messageDefinitionId,
				messageDate);
	}

	public List<Message> getMessages(Integer recipientId,
			String scheduleGroupId, MessageStatus status) {
		return motechDAO.getMessages(recipientId, scheduleGroupId, status);
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
}
