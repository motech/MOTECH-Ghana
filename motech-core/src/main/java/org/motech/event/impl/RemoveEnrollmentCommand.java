package org.motech.event.impl;

import org.motech.event.Command;
import org.motech.svc.RegistrarBean;

public class RemoveEnrollmentCommand implements Command {

	Integer personId;
	String programName;
	RegistrarBean registrarBean;

	public String getProgramNamee() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
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
		registrarBean.removeMessageProgramEnrollment(personId, programName);
	}

}
