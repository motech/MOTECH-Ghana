package org.motechproject.ws;

import java.util.Calendar;

public enum DayOfWeek {
	SUNDAY(Calendar.SUNDAY), MONDAY(Calendar.MONDAY), TUESDAY(Calendar.TUESDAY), WEDNESDAY(
			Calendar.WEDNESDAY), THURSDAY(Calendar.THURSDAY), FRIDAY(
			Calendar.FRIDAY), SATURDAY(Calendar.SATURDAY);

	private final int calendarValue;

	DayOfWeek(int calendarValue) {
		this.calendarValue = calendarValue;
	}

	public int getCalendarValue() {
		return this.calendarValue;
	}
}
