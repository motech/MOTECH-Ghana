package org.motech.event;

import org.openmrs.Patient;

public interface MessageProgram extends BaseInterface {

	MessageProgramState determineState(Patient patient);

	MessageProgramState updateState(Patient patient);

	MessageProgramState getStartState();

	MessageProgramState getEndState();

	String getConceptName();

	String getConceptValue();
}
