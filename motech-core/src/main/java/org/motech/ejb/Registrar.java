package org.motech.ejb;

import java.util.Date;

import javax.ejb.Local;

@Local
public interface Registrar {

	void registerMother(String nursePhoneNumber, String serialId, String name,
			String community, String location, Integer age, Integer nhis,
			Date dueDate, Integer parity, Integer hemoglobin);

	void registerNurse(String name, String phoneNumber, String clinic);

	void registerPatient(String nursePhoneNumber, String serialId, String name,
			String community, String location, Integer age, String gender,
			Integer nhis);

	void registerPregnancy(String nursePhoneNumber, String serialId,
			Date dueDate, Integer parity, Integer hemoglobin);

	void recordMaternalVisit(String nursePhoneNumber, String serialId,
			Integer tetanus, Integer ipt, Integer itn, Integer visitNumber,
			Integer onARV, Integer prePMTCT, Integer testPMTCT,
			Integer postPMTCT, Integer hemoglobinAt36Weeks);
}
