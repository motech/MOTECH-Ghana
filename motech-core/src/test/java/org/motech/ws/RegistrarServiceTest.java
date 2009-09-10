package org.motech.ws;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.logging.LogManager;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.motech.model.Gender;
import org.motech.model.LogType;
import org.motech.model.NotificationType;
import org.motech.model.PhoneType;
import org.motech.svc.RegistrarBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RegistrarServiceTest {

	static ApplicationContext ctx;
	static RegistrarService regWs;
	static RegistrarBean registrarBean;

	@BeforeClass
	public static void setUpClass() throws Exception {
		LogManager.getLogManager().readConfiguration(
				RegistrarServiceTest.class
						.getResourceAsStream("/jul-test.properties"));
		registrarBean = createMock(RegistrarBean.class);
		ctx = new ClassPathXmlApplicationContext("test-context.xml");
		RegistrarWebService regService = (RegistrarWebService) ctx
				.getBean("registrarService");
		regService.setRegistrarBean(registrarBean);
		regWs = (RegistrarService) ctx.getBean("registrarClient");
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		ctx = null;
		regWs = null;
		registrarBean = null;
		LogManager.getLogManager().readConfiguration();
	}

	@Before
	public void setup() {
	}

	@After
	public void tearDown() throws Exception {
		reset(registrarBean);
	}

	@Test
	public void testRegisterClinic() {
		String clinicName = "A-Test-Clinic-Name";

		registrarBean.registerClinic(clinicName);

		replay(registrarBean);

		regWs.registerClinic(clinicName);

		verify(registrarBean);
	}

	@Test
	public void testRegisterNurse() {
		String name = "Sally", phone = "12075555555", clinic = "C-Clinic";

		registrarBean.registerNurse(name, phone, clinic);

		replay(registrarBean);

		regWs.registerNurse(name, phone, clinic);

		verify(registrarBean);
	}

	@Test
	public void testRegisterPatient() {
		String nPhone = "12075551212", serialId = "387946894", name = "Francis", community = "somepeople", location = "somewhere", pPhone = "120755512525";
		Date dob = new Date();
		Gender gender = Gender.female;
		Integer nhis = 3;
		PhoneType phoneType = PhoneType.personal;
		String language = "English";
		NotificationType notificationType = NotificationType.text;

		registrarBean.registerPatient(nPhone, serialId, name, community,
				location, dob, gender, nhis, pPhone, phoneType, language,
				notificationType);

		replay(registrarBean);

		regWs.registerPatient(nPhone, serialId, name, community, location, dob,
				gender, nhis, pPhone, phoneType, language, notificationType);

		verify(registrarBean);
	}

	@Test
	public void testRegisterPregnancy() {
		String nPhone = "12075551212", serialId = "387946894";
		Date date = new Date(), dueDate = new Date();
		Integer parity = 3;
		Double hemo = 2.0;

		registrarBean.registerPregnancy(nPhone, date, serialId, dueDate,
				parity, hemo);

		replay(registrarBean);

		regWs.registerPregnancy(nPhone, date, serialId, dueDate, parity, hemo);

		verify(registrarBean);
	}

	@Test
	public void testRecordMaternalVisit() {
		String nPhone = "12077778383", serialId = "ghj347956y";
		Date date = new Date();
		Boolean tetanus = true, ipt = false, itn = true, onARV = false, prePMTCT = true, testPMTCT = false, postPMTCT = true;
		Integer visitNum = 3;
		Double hemo = 378.34;

		registrarBean.recordMaternalVisit(nPhone, date, serialId, tetanus, ipt,
				itn, visitNum, onARV, prePMTCT, testPMTCT, postPMTCT, hemo);

		replay(registrarBean);

		regWs.recordMaternalVisit(nPhone, date, serialId, tetanus, ipt, itn,
				visitNum, onARV, prePMTCT, testPMTCT, postPMTCT, hemo);

		verify(registrarBean);
	}

	@Test
	public void testLog() {
		LogType type = LogType.success;
		String msg = "logging over ws is slow";

		registrarBean.log(type, msg);

		replay(registrarBean);

		regWs.log(type, msg);

		verify(registrarBean);
	}

	@Test
	public void testRegistrarBeanProperty() throws SecurityException,
			NoSuchFieldException, IllegalArgumentException,
			IllegalAccessException {
		RegistrarWebService regWs = new RegistrarWebService();

		Field regBeanField = regWs.getClass().getDeclaredField("registrarBean");
		regBeanField.setAccessible(true);

		regWs.setRegistrarBean(registrarBean);
		assertEquals(registrarBean, regBeanField.get(regWs));

		regWs.setRegistrarBean(null);
		assertEquals(null, regBeanField.get(regWs));
	}
}
