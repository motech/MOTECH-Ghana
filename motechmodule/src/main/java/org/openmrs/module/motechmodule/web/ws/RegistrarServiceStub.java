package org.openmrs.module.motechmodule.web.ws;

import java.util.Date;

import javax.jws.WebParam;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motech.model.Gender;
import org.motech.model.LogType;

public class RegistrarServiceStub implements RegistrarService {

	protected final Log log = LogFactory.getLog(RegistrarServiceStub.class);

	public void registerClinic(@WebParam(name = "name") String name) {
		log.info("Save Clinic: \n" + "------------------------------ \n"
				+ "Name: " + name);
	}

	public void registerNurse(@WebParam(name = "name") String name,
			@WebParam(name = "phoneNumber") String phoneNumber,
			@WebParam(name = "clinic") String clinic) {
		log.info("Save Nurse: \n" + "------------------------------ \n"
				+ "Name: " + name + " \n" + "Phone Number: " + phoneNumber
				+ " \n" + "Clinic Name: " + clinic);
	}

	public void registerPatient(
			@WebParam(name = "nursePhoneNumber") String nursePhoneNumber,
			@WebParam(name = "serialId") String serialId,
			@WebParam(name = "name") String name,
			@WebParam(name = "community") String community,
			@WebParam(name = "location") String location,
			@WebParam(name = "dateOfBirth") Date dateOfBirth,
			@WebParam(name = "gender") Gender gender,
			@WebParam(name = "nhis") Integer nhis,
			@WebParam(name = "phoneNumber") String phoneNumber) {
		log.info("Save Patient: \n" + "------------------------------ \n"
				+ "Nurse Phone Number: "
				+ nursePhoneNumber
				+ " \n"
				+ "Patient Id: "
				+ serialId
				+ " \n"
				+ "Patient Name: "
				+ name
				+ " \n"
				+ "Community: "
				+ community
				+ " \n"
				+ "Location: "
				+ location
				+ " \n"
				+ "Birth Date: "
				+ dateOfBirth
				+ " \n"
				+ "Gender: "
				+ gender
				+ " \n"
				+ "NHIS: "
				+ nhis + " \n" + "Patient Phone Number: " + phoneNumber);
	}

	public void recordMaternalVisit(
			@WebParam(name = "nursePhoneNumber") String nursePhoneNumber,
			@WebParam(name = "date") Date date,
			@WebParam(name = "serialId") String serialId,
			@WebParam(name = "tetanus") Boolean tetanus,
			@WebParam(name = "ipt") Boolean ipt,
			@WebParam(name = "itn") Boolean itn,
			@WebParam(name = "visitNumber") Integer visitNumber,
			@WebParam(name = "onARV") Boolean onARV,
			@WebParam(name = "prePMTCT") Boolean prePMTCT,
			@WebParam(name = "testPMTCT") Boolean testPMTCT,
			@WebParam(name = "postPMTCT") Boolean postPMTCT,
			@WebParam(name = "hemoglobinAt36Weeks") Double hemoglobinAt36Weeks) {
		log.info("Save Maternal Visit: \n"
				+ "------------------------------ \n" + "Nurse Phone Number: "
				+ nursePhoneNumber
				+ " \n"
				+ "Maternal Visit Date: "
				+ date
				+ " \n"
				+ "Patient Id: "
				+ serialId
				+ " \n"
				+ "Tetanus Given: "
				+ tetanus
				+ " \n"
				+ "IPT Given: "
				+ ipt
				+ " \n"
				+ "ITN Use: "
				+ itn
				+ " \n"
				+ "Visit Number: "
				+ visitNumber
				+ " \n"
				+ "Is On ARV: "
				+ onARV
				+ " \n"
				+ "Pre PMTCT Given: "
				+ prePMTCT
				+ " \n"
				+ "Test PMTCT Given: "
				+ testPMTCT
				+ " \n"
				+ "Post PMTCT Given: "
				+ postPMTCT
				+ " \n"
				+ "Hemoglobin Level at 36 Weeks: " + hemoglobinAt36Weeks);
	}

	public void registerPregnancy(
			@WebParam(name = "nursePhoneNumber") String nursePhoneNumber,
			@WebParam(name = "date") Date date,
			@WebParam(name = "serialId") String serialId,
			@WebParam(name = "dueDate") Date dueDate,
			@WebParam(name = "parity") Integer parity,
			@WebParam(name = "hemoglobin") Double hemoglobin) {
		log.info("Save Pregnancy: \n" + "------------------------------ \n"
				+ "Nurse Phone Number: " + nursePhoneNumber + " \n"
				+ "Pregnancy Registration Date: " + date + " \n"
				+ "Patient Id: " + serialId + " \n" + "Pregnancy Due Date: "
				+ dueDate + " \n" + "Parity: " + parity + " \n"
				+ "Hemoglobin Level: " + hemoglobin);
	}

	public void log(@WebParam(name = "type") LogType type,
			@WebParam(name = "message") String message) {
		log.info("Save Log: \n" + "------------------------------ \n"
				+ "Log Type: " + type + " \n" + "Log Message: " + message);
	}
}
