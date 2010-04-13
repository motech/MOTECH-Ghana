package org.motechproject.server.event.impl;

import org.motechproject.server.time.TimePeriod;

public class ExpectedCareMessageDetails {

	private String name;
	private String upcomingMessageKey;
	private String overdueMessageKey;
	private Integer timeValue;
	private TimePeriod timePeriod;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUpcomingMessageKey() {
		return upcomingMessageKey;
	}

	public void setUpcomingMessageKey(String upcomingMessageKey) {
		this.upcomingMessageKey = upcomingMessageKey;
	}

	public String getOverdueMessageKey() {
		return overdueMessageKey;
	}

	public void setOverdueMessageKey(String overdueMessageKey) {
		this.overdueMessageKey = overdueMessageKey;
	}

	public Integer getTimeValue() {
		return timeValue;
	}

	public void setTimeValue(Integer timeValue) {
		this.timeValue = timeValue;
	}

	public TimePeriod getTimePeriod() {
		return timePeriod;
	}

	public void setTimePeriod(TimePeriod timePeriod) {
		this.timePeriod = timePeriod;
	}

}
