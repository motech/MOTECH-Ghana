package org.motech.event.impl;

import org.motech.svc.RegistrarBean;
import org.openmrs.Patient;

public class MessageProgramStateTransitionExpectedNumImpl extends
		MessageProgramStateTransitionImpl {

	private RegistrarBean registrarBean;
	private int expectedNumber;

	@Override
	public boolean evaluate(Patient patient) {
		String conceptName = prevState.getProgram().getConceptName();
		String conceptValue = prevState.getProgram().getConceptValue();
		int obsNum = registrarBean.getNumberOfObs(patient, conceptName,
				conceptValue);

		if (prevState.equals(nextState)) {
			return obsNum == expectedNumber;
		} else {
			return obsNum >= expectedNumber;
		}
	}

	public RegistrarBean getRegistrarBean() {
		return registrarBean;
	}

	public void setRegistrarBean(RegistrarBean registrarBean) {
		this.registrarBean = registrarBean;
	}

	public int getExpectedNumber() {
		return expectedNumber;
	}

	public void setExpectedNumber(int expectedNumber) {
		this.expectedNumber = expectedNumber;
	}

}