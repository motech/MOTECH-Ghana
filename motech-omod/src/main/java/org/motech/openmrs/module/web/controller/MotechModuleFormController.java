/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.motech.openmrs.module.web.controller;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motech.model.Blackout;
import org.motech.model.HIVStatus;
import org.motech.model.TroubledPhone;
import org.motech.model.WhoRegistered;
import org.motech.openmrs.module.ContextService;
import org.motech.openmrs.module.MotechService;
import org.motech.openmrs.module.xml.LocationXStream;
import org.motech.svc.RegistrarBean;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.Gender;
import org.motechproject.ws.MediaType;
import org.openmrs.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * A spring webmvc controller, defining operations for the administrative
 * actions for the OpenMRS admin links.
 * 
 * @see org.motech.openmrs.module.extension.html.AdminList
 */
@Controller
public class MotechModuleFormController {

	protected final Log log = LogFactory
			.getLog(MotechModuleFormController.class);

	@Autowired
	@Qualifier("registrarBean")
	private RegistrarBean registrarBean;

	private ContextService contextService;

	private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	private SimpleDateFormat intDateFormat = new SimpleDateFormat("dd/MM/yyyy");

	@Autowired
	public void setContextService(ContextService contextService) {
		this.contextService = contextService;
	}

	public void setRegistrarBean(RegistrarBean registrarBean) {
		this.registrarBean = registrarBean;
	}

	@RequestMapping(value = "/module/motechmodule/clinic", method = RequestMethod.GET)
	public String viewClinicForm(ModelMap model) {
		List<Location> locations = registrarBean.getAllClinics();

		LocationXStream xstream = new LocationXStream();
		String locationsXml = xstream.toLocationHierarchyXML(locations);

		model.addAttribute("locationsXml", locationsXml);
		model.addAttribute("locations", locations);

		return "/module/motechmodule/clinic";
	}

	@RequestMapping(value = "/module/motechmodule/nurse", method = RequestMethod.GET)
	public String viewNurseForm(ModelMap model) {
		model.addAttribute("clinics", registrarBean.getAllClinics());
		return "/module/motechmodule/nurse";
	}

	@RequestMapping(value = "/module/motechmodule/child", method = RequestMethod.GET)
	public String viewChildForm(ModelMap model) {
		model.addAttribute("patients", registrarBean.getAllPatients());
		model.addAttribute("regions", contextService.getMotechService()
				.getAllRegions());
		model.addAttribute("districts", contextService.getMotechService()
				.getAllDistricts());
		model.addAttribute("communities", contextService.getMotechService()
				.getAllCommunities());
		model.addAttribute("clinics", contextService.getMotechService()
				.getAllClinics());
		return "/module/motechmodule/child";
	}

	@RequestMapping(value = "/module/motechmodule/pregnancy", method = RequestMethod.GET)
	public String viewPregnancyForm(ModelMap model) {
		model.addAttribute("nurses", registrarBean.getAllNurses());
		model.addAttribute("patients", registrarBean.getAllPatients());
		return "/module/motechmodule/pregnancy";
	}

	@RequestMapping(value = "/module/motechmodule/maternalVisit", method = RequestMethod.GET)
	public String viewMaternalVisitForm(ModelMap model) {
		model.addAttribute("nurses", registrarBean.getAllNurses());
		model.addAttribute("patients", registrarBean.getAllPatients());
		return "/module/motechmodule/maternalVisit";
	}

	@RequestMapping(value = "/module/motechmodule/clinic", method = RequestMethod.POST)
	public String registerClinic(@RequestParam("name") String name,
			@RequestParam("parent") String parent) {
		log.debug("Register Clinic");
		Integer parentId = null;
		if (!parent.equals("")) {
			parentId = Integer.valueOf(parent);
		}
		registrarBean.registerClinic(name, parentId);
		return "redirect:/module/motechmodule/viewdata.form";
	}

	@RequestMapping(value = "/module/motechmodule/nurse", method = RequestMethod.POST)
	public String registerNurse(@RequestParam("name") String name,
			@RequestParam("nurseId") String nurseId,
			@RequestParam("nursePhone") String nursePhone,
			@RequestParam("clinic") Integer clinicId) {
		log.debug("Register Nurse");
		registrarBean.registerNurse(name, nurseId, nursePhone, clinicId);
		return "redirect:/module/motechmodule/viewdata.form";
	}

	@RequestMapping(value = "/module/motechmodule/child", method = RequestMethod.POST)
	public String registerChild(@RequestParam("firstName") String firstName,
			@RequestParam("lastName") String lastName,
			@RequestParam("prefName") String prefName,
			@RequestParam("birthDate") String birthDate,
			@RequestParam("birthDateEst") String birthDateEst,
			@RequestParam("sex") String sex,
			@RequestParam("mother") String mother,
			@RequestParam("registeredGHS") String registeredGHS,
			@RequestParam("regNumberGHS") String regNumberGHS,
			@RequestParam("insured") String insured,
			@RequestParam("nhis") String nhis,
			@RequestParam("nhisExpDate") String nhisExpDate,
			@RequestParam("region") String region,
			@RequestParam("district") String district,
			@RequestParam("community") String community,
			@RequestParam("address") String address,
			@RequestParam("clinic") String clinic,
			@RequestParam("registerPregProgram") String registerPregProgram,
			@RequestParam("primaryPhone") String primaryPhone,
			@RequestParam("primaryPhoneType") String primaryPhoneType,
			@RequestParam("secondaryPhone") String secondaryPhone,
			@RequestParam("secondaryPhoneType") String secondaryPhoneType,
			@RequestParam("mediaTypeInfo") String mediaTypeInfo,
			@RequestParam("mediaTypeReminder") String mediaTypeReminder,
			@RequestParam("languageVoice") String languageVoice,
			@RequestParam("languageText") String languageText,
			@RequestParam("whoRegistered") String whoRegistered)
			throws NumberFormatException, ParseException {
		log.debug("Register Child");

		Integer motherId = null;
		if (!mother.equals("")) {
			motherId = Integer.valueOf(mother);
		}
		registrarBean.registerChild(firstName, lastName, prefName,
				intDateFormat.parse(birthDate), Boolean.valueOf(birthDateEst),
				Gender.valueOf(sex), motherId, Boolean.valueOf(registeredGHS),
				regNumberGHS, Boolean.valueOf(insured), nhis, intDateFormat
						.parse(nhisExpDate), region, district, community,
				address, Integer.valueOf(clinic), Boolean
						.valueOf(registerPregProgram), primaryPhone,
				ContactNumberType.valueOf(primaryPhoneType), secondaryPhone,
				ContactNumberType.valueOf(secondaryPhoneType), MediaType
						.valueOf(mediaTypeInfo), MediaType
						.valueOf(mediaTypeReminder), languageVoice,
				languageText, WhoRegistered.valueOf(whoRegistered));
		return "redirect:/module/motechmodule/viewdata.form";
	}

	@RequestMapping(value = "/module/motechmodule/pregnancy", method = RequestMethod.POST)
	public String registerPregnancy(@RequestParam("nurse") Integer nurseId,
			@RequestParam("regDate") String regDate,
			@RequestParam("patient") Integer patientId,
			@RequestParam("dueDate") String dueDate,
			@RequestParam("parity") String parity,
			@RequestParam("hemoglobin") String hemoglobin)
			throws NumberFormatException, ParseException {
		log.debug("Register Pregnancy");
		registrarBean.registerPregnancy(nurseId,
				(!regDate.equals("") ? dateFormat.parse(regDate) : null),
				patientId, dateFormat.parse(dueDate), Integer.valueOf(parity),
				Double.valueOf(hemoglobin));
		return "redirect:/module/motechmodule/viewdata.form";
	}

	@RequestMapping(value = "/module/motechmodule/maternalVisit", method = RequestMethod.POST)
	public String recordMaternalVisit(@RequestParam("nurse") Integer nurseId,
			@RequestParam("visitDate") String visitDate,
			@RequestParam("patient") Integer patientId,
			@RequestParam("tetanus") String tetanus,
			@RequestParam("ipt") String ipt, @RequestParam("itn") String itn,
			@RequestParam("visitNumber") String visitNumber,
			@RequestParam("onARV") String onARV,
			@RequestParam("prePMTCT") String prePMTCT,
			@RequestParam("testPMTCT") String testPMTCT,
			@RequestParam("postPMTCT") String postPMTCT,
			@RequestParam("hemoglobin") String hemoglobin)
			throws NumberFormatException, ParseException {
		log.debug("Register Maternal Visit");
		registrarBean.recordMaternalVisit(nurseId,
				(!visitDate.equals("") ? dateFormat.parse(visitDate) : null),
				patientId, Boolean.valueOf(tetanus), Boolean.valueOf(ipt),
				Boolean.valueOf(itn), Integer.valueOf(visitNumber), Boolean
						.valueOf(onARV), Boolean.valueOf(prePMTCT), Boolean
						.valueOf(testPMTCT), Boolean.valueOf(postPMTCT), Double
						.valueOf(hemoglobin));
		return "redirect:/module/motechmodule/viewdata.form";
	}

	@RequestMapping("/module/motechmodule/viewdata")
	public String viewData(ModelMap model) {

		model.addAttribute("allClinics", registrarBean.getAllClinics());
		model.addAttribute("allNurses", registrarBean.getAllNurses());
		model.addAttribute("allPatients", registrarBean.getAllPatients());
		model.addAttribute("allMaternalVisits", registrarBean
				.getAllMaternalVisits());
		model.addAttribute("allPregnancies", registrarBean
				.getAllPregnancyVisits());
		model.addAttribute("allScheduledMessages", registrarBean
				.getAllScheduledMessages());
		model.addAttribute("allLogs", registrarBean.getAllLogs());

		return "/module/motechmodule/viewdata";
	}

	@RequestMapping("/module/motechmodule/blackout")
	public String viewBlackoutSettings(ModelMap model) {

		Blackout blackout = contextService.getMotechService()
				.getBlackoutSettings();

		if (blackout != null) {
			model.addAttribute("startTime", blackout.getStartTime());
			model.addAttribute("endTime", blackout.getEndTime());
		}

		return "/module/motechmodule/blackout";
	}

	@RequestMapping(value = "/module/motechmodule/blackout", method = RequestMethod.POST)
	public String saveBlackoutSettings(
			@RequestParam("startTime") String startTime,
			@RequestParam("endTime") String endTime, ModelMap model) {

		MotechService motechService = contextService.getMotechService();
		Blackout blackout = motechService.getBlackoutSettings();

		Time startTimeCvt = Time.valueOf(startTime);
		Time endTimeCvt = Time.valueOf(endTime);

		if (blackout != null) {
			blackout.setStartTime(startTimeCvt);
			blackout.setEndTime(endTimeCvt);
		} else {
			blackout = new Blackout(startTimeCvt, endTimeCvt);
		}

		motechService.setBlackoutSettings(blackout);

		model.addAttribute("startTime", blackout.getStartTime());
		model.addAttribute("endTime", blackout.getEndTime());

		return "/module/motechmodule/blackout";
	}

	@RequestMapping(value = "/module/motechmodule/troubledphone", method = RequestMethod.GET)
	public String handleTroubledPhone(
			@RequestParam(required = false, value = "phoneNumber") String phoneNumber,
			@RequestParam(required = false, value = "remove") Boolean remove,
			ModelMap model) {

		if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {

			MotechService motechService = contextService.getMotechService();

			TroubledPhone troubledPhone = motechService
					.getTroubledPhone(phoneNumber);

			if (remove == Boolean.TRUE)
				motechService.removeTroubledPhone(phoneNumber);
			else if (troubledPhone != null)
				model.addAttribute(troubledPhone);
		}

		return "/module/motechmodule/troubledphone";
	}
}
