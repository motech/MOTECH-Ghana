package org.motech.event.impl;

import org.motech.event.Command;
import org.motech.svc.RegistrarBean;

public class RemoveRegimenEnrollmentCommand implements Command {

	Integer personId;
	String regimenName;
	RegistrarBean registrarBean;

	public String getRegimenName() {
		return regimenName;
	}

	public void setRegimenName(String regimenName) {
		this.regimenName = regimenName;
	}

	public Integer getPersonId() {
		return personId;
	}

	public void setPersonId(Integer personId) {
		this.personId = personId;
	}

	public RegistrarBean getRegistrarBean() {
		return registrarBean;
	}

	public void setRegistrarBean(RegistrarBean registrarBean) {
		this.registrarBean = registrarBean;
	}

	public void execute() {
		registrarBean.removeRegimenEnrollment(personId, regimenName);
	}

}
