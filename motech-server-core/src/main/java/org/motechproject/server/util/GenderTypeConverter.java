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

package org.motechproject.server.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.ws.Gender;

/**
 * Converts Gender enum to/from acceptable OpenMRS values.
 */
public class GenderTypeConverter {


	private static Log log = LogFactory.getLog(GenderTypeConverter.class);

    private GenderTypeConverter() {
        
    }

	public static String toOpenMRSString(Gender gender) {
		if (gender == null) {
			log.warn("Missing Gender type in conversion");
			return null;
		}
		switch (gender) {
		case MALE:
			return MotechConstants.GENDER_MALE_OPENMRS;
		case FEMALE:
			return MotechConstants.GENDER_FEMALE_OPENMRS;
		default:
			log.warn("Unknown Gender type: " + gender);
			return null;
		}
	}

	public static Gender valueOfOpenMRS(String genderLetter) {
		if (MotechConstants.GENDER_MALE_OPENMRS.equals(genderLetter)) {
			return Gender.MALE;
		} else if (MotechConstants.GENDER_FEMALE_OPENMRS.equals(genderLetter)) {
			return Gender.FEMALE;
		} else {
			log.warn("Unknown OpenMRS gender: " + genderLetter);
			return null;
		}
	}
}
