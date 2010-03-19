package org.motech.event.impl;

import org.motech.event.MessageProgramState;
import org.motech.event.MessageProgramStateTransition;
import org.motech.event.MessagesCommand;
import org.motech.model.MessageProgramEnrollment;

public class MessageProgramStateTransitionImpl extends BaseInterfaceImpl
		implements MessageProgramStateTransition {

	protected MessageProgramState prevState;
	protected MessageProgramState nextState;
	protected MessagesCommand command;

	public boolean evaluate(MessageProgramEnrollment enrollment) {
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