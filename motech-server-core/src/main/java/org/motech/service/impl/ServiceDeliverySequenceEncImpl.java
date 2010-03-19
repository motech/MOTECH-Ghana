package org.motech.service.impl;

import java.util.Date;

import org.motech.service.ServiceDelivery;

public class ServiceDeliverySequenceEncImpl extends ServiceDeliverySequenceImpl {

	private String encounterTypeName;

	public String getEncounterTypeName() {
		return encounterTypeName;
	}

	public void setEncounterTypeName(String encounterTypeName) {
		this.encounterTypeName = encounterTypeName;
	}

	@Override
	public Integer getDeliveryId(ServiceDelivery service, Integer patientId) {
		return registrarBean.getEncounterId(patientId, encounterTypeName,
				getEarliestDate(service, patientId), getLatestDate(service,
						patientId));
	}

	@Override
	public Date getEarliestDate(ServiceDelivery service, Integer patientId) {
		return timeBean.determineTime(getEarliestTimePeriod(service),
				getEarliestTimeReference(service),
				getEarliestTimeValue(service), patientId, null, null, null,
				null, encounterTypeName);
	}

	@Override
	public Date getLatestDate(ServiceDelivery service, Integer patientId) {
		return timeBean.determineTime(getLatestTimePeriod(service),
				getLatestTimeReference(service), getLatestTimeValue(service),
				patientId, null, null, null, null, encounterTypeName);
	}

	@Override
	public Date getPreferredStartDate(ServiceDelivery service, Integer patientId) {
		return timeBean.determineTime(service.getStartTimePeriod(), service
				.getStartTimeReference(), service.getStartTimeValue(),
				patientId, null, null, null, null, encounterTypeName);
	}

	@Override
	public Date getPreferredEndDate(ServiceDelivery service, Integer patientId) {
		return timeBean.determineTime(service.getEndTimePeriod(), service
				.getEndTimeReference(), service.getEndTimeValue(), patientId,
				null, null, null, null, encounterTypeName);
	}

}
