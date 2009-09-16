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
			return currentDate.before(actionDate)
					|| currentDate.equals(actionDate);
		} else {
			actionDate = prevState.getDateOfAction(patient);
			return currentDate.after(actionDate);
		}
	}

}