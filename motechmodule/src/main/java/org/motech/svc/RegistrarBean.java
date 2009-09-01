package org.motech.svc;

import java.util.Date;

import org.motech.model.Gender;
import org.motech.model.LogType;
import org.motech.model.NotificationType;
import org.motech.model.PhoneType;

public interface RegistrarBean {

	public void registerClinic(String name);

	public void registerNurse(String name, String phoneNumber, String clinic);

	public void registerPatient(String nursePhoneNumber, String serialId,
			String name, String community, String location, Date dateOfBirth,
			Gender gender, Integer nhis, String phoneNumber,
			PhoneType phoneType, String language,
			NotificationType notificationType);

	public void recordMaternalVisit(String nursePhoneNumber, Date date,
			String serialId, Boolean tetanus, Boolean ipt, Boolean itn,
			Integer visitNumber, Boolean onARV, Boolean prePMTCT,
			Boolean testPMTCT, Boolean postPMTCT, Double hemoglobinAt36Weeks);

	public void registerPregnancy(String nursePhoneNumber, Date date,
			String serialId, Date dueDate, Integer parity, Double hemoglobin);

	public void log(LogType type, String message);
}
