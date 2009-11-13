package org.motech.event.impl;

import org.motech.event.Command;
import org.motech.event.MessageProgramState;
import org.motech.event.MessageProgramStateTransition;
import org.openmrs.Patient;

public class MessageProgramStateTransitionImpl extends BaseInterfaceImpl
		implements MessageProgramStateTransition {

	protected MessageProgramState prevState;
	protected MessageProgramState nextState;
	protected Command command;

	public boolean evaluate(Patient patient) {
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

	public Command getCommand() {
		return command;
	}

	public void setCommand(Command command) {
		this.command = command;
	}

}