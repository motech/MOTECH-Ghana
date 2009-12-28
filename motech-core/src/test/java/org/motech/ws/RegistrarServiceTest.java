package org.motech.ws;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
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
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.Gender;
import org.motechproject.ws.LogType;
import org.motechproject.ws.server.RegistrarService;
import org.motechproject.ws.server.ValidationError;
import org.motechproject.ws.server.ValidationException;
import org.openmrs.Patient;
import org.openmrs.User;
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
	public void testRegisterChild() throws ValidationException {
		Date regDate = new Date(), childDob = new Date(), nhisExpires = new Date();
		String nurseId = "FGH267", motherRegNum = "ABC123", childRegNum = "DEF456", childFirstName = "Sarah", nhis = "14567";
		Gender childGender = Gender.FEMALE;

		User nurse = new User(1);
		Patient mother = new Patient(2);
		Patient child = null;

		expect(registrarBean.getNurseByCHPSId(nurseId)).andReturn(nurse);
		expect(registrarBean.getPatientBySerial(motherRegNum))
				.andReturn(mother);
		expect(registrarBean.getPatientBySerial(childRegNum)).andReturn(child);

		registrarBean.registerChild(nurse, regDate, mother, childRegNum,
				childDob, childGender, childFirstName, nhis, nhisExpires);

		replay(registrarBean);

		regWs.registerChild(nurseId, regDate, motherRegNum, childRegNum,
				childDob, childGender, childFirstName, nhis, nhisExpires);

		verify(registrarBean);
	}

	@Test
	public void testRegisterChildAllErrors() {
		Date regDate = new Date(), childDob = new Date(), nhisExpires = new Date();
		String nurseId = "FGH267", motherRegNum = "ABC123", childRegNum = "DEF456", childFirstName = "Sarah", nhis = "14567";
		Gender childGender = Gender.FEMALE;

		User nurse = null;
		Patient mother = null;
		Patient child = new Patient(3);

		expect(registrarBean.getNurseByCHPSId(nurseId)).andReturn(nurse);
		expect(registrarBean.getPatientBySerial(motherRegNum))
				.andReturn(mother);
		expect(registrarBean.getPatientBySerial(childRegNum)).andReturn(child);

		replay(registrarBean);

		try {
			regWs.registerChild(nurseId, regDate, motherRegNum, childRegNum,
					childDob, childGender, childFirstName, nhis, nhisExpires);
			fail("Expected ValidationException");
		} catch (ValidationException e) {
			assertEquals("Errors in Register Child request", e.getMessage());
			assertNotNull("Validation Exception FaultBean is Null", e
					.getFaultInfo());
			List<ValidationError> errors = e.getFaultInfo().getErrors();
			assertNotNull("Validation Errors is Null", errors);
			assertEquals(3, errors.size());
			ValidationError nurseError = errors.get(0);
			assertEquals(1, nurseError.getCode());
			assertEquals("chpsId", nurseError.getField());
			ValidationError motherError = errors.get(1);
			assertEquals(1, motherError.getCode());
			assertEquals("motherRegNum", motherError.getField());
			ValidationError childError = errors.get(2);
			assertEquals(2, childError.getCode());
			assertEquals("childRegNum", childError.getField());
		}

		verify(registrarBean);
	}

	@Test
	public void testEditPatient() throws ValidationException {
		String nurseId = "FGH267", patientRegNum = "ABC123", primaryPhone = "12075557894", secondaryPhone = "12075557895", nhis = "125";
		ContactNumberType primaryPhoneType = ContactNumberType.PERSONAL, secondaryPhoneType = ContactNumberType.PUBLIC;
		Date nhisExpires = new Date();

		User nurse = new User(1);
		Patient patient = new Patient(2);

		expect(registrarBean.getNurseByCHPSId(nurseId)).andReturn(nurse);
		expect(registrarBean.getPatientBySerial(patientRegNum)).andReturn(
				patient);

		registrarBean.editPatient(nurse, patient, primaryPhone,
				primaryPhoneType, secondaryPhone, secondaryPhoneType, nhis,
				nhisExpires);
		replay(registrarBean);

		regWs.editPatient(nurseId, patientRegNum, primaryPhone,
				primaryPhoneType, secondaryPhone, secondaryPhoneType, nhis,
				nhisExpires);

		verify(registrarBean);
	}

	@Test
	public void testEditPatientAllErrors() {
		String nurseId = "FGH267", patientRegNum = "ABC123", primaryPhone = "12075557894", secondaryPhone = "12075557895", nhis = "125";
		ContactNumberType primaryPhoneType = ContactNumberType.PERSONAL, secondaryPhoneType = ContactNumberType.PUBLIC;
		Date nhisExpires = new Date();

		User nurse = null;
		Patient patient = null;

		expect(registrarBean.getNurseByCHPSId(nurseId)).andReturn(nurse);
		expect(registrarBean.getPatientBySerial(patientRegNum)).andReturn(
				patient);

		replay(registrarBean);

		try {
			regWs.editPatient(nurseId, patientRegNum, primaryPhone,
					primaryPhoneType, secondaryPhone, secondaryPhoneType, nhis,
					nhisExpires);
			fail("Expected ValidationException");
		} catch (ValidationException e) {
			assertEquals("Errors in Edit Patient request", e.getMessage());
			assertNotNull("Validation Exception FaultBean is Null", e
					.getFaultInfo());
			List<ValidationError> errors = e.getFaultInfo().getErrors();
			assertNotNull("Validation Errors is Null", errors);
			assertEquals(2, errors.size());
			ValidationError nurseError = errors.get(0);
			assertEquals(1, nurseError.getCode());
			assertEquals("chpsId", nurseError.getField());
			ValidationError patientError = errors.get(1);
			assertEquals(1, patientError.getCode());
			assertEquals("patientRegNum", patientError.getField());
		}

		verify(registrarBean);
	}

	@Test
	public void testStopPregnancyProgram() throws ValidationException {
		String nurseId = "FGH267", patientRegNum = "ABC123";

		User nurse = new User(1);
		Patient patient = new Patient(2);

		expect(registrarBean.getNurseByCHPSId(nurseId)).andReturn(nurse);
		expect(registrarBean.getPatientBySerial(patientRegNum)).andReturn(
				patient);

		registrarBean.stopPregnancyProgram(nurse, patient);

		replay(registrarBean);

		regWs.stopPregnancyProgram(nurseId, patientRegNum);

		verify(registrarBean);
	}

	@Test
	public void testStopPregnancyProgramAllErrors() {
		String nurseId = "FGH267", patientRegNum = "ABC123";

		User nurse = null;
		Patient patient = null;

		expect(registrarBean.getNurseByCHPSId(nurseId)).andReturn(nurse);
		expect(registrarBean.getPatientBySerial(patientRegNum)).andReturn(
				patient);

		replay(registrarBean);

		try {
			regWs.stopPregnancyProgram(nurseId, patientRegNum);
			fail("Expected ValidationException");
		} catch (ValidationException e) {
			assertEquals("Errors in Stop Pregnancy Program request", e
					.getMessage());
			assertNotNull("Validation Exception FaultBean is Null", e
					.getFaultInfo());
			List<ValidationError> errors = e.getFaultInfo().getErrors();
			assertNotNull("Validation Errors is Null", errors);
			assertEquals(2, errors.size());
			ValidationError nurseError = errors.get(0);
			assertEquals(1, nurseError.getCode());
			assertEquals("chpsId", nurseError.getField());
			ValidationError patientError = errors.get(1);
			assertEquals(1, patientError.getCode());
			assertEquals("patientRegNum", patientError.getField());
		}

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
			assertEquals(3, error.getCode());
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
