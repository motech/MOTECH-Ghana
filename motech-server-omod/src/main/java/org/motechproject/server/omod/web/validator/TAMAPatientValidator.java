/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010 The Trustees of Columbia University in the City of
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

package org.motechproject.server.omod.web.validator;

import org.motechproject.server.omod.web.model.TAMAWebPatient;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class TAMAPatientValidator implements Validator {

	private String dateFormat;
	private String patientIdPattern;
	private String genderPattern;
	private String mobileNumberPattern;

	public boolean supports(Class clazz) {
		return TAMAWebPatient.class.equals(clazz);
	}

	public void validate(Object o, Errors errors) {
		TAMAWebPatient webPatient = (TAMAWebPatient) o;

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "patientId",
				"motechmodule.patientId.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "gender",
				"motechmodule.gender.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dateOfBirth",
				"motechmodule.dateOfBirth.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "mobileNumber",
				"motechmodule.mobileNumber.required");

		if (webPatient.getPatientId() != null) {
			if (!webPatient.getPatientId().matches(patientIdPattern)) {
				errors.rejectValue("patientId",
						"motechmodule.patientId.alphanum");
			}
		}
		if (webPatient.getGender() != null
				&& !webPatient.getGender().matches(genderPattern)) {
			errors.rejectValue("gender", "motechmodule.gender.invalid");
		}
		if (webPatient.getMobileNumber() != null
				&& !webPatient.getMobileNumber().matches(mobileNumberPattern)) {
			errors.rejectValue("mobileNumber",
					"motechmodule.mobileNumber.numeric");
		}
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String getPatientIdPattern() {
		return patientIdPattern;
	}

	public void setPatientIdPattern(String patientIdPattern) {
		this.patientIdPattern = patientIdPattern;
	}

	public String getGenderPattern() {
		return genderPattern;
	}

	public void setGenderPattern(String genderPattern) {
		this.genderPattern = genderPattern;
	}

	public String getMobileNumberPattern() {
		return mobileNumberPattern;
	}

	public void setMobileNumberPattern(String mobileNumberPattern) {
		this.mobileNumberPattern = mobileNumberPattern;
	}

}
