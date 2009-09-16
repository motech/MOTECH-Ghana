package org.motech.event.impl;

import org.motech.event.Command;
import org.motech.event.RegimenState;
import org.motech.event.RegimenStateTransition;
import org.openmrs.Patient;

public class RegimenStateTransitionImpl extends BaseInterfaceImpl implements
		RegimenStateTransition {

	protected RegimenState prevState;
	protected RegimenState nextState;
	protected Command command;

	public boolean evaluate(Patient patient) {
		// Default Transition is always taken
		return true;
	}

	public RegimenState getPrevState() {
		return prevState;
	}

	public void setPrevState(RegimenState prevState) {
		this.prevState = prevState;
	}

	public RegimenState getNextState() {
		return nextState;
	}

	public void setNextState(RegimenState nextState) {
		this.nextState = nextState;
	}

	public Command getCommand() {
		return command;
	}

	public void setCommand(Command command) {
		this.command = command;
	}

}