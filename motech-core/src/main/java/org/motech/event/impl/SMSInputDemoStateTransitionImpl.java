package org.motech.event.impl;

import java.util.Date;

import org.motech.model.MessageProgramEnrollment;
import org.motech.svc.RegistrarBean;

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

			Date terminatingObsDate = registrarBean.getLastObsValue(enrollment
					.getPersonId(), prevState.getProgram().getConceptName());

			boolean terminatingObservationExists = terminatingObsDate != null
					&& enrollment.getStartDate().before(terminatingObsDate);

			return trueBasedOnDate && !terminatingObservationExists;
		} else {
			Date terminatingObsDate = registrarBean.getLastObsValue(enrollment
					.getPersonId(), prevState.getProgram().getConceptName());
			return terminatingObsDate != null
					&& enrollment.getStartDate().before(terminatingObsDate);
		}
	}
}
