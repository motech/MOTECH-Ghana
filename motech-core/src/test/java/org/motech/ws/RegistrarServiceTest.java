package org.motech.ws;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.logging.LogManager;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.motech.svc.RegistrarBean;
import org.motechproject.ws.Gender;
import org.motechproject.ws.LogType;
import org.motechproject.ws.server.RegistrarService;
import org.motechproject.ws.server.ValidationError;
import org.motechproject.ws.server.ValidationException;
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
	public void testRegisterChild() {
		Date regDate = new Date(), childDob = new Date(), nhisExpires = new Date();
		String nurseId = "FGH267", motherRegNum = "ABC123", childRegNum = "DEF456", childFirstName = "Sarah", nhis = "14567";
		Gender childGender = Gender.FEMALE;

		registrarBean.registerChild(nurseId, regDate, motherRegNum,
				childRegNum, childDob, childGender, childFirstName, nhis,
				nhisExpires);

		replay(registrarBean);

		regWs.registerChild(nurseId, regDate, motherRegNum, childRegNum,
				childDob, childGender, childFirstName, nhis, nhisExpires);

		verify(registrarBean);
	}

	@Test
	public void testStopPregnancyProgram() {
		String nurseId = "FGH267", patientRegNum = "ABC123";

		registrarBean.stopPregnancyProgram(nurseId, patientRegNum);

		replay(registrarBean);

		regWs.stopPregnancyProgram(nurseId, patientRegNum);

		verify(registrarBean);
	}

	@Test
	public void testGeneralVisit() throws ValidationException {
		String serial = "Test123";
		Gender gender = Gender.MALE;
		Integer clinic = 1, diagnosis = 5;
		Boolean referral = false;
		Date date = new Date();

		registrarBean.recordGeneralVisit(clinic, date, serial, gender, date,
				diagnosis, referral);

		replay(registrarBean);

		regWs.recordGeneralVisit(clinic, date, serial, gender, date, diagnosis,
				referral);

		verify(registrarBean);
	}

	@Test
	public void testGeneralVisitMissingClinic() {
		String serial = "Test123";
		Gender gender = Gender.MALE;
		Integer clinic = null, diagnosis = 5;
		Boolean referral = false;
		Date date = new Date();

		try {
			regWs.recordGeneralVisit(clinic, date, serial, gender, date,
					diagnosis, referral);
			fail("Expected ValidationException");
		} catch (ValidationException e) {
			assertEquals("Errors in General Visit request", e.getMessage());
			assertNotNull("Validation Exception FaultBean is Null", e
					.getFaultInfo());
			List<ValidationError> errors = e.getFaultInfo().getErrors();
			assertNotNull("Validation Errors is Null", errors);
			assertEquals(1, errors.size());
			ValidationError error = errors.get(0);
			assertEquals(1, error.getCode());
			assertEquals("clinicId", error.getField());
		}
	}

	@Test
	public void testLog() {
		LogType type = LogType.SUCCESS;
		String msg = "logging over ws is slow";

		registrarBean.log(type, msg);

		replay(registrarBean);

		regWs.log(type, msg);

		verify(registrarBean);
	}

	@Test
	public void testSetMessageStatus() {
		String messageId = "12345678-1234-1234-1234-123456789012";
		Boolean success = true;

		registrarBean.setMessageStatus(messageId, success);

		replay(registrarBean);

		regWs.setMessageStatus(messageId, success);

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
