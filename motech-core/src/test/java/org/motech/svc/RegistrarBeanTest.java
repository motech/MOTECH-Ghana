package org.motech.svc;

import java.util.Date;

import javax.persistence.EntityManager;

import org.motech.model.Clinic;
import org.motech.model.FutureServiceDelivery;
import org.motech.model.Gender;
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
			MaternalVisit.class, FutureServiceDelivery.class };

	public RegistrarBeanTest() {
		super(RegistrarBean.class, USED_ENTITIES);
	}

	public void testRegisterMother() {
		EntityManager em = getEntityManager();
		em.getTransaction().begin();
		try {
			Registrar regBean = getBeanToTest();
			regBean.registerNurse("Samuel", "8888888888", "C-Clinic");
			regBean.registerMother("8888888888", new Date(), "g48748",
					"Brandy", "Community", "Location", 16, 23, "3838383838",
					new Date(), 3, 43);
			assertEquals(1, em.createQuery(
					"select p from Patient p where p.serial = :serial")
					.setParameter("serial", "g48748").getResultList().size());
		} finally {
			em.getTransaction().rollback();
		}
	}

	public void testRegisterNurse() {
		EntityManager em = getEntityManager();
		em.getTransaction().begin();
		try {
			Registrar regBean = getBeanToTest();
			regBean.registerNurse("Karen", "7777777777", "B-Clinic");
			assertEquals(1, em.createQuery(
					"select n from Nurse n where name = :name").setParameter(
					"name", "Karen").getResultList().size());
		} finally {
			em.getTransaction().rollback();
		}
	}

	public void testRegisterPregnancy() {
		EntityManager em = getEntityManager();
		em.getTransaction().begin();
		try {
			Registrar regBean = getBeanToTest();
			regBean.registerNurse("Karen", "7777777777", "B-Clinic");
			regBean.registerPatient("7777777777", "ghdg438", "Patient",
					"Community", "Location", 21, Gender.female, 21,
					"2828282828");
			regBean.registerPregnancy("7777777777", new Date(), "ghdg438",
					new Date(), 2, 289);
			assertEquals(
					1,
					em
							.createQuery(
									"select p from Pregnancy p where p.maternalData.patient.serial = :serialId")
							.setParameter("serialId", "ghdg438")
							.getResultList().size());
		} finally {
			em.getTransaction().rollback();
		}
	}
}
