package org.motech.svc;

import java.util.Date;

import org.motech.model.Gender;
import org.motech.model.NotificationType;
import org.motech.model.PhoneType;

public interface Registrar {

	void registerMother(String nursePhoneNumber, Date date, String serialId,
			String name, String community, String location, Date dateOfBirth,
			Integer nhis, String phoneNumber, PhoneType phoneType,
			String language, NotificationType notificationType, Date dueDate,
			Integer parity, Integer hemoglobin);

	void registerClinic(String name);

	void registerNurse(String name, String phoneNumber, String clinic);

	void registerPatient(String nursePhoneNumber, String serialId, String name,
			String community, String location, Date dateOfBirth, Gender gender,
			Integer nhis, String phoneNumber, PhoneType phoneType,
			String language, NotificationType notificationType);

	void registerPregnancy(String nursePhoneNumber, Date date, String serialId,
			Date dueDate, Integer parity, Integer hemoglobin);

	void recordMaternalVisit(String nursePhoneNumber, Date date,
			String serialId, Integer tetanus, Integer ipt, Integer itn,
			Integer visitNumber, Integer onARV, Integer prePMTCT,
			Integer testPMTCT, Integer postPMTCT, Integer hemoglobinAt36Weeks);
}
