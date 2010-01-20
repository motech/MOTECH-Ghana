package org.motech.event.impl;

import org.motech.event.MessagesCommand;
import org.motech.svc.RegistrarBean;

public class RemoveEnrollmentCommand extends MessagesCommand {

	RegistrarBean registrarBean;

	public RegistrarBean getRegistrarBean() {
		return registrarBean;
	}

	public void setRegistrarBean(RegistrarBean registrarBean) {
		this.registrarBean = registrarBean;
	}

	@Override
	public void execute() {
		registrarBean.removeMessageProgramEnrollment(enrollment);
	}

}
