package org.motech.model;

import junit.framework.TestCase;

import org.motech.util.MotechConstants;
import org.motechproject.ws.Gender;

public class GenderTypeConverterTest extends TestCase {

	public void testToOpenMRSString() {
		assertEquals(MotechConstants.GENDER_MALE_OPENMRS, GenderTypeConverter
				.toOpenMRSString(Gender.MALE));
		assertEquals(MotechConstants.GENDER_FEMALE_OPENMRS, GenderTypeConverter
				.toOpenMRSString(Gender.FEMALE));
	}

	public void testValueOfOpenMRS() {
		assertEquals(Gender.MALE, GenderTypeConverter
				.valueOfOpenMRS(MotechConstants.GENDER_MALE_OPENMRS));
		assertEquals(Gender.FEMALE, GenderTypeConverter
				.valueOfOpenMRS(MotechConstants.GENDER_FEMALE_OPENMRS));
	}

}
