package org.motechproject.server.service;

import java.util.Date;

public interface Requirement {

	boolean meetsRequirement(Integer patientId, Date date);

}
