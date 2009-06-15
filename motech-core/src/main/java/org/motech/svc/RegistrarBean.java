package org.motech.svc;

import java.util.Date;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.motech.model.Clinic;
import org.motech.model.Gender;
import org.motech.model.MaternalData;
import org.motech.model.MaternalVisit;
import org.motech.model.Nurse;
import org.motech.model.Patient;
import org.motech.model.Pregnancy;

@Stateless
public class RegistrarBean implements Registrar {

	@PersistenceContext
	EntityManager em;

	public void registerMother(String nursePhoneNumber, Date date,
			String serialId, String name, String community, String location,
			Integer age, Integer nhis, String phoneNumber, Date dueDate,
			Integer parity, Integer hemoglobin) {

		// TODO: Rely on nurse registration, needed for lookup in
		// registerPregnancy

		registerPatient(nursePhoneNumber, serialId, name, community, location,
				age, Gender.female, nhis, phoneNumber);

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
	}

	public void registerPatient(String nursePhoneNumber, String serialId,
			String name, String community, String location, Integer age,
			Gender gender, Integer nhis, String phoneNumber) {

		Nurse n = getNurse(nursePhoneNumber);

		Patient p = new Patient();
		p.setSerial(serialId);
		p.setName(name);
		p.setCommunity(community);
		p.setLocation(location);
		p.setAge(age);
		p.setGender(gender);
		p.setNhis(nhis);
		p.setPhoneNumber(phoneNumber);
		p.setClinic(n.getClinic());

		em.persist(p);
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
	}

	private Nurse getNurse(String phoneNumber) {
		return (Nurse) em.createNamedQuery("findNurseByPhoneNumber")
				.setParameter("phoneNumber", phoneNumber).getSingleResult();
	}

	private Patient getPatient(String serialId, Long clinicId) {
		return (Patient) em.createNamedQuery("findPatientByClinicSerial")
				.setParameter("serial", serialId).setParameter("clinicId",
						clinicId).getSingleResult();
	}
}
