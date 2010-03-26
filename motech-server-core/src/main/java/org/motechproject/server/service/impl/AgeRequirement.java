package org.motechproject.server.service.impl;

import java.util.Calendar;
import java.util.Date;

import org.motechproject.server.service.Requirement;
import org.motechproject.server.time.TimePeriod;
import org.openmrs.Patient;

public class AgeRequirement implements Requirement {

	private Integer minValue;
	private TimePeriod minPeriod;

	private Integer maxValue;
	private TimePeriod maxPeriod;

	public boolean meetsRequirement(Patient patient, Date date) {

		if (minValue != null && minPeriod != null) {
			Date minAgeDate = calculateDate(patient.getBirthdate(), minValue,
					minPeriod);
			if (!date.after(minAgeDate)) {
				return false;
			}
		}
		if (maxValue != null && maxPeriod != null) {
			Date maxAgeDate = calculateDate(patient.getBirthdate(), maxValue,
					maxPeriod);
			if (!date.before(maxAgeDate)) {
				return false;
			}
		}
		return true;
	}

	private Date calculateDate(Date birthDate, Integer value, TimePeriod period) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(birthDate);

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
