package org.motech.event;

import org.openmrs.Patient;

public interface Regimen extends BaseInterface {

	RegimenState determineState(Patient patient);

	RegimenState updateState(Patient patient);

	RegimenState getStartState();

	RegimenState getEndState();

	String getConceptName();

	String getConceptValue();
}
