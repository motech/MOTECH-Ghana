package org.motech.messaging;

import java.util.Date;

public class ScheduledMessage {

	Long id;
	Date scheduledFor;
	MessageDefinition message;
	Long recipientId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getScheduledFor() {
		return scheduledFor;
	}

	public void setScheduledFor(Date scheduledFor) {
		this.scheduledFor = scheduledFor;
	}

	public MessageDefinition getMessage() {
		return message;
	}

	public void setMessage(MessageDefinition message) {
		this.message = message;
	}

	public Long getRecipientId() {
		return recipientId;
	}

	public void setRecipientId(Long recipientId) {
		this.recipientId = recipientId;
	}

}
