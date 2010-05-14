package org.motechproject.server.ws;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.model.ExpectedEncounter;
import org.motechproject.server.model.ExpectedObs;
import org.motechproject.server.model.Facility;
import org.motechproject.server.svc.BirthOutcomeChild;
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
 * This is the service enpoint implementation for the major motech server web
 * service.
 * 
 * This can be accessed via /openmrs/ws/RegistrarService since we mapped it to
 * /ws/RegistrarService in the moduleApplicationContext.xml file.
 */

@WebService(targetNamespace = "http://server.ws.motechproject.org/")
public class RegistrarWebService implements RegistrarService {

	Log log = LogFactory.getLog(RegistrarWebService.class);

	RegistrarBean registrarBean;
	OpenmrsBean openmrsBean;
	WebServiceModelConverter modelConverter;

	@WebMethod
	public void recordMotherANCVisit(
			@WebParam(name = "staffId") Integer staffId,
			@WebParam(name = "facilityId") Integer facilityId,
			@WebParam(name = "date") Date date,
			@WebParam(name = "motechId") Integer motechId,
			@WebParam(name = "visitNumber") Integer visitNumber,
			@WebParam(name = "location") Integer location,
			@WebParam(name = "house") String house,
			@WebParam(name = "community") String community,
			@WebParam(name = "estDeliveryDate") Date estDeliveryDate,
			@WebParam(name = "bpSystolic") Integer bpSystolic,
			@WebParam(name = "bpDiastolic") Integer bpDiastolic,
			@WebParam(name = "weight") Double weight,
			@WebParam(name = "ttDose") Integer ttDose,
			@WebParam(name = "iptDose") Integer iptDose,
			@WebParam(name = "iptReactive") Boolean iptReactive,
			@WebParam(name = "itnUse") Boolean itnUse,
			@WebParam(name = "fht") Integer fht,
			@WebParam(name = "fhr") Integer fhr,
			@WebParam(name = "urineTestProteinPositive") Boolean urineTestProteinPositive,
			@WebParam(name = "urineTestGlucosePositive") Boolean urineTestGlucosePositive,
			@WebParam(name = "hemoglobin") Double hemoglobin,
			@WebParam(name = "vdrlReactive") Boolean vdrlReactive,
			@WebParam(name = "vdrlTreatment") Boolean vdrlTreatment,
			@WebParam(name = "dewormer") Boolean dewormer,
			@WebParam(name = "maleInvolved") Boolean maleInvolved,
			@WebParam(name = "pmtct") Boolean pmtct,
			@WebParam(name = "preTestCounseled") Boolean preTestCounseled,
			@WebParam(name = "hivTestResult") HIVResult hivTestResult,
			@WebParam(name = "postTestCounseled") Boolean postTestCounseled,
			@WebParam(name = "pmtctTreatment") Boolean pmtctTreatment,
			@WebParam(name = "referred") Boolean referred,
			@WebParam(name = "nextANCDate") Date nextANCDate,
			@WebParam(name = "comments") String comments)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User nurse = validateChpsId(staffId, errors, "StaffID");
		validateFacility(facilityId, errors, "FacilityID");
		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Record Mother ANC Visit request", errors);
		}

		registrarBean.recordMotherANCVisit(nurse, facilityId, date, patient,
				visitNumber, location, house, community, estDeliveryDate,
				bpSystolic, bpDiastolic, weight, ttDose, iptDose, iptReactive,
				itnUse, fht, fhr, urineTestProteinPositive,
				urineTestGlucosePositive, hemoglobin, vdrlReactive,
				vdrlTreatment, dewormer, maleInvolved, pmtct, preTestCounseled,
				hivTestResult, postTestCounseled, pmtctTreatment, referred,
				nextANCDate, comments);
	}

	@WebMethod
	public void recordPregnancyTermination(
			@WebParam(name = "staffId") Integer staffId,
			@WebParam(name = "facilityId") Integer facilityId,
			@WebParam(name = "date") Date date,
			@WebParam(name = "motechId") Integer motechId,
			@WebParam(name = "terminationType") Integer terminationType,
			@WebParam(name = "procedure") Integer procedure,
			@WebParam(name = "complications") Integer[] complications,
			@WebParam(name = "maternalDeath") Boolean maternalDeath,
			@WebParam(name = "referred") Boolean referred,
			@WebParam(name = "postAbortionFPCounseled") Boolean postAbortionFPCounseled,
			@WebParam(name = "postAbortionFPAccepted") Boolean postAbortionFPAccepted,
			@WebParam(name = "comments") String comments)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User nurse = validateChpsId(staffId, errors, "StaffID");
		validateFacility(facilityId, errors, "FacilityID");
		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Record Pregnancy Termination request", errors);
		}

		registrarBean.recordPregnancyTermination(nurse, date, patient,
				terminationType, procedure, complications, maternalDeath,
				referred, postAbortionFPCounseled, postAbortionFPAccepted,
				comments);
	}

	@WebMethod
	public Patient[] recordPregnancyDelivery(
			@WebParam(name = "staffId") Integer staffId,
			@WebParam(name = "facilityId") Integer facilityId,
			@WebParam(name = "datetime") Date datetime,
			@WebParam(name = "motechId") Integer motechId,
			@WebParam(name = "mode") Integer mode,
			@WebParam(name = "outcome") Integer outcome,
			@WebParam(name = "deliveryLocation") Integer deliveryLocation,
			@WebParam(name = "deliveredBy") Integer deliveredBy,
			@WebParam(name = "maleInvolved") Boolean maleInvolved,
			@WebParam(name = "complications") Integer[] complications,
			@WebParam(name = "vvf") Integer vvf,
			@WebParam(name = "maternalDeath") Boolean maternalDeath,
			@WebParam(name = "comments") String comments,
			@WebParam(name = "child1Outcome") BirthOutcome child1Outcome,
			@WebParam(name = "child1RegistrationType") RegistrationMode child1RegistrationType,
			@WebParam(name = "child1MotechId") Integer child1MotechId,
			@WebParam(name = "child1Sex") Gender child1Sex,
			@WebParam(name = "child1FirstName") String child1FirstName,
			@WebParam(name = "child1Weight") Double child1Weight,
			@WebParam(name = "child2Outcome") BirthOutcome child2Outcome,
			@WebParam(name = "child2RegistrationType") RegistrationMode child2RegistrationType,
			@WebParam(name = "child2MotechId") Integer child2MotechId,
			@WebParam(name = "child2Sex") Gender child2Sex,
			@WebParam(name = "child2FirstName") String child2FirstName,
			@WebParam(name = "child2Weight") Double child2Weight,
			@WebParam(name = "child3Outcome") BirthOutcome child3Outcome,
			@WebParam(name = "child3RegistrationType") RegistrationMode child3RegistrationType,
			@WebParam(name = "child3MotechId") Integer child3MotechId,
			@WebParam(name = "child3Sex") Gender child3Sex,
			@WebParam(name = "child3FirstName") String child3FirstName,
			@WebParam(name = "child3Weight") Double child3Weight)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User nurse = validateChpsId(staffId, errors, "StaffID");
		validateFacility(facilityId, errors, "FacilityID");
		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Record Pregnancy Delivery request", errors);
		}

		List<BirthOutcomeChild> outcomes = new ArrayList<BirthOutcomeChild>();
		if (child1Outcome != null && child1Sex != null) {
			outcomes.add(new BirthOutcomeChild(child1Outcome,
					child1RegistrationType, child1MotechId, child1Sex,
					child1FirstName, child1Weight));
		}
		if (child2Outcome != null && child2Sex != null) {
			outcomes.add(new BirthOutcomeChild(child2Outcome,
					child2RegistrationType, child2MotechId, child2Sex,
					child2FirstName, child2Weight));
		}
		if (child3Outcome != null && child3Sex != null) {
			outcomes.add(new BirthOutcomeChild(child3Outcome,
					child3RegistrationType, child3MotechId, child3Sex,
					child3FirstName, child3Weight));
		}

		List<org.openmrs.Patient> childPatients = registrarBean
				.recordPregnancyDelivery(nurse, datetime, patient, mode,
						outcome, deliveryLocation, deliveredBy, maleInvolved,
						complications, vvf, maternalDeath, comments, outcomes);

		return modelConverter.patientToWebService(childPatients, true);
	}

	@WebMethod
	public void recordDeliveryNotification(
			@WebParam(name = "staffId") Integer staffId,
			@WebParam(name = "facilityId") Integer facilityId,
			@WebParam(name = "datetime") Date datetime,
			@WebParam(name = "motechId") Integer motechId)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User nurse = validateChpsId(staffId, errors, "StaffID");
		validateFacility(facilityId, errors, "FacilityID");
		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Record Delivery Notification request", errors);
		}

		registrarBean.recordPregnancyDeliveryNotification(nurse, datetime,
				patient);
	}

	@WebMethod
	public void recordMotherPNCVisit(
			@WebParam(name = "staffId") Integer staffId,
			@WebParam(name = "facilityId") Integer facilityId,
			@WebParam(name = "datetime") Date datetime,
			@WebParam(name = "motechId") Integer motechId,
			@WebParam(name = "visitNumber") Integer visitNumber,
			@WebParam(name = "location") Integer location,
			@WebParam(name = "house") String house,
			@WebParam(name = "community") String community,
			@WebParam(name = "referred") Boolean referred,
			@WebParam(name = "maleInvolved") Boolean maleInvolved,
			@WebParam(name = "vitaminA") Boolean vitaminA,
			@WebParam(name = "ttDose") Integer ttDose,
			@WebParam(name = "lochiaColour") Integer lochiaColour,
			@WebParam(name = "lochiaAmountExcess") Boolean lochiaAmountExcess,
			@WebParam(name = "temperature") Integer temperature,
			@WebParam(name = "fht") Integer fht,
			@WebParam(name = "comments") String comments)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User nurse = validateChpsId(staffId, errors, "StaffID");
		validateFacility(facilityId, errors, "FacilityID");
		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Record Mother PNC Visit request", errors);
		}

		registrarBean.recordMotherPNCVisit(nurse, datetime, patient,
				visitNumber, location, house, community, referred,
				maleInvolved, vitaminA, ttDose, lochiaColour,
				lochiaAmountExcess, temperature, fht, comments);
	}

	@WebMethod
	public void recordDeath(@WebParam(name = "staffId") Integer staffId,
			@WebParam(name = "facilityId") Integer facilityId,
			@WebParam(name = "date") Date date,
			@WebParam(name = "motechId") Integer motechId,
			@WebParam(name = "cause") Integer cause) throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User nurse = validateChpsId(staffId, errors, "StaffID");
		validateFacility(facilityId, errors, "FacilityID");
		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException("Errors in Record Death request",
					errors);
		}

		registrarBean.recordDeath(nurse, date, patient, cause);
	}

	@WebMethod
	public void recordTTVisit(@WebParam(name = "staffId") Integer staffId,
			@WebParam(name = "facilityId") Integer facilityId,
			@WebParam(name = "date") Date date,
			@WebParam(name = "motechId") Integer motechId,
			@WebParam(name = "ttDose") Integer ttDose)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User nurse = validateChpsId(staffId, errors, "StaffID");
		validateFacility(facilityId, errors, "FacilityID");
		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException("Errors in Record TT Visit request",
					errors);
		}

		registrarBean.recordTTVisit(nurse, date, patient, ttDose);
	}

	@WebMethod
	public void recordChildPNCVisit(
			@WebParam(name = "staffId") Integer staffId,
			@WebParam(name = "facilityId") Integer facilityId,
			@WebParam(name = "datetime") Date datetime,
			@WebParam(name = "motechId") Integer motechId,
			@WebParam(name = "visitNumber") Integer visitNumber,
			@WebParam(name = "location") Integer location,
			@WebParam(name = "house") String house,
			@WebParam(name = "community") String community,
			@WebParam(name = "referred") Boolean referred,
			@WebParam(name = "maleInvolved") Boolean maleInvolved,
			@WebParam(name = "weight") Double weight,
			@WebParam(name = "temperature") Integer temperature,
			@WebParam(name = "bcg") Boolean bcg,
			@WebParam(name = "opv0") Boolean opv0,
			@WebParam(name = "respiration") Integer respiration,
			@WebParam(name = "cordConditionNormal") Boolean cordConditionNormal,
			@WebParam(name = "babyConditionGood") Boolean babyConditionGood,
			@WebParam(name = "comments") String comments)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User nurse = validateChpsId(staffId, errors, "StaffID");
		validateFacility(facilityId, errors, "FacilityID");
		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Record Child PNC Visit request", errors);
		}

		registrarBean.recordChildPNCVisit(nurse, datetime, patient,
				visitNumber, location, house, community, referred,
				maleInvolved, weight, temperature, bcg, opv0, respiration,
				cordConditionNormal, babyConditionGood, comments);
	}

	@WebMethod
	public void recordChildCWCVisit(
			@WebParam(name = "staffId") Integer staffId,
			@WebParam(name = "facilityId") Integer facilityId,
			@WebParam(name = "date") Date date,
			@WebParam(name = "motechId") Integer motechId,
			@WebParam(name = "cwcLocation") Integer cwcLocation,
			@WebParam(name = "house") String house,
			@WebParam(name = "community") String community,
			@WebParam(name = "bcg") Boolean bcg,
			@WebParam(name = "opvDose") Integer opvDose,
			@WebParam(name = "pentaDose") Integer pentaDose,
			@WebParam(name = "measles") Boolean measles,
			@WebParam(name = "yellowFever") Boolean yellowFever,
			@WebParam(name = "csm") Boolean csm,
			@WebParam(name = "ipti") Boolean ipti,
			@WebParam(name = "vitaminA") Boolean vitaminA,
			@WebParam(name = "dewormer") Boolean dewormer,
			@WebParam(name = "weight") Double weight,
			@WebParam(name = "muac") Integer muac,
			@WebParam(name = "height") Integer height,
			@WebParam(name = "maleInvolved") Boolean maleInvolved,
			@WebParam(name = "comments") String comments)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User nurse = validateChpsId(staffId, errors, "StaffID");
		validateFacility(facilityId, errors, "FacilityID");
		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Record Child CWC Visit request", errors);
		}

		registrarBean.recordChildCWCVisit(nurse, date, patient, cwcLocation,
				house, community, bcg, opvDose, pentaDose, measles,
				yellowFever, csm, ipti, vitaminA, dewormer, weight, muac,
				height, maleInvolved, comments);
	}

	@WebMethod
	public Patient registerPatient(
			@WebParam(name = "staffId") Integer staffId,
			@WebParam(name = "facilityId") Integer facilityId,
			@WebParam(name = "date") Date date,
			@WebParam(name = "registrationMode") RegistrationMode registrationMode,
			@WebParam(name = "motechId") Integer motechId,
			@WebParam(name = "registrantType") RegistrantType registrantType,
			@WebParam(name = "firstName") String firstName,
			@WebParam(name = "middleName") String middleName,
			@WebParam(name = "lastName") String lastName,
			@WebParam(name = "preferredName") String preferredName,
			@WebParam(name = "dateOfBirth") Date dateOfBirth,
			@WebParam(name = "estimatedBirthDate") Boolean estimatedBirthDate,
			@WebParam(name = "sex") Gender sex,
			@WebParam(name = "insured") Boolean insured,
			@WebParam(name = "nhis") String nhis,
			@WebParam(name = "nhisExpires") Date nhisExpires,
			@WebParam(name = "motherMotechId") Integer motherMotechId,
			@WebParam(name = "community") Integer community,
			@WebParam(name = "address") String address,
			@WebParam(name = "phoneNumber") Integer phoneNumber,
			@WebParam(name = "expDeliveryDate") Date expDeliveryDate,
			@WebParam(name = "deliveryDateConfirmed") Boolean deliveryDateConfirmed,
			@WebParam(name = "gravida") Integer gravida,
			@WebParam(name = "parity") Integer parity,
			@WebParam(name = "enroll") Boolean enroll,
			@WebParam(name = "consent") Boolean consent,
			@WebParam(name = "ownership") ContactNumberType ownership,
			@WebParam(name = "format") MediaType format,
			@WebParam(name = "language") String language,
			@WebParam(name = "dayOfWeek") DayOfWeek dayOfWeek,
			@WebParam(name = "timeOfDay") Date timeOfDay,
			@WebParam(name = "reason") InterestReason reason,
			@WebParam(name = "howLearned") HowLearned howLearned,
			@WebParam(name = "messagesStartWeek") Integer messagesStartWeek)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User nurse = validateChpsId(staffId, errors, "StaffID");
		validateFacility(facilityId, errors, "FacilityID");

		if (motechId == null
				&& registrationMode == RegistrationMode.USE_PREPRINTED_ID) {
			errors.add(3, "MotechID");
		} else if (motechId != null
				&& registrationMode == RegistrationMode.USE_PREPRINTED_ID) {
			org.openmrs.Patient patient = openmrsBean
					.getPatientByMotechId(motechId.toString());
			if (patient != null) {
				errors.add(2, "MotechID");
			}
		}

		org.openmrs.Patient mother = null;
		if (motherMotechId != null
				&& registrantType == RegistrantType.CHILD_UNDER_FIVE) {
			mother = validateMotechId(motherMotechId, errors, "MotherMotechID");
		}

		if (registrantType == RegistrantType.CHILD_UNDER_FIVE) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.YEAR, -5);
			if (dateOfBirth.before(calendar.getTime())) {
				errors.add(2, "DoB");
			}
		} else if (registrantType == RegistrantType.PREGNANT_MOTHER) {
			if (sex != Gender.FEMALE)
				errors.add(2, "Sex");
			if (expDeliveryDate == null)
				errors.add(3, "DeliveryDate");
			if (deliveryDateConfirmed == null)
				errors.add(3, "DeliveryDateConfirmed");
			if (gravida == null)
				errors.add(3, "Gravida");
			if (parity == null)
				errors.add(3, "Parity");
		}

		if (errors.getErrors().size() > 0) {
			throw new ValidationException("Errors in Register Patient request",
					errors);
		}

		org.openmrs.Patient patient = registrarBean.registerPatient(nurse,
				facilityId, date, registrationMode, motechId, registrantType,
				firstName, middleName, lastName, preferredName, dateOfBirth,
				estimatedBirthDate, sex, insured, nhis, nhisExpires, mother,
				community, address, phoneNumber, expDeliveryDate,
				deliveryDateConfirmed, gravida, parity, enroll, consent,
				ownership, format, language, dayOfWeek, timeOfDay, reason,
				howLearned, messagesStartWeek);

		return modelConverter.patientToWebService(patient, true);
	}

	@WebMethod
	public void registerPregnancy(@WebParam(name = "staffId") Integer staffId,
			@WebParam(name = "facilityId") Integer facilityId,
			@WebParam(name = "date") Date date,
			@WebParam(name = "motechId") Integer motechId,
			@WebParam(name = "estDeliveryDate") Date estDeliveryDate,
			@WebParam(name = "enroll") Boolean enroll,
			@WebParam(name = "consent") Boolean consent,
			@WebParam(name = "ownership") ContactNumberType ownership,
			@WebParam(name = "phoneNumber") Integer phoneNumber,
			@WebParam(name = "format") MediaType format,
			@WebParam(name = "language") String language,
			@WebParam(name = "dayOfWeek") DayOfWeek dayOfWeek,
			@WebParam(name = "timeOfDay") Date timeOfDay,
			@WebParam(name = "reason") InterestReason reason,
			@WebParam(name = "howLearned") HowLearned howLearned,
			@WebParam(name = "messagesStartWeek") Integer messagesStartWeek)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User nurse = validateChpsId(staffId, errors, "StaffID");
		validateFacility(facilityId, errors, "FacilityID");
		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Register Pregnancy request", errors);
		}

		registrarBean.registerPregnancy(nurse, date, patient, estDeliveryDate,
				enroll, consent, ownership, phoneNumber, format, language,
				dayOfWeek, timeOfDay, reason, howLearned, messagesStartWeek);
	}

	@WebMethod
	public void registerANCMother(@WebParam(name = "staffId") Integer staffId,
			@WebParam(name = "facilityId") Integer facilityId,
			@WebParam(name = "date") Date date,
			@WebParam(name = "motechId") Integer motechId,
			@WebParam(name = "ancRegNumber") String ancRegNumber,
			@WebParam(name = "estDeliveryDate") Date estDeliveryDate,
			@WebParam(name = "height") Integer height,
			@WebParam(name = "gravida") Integer gravida,
			@WebParam(name = "parity") Integer parity,
			@WebParam(name = "enroll") Boolean enroll,
			@WebParam(name = "consent") Boolean consent,
			@WebParam(name = "ownership") ContactNumberType ownership,
			@WebParam(name = "phoneNumber") Integer phoneNumber,
			@WebParam(name = "format") MediaType format,
			@WebParam(name = "language") String language,
			@WebParam(name = "dayOfWeek") DayOfWeek dayOfWeek,
			@WebParam(name = "timeOfDay") Date timeOfDay,
			@WebParam(name = "reason") InterestReason reason,
			@WebParam(name = "howLearned") HowLearned howLearned,
			@WebParam(name = "messagesStartWeek") Integer messagesStartWeek)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User nurse = validateChpsId(staffId, errors, "StaffID");
		validateFacility(facilityId, errors, "FacilityID");
		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Register ANC Mother request", errors);
		}

		registrarBean.registerANCMother(nurse, date, patient, ancRegNumber,
				estDeliveryDate, height, gravida, parity, enroll, consent,
				ownership, phoneNumber, format, language, dayOfWeek, timeOfDay,
				reason, howLearned, messagesStartWeek);
	}

	@WebMethod
	public void registerCWCChild(@WebParam(name = "staffId") Integer staffId,
			@WebParam(name = "facilityId") Integer facilityId,
			@WebParam(name = "date") Date date,
			@WebParam(name = "motechId") Integer motechId,
			@WebParam(name = "cwcRegNumber") String cwcRegNumber,
			@WebParam(name = "enroll") Boolean enroll,
			@WebParam(name = "consent") Boolean consent,
			@WebParam(name = "ownership") ContactNumberType ownership,
			@WebParam(name = "phoneNumber") Integer phoneNumber,
			@WebParam(name = "format") MediaType format,
			@WebParam(name = "language") String language,
			@WebParam(name = "dayOfWeek") DayOfWeek dayOfWeek,
			@WebParam(name = "timeOfDay") Date timeOfDay,
			@WebParam(name = "reason") InterestReason reason,
			@WebParam(name = "howLearned") HowLearned howLearned,
			@WebParam(name = "messagesStartWeek") Integer messagesStartWeek)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User nurse = validateChpsId(staffId, errors, "StaffID");
		validateFacility(facilityId, errors, "FacilityID");
		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Register CWC Child request", errors);
		}

		registrarBean.registerCWCChild(nurse, date, patient, cwcRegNumber,
				enroll, consent, ownership, phoneNumber, format, language,
				dayOfWeek, timeOfDay, reason, howLearned, messagesStartWeek);
	}

	@WebMethod
	public void editPatient(
			@WebParam(name = "staffId") Integer staffId,
			@WebParam(name = "facilityId") Integer facilityId,
			@WebParam(name = "date") Date date,
			@WebParam(name = "motechId") Integer motechId,
			@WebParam(name = "phoneNumber") Integer phoneNumber,
			@WebParam(name = "phoneOwnership") ContactNumberType phoneOwnership,
			@WebParam(name = "nhis") String nhis,
			@WebParam(name = "nhisExpires") Date nhisExpires,
			@WebParam(name = "stopEnrollment") Boolean stopEnrollment)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User nurse = validateChpsId(staffId, errors, "StaffID");
		validateFacility(facilityId, errors, "FacilityID");
		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException("Errors in Edit Patient request",
					errors);
		}

		registrarBean.editPatient(nurse, date, patient, phoneNumber,
				phoneOwnership, nhis, nhisExpires, stopEnrollment);
	}

	@WebMethod
	public void recordGeneralVisit(@WebParam(name = "staffId") Integer staffId,
			@WebParam(name = "facilityId") Integer facilityId,
			@WebParam(name = "date") Date date,
			@WebParam(name = "serialNumber") String serialNumber,
			@WebParam(name = "sex") Gender sex,
			@WebParam(name = "dateOfBirth") Date dateOfBirth,
			@WebParam(name = "insured") Boolean insured,
			@WebParam(name = "diagnosis") Integer diagnosis,
			@WebParam(name = "secondDiagnosis") Integer secondDiagnosis,
			@WebParam(name = "rdtGiven") Boolean rdtGiven,
			@WebParam(name = "rdtPositive") Boolean rdtPositive,
			@WebParam(name = "actTreated") Boolean actTreated,
			@WebParam(name = "newCase") Boolean newCase,
			@WebParam(name = "referred") Boolean referred,
			@WebParam(name = "comments") String comments)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		validateChpsId(staffId, errors, "StaffID");
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

	@WebMethod
	public void recordChildVisit(@WebParam(name = "staffId") Integer staffId,
			@WebParam(name = "facilityId") Integer facilityId,
			@WebParam(name = "date") Date date,
			@WebParam(name = "serialNumber") String serialNumber,
			@WebParam(name = "motechId") Integer motechId,
			@WebParam(name = "diagnosis") Integer diagnosis,
			@WebParam(name = "secondDiagnosis") Integer secondDiagnosis,
			@WebParam(name = "rdtGiven") Boolean rdtGiven,
			@WebParam(name = "rdtPositive") Boolean rdtPositive,
			@WebParam(name = "actTreated") Boolean actTreated,
			@WebParam(name = "newCase") Boolean newCase,
			@WebParam(name = "referred") Boolean referred,
			@WebParam(name = "comments") String comments)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User nurse = validateChpsId(staffId, errors, "StaffID");
		validateFacility(facilityId, errors, "FacilityID");
		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Record Child Visit request", errors);
		}

		registrarBean.recordOutpatientVisit(nurse, date, patient, serialNumber,
				diagnosis, secondDiagnosis, rdtGiven, rdtPositive, actTreated,
				newCase, referred, comments);
	}

	@WebMethod
	public void recordMotherVisit(@WebParam(name = "staffId") Integer staffId,
			@WebParam(name = "facilityId") Integer facilityId,
			@WebParam(name = "date") Date date,
			@WebParam(name = "serialNumber") String serialNumber,
			@WebParam(name = "motechId") Integer motechId,
			@WebParam(name = "diagnosis") Integer diagnosis,
			@WebParam(name = "secondDiagnosis") Integer secondDiagnosis,
			@WebParam(name = "rdtGiven") Boolean rdtGiven,
			@WebParam(name = "rdtPositive") Boolean rdtPositive,
			@WebParam(name = "actTreated") Boolean actTreated,
			@WebParam(name = "newCase") Boolean newCase,
			@WebParam(name = "referred") Boolean referred,
			@WebParam(name = "comments") String comments)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User nurse = validateChpsId(staffId, errors, "StaffID");
		validateFacility(facilityId, errors, "FacilityID");
		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Record Mother Visit request", errors);
		}

		registrarBean.recordOutpatientVisit(nurse, date, patient, serialNumber,
				diagnosis, secondDiagnosis, rdtGiven, rdtPositive, actTreated,
				newCase, referred, comments);
	}

	@WebMethod
	public Care[] queryANCDefaulters(
			@WebParam(name = "staffId") Integer staffId,
			@WebParam(name = "facilityId") Integer facilityId)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		validateChpsId(staffId, errors, "StaffID");
		validateFacility(facilityId, errors, "FacilityID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in ANC Defaulters Query request", errors);
		}

		List<ExpectedEncounter> defaultedEncounters = registrarBean
				.getDefaultedExpectedEncounters(new String[] { "ANC" });
		List<ExpectedObs> defaultedObs = registrarBean
				.getDefaultedExpectedObs(new String[] { "TT", "IPT" });

		Care[] upcomingCares = modelConverter.defaultedToWebServiceCares(
				defaultedEncounters, defaultedObs);

		return upcomingCares;
	}

	@WebMethod
	public Care[] queryTTDefaulters(
			@WebParam(name = "staffId") Integer staffId,
			@WebParam(name = "facilityId") Integer facilityId)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		validateChpsId(staffId, errors, "StaffID");
		validateFacility(facilityId, errors, "FacilityID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in TT Defaulters Query request", errors);
		}

		List<ExpectedObs> defaultedObs = registrarBean
				.getDefaultedExpectedObs(new String[] { "TT" });
		return modelConverter.defaultedObsToWebServiceCares(defaultedObs);
	}

	@WebMethod
	public Care[] queryMotherPNCDefaulters(
			@WebParam(name = "staffId") Integer staffId,
			@WebParam(name = "facilityId") Integer facilityId)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		validateChpsId(staffId, errors, "StaffID");
		validateFacility(facilityId, errors, "FacilityID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Mother PNC Defaulters Query request", errors);
		}

		List<ExpectedEncounter> defaultedEncounters = registrarBean
				.getDefaultedExpectedEncounters(new String[] { "PNC(mother)" });
		return modelConverter
				.defaultedEncountersToWebServiceCares(defaultedEncounters);
	}

	@WebMethod
	public Care[] queryChildPNCDefaulters(
			@WebParam(name = "staffId") Integer staffId,
			@WebParam(name = "facilityId") Integer facilityId)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		validateChpsId(staffId, errors, "StaffID");
		validateFacility(facilityId, errors, "FacilityID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Child PNC Defaulters Query request", errors);
		}

		List<ExpectedEncounter> defaultedEncounters = registrarBean
				.getDefaultedExpectedEncounters(new String[] { "PNC(baby)" });
		return modelConverter
				.defaultedEncountersToWebServiceCares(defaultedEncounters);
	}

	@WebMethod
	public Care[] queryCWCDefaulters(
			@WebParam(name = "staffId") Integer staffId,
			@WebParam(name = "facilityId") Integer facilityId)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		validateChpsId(staffId, errors, "StaffID");
		validateFacility(facilityId, errors, "FacilityID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in CWC Defaulters Query request", errors);
		}

		List<ExpectedObs> defaultedObs = registrarBean
				.getDefaultedExpectedObs(new String[] { "OPV", "BCG", "Penta",
						"YellowFever", "Measles", "VitaA", "IPTI" });
		return modelConverter.defaultedObsToWebServiceCares(defaultedObs);
	}

	@WebMethod
	public Patient[] queryUpcomingDeliveries(
			@WebParam(name = "staffId") Integer staffId,
			@WebParam(name = "facilityId") Integer facilityId)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		validateChpsId(staffId, errors, "StaffID");
		validateFacility(facilityId, errors, "FacilityID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Upcoming Deliveries Query request", errors);
		}

		List<Obs> dueDates = registrarBean.getUpcomingPregnanciesDueDate();
		return modelConverter.dueDatesToWebServicePatients(dueDates);
	}

	@WebMethod
	public Patient[] queryRecentDeliveries(
			@WebParam(name = "staffId") Integer staffId,
			@WebParam(name = "facilityId") Integer facilityId)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		validateChpsId(staffId, errors, "StaffID");
		validateFacility(facilityId, errors, "FacilityID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Recent Deliveries Query request", errors);
		}

		List<Encounter> deliveries = registrarBean.getRecentDeliveries();
		return modelConverter.deliveriesToWebServicePatients(deliveries);
	}

	@WebMethod
	public Patient[] queryOverdueDeliveries(
			@WebParam(name = "staffId") Integer staffId,
			@WebParam(name = "facilityId") Integer facilityId)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		validateChpsId(staffId, errors, "StaffID");
		validateFacility(facilityId, errors, "FacilityID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Overdue Deliveries Query request", errors);
		}

		List<Obs> dueDates = registrarBean.getOverduePregnanciesDueDate();
		return modelConverter.dueDatesToWebServicePatients(dueDates);
	}

	@WebMethod
	public Patient queryUpcomingCare(
			@WebParam(name = "staffId") Integer staffId,
			@WebParam(name = "facilityId") Integer facilityId,
			@WebParam(name = "motechId") Integer motechId)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		validateChpsId(staffId, errors, "StaffID");
		validateFacility(facilityId, errors, "FacilityID");

		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID");

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
				upcomingEncounters, upcomingObs, true);

		wsPatient.setCares(upcomingCares);
		return wsPatient;
	}

	@WebMethod
	public Patient[] queryMotechId(@WebParam(name = "staffId") Integer staffId,
			@WebParam(name = "facilityId") Integer facilityId,
			@WebParam(name = "firstName") String firstName,
			@WebParam(name = "lastName") String lastName,
			@WebParam(name = "preferredName") String preferredName,
			@WebParam(name = "birthDate") Date birthDate,
			@WebParam(name = "nhis") String nhis,
			@WebParam(name = "phoneNumber") Integer phoneNumber)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		validateChpsId(staffId, errors, "StaffID");
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

	@WebMethod
	public Patient queryPatient(@WebParam(name = "staffId") Integer staffId,
			@WebParam(name = "facilityId") Integer facilityId,
			@WebParam(name = "motechId") Integer motechId)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		validateChpsId(staffId, errors, "StaffID");
		validateFacility(facilityId, errors, "FacilityID");

		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException("Errors in Patient Query request",
					errors);
		}

		return modelConverter.patientToWebService(patient, false);
	}

	@WebMethod
	public String[] getPatientEnrollments(
			@WebParam(name = "motechId") Integer motechId)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Get Patient Enrollments request", errors);
		}

		return registrarBean.getActiveMessageProgramEnrollmentNames(patient);
	}

	@WebMethod
	public void log(@WebParam(name = "type") LogType type,
			@WebParam(name = "message") String message) {

		log.info("Logtype: " + type + ", Message: " + message);
	}

	@WebMethod
	public void setMessageStatus(
			@WebParam(name = "messageId") String messageId,
			@WebParam(name = "success") Boolean success) {

		registrarBean.setMessageStatus(messageId, success);
	}

	@WebMethod(exclude = true)
	public void setRegistrarBean(RegistrarBean registrarBean) {
		this.registrarBean = registrarBean;
	}

	@WebMethod(exclude = true)
	public void setOpenmrsBean(OpenmrsBean openmrsBean) {
		this.openmrsBean = openmrsBean;
	}

	@WebMethod(exclude = true)
	public void setModelConverter(WebServiceModelConverter modelConverter) {
		this.modelConverter = modelConverter;
	}

	private User validateChpsId(Integer staffId, ValidationErrors errors,
			String fieldName) {
		if (staffId == null) {
			errors.add(3, fieldName);
			return null;
		}
		User nurse = openmrsBean.getNurseByCHPSId(staffId.toString());
		if (nurse == null) {
			errors.add(1, fieldName);
		}
		return nurse;
	}

	private org.openmrs.Patient validateMotechId(Integer motechId,
			ValidationErrors errors, String fieldName) {
		if (motechId == null) {
			errors.add(3, fieldName);
			return null;
		}
		org.openmrs.Patient patient = openmrsBean.getPatientByMotechId(motechId
				.toString());
		if (patient == null) {
			errors.add(1, fieldName);
		}
		return patient;
	}

	private Facility validateFacility(Integer facilityId,
			ValidationErrors errors, String fieldName) {
		if (facilityId == null) {
			errors.add(3, fieldName);
			return null;
		}
		Facility facility = registrarBean.getFacilityById(facilityId);
		if (facility == null) {
			errors.add(1, fieldName);
		}
		return facility;
	}
}
