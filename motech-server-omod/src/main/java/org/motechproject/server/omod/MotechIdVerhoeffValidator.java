package org.motechproject.server.omod;

import org.openmrs.patient.UnallowedIdentifierException;

/**
 * Verhoeff PatientIdentifier Validator specific to MoTeCH IDs using a numeric
 * Verhoeff check digit having a total length of 7 characters.
 * 
 * @author Matthew Blanchette
 */
public class MotechIdVerhoeffValidator extends VerhoeffValidator {

	public static final String VERHOEFF_NAME = "MoTeCH ID Verhoeff Check Digit Validator";

	public static final int VERHOEFF_ID_LENGTH = 7;

	public static final int VERHOEFF_UNDECORATED_ID_LENGTH = VERHOEFF_ID_LENGTH - 1;

	/**
	 * @see org.openmrs.patient.impl.BaseHyphenatedIdentifierValidator#getName()
	 */
	@Override
	public String getName() {
		return VERHOEFF_NAME;
	}

	/**
	 * Override to disallow identifiers that are not exactly VERHOEFF_ID_LENGTH
	 * long (7 characters).
	 * 
	 * @see org.openmrs.patient.impl.BaseHyphenatedIdentifierValidator#isValid(java.lang.String)
	 */
	@Override
	public boolean isValid(String identifier)
			throws UnallowedIdentifierException {

		checkAllowedIdentifier(identifier);

		if (identifier.length() != VERHOEFF_ID_LENGTH)
			throw new UnallowedIdentifierException("Identifier must be "
					+ VERHOEFF_ID_LENGTH + " digits long.");

		return isValidCheckDigit(identifier);
	}

	/**
	 * Override to disallow identifiers that are not exactly
	 * VERHOEFF_UNDECORATED_ID_LENGTH long (6 characters).
	 * 
	 * @see org.openmrs.patient.impl.BaseHyphenatedIdentifierValidator#getValidIdentifier(java.lang.String)
	 */
	@Override
	public String getValidIdentifier(String undecoratedIdentifier)
			throws UnallowedIdentifierException {

		checkAllowedIdentifier(undecoratedIdentifier);

		if (undecoratedIdentifier.length() != VERHOEFF_UNDECORATED_ID_LENGTH)
			throw new UnallowedIdentifierException(
					"Undecorated identifier must be "
							+ VERHOEFF_UNDECORATED_ID_LENGTH + " digits long.");

		int checkDigit = getCheckDigit(undecoratedIdentifier);
		String result = undecoratedIdentifier + checkDigit;
		return result;
	}

}
