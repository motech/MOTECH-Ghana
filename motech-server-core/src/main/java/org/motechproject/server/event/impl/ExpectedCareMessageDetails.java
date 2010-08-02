package org.motechproject.server.event.impl;

import java.util.Map;

import org.motechproject.server.time.TimePeriod;

public class ExpectedCareMessageDetails {

	private String name;
	private String upcomingMessageKey;
	private String overdueMessageKey;
	private Integer timeValue;
	private TimePeriod timePeriod;
	private Boolean userPreferenceBased = false;
	private Map<String, Integer> careTimeMap;

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

	public Boolean getUserPreferenceBased() {
		return userPreferenceBased;
	}

	public void setUserPreferenceBased(Boolean userPreferenceBased) {
		this.userPreferenceBased = userPreferenceBased;
	}

	public Map<String, Integer> getCareTimeMap() {
		return careTimeMap;
	}

	public void setCareTimeMap(Map<String, Integer> careTimeMap) {
		this.careTimeMap = careTimeMap;
	}

	public Integer getTimeValue(String care) {
		if (careTimeMap != null && careTimeMap.containsKey(care)) {
			return careTimeMap.get(care);
		}
		return timeValue;
	}
}
