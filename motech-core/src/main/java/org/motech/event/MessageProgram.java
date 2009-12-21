package org.motech.event;

public interface MessageProgram extends BaseInterface {

	MessageProgramState determineState(Integer personId);

	MessageProgramState updateState(Integer personId);

	MessageProgramState getStartState();

	MessageProgramState getEndState();

	String getConceptName();

	String getConceptValue();

	MessageProgramType getProgramType();
}
