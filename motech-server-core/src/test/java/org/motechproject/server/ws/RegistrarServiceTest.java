package org.motechproject.server.ws;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.LogManager;

import org.easymock.Capture;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.motechproject.server.svc.BirthOutcomeChild;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.ws.BirthOutcome;
import org.motechproject.ws.Care;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.DeliveredBy;
import org.motechproject.ws.Gender;
import org.motechproject.ws.HIVStatus;
import org.motechproject.ws.LogType;
import org.motechproject.ws.Patient;
import org.motechproject.ws.server.RegistrarService;
import org.motechproject.ws.server.ValidationError;
import org.motechproject.ws.server.ValidationException;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RegistrarServiceTest {

	static ApplicationContext ctx;
	static RegistrarService regWs;
	static RegistrarBean registrarBean;
	static WebServiceModelConverter modelConverter;

	@BeforeClass
	public static void setUpClass() throws Exception {
		LogManager.getLogManager().readConfiguration(
				RegistrarServiceTest.class
						.getResourceAsStream("/jul-test.properties"));
		registrarBean = createMock(RegistrarBean.class);
		modelConverter = createMock(WebServiceModelConverter.class);
		ctx = new ClassPathXmlApplicationContext("test-context.xml");
		RegistrarWebService regService = (RegistrarWebService) ctx
				.getBean("registrarService");
		regService.setRegistrarBean(registrarBean);
		regService.setModelConverter(modelConverter);
		regWs = (RegistrarService) ctx.getBean("registrarClient");
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		ctx = null;
		regWs = null;
		registrarBean = null;
		modelConverter = null;
		LogManager.getLogManager().readConfiguration();
	}

	@Before
	public void setup() {
	}

	@After
	public void tearDown() throws Exception {
		reset(registrarBean, modelConverter);
	}

	@Test
	public void testRecordMotherANCVisit() throws ValidationException {
		String chpsId = "CHPSId", patientId = "123Test";
		Integer visitNumber = 1, ttDose = 1, iptDose = 1;
		Boolean itnUse = true;
		Date date = new Date();
		HIVStatus hivStatus = HIVStatus.NA;

		User nurse = new User(1);
		org.openmrs.Patient patient = new org.openmrs.Patient(2);

		expect(registrarBean.getNurseByCHPSId(chpsId)).andReturn(nurse);
		expect(registrarBean.getPatientByMotechId(patientId))
				.andReturn(patient);
		registrarBean.recordMotherANCVisit(nurse, date, patient, visitNumber,
				ttDose, iptDose, itnUse, hivStatus);

		replay(registrarBean);

		regWs.recordMotherANCVisit(chpsId, date, patientId, visitNumber,
				ttDose, iptDose, itnUse, hivStatus);

		verify(registrarBean);
	}

	@Test
	public void testRecordMotherANCVisitInvalidPatientId()
			throws ValidationException {
		String chpsId = "CHPSId", patientId = "123Test";
		Integer visitNumber = 1, ttDose = 1, iptDose = 1;
		Boolean itnUse = true;
		Date date = new Date();
		HIVStatus hivStatus = HIVStatus.NA;

		User nurse = new User(1);

		expect(registrarBean.getNurseByCHPSId(chpsId)).andReturn(nurse);
		expect(registrarBean.getPatientByMotechId(patientId)).andReturn(null);

		replay(registrarBean);

		try {
			regWs.recordMotherANCVisit(chpsId, date, patientId, visitNumber,
					ttDose, iptDose, itnUse, hivStatus);
			fail("Expected ValidationException");
		} catch (ValidationException e) {
			assertEquals("Errors in Record Mother ANC Visit request", e
					.getMessage());
			assertNotNull("Validation Exception FaultBean is Null", e
					.getFaultInfo());
			List<ValidationError> errors = e.getFaultInfo().getErrors();
			assertNotNull("Validation Errors is Null", errors);
			assertEquals(1, errors.size());
			ValidationError error = errors.get(0);
			assertEquals(1, error.getCode());
			assertEquals("MotechID", error.getField());
		}

		verify(registrarBean);
	}

	@Test
	public void testRecordPregnancyTermination() throws ValidationException {
		String chpsId = "CHPSId", patientId = "123Test";
		Integer abortionType = 1, complication = 1;
		Date date = new Date();

		User nurse = new User(1);
		org.openmrs.Patient patient = new org.openmrs.Patient(2);

		expect(registrarBean.getNurseByCHPSId(chpsId)).andReturn(nurse);
		expect(registrarBean.getPatientByMotechId(patientId))
				.andReturn(patient);
		registrarBean.recordPregnancyTermination(nurse, date, patient,
				abortionType, complication);

		replay(registrarBean);

		regWs.recordPregnancyTermination(chpsId, date, patientId, abortionType,
				complication);

		verify(registrarBean);
	}

	@Test
	public void testRecordPregnancyTerminationInvalidPatientId()
			throws ValidationException {
		String chpsId = "CHPSId", patientId = "123Test";
		Integer abortionType = 1, complication = 1;
		Date date = new Date();

		User nurse = new User(1);

		expect(registrarBean.getNurseByCHPSId(chpsId)).andReturn(nurse);
		expect(registrarBean.getPatientByMotechId(patientId)).andReturn(null);

		replay(registrarBean);

		try {
			regWs.recordPregnancyTermination(chpsId, date, patientId,
					abortionType, complication);
			fail("Expected ValidationException");
		} catch (ValidationException e) {
			assertEquals("Errors in Record Pregnancy Termination request", e
					.getMessage());
			assertNotNull("Validation Exception FaultBean is Null", e
					.getFaultInfo());
			List<ValidationError> errors = e.getFaultInfo().getErrors();
			assertNotNull("Validation Errors is Null", errors);
			assertEquals(1, errors.size());
			ValidationError error = errors.get(0);
			assertEquals(1, error.getCode());
			assertEquals("MotechID", error.getField());
		}

		verify(registrarBean);
	}

	@Test
	public void testRecordPregnancyDelivery() throws ValidationException {
		String chpsId = "CHPSId", patientId = "123Test";
		String child1Id = "246Test", child2Id = "468Test", child1Name = "Child1First", child2Name = "Child2First";
		Integer method = 1, outcome = 2, location = 1, cause = 1;
		Boolean maternalDeath = false, child1opv = true, child1bcg = false, child2opv = false, child2bcg = true;
		Date date = new Date();
		DeliveredBy deliveredBy = DeliveredBy.CHO;
		BirthOutcome child1birthOutcome = BirthOutcome.A;
		BirthOutcome child2birthOutcome = BirthOutcome.FSB;
		Gender child1Sex = Gender.FEMALE;
		Gender child2Sex = Gender.MALE;

		User nurse = new User(1);
		org.openmrs.Patient patient = new org.openmrs.Patient(2);

		Capture<List<BirthOutcomeChild>> outcomesCapture = new Capture<List<BirthOutcomeChild>>();

		expect(registrarBean.getNurseByCHPSId(chpsId)).andReturn(nurse);
		expect(registrarBean.getPatientByMotechId(patientId))
				.andReturn(patient);
		registrarBean.recordPregnancyDelivery(eq(nurse), eq(date), eq(patient),
				eq(method), eq(outcome), eq(location), eq(deliveredBy),
				eq(maternalDeath), eq(cause), capture(outcomesCapture));

		replay(registrarBean);

		regWs.recordPregnancyDelivery(chpsId, date, patientId, method, outcome,
				location, deliveredBy, maternalDeath, cause,
				child1birthOutcome, child1Id, child1Sex, child1Name, child1opv,
				child1bcg, child2birthOutcome, child2Id, child2Sex, child2Name,
				child2opv, child2bcg);

		verify(registrarBean);

		List<BirthOutcomeChild> outcomes = outcomesCapture.getValue();
		assertEquals(2, outcomes.size());

		BirthOutcomeChild child1 = outcomes.get(0);
		assertEquals(child1birthOutcome, child1.getOutcome());
		assertEquals(child1Id, child1.getPatientId());
		assertEquals(child1Name, child1.getFirstName());
		assertEquals(child1Sex, child1.getSex());
		assertEquals(child1bcg, child1.getBcg());
		assertEquals(child1opv, child1.getOpv());

		BirthOutcomeChild child2 = outcomes.get(1);
		assertEquals(child2birthOutcome, child2.getOutcome());
		assertEquals(child2Id, child2.getPatientId());
		assertEquals(child2Name, child2.getFirstName());
		assertEquals(child2Sex, child2.getSex());
		assertEquals(child2bcg, child2.getBcg());
		assertEquals(child2opv, child2.getOpv());
	}

	@Test
	public void testRecordPregnancyDeliveryOneChild()
			throws ValidationException {
		String chpsId = "CHPSId", patientId = "123Test";
		String child1Id = "246Test", child1Name = "Child1First";
		Integer method = 1, outcome = 2, location = 1, cause = 1;
		Boolean maternalDeath = false, child1opv = true, child1bcg = false;
		Date date = new Date();
		DeliveredBy deliveredBy = DeliveredBy.CHO;
		BirthOutcome child1birthOutcome = BirthOutcome.A;
		Gender child1Sex = Gender.FEMALE;

		User nurse = new User(1);
		org.openmrs.Patient patient = new org.openmrs.Patient(2);

		Capture<List<BirthOutcomeChild>> outcomesCapture = new Capture<List<BirthOutcomeChild>>();

		expect(registrarBean.getNurseByCHPSId(chpsId)).andReturn(nurse);
		expect(registrarBean.getPatientByMotechId(patientId))
				.andReturn(patient);
		registrarBean.recordPregnancyDelivery(eq(nurse), eq(date), eq(patient),
				eq(method), eq(outcome), eq(location), eq(deliveredBy),
				eq(maternalDeath), eq(cause), capture(outcomesCapture));

		replay(registrarBean);

		regWs.recordPregnancyDelivery(chpsId, date, patientId, method, outcome,
				location, deliveredBy, maternalDeath, cause,
				child1birthOutcome, child1Id, child1Sex, child1Name, child1opv,
				child1bcg, null, null, null, null, null, null);

		verify(registrarBean);

		List<BirthOutcomeChild> outcomes = outcomesCapture.getValue();
		assertEquals(1, outcomes.size());

		BirthOutcomeChild child1 = outcomes.get(0);
		assertEquals(child1birthOutcome, child1.getOutcome());
		assertEquals(child1Id, child1.getPatientId());
		assertEquals(child1Name, child1.getFirstName());
		assertEquals(child1Sex, child1.getSex());
		assertEquals(child1bcg, child1.getBcg());
		assertEquals(child1opv, child1.getOpv());
	}

	@Test
	public void testRecordPregnancyDeliveryInvalidPatientId()
			throws ValidationException {
		String chpsId = "CHPSId", patientId = "123Test", childId = "246Test", childName = "ChildFirstName";
		Integer method = 1, outcome = 2, location = 1, cause = 1;
		Boolean maternalDeath = false, child1opv = true, child1bcg = false, child2opv = false, child2bcg = true;
		Date date = new Date();
		DeliveredBy deliveredBy = DeliveredBy.CHO;
		BirthOutcome child1birthOutcome = BirthOutcome.A;
		BirthOutcome child2birthOutcome = BirthOutcome.FSB;
		Gender child1Sex = Gender.FEMALE;
		Gender child2Sex = Gender.MALE;

		User nurse = new User(1);

		expect(registrarBean.getNurseByCHPSId(chpsId)).andReturn(nurse);
		expect(registrarBean.getPatientByMotechId(patientId)).andReturn(null);

		replay(registrarBean);

		try {
			regWs.recordPregnancyDelivery(chpsId, date, patientId, method,
					outcome, location, deliveredBy, maternalDeath, cause,
					child1birthOutcome, childId, child1Sex, childName,
					child1opv, child1bcg, child2birthOutcome, childId,
					child2Sex, childName, child2opv, child2bcg);
			fail("Expected ValidationException");
		} catch (ValidationException e) {
			assertEquals("Errors in Record Pregnancy Delivery request", e
					.getMessage());
			assertNotNull("Validation Exception FaultBean is Null", e
					.getFaultInfo());
			List<ValidationError> errors = e.getFaultInfo().getErrors();
			assertNotNull("Validation Errors is Null", errors);
			assertEquals(1, errors.size());
			ValidationError error = errors.get(0);
			assertEquals(1, error.getCode());
			assertEquals("MotechID", error.getField());
		}

		verify(registrarBean);
	}

	@Test
	public void testRecordMotherPPCVisit() throws ValidationException {
		String chpsId = "CHPSId", patientId = "123Test";
		Integer visitNumber = 1, ttDose = 1;
		Boolean vitaminA = true;
		Date date = new Date();

		User nurse = new User(1);
		org.openmrs.Patient patient = new org.openmrs.Patient(2);

		expect(registrarBean.getNurseByCHPSId(chpsId)).andReturn(nurse);
		expect(registrarBean.getPatientByMotechId(patientId))
				.andReturn(patient);
		registrarBean.recordMotherPPCVisit(nurse, date, patient, visitNumber,
				vitaminA, ttDose);

		replay(registrarBean);

		regWs.recordMotherPPCVisit(chpsId, date, patientId, visitNumber,
				vitaminA, ttDose);

		verify(registrarBean);
	}

	@Test
	public void testRecordMotherPPCVisitInvalidPatientId()
			throws ValidationException {
		String chpsId = "CHPSId", patientId = "123Test";
		Integer visitNumber = 1, ttDose = 1;
		Boolean vitaminA = true;
		Date date = new Date();

		User nurse = new User(1);

		expect(registrarBean.getNurseByCHPSId(chpsId)).andReturn(nurse);
		expect(registrarBean.getPatientByMotechId(patientId)).andReturn(null);

		replay(registrarBean);

		try {
			regWs.recordMotherPPCVisit(chpsId, date, patientId, visitNumber,
					vitaminA, ttDose);
			fail("Expected ValidationException");
		} catch (ValidationException e) {
			assertEquals("Errors in Record Mother PPC Visit request", e
					.getMessage());
			assertNotNull("Validation Exception FaultBean is Null", e
					.getFaultInfo());
			List<ValidationError> errors = e.getFaultInfo().getErrors();
			assertNotNull("Validation Errors is Null", errors);
			assertEquals(1, errors.size());
			ValidationError error = errors.get(0);
			assertEquals(1, error.getCode());
			assertEquals("MotechID", error.getField());
		}

		verify(registrarBean);
	}

	@Test
	public void testRecordDeath() throws ValidationException {
		String chpsId = "CHPSId", patientId = "123Test";
		Integer cause = 1;
		Date date = new Date();

		User nurse = new User(1);
		org.openmrs.Patient patient = new org.openmrs.Patient(2);

		expect(registrarBean.getNurseByCHPSId(chpsId)).andReturn(nurse);
		expect(registrarBean.getPatientByMotechId(patientId))
				.andReturn(patient);
		registrarBean.recordDeath(nurse, date, patient, cause);

		replay(registrarBean);

		regWs.recordDeath(chpsId, date, patientId, cause);

		verify(registrarBean);
	}

	@Test
	public void testRecordDeathInvalidPatientId() throws ValidationException {
		String chpsId = "CHPSId", patientId = "123Test";
		Integer cause = 1;
		Date date = new Date();

		User nurse = new User(1);

		expect(registrarBean.getNurseByCHPSId(chpsId)).andReturn(nurse);
		expect(registrarBean.getPatientByMotechId(patientId)).andReturn(null);

		replay(registrarBean);

		try {
			regWs.recordDeath(chpsId, date, patientId, cause);
			fail("Expected ValidationException");
		} catch (ValidationException e) {
			assertEquals("Errors in Record Death request", e.getMessage());
			assertNotNull("Validation Exception FaultBean is Null", e
					.getFaultInfo());
			List<ValidationError> errors = e.getFaultInfo().getErrors();
			assertNotNull("Validation Errors is Null", errors);
			assertEquals(1, errors.size());
			ValidationError error = errors.get(0);
			assertEquals(1, error.getCode());
			assertEquals("MotechID", error.getField());
		}

		verify(registrarBean);
	}

	@Test
	public void testRecordChildPNCVisit() throws ValidationException {
		String chpsId = "CHPSId", patientId = "123Test";
		Integer opvDose = 1, pentaDose = 1;
		Boolean bcg = true, yellowFever = true, csm = true, measles = true, ipti = true, vitaminA = true;
		Date date = new Date();

		User nurse = new User(1);
		org.openmrs.Patient patient = new org.openmrs.Patient(2);

		expect(registrarBean.getNurseByCHPSId(chpsId)).andReturn(nurse);
		expect(registrarBean.getPatientByMotechId(patientId))
				.andReturn(patient);
		registrarBean.recordChildPNCVisit(nurse, date, patient, bcg, opvDose,
				pentaDose, yellowFever, csm, measles, ipti, vitaminA);

		replay(registrarBean);

		regWs.recordChildPNCVisit(chpsId, date, patientId, bcg, opvDose,
				pentaDose, yellowFever, csm, measles, ipti, vitaminA);

		verify(registrarBean);
	}

	@Test
	public void testRecordChildPNCVisitInvalidPatientId()
			throws ValidationException {
		String chpsId = "CHPSId", patientId = "123Test";
		Integer opvDose = 1, pentaDose = 1;
		Boolean bcg = true, yellowFever = true, csm = true, measles = true, ipti = true, vitaminA = true;
		Date date = new Date();

		User nurse = new User(1);

		expect(registrarBean.getNurseByCHPSId(chpsId)).andReturn(nurse);
		expect(registrarBean.getPatientByMotechId(patientId)).andReturn(null);

		replay(registrarBean);

		try {
			regWs.recordChildPNCVisit(chpsId, date, patientId, bcg, opvDose,
					pentaDose, yellowFever, csm, measles, ipti, vitaminA);
			fail("Expected ValidationException");
		} catch (ValidationException e) {
			assertEquals("Errors in Record Child PNC Visit request", e
					.getMessage());
			assertNotNull("Validation Exception FaultBean is Null", e
					.getFaultInfo());
			List<ValidationError> errors = e.getFaultInfo().getErrors();
			assertNotNull("Validation Errors is Null", errors);
			assertEquals(1, errors.size());
			ValidationError error = errors.get(0);
			assertEquals(1, error.getCode());
			assertEquals("MotechID", error.getField());
		}

		verify(registrarBean);
	}

	@Test
	public void testRegisterChild() throws ValidationException {
		Date childDob = new Date(), nhisExpires = new Date();
		String nurseId = "FGH267", motherRegNum = "ABC123", childRegNum = "DEF456", childFirstName = "Sarah", nhis = "14567";
		Gender childGender = Gender.FEMALE;

		User nurse = new User(1);
		org.openmrs.Patient mother = new org.openmrs.Patient(2);
		org.openmrs.Patient child = null;

		expect(registrarBean.getNurseByCHPSId(nurseId)).andReturn(nurse);
		expect(registrarBean.getPatientByMotechId(motherRegNum)).andReturn(
				mother);
		expect(registrarBean.getPatientByMotechId(childRegNum))
				.andReturn(child);

		expect(
				registrarBean.registerChild(nurse, mother, childRegNum,
						childDob, childGender, childFirstName, nhis,
						nhisExpires)).andReturn(new org.openmrs.Patient());

		replay(registrarBean);

		regWs.registerChild(nurseId, motherRegNum, childRegNum, childDob,
				childGender, childFirstName, nhis, nhisExpires);

		verify(registrarBean);
	}

	@Test
	public void testRegisterChildAllErrors() {
		Date nhisExpires = new Date();
		Calendar dobCal = new GregorianCalendar();
		dobCal.set(Calendar.YEAR, dobCal.get(Calendar.YEAR) - 6);
		Date childDob = dobCal.getTime();
		String nurseId = "FGH267", motherRegNum = "ABC123", childRegNum = "DEF456", childFirstName = "Sarah", nhis = "14567";
		Gender childGender = Gender.FEMALE;

		User nurse = null;
		org.openmrs.Patient mother = null;
		org.openmrs.Patient child = new org.openmrs.Patient(3);

		expect(registrarBean.getNurseByCHPSId(nurseId)).andReturn(nurse);
		expect(registrarBean.getPatientByMotechId(motherRegNum)).andReturn(
				mother);
		expect(registrarBean.getPatientByMotechId(childRegNum))
				.andReturn(child);

		replay(registrarBean);

		try {
			regWs.registerChild(nurseId, motherRegNum, childRegNum, childDob,
					childGender, childFirstName, nhis, nhisExpires);
			fail("Expected ValidationException");
		} catch (ValidationException e) {
			assertEquals("Errors in Register Child request", e.getMessage());
			assertNotNull("Validation Exception FaultBean is Null", e
					.getFaultInfo());
			List<ValidationError> errors = e.getFaultInfo().getErrors();
			assertNotNull("Validation Errors is Null", errors);
			assertEquals(4, errors.size());
			ValidationError nurseError = errors.get(0);
			assertEquals(1, nurseError.getCode());
			assertEquals("CHPSID", nurseError.getField());
			ValidationError motherError = errors.get(1);
			assertEquals(1, motherError.getCode());
			assertEquals("MotherMotechID", motherError.getField());
			ValidationError childError = errors.get(2);
			assertEquals(2, childError.getCode());
			assertEquals("ChildMotechID", childError.getField());
			ValidationError dobError = errors.get(3);
			assertEquals(2, dobError.getCode());
			assertEquals("DoB", dobError.getField());
		}

		verify(registrarBean);
	}

	@Test
	public void testEditPatient() throws ValidationException {
		String nurseId = "FGH267", patientRegNum = "ABC123", primaryPhone = "12075557894", secondaryPhone = "12075557895", nhis = "125";
		ContactNumberType primaryPhoneType = ContactNumberType.PERSONAL, secondaryPhoneType = ContactNumberType.PUBLIC;
		Date nhisExpires = new Date();

		User nurse = new User(1);
		org.openmrs.Patient patient = new org.openmrs.Patient(2);

		expect(registrarBean.getNurseByCHPSId(nurseId)).andReturn(nurse);
		expect(registrarBean.getPatientByMotechId(patientRegNum)).andReturn(
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
		org.openmrs.Patient patient = null;

		expect(registrarBean.getNurseByCHPSId(nurseId)).andReturn(nurse);
		expect(registrarBean.getPatientByMotechId(patientRegNum)).andReturn(
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
			assertEquals("CHPSID", nurseError.getField());
			ValidationError patientError = errors.get(1);
			assertEquals(1, patientError.getCode());
			assertEquals("MotechID", patientError.getField());
		}

		verify(registrarBean);
	}

	@Test
	public void testStopPregnancyProgram() throws ValidationException {
		String nurseId = "FGH267", patientRegNum = "ABC123";

		User nurse = new User(1);
		org.openmrs.Patient patient = new org.openmrs.Patient(2);

		expect(registrarBean.getNurseByCHPSId(nurseId)).andReturn(nurse);
		expect(registrarBean.getPatientByMotechId(patientRegNum)).andReturn(
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
		org.openmrs.Patient patient = null;

		expect(registrarBean.getNurseByCHPSId(nurseId)).andReturn(nurse);
		expect(registrarBean.getPatientByMotechId(patientRegNum)).andReturn(
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
			assertEquals("CHPSID", nurseError.getField());
			ValidationError patientError = errors.get(1);
			assertEquals(1, patientError.getCode());
			assertEquals("MotechID", patientError.getField());
		}

		verify(registrarBean);
	}

	@Test
	public void testGeneralVisit() throws ValidationException {
		String chpsId = "Facility1", serial = "Test123";
		Gender gender = Gender.MALE;
		Integer diagnosis = 5, secondDiagnosis = 6;
		Boolean insured = true, newCase = true, referral = false;
		Date date = new Date();

		User nurse = new User(1);

		expect(registrarBean.getNurseByCHPSId(chpsId)).andReturn(nurse);

		registrarBean.recordGeneralVisit(chpsId, date, serial, gender, date,
				insured, newCase, diagnosis, secondDiagnosis, referral);

		replay(registrarBean);

		regWs.recordGeneralVisit(chpsId, date, serial, gender, date, insured,
				newCase, diagnosis, secondDiagnosis, referral);

		verify(registrarBean);
	}

	@Test
	public void testGeneralVisitInvalidNurseId() {
		String chpsId = null, serial = "Test123";
		Gender gender = Gender.MALE;
		Integer diagnosis = 5, secondDiagnosis = 6;
		Boolean insured = true, newCase = true, referral = false;
		Date date = new Date();

		try {
			regWs.recordGeneralVisit(chpsId, date, serial, gender, date,
					insured, newCase, diagnosis, secondDiagnosis, referral);
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
			assertEquals("CHPSID", error.getField());
		}
	}

	@Test
	public void testRecordChildVisit() throws ValidationException {
		String chpsId = "CHPSId", serial = "Test123", patientId = "123Test";
		Integer diagnosis = 5, secondDiagnosis = 6;
		Boolean newCase = true, referral = false;
		Date date = new Date();

		User nurse = new User(1);
		org.openmrs.Patient patient = new org.openmrs.Patient(2);

		expect(registrarBean.getNurseByCHPSId(chpsId)).andReturn(nurse);
		expect(registrarBean.getPatientByMotechId(patientId))
				.andReturn(patient);
		registrarBean.recordChildVisit(nurse, date, patient, serial, newCase,
				diagnosis, secondDiagnosis, referral);

		replay(registrarBean);

		regWs.recordChildVisit(chpsId, date, serial, patientId, newCase,
				diagnosis, secondDiagnosis, referral);

		verify(registrarBean);
	}

	@Test
	public void testRecordChildVisitInvalidPatientId()
			throws ValidationException {
		String chpsId = "CHPSId", serial = "Test123", patientId = "123Test";
		Integer diagnosis = 5, secondDiagnosis = 6;
		Boolean newCase = true, referral = false;
		Date date = new Date();

		User nurse = new User(1);

		expect(registrarBean.getNurseByCHPSId(chpsId)).andReturn(nurse);
		expect(registrarBean.getPatientByMotechId(patientId)).andReturn(null);

		replay(registrarBean);

		try {
			regWs.recordChildVisit(chpsId, date, serial, patientId, newCase,
					diagnosis, secondDiagnosis, referral);
			fail("Expected ValidationException");
		} catch (ValidationException e) {
			assertEquals("Errors in Record Child Visit request", e.getMessage());
			assertNotNull("Validation Exception FaultBean is Null", e
					.getFaultInfo());
			List<ValidationError> errors = e.getFaultInfo().getErrors();
			assertNotNull("Validation Errors is Null", errors);
			assertEquals(1, errors.size());
			ValidationError error = errors.get(0);
			assertEquals(1, error.getCode());
			assertEquals("MotechID", error.getField());
		}

		verify(registrarBean);
	}

	@Test
	public void testRecordMotherVisit() throws ValidationException {
		String chpsId = "CHPSId", serial = "Test123", patientId = "123Test";
		Integer diagnosis = 5, secondDiagnosis = 6;
		Boolean newCase = true, referral = false;
		Date date = new Date();

		User nurse = new User(1);
		org.openmrs.Patient patient = new org.openmrs.Patient(2);

		expect(registrarBean.getNurseByCHPSId(chpsId)).andReturn(nurse);
		expect(registrarBean.getPatientByMotechId(patientId))
				.andReturn(patient);
		registrarBean.recordMotherVisit(nurse, date, patient, serial, newCase,
				diagnosis, secondDiagnosis, referral);

		replay(registrarBean);

		regWs.recordMotherVisit(chpsId, date, serial, patientId, newCase,
				diagnosis, secondDiagnosis, referral);

		verify(registrarBean);
	}

	@Test
	public void testRecordMotherVisitInvalidPatientId()
			throws ValidationException {
		String chpsId = "CHPSId", serial = "Test123", patientId = "123Test";
		Integer diagnosis = 5, secondDiagnosis = 6;
		Boolean newCase = true, referral = false;
		Date date = new Date();

		User nurse = new User(1);

		expect(registrarBean.getNurseByCHPSId(chpsId)).andReturn(nurse);
		expect(registrarBean.getPatientByMotechId(patientId)).andReturn(null);

		replay(registrarBean);

		try {
			regWs.recordMotherVisit(chpsId, date, serial, patientId, newCase,
					diagnosis, secondDiagnosis, referral);
			fail("Expected ValidationException");
		} catch (ValidationException e) {
			assertEquals("Errors in Record Mother Visit request", e
					.getMessage());
			assertNotNull("Validation Exception FaultBean is Null", e
					.getFaultInfo());
			List<ValidationError> errors = e.getFaultInfo().getErrors();
			assertNotNull("Validation Errors is Null", errors);
			assertEquals(1, errors.size());
			ValidationError error = errors.get(0);
			assertEquals(1, error.getCode());
			assertEquals("MotechID", error.getField());
		}

		verify(registrarBean);
	}

	@Test
	public void testQueryANCDefaulters() throws ValidationException {
		String facilityId = "FacilityId", chpsId = "CHPSId";

		expect(registrarBean.getNurseByCHPSId(chpsId)).andReturn(new User(1));

		replay(registrarBean, modelConverter);

		Care[] cares = regWs.queryANCDefaulters(facilityId, chpsId);

		verify(registrarBean, modelConverter);

		assertNotNull("Care result array is null", cares);
		assertEquals(0, cares.length);
	}

	@Test
	public void testQueryTTDefaulters() throws ValidationException {
		String facilityId = "FacilityId", chpsId = "CHPSId";

		expect(registrarBean.getNurseByCHPSId(chpsId)).andReturn(new User(1));

		replay(registrarBean, modelConverter);

		Care[] cares = regWs.queryTTDefaulters(facilityId, chpsId);

		verify(registrarBean, modelConverter);

		assertNotNull("Care result array is null", cares);
		assertEquals(0, cares.length);
	}

	@Test
	public void testQueryPPCDefaulters() throws ValidationException {
		String facilityId = "FacilityId", chpsId = "CHPSId";

		expect(registrarBean.getNurseByCHPSId(chpsId)).andReturn(new User(1));

		replay(registrarBean, modelConverter);

		Care[] cares = regWs.queryPPCDefaulters(facilityId, chpsId);

		verify(registrarBean, modelConverter);

		assertNotNull("Care result array is null", cares);
		assertEquals(0, cares.length);
	}

	@Test
	public void testQueryPNCDefaulters() throws ValidationException {
		String facilityId = "FacilityId", chpsId = "CHPSId";

		expect(registrarBean.getNurseByCHPSId(chpsId)).andReturn(new User(1));

		replay(registrarBean, modelConverter);

		Care[] cares = regWs.queryPNCDefaulters(facilityId, chpsId);

		verify(registrarBean, modelConverter);

		assertNotNull("Care result array is null", cares);
		assertEquals(0, cares.length);
	}

	@Test
	public void testQueryCWCDefaulters() throws ValidationException {
		String facilityId = "FacilityId", chpsId = "CHPSId";

		expect(registrarBean.getNurseByCHPSId(chpsId)).andReturn(new User(1));

		replay(registrarBean, modelConverter);

		Care[] cares = regWs.queryCWCDefaulters(facilityId, chpsId);

		verify(registrarBean, modelConverter);

		assertNotNull("Care result array is null", cares);
		assertEquals(0, cares.length);
	}

	@Test
	public void testQueryUpcomingDeliveries() throws ValidationException {
		String facilityId = "FacilityId", chpsId = "CHPSId";

		List<Obs> pregnancies = new ArrayList<Obs>();
		pregnancies.add(new Obs());

		expect(registrarBean.getNurseByCHPSId(chpsId)).andReturn(new User(1));
		expect(registrarBean.getUpcomingPregnanciesDueDate()).andReturn(
				pregnancies);
		expect(modelConverter.dueDatesToWebServicePatients(pregnancies))
				.andReturn(new Patient[1]);

		replay(registrarBean, modelConverter);

		Patient[] patients = regWs.queryUpcomingDeliveries(facilityId, chpsId);

		verify(registrarBean, modelConverter);

		assertNotNull("Patient result array is null", patients);
		assertEquals(1, patients.length);
	}

	@Test
	public void testQueryRecentDeliveries() throws ValidationException {
		String facilityId = "FacilityId", chpsId = "CHPSId";

		List<Encounter> deliveries = new ArrayList<Encounter>();
		deliveries.add(new Encounter());

		expect(registrarBean.getNurseByCHPSId(chpsId)).andReturn(new User(1));
		expect(registrarBean.getRecentDeliveries()).andReturn(deliveries);
		expect(modelConverter.deliveriesToWebServicePatients(deliveries))
				.andReturn(new Patient[1]);

		replay(registrarBean, modelConverter);

		Patient[] patients = regWs.queryRecentDeliveries(facilityId, chpsId);

		verify(registrarBean, modelConverter);

		assertNotNull("Patient result array is null", patients);
		assertEquals(1, patients.length);
	}

	@Test
	public void testQueryOverdueDeliveries() throws ValidationException {
		String facilityId = "FacilityId", chpsId = "CHPSId";

		List<Obs> pregnancies = new ArrayList<Obs>();
		pregnancies.add(new Obs());

		expect(registrarBean.getNurseByCHPSId(chpsId)).andReturn(new User(1));
		expect(registrarBean.getOverduePregnanciesDueDate()).andReturn(
				pregnancies);
		expect(modelConverter.dueDatesToWebServicePatients(pregnancies))
				.andReturn(new Patient[1]);

		replay(registrarBean, modelConverter);

		Patient[] patients = regWs.queryOverdueDeliveries(facilityId, chpsId);

		verify(registrarBean, modelConverter);

		assertNotNull("Patient result array is null", patients);
		assertEquals(1, patients.length);
	}

	@Test
	public void testQueryUpcomingCare() throws ValidationException {
		String facilityId = "FacilityId", chpsId = "CHPSId", motechId = "MotechId";

		expect(registrarBean.getNurseByCHPSId(chpsId)).andReturn(new User(1));

		replay(registrarBean, modelConverter);

		Patient patient = regWs.queryUpcomingCare(facilityId, chpsId, motechId);

		verify(registrarBean, modelConverter);

		assertNotNull("Patient result is null", patient);
	}

	@Test
	public void testQueryMotechId() throws ValidationException {
		String chpsId = "CHPSId", firstName = "FirstName", lastName = "LastName", prefName = "PrefName";
		String nhis = "NHIS", phone = "Phone";
		Date birthDate = new Date();

		List<org.openmrs.Patient> patients = new ArrayList<org.openmrs.Patient>();
		patients.add(new org.openmrs.Patient(1));

		expect(registrarBean.getNurseByCHPSId(chpsId)).andReturn(new User(1));
		expect(
				registrarBean.getPatients(firstName, lastName, prefName,
						birthDate, null, phone, nhis)).andReturn(patients);
		expect(modelConverter.patientToWebService(patients, true)).andReturn(
				new Patient[1]);

		replay(registrarBean, modelConverter);

		Patient[] wsPatients = regWs.queryMotechId(chpsId, firstName, lastName,
				prefName, birthDate, nhis, phone);

		verify(registrarBean, modelConverter);

		assertNotNull("Patient result array is null", wsPatients);
		assertEquals(1, wsPatients.length);
	}

	@Test
	public void testQueryPatient() throws ValidationException {
		String chpsId = "CHPSId", motechId = "MotechId";

		org.openmrs.Patient patient = new org.openmrs.Patient(1);

		expect(registrarBean.getNurseByCHPSId(chpsId)).andReturn(new User(1));
		expect(registrarBean.getPatientByMotechId(motechId)).andReturn(patient);
		expect(modelConverter.patientToWebService(eq(patient), eq(false)))
				.andReturn(new Patient());

		replay(registrarBean, modelConverter);

		regWs.queryPatient(chpsId, motechId);

		verify(registrarBean, modelConverter);
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
