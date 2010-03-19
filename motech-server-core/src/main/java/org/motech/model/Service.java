package org.motech.model;

import java.util.Date;

public class Service {

	Long id;
	Integer patientId;
	String service;
	String sequence;
	Date earliest;
	Date preferredStart;
	Date preferredEnd;
	Date latest;
	ServiceStatus status;
	Integer deliveryId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getPatientId() {
		return patientId;
	}

	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public Date getEarliest() {
		return earliest;
	}

	public void setEarliest(Date earliest) {
		this.earliest = earliest;
	}

	public Date getPreferredStart() {
		return preferredStart;
	}

	public void setPreferredStart(Date preferredStart) {
		this.preferredStart = preferredStart;
	}

	public Date getPreferredEnd() {
		return preferredEnd;
	}

	public void setPreferredEnd(Date preferredEnd) {
		this.preferredEnd = preferredEnd;
	}

	public Date getLatest() {
		return latest;
	}

	public void setLatest(Date latest) {
		this.latest = latest;
	}

	public ServiceStatus getStatus() {
		return status;
	}

	public void setStatus(ServiceStatus status) {
		this.status = status;
	}

	public Integer getDeliveryId() {
		return deliveryId;
	}

	public void setDeliveryId(Integer deliveryId) {
		this.deliveryId = deliveryId;
	}

}
