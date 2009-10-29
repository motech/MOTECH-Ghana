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

import org.motech.messaging.Message;
import org.motech.messaging.MessageAttribute;
import org.motech.messaging.MessageDefinition;
import org.motech.messaging.MessageStatus;
import org.motech.messaging.ScheduledMessage;
import org.motech.model.Blackout;
import org.motech.model.Log;
import org.motech.model.TroubledPhone;
import org.motech.svc.RegistrarBean;
import org.openmrs.Concept;
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
	List<ScheduledMessage> getScheduledMessages(Integer recipientId,
			Long messageDefinitionId, Date messageDate);

	@Transactional(readOnly = true)
	List<Message> getAllMessages();

	@Transactional(readOnly = true)
	List<Message> getMessages(ScheduledMessage scheduledMessage);

	@Transactional(readOnly = true)
	List<Message> getMessages(Date startDate, Date endDate, MessageStatus status);

	@Transactional(readOnly = true)
	List<Message> getMessages(Integer recipientId, String scheduleGroupId,
			MessageStatus status);

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
	List<String> getObsEnrollment(Integer personId, Concept startConcept,
			Concept endConcept);

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
}
