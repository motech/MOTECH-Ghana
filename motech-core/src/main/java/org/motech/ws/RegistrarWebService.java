package org.motech.ws;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motech.svc.RegistrarBean;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.Gender;
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
	public void registerChild(@WebParam(name = "chpsId") String chpsId,
			@WebParam(name = "regDate") Date regDate,
			@WebParam(name = "motherRegNum") String motherRegNum,
			@WebParam(name = "childRegNum") String childRegNum,
			@WebParam(name = "childDob") Date childDob,
			@WebParam(name = "childGender") Gender childGender,
			@WebParam(name = "childFirstName") String childFirstName,
			@WebParam(name = "nhis") String nhis,
			@WebParam(name = "nhisExpires") Date nhisExpires)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User nurse = registrarBean.getNurseByCHPSId(chpsId);
		if (nurse == null) {
			errors.add(1, "chpsId");
		}

		Patient mother = registrarBean.getPatientBySerial(motherRegNum);
		if (mother == null) {
			errors.add(1, "motherRegNum");
		}

		Patient child = registrarBean.getPatientBySerial(childRegNum);
		if (child != null) {
			errors.add(2, "childRegNum");
		}

		Calendar dobCal = new GregorianCalendar();
		int origYear = dobCal.get(Calendar.YEAR);
		dobCal.set(Calendar.YEAR, origYear - 5);
		if (childDob.before(dobCal.getTime())) {
			errors.add(2, "childDob");
		}

		if (errors.getErrors().size() > 0) {
			throw new ValidationException("Errors in Register Child request",
					errors);
		}

		registrarBean.registerChild(nurse, regDate, mother, childRegNum,
				childDob, childGender, childFirstName, nhis, nhisExpires);
	}

	@WebMethod
	public void editPatient(
			@WebParam(name = "chpsId") String chpsId,
			@WebParam(name = "patientRegNum") String patientRegNum,
			@WebParam(name = "primaryPhone") String primaryPhone,
			@WebParam(name = "primaryPhoneType") ContactNumberType primaryPhoneType,
			@WebParam(name = "secondaryPhone") String secondaryPhone,
			@WebParam(name = "secondaryPhoneType") ContactNumberType secondaryPhoneType,
			@WebParam(name = "nhis") String nhis,
			@WebParam(name = "nhisExpires") Date nhisExpires)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User nurse = registrarBean.getNurseByCHPSId(chpsId);
		if (nurse == null) {
			errors.add(1, "chpsId");
		}

		Patient patient = registrarBean.getPatientBySerial(patientRegNum);
		if (patient == null) {
			errors.add(1, "patientRegNum");
		}

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
			@WebParam(name = "patientRegNum") String patientRegNum)
			throws ValidationException {

		ValidationErrors errors = new ValidationErrors();

		User nurse = registrarBean.getNurseByCHPSId(chpsId);
		if (nurse == null) {
			errors.add(1, "chpsId");
		}

		Patient patient = registrarBean.getPatientBySerial(patientRegNum);
		if (patient == null) {
			errors.add(1, "patientRegNum");
		}

		if (errors.getErrors().size() > 0) {
			throw new ValidationException(
					"Errors in Stop Pregnancy Program request", errors);
		}

		registrarBean.stopPregnancyProgram(nurse, patient);
	}

	@WebMethod
	public void recordGeneralVisit(
			@WebParam(name = "clinicId") Integer clinicId,
			@WebParam(name = "visitDate") Date visitDate,
			@WebParam(name = "patientSerial") String patientSerial,
			@WebParam(name = "patientGender") Gender patientGender,
			@WebParam(name = "patientBirthDate") Date patientBirthDate,
			@WebParam(name = "patientDiagnosis") Integer patientDiagnosis,
			@WebParam(name = "patientReferral") Boolean patientReferral)
			throws ValidationException {

		if (clinicId == null) {
			ValidationErrors errors = new ValidationErrors();
			errors.add(3, "clinicId");
			throw new ValidationException("Errors in General Visit request",
					errors);
		}

		registrarBean.recordGeneralVisit(clinicId, visitDate, patientSerial,
				patientGender, patientBirthDate, patientDiagnosis,
				patientReferral);
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

}
