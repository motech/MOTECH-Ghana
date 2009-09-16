package org.motech.event;

import java.util.List;
import java.util.Set;

import org.openmrs.Patient;

public interface Program extends BaseInterface {

	Set<Regimen> getRegimens();

	boolean isEnabled();

	List<Patient> getParticipants();

	List<Patient> getActiveParticipants();
}
