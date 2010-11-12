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

package org.motechproject.server.omod.web.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.motechproject.server.model.MessageProgramEnrollment;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.MotechService;
import org.motechproject.server.omod.web.model.TAMAWebPatient;
import org.motechproject.server.omod.web.validator.TAMAPatientValidator;
import org.motechproject.server.util.MotechConstants;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.MediaType;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.IdentifierNotUniqueException;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

@Controller
@RequestMapping("/module/motechmodule/tama-patient")
@SessionAttributes("motechmodule")
public class TAMAPatientController {

	@Autowired
	private ContextService contextService;

	@Autowired
	private TAMAPatientValidator tamaPatientValidator;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		String datePattern = tamaPatientValidator.getDateFormat();
		SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(
				dateFormat, true, datePattern.length()));

		binder
				.registerCustomEditor(String.class, new StringTrimmerEditor(
						true));
	}

	@ModelAttribute("motechmodule")
	public TAMAWebPatient getWebPatient() {
		return new TAMAWebPatient();
	}

	@RequestMapping(method = RequestMethod.GET)
	public void viewForm() {
	}

	@RequestMapping(method = RequestMethod.POST)
	public String submitForm(
			@ModelAttribute("motechmodule") TAMAWebPatient webPatient,
			Errors errors, ModelMap model, SessionStatus status) {

		tamaPatientValidator.validate(webPatient, errors);

		if (!errors.hasErrors()) {
			Patient patient = null;
			try {
				patient = savePatient(webPatient);
			} catch (IdentifierNotUniqueException e) {
				errors.rejectValue("patientId", "motechmodule.patientId.inuse");
				return "/module/motechmodule/tama-patient";
			}
			saveEnrollment(patient);

			status.setComplete();
			return "redirect:/module/motechmodule/tama-patient-success.htm";
		}
		return "/module/motechmodule/tama-patient";
	}

	private Patient savePatient(TAMAWebPatient webPatient) {
		PatientService patientService = contextService.getPatientService();
		LocationService locationService = contextService.getLocationService();

		Patient patient = new Patient();
		patient.setGender(webPatient.getGender());
		patient.setBirthdate(webPatient.getDateOfBirth());

		Location location = locationService.getDefaultLocation();
		PatientIdentifierType identifierType = patientService
				.getPatientIdentifierTypeByName("Old Identification Number");

		// Set patient ID
		PatientIdentifier identifier = new PatientIdentifier();
		identifier.setIdentifier(webPatient.getPatientId());
		identifier.setIdentifierType(identifierType);
		identifier.setLocation(location);
		patient.addIdentifier(identifier);

		PersonService personService = contextService.getPersonService();
		PersonAttributeType phoneAttributeType = personService
				.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_PHONE_NUMBER);

		// Set patient mobile phone number
		PersonAttribute phoneAttribute = new PersonAttribute();
		phoneAttribute.setValue(webPatient.getMobileNumber());
		phoneAttribute.setAttributeType(phoneAttributeType);
		patient.addAttribute(phoneAttribute);

		// Set patient phone type (for messaging)
		PersonAttributeType phoneTypeAttributeType = personService
				.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_PHONE_TYPE);
		PersonAttribute phoneTypeAttribute = new PersonAttribute();
		phoneTypeAttribute.setValue(ContactNumberType.PERSONAL.name());
		phoneTypeAttribute.setAttributeType(phoneTypeAttributeType);
		patient.addAttribute(phoneTypeAttribute);

		// Set patient media type as voice (for messaging)
		PersonAttributeType mediaTypeAttributeType = personService
				.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE);
		PersonAttribute mediaTypeAttribute = new PersonAttribute();
		mediaTypeAttribute.setValue(MediaType.VOICE.name());
		mediaTypeAttribute.setAttributeType(mediaTypeAttributeType);
		patient.addAttribute(mediaTypeAttribute);

		// Set patient language (for messaging)
		PersonAttributeType languageAttributeType = personService
				.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_LANGUAGE);
		PersonAttribute langAttribute = new PersonAttribute();
		langAttribute.setValue("en");
		langAttribute.setAttributeType(languageAttributeType);
		patient.addAttribute(langAttribute);

		return patientService.savePatient(patient);
	}

	private void saveEnrollment(Patient patient) {
		MessageProgramEnrollment enrollment = new MessageProgramEnrollment();
		enrollment.setPersonId(patient.getPatientId());
		enrollment.setProgram("TAMA Demo Message Program");
		enrollment.setStartDate(new Date());

		MotechService motechService = contextService.getMotechService();
		motechService.saveMessageProgramEnrollment(enrollment);
	}

}