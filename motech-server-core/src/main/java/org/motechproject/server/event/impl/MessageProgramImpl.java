package org.motechproject.server.event.impl;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.event.MessageProgram;
import org.motechproject.server.event.MessageProgramState;
import org.motechproject.server.event.MessageProgramStateTransition;
import org.motechproject.server.event.MessagesCommand;
import org.motechproject.server.model.MessageProgramEnrollment;

public class MessageProgramImpl extends BaseInterfaceImpl implements
		MessageProgram {

	private static Log log = LogFactory.getLog(MessageProgramImpl.class);

	private MessageProgramState startState;
	private MessageProgramState endState;
	private String conceptName;
	private String conceptValue;

	public MessageProgramState getStartState() {
		return startState;
	}

	public void setStartState(MessageProgramState startState) {
		this.startState = startState;
	}

	public MessageProgramState getEndState() {
		return endState;
	}

	public void setEndState(MessageProgramState endState) {
		this.endState = endState;
	}

	public String getConceptName() {
		return conceptName;
	}

	public void setConceptName(String conceptName) {
		this.conceptName = conceptName;
	}

	public String getConceptValue() {
		return conceptValue;
	}

	public void setConceptValue(String conceptValue) {
		this.conceptValue = conceptValue;
	}

	public MessageProgramState determineState(
			MessageProgramEnrollment enrollment) {
		MessageProgramState state = startState;
		MessageProgramStateTransition transition = state
				.getTransition(enrollment);
		while (!transition.getNextState().equals(state)) {
			state = transition.getNextState();
			transition = state.getTransition(enrollment);
		}

		if (log.isDebugEnabled()) {
			log.debug("Message Program determineState: enrollment id: "
					+ enrollment.getId() + ", state: " + state.getName());
		}

		Date actionDate = state.getDateOfAction(enrollment);

		// Perform state action using date and enrollment
		MessagesCommand command = state.getCommand();
		command.setActionDate(actionDate);
		command.setEnrollment(enrollment);
		command.execute();

		return state;
	}

	public MessageProgramState updateState(MessageProgramEnrollment enrollment) {
		MessageProgramState state = determineState(enrollment);
		if (state.equals(endState)) {
			return state;
		}
		MessageProgramStateTransition transition = state
				.getTransition(enrollment);
		transition.getCommand().execute();
		MessageProgramState newState = transition.getNextState();

		return newState;
	}

}
