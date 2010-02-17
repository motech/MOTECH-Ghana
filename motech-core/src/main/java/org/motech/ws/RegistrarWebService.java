package org.motech.ws;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motech.svc.BirthOutcomeChild;
import org.motech.svc.RegistrarBean;
import org.motechproject.ws.BirthOutcome;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.DeliveredBy;
import org.motechproject.ws.Gender;
import org.motechproject.ws.HIVStatus;
import org.motechproject.ws.LogType;
import org.motechproject.ws.server.RegistrarService;
import org.motechproject.ws.server.ValidationErrors;
import org.motechproject.ws.server.ValidationException;
import org.openmrs.Patient;
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

	@WebMethod
	public void recordMotherANCVisit(@WebParam(name = "chpsId") String chpsId,
			@WebParam(name = "date") Date date,
			@WebParam(name = "patientId") String patientId,
			@WebParam(name = "visitNumber") Integer visitNumber,
			@WebParam(name = "ttDose") Integer ttDose,
			@WebParam(name = "iptDose") Integer iptDose,
			@WebParam(name = "itnUse") Boolean itnUse,
			@WebParam(name = "hivStatus") HIVStatus hivStatus)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User nurse = validateChpsId(chpsId, errors, "chpsId");
		Patient patient = validatePatientId(patientId, errors, "patientId");

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
			@WebParam(name = "patientId") String patientId,
			@WebParam(name = "abortionType") Integer abortionType,
			@WebParam(name = "complication") Integer complication)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User nurse = validateChpsId(chpsId, errors, "chpsId");
		Patient patient = validatePatientId(patientId, errors, "patientId");

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
			@WebParam(name = "patientId") String patientId,
			@WebParam(name = "method") Integer method,
			@WebParam(name = "outcome") Integer outcome,
			@WebParam(name = "location") Integer location,
			@WebParam(name = "deliveredBy") DeliveredBy deliveredBy,
			@WebParam(name = "maternalDeath") Boolean maternalDeath,
			@WebParam(name = "cause") Integer cause,
			@WebParam(name = "child1Outcome") BirthOutcome child1Outcome,
			@WebParam(name = "child1PatientId") String child1PatientId,
			@WebParam(name = "child1Sex") Gender child1Sex,
			@WebParam(name = "child1FirstName") String child1FirstName,
			@WebParam(name = "child1OPV") Boolean child1OPV,
			@WebParam(name = "child1BCG") Boolean child1BCG,
			@WebParam(name = "child2Outcome") BirthOutcome child2Outcome,
			@WebParam(name = "child2PatientId") String child2PatientId,
			@WebParam(name = "child2Sex") Gender child2Sex,
			@WebParam(name = "child2FirstName") String child2FirstName,
			@WebParam(name = "child2OPV") Boolean child2OPV,
			@WebParam(name = "child2BCG") Boolean child2BCG)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User nurse = validateChpsId(chpsId, errors, "chpsId");
		Patient patient = validatePatientId(patientId, errors, "patientId");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Record Pregnancy Delivery request", errors);
		}

		BirthOutcomeChild child1 = new BirthOutcomeChild(child1Outcome,
				child1PatientId, child1Sex, child1FirstName, child1OPV,
				child1BCG);
		BirthOutcomeChild child2 = new BirthOutcomeChild(child2Outcome,
				child2PatientId, child2Sex, child2FirstName, child2OPV,
				child2BCG);
		BirthOutcomeChild[] outcomes = new BirthOutcomeChild[] { child1, child2 };

		registrarBean.recordPregnancyDelivery(nurse, date, patient, method,
				outcome, location, deliveredBy, maternalDeath, cause, outcomes);
	}

	@WebMethod
	public void recordMotherPPCVisit(@WebParam(name = "chpsId") String chpsId,
			@WebParam(name = "date") Date date,
			@WebParam(name = "patientId") String patientId,
			@WebParam(name = "visitNumber") Integer visitNumber,
			@WebParam(name = "vitaminA") Boolean vitaminA,
			@WebParam(name = "ttDose") Integer ttDose)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User nurse = validateChpsId(chpsId, errors, "chpsId");
		Patient patient = validatePatientId(patientId, errors, "patientId");

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
			@WebParam(name = "patientId") String patientId,
			@WebParam(name = "cause") Integer cause) throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User nurse = validateChpsId(chpsId, errors, "chpsId");
		Patient patient = validatePatientId(patientId, errors, "patientId");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException("Errors in Record Death request",
					errors);
		}

		registrarBean.recordDeath(nurse, date, patient, cause);
	}

	@WebMethod
	public void recordChildPNCVisit(@WebParam(name = "chpsId") String chpsId,
			@WebParam(name = "date") Date date,
			@WebParam(name = "patientId") String patientId,
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

		User nurse = validateChpsId(chpsId, errors, "chpsId");
		Patient patient = validatePatientId(patientId, errors, "patientId");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Record Child PNC Visit request", errors);
		}

		registrarBean.recordChildPNCVisit(nurse, date, patient, bcg, opvDose,
				pentaDose, yellowFever, csm, measles, ipti, vitaminA);
	}

	@WebMethod
	public void registerChild(@WebParam(name = "chpsId") String chpsId,
			@WebParam(name = "motherId") String motherId,
			@WebParam(name = "childId") String childId,
			@WebParam(name = "birthDate") Date birthDate,
			@WebParam(name = "sex") Gender sex,
			@WebParam(name = "firstName") String firstName,
			@WebParam(name = "nhis") String nhis,
			@WebParam(name = "nhisExpires") Date nhisExpires)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User nurse = validateChpsId(chpsId, errors, "chpsId");
		Patient mother = validatePatientId(motherId, errors, "motherId");

		Patient child = registrarBean.getPatientBySerial(childId);
		if (child != null) {
			errors.add(2, "childId");
		}

		Calendar dobCal = new GregorianCalendar();
		int origYear = dobCal.get(Calendar.YEAR);
		dobCal.set(Calendar.YEAR, origYear - 5);
		if (birthDate.before(dobCal.getTime())) {
			errors.add(2, "birthDate");
		}

		if (errors.getErrors().size() > 0) {
			throw new ValidationException("Errors in Register Child request",
					errors);
		}

		registrarBean.registerChild(nurse, mother, childId, birthDate, sex,
				firstName, nhis, nhisExpires);
	}

	@WebMethod
	public void editPatient(
			@WebParam(name = "chpsId") String chpsId,
			@WebParam(name = "patientId") String patientId,
			@WebParam(name = "primaryPhone") String primaryPhone,
			@WebParam(name = "primaryPhoneType") ContactNumberType primaryPhoneType,
			@WebParam(name = "secondaryPhone") String secondaryPhone,
			@WebParam(name = "secondaryPhoneType") ContactNumberType secondaryPhoneType,
			@WebParam(name = "nhis") String nhis,
			@WebParam(name = "nhisExpires") Date nhisExpires)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User nurse = validateChpsId(chpsId, errors, "chpsId");
		Patient patient = validatePatientId(patientId, errors, "patientId");

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
			@WebParam(name = "patientId") String patientId)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User nurse = validateChpsId(chpsId, errors, "chpsId");
		Patient patient = validatePatientId(patientId, errors, "patientId");

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

		validateChpsId(chpsId, errors, "chpsId");

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
			@WebParam(name = "patientId") String patientId,
			@WebParam(name = "newCase") Boolean newCase,
			@WebParam(name = "diagnosis") Integer diagnosis,
			@WebParam(name = "secondDiagnosis") Integer secondDiagnosis,
			@WebParam(name = "referral") Boolean referral)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User nurse = validateChpsId(chpsId, errors, "chpsId");
		Patient patient = validatePatientId(patientId, errors, "patientId");

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
			@WebParam(name = "patientId") String patientId,
			@WebParam(name = "newCase") Boolean newCase,
			@WebParam(name = "diagnosis") Integer diagnosis,
			@WebParam(name = "secondDiagnosis") Integer secondDiagnosis,
			@WebParam(name = "referral") Boolean referral)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User nurse = validateChpsId(chpsId, errors, "chpsId");
		Patient patient = validatePatientId(patientId, errors, "patientId");

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Record Mother Visit request", errors);
		}

		registrarBean.recordMotherVisit(nurse, date, patient, serialNumber,
				newCase, diagnosis, secondDiagnosis, referral);
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

	private User validateChpsId(String chpsId, ValidationErrors errors,
			String fieldName) {
		User nurse = registrarBean.getNurseByCHPSId(chpsId);
		if (nurse == null) {
			errors.add(1, fieldName);
		}
		return nurse;
	}

	private Patient validatePatientId(String patientId,
			ValidationErrors errors, String fieldName) {
		Patient patient = registrarBean.getPatientBySerial(patientId);
		if (patient == null) {
			errors.add(1, fieldName);
		}
		return patient;
	}

}
