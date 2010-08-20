package org.motechproject.server.messaging;

import java.util.Date;

import org.motechproject.server.model.MessageProgramEnrollment;

public interface MessageScheduler {

	void scheduleMessages(String messageKey, String messageKeyA,
			String messageKeyB, String messageKeyC,
			MessageProgramEnrollment enrollment, Date messageDate);

	Date adjustMessageDate(MessageProgramEnrollment enrollment,
			Date messageDate, Date currentDate);

}
