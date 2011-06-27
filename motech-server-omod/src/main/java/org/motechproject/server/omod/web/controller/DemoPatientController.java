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

package org.motechproject.server.omod.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.model.ghana.Community;
import org.motechproject.server.omod.MotechIdVerhoeffValidator;
import org.motechproject.server.omod.web.model.WebModelConverter;
import org.motechproject.server.omod.web.model.WebPatient;
import org.motechproject.server.service.ContextService;
import org.motechproject.server.svc.OpenmrsBean;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.server.util.MotechConstants;
import org.motechproject.ws.*;
import org.openmrs.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@Controller
@RequestMapping("/module/motechmodule/demo-patient")
@SessionAttributes("patient")
public class DemoPatientController extends BasePatientController {

	private static Log log = LogFactory.getLog(DemoPatientController.class);

	private WebModelConverter webModelConverter;

	@Autowired
	@Qualifier("registrarBean")
	private RegistrarBean registrarBean;

	@Autowired
	@Qualifier("openmrsBean")
	private OpenmrsBean openmrsBean;

	@Autowired
	public void setContextService(ContextService contextService) {
		this.contextService = contextService;
	}

	public void setRegistrarBean(RegistrarBean registrarBean) {
		this.registrarBean = registrarBean;
	}

	public void setWebModelConverter(WebModelConverter webModelConverter) {
		this.webModelConverter = webModelConverter;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		String datePattern = "dd/MM/yyyy";
		SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(
				dateFormat, true, datePattern.length()));

		String timePattern = MotechConstants.TIME_FORMAT_DELIVERY_TIME;
		SimpleDateFormat timeFormat = new SimpleDateFormat(timePattern);
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, "timeOfDay",
				new CustomDateEditor(timeFormat, true, timePattern.length()));

		binder
				.registerCustomEditor(String.class, new StringTrimmerEditor(
						true));
	}

	@ModelAttribute("regions")
	public List<String> getRegions() {
		return JSONLocationSerializer.getAllRegions();
	}

	@ModelAttribute("districts")
	public List<String> getDistricts() {
		return JSONLocationSerializer.getAllDistricts();
	}

	@ModelAttribute("communities")
	public List<Community> getCommunities() {
		return JSONLocationSerializer.getAllCommunities(false);
	}

	@RequestMapping(method = RequestMethod.GET)
	public void viewForm(@RequestParam(required = false) Integer id,ModelMap model) {
        WebPatient patient = new WebPatient();
        JSONLocationSerializer.populateJavascriptMaps(model, patient.getPreferredLocation());
	}

	@ModelAttribute("patient")
	public WebPatient getWebPatient(@RequestParam(required = false) Integer id) {
		WebPatient result = new WebPatient();
		if (id != null) {
			Patient patient = contextService.getPatientService().getPatient(id);

			if (patient != null) {
				webModelConverter.patientToWeb(patient, result);
			}
		} else {
			// Pre-populate so demo entry isn't a long process
			result.setRegistrationMode(RegistrationMode.AUTO_GENERATE_ID);
			result.setFirstName("Jane");
			result.setLastName("Doe");
			result.setSex(Gender.FEMALE);
			Calendar dobCal = new GregorianCalendar();
			dobCal.set(1984, 0, 4);
			result.setBirthDate(dobCal.getTime());
			result.setBirthDateEst(Boolean.FALSE);
			result.setInsured(Boolean.FALSE);
			result.setRegion(getRegions().get(0));
			result.setDistrict(getDistricts().get(0));
			result.setCommunityId(getCommunities().get(0).getCommunityId());
			result.setAddress("Somewhere important");
			result.setEnroll(Boolean.TRUE);
			result.setPhoneType(ContactNumberType.PERSONAL);
			result.setMediaType(MediaType.TEXT);
			result.setLanguage("en");
			result.setHowLearned(HowLearned.MOTECH_FIELD_AGENT);
			result
					.setInterestReason(InterestReason.KNOW_MORE_PREGNANCY_CHILDBIRTH);
		}
		return result;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String submitForm(@ModelAttribute("patient") WebPatient patient,
			Errors errors, ModelMap model, SessionStatus status,
			HttpSession session) {

		log.debug("Register Demo Patient");

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "registrationMode",
				"motechmodule.registrationMode.required");
		if (patient.getRegistrationMode() == RegistrationMode.USE_PREPRINTED_ID) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "motechId",
					"motechmodule.motechId.required");
		}
		if (patient.getMotechId() != null) {
			String motechIdString = patient.getMotechId().toString();
			boolean validId = false;
			MotechIdVerhoeffValidator validator = new MotechIdVerhoeffValidator();
			try {
				validId = validator.isValid(motechIdString);
			} catch (Exception e) {
			}
			if (!validId) {
				errors.rejectValue("motechId", "motechmodule.motechId.invalid");
			} else if (openmrsBean.getPatientByMotechId(motechIdString) != null) {
				errors.rejectValue("motechId",
						"motechmodule.motechId.nonunique");
			}
		}

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName",
				"motechmodule.firstName.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName",
				"motechmodule.lastName.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "birthDate",
				"motechmodule.birthDate.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "birthDateEst",
				"motechmodule.birthDateEst.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "sex",
				"motechmodule.sex.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "insured",
				"motechmodule.insured.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "region",
				"motechmodule.region.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "district",
				"motechmodule.district.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "communityId",
				"motechmodule.communityId.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "address",
				"motechmodule.address.required");

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "enroll",
				"motechmodule.enroll.required");

		if (Boolean.TRUE.equals(patient.getEnroll())) {
			if (!Boolean.TRUE.equals(patient.getConsent())) {
				errors.rejectValue("consent", "motechmodule.consent.required");
			}
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phoneType",
					"motechmodule.phoneType.required");
			if (patient.getPhoneType() == ContactNumberType.PERSONAL
					|| patient.getPhoneType() == ContactNumberType.HOUSEHOLD) {
				ValidationUtils.rejectIfEmptyOrWhitespace(errors,
						"phoneNumber", "motechmodule.phoneNumber.required");
			}
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "mediaType",
					"motechmodule.mediaType.required");
			if (patient.getPhoneType() == ContactNumberType.PUBLIC
					&& patient.getMediaType() != null
					&& patient.getMediaType() != MediaType.VOICE) {
				errors.rejectValue("mediaType", "motechmodule.mediaType.voice");
			}
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "language",
					"motechmodule.language.required");
			if (patient.getMediaType() == MediaType.TEXT
					&& patient.getLanguage() != null
					&& !patient.getLanguage().equals("en")) {
				errors.rejectValue("language", "motechmodule.language.english");
			}
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "interestReason",
					"motechmodule.interestReason.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "howLearned",
					"motechmodule.howLearned.required");
		}

		Community community = null;
		if (patient.getCommunityId() != null) {
			community = registrarBean
					.getCommunityById(patient.getCommunityId());
			if (community == null) {
				errors.rejectValue("communityId",
						"motechmodule.communityId.notexist");
			}
		}

		if (patient.getPhoneNumber() != null
				&& !patient.getPhoneNumber().matches(
						MotechConstants.PHONE_REGEX_PATTERN)) {
			errors.rejectValue("phoneNumber",
					"motechmodule.phoneNumber.invalid");
		}

		validateTextLength(errors, "firstName", patient.getFirstName(),
				MotechConstants.MAX_STRING_LENGTH_OPENMRS);
		validateTextLength(errors, "middleName", patient.getMiddleName(),
				MotechConstants.MAX_STRING_LENGTH_OPENMRS);
		validateTextLength(errors, "lastName", patient.getLastName(),
				MotechConstants.MAX_STRING_LENGTH_OPENMRS);
		validateTextLength(errors, "prefName", patient.getPrefName(),
				MotechConstants.MAX_STRING_LENGTH_OPENMRS);
		validateTextLength(errors, "nhis", patient.getNhis(),
				MotechConstants.MAX_STRING_LENGTH_OPENMRS);
		validateTextLength(errors, "address", patient.getAddress(),
				MotechConstants.MAX_STRING_LENGTH_OPENMRS);
		validateTextLength(errors, "phoneNumber", patient.getPhoneNumber(),
				MotechConstants.MAX_STRING_LENGTH_OPENMRS);

		if (!errors.hasErrors()) {
			registrarBean.demoRegisterPatient(patient.getRegistrationMode(),
					patient.getMotechId(), patient.getFirstName(), patient
							.getMiddleName(), patient.getLastName(), patient
							.getPrefName(), patient.getBirthDate(), patient
							.getBirthDateEst(), patient.getSex(), patient
							.getInsured(), patient.getNhis(), patient
							.getNhisExpDate(), community, patient.getAddress(),
					patient.getPhoneNumber(), patient.getEnroll(), patient
							.getConsent(), patient.getPhoneType(), patient
							.getMediaType(), patient.getLanguage(), patient
							.getDayOfWeek(), patient.getTimeOfDay(), patient
							.getInterestReason(), patient.getHowLearned());

			model.addAttribute("successMsg",
					"motechmodule.Demo.Patient.register.success");

			status.setComplete();

			// Save the registration number for next step of demo
			session.setAttribute("demoLastMotechId", patient.getMotechId());

			return "redirect:/module/motechmodule/demo-success.htm";
		}

		JSONLocationSerializer.populateJavascriptMaps(model, patient.getPreferredLocation());

		return "/module/motechmodule/demo-patient";
	}

	void validateTextLength(Errors errors, String fieldname, String fieldValue,
			int lengthLimit) {

		if (fieldValue != null && fieldValue.length() > lengthLimit) {
			errors.rejectValue(fieldname, "motechmodule.string.maxlength",
					new Integer[] { lengthLimit },
					"Specified text is longer than max characters.");
		}
	}
}
