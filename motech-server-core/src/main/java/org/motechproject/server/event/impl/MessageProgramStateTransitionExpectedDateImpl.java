package org.motechproject.server.event.impl;

import java.util.Date;

import org.motechproject.server.model.MessageProgramEnrollment;

public class MessageProgramStateTransitionExpectedDateImpl extends
		MessageProgramStateTransitionImpl {

	@Override
	public boolean evaluate(MessageProgramEnrollment enrollment) {
		Date currentDate = new Date();
		Date actionDate;
		if (nextState.equals(prevState)) {
			actionDate = nextState.getDateOfAction(enrollment);
			if (actionDate == null) {
				return false;
			}
			return currentDate.before(actionDate)
					|| currentDate.equals(actionDate);
		} else {
			actionDate = prevState.getDateOfAction(enrollment);
			if (actionDate == null) {
				return false;
			}
			return currentDate.after(actionDate);
		}
	}

}