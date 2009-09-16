package org.motech.event.impl;

import java.util.HashMap;
import java.util.Map;

import org.motech.event.Regimen;
import org.motech.event.RegimenState;
import org.motech.event.RegimenStateTransition;
import org.openmrs.Patient;

public class RegimenImpl extends BaseInterfaceImpl implements Regimen {

	private RegimenState startState;
	private RegimenState endState;
	private Map<Patient, RegimenState> patientStates = new HashMap<Patient, RegimenState>();

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

	public RegimenState determineState(Patient patient) {
		RegimenState state = startState;
		RegimenStateTransition transition = state.getTransition(patient);
		while (!transition.getNextState().equals(state)) {
			state = transition.getNextState();
			transition = state.getTransition(patient);
		}

		// Perform state action using date
		state.getDateOfAction(patient);
		state.getCommand().execute();

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

		// Perform state action using date
		newState.getDateOfAction(patient);
		newState.getCommand().execute();

		return newState;
	}

}
