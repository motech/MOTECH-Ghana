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
import java.util.ArrayList;
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
import org.motech.util.MotechConstants;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.DeliveryTime;
import org.motechproject.ws.Gender;
import org.motechproject.ws.MediaType;
import org.motechproject.ws.server.RegistrarService;
import org.openmrs.EncounterType;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
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
	@Qualifier("registrarClient")
	private RegistrarService registrarClient;

	private ContextService contextService;

	@Autowired
	public void setContextService(ContextService contextService) {
		this.contextService = contextService;
	}

	private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

	public void setRegistrarClient(RegistrarService registrarClient) {
		this.registrarClient = registrarClient;
	}

	public RegistrarService getRegistrarClient() {
		return registrarClient;
	}

	@RequestMapping(value = "/module/motechmodule/quick", method = RequestMethod.GET)
	public String viewQuickTestForm() {
		return "/module/motechmodule/quick";
	}

	@RequestMapping(value = "/module/motechmodule/clinic", method = RequestMethod.GET)
	public String viewClinicForm() {
		return "/module/motechmodule/clinic";
	}

	@RequestMapping(value = "/module/motechmodule/nurse", method = RequestMethod.GET)
	public String viewNurseForm() {
		return "/module/motechmodule/nurse";
	}

	@RequestMapping(value = "/module/motechmodule/patient", method = RequestMethod.GET)
	public String viewPatientForm() {
		return "/module/motechmodule/patient";
	}

	@RequestMapping(value = "/module/motechmodule/pregnancy", method = RequestMethod.GET)
	public String viewPregnancyForm() {
		return "/module/motechmodule/pregnancy";
	}

	@RequestMapping(value = "/module/motechmodule/maternalVisit", method = RequestMethod.GET)
	public String viewMaternalVisitForm() {
		return "/module/motechmodule/maternalVisit";
	}

	@RequestMapping(value = "/module/motechmodule/quick", method = RequestMethod.POST)
	public String quickTest(
			@RequestParam("nurseName") String nurseName,
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
			@RequestParam(value = "regimen", required = false) String[] regimen,
			@RequestParam("dateOfBirth") String dateOfBirth,
			@RequestParam("dueDate") String dueDate,
			@RequestParam("parity") String parity,
			@RequestParam("hemoglobin") String hemoglobin)
			throws NumberFormatException, ParseException {
		log.debug("Quick Test");
		registrarClient.registerClinic(clinicName);

		registrarClient.registerNurse(nurseName, nursePhone, clinicName);

		registrarClient.registerPatient(nursePhone, serialId, name, community,
				location, dateFormat.parse(dateOfBirth), Gender.FEMALE, Integer
						.valueOf(nhis), patientPhone, ContactNumberType
						.valueOf(patientPhoneType), language, MediaType
						.valueOf(mediaType),
				DeliveryTime.valueOf(deliveryTime),
				convertToActualRegimen(regimen));
		registrarClient.registerPregnancy(nursePhone, new Date(), serialId,
				dateFormat.parse(dueDate), Integer.valueOf(parity), Double
						.valueOf(hemoglobin));

		registrarClient.recordMaternalVisit(nursePhone, new Date(), serialId,
				true, true, true, 1, true, true, true, true, 10.6);
		return "redirect:/module/motechmodule/viewdata.form";
	}

	private String[] convertToActualRegimen(String[] regimen) {
		String[] regimenActual = new String[] {};
		if (regimen != null) {
			Set<String> regimenInputSet = new HashSet<String>(Arrays
					.asList(regimen));
			Set<String> regimenActualSet = new HashSet<String>();
			if (regimenInputSet.contains("minuteTetanus")) {
				regimenActualSet.add("Tetanus Information Regimen");
				regimenActualSet.add("Tetanus Immunization Regimen");
			}
			if (regimenInputSet.contains("dailyPregnancy")) {
				regimenActualSet.add("Daily Pregnancy Regimen");
			}
			regimenActual = regimenActualSet
					.toArray(new String[regimenActualSet.size()]);
		}
		return regimenActual;
	}

	@RequestMapping(value = "/module/motechmodule/clinic", method = RequestMethod.POST)
	public String registerClinic(@RequestParam("name") String name) {
		log.debug("Register Clinic");
		registrarClient.registerClinic(name);
		return "redirect:/module/motechmodule/viewdata.form";
	}

	@RequestMapping(value = "/module/motechmodule/nurse", method = RequestMethod.POST)
	public String registerNurse(@RequestParam("name") String name,
			@RequestParam("nursePhone") String nursePhone,
			@RequestParam("clinic") String clinic) {
		log.debug("Register Nurse");
		registrarClient.registerNurse(name, nursePhone, clinic);
		return "redirect:/module/motechmodule/viewdata.form";
	}

	@RequestMapping(value = "/module/motechmodule/patient", method = RequestMethod.POST)
	public String registerPatient(
			@RequestParam("nursePhone") String nursePhone,
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
			@RequestParam(value = "regimen", required = false) String[] regimen)
			throws NumberFormatException, ParseException {
		log.debug("Register Patient");

		registrarClient
				.registerPatient(nursePhone, serialId, name, community,
						location, dateFormat.parse(dateOfBirth), Gender
								.valueOf(gender), Integer.valueOf(nhis),
						patientPhone, ContactNumberType
								.valueOf(patientPhoneType), language, MediaType
								.valueOf(mediaType), DeliveryTime
								.valueOf(deliveryTime),
						convertToActualRegimen(regimen));
		return "redirect:/module/motechmodule/viewdata.form";
	}

	@RequestMapping(value = "/module/motechmodule/pregnancy", method = RequestMethod.POST)
	public String registerPregnancy(
			@RequestParam("nursePhone") String nursePhone,
			@RequestParam("regDate") String regDate,
			@RequestParam("serialId") String serialId,
			@RequestParam("dueDate") String dueDate,
			@RequestParam("parity") String parity,
			@RequestParam("hemoglobin") String hemoglobin)
			throws NumberFormatException, ParseException {
		log.debug("Register Pregnancy");
		registrarClient.registerPregnancy(nursePhone,
				(!regDate.equals("") ? dateFormat.parse(regDate) : null),
				serialId, dateFormat.parse(dueDate), Integer.valueOf(parity),
				Double.valueOf(hemoglobin));
		return "redirect:/module/motechmodule/viewdata.form";
	}

	@RequestMapping(value = "/module/motechmodule/maternalVisit", method = RequestMethod.POST)
	public String recordMaternalVisit(
			@RequestParam("nursePhone") String nursePhone,
			@RequestParam("visitDate") String visitDate,
			@RequestParam("serialId") String serialId,
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
		registrarClient.recordMaternalVisit(nursePhone,
				(!visitDate.equals("") ? dateFormat.parse(visitDate) : null),
				serialId, Boolean.valueOf(tetanus), Boolean.valueOf(ipt),
				Boolean.valueOf(itn), Integer.valueOf(visitNumber), Boolean
						.valueOf(onARV), Boolean.valueOf(prePMTCT), Boolean
						.valueOf(testPMTCT), Boolean.valueOf(postPMTCT), Double
						.valueOf(hemoglobin));
		return "redirect:/module/motechmodule/viewdata.form";
	}

	@RequestMapping("/module/motechmodule/viewdata")
	public String viewData(ModelMap model) {

		model.addAttribute("allClinics", Context.getLocationService()
				.getAllLocations());
		model.addAttribute("allNurses", Context.getUserService().getAllUsers());

		List<PatientIdentifierType> ghanaPatientIdType = new ArrayList<PatientIdentifierType>();
		ghanaPatientIdType.add(Context.getPatientService()
				.getPatientIdentifierTypeByName(
						MotechConstants.PATIENT_IDENTIFIER_GHANA_CLINIC_ID));
		model.addAttribute("allPatients", Context.getPatientService()
				.getPatients(null, null, ghanaPatientIdType, false));

		List<EncounterType> maternalVisitType = new ArrayList<EncounterType>();
		maternalVisitType.add(Context.getEncounterService().getEncounterType(
				"MATERNALVISIT"));
		model.addAttribute("allMaternalVisits", Context.getEncounterService()
				.getEncounters(null, null, null, null, null, maternalVisitType,
						null, false));

		List<EncounterType> pregnancyType = new ArrayList<EncounterType>();
		pregnancyType.add(Context.getEncounterService().getEncounterType(
				"PREGNANCYVISIT"));
		model.addAttribute("allPregnancies", Context.getEncounterService()
				.getEncounters(null, null, null, null, null, pregnancyType,
						null, false));

		model.addAttribute("allScheduledMessages", Context.getService(
				MotechService.class).getAllScheduledMessages());
		model.addAttribute("allLogs", Context.getService(MotechService.class)
				.getAllLogs());

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
