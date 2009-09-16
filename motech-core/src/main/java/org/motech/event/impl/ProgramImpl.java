package org.motech.event.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.motech.event.Program;
import org.motech.event.Regimen;
import org.openmrs.Patient;

public class ProgramImpl extends BaseInterfaceImpl implements Program {

	List<Patient> activeParticipants = new ArrayList<Patient>();
	List<Patient> participants = new ArrayList<Patient>();
	Set<Regimen> regimens = new HashSet<Regimen>();
	boolean isEnabled;

	public List<Patient> getActiveParticipants() {
		return activeParticipants;
	}

	public void setActiveParticipants(List<Patient> activeParticipants) {
		this.activeParticipants = activeParticipants;
	}

	public List<Patient> getParticipants() {
		return participants;
	}

	public void setParticipants(List<Patient> participants) {
		this.participants = participants;
	}

	public Set<Regimen> getRegimens() {
		return regimens;
	}

	public void setRegimens(Set<Regimen> regimens) {
		this.regimens = regimens;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

}
