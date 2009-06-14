package org.motech.svc;

import javax.persistence.EntityManager;

import org.motech.model.Clinic;
import org.motech.model.MaternalData;
import org.motech.model.MaternalVisit;
import org.motech.model.Nurse;
import org.motech.model.Patient;
import org.motech.model.Pregnancy;

import com.bm.testsuite.BaseSessionBeanFixture;

public class RegistrarBeanTest extends BaseSessionBeanFixture<RegistrarBean> {

	@SuppressWarnings("unchecked")
	private static Class[] USED_ENTITIES = new Class[] { Nurse.class,
			Clinic.class, Patient.class, MaternalData.class, Pregnancy.class,
			MaternalVisit.class };

	public RegistrarBeanTest() {
		super(RegistrarBean.class, USED_ENTITIES);
	}

	public void testRegisterNurse() {
		EntityManager em = getEntityManager();
		em.getTransaction().begin();
		Registrar regBean = getBeanToTest();
		regBean.registerNurse("Karen", "7777777777", "B-Clinic");
		em.getTransaction().commit();
		assertEquals(1, em.createQuery(
				"select n from Nurse n where name = :name").setParameter(
				"name", "Karen").getResultList().size());
	}
}
