package org.motech.messaging;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motech.openmrs.module.ContextService;
import org.motech.openmrs.module.MotechService;

public class MessageSchedulerImpl implements MessageScheduler {

	private static Log log = LogFactory.getLog(MessageSchedulerImpl.class);

	private ContextService contextService;

	public void scheduleMessage(String messageKey, Long publicId,
			String messageGroup, Integer messageRecipientId, Date messageDate) {

		// Create message definition if it does not already exist,
		// or return existing
		MessageDefinition messageDefinition = createMessageDefinition(
				messageKey, publicId);

		// Cancel any unsent messages for the same group, unless matching the
		// message to schedule
		removeUnsentMessages(messageRecipientId, messageGroup,
				messageDefinition, messageDate);

		// Create new scheduled message (with pending attempt) for group
		// if none matching already exist
		createScheduledMessage(messageRecipientId, messageDefinition,
				messageGroup, messageDate);
	}

	private MessageDefinition createMessageDefinition(String messageKey,
			Long publicId) {
		MotechService motechService = contextService.getMotechService();
		MessageDefinition messageDefinition = motechService
				.getMessageDefinition(messageKey);
		if (messageDefinition == null) {
			log.info(messageKey
					+ " MessageDefinition Does Not Exist - Creating");

			messageDefinition = new MessageDefinition();
			messageDefinition.setMessageKey(messageKey);
			messageDefinition.setPublicId(publicId);
			messageDefinition = motechService
					.saveMessageDefinition(messageDefinition);
		}
		return messageDefinition;
	}

	protected void removeUnsentMessages(Integer recipientId,
			String messageGroup, MessageDefinition messageDefinition,
			Date messageDate) {
		MotechService motechService = contextService.getMotechService();
		List<Message> unsentMessages = motechService.getMessages(recipientId,
				messageGroup, MessageStatus.SHOULD_ATTEMPT);
		log.debug("Unsent messages found: " + unsentMessages.size());

		for (Message unsentMessage : unsentMessages) {
			ScheduledMessage messageSchedule = unsentMessage.getSchedule();

			if (log.isDebugEnabled()) {
				log.debug("Found message definition id: "
						+ messageSchedule.getMessage().getId()
						+ ", schedule date: "
						+ messageSchedule.getScheduledFor()
						+ ", New message definintion id: "
						+ messageDefinition.getId() + ", new schedule date: "
						+ messageDate);
			}

			if (!messageDefinition.getId().equals(
					messageSchedule.getMessage().getId())
					|| !messageDate.equals(messageSchedule.getScheduledFor())) {

				unsentMessage.setAttemptStatus(MessageStatus.CANCELLED);
				motechService.saveMessage(unsentMessage);

				log.debug("Message cancelled: Id: " + unsentMessage.getId());
			}
		}
	}

	public void removeAllUnsentMessages(Integer recipientId, String messageGroup) {
		MotechService motechService = contextService.getMotechService();
		List<Message> unsentMessages = motechService.getMessages(recipientId,
				messageGroup, MessageStatus.SHOULD_ATTEMPT);
		log.debug("Unsent messages found: " + unsentMessages.size());

		for (Message unsentMessage : unsentMessages) {

			unsentMessage.setAttemptStatus(MessageStatus.CANCELLED);
			motechService.saveMessage(unsentMessage);

			log.debug("Message cancelled: Id: " + unsentMessage.getId());
		}
	}

	private void createScheduledMessage(Integer recipientId,
			MessageDefinition messageDefinition, String messageGroup,
			Date messageDate) {
		MotechService motechService = contextService.getMotechService();
		List<ScheduledMessage> scheduledMessages = motechService
				.getScheduledMessages(recipientId, messageDefinition.getId(),
						messageDate);
		if (scheduledMessages.size() == 0) {
			log.info(recipientId + ", " + messageDefinition.getId() + ", "
					+ messageDate
					+ " - ScheduledMessage Does Not Exist - Creating");

			ScheduledMessage scheduledMessage = new ScheduledMessage();
			scheduledMessage.setScheduledFor(messageDate);
			scheduledMessage.setRecipientId(recipientId);
			scheduledMessage.setMessage(messageDefinition);
			scheduledMessage.getGroupIds().add(messageGroup);

			Message message = messageDefinition.createMessage(scheduledMessage);
			message.setAttemptDate(messageDate);
			scheduledMessage.getMessageAttempts().add(message);

			motechService.saveScheduledMessage(scheduledMessage);
		}
	}

	public ContextService getContextService() {
		return contextService;
	}

	public void setContextService(ContextService contextService) {
		this.contextService = contextService;
	}

}
