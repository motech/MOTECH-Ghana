package org.motech.model;

import junit.framework.TestCase;

public class GenderTest extends TestCase {

	public void testToOpenMRSString() {
		assertEquals("M", Gender.male.toOpenMRSString());
		assertEquals("F", Gender.female.toOpenMRSString());
	}

	public void testValueOfOpenMRS() {
		assertEquals(Gender.male, Gender.valueOfOpenMRS("M"));
		assertEquals(Gender.female, Gender.valueOfOpenMRS("F"));
	}

}
