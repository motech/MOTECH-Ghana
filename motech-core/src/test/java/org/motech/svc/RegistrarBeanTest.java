package org.motech.svc;

import java.util.Date;

import javax.persistence.EntityManager;

import org.motech.model.Clinic;
import org.motech.model.FutureServiceDelivery;
import org.motech.model.Gender;
import org.motech.model.Log;
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
			MaternalVisit.class, FutureServiceDelivery.class, Log.class };

	public RegistrarBeanTest() {
		super(RegistrarBean.class, USED_ENTITIES);
	}

	public void testRegisterMother() {
		EntityManager em = getEntityManager();
		em.getTransaction().begin();
		try {
			Registrar regBean = getBeanToTest();
			regBean.registerClinic("C-Clinic");
			regBean.registerNurse("Samuel", "8888888888", "C-Clinic");
			regBean.registerMother("8888888888", new Date(), "g48748",
					"Brandy", "Community", "Location", new Date(), 23,
					"3838383838", new Date(), 3, 43);
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
			regBean.registerClinic("B-Clinic");
			regBean.registerNurse("Karen", "7777777777", "B-Clinic");
			assertEquals(1, em.createQuery(
					"select n from Nurse n where name = :name").setParameter(
					"name", "Karen").getResultList().size());
		} finally {
			em.getTransaction().rollback();
		}
	}

	public void testRegisterPatient() {
		EntityManager em = getEntityManager();
		em.getTransaction().begin();
		try {
			Registrar regBean = getBeanToTest();
			regBean.registerClinic("P-Clinic");
			regBean.registerNurse("Cindy", "3783783738", "P-Clinic");
			regBean.registerPatient("3783783738", "4ui4hu4", "Charles",
					"Somewhere", "Somewhere smaller", new Date(),
					Gender.female, 23, "378878787873");
			assertEquals(1, em.createQuery(
					"select p from Patient p where serial = :serial")
					.setParameter("serial", "4ui4hu4").getResultList().size());
		} finally {
			em.getTransaction().rollback();
		}
	}

	public void testRegisterPregnancy() {
		EntityManager em = getEntityManager();
		em.getTransaction().begin();
		try {
			Registrar regBean = getBeanToTest();
			regBean.registerClinic("B-Clinic");
			regBean.registerNurse("Karen", "7777777777", "B-Clinic");
			regBean.registerPatient("7777777777", "ghdg438", "Patient",
					"Community", "Location", new Date(), Gender.female, 21,
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

	public void testRecordMaternalVisit() {
		EntityManager em = getEntityManager();
		em.getTransaction().begin();
		try {
			Registrar regBean = getBeanToTest();
			regBean.registerClinic("F-Clinic");
			regBean.registerNurse("Daniel", "3478478784", "F-Clinic");
			regBean.registerPatient("3478478784", "d99d89d", "AMother",
					"ACommunity", "ALocation", new Date(), Gender.female, 3,
					"2828282828");
			regBean.recordMaternalVisit("3478478784", new Date(), "d99d89d",
					34, 32, 3, 38, 27, 29, 28, 93, 28);
			assertEquals(1, em.createQuery(
					"select mv from MaternalVisit mv "
							+ "where mv.maternalData.patient.serial = :serial "
							+ "and mv.nurse.name = :nurseName").setParameter(
					"serial", "d99d89d").setParameter("nurseName", "Daniel")
					.getResultList().size());
		} finally {
			em.getTransaction().rollback();
		}
	}
}
