package org.motech.dao;

import java.util.Date;
import java.util.List;

import org.motech.model.Clinic;
import org.motech.model.FutureServiceDelivery;
import org.motech.model.MaternalData;
import org.motech.model.MaternalVisit;
import org.motech.model.Nurse;
import org.motech.model.Patient;
import org.motech.model.Pregnancy;

public interface SimpleDao {

	Long saveClinic(Clinic c);

	Clinic getClinic(String name);

	List<Clinic> getClinics();

	Long saveNurse(Nurse n);

	Nurse getNurse(String phoneNumber);

	List<Nurse> getNurses();

	Long savePatient(Patient p);

	Patient getPatient(String serialId, Long clinicId);

	List<Patient> getPatients();

	List<Pregnancy> getPregnancies();

	Long saveMaternalData(MaternalData md);

	List<MaternalVisit> getMaternalVisits();

	Long saveFutureServiceDelivery(FutureServiceDelivery fsd);

	void updateFutureServiceDelivery(FutureServiceDelivery fsd);

	List<FutureServiceDelivery> getFutureServiceDeliveries(Date startDate,
			Date endDate);

	List<FutureServiceDelivery> getFutureServiceDeliveries(Long patientId);

	List<FutureServiceDelivery> getFutureServiceDeliveries();
}
