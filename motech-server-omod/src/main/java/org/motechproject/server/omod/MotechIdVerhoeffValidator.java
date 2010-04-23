package org.motechproject.server.omod;

import org.openmrs.patient.UnallowedIdentifierException;
import org.openmrs.patient.impl.BaseHyphenatedIdentifierValidator;

/**
 * Verhoeff PatientIdentifier Validator requiring a numeric Patient Id with a
 * numeric Verhoeff check digit having a total length of 7 characters.
 * 
 * Although this PatientIdentifier Validator does not allow hyphens, it extends
 * the BaseHyphenatedIdentifierValidator to use the allowed characters, name,
 * and checkAllowedIdentifier methods.
 * 
 * Verhoeff Implementation to possibly be replaced with VerhoeffCheckDigit in
 * Commons Validator in 1.4 release
 * 
 * @author Matthew Blanchette
 */
public class MotechIdVerhoeffValidator extends
		BaseHyphenatedIdentifierValidator {

	public static final String ALLOWED_CHARS = "0123456789";

	public static final String VERHOEFF_NAME = "MoTeCH ID Verhoeff Check Digit Validator";

	public static final int VERHOEFF_ID_LENGTH = 7;

	public static final int VERHOEFF_UNDECORATED_ID_LENGTH = VERHOEFF_ID_LENGTH - 1;

	/**
	 * @see org.openmrs.patient.impl.BaseHyphenatedIdentifierValidator#getAllowedCharacters()
	 */
	@Override
	public String getAllowedCharacters() {
		return ALLOWED_CHARS;
	}

	/**
	 * @see org.openmrs.patient.impl.BaseHyphenatedIdentifierValidator#getName()
	 */
	@Override
	public String getName() {
		return VERHOEFF_NAME;
	}

	/**
	 * @see org.openmrs.patient.impl.BaseHyphenatedIdentifierValidator#getCheckDigit(java.lang.String)
	 */
	@Override
	protected int getCheckDigit(String undecoratedIdentifier) {
		int checkDigit = calculateCheckDigit(undecoratedIdentifier, false);
		return inv_table[checkDigit];
	}

	/**
	 * Override to disallow non-numeric check digits (using allowed characters)
	 * and identifiers that are not exactly VERHOEFF_ID_LENGTH long (7
	 * characters).
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

	protected boolean isValidCheckDigit(String identifier) {
		int checkDigit = calculateCheckDigit(identifier, true);
		return checkDigit == 0;
	}

	protected int calculateCheckDigit(String identifier,
			boolean includesCheckDigit) {
		int checkDigit = 0;
		int i = includesCheckDigit ? 0 : 1;

		for (int j = identifier.length() - 1; j >= 0; j--) {
			int number = Character.getNumericValue(identifier.charAt(j));
			if (number < 0 || number > 9) {
				throw new UnallowedIdentifierException("\""
						+ identifier.charAt(j) + "\" is an invalid character.");
			}
			checkDigit = d_table[checkDigit][p_table[i % 8][number]];
			i++;
		}
		return checkDigit;
	}

	/**
	 * The multiplication table
	 */
	private final static int[][] d_table = new int[][] {
			{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }, { 1, 2, 3, 4, 0, 6, 7, 8, 9, 5 },
			{ 2, 3, 4, 0, 1, 7, 8, 9, 5, 6 }, { 3, 4, 0, 1, 2, 8, 9, 5, 6, 7 },
			{ 4, 0, 1, 2, 3, 9, 5, 6, 7, 8 }, { 5, 9, 8, 7, 6, 0, 4, 3, 2, 1 },
			{ 6, 5, 9, 8, 7, 1, 0, 4, 3, 2 }, { 7, 6, 5, 9, 8, 2, 1, 0, 4, 3 },
			{ 8, 7, 6, 5, 9, 3, 2, 1, 0, 4 }, { 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 } };

	/**
	 * The permutation table
	 */
	private final static int[][] p_table = new int[][] {
			{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }, { 1, 5, 7, 6, 2, 8, 3, 0, 9, 4 },
			{ 5, 8, 0, 3, 7, 9, 6, 1, 4, 2 }, { 8, 9, 1, 6, 0, 4, 3, 5, 2, 7 },
			{ 9, 4, 5, 3, 1, 2, 6, 8, 7, 0 }, { 4, 2, 8, 6, 5, 7, 3, 9, 0, 1 },
			{ 2, 7, 9, 3, 8, 0, 6, 4, 1, 5 }, { 7, 0, 4, 6, 9, 1, 3, 2, 5, 8 } };

	/**
	 * The inverse table
	 */
	private final static int[] inv_table = { 0, 4, 3, 2, 1, 5, 6, 7, 8, 9 };

}
