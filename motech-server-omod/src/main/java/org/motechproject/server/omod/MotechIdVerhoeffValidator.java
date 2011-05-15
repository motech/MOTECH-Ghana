/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010-11 The Trustees of Columbia University in the City of
 * New York and Grameen Foundation USA.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Grameen Foundation USA, Columbia University, or
 * their respective contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA, COLUMBIA UNIVERSITY
 * AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION
 * USA, COLUMBIA UNIVERSITY OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
        return undecoratedIdentifier + checkDigit;
	}

}
