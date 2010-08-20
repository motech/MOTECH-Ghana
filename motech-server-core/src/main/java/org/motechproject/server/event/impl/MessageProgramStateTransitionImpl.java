package org.motechproject.server.event.impl;

import java.util.Date;

import org.motechproject.server.event.MessageProgramState;
import org.motechproject.server.event.MessageProgramStateTransition;
import org.motechproject.server.event.MessagesCommand;
import org.motechproject.server.model.MessageProgramEnrollment;

public class MessageProgramStateTransitionImpl extends BaseInterfaceImpl
		implements MessageProgramStateTransition {

	protected MessageProgramState prevState;
	protected MessageProgramState nextState;
	protected MessagesCommand command;

	public boolean evaluate(MessageProgramEnrollment enrollment,
			Date currentDate) {
		// Default Transition is always taken
		return true;
	}

	public MessageProgramState getPrevState() {
		return prevState;
	}

	public void setPrevState(MessageProgramState prevState) {
		this.prevState = prevState;
	}

	public MessageProgramState getNextState() {
		return nextState;
	}

	public void setNextState(MessageProgramState nextState) {
		this.nextState = nextState;
	}

	public MessagesCommand getCommand() {
		return command;
	}

	public void setCommand(MessagesCommand command) {
		this.command = command;
	}

}