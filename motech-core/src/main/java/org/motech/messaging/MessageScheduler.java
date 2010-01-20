package org.motech.messaging;

import java.util.Date;

import org.motech.model.MessageProgramEnrollment;

public interface MessageScheduler {

	void scheduleMessage(String messageKey,
			MessageProgramEnrollment enrollment, Date messageDate);

}
