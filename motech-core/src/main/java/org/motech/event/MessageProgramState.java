package org.motech.event;

import java.util.Date;

import org.motech.model.MessageProgramEnrollment;
import org.motech.time.TimePeriod;
import org.motech.time.TimeReference;

public interface MessageProgramState extends BaseInterface {

	MessagesCommand getCommand();

	MessageProgramStateTransition getTransition(
			MessageProgramEnrollment enrollment);

	MessageProgram getProgram();

	Integer getTimeValue();

	TimePeriod getTimePeriod();

	TimeReference getTimeReference();

	Date getDateOfAction(MessageProgramEnrollment enrollment);
}
