package org.motechproject.server.ws;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.model.ExpectedEncounter;
import org.motechproject.server.model.ExpectedObs;
import org.motechproject.server.svc.BirthOutcomeChild;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.ws.BirthOutcome;
import org.motechproject.ws.Care;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.DeliveredBy;
import org.motechproject.ws.Gender;
import org.motechproject.ws.HIVStatus;
import org.motechproject.ws.LogType;
import org.motechproject.ws.Patient;
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
	WebServiceModelConverter modelConverter;

	@WebMethod
	public void recordMotherANCVisit(@WebParam(name = "chpsId") String chpsId,
			@WebParam(name = "date") Date date,
			@WebParam(name = "motechId") String motechId,
			@WebParam(name = "visitNumber") Integer visitNumber,
			@WebParam(name = "ttDose") Integer ttDose,
			@WebParam(name = "iptDose") Integer iptDose,
			@WebParam(name = "itnUse") Boolean itnUse,
			@WebParam(name = "hivStatus") HIVStatus hivStatus)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User nurse = validateChpsId(chpsId, errors, "CHPSID");
		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Record Mother ANC Visit request", errors);
		}

		registrarBean.recordMotherANCVisit(nurse, date, patient, visitNumber,
				ttDose, iptDose, itnUse, hivStatus);
	}

	@WebMethod
	public void recordPregnancyTermination(
			@WebParam(name = "chpsId") String chpsId,
			@WebParam(name = "date") Date date,
			@WebParam(name = "motechId") String motechId,
			@WebParam(name = "abortionType") Integer abortionType,
			@WebParam(name = "complication") Integer complication)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User nurse = validateChpsId(chpsId, errors, "CHPSID");
		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Record Pregnancy Termination request", errors);
		}

		registrarBean.recordPregnancyTermination(nurse, date, patient,
				abortionType, complication);
	}

	@WebMethod
	public void recordPregnancyDelivery(
			@WebParam(name = "chpsId") String chpsId,
			@WebParam(name = "date") Date date,
			@WebParam(name = "motechId") String motechId,
			@WebParam(name = "method") Integer method,
			@WebParam(name = "outcome") Integer outcome,
			@WebParam(name = "location") Integer location,
			@WebParam(name = "deliveredBy") DeliveredBy deliveredBy,
			@WebParam(name = "maternalDeath") Boolean maternalDeath,
			@WebParam(name = "cause") Integer cause,
			@WebParam(name = "child1Outcome") BirthOutcome child1Outcome,
			@WebParam(name = "child1MotechId") String child1MotechId,
			@WebParam(name = "child1Sex") Gender child1Sex,
			@WebParam(name = "child1FirstName") String child1FirstName,
			@WebParam(name = "child1OPV") Boolean child1OPV,
			@WebParam(name = "child1BCG") Boolean child1BCG,
			@WebParam(name = "child2Outcome") BirthOutcome child2Outcome,
			@WebParam(name = "child2MotechId") String child2MotechId,
			@WebParam(name = "child2Sex") Gender child2Sex,
			@WebParam(name = "child2FirstName") String child2FirstName,
			@WebParam(name = "child2OPV") Boolean child2OPV,
			@WebParam(name = "child2BCG") Boolean child2BCG,
			@WebParam(name = "child3Outcome") BirthOutcome child3Outcome,
			@WebParam(name = "child3MotechId") String child3MotechId,
			@WebParam(name = "child3Sex") Gender child3Sex,
			@WebParam(name = "child3FirstName") String child3FirstName,
			@WebParam(name = "child3OPV") Boolean child3OPV,
			@WebParam(name = "child3BCG") Boolean child3BCG)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User nurse = validateChpsId(chpsId, errors, "CHPSID");
		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Record Pregnancy Delivery request", errors);
		}

		List<BirthOutcomeChild> outcomes = new ArrayList<BirthOutcomeChild>();
		if (child1Outcome != null && child1MotechId != null
				&& child1Sex != null) {
			outcomes.add(new BirthOutcomeChild(child1Outcome, child1MotechId,
					child1Sex, child1FirstName, child1OPV, child1BCG));
		}
		if (child2Outcome != null && child2MotechId != null
				&& child2Sex != null) {
			outcomes.add(new BirthOutcomeChild(child2Outcome, child2MotechId,
					child2Sex, child2FirstName, child2OPV, child2BCG));
		}
		if (child3Outcome != null && child3MotechId != null
				&& child3Sex != null) {
			outcomes.add(new BirthOutcomeChild(child3Outcome, child3MotechId,
					child3Sex, child3FirstName, child3OPV, child3BCG));
		}

		registrarBean.recordPregnancyDelivery(nurse, date, patient, method,
				outcome, location, deliveredBy, maternalDeath, cause, outcomes);
	}

	@WebMethod
	public void recordMotherPPCVisit(@WebParam(name = "chpsId") String chpsId,
			@WebParam(name = "date") Date date,
			@WebParam(name = "motechId") String motechId,
			@WebParam(name = "visitNumber") Integer visitNumber,
			@WebParam(name = "vitaminA") Boolean vitaminA,
			@WebParam(name = "ttDose") Integer ttDose)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User nurse = validateChpsId(chpsId, errors, "CHPSID");
		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Record Mother PPC Visit request", errors);
		}

		registrarBean.recordMotherPPCVisit(nurse, date, patient, visitNumber,
				vitaminA, ttDose);
	}

	@WebMethod
	public void recordDeath(@WebParam(name = "chpsId") String chpsId,
			@WebParam(name = "date") Date date,
			@WebParam(name = "motechId") String motechId,
			@WebParam(name = "cause") Integer cause) throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User nurse = validateChpsId(chpsId, errors, "CHPSID");
		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException("Errors in Record Death request",
					errors);
		}

		registrarBean.recordDeath(nurse, date, patient, cause);
	}

	@WebMethod
	public void recordChildPNCVisit(@WebParam(name = "chpsId") String chpsId,
			@WebParam(name = "date") Date date,
			@WebParam(name = "motechId") String motechId,
			@WebParam(name = "bcg") Boolean bcg,
			@WebParam(name = "opvDose") Integer opvDose,
			@WebParam(name = "pentaDose") Integer pentaDose,
			@WebParam(name = "yellowFever") Boolean yellowFever,
			@WebParam(name = "csm") Boolean csm,
			@WebParam(name = "measles") Boolean measles,
			@WebParam(name = "ipti") Boolean ipti,
			@WebParam(name = "vitaminA") Boolean vitaminA)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User nurse = validateChpsId(chpsId, errors, "CHPSID");
		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Record Child PNC Visit request", errors);
		}

		registrarBean.recordChildPNCVisit(nurse, date, patient, bcg, opvDose,
				pentaDose, yellowFever, csm, measles, ipti, vitaminA);
	}

	@WebMethod
	public void registerChild(@WebParam(name = "chpsId") String chpsId,
			@WebParam(name = "motherMotechId") String motherMotechId,
			@WebParam(name = "childMotechId") String childMotechId,
			@WebParam(name = "birthDate") Date birthDate,
			@WebParam(name = "sex") Gender sex,
			@WebParam(name = "firstName") String firstName,
			@WebParam(name = "nhis") String nhis,
			@WebParam(name = "nhisExpires") Date nhisExpires)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User nurse = validateChpsId(chpsId, errors, "CHPSID");
		org.openmrs.Patient mother = validateMotechId(motherMotechId, errors,
				"MotherMotechID");

		org.openmrs.Patient child = registrarBean
				.getPatientByMotechId(childMotechId);
		if (child != null) {
			errors.add(2, "ChildMotechID");
		}

		Calendar dobCal = new GregorianCalendar();
		int origYear = dobCal.get(Calendar.YEAR);
		dobCal.set(Calendar.YEAR, origYear - 5);
		if (birthDate.before(dobCal.getTime())) {
			errors.add(2, "DoB");
		}

		if (errors.getErrors().size() > 0) {
			throw new ValidationException("Errors in Register Child request",
					errors);
		}

		registrarBean.registerChild(nurse, mother, childMotechId, birthDate,
				sex, firstName, nhis, nhisExpires);
	}

	@WebMethod
	public void editPatient(
			@WebParam(name = "chpsId") String chpsId,
			@WebParam(name = "motechId") String motechId,
			@WebParam(name = "primaryPhone") String primaryPhone,
			@WebParam(name = "primaryPhoneType") ContactNumberType primaryPhoneType,
			@WebParam(name = "secondaryPhone") String secondaryPhone,
			@WebParam(name = "secondaryPhoneType") ContactNumberType secondaryPhoneType,
			@WebParam(name = "nhis") String nhis,
			@WebParam(name = "nhisExpires") Date nhisExpires)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User nurse = validateChpsId(chpsId, errors, "CHPSID");
		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException("Errors in Edit Patient request",
					errors);
		}

		registrarBean.editPatient(nurse, patient, primaryPhone,
				primaryPhoneType, secondaryPhone, secondaryPhoneType, nhis,
				nhisExpires);
	}

	@WebMethod
	public void stopPregnancyProgram(@WebParam(name = "chpsId") String chpsId,
			@WebParam(name = "motechId") String motechId)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User nurse = validateChpsId(chpsId, errors, "CHPSID");
		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Stop Pregnancy Program request", errors);
		}

		registrarBean.stopPregnancyProgram(nurse, patient);
	}

	@WebMethod
	public void recordGeneralVisit(@WebParam(name = "chpsId") String chpsId,
			@WebParam(name = "date") Date date,
			@WebParam(name = "serialNumber") String serialNumber,
			@WebParam(name = "sex") Gender sex,
			@WebParam(name = "birthDate") Date birthDate,
			@WebParam(name = "insured") Boolean insured,
			@WebParam(name = "newCase") Boolean newCase,
			@WebParam(name = "diagnosis") Integer diagnosis,
			@WebParam(name = "secondDiagnosis") Integer secondDiagnosis,
			@WebParam(name = "referral") Boolean referral)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		validateChpsId(chpsId, errors, "CHPSID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException("Errors in General Visit request",
					errors);
		}

		registrarBean.recordGeneralVisit(chpsId, date, serialNumber, sex,
				birthDate, insured, newCase, diagnosis, secondDiagnosis,
				referral);
	}

	@WebMethod
	public void recordChildVisit(@WebParam(name = "chpsId") String chpsId,
			@WebParam(name = "date") Date date,
			@WebParam(name = "serialNumber") String serialNumber,
			@WebParam(name = "motechId") String motechId,
			@WebParam(name = "newCase") Boolean newCase,
			@WebParam(name = "diagnosis") Integer diagnosis,
			@WebParam(name = "secondDiagnosis") Integer secondDiagnosis,
			@WebParam(name = "referral") Boolean referral)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User nurse = validateChpsId(chpsId, errors, "CHPSID");
		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Record Child Visit request", errors);
		}

		registrarBean.recordChildVisit(nurse, date, patient, serialNumber,
				newCase, diagnosis, secondDiagnosis, referral);
	}

	@WebMethod
	public void recordMotherVisit(@WebParam(name = "chpsId") String chpsId,
			@WebParam(name = "date") Date date,
			@WebParam(name = "serialNumber") String serialNumber,
			@WebParam(name = "motechId") String motechId,
			@WebParam(name = "newCase") Boolean newCase,
			@WebParam(name = "diagnosis") Integer diagnosis,
			@WebParam(name = "secondDiagnosis") Integer secondDiagnosis,
			@WebParam(name = "referral") Boolean referral)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User nurse = validateChpsId(chpsId, errors, "CHPSID");
		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Record Mother Visit request", errors);
		}

		registrarBean.recordMotherVisit(nurse, date, patient, serialNumber,
				newCase, diagnosis, secondDiagnosis, referral);
	}

	@WebMethod
	public Care[] queryANCDefaulters(
			@WebParam(name = "facilityId") String facilityId,
			@WebParam(name = "chpsId") String chpsId)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		validateChpsId(chpsId, errors, "CHPSID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in ANC Defaulters Query request", errors);
		}

		List<ExpectedEncounter> defaultedEncounters = registrarBean
				.getDefaultedExpectedEncounters(new String[] { "ANC" });
		Care[] encounterCares = modelConverter
				.defaultedEncountersToWebServiceCares(defaultedEncounters);

		List<ExpectedObs> defaultedObs = registrarBean
				.getDefaultedExpectedObs(new String[] { "TT", "IPT" });
		Care[] obsCares = modelConverter
				.defaultedObsToWebServiceCares(defaultedObs);

		Care[] upcomingCares = (Care[]) ArrayUtils.addAll(encounterCares,
				obsCares);
		return upcomingCares;
	}

	@WebMethod
	public Care[] queryTTDefaulters(
			@WebParam(name = "facilityId") String facilityId,
			@WebParam(name = "chpsId") String chpsId)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		validateChpsId(chpsId, errors, "CHPSID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in TT Defaulters Query request", errors);
		}

		List<ExpectedObs> defaultedObs = registrarBean
				.getDefaultedExpectedObs(new String[] { "TT" });
		return modelConverter.defaultedObsToWebServiceCares(defaultedObs);
	}

	@WebMethod
	public Care[] queryPPCDefaulters(
			@WebParam(name = "facilityId") String facilityId,
			@WebParam(name = "chpsId") String chpsId)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		validateChpsId(chpsId, errors, "CHPSID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in PPC Defaulters Query request", errors);
		}

		List<ExpectedEncounter> defaultedEncounters = registrarBean
				.getDefaultedExpectedEncounters(new String[] { "PPC" });
		return modelConverter
				.defaultedEncountersToWebServiceCares(defaultedEncounters);
	}

	@WebMethod
	public Care[] queryPNCDefaulters(
			@WebParam(name = "facilityId") String facilityId,
			@WebParam(name = "chpsId") String chpsId)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		validateChpsId(chpsId, errors, "CHPSID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in PNC Defaulters Query request", errors);
		}

		List<ExpectedEncounter> defaultedEncounters = registrarBean
				.getDefaultedExpectedEncounters(new String[] { "PNC" });
		return modelConverter
				.defaultedEncountersToWebServiceCares(defaultedEncounters);
	}

	@WebMethod
	public Care[] queryCWCDefaulters(
			@WebParam(name = "facilityId") String facilityId,
			@WebParam(name = "chpsId") String chpsId)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		validateChpsId(chpsId, errors, "CHPSID");

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
			@WebParam(name = "facilityId") String facilityId,
			@WebParam(name = "chpsId") String chpsId)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		validateChpsId(chpsId, errors, "CHPSID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Upcoming Deliveries Query request", errors);
		}

		List<Obs> dueDates = registrarBean.getUpcomingPregnanciesDueDate();
		return modelConverter.dueDatesToWebServicePatients(dueDates);
	}

	@WebMethod
	public Patient[] queryRecentDeliveries(
			@WebParam(name = "facilityId") String facilityId,
			@WebParam(name = "chpsId") String chpsId)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		validateChpsId(chpsId, errors, "CHPSID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Recent Deliveries Query request", errors);
		}

		List<Encounter> deliveries = registrarBean.getRecentDeliveries();
		return modelConverter.deliveriesToWebServicePatients(deliveries);
	}

	@WebMethod
	public Patient[] queryOverdueDeliveries(
			@WebParam(name = "facilityId") String facilityId,
			@WebParam(name = "chpsId") String chpsId)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		validateChpsId(chpsId, errors, "CHPSID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Overdue Deliveries Query request", errors);
		}

		List<Obs> dueDates = registrarBean.getOverduePregnanciesDueDate();
		return modelConverter.dueDatesToWebServicePatients(dueDates);
	}

	@WebMethod
	public Patient queryUpcomingCare(
			@WebParam(name = "facilityId") String facilityId,
			@WebParam(name = "chpsId") String chpsId,
			@WebParam(name = "motechId") String motechId)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		validateChpsId(chpsId, errors, "CHPSID");

		org.openmrs.Patient patient = validateMotechId(motechId, errors,
				"MotechID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Upcoming Care Query request", errors);
		}

		Patient wsPatient = modelConverter.patientToWebService(patient, true);

		List<ExpectedEncounter> upcomingEncounters = registrarBean
				.getUpcomingExpectedEncounters(patient);
		Care[] expectedEncounterCares = modelConverter
				.upcomingEncountersToWebServiceCares(upcomingEncounters);

		List<ExpectedObs> upcomingObs = registrarBean
				.getUpcomingExpectedObs(patient);
		Care[] expectedObsCares = modelConverter
				.upcomingObsToWebServiceCares(upcomingObs);

		Care[] upcomingCares = (Care[]) ArrayUtils.addAll(
				expectedEncounterCares, expectedObsCares);

		Arrays.sort(upcomingCares, new CareDateComparator());
		wsPatient.setCares(upcomingCares);

		return wsPatient;
	}

	@WebMethod
	public Patient[] queryMotechId(@WebParam(name = "chpsId") String chpsId,
			@WebParam(name = "firstName") String firstName,
			@WebParam(name = "lastName") String lastName,
			@WebParam(name = "preferredName") String preferredName,
			@WebParam(name = "birthDate") Date birthDate,
			@WebParam(name = "nhis") String nhis,
			@WebParam(name = "phoneNumber") String phoneNumber)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		validateChpsId(chpsId, errors, "CHPSID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException("Errors in MotechID Query request",
					errors);
		}

		List<org.openmrs.Patient> patients = registrarBean.getPatients(
				firstName, lastName, preferredName, birthDate, null,
				phoneNumber, nhis);
		return modelConverter.patientToWebService(patients, true);
	}

	@WebMethod
	public Patient queryPatient(@WebParam(name = "chpsId") String chpsId,
			@WebParam(name = "motechId") String motechId)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		validateChpsId(chpsId, errors, "CHPSID");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException("Errors in Patient Query request",
					errors);
		}

		org.openmrs.Patient patient = registrarBean
				.getPatientByMotechId(motechId);
		return modelConverter.patientToWebService(patient, false);
	}

	@WebMethod
	public void log(@WebParam(name = "type") LogType type,
			@WebParam(name = "message") String message) {

		registrarBean.log(type, message);
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
	public void setModelConverter(WebServiceModelConverter modelConverter) {
		this.modelConverter = modelConverter;
	}

	private User validateChpsId(String chpsId, ValidationErrors errors,
			String fieldName) {
		User nurse = registrarBean.getNurseByCHPSId(chpsId);
		if (nurse == null) {
			errors.add(1, fieldName);
		}
		return nurse;
	}

	private org.openmrs.Patient validateMotechId(String motechId,
			ValidationErrors errors, String fieldName) {
		org.openmrs.Patient patient = registrarBean
				.getPatientByMotechId(motechId);
		if (patient == null) {
			errors.add(1, fieldName);
		}
		return patient;
	}

}
