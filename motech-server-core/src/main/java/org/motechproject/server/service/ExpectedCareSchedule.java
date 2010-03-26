package org.motechproject.server.service;

import java.util.Date;

import org.openmrs.Patient;

public interface ExpectedCareSchedule {

	String getName();

	boolean meetsRequirements(Patient patient, Date date);

	void updateSchedule(Patient patient, Date date);

}
