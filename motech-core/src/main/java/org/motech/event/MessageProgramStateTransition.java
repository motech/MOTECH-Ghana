package org.motech.event;

public interface MessageProgramStateTransition extends BaseInterface {

	MessageProgramState getPrevState();

	MessageProgramState getNextState();

	Command getCommand();

	boolean evaluate(Integer personId);
}
