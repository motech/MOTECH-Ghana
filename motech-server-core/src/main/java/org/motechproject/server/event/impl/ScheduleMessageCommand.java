package org.motechproject.server.event.impl;

import java.util.Date;

import org.motechproject.server.event.MessagesCommand;
import org.motechproject.server.messaging.MessageScheduler;
import org.motechproject.server.model.MessageProgramEnrollment;

public class ScheduleMessageCommand extends MessagesCommand {

	String messageKey;
	MessageScheduler messageScheduler;

	@Override
	public void execute(MessageProgramEnrollment enrollment, Date actionDate) {
		if (actionDate == null) {
			return;
		}
		messageScheduler.scheduleMessage(messageKey, enrollment, actionDate);
	}

	@Override
	public Date adjustActionDate(MessageProgramEnrollment enrollment,
			Date actionDate) {
		return messageScheduler.adjustMessageDate(enrollment, actionDate);
	}

	public String getMessageKey() {
		return messageKey;
	}

	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}

	public MessageScheduler getMessageScheduler() {
		return messageScheduler;
	}

	public void setMessageScheduler(MessageScheduler messageScheduler) {
		this.messageScheduler = messageScheduler;
	}

}
