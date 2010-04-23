package org.motechproject.server.omod;

import junit.framework.TestCase;

import org.openmrs.patient.UnallowedIdentifierException;

public class MotechIdVerhoeffValidatorTest extends TestCase {

	MotechIdVerhoeffValidator validator;

	@Override
	protected void setUp() throws Exception {
		validator = new MotechIdVerhoeffValidator();
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

	public void testGetValidShortIdentifier() {
		String undecoratedIdentifier = "012";
		try {
			validator.getValidIdentifier(undecoratedIdentifier);
			fail();
		} catch (UnallowedIdentifierException e) {
			assertEquals("Undecorated identifier must be 6 digits long.", e
					.getMessage());
		}
	}

	public void testIsValidShortIdentifier() {
		String identifier = "012";
		try {
			validator.isValid(identifier);
			fail();
		} catch (UnallowedIdentifierException e) {
			assertEquals("Identifier must be 7 digits long.", e.getMessage());
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
		String undecoratedIdentifier = "248693";
		String identifier = validator.getValidIdentifier(undecoratedIdentifier);
		assertNotNull("Validator should not be null", identifier);
		assertEquals(7, identifier.length());
		assertTrue("Expected valid identifier with Verhoeff check digit",
				validator.isValid(identifier));
	}

	public void testGetValidKnownVerhoeffIdentifier() {
		String undecoratedIdentifier = "123456";
		assertEquals("1234568", validator
				.getValidIdentifier(undecoratedIdentifier));
	}

	public void testIsValidKnownVerhoeffIdentifier() {
		String identifier = "1234568";
		assertTrue("Expected valid identifier with Verhoeff check digit",
				validator.isValid(identifier));
	}

}
