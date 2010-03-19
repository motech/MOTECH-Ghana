package org.motechproject.server.event;

import java.util.Date;

import org.motechproject.server.model.MessageProgramEnrollment;

public class MessagesCommand {

	protected MessageProgramEnrollment enrollment;
	protected Date actionDate;

	public void execute() {
		// No operation
	}

	public MessageProgramEnrollment getEnrollment() {
		return enrollment;
	}

	public void setEnrollment(MessageProgramEnrollment enrollment) {
		this.enrollment = enrollment;
	}

	public Date getActionDate() {
		return actionDate;
	}

	public void setActionDate(Date actionDate) {
		this.actionDate = actionDate;
	}

}
