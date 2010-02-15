package org.motech.service.impl;

import java.util.Date;

import org.motech.service.Requirement;
import org.motech.svc.RegistrarBean;

public class ActivePregnancyRequirement implements Requirement {

	private RegistrarBean registrarBean;

	public boolean meetsRequirement(Integer patientId, Date date) {
		return registrarBean.getActivePregnancy(patientId) != null;
	}

	public RegistrarBean getRegistrarBean() {
		return registrarBean;
	}

	public void setRegistrarBean(RegistrarBean registrarBean) {
		this.registrarBean = registrarBean;
	}

}
