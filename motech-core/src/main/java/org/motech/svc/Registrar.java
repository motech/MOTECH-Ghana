package org.motech.svc;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import org.motech.model.Gender;
import org.motech.model.MaternalVisit;
import org.motech.model.Nurse;
import org.motech.model.Patient;
import org.motech.model.Pregnancy;

@Local
public interface Registrar {

	void registerMother(String nursePhoneNumber, Date date, String serialId,
			String name, String community, String location, Date dateOfBirth,
			Integer nhis, String phoneNumber, Date dueDate, Integer parity,
			Integer hemoglobin);

	void registerNurse(String name, String phoneNumber, String clinic);

	void registerPatient(String nursePhoneNumber, String serialId, String name,
			String community, String location, Date dateOfBirth, Gender gender,
			Integer nhis, String phoneNumber);

	void registerPregnancy(String nursePhoneNumber, Date date, String serialId,
			Date dueDate, Integer parity, Integer hemoglobin);

	void recordMaternalVisit(String nursePhoneNumber, Date date,
			String serialId, Integer tetanus, Integer ipt, Integer itn,
			Integer visitNumber, Integer onARV, Integer prePMTCT,
			Integer testPMTCT, Integer postPMTCT, Integer hemoglobinAt36Weeks);

	Nurse getNurse(String phoneNumber);

	List<Nurse> getNurses();

	Patient getPatient(String serialId, Long clinicId);

	List<Patient> getPatients();

	List<Pregnancy> getPregnancies();

	List<MaternalVisit> getMaternalVisits();
}
