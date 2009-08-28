package org.openmrs.module.motechmodule;

import static junit.framework.Assert.assertEquals;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;

import java.util.Date;
import java.util.logging.LogManager;

import org.easymock.Capture;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.motech.model.Gender;
import org.motech.svc.RegistrarBean;
import org.openmrs.module.motechmodule.web.ws.RegistrarService;
import org.openmrs.module.motechmodule.web.ws.RegistrarWebService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RegistrarServiceTest {

	static ApplicationContext ctx;
	static RegistrarService regWs;
	static RegistrarBean registrarBean;

	Capture<String> clinicCap;
	Capture<String> nPhoneCap;
	Capture<String> serialIdCap;
	Capture<String> nameCap;
	Capture<String> communityCap;
	Capture<String> locationCap;
	Capture<Date> dobCap;
	Capture<Gender> genderCap;
	Capture<Integer> nhisCap;
	Capture<String> pPhoneCap;

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
		clinicCap = new Capture<String>();
		nPhoneCap = new Capture<String>();
		serialIdCap = new Capture<String>();
		nameCap = new Capture<String>();
		communityCap = new Capture<String>();
		locationCap = new Capture<String>();
		dobCap = new Capture<Date>();
		genderCap = new Capture<Gender>();
		nhisCap = new Capture<Integer>();
		pPhoneCap = new Capture<String>();
	}

	@After
	public void tearDown() throws Exception {
		clinicCap = null;
		nPhoneCap = null;
		serialIdCap = null;
		nameCap = null;
		communityCap = null;
		locationCap = null;
		dobCap = null;
		genderCap = null;
		nhisCap = null;
		pPhoneCap = null;
		reset(registrarBean);
	}

	@Test
	public void testRegisterClinic() {
		registrarBean.registerClinic(capture(clinicCap));

		replay(registrarBean);

		String clinicName = "A-Test-Clinic-Name";
		regWs.registerClinic(clinicName);

		verify(registrarBean);

		assertEquals(clinicName, clinicCap.getValue());
	}

	@Test
	public void testRegisterNurse() {
		registrarBean.registerNurse(capture(nameCap), capture(nPhoneCap),
				capture(clinicCap));

		replay(registrarBean);

		String name = "Sally", phone = "12075555555", clinic = "C-Clinic";
		regWs.registerNurse(name, phone, clinic);

		verify(registrarBean);

		assertEquals(name, nameCap.getValue());
		assertEquals(phone, nPhoneCap.getValue());
		assertEquals(clinic, clinicCap.getValue());
	}

	@Test
	public void testRegisterPatient() {
		registrarBean.registerPatient(capture(nPhoneCap), capture(serialIdCap),
				capture(nameCap), capture(communityCap), capture(locationCap),
				capture(dobCap), capture(genderCap), capture(nhisCap),
				capture(pPhoneCap));

		replay(registrarBean);

		String nPhone = "12075551212", serialId = "387946894", name = "Francis", community = "somepeople", location = "somewhere", pPhone = "120755512525";
		Date dob = new Date();
		Gender gender = Gender.female;
		Integer nhis = 3;

		regWs.registerPatient(nPhone, serialId, name, community, location, dob,
				gender, nhis, pPhone);

		verify(registrarBean);

		assertEquals(nPhone, nPhoneCap.getValue());
		assertEquals(serialId, serialIdCap.getValue());
		assertEquals(name, nameCap.getValue());
		assertEquals(community, communityCap.getValue());
		assertEquals(location, locationCap.getValue());
		assertEquals(pPhone, pPhoneCap.getValue());
		assertEquals(dob, dobCap.getValue());
		assertEquals(gender, genderCap.getValue());
		assertEquals(nhis, nhisCap.getValue());
	}
}
