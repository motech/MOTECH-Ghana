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
package org.openmrs.module.motechmodule.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.EncounterType;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.motechmodule.MotechService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MotechModuleFormController {
	
	protected final Log log = LogFactory.getLog(MotechModuleFormController.class);
	
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
	public String quickTest(@RequestParam("nurseName") String nurseName, @RequestParam("nursePhone") String nursePhone,
	                        @RequestParam("clinicName") String clinicName, @RequestParam("serialId") String serialId,
	                        @RequestParam("name") String name, @RequestParam("community") String community,
	                        @RequestParam("location") String location, @RequestParam("nhis") String nhis,
	                        @RequestParam("patientPhone") String patientPhone,
	                        @RequestParam("patientPhoneType") String patientPhoneType,
	                        @RequestParam("language") String language,
	                        @RequestParam("notificationType") String notificationType,
	                        @RequestParam("dateOfBirth") String dateOfBirth, @RequestParam("dueDate") String dueDate,
	                        @RequestParam("parity") String parity, @RequestParam("hemoglobin") String hemoglobin) {
		log.debug("Quick Test");
		return "redirect:/module/motechmodule/viewdata.form";
	}
	
	@RequestMapping(value = "/module/motechmodule/clinic", method = RequestMethod.POST)
	public String registerClinic(@RequestParam("name") String name) {
		log.debug("Register Clinic");
		return "redirect:/module/motechmodule/viewdata.form";
	}
	
	@RequestMapping(value = "/module/motechmodule/nurse", method = RequestMethod.POST)
	public String registerNurse(@RequestParam("name") String name, @RequestParam("nursePhone") String nursePhone,
	                            @RequestParam("clinic") String clinic) {
		log.debug("Register Nurse");
		return "redirect:/module/motechmodule/viewdata.form";
	}
	
	@RequestMapping(value = "/module/motechmodule/patient", method = RequestMethod.POST)
	public String registerPatient(@RequestParam("nursePhone") String nursePhone, @RequestParam("serialId") String serialId,
	                              @RequestParam("name") String name, @RequestParam("community") String community,
	                              @RequestParam("location") String location, @RequestParam("nhis") String nhis,
	                              @RequestParam("patientPhone") String patientPhone,
	                              @RequestParam("patientPhoneType") String patientPhoneType,
	                              @RequestParam("dateOfBirth") String dateOfBirth, @RequestParam("gender") String gender,
	                              @RequestParam("language") String language,
	                              @RequestParam("notificationType") String notificationType) {
		log.debug("Register Patient");
		return "redirect:/module/motechmodule/viewdata.form";
	}
	
	@RequestMapping(value = "/module/motechmodule/pregnancy", method = RequestMethod.POST)
	public String registerPregnancy(@RequestParam("name") String name, @RequestParam("nursePhone") String nursePhone,
	                                @RequestParam("regDate") String regDate, @RequestParam("serialId") String serialId,
	                                @RequestParam("dueDate") String dueDate, @RequestParam("parity") String parity,
	                                @RequestParam("hemoglobin") String hemoglobin) {
		log.debug("Register Pregnancy");
		return "redirect:/module/motechmodule/viewdata.form";
	}
	
	@RequestMapping(value = "/module/motechmodule/maternalVisit", method = RequestMethod.POST)
	public String recordMaternalVisit(@RequestParam("nursePhone") String nursePhone,
	                                  @RequestParam("visitDate") String visitDate,
	                                  @RequestParam("serialId") String serialId, @RequestParam("tetanus") String tetanus,
	                                  @RequestParam("ipt") String ipt, @RequestParam("itn") String itn,
	                                  @RequestParam("visitNumber") String visitNumber, @RequestParam("onARV") String onARV,
	                                  @RequestParam("prePMTCT") String prePMTCT,
	                                  @RequestParam("testPMTCT") String testPMTCT,
	                                  @RequestParam("postPMTCT") String postPMTCT,
	                                  @RequestParam("hemoglobin") String hemoglobin) {
		log.debug("Register Maternal Visit");
		return "redirect:/module/motechmodule/viewdata.form";
	}
	
	@RequestMapping("/module/motechmodule/viewdata")
	public String viewData(ModelMap model) {
		
		model.addAttribute("allClinics", Context.getLocationService().getAllLocations());
		model.addAttribute("allNurses", Context.getUserService().getAllUsers());
		
		List<PatientIdentifierType> ghanaPatientIdType = new ArrayList<PatientIdentifierType>();
		ghanaPatientIdType.add(Context.getPatientService().getPatientIdentifierTypeByName("Ghana Clinic Id"));
		model.addAttribute("allPatients", Context.getPatientService().getPatients(null, null, ghanaPatientIdType, false));
		
		List<EncounterType> maternalVisitType = new ArrayList<EncounterType>();
		maternalVisitType.add(Context.getEncounterService().getEncounterType("MATERNALVISIT"));
		model.addAttribute("allMaternalVisits", Context.getEncounterService().getEncounters(null, null, null, null, null,
		    maternalVisitType, null, false));
		
		List<EncounterType> pregnancyType = new ArrayList<EncounterType>();
		pregnancyType.add(Context.getEncounterService().getEncounterType("PREGNANCYVISIT"));
		model.addAttribute("allPregnancies", Context.getEncounterService().getEncounters(null, null, null, null, null,
		    pregnancyType, null, false));
		
		model.addAttribute("allFutureServiceDeliveries", Context.getService(MotechService.class)
		        .getAllFutureServiceDeliveries());
		model.addAttribute("allLogs", Context.getService(MotechService.class).getAllLogs());
		
		return "/module/motechmodule/viewdata";
	}
	
}
