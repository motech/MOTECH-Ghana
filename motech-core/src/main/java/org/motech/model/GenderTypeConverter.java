package org.motech.model;

import org.motechproject.ws.Gender;

/**
 * Converts Gender enum to/from acceptable OpenMRS values.
 */
public class GenderTypeConverter {

	public static String toOpenMRSString(Gender gender) {
		switch (gender) {
		case MALE:
			return "M";
		case FEMALE:
			return "F";
		default:
			return null;
		}
	}

	public static Gender valueOfOpenMRS(String genderLetter) {
		if ("M".equals(genderLetter)) {
			return Gender.MALE;
		} else if ("F".equals(genderLetter)) {
			return Gender.FEMALE;
		} else {
			return null;
		}
	}
}
