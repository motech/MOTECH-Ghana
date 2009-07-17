package org.motech.ws;

import java.util.Date;

import javax.jws.WebService;

import org.motech.model.Gender;
import org.motech.model.LogType;
import org.motech.svc.Logger;
import org.motech.svc.Registrar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@WebService(name = "Registrar", serviceName = "RegistrarService", portName = "RegistrarPort", targetNamespace = "http://motech.org/", endpointInterface = "org.motech.ws.RegistrarWS")
@Service
public class RegistrarService implements RegistrarWS {

	@Autowired
	Registrar registrationBean;

	@Autowired
	Logger loggerBean;

	public void registerMother(String nursePhoneNumber, Date date,
			String serialId, String name, String community, String location,
			Date dateOfBirth, Integer nhis, String phoneNumber, Date dueDate,
			Integer parity, Integer hemoglobin) {

		registrationBean.registerMother(nursePhoneNumber, date, serialId, name,
				community, location, dateOfBirth, nhis, phoneNumber, dueDate,
				parity, hemoglobin);
	}

	public void registerClinic(String name) {
		registrationBean.registerClinic(name);
	}

	public void registerNurse(String name, String phoneNumber, String clinic) {

		registrationBean.registerNurse(name, phoneNumber, clinic);
	}

	public void registerPatient(String nursePhoneNumber, String serialId,
			String name, String community, String location, Date dateOfBirth,
			Gender gender, Integer nhis, String phoneNumber) {

		registrationBean.registerPatient(nursePhoneNumber, serialId, name,
				community, location, dateOfBirth, gender, nhis, phoneNumber);
	}

	public void registerPregnancy(String nursePhoneNumber, Date date,
			String serialId, Date dueDate, Integer parity, Integer hemoglobin) {

		registrationBean.registerPregnancy(nursePhoneNumber, date, serialId,
				dueDate, parity, hemoglobin);
	}

	public void recordMaternalVisit(String nursePhoneNumber, Date date,
			String serialId, Integer tetanus, Integer ipt, Integer itn,
			Integer visitNumber, Integer onARV, Integer prePMTCT,
			Integer testPMTCT, Integer postPMTCT, Integer hemoglobinAt36Weeks) {

		registrationBean.recordMaternalVisit(nursePhoneNumber, date, serialId,
				tetanus, ipt, itn, visitNumber, onARV, prePMTCT, testPMTCT,
				postPMTCT, hemoglobinAt36Weeks);
	}

	public void log(LogType type, String message) {

		loggerBean.log(type, message);
	}
}
