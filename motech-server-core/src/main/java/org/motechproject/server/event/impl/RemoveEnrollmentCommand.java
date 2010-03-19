package org.motechproject.server.event.impl;

import org.motechproject.server.event.MessagesCommand;
import org.motechproject.server.svc.RegistrarBean;

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
