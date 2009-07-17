package org.motech.ws;

import java.util.Date;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.motech.model.Gender;
import org.motech.model.LogType;

@WebService(name = "Registrar", serviceName = "RegistrarService", portName = "RegistrarPort", targetNamespace = "http://motech.org/")
public interface RegistrarWS {

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
			@WebParam(name = "hemoglobin") Integer hemoglobin);

	@WebMethod
	public void registerClinic(@WebParam(name = "name") String name);

	@WebMethod
	public void registerNurse(@WebParam(name = "name") String name,
			@WebParam(name = "phoneNumber") String phoneNumber,
			@WebParam(name = "clinic") String clinic);

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
			@WebParam(name = "phoneNumber") String phoneNumber);

	@WebMethod
	public void registerPregnancy(
			@WebParam(name = "nursePhoneNumber") String nursePhoneNumber,
			@WebParam(name = "date") Date date,
			@WebParam(name = "serialId") String serialId,
			@WebParam(name = "dueDate") Date dueDate,
			@WebParam(name = "parity") Integer parity,
			@WebParam(name = "hemoglobin") Integer hemoglobin);

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
			@WebParam(name = "hemoglobinAt36Weeks") Integer hemoglobinAt36Weeks);

	@WebMethod
	public void log(@WebParam(name = "type") LogType type,
			@WebParam(name = "message") String message);

}