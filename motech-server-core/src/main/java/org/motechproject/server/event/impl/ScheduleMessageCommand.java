package org.motechproject.server.event.impl;

import java.util.Date;

import org.motechproject.server.event.MessagesCommand;
import org.motechproject.server.messaging.MessageScheduler;
import org.motechproject.server.model.MessageProgramEnrollment;

public class ScheduleMessageCommand extends MessagesCommand {

	String messageKey;
	String messageKeyA;
	String messageKeyB;
	String messageKeyC;
	MessageScheduler messageScheduler;

	@Override
	public void execute(MessageProgramEnrollment enrollment, Date actionDate) {
		if (actionDate == null) {
			return;
		}
		messageScheduler.scheduleMessages(messageKey, messageKeyA, messageKeyB,
				messageKeyC, enrollment, actionDate);
	}

	@Override
	public Date adjustActionDate(MessageProgramEnrollment enrollment,
			Date actionDate, Date currentDate) {
		return messageScheduler.adjustMessageDate(enrollment, actionDate,
				currentDate);
	}

	public String getMessageKey() {
		return messageKey;
	}

	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}

	public String getMessageKeyA() {
		return messageKeyA;
	}

	public void setMessageKeyA(String messageKeyA) {
		this.messageKeyA = messageKeyA;
	}

	public String getMessageKeyB() {
		return messageKeyB;
	}

	public void setMessageKeyB(String messageKeyB) {
		this.messageKeyB = messageKeyB;
	}

	public String getMessageKeyC() {
		return messageKeyC;
	}

	public void setMessageKeyC(String messageKeyC) {
		this.messageKeyC = messageKeyC;
	}

	public MessageScheduler getMessageScheduler() {
		return messageScheduler;
	}

	public void setMessageScheduler(MessageScheduler messageScheduler) {
		this.messageScheduler = messageScheduler;
	}

}
