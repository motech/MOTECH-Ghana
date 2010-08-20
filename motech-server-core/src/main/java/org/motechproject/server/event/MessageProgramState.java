package org.motechproject.server.event;

import java.util.Date;

import org.motechproject.server.model.MessageProgramEnrollment;
import org.motechproject.server.time.TimePeriod;
import org.motechproject.server.time.TimeReference;

public interface MessageProgramState extends BaseInterface {

	MessagesCommand getCommand();

	MessageProgramStateTransition getTransition(
			MessageProgramEnrollment enrollment, Date currentDate);

	MessageProgram getProgram();

	Integer getTimeValue();

	TimePeriod getTimePeriod();

	TimeReference getTimeReference();

	Date getDateOfAction(MessageProgramEnrollment enrollment, Date currentDate);
}
