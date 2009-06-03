package org.motech.ejb;

import java.util.Date;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.motech.model.Gender;
import org.motech.model.Mother;
import org.motech.model.Nurse;
import org.motech.model.Pregnancy;

@Stateless
public class RegistrarBean implements Registrar {

	@PersistenceContext
	EntityManager em;

	public void registerMother(String nurseId, String serialId, String name,
			String community, String location, Date dueDate, Integer age,
			Integer parity, Integer hemoglobin) {

		// TODO: Rely on nurse registration, lookup instead
		Nurse n = new Nurse();
		n.setName("Mark");
		n.setPhoneNumber("8795309");
		n.setClinic("A-Clinic");
		em.persist(n);

		Mother m = new Mother();
		m.setSerial(serialId);
		m.setName(name);
		m.setCommunity(community);
		m.setLocation(location);
		m.setAge(age);
		m.setGender(Gender.female);
		em.persist(m);

		Pregnancy p = new Pregnancy();
		p.setParity(parity);
		p.setHemoglobin(hemoglobin);
		p.setDueDate(dueDate);
		em.persist(p);

		p.setMother(m);
		p.setNurse(n);
	}

}
