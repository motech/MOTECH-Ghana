package org.motechproject.server.service;

import org.motechproject.server.time.TimePeriod;

public class ExpectedCareEvent {

	protected String name;
	protected Integer number;

	protected Integer minValue;
	protected TimePeriod minPeriod;

	protected Integer dueValue;
	protected TimePeriod duePeriod;
	protected Boolean dueReferencePrevious;

	protected Integer lateValue;
	protected TimePeriod latePeriod;

	protected Integer maxValue;
	protected TimePeriod maxPeriod;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Integer getMinValue() {
		return minValue;
	}

	public void setMinValue(Integer minValue) {
		this.minValue = minValue;
	}

	public TimePeriod getMinPeriod() {
		return minPeriod;
	}

	public void setMinPeriod(TimePeriod minPeriod) {
		this.minPeriod = minPeriod;
	}

	public Integer getDueValue() {
		return dueValue;
	}

	public void setDueValue(Integer dueValue) {
		this.dueValue = dueValue;
	}

	public TimePeriod getDuePeriod() {
		return duePeriod;
	}

	public void setDuePeriod(TimePeriod duePeriod) {
		this.duePeriod = duePeriod;
	}

	public Boolean getDueReferencePrevious() {
		return dueReferencePrevious;
	}

	public void setDueReferencePrevious(Boolean dueReferencePrevious) {
		this.dueReferencePrevious = dueReferencePrevious;
	}

	public Integer getLateValue() {
		return lateValue;
	}

	public void setLateValue(Integer lateValue) {
		this.lateValue = lateValue;
	}

	public TimePeriod getLatePeriod() {
		return latePeriod;
	}

	public void setLatePeriod(TimePeriod latePeriod) {
		this.latePeriod = latePeriod;
	}

	public Integer getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Integer maxValue) {
		this.maxValue = maxValue;
	}

	public TimePeriod getMaxPeriod() {
		return maxPeriod;
	}

	public void setMaxPeriod(TimePeriod maxPeriod) {
		this.maxPeriod = maxPeriod;
	}

}
