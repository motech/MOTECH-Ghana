package org.motechproject.server.service.impl;

import org.motechproject.server.service.ServiceDelivery;
import org.motechproject.server.time.TimePeriod;
import org.motechproject.server.time.TimeReference;

public class ServiceDeliveryImpl implements ServiceDelivery {

	protected String name;

	protected Integer earliestTimeValue;
	protected TimePeriod earliestTimePeriod;
	protected TimeReference earliestTimeReference;

	protected Integer startTimeValue;
	protected TimePeriod startTimePeriod;
	protected TimeReference startTimeReference;

	protected Integer endTimeValue;
	protected TimePeriod endTimePeriod;
	protected TimeReference endTimeReference;

	protected Integer latestTimeValue;
	protected TimePeriod latestTimePeriod;
	protected TimeReference latestTimeReference;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getEarliestTimeValue() {
		return earliestTimeValue;
	}

	public void setEarliestTimeValue(Integer earliestTimeValue) {
		this.earliestTimeValue = earliestTimeValue;
	}

	public TimePeriod getEarliestTimePeriod() {
		return earliestTimePeriod;
	}

	public void setEarliestTimePeriod(TimePeriod earliestTimePeriod) {
		this.earliestTimePeriod = earliestTimePeriod;
	}

	public TimeReference getEarliestTimeReference() {
		return earliestTimeReference;
	}

	public void setEarliestTimeReference(TimeReference earliestTimeReference) {
		this.earliestTimeReference = earliestTimeReference;
	}

	public Integer getStartTimeValue() {
		return startTimeValue;
	}

	public void setStartTimeValue(Integer startTimeValue) {
		this.startTimeValue = startTimeValue;
	}

	public TimePeriod getStartTimePeriod() {
		return startTimePeriod;
	}

	public void setStartTimePeriod(TimePeriod startTimePeriod) {
		this.startTimePeriod = startTimePeriod;
	}

	public TimeReference getStartTimeReference() {
		return startTimeReference;
	}

	public void setStartTimeReference(TimeReference startTimeReference) {
		this.startTimeReference = startTimeReference;
	}

	public Integer getEndTimeValue() {
		return endTimeValue;
	}

	public void setEndTimeValue(Integer endTimeValue) {
		this.endTimeValue = endTimeValue;
	}

	public TimePeriod getEndTimePeriod() {
		return endTimePeriod;
	}

	public void setEndTimePeriod(TimePeriod endTimePeriod) {
		this.endTimePeriod = endTimePeriod;
	}

	public TimeReference getEndTimeReference() {
		return endTimeReference;
	}

	public void setEndTimeReference(TimeReference endTimeReference) {
		this.endTimeReference = endTimeReference;
	}

	public Integer getLatestTimeValue() {
		return latestTimeValue;
	}

	public void setLatestTimeValue(Integer latestTimeValue) {
		this.latestTimeValue = latestTimeValue;
	}

	public TimePeriod getLatestTimePeriod() {
		return latestTimePeriod;
	}

	public void setLatestTimePeriod(TimePeriod latestTimePeriod) {
		this.latestTimePeriod = latestTimePeriod;
	}

	public TimeReference getLatestTimeReference() {
		return latestTimeReference;
	}

	public void setLatestTimeReference(TimeReference latestTimeReference) {
		this.latestTimeReference = latestTimeReference;
	}

}
