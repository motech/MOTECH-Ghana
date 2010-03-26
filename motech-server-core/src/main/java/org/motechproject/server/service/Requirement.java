package org.motechproject.server.service;

import java.util.Date;

import org.openmrs.Patient;

public interface Requirement {

	boolean meetsRequirement(Patient patient, Date date);

}
