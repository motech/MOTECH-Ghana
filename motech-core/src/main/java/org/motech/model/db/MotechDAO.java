package org.motech.model.db;

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
import org.openmrs.Concept;

/**
 * The interface definition for the motech server's data access requirements.
 */
public interface MotechDAO {

	Log saveLog(Log log);

	List<Log> getLogs();

	List<Integer> getUsersByPersonAttribute(Integer personAttributeTypeId,
			String personAttributeValue);

	ScheduledMessage saveScheduledMessage(ScheduledMessage scheduledMessage);

	Message saveMessage(Message Message);

	MessageDefinition saveMessageDefinition(MessageDefinition messageDefinition);

	MessageAttribute saveMessageAttribute(MessageAttribute messageAttribute);

	List<ScheduledMessage> getScheduledMessages();

	List<ScheduledMessage> getScheduledMessages(Date startDate, Date endDate);

	List<ScheduledMessage> getScheduledMessages(Integer recipientId,
			Long messageDefinitionId, Date messageDate);

	List<Message> getMessages();

	List<Message> getMessages(Date startDate, Date endDate, MessageStatus status);

	List<Message> getMessages(Integer recipientId, String scheduleGroupId,
			MessageStatus status);

	Message getMessage(String publicId);

	List<Message> getMessages(ScheduledMessage scheduledMessage);

	List<MessageDefinition> getMessageDefinitions();

	MessageDefinition getMessageDefinition(String messageKey);

	Blackout getBlackoutSettings();

	void setBlackoutSettings(Blackout blackout);

	List<MessageAttribute> getMessageAttributes();

	TroubledPhone getTroubledPhone(Long id);

	TroubledPhone getTroubledPhoneByNumber(String phoneNumber);

	void removeTroubledPhone(Long id);

	void saveTroubledPhone(TroubledPhone phone);

	List<String> getObsEnrollment(Integer personId, Concept startConcept,
			Concept endConcept);
}
