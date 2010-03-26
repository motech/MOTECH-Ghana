package org.motechproject.server.service.impl;

import java.util.Date;

import org.apache.commons.collections.Predicate;
import org.openmrs.Encounter;

public class EncounterPredicate implements Predicate {

	private Date minDate;
	private Date maxDate;

	public boolean evaluate(Object input) {
		if (input instanceof Encounter) {
			Encounter obs = (Encounter) input;
			Date encounterDate = obs.getEncounterDatetime();

			boolean afterMinDate = true;
			if (minDate != null) {
				afterMinDate = encounterDate.after(minDate);
			}

			boolean beforeMaxDate = true;
			if (maxDate != null) {
				beforeMaxDate = encounterDate.before(maxDate);
			}

			return afterMinDate && beforeMaxDate;
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

}
