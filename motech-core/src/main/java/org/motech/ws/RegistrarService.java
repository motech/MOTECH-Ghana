package org.motech.ws;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.motech.model.Gender;
import org.motech.model.LogType;
import org.motech.svc.Logger;
import org.motech.svc.Registrar;

@Stateless
@WebService(name = "Registrar", serviceName = "RegistrarService", portName = "RegistrarPort", targetNamespace = "http://motech.org/")
public class RegistrarService implements RegistrarWS {

	@EJB
	Registrar registrationBean;

	@EJB
	Logger loggerBean;

	@WebMethod
	public void registerMother(
			@WebParam(name = "nursePhoneNumber") String nursePhoneNumber,
			@WebParam(name = "date") Date date,
			@WebParam(name = "serialId") String serialId,
			@WebParam(name = "name") String name,
			@WebParam(name = "community") String community,
			@WebParam(name = "location") String location,
			@WebParam(name = "dateOfBirth") Date dateOfBirth,
			@WebParam(name = "nhis") Integer nhis,
			@WebParam(name = "phoneNumber") String phoneNumber,
			@WebParam(name = "dueDate") Date dueDate,
			@WebParam(name = "parity") Integer parity,
			@WebParam(name = "hemoglobin") Integer hemoglobin) {

		registrationBean.registerMother(nursePhoneNumber, date, serialId, name,
				community, location, dateOfBirth, nhis, phoneNumber, dueDate,
				parity, hemoglobin);
	}

	@WebMethod
	public void registerClinic(@WebParam(name = "name") String name) {
		registrationBean.registerClinic(name);
	}

	@WebMethod
	public void registerNurse(@WebParam(name = "name") String name,
			@WebParam(name = "phoneNumber") String phoneNumber,
			@WebParam(name = "clinic") String clinic) {

		registrationBean.registerNurse(name, phoneNumber, clinic);
	}

	@WebMethod
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

		registrationBean.registerPatient(nursePhoneNumber, serialId, name,
				community, location, dateOfBirth, gender, nhis, phoneNumber);
	}

	@WebMethod
	public void registerPregnancy(
			@WebParam(name = "nursePhoneNumber") String nursePhoneNumber,
			@WebParam(name = "date") Date date,
			@WebParam(name = "serialId") String serialId,
			@WebParam(name = "dueDate") Date dueDate,
			@WebParam(name = "parity") Integer parity,
			@WebParam(name = "hemoglobin") Integer hemoglobin) {

		registrationBean.registerPregnancy(nursePhoneNumber, date, serialId,
				dueDate, parity, hemoglobin);
	}

	@WebMethod
	public void recordMaternalVisit(
			@WebParam(name = "nursePhoneNumber") String nursePhoneNumber,
			@WebParam(name = "date") Date date,
			@WebParam(name = "serialId") String serialId,
			@WebParam(name = "tetanus") Integer tetanus,
			@WebParam(name = "ipt") Integer ipt,
			@WebParam(name = "itn") Integer itn,
			@WebParam(name = "visitNumber") Integer visitNumber,
			@WebParam(name = "onARV") Integer onARV,
			@WebParam(name = "prePMTCT") Integer prePMTCT,
			@WebParam(name = "testPMTCT") Integer testPMTCT,
			@WebParam(name = "postPMTCT") Integer postPMTCT,
			@WebParam(name = "hemoglobinAt36Weeks") Integer hemoglobinAt36Weeks) {

		registrationBean.recordMaternalVisit(nursePhoneNumber, date, serialId,
				tetanus, ipt, itn, visitNumber, onARV, prePMTCT, testPMTCT,
				postPMTCT, hemoglobinAt36Weeks);
	}

	@WebMethod
	public void log(@WebParam(name = "type") LogType type,
			@WebParam(name = "message") String message) {

		loggerBean.log(type, message);
	}
}
