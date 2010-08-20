package org.motechproject.server.event.impl;

import java.util.Calendar;
import java.util.Date;

import org.motechproject.server.model.MessageProgramEnrollment;
import org.motechproject.server.time.TimePeriod;

public class MessageProgramStateTransitionExpectedDateImpl extends
		MessageProgramStateTransitionImpl {

	private Integer timeValue;
	private TimePeriod timePeriod;

	@Override
	public boolean evaluate(MessageProgramEnrollment enrollment,
			Date currentDate) {
		Date actionDate;
		if (nextState.equals(prevState)) {
			actionDate = nextState.getDateOfAction(enrollment, currentDate);
			if (actionDate == null) {
				return false;
			}
			return currentDate.before(actionDate)
					|| currentDate.equals(actionDate);
		} else if (timeValue != null && timePeriod != null) {
			actionDate = nextState.getDateOfAction(enrollment, currentDate);
			if (timeValue != null && timePeriod != null) {
				actionDate = calculateDate(actionDate, timeValue, timePeriod);
			}
			if (actionDate == null) {
				return false;
			}
			return currentDate.after(actionDate);
		} else {
			actionDate = prevState.getDateOfAction(enrollment, currentDate);
			if (actionDate == null) {
				return false;
			}
			return currentDate.after(actionDate);
		}
	}

	protected Date calculateDate(Date date, Integer value, TimePeriod period) {
		if (date == null || value == null || period == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		switch (period) {
		case minute:
			calendar.add(Calendar.MINUTE, value);
			break;
		case hour:
			calendar.add(Calendar.HOUR, value);
			break;
		case day:
			calendar.add(Calendar.DATE, value);
			break;
		case week:
			// Add weeks as days
			calendar.add(Calendar.DATE, value * 7);
			break;
		case month:
			calendar.add(Calendar.MONTH, value);
			break;
		case year:
			calendar.add(Calendar.YEAR, value);
			break;
		}
		return calendar.getTime();
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