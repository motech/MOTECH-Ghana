package org.motech.service;

import java.util.Date;

public interface Requirement {

	boolean meetsRequirement(Integer patientId, Date date);

}
