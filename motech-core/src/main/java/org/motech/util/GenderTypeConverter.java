package org.motech.util;

import org.motechproject.ws.Gender;

/**
 * Converts Gender enum to/from acceptable OpenMRS values.
 */
public class GenderTypeConverter {

	public static String toOpenMRSString(Gender gender) {
		switch (gender) {
		case MALE:
			return MotechConstants.GENDER_MALE_OPENMRS;
		case FEMALE:
			return MotechConstants.GENDER_FEMALE_OPENMRS;
		default:
			return null;
		}
	}

	public static Gender valueOfOpenMRS(String genderLetter) {
		if (MotechConstants.GENDER_MALE_OPENMRS.equals(genderLetter)) {
			return Gender.MALE;
		} else if (MotechConstants.GENDER_FEMALE_OPENMRS.equals(genderLetter)) {
			return Gender.FEMALE;
		} else {
			return null;
		}
	}
}
