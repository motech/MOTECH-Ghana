package org.motech.event;

import org.motech.model.MessageProgramEnrollment;

public interface MessageProgramStateTransition extends BaseInterface {

	MessageProgramState getPrevState();

	MessageProgramState getNextState();

	MessagesCommand getCommand();

	boolean evaluate(MessageProgramEnrollment enrollment);
}
