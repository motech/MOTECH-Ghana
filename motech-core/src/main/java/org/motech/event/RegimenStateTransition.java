package org.motech.event;

import org.openmrs.Patient;

public interface RegimenStateTransition extends BaseInterface {

	RegimenState getPrevState();

	RegimenState getNextState();

	Command getCommand();

	boolean evaluate(Patient patient);
}
