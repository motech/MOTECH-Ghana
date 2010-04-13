package org.motechproject.server.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScheduledMessage {

	Long id;
	Date scheduledFor;
	MessageDefinition message;
	Integer recipientId;
	MessageProgramEnrollment enrollment;
	List<Message> messageAttempts = new ArrayList<Message>();
	String care;

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

	public Integer getRecipientId() {
		return recipientId;
	}

	public void setRecipientId(Integer recipientId) {
		this.recipientId = recipientId;
	}

	public MessageProgramEnrollment getEnrollment() {
		return enrollment;
	}

	public void setEnrollment(MessageProgramEnrollment enrollment) {
		this.enrollment = enrollment;
	}

	public List<Message> getMessageAttempts() {
		return messageAttempts;
	}

	public void setMessageAttempts(List<Message> messageAttempts) {
		this.messageAttempts = messageAttempts;
	}

	public String getCare() {
		return care;
	}

	public void setCare(String care) {
		this.care = care;
	}
}
