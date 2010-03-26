package org.motechproject.server.service.impl;

import java.util.Date;

import org.motechproject.server.service.Requirement;
import org.openmrs.Patient;

public class AliveRequirement implements Requirement {

	public boolean meetsRequirement(Patient patient, Date date) {

		return !patient.isVoided() && !patient.isDead();
	}

}
