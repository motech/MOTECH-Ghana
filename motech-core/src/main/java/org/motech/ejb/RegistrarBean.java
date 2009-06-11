package org.motech.ejb;

import java.util.Date;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motech.model.Clinic;
import org.motech.model.Gender;
import org.motech.model.MaternalData;
import org.motech.model.MaternalVisit;
import org.motech.model.Nurse;
import org.motech.model.Patient;
import org.motech.model.Pregnancy;

@Stateless
@WebService
public class RegistrarBean implements Registrar {

	private static Log log = LogFactory.getLog(RegistrarBean.class);

	@Resource
	SessionContext ctx;

	@PersistenceContext
	EntityManager em;

	@WebMethod
	public void registerMother(String nursePhoneNumber, String serialId,
			String name, String community, String location, Integer age,
			Integer nhis, Date dueDate, Integer parity, Integer hemoglobin) {

		// TODO: Rely on nurse registration, needed for lookup in
		// registerPregnancy
		registerNurse("Mark", nursePhoneNumber, "A-Clinic");

		registerPatient(nursePhoneNumber, serialId, name, community, location,
				age, Gender.female.toString(), nhis);

		registerPregnancy(nursePhoneNumber, serialId, dueDate, parity,
				hemoglobin);
	}

	@WebMethod
	public void registerNurse(String name, String phoneNumber, String clinic) {
		Clinic c = new Clinic();
		c.setName(clinic);
		em.persist(c);

		Nurse n = new Nurse();
		n.setName(name);
		n.setPhoneNumber(phoneNumber);
		n.setClinic(c);
		em.persist(n);
	}

	@WebMethod
	public void registerPatient(String nursePhoneNumber, String serialId,
			String name, String community, String location, Integer age,
			String gender, Integer nhis) {

		Nurse n = (Nurse) em.createNamedQuery("findNurseByPhoneNumber")
				.setParameter("phoneNumber", nursePhoneNumber)
				.getSingleResult();

		Patient p = new Patient();
		p.setSerial(serialId);
		p.setName(name);
		p.setCommunity(community);
		p.setLocation(location);
		p.setAge(age);
		p.setGender(Gender.valueOf(gender));
		p.setNhis(nhis);
		p.setClinic(n.getClinic());

		em.persist(p);
	}

	@WebMethod
	public void registerPregnancy(String nursePhoneNumber, String serialId,
			Date dueDate, Integer parity, Integer hemoglobin) {

		Nurse n = (Nurse) em.createNamedQuery("findNurseByPhoneNumber")
				.setParameter("phoneNumber", nursePhoneNumber)
				.getSingleResult();

		Patient a = (Patient) em.createNamedQuery("findPatientByClinicSerial")
				.setParameter("serial", serialId).setParameter("clinicId",
						n.getClinic().getId()).getSingleResult();

		// TODO: Assumes MaternalData does not already exist
		// if a.getMaternalData() is null

		MaternalData m = new MaternalData();
		m.setPatient(a);
		a.setMaternalData(m);

		Pregnancy p = new Pregnancy();
		p.setRegistrationDate(new Date());
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

		// Schedule notification to go out in 15 seconds
		Long motherId = m.getId();
		Date callbackDate = new Date(System.currentTimeMillis() + (15 * 1000));
		ctx.getTimerService().createTimer(callbackDate,
				"registered mother: " + motherId);
	}

	public void recordMaternalVisit(String nursePhoneNumber, String serialId,
			Integer tetanus, Integer ipt, Integer itn, Integer visitNumber,
			Integer onARV, Integer prePMTCT, Integer testPMTCT,
			Integer postPMTCT, Integer hemoglobinAt36Weeks) {

		Nurse n = (Nurse) em.createNamedQuery("findNurseByPhoneNumber")
				.setParameter("phoneNumber", nursePhoneNumber)
				.getSingleResult();

		Patient a = (Patient) em.createNamedQuery("findPatientByClinicSerial")
				.setParameter("serial", serialId).setParameter("clinicId",
						n.getClinic().getId()).getSingleResult();

		MaternalVisit v = new MaternalVisit();
		v.setDate(new Date());
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
		v.setMaternalData(m);
		m.getMaternalVisits().add(v);

		em.persist(m);
	}

	@Timeout
	public void sendRegNotification(Timer timer) {
		log.info("registration notification - " + timer.getInfo());
	}
}
