package org.motechproject.server.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.ws.Gender;

/**
 * Converts Gender enum to/from acceptable OpenMRS values.
 */
public class GenderTypeConverter {

	private static Log log = LogFactory.getLog(GenderTypeConverter.class);

	public static String toOpenMRSString(Gender gender) {
		if (gender == null) {
			log.warn("Missing Gender type in conversion");
			return null;
		}
		switch (gender) {
		case MALE:
			return MotechConstants.GENDER_MALE_OPENMRS;
		case FEMALE:
			return MotechConstants.GENDER_FEMALE_OPENMRS;
		default:
			log.warn("Unknown Gender type: " + gender);
			return null;
		}
	}

	public static Gender valueOfOpenMRS(String genderLetter) {
		if (MotechConstants.GENDER_MALE_OPENMRS.equals(genderLetter)) {
			return Gender.MALE;
		} else if (MotechConstants.GENDER_FEMALE_OPENMRS.equals(genderLetter)) {
			return Gender.FEMALE;
		} else {
			log.warn("Unknown OpenMRS gender: " + genderLetter);
			return null;
		}
	}
}
