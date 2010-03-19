package org.motechproject.server.model;

import java.util.Date;

public class Message {

	Long id;
	String publicId;
	ScheduledMessage schedule;
	Date attemptDate;
	MessageStatus attemptStatus;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPublicId() {
		return publicId;
	}

	public void setPublicId(String publicId) {
		this.publicId = publicId;
	}

	public ScheduledMessage getSchedule() {
		return schedule;
	}

	public void setSchedule(ScheduledMessage schedule) {
		this.schedule = schedule;
	}

	public Date getAttemptDate() {
		return attemptDate;
	}

	public void setAttemptDate(Date attemptDate) {
		this.attemptDate = attemptDate;
	}

	public MessageStatus getAttemptStatus() {
		return attemptStatus;
	}

	public void setAttemptStatus(MessageStatus attemptStatus) {
		this.attemptStatus = attemptStatus;
	}

}
