package org.motechproject.server.omod;

import junit.framework.TestCase;

import org.openmrs.patient.UnallowedIdentifierException;

public class VerhoeffValidatorTest extends TestCase {

	VerhoeffValidator validator;

	@Override
	protected void setUp() throws Exception {
		validator = new VerhoeffValidator();
	}

	@Override
	protected void tearDown() throws Exception {
		validator = null;
	}

	public void testGetValidNullIdentifier() {
		String undecoratedIdentifier = null;
		try {
			validator.getValidIdentifier(undecoratedIdentifier);
			fail();
		} catch (UnallowedIdentifierException e) {
			assertEquals("Identifier can not be null.", e.getMessage());
		}
	}

	public void testIsValidNullIdentifier() {
		String identifier = null;
		try {
			validator.isValid(identifier);
			fail();
		} catch (UnallowedIdentifierException e) {
			assertEquals("Identifier can not be null.", e.getMessage());
		}
	}

	public void testGetValidEmptyIdentifier() {
		String undecoratedIdentifier = "";
		try {
			validator.getValidIdentifier(undecoratedIdentifier);
			fail();
		} catch (UnallowedIdentifierException e) {
			assertEquals("Identifier must contain at least one character.", e
					.getMessage());
		}
	}

	public void testIsValidEmptyIdentifier() {
		String identifier = "";
		try {
			validator.isValid(identifier);
			fail();
		} catch (UnallowedIdentifierException e) {
			assertEquals("Identifier must contain at least one character.", e
					.getMessage());
		}
	}

	public void testGetValidWhitespaceIdentifier() {
		String undecoratedIdentifier = " ";
		try {
			validator.getValidIdentifier(undecoratedIdentifier);
			fail();
		} catch (UnallowedIdentifierException e) {
			assertEquals("Identifier may not contain white space.", e
					.getMessage());
		}
	}

	public void testIsValidWhitespaceIdentifier() {
		String identifier = " ";
		try {
			validator.isValid(identifier);
			fail();
		} catch (UnallowedIdentifierException e) {
			assertEquals("Identifier may not contain white space.", e
					.getMessage());
		}
	}

	public void testGetValidAlphaIdentifier() {
		String undecoratedIdentifier = "ABCDEF";
		try {
			validator.getValidIdentifier(undecoratedIdentifier);
			fail();
		} catch (UnallowedIdentifierException e) {
			assertEquals("\"A\" is an invalid character.", e.getMessage());
		}
	}

	public void testIsValidAlphaIdentifier() {
		String identifier = "ABCDEF0";
		try {
			validator.isValid(identifier);
			fail();
		} catch (UnallowedIdentifierException e) {
			assertEquals("\"A\" is an invalid character.", e.getMessage());
		}
	}

	public void testIsValidAlphaCheckDigitIdentifier() {
		String identifier = "012345X";
		try {
			validator.isValid(identifier);
			fail();
		} catch (UnallowedIdentifierException e) {
			assertEquals("\"X\" is an invalid character.", e.getMessage());
		}
	}

	public void testGetValidIsValidIdentifier() {
		String undecoratedIdentifier = "2";
		String identifier = validator.getValidIdentifier(undecoratedIdentifier);
		assertNotNull("Validator should not be null", identifier);
		assertEquals(2, identifier.length());
		assertEquals("27", identifier);
		assertTrue("Expected valid identifier with Verhoeff check digit",
				validator.isValid(identifier));
	}

	public void testIsValidKnownVerhoeffIdentifier() {

		Integer[] staffIds = { 27, 36, 43, 58, 62, 70, 89, 91, 109, 113, 121,
				132, 145, 150, 166, 178, 184, 197, 204, 215, 227, 236, 243,
				258, 262, 270, 289, 291, 301, 317, 329, 338, 340, 355, 364,
				372, 386, 393, 408, 412, 420, 431, 449, 454 };

		Integer[] facilityIds = { 11117, 11210, 11223, 11313, 11418, 11425,
				11439, 11441, 11516, 11528, 11619, 11626, 11711, 11724, 11814,
				12113, 12121, 12132, 12215, 12227, 12236, 12243, 12317, 12329,
				12338, 12412, 12420, 12514, 12611, 12624, 99998 };

		for (Integer staffId : staffIds) {
			assertTrue("Expected valid identifier with Verhoeff check digit",
					validator.isValid(staffId.toString()));
		}
		for (Integer facilityId : facilityIds) {
			assertTrue("Expected valid identifier with Verhoeff check digit",
					validator.isValid(facilityId.toString()));
		}
	}

}
