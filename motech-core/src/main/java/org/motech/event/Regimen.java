package org.motech.event;

import org.openmrs.Patient;

public interface Regimen extends BaseInterface {

	RegimenState updateState(Patient patient);

	RegimenState getState(Patient patient);

	RegimenState getStartState();

	RegimenState getEndState();
}
