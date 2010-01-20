package org.motech.event;

import java.util.Date;

import org.motech.model.MessageProgramEnrollment;

public interface MessageProgramState extends BaseInterface {

	MessagesCommand getCommand();

	MessageProgramStateTransition getTransition(
			MessageProgramEnrollment enrollment);

	MessageProgram getProgram();

	int getTimeValue();

	TimePeriod getTimePeriod();

	TimeReference getTimeReference();

	Date getDateOfAction(MessageProgramEnrollment enrollment);
}
