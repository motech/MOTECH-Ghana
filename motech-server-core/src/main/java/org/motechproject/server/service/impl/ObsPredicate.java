package org.motechproject.server.service.impl;

import java.util.Date;

import org.apache.commons.collections.Predicate;
import org.openmrs.Obs;

public class ObsPredicate implements Predicate {

	private Date minDate;
	private Date maxDate;
	private Integer value;

	public boolean evaluate(Object input) {
		if (input instanceof Obs) {
			Obs obs = (Obs) input;
			Date obsDate = obs.getObsDatetime();

			boolean afterMinDate = true;
			if (minDate != null) {
				afterMinDate = obsDate.after(minDate);
			}

			boolean beforeMaxDate = true;
			if (maxDate != null) {
				beforeMaxDate = obsDate.before(maxDate);
			}

			Double obsValue = obs.getValueNumeric();
			boolean matchingValue = true;
			if (value != null && obsValue != null) {
				matchingValue = value.intValue() == obsValue.intValue();
			}

			return afterMinDate && beforeMaxDate && matchingValue;
		}
		return false;
	}

	public Date getMinDate() {
		return minDate;
	}

	public void setMinDate(Date minDate) {
		this.minDate = minDate;
	}

	public Date getMaxDate() {
		return maxDate;
	}

	public void setMaxDate(Date maxDate) {
		this.maxDate = maxDate;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

}
