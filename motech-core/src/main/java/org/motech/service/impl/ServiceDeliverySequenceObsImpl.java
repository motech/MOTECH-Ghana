package org.motech.service.impl;

import java.util.Date;

import org.motech.service.ServiceDelivery;

public class ServiceDeliverySequenceObsImpl extends ServiceDeliverySequenceImpl {

	private String serviceConceptName;
	private String serviceValueConceptName;

	public String getServiceConceptName() {
		return serviceConceptName;
	}

	public void setServiceConceptName(String serviceConceptName) {
		this.serviceConceptName = serviceConceptName;
	}

	public String getServiceValueConceptName() {
		return serviceValueConceptName;
	}

	public void setServiceValueConceptName(String serviceValueConceptName) {
		this.serviceValueConceptName = serviceValueConceptName;
	}

	@Override
	public Integer getDeliveryId(ServiceDelivery service, Integer patientId) {
		Integer obsId = null;
		Integer doseNumber = getDoseNumber(service);
		if (doseNumber != null) {
			obsId = registrarBean.getObsId(patientId, serviceConceptName,
					doseNumber, getEarliestDate(service, patientId),
					getLatestDate(service, patientId));
		} else {
			obsId = registrarBean.getObsId(patientId, serviceConceptName,
					serviceValueConceptName,
					getEarliestDate(service, patientId), getLatestDate(service,
							patientId));
		}
		return obsId;
	}

	@Override
	public Date getEarliestDate(ServiceDelivery service, Integer patientId) {
		return timeBean.determineTime(getEarliestTimePeriod(service),
				getEarliestTimeReference(service),
				getEarliestTimeValue(service), patientId, null,
				serviceConceptName, serviceValueConceptName,
				getDoseNumber(service), null);
	}

	@Override
	public Date getLatestDate(ServiceDelivery service, Integer patientId) {
		return timeBean.determineTime(getLatestTimePeriod(service),
				getLatestTimeReference(service), getLatestTimeValue(service),
				patientId, null, serviceConceptName, serviceValueConceptName,
				getDoseNumber(service), null);
	}

	@Override
	public Date getPreferredStartDate(ServiceDelivery service, Integer patientId) {
		return timeBean.determineTime(service.getStartTimePeriod(), service
				.getStartTimeReference(), service.getStartTimeValue(),
				patientId, null, serviceConceptName, serviceValueConceptName,
				getDoseNumber(service), null);
	}

	@Override
	public Date getPreferredEndDate(ServiceDelivery service, Integer patientId) {
		return timeBean.determineTime(service.getEndTimePeriod(), service
				.getEndTimeReference(), service.getEndTimeValue(), patientId,
				null, serviceConceptName, serviceValueConceptName,
				getDoseNumber(service), null);
	}

	private Integer getDoseNumber(ServiceDelivery service) {
		Integer doseNumber = null;
		if (service instanceof ServiceDeliveryDoseImpl) {
			doseNumber = ((ServiceDeliveryDoseImpl) service).getDoseNumber();
		}
		return doseNumber;
	}

}
