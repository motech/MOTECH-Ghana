package org.motechproject.server.service.impl;

import java.util.Date;

import org.motechproject.server.model.ExpectedEncounter;

public class ExpectedEncounterDatePredicate extends ExpectedEncounterPredicate {

	private Date minDate;
	private Date dueDate;
	private Date lateDate;
	private Date maxDate;

	@Override
	public boolean evaluate(Object input) {
		if (input instanceof ExpectedEncounter) {
			ExpectedEncounter expectedEncounter = (ExpectedEncounter) input;

			boolean minDateMatch = true;
			if (minDate != null) {
				minDateMatch = minDate.equals(expectedEncounter
						.getMinEncounterDatetime());
			}
			boolean dueDateMatch = true;
			if (dueDate != null) {
				dueDateMatch = dueDate.equals(expectedEncounter
						.getDueEncounterDatetime());
			}
			boolean lateDateMatch = true;
			if (lateDate != null) {
				lateDateMatch = lateDate.equals(expectedEncounter
						.getLateEncounterDatetime());
			}
			boolean maxDateMatch = true;
			if (maxDate != null) {
				maxDateMatch = maxDate.equals(expectedEncounter
						.getMaxEncounterDatetime());
			}

			return minDateMatch && dueDateMatch && lateDateMatch
					&& maxDateMatch;
		}
		return false;
	}

	public Date getMinDate() {
		return minDate;
	}

	public void setMinDate(Date minDate) {
		this.minDate = minDate;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Date getLateDate() {
		return lateDate;
	}

	public void setLateDate(Date lateDate) {
		this.lateDate = lateDate;
	}

	public Date getMaxDate() {
		return maxDate;
	}

	public void setMaxDate(Date maxDate) {
		this.maxDate = maxDate;
	}

}
