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

import org.motech.messaging.Message;
import org.motech.messaging.MessageAttribute;
import org.motech.messaging.MessageDefinition;
import org.motech.messaging.ScheduledMessage;
import org.motech.model.Blackout;
import org.motech.model.Log;
import org.motech.model.TroubledPhone;
import org.motech.model.db.MotechDAO;
import org.motech.openmrs.module.MotechService;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;

import com.dreamoval.motech.webapp.webservices.MessageService;

/**
 * An implementation of the MotechService interface using OpenMRS.
 */
public class MotechServiceImpl extends BaseOpenmrsService implements
		MotechService {

	private MotechDAO motechDAO;
	private MessageService mobileService;

	public MotechDAO getMotechDAO() {
		return motechDAO;
	}

	public void setMotechDAO(MotechDAO motechDAO) {
		this.motechDAO = motechDAO;
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

	public User getUserByPhoneNumber(String phoneNumber) {
		Integer phoneAttributeTypeId = Context.getPersonService()
				.getPersonAttributeTypeByName("Phone Number")
				.getPersonAttributeTypeId();
		// If more than one user matches phone number, first user in list is
		// returned
		Integer userId = motechDAO.getUsersByPersonAttribute(
				phoneAttributeTypeId, phoneNumber).get(0);
		return Context.getUserService().getUser(userId);
	}

	public MessageService getMobileService() {
		return mobileService;
	}

	public void setMobileService(MessageService mobileService) {
		this.mobileService = mobileService;
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
}
