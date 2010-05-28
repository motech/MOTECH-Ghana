package org.motechproject.server.messaging;

import java.util.Date;

import org.motechproject.server.model.MessageProgramEnrollment;

public interface MessageScheduler {

	void scheduleMessage(String messageKey,
			MessageProgramEnrollment enrollment, Date messageDate);

	Date adjustMessageDate(MessageProgramEnrollment enrollment, Date messageDate);

}
