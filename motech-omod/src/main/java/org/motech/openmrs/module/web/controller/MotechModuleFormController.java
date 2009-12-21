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
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motech.model.Blackout;
import org.motech.model.TroubledPhone;
import org.motech.openmrs.module.ContextService;
import org.motech.openmrs.module.MotechService;
import org.motech.openmrs.module.xml.LocationXStream;
import org.motech.svc.RegistrarBean;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.DeliveryTime;
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

	@Autowired
	public void setContextService(ContextService contextService) {
		this.contextService = contextService;
	}

	public void setRegistrarBean(RegistrarBean registrarBean) {
		this.registrarBean = registrarBean;
	}

	@RequestMapping(value = "/module/motechmodule/quick", method = RequestMethod.GET)
	public String viewQuickTestForm() {
		return "/module/motechmodule/quick";
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

	@RequestMapping(value = "/module/motechmodule/patient", method = RequestMethod.GET)
	public String viewPatientForm(ModelMap model) {
		model.addAttribute("nurses", registrarBean.getAllNurses());
		return "/module/motechmodule/patient";
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

	@RequestMapping(value = "/module/motechmodule/quick", method = RequestMethod.POST)
	public String quickTest(
			@RequestParam("nurseName") String nurseName,
			@RequestParam("nurseId") String nurseId,
			@RequestParam("nursePhone") String nursePhone,
			@RequestParam("clinicName") String clinicName,
			@RequestParam("serialId") String serialId,
			@RequestParam("name") String name,
			@RequestParam("community") String community,
			@RequestParam("location") String location,
			@RequestParam("nhis") String nhis,
			@RequestParam("patientPhone") String patientPhone,
			@RequestParam("patientPhoneType") String patientPhoneType,
			@RequestParam("language") String language,
			@RequestParam("mediaType") String mediaType,
			@RequestParam("deliveryTime") String deliveryTime,
			@RequestParam(value = "messagePrograms", required = false) String[] messagePrograms,
			@RequestParam("dateOfBirth") String dateOfBirth,
			@RequestParam("dueDate") String dueDate,
			@RequestParam("parity") String parity,
			@RequestParam("hemoglobin") String hemoglobin)
			throws NumberFormatException, ParseException {
		log.debug("Quick Test");
		registrarBean.registerClinic(clinicName, null);

		registrarBean.registerNurse(nurseName, nurseId, nursePhone, clinicName);

		registrarBean.registerPatient(nursePhone, serialId, name, community,
				location, dateFormat.parse(dateOfBirth), Gender.FEMALE, Integer
						.valueOf(nhis), patientPhone, ContactNumberType
						.valueOf(patientPhoneType), language, MediaType
						.valueOf(mediaType),
				DeliveryTime.valueOf(deliveryTime),
				convertToActualMessagePrograms(messagePrograms));
		registrarBean.registerPregnancy(nursePhone, new Date(), serialId,
				dateFormat.parse(dueDate), Integer.valueOf(parity), Double
						.valueOf(hemoglobin));

		registrarBean.recordMaternalVisit(nursePhone, new Date(), serialId,
				true, true, true, 1, true, true, true, true, 10.6);
		return "redirect:/module/motechmodule/viewdata.form";
	}

	private String[] convertToActualMessagePrograms(String[] messagePrograms) {
		String[] messageProgramsActual = new String[] {};
		if (messagePrograms != null) {
			Set<String> messageProgramsInputSet = new HashSet<String>(Arrays
					.asList(messagePrograms));
			Set<String> messageProgramsActualSet = new HashSet<String>();
			if (messageProgramsInputSet.contains("minuteTetanus")) {
				messageProgramsActualSet
						.add("Tetanus Information Message Program");
				messageProgramsActualSet
						.add("Tetanus Immunization Message Program");
			}
			if (messageProgramsInputSet.contains("weeklyPregnancy")) {
				messageProgramsActualSet
						.add("Weekly Pregnancy Message Program");
			}
			messageProgramsActual = messageProgramsActualSet
					.toArray(new String[messageProgramsActualSet.size()]);
		}
		return messageProgramsActual;
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

	@RequestMapping(value = "/module/motechmodule/patient", method = RequestMethod.POST)
	public String registerPatient(
			@RequestParam("nurse") Integer nurseId,
			@RequestParam("serialId") String serialId,
			@RequestParam("name") String name,
			@RequestParam("community") String community,
			@RequestParam("location") String location,
			@RequestParam("nhis") String nhis,
			@RequestParam("patientPhone") String patientPhone,
			@RequestParam("patientPhoneType") String patientPhoneType,
			@RequestParam("dateOfBirth") String dateOfBirth,
			@RequestParam("gender") String gender,
			@RequestParam("language") String language,
			@RequestParam("mediaType") String mediaType,
			@RequestParam("deliveryTime") String deliveryTime,
			@RequestParam(value = "messagePrograms", required = false) String[] messagePrograms)
			throws NumberFormatException, ParseException {
		log.debug("Register Patient");

		registrarBean.registerPatient(nurseId, serialId, name, community,
				location, dateFormat.parse(dateOfBirth),
				Gender.valueOf(gender), Integer.valueOf(nhis), patientPhone,
				ContactNumberType.valueOf(patientPhoneType), language,
				MediaType.valueOf(mediaType), DeliveryTime
						.valueOf(deliveryTime),
				convertToActualMessagePrograms(messagePrograms));
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
