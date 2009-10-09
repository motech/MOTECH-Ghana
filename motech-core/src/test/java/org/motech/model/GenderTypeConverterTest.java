package org.motech.model;

import junit.framework.TestCase;

import org.motechproject.ws.Gender;

public class GenderTypeConverterTest extends TestCase {

	public void testToOpenMRSString() {
		assertEquals("M", GenderTypeConverter.toOpenMRSString(Gender.MALE));
		assertEquals("F", GenderTypeConverter.toOpenMRSString(Gender.FEMALE));
	}

	public void testValueOfOpenMRS() {
		assertEquals(Gender.MALE, GenderTypeConverter.valueOfOpenMRS("M"));
		assertEquals(Gender.FEMALE, GenderTypeConverter.valueOfOpenMRS("F"));
	}

}
