package org.motech.model.db;

import java.util.Date;
import java.util.List;

import org.motech.messaging.Message;
import org.motech.messaging.MessageAttribute;
import org.motech.messaging.MessageDefinition;
import org.motech.messaging.ScheduledMessage;
import org.motech.model.Blackout;
import org.motech.model.Log;
import org.motech.model.TroubledPhone;

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

	List<Message> getMessages();

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
}
