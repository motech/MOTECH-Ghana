package org.motech.event.impl;

import java.util.Date;

import org.motech.event.Command;
import org.motech.messaging.MessageScheduler;

public class ScheduleMessageCommand implements Command {

	String messageKey;
	String messageGroup;
	Integer messageRecipientId;
	Date messageDate;
	MessageScheduler messageScheduler;

	public void execute() {

		messageScheduler.scheduleMessage(messageKey, messageGroup,
				messageRecipientId, messageDate);
	}

	public String getMessageKey() {
		return messageKey;
	}

	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}

	public String getMessageGroup() {
		return messageGroup;
	}

	public void setMessageGroup(String messageGroup) {
		this.messageGroup = messageGroup;
	}

	public Integer getMessageRecipientId() {
		return messageRecipientId;
	}

	public void setMessageRecipientId(Integer messageRecipientId) {
		this.messageRecipientId = messageRecipientId;
	}

	public Date getMessageDate() {
		return messageDate;
	}

	public void setMessageDate(Date messageDate) {
		this.messageDate = messageDate;
	}

	public MessageScheduler getMessageScheduler() {
		return messageScheduler;
	}

	public void setMessageScheduler(MessageScheduler messageScheduler) {
		this.messageScheduler = messageScheduler;
	}

}
