package org.motech.ws;

import java.util.Date;

import javax.ejb.Local;

import org.motech.model.Gender;

@Local
public interface RegistrarWS {

	public void registerMother(String nursePhoneNumber, Date date,
			String serialId, String name, String community, String location,
			Date dateOfBirth, Integer nhis, String phoneNumber, Date dueDate,
			Integer parity, Integer hemoglobin);

	public void registerNurse(String name, String phoneNumber, String clinic);

	public void registerPatient(String nursePhoneNumber, String serialId,
			String name, String community, String location, Date dateOfBirth,
			Gender gender, Integer nhis, String phoneNumber);

	public void registerPregnancy(String nursePhoneNumber, Date date,
			String serialId, Date dueDate, Integer parity, Integer hemoglobin);

	public void recordMaternalVisit(String nursePhoneNumber, Date date,
			String serialId, Integer tetanus, Integer ipt, Integer itn,
			Integer visitNumber, Integer onARV, Integer prePMTCT,
			Integer testPMTCT, Integer postPMTCT, Integer hemoglobinAt36Weeks);

}