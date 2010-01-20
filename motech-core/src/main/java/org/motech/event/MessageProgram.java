package org.motech.event;

import org.motech.model.MessageProgramEnrollment;

public interface MessageProgram extends BaseInterface {

	MessageProgramState determineState(MessageProgramEnrollment enrollment);

	MessageProgramState updateState(MessageProgramEnrollment enrollment);

	MessageProgramState getStartState();

	MessageProgramState getEndState();

	String getConceptName();

	String getConceptValue();
}
