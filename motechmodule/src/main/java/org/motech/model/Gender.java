package org.motech.model;

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
