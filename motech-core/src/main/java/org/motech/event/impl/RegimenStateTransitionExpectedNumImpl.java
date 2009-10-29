package org.motech.event.impl;

import org.motech.svc.RegistrarBean;
import org.openmrs.Patient;

public class RegimenStateTransitionExpectedNumImpl extends
		RegimenStateTransitionImpl {

	private RegistrarBean registrarBean;
	private int expectedNumber;

	@Override
	public boolean evaluate(Patient patient) {
		String conceptName = prevState.getRegimen().getConceptName();
		String conceptValue = prevState.getRegimen().getConceptValue();
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