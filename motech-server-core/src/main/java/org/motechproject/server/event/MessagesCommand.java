package org.motechproject.server.event;

import java.util.Date;

import org.motechproject.server.model.MessageProgramEnrollment;

public class MessagesCommand {

	public void execute(MessageProgramEnrollment enrollment, Date actionDate,
			Date currentDate) {
		// No operation
	}

	public Date adjustActionDate(MessageProgramEnrollment enrollment,
			Date actionDate, Date currentDate) {
		return actionDate;
	}
}
