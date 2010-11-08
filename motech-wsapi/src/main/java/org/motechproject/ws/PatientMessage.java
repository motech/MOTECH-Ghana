package org.motechproject.ws;

import java.util.Date;

public class PatientMessage {

	String messageId;
	NameValuePair[] personalInfo;
	String patientNumber;
	ContactNumberType patientNumberType;
	String langCode;
	MediaType mediaType;
	Long notificationType;
	Date startDate;
	Date endDate;
	String recipientId;

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public NameValuePair[] getPersonalInfo() {
		return personalInfo;
	}

	public void setPersonalInfo(NameValuePair[] personalInfo) {
		this.personalInfo = personalInfo;
	}

	public String getPatientNumber() {
		return patientNumber;
	}

	public void setPatientNumber(String patientNumber) {
		this.patientNumber = patientNumber;
	}

	public ContactNumberType getPatientNumberType() {
		return patientNumberType;
	}

	public void setPatientNumberType(ContactNumberType patientNumberType) {
		this.patientNumberType = patientNumberType;
	}

	public String getLangCode() {
		return langCode;
	}

	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	public MediaType getMediaType() {
		return mediaType;
	}

	public void setMediaType(MediaType mediaType) {
		this.mediaType = mediaType;
	}

	public Long getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(Long notificationType) {
		this.notificationType = notificationType;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getRecipientId() {
		return recipientId;
	}

	public void setRecipientId(String recipientId) {
		this.recipientId = recipientId;
	}

}
