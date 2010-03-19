package org.motechproject.server.event.impl;

import java.util.Date;

import org.motechproject.server.model.MessageProgramEnrollment;
import org.motechproject.server.svc.RegistrarBean;

public class SMSInputDemoStateTransitionImpl extends
		MessageProgramStateTransitionExpectedDateImpl {

	RegistrarBean registrarBean;

	boolean terminatingTransition = false;

	public void setRegistrarBean(RegistrarBean registrarBean) {
		this.registrarBean = registrarBean;
	}

	public void setTerminating(boolean terminating) {
		this.terminatingTransition = terminating;
	}

	@Override
	public boolean evaluate(MessageProgramEnrollment enrollment) {

		if (!terminatingTransition) {
			boolean trueBasedOnDate = super.evaluate(enrollment);

			Date terminatingObsDate = registrarBean.getLastObsCreationDate(
					enrollment.getPersonId(), prevState.getProgram()
							.getConceptName(), null);

			boolean terminatingObservationExists = terminatingObsDate != null
					&& enrollment.getStartDate().before(terminatingObsDate);

			return trueBasedOnDate && !terminatingObservationExists;
		} else {
			Date terminatingObsDate = registrarBean.getLastObsCreationDate(
					enrollment.getPersonId(), prevState.getProgram()
							.getConceptName(), null);
			return terminatingObsDate != null
					&& enrollment.getStartDate().before(terminatingObsDate);
		}
	}
}
