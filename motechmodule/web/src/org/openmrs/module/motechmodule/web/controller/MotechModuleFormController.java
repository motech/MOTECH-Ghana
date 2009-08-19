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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motech.model.Gender;
import org.openmrs.EncounterType;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.motechmodule.MotechService;
import org.openmrs.module.motechmodule.web.ws.RegistrarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MotechModuleFormController {
	
	protected final Log log = LogFactory.getLog(MotechModuleFormController.class);
	
	@Autowired
	@Qualifier("registrarClient")
	private RegistrarService registrarClient;
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	
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
	                        @RequestParam("parity") String parity, @RequestParam("hemoglobin") String hemoglobin)
	                                                                                                             throws NumberFormatException,
	                                                                                                             ParseException {
		log.debug("Quick Test");
		registrarClient.registerClinic(clinicName);
		
		registrarClient.registerNurse(nurseName, nursePhone, clinicName);
		
		registrarClient.registerPatient(nursePhone, serialId, name, community, location, dateFormat.parse(dateOfBirth),
		    Gender.female, Integer.valueOf(nhis), patientPhone);
		registrarClient.registerPregnancy(nursePhone, new Date(), serialId, dateFormat.parse(dueDate), Integer
		        .valueOf(parity), Double.valueOf(hemoglobin));
		
		registrarClient.recordMaternalVisit(nursePhone, new Date(), serialId, true, true, true, 1, true, true, true, true,
		    10.6);
		return "redirect:/module/motechmodule/viewdata.form";
	}
	
	@RequestMapping(value = "/module/motechmodule/clinic", method = RequestMethod.POST)
	public String registerClinic(@RequestParam("name") String name) {
		log.debug("Register Clinic");
		registrarClient.registerClinic(name);
		return "redirect:/module/motechmodule/viewdata.form";
	}
	
	@RequestMapping(value = "/module/motechmodule/nurse", method = RequestMethod.POST)
	public String registerNurse(@RequestParam("name") String name, @RequestParam("nursePhone") String nursePhone,
	                            @RequestParam("clinic") String clinic) {
		log.debug("Register Nurse");
		registrarClient.registerNurse(name, nursePhone, clinic);
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
	                              @RequestParam("notificationType") String notificationType) throws NumberFormatException,
	                                                                                        ParseException {
		log.debug("Register Patient");
		registrarClient.registerPatient(nursePhone, serialId, name, community, location, dateFormat.parse(dateOfBirth),
		    Gender.valueOf(gender), Integer.valueOf(nhis), patientPhone);
		return "redirect:/module/motechmodule/viewdata.form";
	}
	
	@RequestMapping(value = "/module/motechmodule/pregnancy", method = RequestMethod.POST)
	public String registerPregnancy(@RequestParam("name") String name, @RequestParam("nursePhone") String nursePhone,
	                                @RequestParam("regDate") String regDate, @RequestParam("serialId") String serialId,
	                                @RequestParam("dueDate") String dueDate, @RequestParam("parity") String parity,
	                                @RequestParam("hemoglobin") String hemoglobin) throws NumberFormatException,
	                                                                              ParseException {
		log.debug("Register Pregnancy");
		registrarClient.registerPregnancy(nursePhone, dateFormat.parse(regDate), serialId, dateFormat.parse(dueDate),
		    Integer.valueOf(parity), Double.valueOf(hemoglobin));
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
	                                  @RequestParam("hemoglobin") String hemoglobin) throws NumberFormatException,
	                                                                                ParseException {
		log.debug("Register Maternal Visit");
		registrarClient.recordMaternalVisit(nursePhone, dateFormat.parse(visitDate), serialId, Boolean.valueOf(tetanus),
		    Boolean.valueOf(ipt), Boolean.valueOf(itn), Integer.valueOf(visitNumber), Boolean.valueOf(onARV), Boolean
		            .valueOf(prePMTCT), Boolean.valueOf(testPMTCT), Boolean.valueOf(postPMTCT), Double.valueOf(hemoglobin));
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
		    maternalVisitType, false));
		
		List<EncounterType> pregnancyType = new ArrayList<EncounterType>();
		pregnancyType.add(Context.getEncounterService().getEncounterType("PREGNANCYVISIT"));
		model.addAttribute("allPregnancies", Context.getEncounterService().getEncounters(null, null, null, null, null,
		    pregnancyType, false));
		
		model.addAttribute("allFutureServiceDeliveries", Context.getService(MotechService.class)
		        .getAllFutureServiceDeliveries());
		model.addAttribute("allLogs", Context.getService(MotechService.class).getAllLogs());
		
		return "/module/motechmodule/viewdata";
	}
	
}
