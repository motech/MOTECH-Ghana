package org.motech.event;

import java.util.Date;

public interface MessageProgramState extends BaseInterface {

	Command getCommand();

	MessageProgramStateTransition getTransition(Integer personId);

	MessageProgram getProgram();

	int getTimeValue();

	TimePeriod getTimePeriod();

	TimeReference getTimeReference();

	Date getDateOfAction(Integer personId);
}
