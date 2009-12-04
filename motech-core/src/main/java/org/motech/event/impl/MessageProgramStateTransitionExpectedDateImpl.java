package org.motech.event.impl;

import java.util.Date;

public class MessageProgramStateTransitionExpectedDateImpl extends
		MessageProgramStateTransitionImpl {

	@Override
	public boolean evaluate(Integer personId) {
		Date currentDate = new Date();
		Date actionDate;
		if (nextState.equals(prevState)) {
			actionDate = nextState.getDateOfAction(personId);
			if (actionDate == null) {
				return false;
			}
			return currentDate.before(actionDate)
					|| currentDate.equals(actionDate);
		} else {
			actionDate = prevState.getDateOfAction(personId);
			if (actionDate == null) {
				return false;
			}
			return currentDate.after(actionDate);
		}
	}

}