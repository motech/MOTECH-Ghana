package org.motechproject.server.service.impl;

import java.util.Date;

import org.motechproject.server.service.Requirement;
import org.motechproject.server.util.GenderTypeConverter;
import org.motechproject.ws.Gender;
import org.openmrs.Patient;

public class GenderRequirement implements Requirement {

	private Gender gender;

	public boolean meetsRequirement(Patient patient, Date date) {

		return patient.getGender().equals(
				GenderTypeConverter.toOpenMRSString(gender));
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

}
