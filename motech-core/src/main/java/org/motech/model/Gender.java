package org.motech.model;

/**
 * Represents an individual's gender in motech server. Used to reference valid
 * gender values in a type-safe manner. It also knows how to convert to/from
 * acceptable OpenMRS values.
 */
public enum Gender {
	male, female;

	public String toOpenMRSString() {
		switch (this) {
		case male:
			return "M";
		default: // female
			return "F";
		}
	}

	public static Gender valueOfOpenMRS(String genderLetter) {
		if ("M".equals(genderLetter)) {
			return male;
		} else { // Assuming "F" otherwise
			return female;
		}
	}
}
