package org.motech.svc;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.motech.model.Clinic;
import org.motech.model.FutureServiceDelivery;
import org.motech.model.Gender;
import org.motech.model.LogType;
import org.motech.model.MaternalData;
import org.motech.model.MaternalVisit;
import org.motech.model.Nurse;
import org.motech.model.Patient;
import org.motech.model.Pregnancy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RegistrarBean implements Registrar {

	private static Log log = LogFactory.getLog(RegistrarBean.class);

	SessionFactory factory;

	@Autowired
	Logger loggerBean;

	@Autowired
	public void setSessionFactory(SessionFactory sf) {
		log.debug("setting session factory" + sf);
		factory = sf;
	}

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

	public void registerClinic(String name) {
		Session session = factory.getCurrentSession();

		Clinic c = new Clinic();
		c.setName(name);
		session.save(c);

		loggerBean.log(LogType.success, "Clinic Registered: " + name);
	}

	public void registerNurse(String name, String phoneNumber, String clinic) {
		Session session = factory.getCurrentSession();

		Clinic c = getClinic(clinic);

		Nurse n = new Nurse();
		n.setName(name);
		n.setPhoneNumber(phoneNumber);
		n.setClinic(c);
		session.save(n);

		loggerBean.log(LogType.success, "Nurse Registered: " + name + ","
				+ phoneNumber);
	}

	public void registerPatient(String nursePhoneNumber, String serialId,
			String name, String community, String location, Date dateOfBirth,
			Gender gender, Integer nhis, String phoneNumber) {

		Session session = factory.getCurrentSession();

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

		session.save(p);

		loggerBean.log(LogType.success, "Patient Registered: " + serialId + ","
				+ n.getClinic().getId());
	}

	public void registerPregnancy(String nursePhoneNumber, Date date,
			String serialId, Date dueDate, Integer parity, Integer hemoglobin) {

		Session session = factory.getCurrentSession();

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
		session.save(m);

		loggerBean.log(LogType.success, "Pregnancy Registered: " + serialId
				+ "," + dueDate);
	}

	public void recordMaternalVisit(String nursePhoneNumber, Date date,
			String serialId, Integer tetanus, Integer ipt, Integer itn,
			Integer visitNumber, Integer onARV, Integer prePMTCT,
			Integer testPMTCT, Integer postPMTCT, Integer hemoglobinAt36Weeks) {

		Session session = factory.getCurrentSession();

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

		session.save(m);

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

		session.save(f);

		loggerBean.log(LogType.success, "Future Service Delivery Scheduled: "
				+ serialId + "," + nextServiceDate);
	}

	public void notifyFutureService(FutureServiceDelivery service,
			Date notificationDate) {

		Session session = factory.getCurrentSession();

		service.setNurseNotifiedDate(notificationDate);
		service.setPatientNotifiedDate(notificationDate);

		session.merge(service);
	}

	public Clinic getClinic(String name) {
		Session session = factory.getCurrentSession();
		return (Clinic) session.createQuery("from Clinic where name = :name")
				.setParameter("name", name).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Clinic> getClinics() {
		Session session = factory.getCurrentSession();
		return (List<Clinic>) session.createQuery("from Clinic").list();
	}

	public Nurse getNurse(String phoneNumber) {
		Session session = factory.getCurrentSession();
		return (Nurse) session.createQuery(
				"from Nurse where phoneNumber = :phoneNumber").setParameter(
				"phoneNumber", phoneNumber).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Nurse> getNurses() {
		Session session = factory.getCurrentSession();
		return (List<Nurse>) session.createQuery("from Nurse").list();
	}

	public Patient getPatient(String serialId, Long clinicId) {
		Session session = factory.getCurrentSession();
		return (Patient) session
				.createQuery(
						"from Patient where serial = :serialId and clinic.id = :clinicId")
				.setParameter("serialId", serialId).setParameter("clinicId",
						clinicId).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Patient> getPatients() {
		Session session = factory.getCurrentSession();
		return (List<Patient>) session.createQuery("from Patient").list();
	}

	@SuppressWarnings("unchecked")
	public List<Pregnancy> getPregnancies() {
		Session session = factory.getCurrentSession();
		return (List<Pregnancy>) session.createQuery("from Pregnancy p join fetch p.maternalData").list();
	}

	@SuppressWarnings("unchecked")
	public List<MaternalVisit> getMaternalVisits() {
		Session session = factory.getCurrentSession();
		return (List<MaternalVisit>) session.createQuery("from MaternalVisit v join fetch v.maternalData")
				.list();
	}

	@SuppressWarnings("unchecked")
	public List<FutureServiceDelivery> getFutureServiceDeliveries(
			Date startDate, Date endDate) {
		Session session = factory.getCurrentSession();
		return (List<FutureServiceDelivery>) session
				.createQuery(
						"from FutureServiceDelivery where date between :startDate and :endDate")
				.setParameter("startDate", startDate).setParameter("endDate",
						endDate).list();
	}

	@SuppressWarnings("unchecked")
	public List<FutureServiceDelivery> getFutureServiceDeliveries(Long patientId) {
		Session session = factory.getCurrentSession();
		return (List<FutureServiceDelivery>) session.createQuery(
				"from FutureServiceDelivery where patient.id = :patientId")
				.setParameter("patientId", patientId).list();
	}

	@SuppressWarnings("unchecked")
	public List<FutureServiceDelivery> getFutureServiceDeliveries() {
		Session session = factory.getCurrentSession();
		return (List<FutureServiceDelivery>) session.createQuery(
				"from FutureServiceDelivery").list();
	}
}
