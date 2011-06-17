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
import org.motechproject.server.omod.web.model.WebModelConverter;
import org.motechproject.server.omod.web.model.WebPatient;
import org.motechproject.server.service.ContextService;
import org.motechproject.server.svc.RegistrarBean;
import org.openmrs.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping(value = "/module/motechmodule/search")
public class SearchPatientsController {

	protected final Log log = LogFactory.getLog(SearchPatientsController.class);

	@Autowired
	@Qualifier("registrarBean")
	private RegistrarBean registrarBean;

	private ContextService contextService;
	private WebModelConverter webModelConverter;

	public void setRegistrarBean(RegistrarBean registrarBean) {
		this.registrarBean = registrarBean;
	}

	@Autowired
	public void setContextService(ContextService contextService) {
		this.contextService = contextService;
	}

	@Autowired
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
		binder
				.registerCustomEditor(String.class, new StringTrimmerEditor(
						true));
	}

	@ModelAttribute("communities")
	public List<Community> getCommunities() {
		return contextService.getMotechService().getAllCommunities(true);
	}

	@ModelAttribute("patient")
	public WebPatient getWebPatient(ModelMap model) {
		return new WebPatient();
	}

	@ModelAttribute("matchingPatients")
	public List<WebPatient> getMatchingPatients(ModelMap model) {
		return new ArrayList<WebPatient>();
	}

	@RequestMapping(method = RequestMethod.GET)
	public void viewForm(@RequestParam(required = false) Integer id,
			ModelMap model) {
	}

	@RequestMapping(method = RequestMethod.POST)
	public void submitForm(@ModelAttribute("patient") WebPatient webPatient,
			Errors errors, ModelMap model, SessionStatus status) {

		if (log.isDebugEnabled()) {
			log.debug("Search Matching Patients: " + webPatient.getFirstName()
					+ ", " + webPatient.getLastName() + ", "
					+ webPatient.getPrefName() + ", "
					+ webPatient.getBirthDate() + ", "
					+ webPatient.getCommunityId() + ", "
					+ webPatient.getPhoneNumber() + ", " + webPatient.getNhis()
					+ ", " + webPatient.getMotechId());
		}

		String motechIdString = null;
		if (webPatient.getMotechId() != null) {
			motechIdString = webPatient.getMotechId().toString();
		}

		if (webPatient.getCommunityId() != null) {
			Community community = registrarBean.getCommunityById(webPatient
					.getCommunityId());
			if (community == null) {
				errors.rejectValue("communityId",
						"motechmodule.communityId.notexist");
			}
		}

		if (!errors.hasErrors()) {
			List<WebPatient> matchingWebPatientsList = new ArrayList<WebPatient>();
			List<Patient> matchingPatientsList = registrarBean.getPatients(
					webPatient.getFirstName(), webPatient.getLastName(),
					webPatient.getPrefName(), webPatient.getBirthDate(),
					webPatient.getFacility(), webPatient.getPhoneNumber(),
					webPatient.getNhis(), webPatient.getCommunityId(), motechIdString);

			for (Patient patient : matchingPatientsList) {
				WebPatient newWebPatient = new WebPatient();
				webModelConverter.patientToWeb(patient, newWebPatient);
				matchingWebPatientsList.add(newWebPatient);
			}
			model.addAttribute("matchingPatients", matchingWebPatientsList);
		}
	}

}
