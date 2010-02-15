package org.motech.service;

import org.motech.time.TimePeriod;
import org.motech.time.TimeReference;

public interface ServiceDelivery {

	String getName();

	Integer getEarliestTimeValue();

	TimePeriod getEarliestTimePeriod();

	TimeReference getEarliestTimeReference();

	Integer getStartTimeValue();

	TimePeriod getStartTimePeriod();

	TimeReference getStartTimeReference();

	Integer getEndTimeValue();

	TimePeriod getEndTimePeriod();

	TimeReference getEndTimeReference();

	Integer getLatestTimeValue();

	TimePeriod getLatestTimePeriod();

	TimeReference getLatestTimeReference();

}
