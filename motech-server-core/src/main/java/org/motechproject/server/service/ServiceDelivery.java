package org.motechproject.server.service;

import org.motechproject.server.time.TimePeriod;
import org.motechproject.server.time.TimeReference;

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
