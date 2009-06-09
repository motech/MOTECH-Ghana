package org.motech.ejb;

import java.util.Date;

import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.motech.model.Clinic;
import org.motech.model.Gender;
import org.motech.model.MaternalData;
import org.motech.model.Nurse;
import org.motech.model.Patient;
import org.motech.model.Pregnancy;

@Stateless
@WebService
public class RegistrarBean implements Registrar {

	@PersistenceContext
	EntityManager em;

	@WebMethod
	public void registerMother(String nursePhoneNumber, String serialId, String name,
			String community, String location, Integer age, Integer nhis,
			Date dueDate, Integer parity, Integer hemoglobin) {

		// TODO: Rely on nurse registration, needed for lookup in registerPregnancy
		registerNurse("Mark", nursePhoneNumber, "A-Clinic");

		registerPatient(serialId, name, community, location, age, 
			Gender.female.toString(), nhis);
		
		registerPregnancy(nursePhoneNumber, serialId, dueDate, parity, hemoglobin);
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
	public void registerPatient(String serialId, String name, String community, 
			String location, Integer age, String gender, Integer nhis) {
			
		Patient p = new Patient();
		p.setSerial(serialId);
		p.setName(name);
		p.setCommunity(community);
		p.setLocation(location);
		p.setAge(age);
		p.setGender(Gender.valueOf(gender));
		p.setNhis(nhis);
		
		em.persist(p);
	}
		
	@WebMethod
	public void registerPregnancy(String nursePhoneNumber, String serialId,
			Date dueDate, Integer parity, Integer hemoglobin) {
		
		Nurse n = (Nurse)em.createNamedQuery("findNurseByPhoneNumber")
			.setParameter("phoneNumber", nursePhoneNumber).getSingleResult();
		
		Patient a = (Patient)em.createNamedQuery("findPatientBySerial")
			.setParameter("serial", serialId).getSingleResult();
		

		// TODO: Assumes MaternalData does not already exist
		//if a.getMaternalData() is null
		
		MaternalData m = new MaternalData();
		m.setPatient(a);
		a.setMaternalData(m);
		
		Pregnancy p = new Pregnancy();
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

}
