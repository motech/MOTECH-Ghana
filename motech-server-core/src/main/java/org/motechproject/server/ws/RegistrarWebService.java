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

package org.motechproject.server.ws;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.model.Community;
import org.motechproject.server.model.ExpectedEncounter;
import org.motechproject.server.model.ExpectedObs;
import org.motechproject.server.model.Facility;
import org.motechproject.server.svc.BirthOutcomeChild;
import org.motechproject.server.svc.MessageSourceBean;
import org.motechproject.server.svc.OpenmrsBean;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.ws.BirthOutcome;
import org.motechproject.ws.Care;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.DayOfWeek;
import org.motechproject.ws.Gender;
import org.motechproject.ws.HIVResult;
import org.motechproject.ws.HowLearned;
import org.motechproject.ws.InterestReason;
import org.motechproject.ws.LogType;
import org.motechproject.ws.MediaType;
import org.motechproject.ws.Patient;
import org.motechproject.ws.RegistrantType;
import org.motechproject.ws.RegistrationMode;
import org.motechproject.ws.server.RegistrarService;
import org.motechproject.ws.server.ValidationErrors;
import org.motechproject.ws.server.ValidationException;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.User;

/**
 * This is the service implementation for the motech server interface.
 */
public class RegistrarWebService implements RegistrarService {

	Log log = LogFactory.getLog(RegistrarWebService.class);

	RegistrarBean registrarBean;
	OpenmrsBean openmrsBean;
	WebServiceModelConverter modelConverter;
	MessageSourceBean messageBean;

	public void recordPatientHistory(Integer staffId, Integer facilityId,
			Date date, Integer motechId, Integer lastIPT, Date lastIPTDate,
			Integer lastTT, Date lastTTDate, Date bcgDate, Integer lastOPV,
			Date lastOPVDate, Integer lastPenta, Date lastPentaDate,
			Date measlesDate, Date yellowFeverDate, Integer lastIPTI,
			Date lastIPTIDate, Date lastVitaminADate)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User staff = validateStaffId(staffId, errors, "StaffID");
		Facility facility = validateFacility(facilityId, errors, "FacilityID");
		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID", true);

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Record Patient History request", errors);
		}

		registrarBean.recordPatientHistory(staff, facility.getLocation(), date,
				patient, lastIPT, lastIPTDate, lastTT, lastTTDate, bcgDate,
				lastOPV, lastOPVDate, lastPenta, lastPentaDate, measlesDate,
				yellowFeverDate, lastIPTI, lastIPTIDate, lastVitaminADate);
	}

	public void recordMotherANCVisit(Integer staffId, Integer facilityId,
			Date date, Integer motechId, Integer visitNumber, Integer location,
			String house, String community, Date estDeliveryDate,
			Integer bpSystolic, Integer bpDiastolic, Double weight,
			Integer ttDose, Integer iptDose, Boolean iptReactive,
			Boolean itnUse, Double fht, Integer fhr, Integer urineTestProtein,
			Integer urineTestGlucose, Double hemoglobin, Boolean vdrlReactive,
			Boolean vdrlTreatment, Boolean dewormer, Boolean maleInvolved,
			Boolean pmtct, Boolean preTestCounseled, HIVResult hivTestResult,
			Boolean postTestCounseled, Boolean pmtctTreatment,
			Boolean referred, Date nextANCDate, String comments)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User staff = validateStaffId(staffId, errors, "StaffID");
		Facility facility = validateFacility(facilityId, errors, "FacilityID");
		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID", true);

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Record Mother ANC Visit request", errors);
		}

		registrarBean.recordMotherANCVisit(staff, facility.getLocation(), date,
				patient, visitNumber, location, house, community,
				estDeliveryDate, bpSystolic, bpDiastolic, weight, ttDose,
				iptDose, iptReactive, itnUse, fht, fhr, urineTestProtein,
				urineTestGlucose, hemoglobin, vdrlReactive, vdrlTreatment,
				dewormer, maleInvolved, pmtct, preTestCounseled, hivTestResult,
				postTestCounseled, pmtctTreatment, referred, nextANCDate,
				comments);
	}

	public void recordPregnancyTermination(Integer staffId, Integer facilityId,
			Date date, Integer motechId, Integer terminationType,
			Integer procedure, Integer[] complications, Boolean maternalDeath,
			Boolean referred, Boolean postAbortionFPCounseled,
			Boolean postAbortionFPAccepted, String comments)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User staff = validateStaffId(staffId, errors, "StaffID");
		Facility facility = validateFacility(facilityId, errors, "FacilityID");
		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID", true);

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Record Pregnancy Termination request", errors);
		}

		registrarBean.recordPregnancyTermination(staff, facility.getLocation(),
				date, patient, terminationType, procedure, complications,
				maternalDeath, referred, postAbortionFPCounseled,
				postAbortionFPAccepted, comments);
	}

	public Patient[] recordPregnancyDelivery(Integer staffId,
			Integer facilityId, Date datetime, Integer motechId, Integer mode,
			Integer outcome, Integer deliveryLocation, Integer deliveredBy,
			Boolean maleInvolved, Integer[] complications, Integer vvf,
			Boolean maternalDeath, String comments, BirthOutcome child1Outcome,
			RegistrationMode child1RegistrationType, Integer child1MotechId,
			Gender child1Sex, String child1FirstName, Double child1Weight,
			BirthOutcome child2Outcome,
			RegistrationMode child2RegistrationType, Integer child2MotechId,
			Gender child2Sex, String child2FirstName, Double child2Weight,
			BirthOutcome child3Outcome,
			RegistrationMode child3RegistrationType, Integer child3MotechId,
			Gender child3Sex, String child3FirstName, Double child3Weight)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User staff = validateStaffId(staffId, errors, "StaffID");
		Facility facility = validateFacility(facilityId, errors, "FacilityID");
		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID", true);

		// Verify that patient ids are internally distinct
		Integer[] motechIdArray = { child1MotechId, child2MotechId,
				child3MotechId };
		Set<Integer> motechIds = new HashSet<Integer>();
		for (int i = 0; i < motechIdArray.length; i++) {
			Integer childId = motechIdArray[i];
			String fieldName = "Child" + (i + 1) + "MotechID";
			if (childId != null) {
				validateMotechId(childId, errors, fieldName, false);
				if (motechIds.contains(childId))
					errors.add(messageBean.getMessage("motechmodule.ws.inuse",
							fieldName));
				else
					motechIds.add(childId);
			}
		}

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Record Pregnancy Delivery request", errors);
		}

		List<BirthOutcomeChild> outcomes = new ArrayList<BirthOutcomeChild>();
		if (child1Outcome != null) {
			outcomes.add(new BirthOutcomeChild(child1Outcome,
					child1RegistrationType, child1MotechId, child1Sex,
					child1FirstName, child1Weight));
		}
		if (child2Outcome != null) {
			outcomes.add(new BirthOutcomeChild(child2Outcome,
					child2RegistrationType, child2MotechId, child2Sex,
					child2FirstName, child2Weight));
		}
		if (child3Outcome != null) {
			outcomes.add(new BirthOutcomeChild(child3Outcome,
					child3RegistrationType, child3MotechId, child3Sex,
					child3FirstName, child3Weight));
		}

		List<org.openmrs.Patient> childPatients = registrarBean
				.recordPregnancyDelivery(staff, facility.getLocation(),
						datetime, patient, mode, outcome, deliveryLocation,
						deliveredBy, maleInvolved, complications, vvf,
						maternalDeath, comments, outcomes);

		return modelConverter.patientToWebService(childPatients, true);
	}

	public void recordDeliveryNotification(Integer staffId, Integer facilityId,
			Date datetime, Integer motechId) throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User staff = validateStaffId(staffId, errors, "StaffID");
		Facility facility = validateFacility(facilityId, errors, "FacilityID");
		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID", true);

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Record Delivery Notification request", errors);
		}

		registrarBean.recordPregnancyDeliveryNotification(staff, facility
				.getLocation(), datetime, patient);
	}

	public void recordMotherPNCVisit(Integer staffId, Integer facilityId,
			Date datetime, Integer motechId, Integer visitNumber,
			Integer location, String house, String community, Boolean referred,
			Boolean maleInvolved, Boolean vitaminA, Integer ttDose,
			Integer lochiaColour, Boolean lochiaAmountExcess,
			Boolean lochiaOdourFoul, Double temperature, Double fht,
			String comments) throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User staff = validateStaffId(staffId, errors, "StaffID");
		Facility facility = validateFacility(facilityId, errors, "FacilityID");
		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID", true);

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Record Mother PNC Visit request", errors);
		}

		registrarBean
				.recordMotherPNCVisit(staff, facility.getLocation(), datetime,
						patient, visitNumber, location, house, community,
						referred, maleInvolved, vitaminA, ttDose, lochiaColour,
						lochiaAmountExcess, lochiaOdourFoul, temperature, fht,
						comments);
	}

	public void recordDeath(Integer staffId, Integer facilityId, Date date,
			Integer motechId) throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User staff = validateStaffId(staffId, errors, "StaffID");
		Facility facility = validateFacility(facilityId, errors, "FacilityID");
		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID", true);

		if (errors.getErrors().size() > 0) {
			throw new ValidationException("Errors in Record Death request",
					errors);
		}

		registrarBean.recordDeath(staff, facility.getLocation(), date, patient);
	}

	public void recordTTVisit(Integer staffId, Integer facilityId, Date date,
			Integer motechId, Integer ttDose) throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User staff = validateStaffId(staffId, errors, "StaffID");
		Facility facility = validateFacility(facilityId, errors, "FacilityID");
		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID", true);

		if (errors.getErrors().size() > 0) {
			throw new ValidationException("Errors in Record TT Visit request",
					errors);
		}

		registrarBean.recordTTVisit(staff, facility.getLocation(), date,
				patient, ttDose);
	}

	public void recordChildPNCVisit(Integer staffId, Integer facilityId,
			Date datetime, Integer motechId, Integer visitNumber,
			Integer location, String house, String community, Boolean referred,
			Boolean maleInvolved, Double weight, Double temperature,
			Boolean bcg, Boolean opv0, Integer respiration,
			Boolean cordConditionNormal, Boolean babyConditionGood,
			String comments) throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User staff = validateStaffId(staffId, errors, "StaffID");
		Facility facility = validateFacility(facilityId, errors, "FacilityID");
		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID", true);

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Record Child PNC Visit request", errors);
		}

		registrarBean.recordChildPNCVisit(staff, facility.getLocation(),
				datetime, patient, visitNumber, location, house, community,
				referred, maleInvolved, weight, temperature, bcg, opv0,
				respiration, cordConditionNormal, babyConditionGood, comments);
	}

	public void recordChildCWCVisit(Integer staffId, Integer facilityId,
			Date date, Integer motechId, Integer cwcLocation, String house,
			String community, Boolean bcg, Integer opvDose, Integer pentaDose,
			Boolean measles, Boolean yellowFever, Boolean csm,
			Integer iptiDose, Boolean vitaminA, Boolean dewormer,
			Double weight, Double muac, Double height, Boolean maleInvolved,
			String comments) throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User staff = validateStaffId(staffId, errors, "StaffID");
		Facility facility = validateFacility(facilityId, errors, "FacilityID");
		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID", true);

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Record Child CWC Visit request", errors);
		}

		registrarBean.recordChildCWCVisit(staff, facility.getLocation(), date,
				patient, cwcLocation, house, community, bcg, opvDose,
				pentaDose, measles, yellowFever, csm, iptiDose, vitaminA,
				dewormer, weight, muac, height, maleInvolved, comments);
	}

	public Patient registerPatient(Integer staffId, Integer facilityId,
			Date date, RegistrationMode registrationMode, Integer motechId,
			RegistrantType registrantType, String firstName, String middleName,
			String lastName, String preferredName, Date dateOfBirth,
			Boolean estimatedBirthDate, Gender sex, Boolean insured,
			String nhis, Date nhisExpires, Integer motherMotechId,
			Integer community, String address, String phoneNumber,
			Date expDeliveryDate, Boolean deliveryDateConfirmed,
			Boolean enroll, Boolean consent, ContactNumberType ownership,
			MediaType format, String language, DayOfWeek dayOfWeek,
			Date timeOfDay, InterestReason reason, HowLearned howLearned,
			Integer messagesStartWeek) throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User staff = validateStaffId(staffId, errors, "StaffID");
		Facility facility = validateFacility(facilityId, errors, "FacilityID");
		Community communityObj = validateCommunity(community, errors,
				"Community");

		if (registrationMode == RegistrationMode.USE_PREPRINTED_ID) {
			validateMotechId(motechId, errors, "MotechID", false);
		} else {
			// Ignore value if provided
			motechId = null;
		}

		org.openmrs.Patient mother = null;
		if (motherMotechId != null
				&& registrantType == RegistrantType.CHILD_UNDER_FIVE) {
			mother = validateMotechId(motherMotechId, errors, "MotherMotechID",
					true);
		}

		if (registrantType == RegistrantType.CHILD_UNDER_FIVE) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.YEAR, -5);
			if (dateOfBirth.before(calendar.getTime())) {
				errors.add(messageBean.getMessage("motechmodule.ws.invalid",
						"DOB"));
			}
		} else if (registrantType == RegistrantType.PREGNANT_MOTHER) {
			if (sex != Gender.FEMALE)
				errors.add(messageBean.getMessage("motechmodule.ws.invalid",
						"Sex"));
			if (expDeliveryDate == null)
				errors.add(messageBean.getMessage("motechmodule.ws.missing",
						"DeliveryDate"));
			if (deliveryDateConfirmed == null)
				errors.add(messageBean.getMessage("motechmodule.ws.missing",
						"DeliveryDateConfirmed"));
		}

		if (errors.getErrors().size() > 0) {
			throw new ValidationException("Errors in Register Patient request",
					errors);
		}

		org.openmrs.Patient patient = registrarBean.registerPatient(staff,
				facility.getLocation(), date, registrationMode, motechId,
				registrantType, firstName, middleName, lastName, preferredName,
				dateOfBirth, estimatedBirthDate, sex, insured, nhis,
				nhisExpires, mother, communityObj, address, phoneNumber,
				expDeliveryDate, deliveryDateConfirmed, enroll, consent,
				ownership, format, language, dayOfWeek, timeOfDay, reason,
				howLearned, messagesStartWeek);

		return modelConverter.patientToWebService(patient, true);
	}

	public void registerPregnancy(Integer staffId, Integer facilityId,
			Date date, Integer motechId, Date estDeliveryDate, Boolean enroll,
			Boolean consent, ContactNumberType ownership, String phoneNumber,
			MediaType format, String language, DayOfWeek dayOfWeek,
			Date timeOfDay, HowLearned howLearned) throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User staff = validateStaffId(staffId, errors, "StaffID");
		Facility facility = validateFacility(facilityId, errors, "FacilityID");
		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID", true);

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Register Pregnancy request", errors);
		}

		registrarBean
				.registerPregnancy(staff, facility.getLocation(), date,
						patient, estDeliveryDate, enroll, consent, ownership,
						phoneNumber, format, language, dayOfWeek, timeOfDay,
						howLearned);
	}

	public void registerANCMother(Integer staffId, Integer facilityId,
			Date date, Integer motechId, String ancRegNumber,
			Date estDeliveryDate, Double height, Integer gravida,
			Integer parity, Boolean enroll, Boolean consent,
			ContactNumberType ownership, String phoneNumber, MediaType format,
			String language, DayOfWeek dayOfWeek, Date timeOfDay,
			HowLearned howLearned) throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User staff = validateStaffId(staffId, errors, "StaffID");
		Facility facility = validateFacility(facilityId, errors, "FacilityID");
		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID", true);

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Register ANC Mother request", errors);
		}

		registrarBean.registerANCMother(staff, facility.getLocation(), date,
				patient, ancRegNumber, estDeliveryDate, height, gravida,
				parity, enroll, consent, ownership, phoneNumber, format,
				language, dayOfWeek, timeOfDay, howLearned);
	}

	public void registerCWCChild(Integer staffId, Integer facilityId,
			Date date, Integer motechId, String cwcRegNumber, Boolean enroll,
			Boolean consent, ContactNumberType ownership, String phoneNumber,
			MediaType format, String language, DayOfWeek dayOfWeek,
			Date timeOfDay, HowLearned howLearned) throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User staff = validateStaffId(staffId, errors, "StaffID");
		Facility facility = validateFacility(facilityId, errors, "FacilityID");
		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID", true);

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Register CWC Child request", errors);
		}

		registrarBean.registerCWCChild(staff, facility.getLocation(), date,
				patient, cwcRegNumber, enroll, consent, ownership, phoneNumber,
				format, language, dayOfWeek, timeOfDay, howLearned);
	}

	public void editPatient(Integer staffId, Integer facilityId, Date date,
			Integer motechId, String phoneNumber,
			ContactNumberType phoneOwnership, String nhis, Date nhisExpires,
			Boolean stopEnrollment) throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User staff = validateStaffId(staffId, errors, "StaffID");
		validateFacility(facilityId, errors, "FacilityID");
		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID", true);

		if (errors.getErrors().size() > 0) {
			throw new ValidationException("Errors in Edit Patient request",
					errors);
		}

		registrarBean.editPatient(staff, date, patient, phoneNumber,
				phoneOwnership, nhis, nhisExpires, stopEnrollment);
	}

	public void recordGeneralVisit(Integer staffId, Integer facilityId,
			Date date, String serialNumber, Gender sex, Date dateOfBirth,
			Boolean insured, Integer diagnosis, Integer secondDiagnosis,
			Boolean rdtGiven, Boolean rdtPositive, Boolean actTreated,
			Boolean newCase, Boolean referred, String comments)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		validateStaffId(staffId, errors, "StaffID");
		validateFacility(facilityId, errors, "FacilityID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException("Errors in General Visit request",
					errors);
		}

		registrarBean.recordGeneralOutpatientVisit(staffId, facilityId, date,
				serialNumber, sex, dateOfBirth, insured, diagnosis,
				secondDiagnosis, rdtGiven, rdtPositive, actTreated, newCase,
				referred, comments);
	}

	public void recordChildVisit(Integer staffId, Integer facilityId,
			Date date, String serialNumber, Integer motechId, Boolean insured,
			Integer diagnosis, Integer secondDiagnosis, Boolean rdtGiven,
			Boolean rdtPositive, Boolean actTreated, Boolean newCase,
			Boolean referred, String comments) throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User staff = validateStaffId(staffId, errors, "StaffID");
		Facility facility = validateFacility(facilityId, errors, "FacilityID");
		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID", true);

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Record Child Visit request", errors);
		}

		registrarBean.recordOutpatientVisit(staff, facility.getLocation(),
				date, patient, serialNumber, insured, diagnosis,
				secondDiagnosis, rdtGiven, rdtPositive, actTreated, newCase,
				referred, comments);
	}

	public void recordMotherVisit(Integer staffId, Integer facilityId,
			Date date, String serialNumber, Integer motechId, Boolean insured,
			Integer diagnosis, Integer secondDiagnosis, Boolean rdtGiven,
			Boolean rdtPositive, Boolean actTreated, Boolean newCase,
			Boolean referred, String comments) throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User staff = validateStaffId(staffId, errors, "StaffID");
		Facility facility = validateFacility(facilityId, errors, "FacilityID");
		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID", true);

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Record Mother Visit request", errors);
		}

		registrarBean.recordOutpatientVisit(staff, facility.getLocation(),
				date, patient, serialNumber, insured, diagnosis,
				secondDiagnosis, rdtGiven, rdtPositive, actTreated, newCase,
				referred, comments);
	}

	public Care[] queryANCDefaulters(Integer staffId, Integer facilityId)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		validateStaffId(staffId, errors, "StaffID");
		Facility facility = validateFacility(facilityId, errors, "FacilityID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in ANC Defaulters Query request", errors);
		}

		List<ExpectedEncounter> defaultedEncounters = registrarBean
				.getDefaultedExpectedEncounters(facility,
						new String[] { "ANC" });
		List<ExpectedObs> defaultedObs = registrarBean.getDefaultedExpectedObs(
				facility, new String[] { "TT", "IPT" });

		Care[] upcomingCares = modelConverter.defaultedToWebServiceCares(
				defaultedEncounters, defaultedObs);

		return upcomingCares;
	}

	public Care[] queryTTDefaulters(Integer staffId, Integer facilityId)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		validateStaffId(staffId, errors, "StaffID");
		Facility facility = validateFacility(facilityId, errors, "FacilityID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in TT Defaulters Query request", errors);
		}

		List<ExpectedObs> defaultedObs = registrarBean.getDefaultedExpectedObs(
				facility, new String[] { "TT" });
		return modelConverter.defaultedObsToWebServiceCares(defaultedObs);
	}

	public Care[] queryMotherPNCDefaulters(Integer staffId, Integer facilityId)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		validateStaffId(staffId, errors, "StaffID");
		Facility facility = validateFacility(facilityId, errors, "FacilityID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Mother PNC Defaulters Query request", errors);
		}

		List<ExpectedEncounter> defaultedEncounters = registrarBean
				.getDefaultedExpectedEncounters(facility,
						new String[] { "PNC(mother)" });
		return modelConverter
				.defaultedEncountersToWebServiceCares(defaultedEncounters);
	}

	public Care[] queryChildPNCDefaulters(Integer staffId, Integer facilityId)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		validateStaffId(staffId, errors, "StaffID");
		Facility facility = validateFacility(facilityId, errors, "FacilityID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Child PNC Defaulters Query request", errors);
		}

		List<ExpectedEncounter> defaultedEncounters = registrarBean
				.getDefaultedExpectedEncounters(facility,
						new String[] { "PNC(baby)" });
		return modelConverter
				.defaultedEncountersToWebServiceCares(defaultedEncounters);
	}

	public Care[] queryCWCDefaulters(Integer staffId, Integer facilityId)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		validateStaffId(staffId, errors, "StaffID");
		Facility facility = validateFacility(facilityId, errors, "FacilityID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in CWC Defaulters Query request", errors);
		}

		List<ExpectedObs> defaultedObs = registrarBean.getDefaultedExpectedObs(
				facility, new String[] { "OPV", "BCG", "Penta", "YellowFever",
						"Measles", "VitaA", "IPTI" });
		return modelConverter.defaultedObsToWebServiceCares(defaultedObs);
	}

	public Patient[] queryUpcomingDeliveries(Integer staffId, Integer facilityId)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		validateStaffId(staffId, errors, "StaffID");
		Facility facility = validateFacility(facilityId, errors, "FacilityID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Upcoming Deliveries Query request", errors);
		}

		List<Obs> dueDates = registrarBean
				.getUpcomingPregnanciesDueDate(facility);
		return modelConverter.dueDatesToWebServicePatients(dueDates);
	}

	public Patient[] queryRecentDeliveries(Integer staffId, Integer facilityId)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		validateStaffId(staffId, errors, "StaffID");
		Facility facility = validateFacility(facilityId, errors, "FacilityID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Recent Deliveries Query request", errors);
		}

		List<Encounter> deliveries = registrarBean
				.getRecentDeliveries(facility);
		return modelConverter.deliveriesToWebServicePatients(deliveries);
	}

	public Patient[] queryOverdueDeliveries(Integer staffId, Integer facilityId)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		validateStaffId(staffId, errors, "StaffID");
		Facility facility = validateFacility(facilityId, errors, "FacilityID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Overdue Deliveries Query request", errors);
		}

		List<Obs> dueDates = registrarBean
				.getOverduePregnanciesDueDate(facility);
		return modelConverter.dueDatesToWebServicePatients(dueDates);
	}

	public Patient queryUpcomingCare(Integer staffId, Integer facilityId,
			Integer motechId) throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		validateStaffId(staffId, errors, "StaffID");
		validateFacility(facilityId, errors, "FacilityID");

		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID", true);

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Upcoming Care Query request", errors);
		}

		Patient wsPatient = modelConverter.patientToWebService(patient, true);

		List<ExpectedEncounter> upcomingEncounters = registrarBean
				.getUpcomingExpectedEncounters(patient);
		List<ExpectedObs> upcomingObs = registrarBean
				.getUpcomingExpectedObs(patient);

		Care[] upcomingCares = modelConverter.upcomingToWebServiceCares(
				upcomingEncounters, upcomingObs, false);

		wsPatient.setCares(upcomingCares);
		return wsPatient;
	}

	public Patient[] queryMotechId(Integer staffId, Integer facilityId,
			String firstName, String lastName, String preferredName,
			Date birthDate, String nhis, String phoneNumber)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		validateStaffId(staffId, errors, "StaffID");
		validateFacility(facilityId, errors, "FacilityID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException("Errors in MotechID Query request",
					errors);
		}

		List<org.openmrs.Patient> patients = registrarBean.getPatients(
				firstName, lastName, preferredName, birthDate, null,
				phoneNumber, nhis, null);
		return modelConverter.patientToWebService(patients, true);
	}

	public Patient queryPatient(Integer staffId, Integer facilityId,
			Integer motechId) throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		validateStaffId(staffId, errors, "StaffID");
		validateFacility(facilityId, errors, "FacilityID");

		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID", true);

		if (errors.getErrors().size() > 0) {
			throw new ValidationException("Errors in Patient Query request",
					errors);
		}

		return modelConverter.patientToWebService(patient, false);
	}

	public String[] getPatientEnrollments(Integer motechId)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID", true);

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Get Patient Enrollments request", errors);
		}

		return registrarBean.getActiveMessageProgramEnrollmentNames(patient);
	}

	public void log(LogType type, String message) {

		log.info("Logtype: " + type + ", Message: " + message);
	}

	public void setMessageStatus(String messageId, Boolean success) {

		registrarBean.setMessageStatus(messageId, success);
	}

	public void setRegistrarBean(RegistrarBean registrarBean) {
		this.registrarBean = registrarBean;
	}

	public void setOpenmrsBean(OpenmrsBean openmrsBean) {
		this.openmrsBean = openmrsBean;
	}

	public void setModelConverter(WebServiceModelConverter modelConverter) {
		this.modelConverter = modelConverter;
	}

	public void setMessageBean(MessageSourceBean messageBean) {
		this.messageBean = messageBean;
	}

	private User validateStaffId(Integer staffId, ValidationErrors errors,
			String fieldName) {
		if (staffId == null) {
			errors.add(messageBean.getMessage("motechmodule.ws.missing",
					fieldName));
			return null;
		}
		if (!registrarBean.isValidIdCheckDigit(staffId)) {
			errors.add(messageBean.getMessage("motechmodule.ws.invalid",
					fieldName));
			return null;
		}
		User staff = openmrsBean.getStaffBySystemId(staffId.toString());
		if (staff == null) {
			errors.add(messageBean.getMessage("motechmodule.ws.notfound",
					fieldName));
		}
		return staff;
	}

	private org.openmrs.Patient validateMotechId(Integer motechId,
			ValidationErrors errors, String fieldName, boolean mustExist) {
		if (motechId == null) {
			errors.add(messageBean.getMessage("motechmodule.ws.missing",
					fieldName));
			return null;
		}
		if (!registrarBean.isValidMotechIdCheckDigit(motechId)) {
			errors.add(messageBean.getMessage("motechmodule.ws.invalid",
					fieldName));
			return null;
		}
		org.openmrs.Patient patient = openmrsBean.getPatientByMotechId(motechId
				.toString());
		if (mustExist && patient == null) {
			errors.add(messageBean.getMessage("motechmodule.ws.notfound",
					fieldName));
		} else if (!mustExist && patient != null) {
			errors.add(messageBean.getMessage("motechmodule.ws.inuse",
					fieldName));
		}
		return patient;
	}

	private Facility validateFacility(Integer facilityId,
			ValidationErrors errors, String fieldName) {
		if (facilityId == null) {
			errors.add(messageBean.getMessage("motechmodule.ws.missing",
					fieldName));
			return null;
		}
		if (!registrarBean.isValidIdCheckDigit(facilityId)) {
			errors.add(messageBean.getMessage("motechmodule.ws.invalid",
					fieldName));
			return null;
		}
		Facility facility = registrarBean.getFacilityById(facilityId);
		if (facility == null) {
			errors.add(messageBean.getMessage("motechmodule.ws.notfound",
					fieldName));
		}
		return facility;
	}

	private Community validateCommunity(Integer communityId,
			ValidationErrors errors, String fieldName) {
		if (communityId == null) {
			errors.add(messageBean.getMessage("motechmodule.ws.missing",
					fieldName));
			return null;
		}
		Community community = registrarBean.getCommunityById(communityId);
		if (community == null) {
			errors.add(messageBean.getMessage("motechmodule.ws.notfound",
					fieldName));
		} else if (Boolean.TRUE.equals(community.getRetired())) {
			errors.add(messageBean.getMessage("motechmodule.ws.invalid",
					fieldName));
		}
		return community;
	}

}
