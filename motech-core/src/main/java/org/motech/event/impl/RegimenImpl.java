package org.motech.event.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motech.event.Command;
import org.motech.event.Regimen;
import org.motech.event.RegimenState;
import org.motech.event.RegimenStateTransition;
import org.openmrs.Patient;

public class RegimenImpl extends BaseInterfaceImpl implements Regimen {

	private static Log log = LogFactory.getLog(RegimenImpl.class);

	private RegimenState startState;
	private RegimenState endState;
	private Map<Patient, RegimenState> patientStates = new HashMap<Patient, RegimenState>();
	private String conceptName;
	private String conceptValue;

	public RegimenState getStartState() {
		return startState;
	}

	public void setStartState(RegimenState startState) {
		this.startState = startState;
	}

	public RegimenState getEndState() {
		return endState;
	}

	public void setEndState(RegimenState endState) {
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

	public RegimenState determineState(Patient patient) {
		RegimenState state = startState;
		RegimenStateTransition transition = state.getTransition(patient);
		while (!transition.getNextState().equals(state)) {
			state = transition.getNextState();
			transition = state.getTransition(patient);
		}

		if (log.isDebugEnabled()) {
			log.debug("Regimen determineState: patient id: "
					+ patient.getPatientId() + ", state: " + state.getName());
		}

		// Perform state action using date
		Command command = state.getCommand();
		List<Command> allCommands = null;
		if (command instanceof CompositeCommand) {
			allCommands = ((CompositeCommand) command).getCommands();
		} else {
			allCommands = new ArrayList<Command>();
			allCommands.add(command);
		}
		// Handle setting properties for all commands incase composite command
		boolean performExecute = true;
		for (Command commandInList : allCommands) {
			if (commandInList instanceof ScheduleMessageCommand) {
				Date messageDate = state.getDateOfAction(patient);
				if (messageDate == null) {
					performExecute = false;
				}
				((ScheduleMessageCommand) commandInList)
						.setMessageDate(messageDate);
				((ScheduleMessageCommand) commandInList)
						.setMessageRecipientId(patient.getPatientId());
				((ScheduleMessageCommand) commandInList).setMessageGroup(this
						.getName());
			} else if (commandInList instanceof RemoveMessagesCommand) {
				((RemoveMessagesCommand) commandInList)
						.setMessageRecipientId(patient.getPatientId());
				((RemoveMessagesCommand) commandInList).setMessageGroup(this
						.getName());
			} else if (commandInList instanceof RemoveRegimenEnrollmentCommand) {
				((RemoveRegimenEnrollmentCommand) commandInList)
						.setPersonId(patient.getPatientId());
				((RemoveRegimenEnrollmentCommand) commandInList)
						.setRegimenName(this.getName());
			}
		}
		if (performExecute) {
			command.execute();
		}

		return state;
	}

	public RegimenState getState(Patient patient) {
		RegimenState state = patientStates.get(patient);
		if (state == null) {
			state = determineState(patient);
			patientStates.put(patient, state);
		}
		return state;
	}

	public RegimenState updateState(Patient patient) {
		RegimenState state = getState(patient);
		if (state.equals(endState)) {
			return state;
		}
		RegimenStateTransition transition = state.getTransition(patient);
		transition.getCommand().execute();
		RegimenState newState = transition.getNextState();
		patientStates.put(patient, newState);

		return newState;
	}

}
