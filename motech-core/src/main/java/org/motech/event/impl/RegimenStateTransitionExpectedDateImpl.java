package org.motech.event.impl;

import java.util.Date;

import org.openmrs.Patient;

public class RegimenStateTransitionExpectedDateImpl extends
		RegimenStateTransitionImpl {

	@Override
	public boolean evaluate(Patient patient) {
		Date currentDate = new Date();
		Date actionDate;
		if (nextState.equals(prevState)) {
			actionDate = nextState.getDateOfAction(patient);
			if (actionDate == null) {
				return false;
			}
			return currentDate.before(actionDate)
					|| currentDate.equals(actionDate);
		} else {
			actionDate = prevState.getDateOfAction(patient);
			if (actionDate == null) {
				return false;
			}
			return currentDate.after(actionDate);
		}
	}

}