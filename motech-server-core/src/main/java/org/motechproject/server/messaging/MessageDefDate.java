package org.motechproject.server.messaging;

import java.util.Date;

import org.motechproject.server.model.MessageDefinition;

public class MessageDefDate {

	MessageDefinition message;
	Date date;

	public MessageDefDate(MessageDefinition message, Date date) {
		this.message = message;
		this.date = date;
	}

	public MessageDefinition getMessage() {
		return message;
	}

	public void setMessage(MessageDefinition message) {
		this.message = message;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
