package org.motech.messaging;

import java.util.Date;

public class Message {

	Long id;
	Long publicId;
	ScheduledMessage schedule;
	Date lastAttempt;
	MessageStatus lastStatus;
	int attempts;
	MessageDefinition definition;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPublicId() {
		return publicId;
	}

	public void setPublicId(Long publicId) {
		this.publicId = publicId;
	}

	public ScheduledMessage getSchedule() {
		return schedule;
	}

	public void setSchedule(ScheduledMessage schedule) {
		this.schedule = schedule;
	}

	public Date getLastAttempt() {
		return lastAttempt;
	}

	public void setLastAttempt(Date lastAttempt) {
		this.lastAttempt = lastAttempt;
	}

	public MessageStatus getLastStatus() {
		return lastStatus;
	}

	public void setLastStatus(MessageStatus lastStatus) {
		this.lastStatus = lastStatus;
	}

	public int getAttempts() {
		return attempts;
	}

	public void setAttempts(int attempts) {
		this.attempts = attempts;
	}

	public MessageDefinition getDefinition() {
		return definition;
	}

	public void setDefinition(MessageDefinition definition) {
		this.definition = definition;
	}

}
