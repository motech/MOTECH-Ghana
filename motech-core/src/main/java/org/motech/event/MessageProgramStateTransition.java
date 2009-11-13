package org.motech.event;

import org.openmrs.Patient;

public interface MessageProgramStateTransition extends BaseInterface {

	MessageProgramState getPrevState();

	MessageProgramState getNextState();

	Command getCommand();

	boolean evaluate(Patient patient);
}
