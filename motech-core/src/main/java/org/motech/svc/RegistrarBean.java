package org.motech.svc;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.motech.model.Clinic;
import org.motech.model.FutureServiceDelivery;
import org.motech.model.Gender;
import org.motech.model.LogType;
import org.motech.model.MaternalData;
import org.motech.model.MaternalVisit;
import org.motech.model.Nurse;
import org.motech.model.Patient;
import org.motech.model.Pregnancy;

@Stateless
public class RegistrarBean implements Registrar {

	@Resource
	TimerService timerService;

	@PersistenceContext
	EntityManager em;

	@EJB
	Logger loggerBean;

	public void registerMother(String nursePhoneNumber, Date date,
			String serialId, String name, String community, String location,
			Date dateOfBirth, Integer nhis, String phoneNumber, Date dueDate,
			Integer parity, Integer hemoglobin) {

		// TODO: Rely on nurse registration, needed for lookup in
		// registerPregnancy

		registerPatient(nursePhoneNumber, serialId, name, community, location,
				dateOfBirth, Gender.female, nhis, phoneNumber);

		registerPregnancy(nursePhoneNumber, date, serialId, dueDate, parity,
				hemoglobin);
	}

	public void registerNurse(String name, String phoneNumber, String clinic) {
		Clinic c = new Clinic();
		c.setName(clinic);
		em.persist(c);

		Nurse n = new Nurse();
		n.setName(name);
		n.setPhoneNumber(phoneNumber);
		n.setClinic(c);
		em.persist(n);

		loggerBean.log(LogType.success, "Nurse Registered: " + name + ","
				+ phoneNumber);
	}

	public void registerPatient(String nursePhoneNumber, String serialId,
			String name, String community, String location, Date dateOfBirth,
			Gender gender, Integer nhis, String phoneNumber) {

		Nurse n = getNurse(nursePhoneNumber);

		Patient p = new Patient();
		p.setSerial(serialId);
		p.setName(name);
		p.setCommunity(community);
		p.setLocation(location);
		p.setDateOfBirth(dateOfBirth);
		p.setGender(gender);
		p.setNhis(nhis);
		p.setPhoneNumber(phoneNumber);
		p.setClinic(n.getClinic());

		em.persist(p);

		loggerBean.log(LogType.success, "Patient Registered: " + serialId + ","
				+ n.getClinic().getId());
	}

	public void registerPregnancy(String nursePhoneNumber, Date date,
			String serialId, Date dueDate, Integer parity, Integer hemoglobin) {

		Nurse n = getNurse(nursePhoneNumber);

		Patient a = getPatient(serialId, n.getClinic().getId());

		MaternalData m = a.getMaternalData();
		// Check if a Maternal Visit or Pregnancy have been recorded previously
		if (m == null) {
			m = new MaternalData();
			m.setPatient(a);
			a.setMaternalData(m);
		}

		Pregnancy p = new Pregnancy();
		p.setRegistrationDate(date);
		p.setParity(parity);
		p.setHemoglobin(hemoglobin);
		p.setDueDate(dueDate);
		p.setMaternalData(m);

		// Hookup relationships
		n.getPregnancies().add(p);
		p.setNurse(n);

		m.getPregnancies().add(p);
		p.setMaternalData(m);

		// Persist (Mother persists pregnancy and patient transitively)
		em.persist(m);

		loggerBean.log(LogType.success, "Pregnancy Registered: " + serialId
				+ "," + dueDate);
	}

	public void recordMaternalVisit(String nursePhoneNumber, Date date,
			String serialId, Integer tetanus, Integer ipt, Integer itn,
			Integer visitNumber, Integer onARV, Integer prePMTCT,
			Integer testPMTCT, Integer postPMTCT, Integer hemoglobinAt36Weeks) {

		Nurse n = getNurse(nursePhoneNumber);

		Patient a = getPatient(serialId, n.getClinic().getId());

		MaternalVisit v = new MaternalVisit();
		v.setDate(date);
		v.setNurse(n);
		v.setTetanus(tetanus);
		v.setIpt(ipt);
		v.setItn(itn);
		v.setVisitNumber(visitNumber);
		v.setOnARV(onARV);
		v.setPrePMTCT(prePMTCT);
		v.setTestPMTCT(testPMTCT);
		v.setPostPMTCT(postPMTCT);
		v.setHemoglobinAt36Weeks(hemoglobinAt36Weeks);

		MaternalData m = a.getMaternalData();
		// Check if a Maternal Visit or Pregnancy have been recorded previously
		if (m == null) {
			m = new MaternalData();
			m.setPatient(a);
			a.setMaternalData(m);
		}

		v.setMaternalData(m);
		m.getMaternalVisits().add(v);

		em.persist(m);

		loggerBean.log(LogType.success, "Maternal Visit Registered: "
				+ serialId + "," + date);

		// Date 30 seconds in future
		long nextServiceTime = 30 * 1000;
		Date nextServiceDate = new Date(System.currentTimeMillis()
				+ nextServiceTime);

		FutureServiceDelivery f = new FutureServiceDelivery();
		f.setDate(nextServiceDate);
		f.setNurse(n);
		f.setPatient(a);
		f.setService("Maternal Visit");

		em.persist(f);

		loggerBean.log(LogType.success, "Future Service Delivery Scheduled: "
				+ serialId + "," + nextServiceDate);

		// Schedule notifications to go out in 30 seconds
		// EJB3Unit TimerService cannot handle Date, only long
		timerService.createTimer(nextServiceTime, null);
	}

	@Timeout
	public void sendNotifications(Timer timer) {
		Date startDate = new Date(System.currentTimeMillis() - (30 * 1000));
		Date endDate = new Date(System.currentTimeMillis() + (30 * 1000));
		List<FutureServiceDelivery> futureServices = getFutureServiceDeliveries(
				startDate, endDate);
		Date notificationDate = new Date();
		for (FutureServiceDelivery service : futureServices) {
			loggerBean.log(LogType.success,
					"Future Service Delivery Notifications: "
							+ service.getNurse().getPhoneNumber() + ","
							+ service.getPatient().getPhoneNumber());
			service.setNurseNotifiedDate(notificationDate);
			service.setPatientNotifiedDate(notificationDate);
		}
	}

	public Nurse getNurse(String phoneNumber) {
		return (Nurse) em.createNamedQuery("findNurseByPhoneNumber")
				.setParameter("phoneNumber", phoneNumber).getSingleResult();
	}

	@SuppressWarnings("unchecked")
	public List<Nurse> getNurses() {
		return (List<Nurse>) em.createNamedQuery("findAllNurses")
				.getResultList();
	}

	public Patient getPatient(String serialId, Long clinicId) {
		return (Patient) em.createNamedQuery("findPatientByClinicSerial")
				.setParameter("serial", serialId).setParameter("clinicId",
						clinicId).getSingleResult();
	}

	@SuppressWarnings("unchecked")
	public List<Patient> getPatients() {
		return (List<Patient>) em.createNamedQuery("findAllPatients")
				.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Pregnancy> getPregnancies() {
		return (List<Pregnancy>) em.createNamedQuery("findAllPregnancies")
				.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<MaternalVisit> getMaternalVisits() {
		return (List<MaternalVisit>) em.createNamedQuery(
				"findAllMaternalVisits").getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<FutureServiceDelivery> getFutureServiceDeliveries(Date start,
			Date end) {
		return (List<FutureServiceDelivery>) em.createNamedQuery(
				"findFutureServiceDeliveriesByDate").setParameter("startDate",
				start).setParameter("endDate", end).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<FutureServiceDelivery> getFutureServiceDeliveries(Patient p) {
		return (List<FutureServiceDelivery>) em.createNamedQuery(
				"findFutureServiceDeliveriesForPatient").setParameter(
				"patientId", p.getId()).getResultList();
	}
}
