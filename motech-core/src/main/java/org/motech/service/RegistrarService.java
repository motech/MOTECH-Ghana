package org.motech.service;

import java.util.Date;

import javax.ejb.EJB;
import javax.jws.WebMethod;
import javax.jws.WebService;

import org.motech.ejb.Registrar;

@WebService
public class RegistrarService {

	@EJB
	Registrar registrationBean;

	@WebMethod
	public void registerMother(String nursePhoneNumber, Date date,
			String serialId, String name, String community, String location,
			Integer age, Integer nhis, String phoneNumber, Date dueDate,
			Integer parity, Integer hemoglobin) {

		registrationBean.registerMother(nursePhoneNumber, date, serialId, name,
				community, location, age, nhis, phoneNumber, dueDate, parity,
				hemoglobin);
	}

	@WebMethod
	public void registerNurse(String name, String phoneNumber, String clinic) {

		registrationBean.registerNurse(name, phoneNumber, clinic);
	}

	@WebMethod
	public void registerPatient(String nursePhoneNumber, String serialId,
			String name, String community, String location, Integer age,
			String gender, Integer nhis, String phoneNumber) {

		registrationBean.registerPatient(nursePhoneNumber, serialId, name,
				community, location, age, gender, nhis, phoneNumber);
	}

	@WebMethod
	public void registerPregnancy(String nursePhoneNumber, Date date,
			String serialId, Date dueDate, Integer parity, Integer hemoglobin) {

		registrationBean.registerPregnancy(nursePhoneNumber, date, serialId,
				dueDate, parity, hemoglobin);
	}

	@WebMethod
	public void recordMaternalVisit(String nursePhoneNumber, Date date,
			String serialId, Integer tetanus, Integer ipt, Integer itn,
			Integer visitNumber, Integer onARV, Integer prePMTCT,
			Integer testPMTCT, Integer postPMTCT, Integer hemoglobinAt36Weeks) {

		registrationBean.recordMaternalVisit(nursePhoneNumber, date, serialId,
				tetanus, ipt, itn, visitNumber, onARV, prePMTCT, testPMTCT,
				postPMTCT, hemoglobinAt36Weeks);
	}

}
