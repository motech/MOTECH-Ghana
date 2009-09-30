package org.motech.event.impl;

import java.util.Date;

import org.motech.event.Command;
import org.motech.messaging.MessageScheduler;

public class ScheduleMessageCommand implements Command {

	String messageKey;
	Long publicId;
	Integer messageRecipientId;
	Date messageDate;
	MessageScheduler messageScheduler;

	public void execute() {

		messageScheduler.scheduleMessage(messageKey, publicId,
				messageRecipientId, messageDate);
	}

	public String getMessageKey() {
		return messageKey;
	}

	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}

	public Long getPublicId() {
		return publicId;
	}

	public void setPublicId(Long publicId) {
		this.publicId = publicId;
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
