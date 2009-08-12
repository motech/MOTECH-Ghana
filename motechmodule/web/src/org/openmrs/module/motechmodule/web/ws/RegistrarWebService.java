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
package org.openmrs.module.motechmodule.web.ws;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import javax.xml.ws.WebServiceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motech.model.Gender;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.motechmodule.MotechService;
import org.openmrs.web.ws.WebServiceSupport;

/**
 * This can be accessed via /openmrs/ws/registrarservice since we mapped it to /ws/registrarservice
 * in the metadata/moduleApplicationContext.xml file.
 */
@WebService
@SOAPBinding(style = Style.RPC, use = Use.LITERAL)
public class RegistrarWebService {
	
	Log log = LogFactory.getLog(RegistrarWebService.class);
	
	@Resource
	WebServiceContext webServiceContext;
	
	//TODO: Add OpenMRS API Exceptions as WebFaults ?
	
	@WebMethod
	public void registerClinic(@WebParam(name = "name") String name) {
		
		WebServiceSupport.authenticate(webServiceContext);
		
		Location clinic = new Location();
		clinic.setName(name);
		clinic.setDescription("A Ghana Clinic Location");
		
		Context.getLocationService().saveLocation(clinic);
	}
	
	@WebMethod
	public void registerNurse(@WebParam(name = "name") String name, @WebParam(name = "phoneNumber") String phoneNumber,
	                          @WebParam(name = "clinic") String clinic) {
		
		WebServiceSupport.authenticate(webServiceContext);
		
		// TODO: Create nurses as person and use same User for all actions ?
		User nurse = new User();
		nurse.setUsername(name);
		
		// TODO: Nurse gender hardcoded, required for Person
		nurse.setGender(Gender.female.toOpenMRSString());
		
		PersonName personName = new PersonName();
		personName.setGivenName(name);
		// Family name appears required in UI
		personName.setFamilyName(name);
		nurse.addName(personName);
		
		// Must be created previously through API or UI to lookup
		PersonAttributeType phoneNumberAttrType = Context.getPersonService().getPersonAttributeTypeByName("Phone Number");
		nurse.addAttribute(new PersonAttribute(phoneNumberAttrType, phoneNumber));
		
		// TODO: Create Nurse role with proper privileges
		Role role = Context.getUserService().getRole("Provider");
		nurse.addRole(role);
		
		// TODO: Clinic not used, no connection currently between Nurse and Clinic
		Location clinicLocation = Context.getLocationService().getLocation(clinic);
		PersonAttributeType clinicType = Context.getPersonService().getPersonAttributeTypeByName("Health Center");
		nurse.addAttribute(new PersonAttribute(clinicType, clinicLocation.getLocationId().toString()));
		
		Context.getUserService().saveUser(nurse, "password");
	}
	
	@WebMethod
	public void registerPatient(@WebParam(name = "nursePhoneNumber") String nursePhoneNumber,
	                            @WebParam(name = "serialId") String serialId, @WebParam(name = "name") String name,
	                            @WebParam(name = "community") String community,
	                            @WebParam(name = "location") String location,
	                            @WebParam(name = "dateOfBirth") Date dateOfBirth, @WebParam(name = "gender") Gender gender,
	                            @WebParam(name = "nhis") Integer nhis, @WebParam(name = "phoneNumber") String phoneNumber) {
		
		WebServiceSupport.authenticate(webServiceContext);
		
		Patient patient = new Patient();
		
		// Must be created previously through API or UI to lookup
		PatientIdentifierType serialIdType = Context.getPatientService().getPatientIdentifierTypeByName("Ghana Clinic Id");
		
		User nurse = Context.getService(MotechService.class).getUserByPhoneNumber(nursePhoneNumber);
		
		PersonAttribute clinic = nurse
		        .getAttribute(Context.getPersonService().getPersonAttributeTypeByName("Health Center"));
		Integer clinicId = Integer.valueOf(clinic.getValue());
		Location clinicLocation = Context.getLocationService().getLocation(clinicId);
		patient.addIdentifier(new PatientIdentifier(serialId, serialIdType, clinicLocation));
		
		PersonName personName = new PersonName();
		personName.setGivenName(name);
		// Family name appears required, PersonName parsePersonName(name)
		personName.setFamilyName(name);
		patient.addName(personName);
		
		PersonAddress address = new PersonAddress();
		address.setAddress1(location);
		address.setCityVillage(community);
		patient.addAddress(address);
		
		patient.setBirthdate(dateOfBirth);
		
		// Should be "M" or "F"
		patient.setGender(gender.toOpenMRSString());
		
		// Must be created previously through API or UI to lookup
		PersonAttributeType nhisAttrType = Context.getPersonService().getPersonAttributeTypeByName("NHIS Number");
		patient.addAttribute(new PersonAttribute(nhisAttrType, nhis.toString()));
		
		// Must be created previously through API or UI to lookup
		PersonAttributeType phoneNumberAttrType = Context.getPersonService().getPersonAttributeTypeByName("Phone Number");
		patient.addAttribute(new PersonAttribute(phoneNumberAttrType, phoneNumber));
		
		Context.getPatientService().savePatient(patient);
	}
	
	@WebMethod
	public void recordMaternalVisit(@WebParam(name = "nursePhoneNumber") String nursePhoneNumber,
	                                @WebParam(name = "date") Date date, @WebParam(name = "serialId") String serialId,
	                                @WebParam(name = "tetanus") Boolean tetanus, @WebParam(name = "ipt") Boolean ipt,
	                                @WebParam(name = "itn") Boolean itn,
	                                @WebParam(name = "visitNumber") Integer visitNumber,
	                                @WebParam(name = "onARV") Boolean onARV, @WebParam(name = "prePMTCT") Boolean prePMTCT,
	                                @WebParam(name = "testPMTCT") Boolean testPMTCT,
	                                @WebParam(name = "postPMTCT") Boolean postPMTCT,
	                                @WebParam(name = "hemoglobinAt36Weeks") Double hemoglobinAt36Weeks) {
		
		WebServiceSupport.authenticate(webServiceContext);
		
		PatientIdentifierType serialIdType = Context.getPatientService().getPatientIdentifierTypeByName("Ghana Clinic Id");
		List<PatientIdentifierType> idTypes = new ArrayList<PatientIdentifierType>();
		idTypes.add(serialIdType);
		
		// Parameters are Name, Id, Id type, match exactly boolean
		List<Patient> patients = Context.getPatientService().getPatients(null, serialId, idTypes, true);
		Patient patient = patients.get(0);
		
		Encounter encounter = new Encounter();
		encounter.setEncounterDatetime(date);
		encounter.setPatient(patient);
		
		User nurse = Context.getService(MotechService.class).getUserByPhoneNumber(nursePhoneNumber);
		encounter.setProvider(nurse);
		
		PersonAttribute clinic = nurse
		        .getAttribute(Context.getPersonService().getPersonAttributeTypeByName("Health Center"));
		Integer clinicId = Integer.valueOf(clinic.getValue());
		Location clinicLocation = Context.getLocationService().getLocation(clinicId);
		
		// Encounter types must be created previously
		EncounterType encounterType = Context.getEncounterService().getEncounterType("MATERNALVISIT");
		encounter.setEncounterType(encounterType);
		encounter.setLocation(clinicLocation);
		
		if (tetanus) {
			Obs tetanusObs = new Obs();
			tetanusObs.setObsDatetime(date);
			tetanusObs.setConcept(Context.getConceptService().getConcept("IMMUNIZATIONS ORDERED"));
			tetanusObs.setPerson(patient);
			tetanusObs.setLocation(clinicLocation);
			tetanusObs.setEncounter(encounter);
			tetanusObs.setValueCoded(Context.getConceptService().getConcept("TETANUS BOOSTER"));
			encounter.addObs(tetanusObs);
		}
		
		// TODO: Add IPT to proper Concept as an Answer, not an immunization
		if (ipt) {
			Obs iptObs = new Obs();
			iptObs.setObsDatetime(date);
			iptObs.setConcept(Context.getConceptService().getConcept("IMMUNIZATIONS ORDERED"));
			iptObs.setPerson(patient);
			iptObs.setLocation(clinicLocation);
			iptObs.setEncounter(encounter);
			iptObs.setValueCoded(Context.getConceptService().getConcept("INTERMITTENT PREVENTATIVE TREATMENT"));
			encounter.addObs(iptObs);
		}
		
		if (itn) {
			Obs itnObs = new Obs();
			itnObs.setObsDatetime(date);
			itnObs.setConcept(Context.getConceptService().getConcept("INSECTICIDE-TREATED NET USAGE"));
			itnObs.setPerson(patient);
			itnObs.setLocation(clinicLocation);
			itnObs.setEncounter(encounter);
			itnObs.setValueNumeric(new Double(1)); // Boolean currently stored as Numeric 1 or 0
			encounter.addObs(itnObs);
		}
		
		Obs visitNumberObs = new Obs();
		visitNumberObs.setObsDatetime(date);
		visitNumberObs.setConcept(Context.getConceptService().getConcept("PREGNANCY VISIT NUMBER"));
		visitNumberObs.setPerson(patient);
		visitNumberObs.setLocation(clinicLocation);
		visitNumberObs.setEncounter(encounter);
		visitNumberObs.setValueNumeric(new Double(visitNumber));
		encounter.addObs(visitNumberObs);
		
		if (onARV) {
			Obs arvObs = new Obs();
			arvObs.setObsDatetime(date);
			arvObs.setConcept(Context.getConceptService().getConcept("ANTIRETROVIRAL USE DURING PREGNANCY"));
			arvObs.setPerson(patient);
			arvObs.setLocation(clinicLocation);
			arvObs.setEncounter(encounter);
			arvObs.setValueCoded(Context.getConceptService().getConcept("ON ANTIRETROVIRAL THERAPY"));
			encounter.addObs(arvObs);
		}
		
		if (prePMTCT) {
			Obs prePmtctObs = new Obs();
			prePmtctObs.setObsDatetime(date);
			prePmtctObs.setConcept(Context.getConceptService().getConcept("PRE PREVENTING MATERNAL TO CHILD TRANSMISSION"));
			prePmtctObs.setPerson(patient);
			prePmtctObs.setLocation(clinicLocation);
			prePmtctObs.setEncounter(encounter);
			prePmtctObs.setValueNumeric(new Double(1)); // Boolean currently stored as Numeric 1 or 0
			encounter.addObs(prePmtctObs);
		}
		
		if (testPMTCT) {
			Obs testPmtctObs = new Obs();
			testPmtctObs.setObsDatetime(date);
			testPmtctObs
			        .setConcept(Context.getConceptService().getConcept("TEST PREVENTING MATERNAL TO CHILD TRANSMISSION"));
			testPmtctObs.setPerson(patient);
			testPmtctObs.setLocation(clinicLocation);
			testPmtctObs.setEncounter(encounter);
			testPmtctObs.setValueNumeric(new Double(1)); // Boolean currently stored as Numeric 1 or 0
			encounter.addObs(testPmtctObs);
		}
		
		if (postPMTCT) {
			Obs postPmtctObs = new Obs();
			postPmtctObs.setObsDatetime(date);
			postPmtctObs
			        .setConcept(Context.getConceptService().getConcept("POST PREVENTING MATERNAL TO CHILD TRANSMISSION"));
			postPmtctObs.setPerson(patient);
			postPmtctObs.setLocation(clinicLocation);
			postPmtctObs.setEncounter(encounter);
			postPmtctObs.setValueNumeric(new Double(1)); // Boolean currently stored as Numeric 1 or 0
			encounter.addObs(postPmtctObs);
		}
		
		Obs hemoglobinObs = new Obs();
		hemoglobinObs.setObsDatetime(date);
		hemoglobinObs.setConcept(Context.getConceptService().getConcept("HEMOGLOBIN AT 36 WEEKS"));
		hemoglobinObs.setPerson(patient);
		hemoglobinObs.setLocation(clinicLocation);
		hemoglobinObs.setEncounter(encounter);
		hemoglobinObs.setValueNumeric(hemoglobinAt36Weeks);
		encounter.addObs(hemoglobinObs);
		
		Context.getEncounterService().saveEncounter(encounter);
	}
	
	@WebMethod
	public void registerPregnancy(@WebParam(name = "nursePhoneNumber") String nursePhoneNumber,
	                              @WebParam(name = "date") Date date, @WebParam(name = "serialId") String serialId,
	                              @WebParam(name = "dueDate") Date dueDate, @WebParam(name = "parity") Integer parity,
	                              @WebParam(name = "hemoglobin") Double hemoglobin) {
		
		WebServiceSupport.authenticate(webServiceContext);
		
		PatientIdentifierType serialIdType = Context.getPatientService().getPatientIdentifierTypeByName("Ghana Clinic Id");
		List<PatientIdentifierType> idTypes = new ArrayList<PatientIdentifierType>();
		idTypes.add(serialIdType);
		
		// Parameters are Name, Id, Id type, match exactly boolean
		List<Patient> patients = Context.getPatientService().getPatients(null, serialId, idTypes, true);
		Patient patient = patients.get(0);
		
		Encounter encounter = new Encounter();
		encounter.setEncounterDatetime(date);
		encounter.setPatient(patient);
		
		User nurse = Context.getService(MotechService.class).getUserByPhoneNumber(nursePhoneNumber);
		encounter.setProvider(nurse);
		
		PersonAttribute clinic = nurse
		        .getAttribute(Context.getPersonService().getPersonAttributeTypeByName("Health Center"));
		Integer clinicId = Integer.valueOf(clinic.getValue());
		Location clinicLocation = Context.getLocationService().getLocation(clinicId);
		
		// Encounter types must be created previously
		EncounterType encounterType = Context.getEncounterService().getEncounterType("PREGNANCYVISIT");
		encounter.setEncounterType(encounterType);
		encounter.setLocation(clinicLocation);
		
		Obs pregSatusObs = new Obs();
		pregSatusObs.setObsDatetime(date);
		pregSatusObs.setConcept(Context.getConceptService().getConcept("PREGNANCY STATUS"));
		pregSatusObs.setPerson(patient);
		pregSatusObs.setLocation(clinicLocation);
		pregSatusObs.setEncounter(encounter);
		pregSatusObs.setValueNumeric(new Double(1)); // Boolean currently stored as Numeric 1 or 0
		encounter.addObs(pregSatusObs);
		
		Obs dueDateObs = new Obs();
		dueDateObs.setObsDatetime(date);
		dueDateObs.setConcept(Context.getConceptService().getConcept("ESTIMATED DATE OF CONFINEMENT"));
		dueDateObs.setPerson(patient);
		dueDateObs.setLocation(clinicLocation);
		dueDateObs.setEncounter(encounter);
		dueDateObs.setValueDatetime(dueDate);
		encounter.addObs(dueDateObs);
		
		Obs parityObs = new Obs();
		parityObs.setObsDatetime(date);
		parityObs.setConcept(Context.getConceptService().getConcept("GRAVIDA"));
		parityObs.setPerson(patient);
		parityObs.setLocation(clinicLocation);
		parityObs.setEncounter(encounter);
		parityObs.setValueNumeric(new Double(parity));
		encounter.addObs(parityObs);
		
		Obs hemoglobinObs = new Obs();
		hemoglobinObs.setObsDatetime(date);
		hemoglobinObs.setConcept(Context.getConceptService().getConcept("HEMOGLOBIN"));
		hemoglobinObs.setPerson(patient);
		hemoglobinObs.setLocation(clinicLocation);
		hemoglobinObs.setEncounter(encounter);
		hemoglobinObs.setValueNumeric(hemoglobin);
		encounter.addObs(hemoglobinObs);
		
		Context.getEncounterService().saveEncounter(encounter);
	}

}
