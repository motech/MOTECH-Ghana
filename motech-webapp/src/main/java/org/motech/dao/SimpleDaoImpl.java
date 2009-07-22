package org.motech.dao;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.motech.model.Clinic;
import org.motech.model.FutureServiceDelivery;
import org.motech.model.MaternalData;
import org.motech.model.MaternalVisit;
import org.motech.model.Nurse;
import org.motech.model.Patient;
import org.motech.model.Pregnancy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SimpleDaoImpl implements SimpleDao {

	private static Log log = LogFactory.getLog(SimpleDaoImpl.class);

	SessionFactory factory;

	@Autowired
	public void setSessionFactory(SessionFactory sf) {
		log.debug("setting session factory" + sf);
		factory = sf;
	}

	public Long saveClinic(Clinic c) {
		Session session = factory.getCurrentSession();
		return (Long) session.save(c);
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

	public Long saveNurse(Nurse n) {
		Session session = factory.getCurrentSession();
		return (Long) session.save(n);
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

	public Long savePatient(Patient p) {
		Session session = factory.getCurrentSession();
		return (Long) session.save(p);
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
		return (List<Pregnancy>) session.createQuery(
				"from Pregnancy p join fetch p.maternalData").list();
	}

	public Long saveMaternalData(MaternalData md) {
		Session session = factory.getCurrentSession();
		return (Long) session.save(md);
	}

	@SuppressWarnings("unchecked")
	public List<MaternalVisit> getMaternalVisits() {
		Session session = factory.getCurrentSession();
		return (List<MaternalVisit>) session.createQuery(
				"from MaternalVisit v join fetch v.maternalData").list();
	}

	public Long saveFutureServiceDelivery(FutureServiceDelivery fsd) {
		Session session = factory.getCurrentSession();
		return (Long) session.save(fsd);
	}

	public void updateFutureServiceDelivery(FutureServiceDelivery fsd) {
		Session session = factory.getCurrentSession();
		session.merge(fsd);
	}

	@SuppressWarnings("unchecked")
	public List<FutureServiceDelivery> getFutureServiceDeliveries(
			Date startDate, Date endDate) {
		Session session = factory.getCurrentSession();
		return (List<FutureServiceDelivery>) session.createQuery(
				"from FutureServiceDelivery s join fetch s.nurse join fetch s.patient "
						+ "where date between :startDate and :endDate")
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
