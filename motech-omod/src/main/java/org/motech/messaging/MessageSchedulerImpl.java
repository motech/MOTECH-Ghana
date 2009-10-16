package org.motech.messaging;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motech.openmrs.module.ContextService;
import org.motech.openmrs.module.MotechService;
import org.motechproject.ws.DeliveryTime;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;

public class MessageSchedulerImpl implements MessageScheduler {

	private static Log log = LogFactory.getLog(MessageSchedulerImpl.class);

	private ContextService contextService;
	private Boolean userPreferenceBased = false;

	public Boolean getUserPreferenceBased() {
		return userPreferenceBased;
	}

	public void setUserPreferenceBased(Boolean userPreferenceBased) {
		this.userPreferenceBased = userPreferenceBased;
	}

	public void scheduleMessage(String messageKey, String messageGroup,
			Integer messageRecipientId, Date messageDate) {

		// Return existing message definition
		MessageDefinition messageDefinition = getMessageDefinition(messageKey);

		// Cancel any unsent messages for the same group, unless matching the
		// message to schedule
		removeUnsentMessages(messageRecipientId, messageGroup,
				messageDefinition, messageDate);

		// Create new scheduled message (with pending attempt) for group
		// if none matching already exist
		createScheduledMessage(messageRecipientId, messageDefinition,
				messageGroup, messageDate);
	}

	private MessageDefinition getMessageDefinition(String messageKey) {
		MotechService motechService = contextService.getMotechService();
		MessageDefinition messageDefinition = motechService
				.getMessageDefinition(messageKey);
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

		Date scheduledMessageDate;
		if (!userPreferenceBased) {
			scheduledMessageDate = messageDate;
		} else {
			Person recipient = contextService.getPersonService().getPerson(
					recipientId);
			PersonAttributeType deliveryTimeType = contextService
					.getPersonService().getPersonAttributeTypeByName(
							"Delivery Time");
			PersonAttribute deliveryTimeAttr = recipient
					.getAttribute(deliveryTimeType);
			String deliveryTimeString = deliveryTimeAttr.getValue();
			DeliveryTime deliveryTime = DeliveryTime.ANYTIME;
			if (deliveryTimeString != null) {
				deliveryTime = DeliveryTime.valueOf(deliveryTimeString);
			}

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(messageDate);
			switch (deliveryTime) {
			case MORNING:
				calendar.set(Calendar.HOUR_OF_DAY, 9);
				break;
			case AFTERNOON:
				calendar.set(Calendar.HOUR_OF_DAY, 13);
				break;
			case EVENING:
				calendar.set(Calendar.HOUR_OF_DAY, 18);
				break;
			default:
				calendar.set(Calendar.HOUR_OF_DAY, 9);
				break;
			}
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			scheduledMessageDate = calendar.getTime();
		}

		List<ScheduledMessage> scheduledMessages = motechService
				.getScheduledMessages(recipientId, messageDefinition.getId(),
						messageDate);
		if (scheduledMessages.size() == 0) {
			log.info(recipientId + ", " + messageDefinition.getId() + ", "
					+ messageDate
					+ " - ScheduledMessage Does Not Exist - Creating");

			ScheduledMessage scheduledMessage = new ScheduledMessage();
			scheduledMessage.setScheduledFor(scheduledMessageDate);
			scheduledMessage.setRecipientId(recipientId);
			scheduledMessage.setMessage(messageDefinition);
			scheduledMessage.getGroupIds().add(messageGroup);

			Message message = messageDefinition.createMessage(scheduledMessage);
			message.setAttemptDate(scheduledMessageDate);
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
