package org.motechproject.server.util;

import junit.framework.TestCase;

import org.motechproject.ws.Gender;

public class GenderTypeConverterTest extends TestCase {

	public void testToOpenMRSString() {
		assertEquals(MotechConstants.GENDER_MALE_OPENMRS, GenderTypeConverter
				.toOpenMRSString(Gender.MALE));
		assertEquals(MotechConstants.GENDER_FEMALE_OPENMRS, GenderTypeConverter
				.toOpenMRSString(Gender.FEMALE));
		assertEquals(null, GenderTypeConverter.toOpenMRSString(null));
	}

	public void testValueOfOpenMRS() {
		assertEquals(Gender.MALE, GenderTypeConverter
				.valueOfOpenMRS(MotechConstants.GENDER_MALE_OPENMRS));
		assertEquals(Gender.FEMALE, GenderTypeConverter
				.valueOfOpenMRS(MotechConstants.GENDER_FEMALE_OPENMRS));
		assertEquals(null, GenderTypeConverter.valueOfOpenMRS(null));
	}

}
