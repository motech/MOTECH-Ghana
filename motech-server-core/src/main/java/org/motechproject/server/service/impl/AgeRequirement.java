package org.motechproject.server.service.impl;

import java.util.Date;

import org.motechproject.server.service.Requirement;
import org.motechproject.server.time.TimeBean;
import org.motechproject.server.time.TimePeriod;
import org.motechproject.server.time.TimeReference;

public class AgeRequirement implements Requirement {

	private Integer minTimeValue;
	private TimePeriod minTimePeriod;

	private Integer maxTimeValue;
	private TimePeriod maxTimePeriod;

	private TimeBean timeBean;

	public boolean meetsRequirement(Integer patientId, Date date) {

		if (minTimeValue != null && minTimePeriod != null) {
			Date minDate = timeBean.determineTime(minTimePeriod,
					TimeReference.patient_birthdate, minTimeValue, patientId,
					null, null, null, null, null);
			if (!date.after(minDate)) {
				return false;
			}
		}
		if (maxTimeValue != null && maxTimePeriod != null) {
			Date maxDate = timeBean.determineTime(maxTimePeriod,
					TimeReference.patient_birthdate, maxTimeValue, patientId,
					null, null, null, null, null);
			if (!date.before(maxDate)) {
				return false;
			}
		}
		return true;
	}

	public Integer getMinTimeValue() {
		return minTimeValue;
	}

	public void setMinTimeValue(Integer minTimeValue) {
		this.minTimeValue = minTimeValue;
	}

	public TimePeriod getMinTimePeriod() {
		return minTimePeriod;
	}

	public void setMinTimePeriod(TimePeriod minTimePeriod) {
		this.minTimePeriod = minTimePeriod;
	}

	public Integer getMaxTimeValue() {
		return maxTimeValue;
	}

	public void setMaxTimeValue(Integer maxTimeValue) {
		this.maxTimeValue = maxTimeValue;
	}

	public TimePeriod getMaxTimePeriod() {
		return maxTimePeriod;
	}

	public void setMaxTimePeriod(TimePeriod maxTimePeriod) {
		this.maxTimePeriod = maxTimePeriod;
	}

	public TimeBean getTimeBean() {
		return timeBean;
	}

	public void setTimeBean(TimeBean timeBean) {
		this.timeBean = timeBean;
	}

}
