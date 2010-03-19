package org.motechproject.server.model;

import java.sql.Timestamp;

public class TroubledPhone {

	private Long id;
	private String phoneNumber;
	private Timestamp creationTime;
	private Integer sendFailures;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Timestamp getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Timestamp creationTime) {
		this.creationTime = creationTime;
	}

	public Integer getSendFailures() {
		return sendFailures;
	}

	public void setSendFailures(Integer sendFailures) {
		this.sendFailures = sendFailures;
	}

}
