package org.motechproject.server.omod.web.model;

import java.util.Date;

import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.MediaType;

public class WebMessage {

	Integer motechId;
	String phoneNumber;
	ContactNumberType phoneType;
	String language;
	MediaType mediaType;
	Long notificationType;
	Date startDate;
	Date endDate;

	public Integer getMotechId() {
		return motechId;
	}

	public void setMotechId(Integer motechId) {
		this.motechId = motechId;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public ContactNumberType getPhoneType() {
		return phoneType;
	}

	public void setPhoneType(ContactNumberType phoneType) {
		this.phoneType = phoneType;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
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

}
