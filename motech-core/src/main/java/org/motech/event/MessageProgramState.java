package org.motech.event;

import java.util.Date;

import org.openmrs.Patient;

public interface MessageProgramState extends BaseInterface {

	Command getCommand();

	MessageProgramStateTransition getTransition(Patient patient);

	MessageProgram getProgram();

	int getTimeValue();

	TimePeriod getTimePeriod();

	TimeReference getTimeReference();

	Date getDateOfAction(Patient patient);
}
