package org.motech.messaging;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motech.openmrs.module.MotechService;
import org.openmrs.api.context.Context;

public class MessageSchedulerImpl implements MessageScheduler {

	private static Log log = LogFactory.getLog(MessageSchedulerImpl.class);

	public void scheduleMessage(String messageKey, Long publicId,
			Integer messageRecipientId, Date messageDate) {

		// Create message definition if it does not already exist,
		// or return existing
		MessageDefinition messageDefinition = createMessageDefinition(
				messageKey, publicId);

		// Create new scheduled message (with pending attempt)
		// if none matching already exist
		createScheduledMessage(messageRecipientId, messageDefinition,
				messageDate);
	}

	private MessageDefinition createMessageDefinition(String messageKey,
			Long publicId) {
		MessageDefinition messageDefinition = Context.getService(
				MotechService.class).getMessageDefinition(messageKey);
		if (messageDefinition == null) {
			log.info(messageKey
					+ " MessageDefinition Does Not Exist - Creating");

			messageDefinition = new MessageDefinition();
			messageDefinition.setMessageKey(messageKey);
			messageDefinition.setPublicId(publicId);
			messageDefinition = Context.getService(MotechService.class)
					.saveMessageDefinition(messageDefinition);
		}
		return messageDefinition;
	}

	private void createScheduledMessage(Integer recipientId,
			MessageDefinition messageDefinition, Date messageDate) {
		List<ScheduledMessage> scheduledMessages = Context.getService(
				MotechService.class).getScheduledMessages(recipientId,
				messageDefinition.getId(), messageDate);
		if (scheduledMessages.size() == 0) {
			log.info(recipientId + ", " + messageDefinition.getId() + ", "
					+ messageDate
					+ " - ScheduledMessage Does Not Exist - Creating");

			ScheduledMessage scheduledMessage = new ScheduledMessage();
			scheduledMessage.setScheduledFor(messageDate);
			scheduledMessage.setRecipientId(recipientId);
			scheduledMessage.setMessage(messageDefinition);

			Message message = messageDefinition.createMessage(scheduledMessage);
			message.setAttemptDate(messageDate);
			scheduledMessage.getMessageAttempts().add(message);

			Context.getService(MotechService.class).saveScheduledMessage(
					scheduledMessage);
		}
	}
}
