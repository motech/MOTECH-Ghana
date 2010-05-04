package org.motechproject.server.ws;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.LogManager;

import org.easymock.Capture;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.motechproject.server.model.ExpectedEncounter;
import org.motechproject.server.model.ExpectedObs;
import org.motechproject.server.svc.BirthOutcomeChild;
import org.motechproject.server.svc.OpenmrsBean;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.ws.BirthOutcome;
import org.motechproject.ws.Care;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.DayOfWeek;
import org.motechproject.ws.Gender;
import org.motechproject.ws.HIVResult;
import org.motechproject.ws.HowLearned;
import org.motechproject.ws.InterestReason;
import org.motechproject.ws.LogType;
import org.motechproject.ws.MediaType;
import org.motechproject.ws.Patient;
import org.motechproject.ws.RegistrantType;
import org.motechproject.ws.RegistrationMode;
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
	static OpenmrsBean openmrsBean;
	static WebServiceModelConverter modelConverter;

	@BeforeClass
	public static void setUpClass() throws Exception {
		LogManager.getLogManager().readConfiguration(
				RegistrarServiceTest.class
						.getResourceAsStream("/jul-test.properties"));
		registrarBean = createMock(RegistrarBean.class);
		openmrsBean = createMock(OpenmrsBean.class);
		modelConverter = createMock(WebServiceModelConverter.class);
		ctx = new ClassPathXmlApplicationContext("test-context.xml");
		RegistrarWebService regService = (RegistrarWebService) ctx
				.getBean("registrarService");
		regService.setRegistrarBean(registrarBean);
		regService.setOpenmrsBean(openmrsBean);
		regService.setModelConverter(modelConverter);
		regWs = (RegistrarService) ctx.getBean("registrarClient");
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		ctx = null;
		regWs = null;
		registrarBean = null;
		openmrsBean = null;
		modelConverter = null;
		LogManager.getLogManager().readConfiguration();
	}

	@Before
	public void setup() {
	}

	@After
	public void tearDown() throws Exception {
		reset(registrarBean, modelConverter, openmrsBean);
	}

	@Test
	public void testRecordMotherANCVisit() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		Integer visitNumber = 1, location = 1, bpSystolic = 130, bpDiastolic = 80;
		Double weight = 63.3, hemoglobin = 11.1;
		String house = "House", community = "Community", comments = "Comments";
		Integer ttDose = 1, iptDose = 1, fht = 130, fhr = 130;
		Boolean iptReactive = false, itnUse = true, urineProtein = false, urineGlucose = false;
		Boolean vdrlReactive = false, vdrlTreatment = false, dewormer = false, maleInvolved = true;
		Boolean pmtct = false, preTest = false, postTest = false, pmtctTreatment = false, referred = false;
		Date date = new Date();
		HIVResult hivResult = HIVResult.NO_TEST;

		User nurse = new User(1);
		org.openmrs.Patient patient = new org.openmrs.Patient(2);

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				nurse);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);
		registrarBean.recordMotherANCVisit(nurse, facilityId, date, patient,
				visitNumber, location, house, community, date, bpSystolic,
				bpDiastolic, weight, ttDose, iptDose, iptReactive, itnUse, fht,
				fhr, urineProtein, urineGlucose, hemoglobin, vdrlReactive,
				vdrlTreatment, dewormer, maleInvolved, pmtct, preTest,
				hivResult, postTest, pmtctTreatment, referred, date, comments);

		replay(registrarBean, openmrsBean);

		regWs.recordMotherANCVisit(staffId, facilityId, date, motechId,
				visitNumber, location, house, community, date, bpSystolic,
				bpDiastolic, weight, ttDose, iptDose, iptReactive, itnUse, fht,
				fhr, urineProtein, urineGlucose, hemoglobin, vdrlReactive,
				vdrlTreatment, dewormer, maleInvolved, pmtct, preTest,
				hivResult, postTest, pmtctTreatment, referred, date, comments);

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void testRecordMotherANCVisitInvalidPatientId()
			throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		Integer visitNumber = 1, location = 1, bpSystolic = 130, bpDiastolic = 80;
		Double weight = 63.3, hemoglobin = 11.1;
		String house = "House", community = "Community", comments = "Comments";
		Integer ttDose = 1, iptDose = 1, fht = 130, fhr = 130;
		Boolean iptReactive = false, itnUse = true, urineProtein = false, urineGlucose = false;
		Boolean vdrlReactive = false, vdrlTreatment = false, dewormer = false, maleInvolved = true;
		Boolean pmtct = false, preTest = false, postTest = false, pmtctTreatment = false, referred = false;
		Date date = new Date();
		HIVResult hivResult = HIVResult.NO_TEST;

		User nurse = new User(1);

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				nurse);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(null);

		replay(registrarBean, openmrsBean);

		try {
			regWs.recordMotherANCVisit(staffId, facilityId, date, motechId,
					visitNumber, location, house, community, date, bpSystolic,
					bpDiastolic, weight, ttDose, iptDose, iptReactive, itnUse,
					fht, fhr, urineProtein, urineGlucose, hemoglobin,
					vdrlReactive, vdrlTreatment, dewormer, maleInvolved, pmtct,
					preTest, hivResult, postTest, pmtctTreatment, referred,
					date, comments);
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

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void testRecordPregnancyTermination() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		Integer terminationType = 1, procedure = 2;
		Integer[] complications = new Integer[] { 1, 3, 5, 7 };
		Boolean maternalDeath = false, referred = false, postCounsel = true, postAccept = true;
		String comments = "Comments";
		Date date = new Date();

		User nurse = new User(1);
		org.openmrs.Patient patient = new org.openmrs.Patient(2);

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				nurse);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);
		registrarBean.recordPregnancyTermination(nurse, date, patient,
				terminationType, null);

		replay(registrarBean, openmrsBean);

		regWs.recordPregnancyTermination(staffId, facilityId, date, motechId,
				terminationType, procedure, complications, maternalDeath,
				referred, postCounsel, postAccept, comments);

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void testRecordPregnancyTerminationInvalidPatientId()
			throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		Integer terminationType = 1, procedure = 2;
		Integer[] complications = new Integer[] { 1, 3, 5, 7 };
		Boolean maternalDeath = false, referred = false, postCounsel = true, postAccept = true;
		String comments = "Comments";
		Date date = new Date();

		User nurse = new User(1);

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				nurse);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(null);

		replay(registrarBean, openmrsBean);

		try {
			regWs.recordPregnancyTermination(staffId, facilityId, date,
					motechId, terminationType, procedure, complications,
					maternalDeath, referred, postCounsel, postAccept, comments);
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

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void testRecordPregnancyDelivery() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		Integer child1Id = 246, child2Id = 468, child3Id = 579, deliveredBy = 1;
		Integer[] complications = new Integer[] { 1, 3, 5, 7 };
		String child1Name = "Child1First", child2Name = "Child2First", child3Name = "Child3First", comments = "Comments";
		Integer method = 1, outcome = 2, location = 1, vvf = 2;
		Double child1Weight = 4.2, child2Weight = 4.4, child3Weight = 3.7;
		Boolean maternalDeath = false, maleInvolved = true;
		Boolean child1opv = true, child1bcg = false, child2opv = false, child2bcg = true, child3opv = false, child3bcg = true;
		Date date = new Date();
		BirthOutcome child1birthOutcome = BirthOutcome.A;
		BirthOutcome child2birthOutcome = BirthOutcome.FSB;
		BirthOutcome child3birthOutcome = BirthOutcome.MSB;
		Gender child1Sex = Gender.FEMALE;
		Gender child2Sex = Gender.MALE;
		Gender child3Sex = Gender.MALE;
		RegistrationMode child1RegType = RegistrationMode.USE_PREPRINTED_ID;
		RegistrationMode child2RegType = RegistrationMode.USE_PREPRINTED_ID;
		RegistrationMode child3RegType = RegistrationMode.AUTO_GENERATE_ID;

		User nurse = new User(1);
		org.openmrs.Patient patient = new org.openmrs.Patient(2);

		Capture<List<BirthOutcomeChild>> outcomesCapture = new Capture<List<BirthOutcomeChild>>();

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				nurse);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);
		registrarBean
				.recordPregnancyDelivery(eq(nurse), eq(date), eq(patient),
						eq(method), eq(outcome), eq(location), eq(deliveredBy),
						eq(maternalDeath), eq((Integer) null),
						capture(outcomesCapture));

		replay(registrarBean, openmrsBean);

		regWs.recordPregnancyDelivery(staffId, facilityId, date, motechId,
				method, outcome, location, deliveredBy, maleInvolved,
				complications, vvf, maternalDeath, comments,
				child1birthOutcome, child1RegType, child1Id, child1Sex,
				child1Name, child1Weight, child1opv, child1bcg,
				child2birthOutcome, child2RegType, child2Id, child2Sex,
				child2Name, child2Weight, child2opv, child2bcg,
				child3birthOutcome, child3RegType, child3Id, child3Sex,
				child3Name, child3Weight, child3opv, child3bcg);

		verify(registrarBean, openmrsBean);

		List<BirthOutcomeChild> outcomes = outcomesCapture.getValue();
		assertEquals(3, outcomes.size());

		BirthOutcomeChild child1 = outcomes.get(0);
		assertEquals(child1birthOutcome, child1.getOutcome());
		assertEquals(child1Id, child1.getMotechId());
		assertEquals(child1Name, child1.getFirstName());
		assertEquals(child1Sex, child1.getSex());
		assertEquals(child1bcg, child1.getBcg());
		assertEquals(child1opv, child1.getOpv());

		BirthOutcomeChild child2 = outcomes.get(1);
		assertEquals(child2birthOutcome, child2.getOutcome());
		assertEquals(child2Id, child2.getMotechId());
		assertEquals(child2Name, child2.getFirstName());
		assertEquals(child2Sex, child2.getSex());
		assertEquals(child2bcg, child2.getBcg());
		assertEquals(child2opv, child2.getOpv());

		BirthOutcomeChild child3 = outcomes.get(2);
		assertEquals(child3birthOutcome, child3.getOutcome());
		assertEquals(child3Id, child3.getMotechId());
		assertEquals(child3Name, child3.getFirstName());
		assertEquals(child3Sex, child3.getSex());
		assertEquals(child3bcg, child3.getBcg());
		assertEquals(child3opv, child3.getOpv());

	}

	@Test
	public void testRecordPregnancyDeliveryOneChild()
			throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		Integer child1Id = 246, deliveredBy = 1;
		Integer[] complications = new Integer[] { 1, 3, 5, 7 };
		String child1Name = "Child1First", comments = "Comments";
		Integer method = 1, outcome = 2, location = 1, vvf = 2;
		Double child1Weight = 4.2;
		Boolean maternalDeath = false, maleInvolved = true;
		Boolean child1opv = true, child1bcg = false;
		Date date = new Date();
		BirthOutcome child1birthOutcome = BirthOutcome.A;
		Gender child1Sex = Gender.FEMALE;
		RegistrationMode child1RegType = RegistrationMode.USE_PREPRINTED_ID;

		User nurse = new User(1);
		org.openmrs.Patient patient = new org.openmrs.Patient(2);

		Capture<List<BirthOutcomeChild>> outcomesCapture = new Capture<List<BirthOutcomeChild>>();

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				nurse);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);
		registrarBean
				.recordPregnancyDelivery(eq(nurse), eq(date), eq(patient),
						eq(method), eq(outcome), eq(location), eq(deliveredBy),
						eq(maternalDeath), eq((Integer) null),
						capture(outcomesCapture));

		replay(registrarBean, openmrsBean);

		regWs.recordPregnancyDelivery(staffId, facilityId, date, motechId,
				method, outcome, location, deliveredBy, maleInvolved,
				complications, vvf, maternalDeath, comments,
				child1birthOutcome, child1RegType, child1Id, child1Sex,
				child1Name, child1Weight, child1opv, child1bcg, null, null,
				null, null, null, null, null, null, null, null, null, null,
				null, null, null, null);

		verify(registrarBean, openmrsBean);

		List<BirthOutcomeChild> outcomes = outcomesCapture.getValue();
		assertEquals(1, outcomes.size());

		BirthOutcomeChild child1 = outcomes.get(0);
		assertEquals(child1birthOutcome, child1.getOutcome());
		assertEquals(child1Id, child1.getMotechId());
		assertEquals(child1Name, child1.getFirstName());
		assertEquals(child1Sex, child1.getSex());
		assertEquals(child1bcg, child1.getBcg());
		assertEquals(child1opv, child1.getOpv());
	}

	@Test
	public void testRecordPregnancyDeliveryInvalidPatientId()
			throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		Integer child1Id = 246, child2Id = 468, child3Id = 579, deliveredBy = 1;
		Integer[] complications = new Integer[] { 1, 3, 5, 7 };
		String child1Name = "Child1First", child2Name = "Child2First", child3Name = "Child3First", comments = "Comments";
		Integer method = 1, outcome = 2, location = 1, vvf = 2;
		Double child1Weight = 4.2, child2Weight = 4.4, child3Weight = 3.7;
		Boolean maternalDeath = false, maleInvolved = true;
		Boolean child1opv = true, child1bcg = false, child2opv = false, child2bcg = true, child3opv = false, child3bcg = true;
		Date date = new Date();
		BirthOutcome child1birthOutcome = BirthOutcome.A;
		BirthOutcome child2birthOutcome = BirthOutcome.FSB;
		BirthOutcome child3birthOutcome = BirthOutcome.MSB;
		Gender child1Sex = Gender.FEMALE;
		Gender child2Sex = Gender.MALE;
		Gender child3Sex = Gender.MALE;
		RegistrationMode child1RegType = RegistrationMode.USE_PREPRINTED_ID;
		RegistrationMode child2RegType = RegistrationMode.USE_PREPRINTED_ID;
		RegistrationMode child3RegType = RegistrationMode.AUTO_GENERATE_ID;

		User nurse = new User(1);

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				nurse);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(null);

		replay(registrarBean, openmrsBean);

		try {
			regWs.recordPregnancyDelivery(staffId, facilityId, date, motechId,
					method, outcome, location, deliveredBy, maleInvolved,
					complications, vvf, maternalDeath, comments,
					child1birthOutcome, child1RegType, child1Id, child1Sex,
					child1Name, child1Weight, child1opv, child1bcg,
					child2birthOutcome, child2RegType, child2Id, child2Sex,
					child2Name, child2Weight, child2opv, child2bcg,
					child3birthOutcome, child3RegType, child3Id, child3Sex,
					child3Name, child3Weight, child3opv, child3bcg);
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

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void testRecordDeliveryNotification() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		Date date = new Date();

		User nurse = new User(1);
		org.openmrs.Patient patient = new org.openmrs.Patient(2);

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				nurse);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);

		replay(registrarBean, openmrsBean);

		regWs.recordDeliveryNotification(staffId, facilityId, date, motechId);

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void testRecordRecordDeliveryNotificationInvalidPatientId()
			throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		Date date = new Date();

		User nurse = new User(1);

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				nurse);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(null);

		replay(registrarBean, openmrsBean);

		try {
			regWs.recordDeliveryNotification(staffId, facilityId, date,
					motechId);
			fail("Expected ValidationException");
		} catch (ValidationException e) {
			assertEquals("Errors in Record Delivery Notification request", e
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

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void testRecordMotherPNCVisit() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		Integer visitNumber = 1, location = 1, temperature = 25, ttDose = 1;
		Integer lochiaColour = 1, fht = 140;
		String house = "House", community = "Community", comments = "Comments";
		Boolean referred = false, maleInvolved = true;
		Boolean vitaminA = true, lochiaExcess = false;
		Date date = new Date();

		User nurse = new User(1);
		org.openmrs.Patient patient = new org.openmrs.Patient(2);

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				nurse);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);
		registrarBean.recordMotherPPCVisit(nurse, date, patient, visitNumber,
				vitaminA, ttDose);

		replay(registrarBean, openmrsBean);

		regWs.recordMotherPNCVisit(staffId, facilityId, date, motechId,
				visitNumber, location, house, community, referred,
				maleInvolved, vitaminA, ttDose, lochiaColour, lochiaExcess,
				temperature, fht, comments);

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void testRecordMotherPNCVisitInvalidPatientId()
			throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		Integer visitNumber = 1, location = 1, temperature = 25, ttDose = 1;
		Integer lochiaColour = 1, fht = 140;
		String house = "House", community = "Community", comments = "Comments";
		Boolean referred = false, maleInvolved = true;
		Boolean vitaminA = true, lochiaExcess = false;
		Date date = new Date();

		User nurse = new User(1);

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				nurse);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(null);

		replay(registrarBean, openmrsBean);

		try {
			regWs.recordMotherPNCVisit(staffId, facilityId, date, motechId,
					visitNumber, location, house, community, referred,
					maleInvolved, vitaminA, ttDose, lochiaColour, lochiaExcess,
					temperature, fht, comments);
			fail("Expected ValidationException");
		} catch (ValidationException e) {
			assertEquals("Errors in Record Mother PNC Visit request", e
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

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void testRecordDeath() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		Integer cause = 1;
		Date date = new Date();

		User nurse = new User(1);
		org.openmrs.Patient patient = new org.openmrs.Patient(2);

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				nurse);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);
		registrarBean.recordDeath(nurse, date, patient, cause);

		replay(registrarBean, openmrsBean);

		regWs.recordDeath(staffId, facilityId, date, motechId, cause);

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void testRecordDeathInvalidPatientId() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		Integer cause = 1;
		Date date = new Date();

		User nurse = new User(1);

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				nurse);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(null);

		replay(registrarBean, openmrsBean);

		try {
			regWs.recordDeath(staffId, facilityId, date, motechId, cause);
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

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void testRecordTTVisit() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		Integer ttDose = 1;
		Date date = new Date();

		User nurse = new User(1);
		org.openmrs.Patient patient = new org.openmrs.Patient(2);

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				nurse);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);

		replay(registrarBean, openmrsBean);

		regWs.recordTTVisit(staffId, facilityId, date, motechId, ttDose);

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void testRecordTTVisitInvalidPatientId() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		Integer ttDose = 1;
		Date date = new Date();

		User nurse = new User(1);

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				nurse);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(null);

		replay(registrarBean, openmrsBean);

		try {
			regWs.recordTTVisit(staffId, facilityId, date, motechId, ttDose);
			fail("Expected ValidationException");
		} catch (ValidationException e) {
			assertEquals("Errors in Record TT Visit request", e.getMessage());
			assertNotNull("Validation Exception FaultBean is Null", e
					.getFaultInfo());
			List<ValidationError> errors = e.getFaultInfo().getErrors();
			assertNotNull("Validation Errors is Null", errors);
			assertEquals(1, errors.size());
			ValidationError error = errors.get(0);
			assertEquals(1, error.getCode());
			assertEquals("MotechID", error.getField());
		}

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void testRecordChildPNCVisit() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		Integer visitNumber = 1, location = 1, temperature = 25, respiration = 60;
		String house = "House", community = "Community", comments = "Comments";
		Boolean referred = false, maleInvolved = true;
		Boolean bcg = true, opv0 = true, cordCondition = true, babyCondition = true;
		Double weight = 26.1;
		Date date = new Date();

		User nurse = new User(1);
		org.openmrs.Patient patient = new org.openmrs.Patient(2);

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				nurse);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);

		replay(registrarBean, openmrsBean);

		regWs.recordChildPNCVisit(staffId, facilityId, date, motechId,
				visitNumber, location, house, community, referred,
				maleInvolved, weight, temperature, bcg, opv0, respiration,
				cordCondition, babyCondition, comments);

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void testRecordChildPNCVisitInvalidPatientId()
			throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		Integer visitNumber = 1, location = 1, temperature = 25, respiration = 60;
		String house = "House", community = "Community", comments = "Comments";
		Boolean referred = false, maleInvolved = true;
		Boolean bcg = true, opv0 = true, cordCondition = true, babyCondition = true;
		Double weight = 26.1;
		Date date = new Date();

		User nurse = new User(1);

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				nurse);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(null);

		replay(registrarBean, openmrsBean);

		try {
			regWs.recordChildPNCVisit(staffId, facilityId, date, motechId,
					visitNumber, location, house, community, referred,
					maleInvolved, weight, temperature, bcg, opv0, respiration,
					cordCondition, babyCondition, comments);
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

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void testRecordChildCWCVisit() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		String house = "House", community = "Community", comments = "Comments";
		Integer location = 1, opvDose = 1, pentaDose = 1;
		Boolean bcg = true, yellowFever = true, csm = true, measles = true, ipti = true, vitaminA = true;
		Boolean dewormer = false, maleInvolved = true;
		Integer muac = 5, height = 37;
		Double weight = 25.2;
		Date date = new Date();

		User nurse = new User(1);
		org.openmrs.Patient patient = new org.openmrs.Patient(2);

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				nurse);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);
		registrarBean.recordChildPNCVisit(nurse, date, patient, bcg, opvDose,
				pentaDose, yellowFever, csm, measles, ipti, vitaminA);

		replay(registrarBean, openmrsBean);

		regWs.recordChildCWCVisit(staffId, facilityId, date, motechId,
				location, house, community, bcg, opvDose, pentaDose, measles,
				yellowFever, csm, ipti, vitaminA, dewormer, weight, muac,
				height, maleInvolved, comments);

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void testRecordChildCWCVisitInvalidPatientId()
			throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		String house = "House", community = "Community", comments = "Comments";
		Integer location = 1, opvDose = 1, pentaDose = 1;
		Boolean bcg = true, yellowFever = true, csm = true, measles = true, ipti = true, vitaminA = true;
		Boolean dewormer = false, maleInvolved = true;
		Integer muac = 5, height = 37;
		Double weight = 25.2;
		Date date = new Date();

		User nurse = new User(1);

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				nurse);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(null);

		replay(registrarBean, openmrsBean);

		try {
			regWs.recordChildCWCVisit(staffId, facilityId, date, motechId,
					location, house, community, bcg, opvDose, pentaDose,
					measles, yellowFever, csm, ipti, vitaminA, dewormer,
					weight, muac, height, maleInvolved, comments);
			fail("Expected ValidationException");
		} catch (ValidationException e) {
			assertEquals("Errors in Record Child CWC Visit request", e
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

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void testRegisterPatient() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3, motherMotechId = 4;
		String firstName = "First", middleName = "Middle", lastName = "Last", prefName = "Pref";
		String nhis = "NHIS", address = "Address", language = "Language";
		String region = "Region", district = "District", subdistrict = "SubDistrict", community = "Community";
		Gender gender = Gender.FEMALE;
		Boolean estBirthDate = false, insured = true, delivDateConf = true, enroll = true, consent = true;
		Integer gravida = 0, parity = 0, messageWeek = 5, phone = 15555555;
		Date date = new Date();
		RegistrationMode mode = RegistrationMode.USE_PREPRINTED_ID;
		RegistrantType type = RegistrantType.CHILD_UNDER_FIVE;
		ContactNumberType phoneType = ContactNumberType.PERSONAL;
		MediaType format = MediaType.VOICE;
		DayOfWeek day = DayOfWeek.MONDAY;
		InterestReason reason = InterestReason.RECENTLY_DELIVERED;
		HowLearned how = HowLearned.GHS_NURSE;

		User nurse = new User(1);
		org.openmrs.Patient patient = null;
		org.openmrs.Patient mother = new org.openmrs.Patient(3);

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				nurse);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);
		expect(openmrsBean.getPatientByMotechId(motherMotechId.toString()))
				.andReturn(mother);

		replay(registrarBean, openmrsBean);

		regWs.registerPatient(staffId, facilityId, date, mode, motechId, type,
				firstName, middleName, lastName, prefName, date, estBirthDate,
				gender, insured, nhis, date, motherMotechId, region, district,
				subdistrict, community, address, phone, date, delivDateConf,
				gravida, parity, enroll, consent, phoneType, format, language,
				day, date, reason, how, messageWeek);

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void testRegisterPatientAllErrors() {
		Integer staffId = 1, facilityId = 2, motechId = 3, motherMotechId = 4;
		String firstName = "First", middleName = "Middle", lastName = "Last", prefName = "Pref";
		String nhis = "NHIS", address = "Address", language = "Language";
		String region = "Region", district = "District", subdistrict = "SubDistrict", community = "Community";
		Gender gender = Gender.FEMALE;
		Boolean estBirthDate = false, insured = true, delivDateConf = true, enroll = true, consent = true;
		Integer gravida = 0, parity = 0, messageWeek = 5, phone = 15555555;
		Date date = new Date();
		RegistrationMode mode = RegistrationMode.USE_PREPRINTED_ID;
		RegistrantType type = RegistrantType.CHILD_UNDER_FIVE;
		ContactNumberType phoneType = ContactNumberType.PERSONAL;
		MediaType format = MediaType.VOICE;
		DayOfWeek day = DayOfWeek.MONDAY;
		InterestReason reason = InterestReason.RECENTLY_DELIVERED;
		HowLearned how = HowLearned.GHS_NURSE;

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, -6);
		Date childBirthDate = calendar.getTime();

		User nurse = null;
		org.openmrs.Patient patient = new org.openmrs.Patient(2);
		org.openmrs.Patient mother = null;

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				nurse);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);
		expect(openmrsBean.getPatientByMotechId(motherMotechId.toString()))
				.andReturn(mother);

		replay(registrarBean, openmrsBean);

		try {
			regWs.registerPatient(staffId, facilityId, date, mode, motechId,
					type, firstName, middleName, lastName, prefName,
					childBirthDate, estBirthDate, gender, insured, nhis, date,
					motherMotechId, region, district, subdistrict, community,
					address, phone, date, delivDateConf, gravida, parity,
					enroll, consent, phoneType, format, language, day, date,
					reason, how, messageWeek);
			fail("Expected ValidationException");
		} catch (ValidationException e) {
			assertEquals("Errors in Register Patient request", e.getMessage());
			assertNotNull("Validation Exception FaultBean is Null", e
					.getFaultInfo());
			List<ValidationError> errors = e.getFaultInfo().getErrors();
			assertNotNull("Validation Errors is Null", errors);
			assertEquals(4, errors.size());
			ValidationError nurseError = errors.get(0);
			assertEquals(1, nurseError.getCode());
			assertEquals("StaffID", nurseError.getField());
			ValidationError patientError = errors.get(1);
			assertEquals(2, patientError.getCode());
			assertEquals("MotechID", patientError.getField());
			ValidationError motherError = errors.get(2);
			assertEquals(1, motherError.getCode());
			assertEquals("MotherMotechID", motherError.getField());
			ValidationError dobError = errors.get(3);
			assertEquals(2, dobError.getCode());
			assertEquals("DoB", dobError.getField());
		}

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void testRegisterPregnancy() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		String language = "Language";
		Boolean enroll = true, consent = true;
		Integer messageWeek = 5, phone = 15555555;
		Date date = new Date();
		ContactNumberType phoneType = ContactNumberType.PERSONAL;
		MediaType format = MediaType.VOICE;
		DayOfWeek day = DayOfWeek.MONDAY;
		InterestReason reason = InterestReason.RECENTLY_DELIVERED;
		HowLearned how = HowLearned.GHS_NURSE;

		User nurse = new User(1);
		org.openmrs.Patient patient = new org.openmrs.Patient(2);

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				nurse);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);

		replay(registrarBean, openmrsBean);

		regWs.registerPregnancy(staffId, facilityId, date, motechId, date,
				enroll, consent, phoneType, phone, format, language, day, date,
				reason, how, messageWeek);

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void testRegisterPregnancyInvalidIds() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		String language = "Language";
		Boolean enroll = true, consent = true;
		Integer messageWeek = 5, phone = 15555555;
		Date date = new Date();
		ContactNumberType phoneType = ContactNumberType.PERSONAL;
		MediaType format = MediaType.VOICE;
		DayOfWeek day = DayOfWeek.MONDAY;
		InterestReason reason = InterestReason.RECENTLY_DELIVERED;
		HowLearned how = HowLearned.GHS_NURSE;

		User nurse = null;
		org.openmrs.Patient patient = null;

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				nurse);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);

		replay(registrarBean, openmrsBean);

		try {
			regWs.registerPregnancy(staffId, facilityId, date, motechId, date,
					enroll, consent, phoneType, phone, format, language, day,
					date, reason, how, messageWeek);
			fail("Expected ValidationException");
		} catch (ValidationException e) {
			assertEquals("Errors in Register Pregnancy request", e.getMessage());
			assertNotNull("Validation Exception FaultBean is Null", e
					.getFaultInfo());
			List<ValidationError> errors = e.getFaultInfo().getErrors();
			assertNotNull("Validation Errors is Null", errors);
			assertEquals(2, errors.size());
			ValidationError nurseError = errors.get(0);
			assertEquals(1, nurseError.getCode());
			assertEquals("StaffID", nurseError.getField());
			ValidationError patientError = errors.get(1);
			assertEquals(1, patientError.getCode());
			assertEquals("MotechID", patientError.getField());
		}

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void testRegisterANCMother() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		String language = "Language", regNumber = "RegNumber";
		Boolean enroll = true, consent = true;
		Integer gravida = 0, parity = 0, messageWeek = 5, phone = 15555555, height = 45;
		Date date = new Date();
		ContactNumberType phoneType = ContactNumberType.PERSONAL;
		MediaType format = MediaType.VOICE;
		DayOfWeek day = DayOfWeek.MONDAY;
		InterestReason reason = InterestReason.RECENTLY_DELIVERED;
		HowLearned how = HowLearned.GHS_NURSE;

		User nurse = new User(1);
		org.openmrs.Patient patient = new org.openmrs.Patient(2);

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				nurse);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);

		replay(registrarBean, openmrsBean);

		regWs.registerANCMother(staffId, facilityId, date, motechId, regNumber,
				date, height, gravida, parity, enroll, consent, phoneType,
				phone, format, language, day, date, reason, how, messageWeek);

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void testRegisterANCMotherInvalidIds() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		String language = "Language", regNumber = "RegNumber";
		Boolean enroll = true, consent = true;
		Integer gravida = 0, parity = 0, messageWeek = 5, phone = 15555555, height = 45;
		Date date = new Date();
		ContactNumberType phoneType = ContactNumberType.PERSONAL;
		MediaType format = MediaType.VOICE;
		DayOfWeek day = DayOfWeek.MONDAY;
		InterestReason reason = InterestReason.RECENTLY_DELIVERED;
		HowLearned how = HowLearned.GHS_NURSE;

		User nurse = null;
		org.openmrs.Patient patient = null;

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				nurse);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);

		replay(registrarBean, openmrsBean);

		try {

			regWs.registerANCMother(staffId, facilityId, date, motechId,
					regNumber, date, height, gravida, parity, enroll, consent,
					phoneType, phone, format, language, day, date, reason, how,
					messageWeek);
			fail("Expected ValidationException");
		} catch (ValidationException e) {
			assertEquals("Errors in Register ANC Mother request", e
					.getMessage());
			assertNotNull("Validation Exception FaultBean is Null", e
					.getFaultInfo());
			List<ValidationError> errors = e.getFaultInfo().getErrors();
			assertNotNull("Validation Errors is Null", errors);
			assertEquals(2, errors.size());
			ValidationError nurseError = errors.get(0);
			assertEquals(1, nurseError.getCode());
			assertEquals("StaffID", nurseError.getField());
			ValidationError patientError = errors.get(1);
			assertEquals(1, patientError.getCode());
			assertEquals("MotechID", patientError.getField());
		}

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void testRegisterCWCChild() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		String language = "Language", regNumber = "RegNumber";
		Boolean enroll = true, consent = true;
		Integer messageWeek = 5, phone = 15555555;
		Date date = new Date();
		ContactNumberType phoneType = ContactNumberType.PERSONAL;
		MediaType format = MediaType.VOICE;
		DayOfWeek day = DayOfWeek.MONDAY;
		InterestReason reason = InterestReason.RECENTLY_DELIVERED;
		HowLearned how = HowLearned.GHS_NURSE;

		User nurse = new User(1);
		org.openmrs.Patient patient = new org.openmrs.Patient(2);

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				nurse);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);

		replay(registrarBean, openmrsBean);

		regWs.registerCWCChild(staffId, facilityId, date, motechId, regNumber,
				enroll, consent, phoneType, phone, format, language, day, date,
				reason, how, messageWeek);

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void testRegisterCWCChildInvalidIds() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		String language = "Language", regNumber = "RegNumber";
		Boolean enroll = true, consent = true;
		Integer messageWeek = 5, phone = 15555555;
		Date date = new Date();
		ContactNumberType phoneType = ContactNumberType.PERSONAL;
		MediaType format = MediaType.VOICE;
		DayOfWeek day = DayOfWeek.MONDAY;
		InterestReason reason = InterestReason.RECENTLY_DELIVERED;
		HowLearned how = HowLearned.GHS_NURSE;

		User nurse = null;
		org.openmrs.Patient patient = null;

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				nurse);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);

		replay(registrarBean, openmrsBean);

		try {
			regWs.registerCWCChild(staffId, facilityId, date, motechId,
					regNumber, enroll, consent, phoneType, phone, format,
					language, day, date, reason, how, messageWeek);
			fail("Expected ValidationException");
		} catch (ValidationException e) {
			assertEquals("Errors in Register CWC Child request", e.getMessage());
			assertNotNull("Validation Exception FaultBean is Null", e
					.getFaultInfo());
			List<ValidationError> errors = e.getFaultInfo().getErrors();
			assertNotNull("Validation Errors is Null", errors);
			assertEquals(2, errors.size());
			ValidationError nurseError = errors.get(0);
			assertEquals(1, nurseError.getCode());
			assertEquals("StaffID", nurseError.getField());
			ValidationError patientError = errors.get(1);
			assertEquals(1, patientError.getCode());
			assertEquals("MotechID", patientError.getField());
		}

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void testEditPatient() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		String phoneNumber = "12075557894", nhis = "125";
		ContactNumberType phoneType = ContactNumberType.PERSONAL;
		Boolean stopEnrollment = false;
		Date date = new Date();

		User nurse = new User(1);
		org.openmrs.Patient patient = new org.openmrs.Patient(2);

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				nurse);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);

		registrarBean.editPatient(nurse, patient, phoneNumber, phoneType, null,
				null, nhis, date);
		replay(registrarBean, openmrsBean);

		regWs.editPatient(staffId, facilityId, date, motechId, phoneNumber,
				phoneType, nhis, date, stopEnrollment);

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void testEditPatientAllErrors() {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		String phoneNumber = "12075557894", nhis = "125";
		ContactNumberType phoneType = ContactNumberType.PERSONAL;
		Boolean stopEnrollment = false;
		Date date = new Date();

		User nurse = null;
		org.openmrs.Patient patient = null;

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				nurse);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);

		replay(registrarBean, openmrsBean);

		try {
			regWs.editPatient(staffId, facilityId, date, motechId, phoneNumber,
					phoneType, nhis, date, stopEnrollment);
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
			assertEquals("StaffID", nurseError.getField());
			ValidationError patientError = errors.get(1);
			assertEquals(1, patientError.getCode());
			assertEquals("MotechID", patientError.getField());
		}

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void testGeneralVisit() throws ValidationException {
		Integer staffId = 1, facilityId = 2;
		String serial = "Serial", comments = "Comments";
		Integer diagnosis = 5, secondDiagnosis = 6;
		Boolean insured = true, newCase = true, referred = false, rdtGiven = true, rdtPositive = false, actTreated = false;
		Date date = new Date();
		Gender gender = Gender.MALE;

		User nurse = new User(1);

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				nurse);

		registrarBean.recordGeneralVisit(staffId.toString(), date, serial,
				gender, date, insured, newCase, diagnosis, secondDiagnosis,
				referred);

		replay(registrarBean, openmrsBean);

		regWs.recordGeneralVisit(staffId, facilityId, date, serial, gender,
				date, insured, diagnosis, secondDiagnosis, rdtGiven,
				rdtPositive, actTreated, newCase, referred, comments);

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void testGeneralVisitInvalidNurseId() {
		Integer staffId = 1, facilityId = 2;
		String serial = "Serial", comments = "Comments";
		Integer diagnosis = 5, secondDiagnosis = 6;
		Boolean insured = true, newCase = true, referred = false, rdtGiven = true, rdtPositive = false, actTreated = false;
		Date date = new Date();
		Gender gender = Gender.MALE;

		try {
			regWs.recordGeneralVisit(staffId, facilityId, date, serial, gender,
					date, insured, diagnosis, secondDiagnosis, rdtGiven,
					rdtPositive, actTreated, newCase, referred, comments);
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
			assertEquals("StaffID", error.getField());
		}
	}

	@Test
	public void testRecordChildVisit() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		String serial = "Serial", comments = "Comments";
		Integer diagnosis = 5, secondDiagnosis = 6;
		Boolean newCase = true, referred = false, rdtGiven = true, rdtPositive = false, actTreated = false;
		Date date = new Date();

		User nurse = new User(1);
		org.openmrs.Patient patient = new org.openmrs.Patient(2);

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				nurse);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);
		registrarBean.recordChildVisit(nurse, date, patient, serial, newCase,
				diagnosis, secondDiagnosis, referred);

		replay(registrarBean, openmrsBean);

		regWs.recordChildVisit(staffId, facilityId, date, serial, motechId,
				diagnosis, secondDiagnosis, rdtGiven, rdtPositive, actTreated,
				newCase, referred, comments);

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void testRecordChildVisitInvalidPatientId()
			throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		String serial = "Serial", comments = "Comments";
		Integer diagnosis = 5, secondDiagnosis = 6;
		Boolean newCase = true, referred = false, rdtGiven = true, rdtPositive = false, actTreated = false;
		Date date = new Date();

		User nurse = new User(1);

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				nurse);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(null);

		replay(registrarBean, openmrsBean);

		try {
			regWs.recordChildVisit(staffId, facilityId, date, serial, motechId,
					diagnosis, secondDiagnosis, rdtGiven, rdtPositive,
					actTreated, newCase, referred, comments);
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

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void testRecordMotherVisit() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		String serial = "Serial", comments = "Comments";
		Integer diagnosis = 5, secondDiagnosis = 6;
		Boolean newCase = true, referred = false, rdtGiven = true, rdtPositive = false, actTreated = false;
		Date date = new Date();

		User nurse = new User(1);
		org.openmrs.Patient patient = new org.openmrs.Patient(2);

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				nurse);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);
		registrarBean.recordMotherVisit(nurse, date, patient, serial, newCase,
				diagnosis, secondDiagnosis, referred);

		replay(registrarBean, openmrsBean);

		regWs.recordMotherVisit(staffId, facilityId, date, serial, motechId,
				diagnosis, secondDiagnosis, rdtGiven, rdtPositive, actTreated,
				newCase, referred, comments);

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void testRecordMotherVisitInvalidPatientId()
			throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		String serial = "Serial", comments = "Comments";
		Integer diagnosis = 5, secondDiagnosis = 6;
		Boolean newCase = true, referred = false, rdtGiven = true, rdtPositive = false, actTreated = false;
		Date date = new Date();

		User nurse = new User(1);

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				nurse);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(null);

		replay(registrarBean, openmrsBean);

		try {
			regWs.recordMotherVisit(staffId, facilityId, date, serial,
					motechId, diagnosis, secondDiagnosis, rdtGiven,
					rdtPositive, actTreated, newCase, referred, comments);
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

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void testQueryANCDefaulters() throws ValidationException {
		Integer staffId = 1, facilityId = 2;

		Capture<String[]> encounterGroups = new Capture<String[]>();
		Capture<String[]> obsGroups = new Capture<String[]>();

		List<ExpectedEncounter> expectedEncounters = new ArrayList<ExpectedEncounter>();
		List<ExpectedObs> expectedObs = new ArrayList<ExpectedObs>();

		Care encounterCare = new Care();
		encounterCare.setName("EncounterCare");
		Care obsCare = new Care();
		obsCare.setName("ObsCare");
		Care[] defaultedCares = { encounterCare, obsCare };

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				new User(1));
		expect(
				registrarBean
						.getDefaultedExpectedEncounters(capture(encounterGroups)))
				.andReturn(expectedEncounters);
		expect(registrarBean.getDefaultedExpectedObs(capture(obsGroups)))
				.andReturn(expectedObs);
		expect(
				modelConverter.defaultedToWebServiceCares(expectedEncounters,
						expectedObs)).andReturn(defaultedCares);

		replay(registrarBean, modelConverter, openmrsBean);

		Care[] cares = regWs.queryANCDefaulters(staffId, facilityId);

		verify(registrarBean, modelConverter, openmrsBean);

		assertEquals(1, encounterGroups.getValue().length);
		assertEquals("ANC", encounterGroups.getValue()[0]);

		assertEquals(2, obsGroups.getValue().length);
		assertEquals("TT", obsGroups.getValue()[0]);
		assertEquals("IPT", obsGroups.getValue()[1]);

		assertNotNull("Care result array is null", cares);
		assertEquals(2, cares.length);
		assertEquals(encounterCare.getName(), cares[0].getName());
		assertEquals(obsCare.getName(), cares[1].getName());
	}

	@Test
	public void testQueryTTDefaulters() throws ValidationException {
		Integer staffId = 1, facilityId = 2;

		Capture<String[]> obsGroups = new Capture<String[]>();

		List<ExpectedObs> expectedObs = new ArrayList<ExpectedObs>();

		Care obsCare = new Care();
		obsCare.setName("ObsCare");
		Care[] obsCares = { obsCare };

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				new User(1));
		expect(registrarBean.getDefaultedExpectedObs(capture(obsGroups)))
				.andReturn(expectedObs);
		expect(modelConverter.defaultedObsToWebServiceCares(expectedObs))
				.andReturn(obsCares);

		replay(registrarBean, modelConverter, openmrsBean);

		Care[] cares = regWs.queryTTDefaulters(staffId, facilityId);

		verify(registrarBean, modelConverter, openmrsBean);

		assertEquals(1, obsGroups.getValue().length);
		assertEquals("TT", obsGroups.getValue()[0]);

		assertNotNull("Care result array is null", cares);
		assertEquals(1, cares.length);
		assertEquals(obsCare.getName(), cares[0].getName());
	}

	@Test
	public void testQueryMotherPNCDefaulters() throws ValidationException {
		Integer staffId = 1, facilityId = 2;

		Capture<String[]> encounterGroups = new Capture<String[]>();

		List<ExpectedEncounter> expectedEncounters = new ArrayList<ExpectedEncounter>();

		Care encounterCare = new Care();
		encounterCare.setName("EncounterCare");
		Care[] encounterCares = { encounterCare };

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				new User(1));
		expect(
				registrarBean
						.getDefaultedExpectedEncounters(capture(encounterGroups)))
				.andReturn(expectedEncounters);
		expect(
				modelConverter
						.defaultedEncountersToWebServiceCares(expectedEncounters))
				.andReturn(encounterCares);

		replay(registrarBean, modelConverter, openmrsBean);

		Care[] cares = regWs.queryMotherPNCDefaulters(staffId, facilityId);

		verify(registrarBean, modelConverter, openmrsBean);

		assertEquals(1, encounterGroups.getValue().length);
		assertEquals("PPC", encounterGroups.getValue()[0]);

		assertNotNull("Care result array is null", cares);
		assertEquals(1, cares.length);
		assertEquals(encounterCare.getName(), cares[0].getName());
	}

	@Test
	public void testQueryChildPNCDefaulters() throws ValidationException {
		Integer staffId = 1, facilityId = 2;

		Capture<String[]> encounterGroups = new Capture<String[]>();

		List<ExpectedEncounter> expectedEncounters = new ArrayList<ExpectedEncounter>();

		Care encounterCare = new Care();
		encounterCare.setName("EncounterCare");
		Care[] encounterCares = { encounterCare };

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				new User(1));
		expect(
				registrarBean
						.getDefaultedExpectedEncounters(capture(encounterGroups)))
				.andReturn(expectedEncounters);
		expect(
				modelConverter
						.defaultedEncountersToWebServiceCares(expectedEncounters))
				.andReturn(encounterCares);

		replay(registrarBean, modelConverter, openmrsBean);

		Care[] cares = regWs.queryChildPNCDefaulters(staffId, facilityId);

		verify(registrarBean, modelConverter, openmrsBean);

		assertEquals(1, encounterGroups.getValue().length);
		assertEquals("PNC", encounterGroups.getValue()[0]);

		assertNotNull("Care result array is null", cares);
		assertEquals(1, cares.length);
		assertEquals(encounterCare.getName(), cares[0].getName());
	}

	@Test
	public void testQueryCWCDefaulters() throws ValidationException {
		Integer staffId = 1, facilityId = 2;

		Capture<String[]> obsGroups = new Capture<String[]>();

		List<ExpectedObs> expectedObs = new ArrayList<ExpectedObs>();

		Care obsCare = new Care();
		obsCare.setName("ObsCare");
		Care[] obsCares = { obsCare };

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				new User(1));
		expect(registrarBean.getDefaultedExpectedObs(capture(obsGroups)))
				.andReturn(expectedObs);
		expect(modelConverter.defaultedObsToWebServiceCares(expectedObs))
				.andReturn(obsCares);

		replay(registrarBean, modelConverter, openmrsBean);

		Care[] cares = regWs.queryCWCDefaulters(staffId, facilityId);

		verify(registrarBean, modelConverter, openmrsBean);

		assertEquals(7, obsGroups.getValue().length);
		assertEquals("OPV", obsGroups.getValue()[0]);
		assertEquals("BCG", obsGroups.getValue()[1]);
		assertEquals("Penta", obsGroups.getValue()[2]);
		assertEquals("YellowFever", obsGroups.getValue()[3]);
		assertEquals("Measles", obsGroups.getValue()[4]);
		assertEquals("VitaA", obsGroups.getValue()[5]);
		assertEquals("IPTI", obsGroups.getValue()[6]);

		assertNotNull("Care result array is null", cares);
		assertEquals(1, cares.length);
		assertEquals(obsCare.getName(), cares[0].getName());
	}

	@Test
	public void testQueryUpcomingDeliveries() throws ValidationException {
		Integer staffId = 1, facilityId = 2;

		List<Obs> pregnancies = new ArrayList<Obs>();
		pregnancies.add(new Obs());

		Patient patient = new Patient();
		patient.setMotechId("MotechId");
		Patient[] result = { patient };

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				new User(1));
		expect(registrarBean.getUpcomingPregnanciesDueDate()).andReturn(
				pregnancies);
		expect(modelConverter.dueDatesToWebServicePatients(pregnancies))
				.andReturn(result);

		replay(registrarBean, modelConverter, openmrsBean);

		Patient[] patients = regWs.queryUpcomingDeliveries(staffId, facilityId);

		verify(registrarBean, modelConverter, openmrsBean);

		assertNotNull("Patient result array is null", patients);
		assertEquals(1, patients.length);
	}

	@Test
	public void testQueryRecentDeliveries() throws ValidationException {
		Integer staffId = 1, facilityId = 2;

		List<Encounter> deliveries = new ArrayList<Encounter>();
		deliveries.add(new Encounter());

		Patient patient = new Patient();
		patient.setMotechId("MotechId");
		Patient[] result = { patient };

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				new User(1));
		expect(registrarBean.getRecentDeliveries()).andReturn(deliveries);
		expect(modelConverter.deliveriesToWebServicePatients(deliveries))
				.andReturn(result);

		replay(registrarBean, modelConverter, openmrsBean);

		Patient[] patients = regWs.queryRecentDeliveries(staffId, facilityId);

		verify(registrarBean, modelConverter, openmrsBean);

		assertNotNull("Patient result array is null", patients);
		assertEquals(1, patients.length);
	}

	@Test
	public void testQueryOverdueDeliveries() throws ValidationException {
		Integer staffId = 1, facilityId = 2;

		List<Obs> pregnancies = new ArrayList<Obs>();
		pregnancies.add(new Obs());

		Patient patient = new Patient();
		patient.setMotechId("MotechId");
		Patient[] result = { patient };

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				new User(1));
		expect(registrarBean.getOverduePregnanciesDueDate()).andReturn(
				pregnancies);
		expect(modelConverter.dueDatesToWebServicePatients(pregnancies))
				.andReturn(result);

		replay(registrarBean, modelConverter, openmrsBean);

		Patient[] patients = regWs.queryOverdueDeliveries(staffId, facilityId);

		verify(registrarBean, modelConverter, openmrsBean);

		assertNotNull("Patient result array is null", patients);
		assertEquals(1, patients.length);
	}

	@Test
	public void testQueryUpcomingCare() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;

		List<ExpectedEncounter> expectedEncounters = new ArrayList<ExpectedEncounter>();
		List<ExpectedObs> expectedObs = new ArrayList<ExpectedObs>();

		Calendar calendar = Calendar.getInstance();

		Care encounterCare1 = new Care();
		encounterCare1.setName("EncounterCare1");
		calendar.set(2010, Calendar.APRIL, 4);
		encounterCare1.setDate(calendar.getTime());
		Care encounterCare2 = new Care();
		encounterCare2.setName("EncounterCare2");
		calendar.set(2010, Calendar.DECEMBER, 12);
		encounterCare2.setDate(calendar.getTime());
		Care obsCare1 = new Care();
		obsCare1.setName("ObsCare1");
		calendar.set(2010, Calendar.OCTOBER, 10);
		obsCare1.setDate(calendar.getTime());
		Care obsCare2 = new Care();
		obsCare2.setName("ObsCare2");
		calendar.set(2010, Calendar.JANUARY, 1);
		obsCare2.setDate(calendar.getTime());
		Care[] upcomingCares = { obsCare2, encounterCare1, obsCare1,
				encounterCare2 };

		org.openmrs.Patient patient = new org.openmrs.Patient(1);

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				new User(1));
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);
		expect(modelConverter.patientToWebService(eq(patient), eq(true)))
				.andReturn(new Patient());

		expect(registrarBean.getUpcomingExpectedEncounters(patient)).andReturn(
				expectedEncounters);
		expect(registrarBean.getUpcomingExpectedObs(patient)).andReturn(
				expectedObs);
		expect(
				modelConverter.upcomingToWebServiceCares(expectedEncounters,
						expectedObs, true)).andReturn(upcomingCares);

		replay(registrarBean, modelConverter, openmrsBean);

		Patient wsPatient = regWs.queryUpcomingCare(staffId, facilityId,
				motechId);

		verify(registrarBean, modelConverter, openmrsBean);

		assertNotNull("Patient result is null", wsPatient);
		Care[] cares = wsPatient.getCares();
		assertNotNull("Patient cares is null", cares);
		assertEquals(4, cares.length);
		assertEquals(obsCare2.getName(), cares[0].getName());
		assertEquals(obsCare2.getDate(), cares[0].getDate());
		assertEquals(encounterCare1.getName(), cares[1].getName());
		assertEquals(encounterCare1.getDate(), cares[1].getDate());
		assertEquals(obsCare1.getName(), cares[2].getName());
		assertEquals(obsCare1.getDate(), cares[2].getDate());
		assertEquals(encounterCare2.getName(), cares[3].getName());
		assertEquals(encounterCare2.getDate(), cares[3].getDate());
	}

	@Test
	public void testQueryMotechId() throws ValidationException {
		Integer staffId = 1, facilityId = 2;
		String firstName = "FirstName", lastName = "LastName", prefName = "PrefName";
		String nhis = "NHIS", phone = "Phone";
		Date birthDate = new Date();

		List<org.openmrs.Patient> patients = new ArrayList<org.openmrs.Patient>();
		patients.add(new org.openmrs.Patient(1));

		Patient patient = new Patient();
		patient.setMotechId("MotechId");
		Patient[] result = { patient };

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				new User(1));
		expect(
				registrarBean.getPatients(firstName, lastName, prefName,
						birthDate, null, phone, nhis, null))
				.andReturn(patients);
		expect(modelConverter.patientToWebService(patients, true)).andReturn(
				result);

		replay(registrarBean, modelConverter, openmrsBean);

		Patient[] wsPatients = regWs.queryMotechId(staffId, facilityId,
				firstName, lastName, prefName, birthDate, nhis, phone);

		verify(registrarBean, modelConverter, openmrsBean);

		assertNotNull("Patient result array is null", wsPatients);
		assertEquals(1, wsPatients.length);
	}

	@Test
	public void testQueryPatient() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;

		org.openmrs.Patient patient = new org.openmrs.Patient(1);

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				new User(1));
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);
		expect(modelConverter.patientToWebService(eq(patient), eq(false)))
				.andReturn(new Patient());

		replay(registrarBean, modelConverter, openmrsBean);

		regWs.queryPatient(staffId, facilityId, motechId);

		verify(registrarBean, modelConverter, openmrsBean);
	}

	@Test
	public void testQueryPatientInvalidPatientId() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;

		expect(openmrsBean.getNurseByCHPSId(staffId.toString())).andReturn(
				new User(1));
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(null);

		replay(registrarBean, modelConverter, openmrsBean);

		try {
			regWs.queryPatient(staffId, facilityId, motechId);

			fail("Expected ValidationException");
		} catch (ValidationException e) {
			assertEquals("Errors in Patient Query request", e.getMessage());
			assertNotNull("Validation Exception FaultBean is Null", e
					.getFaultInfo());
			List<ValidationError> errors = e.getFaultInfo().getErrors();
			assertNotNull("Validation Errors is Null", errors);
			assertEquals(1, errors.size());
			ValidationError error = errors.get(0);
			assertEquals(1, error.getCode());
			assertEquals("MotechID", error.getField());
		}

		verify(registrarBean, modelConverter, openmrsBean);
	}

	@Test
	public void testGetPatientEnrollments() throws ValidationException {
		Integer motechId = 3;

		org.openmrs.Patient patient = new org.openmrs.Patient(1);
		String[] enrollments = { "Enrollment1", "Enrollment2" };

		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);
		expect(registrarBean.getActiveMessageProgramEnrollmentNames(patient))
				.andReturn(enrollments);

		replay(registrarBean, modelConverter, openmrsBean);

		String[] result = regWs.getPatientEnrollments(motechId);

		verify(registrarBean, modelConverter, openmrsBean);

		assertArrayEquals(enrollments, result);
	}

	@Test
	public void testGetPatientEnrollmentsInvalidPatientId()
			throws ValidationException {
		Integer motechId = 3;

		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(null);

		replay(registrarBean, modelConverter, openmrsBean);

		try {
			regWs.getPatientEnrollments(motechId);

			fail("Expected ValidationException");
		} catch (ValidationException e) {
			assertEquals("Errors in Get Patient Enrollments request", e
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

		verify(registrarBean, modelConverter, openmrsBean);
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
