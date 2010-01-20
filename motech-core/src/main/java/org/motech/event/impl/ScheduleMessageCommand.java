package org.motech.event.impl;

import org.motech.event.MessagesCommand;
import org.motech.messaging.MessageScheduler;

public class ScheduleMessageCommand extends MessagesCommand {

	String messageKey;
	MessageScheduler messageScheduler;

	@Override
	public void execute() {
		if (actionDate == null) {
			return;
		}
		messageScheduler.scheduleMessage(messageKey, enrollment, actionDate);
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
