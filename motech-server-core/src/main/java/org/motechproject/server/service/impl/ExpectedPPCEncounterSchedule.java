package org.motechproject.server.service.impl;

import java.util.Date;

import org.motechproject.server.time.TimePeriod;
import org.openmrs.Patient;

public class ExpectedPPCEncounterSchedule extends ExpectedEncounterSchedule {

	protected Integer maxDeliveryValue;
	protected TimePeriod maxDeliveryPeriod;

	@Override
	protected Date getReferenceDate(Patient patient) {
		return registrarBean.getCurrentDeliveryDate(patient);
	}

	@Override
	protected boolean validReferenceDate(Date referenceDate, Date currentDate) {
		Date maxDeliveryDate = calculateDate(referenceDate, maxDeliveryValue,
				maxDeliveryPeriod);
		return super.validReferenceDate(referenceDate, currentDate)
				&& !currentDate.after(maxDeliveryDate);
	}

	public Integer getMaxDeliveryValue() {
		return maxDeliveryValue;
	}

	public void setMaxDeliveryValue(Integer maxDeliveryValue) {
		this.maxDeliveryValue = maxDeliveryValue;
	}

	public TimePeriod getMaxDeliveryPeriod() {
		return maxDeliveryPeriod;
	}

	public void setMaxDeliveryPeriod(TimePeriod maxDeliveryPeriod) {
		this.maxDeliveryPeriod = maxDeliveryPeriod;
	}

}
