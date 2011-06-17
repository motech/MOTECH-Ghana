/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010-11 The Trustees of Columbia University in the City of
 * New York and Grameen Foundation USA.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Grameen Foundation USA, Columbia University, or
 * their respective contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA, COLUMBIA UNIVERSITY
 * AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION
 * USA, COLUMBIA UNIVERSITY OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.motechproject.server.ws;

import org.easymock.Capture;
import org.junit.*;
import org.motechproject.server.model.ghana.Community;
import org.motechproject.server.model.ExpectedEncounter;
import org.motechproject.server.model.ExpectedObs;
import org.motechproject.server.model.ghana.Facility;
import org.motechproject.server.svc.BirthOutcomeChild;
import org.motechproject.server.svc.OpenmrsBean;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.server.util.DateUtil;
import org.motechproject.server.util.MotechConstants;
import org.motechproject.ws.*;
import org.motechproject.ws.server.RegistrarService;
import org.motechproject.ws.server.ValidationException;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.LogManager;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class RegistrarServiceTest{

	static ApplicationContext ctx;
	static RegistrarService regWs;
	static RegistrarBean registrarBean;
	static OpenmrsBean openmrsBean;
	static WebServicePatientModelConverter patientModelConverter;
	static WebServiceCareModelConverter careModelConverter;

	@BeforeClass
	public static void setUpClass() throws Exception {
		LogManager.getLogManager().readConfiguration(
				RegistrarServiceTest.class
						.getResourceAsStream("/jul-test.properties"));
		registrarBean = createMock(RegistrarBean.class);
		openmrsBean = createMock(OpenmrsBean.class);
		patientModelConverter = createMock(WebServicePatientModelConverter.class);
        careModelConverter = createMock(WebServiceCareModelConverter.class);
		ctx = new ClassPathXmlApplicationContext("test-context.xml");

        RegistrarWebService regService = (RegistrarWebService) ctx.getBean("registrarService");
		regService.setRegistrarBean(registrarBean);
		regService.setOpenmrsBean(openmrsBean);
		regService.setPatientModelConverter(patientModelConverter);
        regService.setCareModelConverter(careModelConverter);

		regWs = (RegistrarService) ctx.getBean("registrarClient");
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		ctx = null;
		regWs = null;
		registrarBean = null;
		openmrsBean = null;
		patientModelConverter = null;
        careModelConverter = null;
		LogManager.getLogManager().readConfiguration();
	}

	@Before
	public void setup() {
	}

	@After
	public void tearDown() throws Exception {
		reset(registrarBean, patientModelConverter, openmrsBean, careModelConverter);
	}

	@Test
	public void recordPatientHistory() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		Integer lastIPT = 1, lastTT = 1;
		Integer lastOPV = 1, lastPenta = 1, lastIPTI = 1;
		Date date = new Date();

		User staff = new User(1);
		Location facilityLocation = new Location(1);
		Facility facility = new Facility();
		facility.setLocation(facilityLocation);
		org.openmrs.Patient patient = new org.openmrs.Patient(2);

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				staff);
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(facility);
		expect(registrarBean.isValidMotechIdCheckDigit(motechId)).andReturn(
				true);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);
		registrarBean.recordPatientHistory(staff, facilityLocation, date,
				patient, lastIPT, date, lastTT, date, date, lastOPV, date,
				lastPenta, date, date, date, lastIPTI, date, date, null);

		replay(registrarBean, openmrsBean);

		regWs.recordPatientHistory(staffId, facilityId, date, motechId,
				lastIPT, date, lastTT, date, date, lastOPV, date, lastPenta,
				date, date, date, lastIPTI, date, date);

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void recordPatientHistoryInvalidPatientId()
			throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		Integer lastIPT = 1, lastTT = 1;
		Integer lastOPV = 1, lastPenta = 1, lastIPTI = 1;
		Date date = new Date();

		User staff = new User(1);

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				staff);
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(
				new Facility());
		expect(registrarBean.isValidMotechIdCheckDigit(motechId)).andReturn(
				true);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(null);

		replay(registrarBean, openmrsBean);

		try {
			regWs.recordPatientHistory(staffId, facilityId, date, motechId,
					lastIPT, date, lastTT, date, date, lastOPV, date,
					lastPenta, date, date, date, lastIPTI, date, date);
			fail("Expected ValidationException");
		} catch (ValidationException e) {
			assertEquals("Errors in Record Patient History request", e
					.getMessage());
			assertNotNull("Validation Exception FaultBean is Null", e
					.getFaultInfo());
			List<String> errors = e.getFaultInfo().getErrors();
			assertNotNull("Validation Errors is Null", errors);
			assertEquals(1, errors.size());
			String error = errors.get(0);
			assertEquals("MotechID=not found", error);
		}

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void recordMotherANCVisit() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		Integer visitNumber = 1, location = 1, bpSystolic = 130, bpDiastolic = 80;
		Double weight = 63.3, hemoglobin = 11.1, fht = 130.1;
		String house = "House", community = "Community", comments = "Comments";
		Integer ttDose = 1, iptDose = 1, fhr = 130, urineProtein = 0, urineGlucose = 0;
		Boolean iptReactive = false, itnUse = true;
		Boolean vdrlReactive = false, vdrlTreatment = false, dewormer = false, maleInvolved = true;
		Boolean pmtct = false, preTest = false, postTest = false, pmtctTreatment = false, referred = false;
		Date date = new Date();
		HIVResult hivResult = HIVResult.NO_TEST;

		User staff = new User(1);
		Location facilityLocation = new Location(1);
		Facility facility = new Facility();
		facility.setLocation(facilityLocation);
		org.openmrs.Patient patient = new org.openmrs.Patient(2);

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				staff);
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(facility);
		expect(registrarBean.isValidMotechIdCheckDigit(motechId)).andReturn(
				true);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);
		registrarBean.recordMotherANCVisit(staff, facilityLocation, date,
				patient, visitNumber, location, house, community, date,
				bpSystolic, bpDiastolic, weight, ttDose, iptDose, iptReactive,
				itnUse, fht, fhr, urineProtein, urineGlucose, hemoglobin,
				vdrlReactive, vdrlTreatment, dewormer, maleInvolved, pmtct,
				preTest, hivResult, postTest, pmtctTreatment, referred, date,
				comments);

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
	public void recordMotherANCVisitInvalidPatientId()
			throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		Integer visitNumber = 1, location = 1, bpSystolic = 130, bpDiastolic = 80;
		Double weight = 63.3, hemoglobin = 11.1, fht = 130.1;
		String house = "House", community = "Community", comments = "Comments";
		Integer ttDose = 1, iptDose = 1, fhr = 130, urineProtein = 0, urineGlucose = 0;
		Boolean iptReactive = false, itnUse = true;
		Boolean vdrlReactive = false, vdrlTreatment = false, dewormer = false, maleInvolved = true;
		Boolean pmtct = false, preTest = false, postTest = false, pmtctTreatment = false, referred = false;
		Date date = new Date();
		HIVResult hivResult = HIVResult.NO_TEST;

		User staff = new User(1);

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				staff);
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(
				new Facility());
		expect(registrarBean.isValidMotechIdCheckDigit(motechId)).andReturn(
				true);
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
			List<String> errors = e.getFaultInfo().getErrors();
			assertNotNull("Validation Errors is Null", errors);
			assertEquals(1, errors.size());
			String error = errors.get(0);
			assertEquals("MotechID=not found", error);
		}

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void recordPregnancyTermination() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		Integer terminationType = 1, procedure = 2;
		Integer[] complications = new Integer[] { 1, 3, 5, 7 };
		Boolean maternalDeath = false, referred = false, postCounsel = true, postAccept = true;
		String comments = "Comments";
		Date date = new Date();

		User staff = new User(1);
		Location facilityLocation = new Location(1);
		Facility facility = new Facility();
		facility.setLocation(facilityLocation);
		org.openmrs.Patient patient = new org.openmrs.Patient(2);

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				staff);
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(facility);
		expect(registrarBean.isValidMotechIdCheckDigit(motechId)).andReturn(
				true);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);
		registrarBean.recordPregnancyTermination(eq(staff),
				eq(facilityLocation), eq(date), eq(patient),
				eq(terminationType), eq(procedure), aryEq(complications),
				eq(maternalDeath), eq(referred), eq(postCounsel),
				eq(postAccept), eq(comments));

		replay(registrarBean, openmrsBean);

		regWs.recordPregnancyTermination(staffId, facilityId, date, motechId,
				terminationType, procedure, complications, maternalDeath,
				referred, postCounsel, postAccept, comments);

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void recordPregnancyTerminationInvalidPatientId()
			throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		Integer terminationType = 1, procedure = 2;
		Integer[] complications = new Integer[] { 1, 3, 5, 7 };
		Boolean maternalDeath = false, referred = false, postCounsel = true, postAccept = true;
		String comments = "Comments";
		Date date = new Date();

		User staff = new User(1);

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				staff);
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(
				new Facility());
		expect(registrarBean.isValidMotechIdCheckDigit(motechId)).andReturn(
				true);
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
			List<String> errors = e.getFaultInfo().getErrors();
			assertNotNull("Validation Errors is Null", errors);
			assertEquals(1, errors.size());
			String error = errors.get(0);
			assertEquals("MotechID=not found", error);
		}

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void recordPregnancyDelivery() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		Integer child1Id = 246, child2Id = 468, child3Id = 579, deliveredBy = 1;
		Integer[] complications = new Integer[] { 1, 3, 5, 7 };
		String child1Name = "Child1First", child2Name = "Child2First", child3Name = "Child3First", comments = "Comments";
		Integer mode = 1, outcome = 2, location = 1, vvf = 2;
		Double child1Weight = 4.2, child2Weight = 4.4, child3Weight = 3.7;
		Boolean maternalDeath = false, maleInvolved = true;
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

		User staff = new User(1);
		Location facilityLocation = new Location(1);
		Facility facility = new Facility();
		facility.setLocation(facilityLocation);
		org.openmrs.Patient patient = new org.openmrs.Patient(2);

		List<org.openmrs.Patient> childPatients = new ArrayList<org.openmrs.Patient>();
		childPatients.add(new org.openmrs.Patient(3));

		Capture<List<BirthOutcomeChild>> outcomesCapture = new Capture<List<BirthOutcomeChild>>();

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				staff);
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(facility);
		expect(registrarBean.isValidMotechIdCheckDigit(motechId)).andReturn(
				true);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);
		expect(registrarBean.isValidMotechIdCheckDigit(child1Id)).andReturn(
				true);
		expect(openmrsBean.getPatientByMotechId(child1Id.toString()))
				.andReturn(null);
		expect(registrarBean.isValidMotechIdCheckDigit(child2Id)).andReturn(
				true);
		expect(openmrsBean.getPatientByMotechId(child2Id.toString()))
				.andReturn(null);
		expect(registrarBean.isValidMotechIdCheckDigit(child3Id)).andReturn(
				true);
		expect(openmrsBean.getPatientByMotechId(child3Id.toString()))
				.andReturn(null);
		expect(
				registrarBean.recordPregnancyDelivery(eq(staff),
						eq(facility), eq(date), eq(patient), eq(mode),
						eq(outcome), eq(location), eq(deliveredBy),
						eq(maleInvolved), aryEq(complications), eq(vvf),
						eq(maternalDeath), eq(comments),
						capture(outcomesCapture))).andReturn(childPatients);
		expect(patientModelConverter.patientToWebService(childPatients, true))
				.andReturn(new Patient[1]);

		replay(registrarBean, openmrsBean);

		regWs.recordPregnancyDelivery(staffId, facilityId, date, motechId,
				mode, outcome, location, deliveredBy, maleInvolved,
				complications, vvf, maternalDeath, comments,
				child1birthOutcome, child1RegType, child1Id, child1Sex,
				child1Name, child1Weight, child2birthOutcome, child2RegType,
				child2Id, child2Sex, child2Name, child2Weight,
				child3birthOutcome, child3RegType, child3Id, child3Sex,
				child3Name, child3Weight);

		verify(registrarBean, openmrsBean);

		List<BirthOutcomeChild> outcomes = outcomesCapture.getValue();
		assertEquals(3, outcomes.size());

		BirthOutcomeChild child1 = outcomes.get(0);
		assertEquals(child1birthOutcome, child1.getOutcome());
		assertEquals(child1Id, child1.getMotechId());
		assertEquals(child1Name, child1.getFirstName());
		assertEquals(child1Sex, child1.getSex());

		BirthOutcomeChild child2 = outcomes.get(1);
		assertEquals(child2birthOutcome, child2.getOutcome());
		assertEquals(child2Id, child2.getMotechId());
		assertEquals(child2Name, child2.getFirstName());
		assertEquals(child2Sex, child2.getSex());

		BirthOutcomeChild child3 = outcomes.get(2);
		assertEquals(child3birthOutcome, child3.getOutcome());
		assertEquals(child3Id, child3.getMotechId());
		assertEquals(child3Name, child3.getFirstName());
		assertEquals(child3Sex, child3.getSex());
	}

	@Test
	public void recordPregnancyDeliveryOneChild()
			throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		Integer child1Id = 246, deliveredBy = 1;
		Integer[] complications = new Integer[] { 1, 3, 5, 7 };
		String child1Name = "Child1First", comments = "Comments";
		Integer mode = 1, outcome = 2, location = 1, vvf = 2;
		Double child1Weight = 4.2;
		Boolean maternalDeath = false, maleInvolved = true;
		Date date = new Date();
		BirthOutcome child1birthOutcome = BirthOutcome.A;
		Gender child1Sex = Gender.FEMALE;
		RegistrationMode child1RegType = RegistrationMode.USE_PREPRINTED_ID;

		User staff = new User(1);
		Location facilityLocation = new Location(1);
		Facility facility = new Facility();
		facility.setLocation(facilityLocation);
		org.openmrs.Patient patient = new org.openmrs.Patient(2);

		List<org.openmrs.Patient> childPatients = new ArrayList<org.openmrs.Patient>();
		childPatients.add(new org.openmrs.Patient(3));

		Capture<List<BirthOutcomeChild>> outcomesCapture = new Capture<List<BirthOutcomeChild>>();

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				staff);
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(facility);
		expect(registrarBean.isValidMotechIdCheckDigit(motechId)).andReturn(
				true);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);
		expect(registrarBean.isValidMotechIdCheckDigit(child1Id)).andReturn(
				true);
		expect(openmrsBean.getPatientByMotechId(child1Id.toString()))
				.andReturn(null);
		expect(
				registrarBean.recordPregnancyDelivery(eq(staff),
						eq(facility), eq(date), eq(patient), eq(mode),
						eq(outcome), eq(location), eq(deliveredBy),
						eq(maleInvolved), aryEq(complications), eq(vvf),
						eq(maternalDeath), eq(comments),
						capture(outcomesCapture))).andReturn(childPatients);
		expect(patientModelConverter.patientToWebService(childPatients, true))
				.andReturn(new Patient[1]);

		replay(registrarBean, openmrsBean);

		regWs.recordPregnancyDelivery(staffId, facilityId, date, motechId,
				mode, outcome, location, deliveredBy, maleInvolved,
				complications, vvf, maternalDeath, comments,
				child1birthOutcome, child1RegType, child1Id, child1Sex,
				child1Name, child1Weight, null, null, null, null, null, null,
				null, null, null, null, null, null);

		verify(registrarBean, openmrsBean);

		List<BirthOutcomeChild> outcomes = outcomesCapture.getValue();
		assertEquals(1, outcomes.size());

		BirthOutcomeChild child1 = outcomes.get(0);
		assertEquals(child1birthOutcome, child1.getOutcome());
		assertEquals(child1Id, child1.getMotechId());
		assertEquals(child1Name, child1.getFirstName());
		assertEquals(child1Sex, child1.getSex());
	}

	@Test
	public void recordPregnancyDeliveryInvalidIds()
			throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		Integer child1Id = 246, child2Id = 246, child3Id = 246, deliveredBy = 1;
		Integer[] complications = new Integer[] { 1, 3, 5, 7 };
		String child1Name = "Child1First", child2Name = "Child2First", child3Name = "Child3First", comments = "Comments";
		Integer method = 1, outcome = 2, location = 1, vvf = 2;
		Double child1Weight = 4.2, child2Weight = 4.4, child3Weight = 3.7;
		Boolean maternalDeath = false, maleInvolved = true;
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

		User staff = new User(1);

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				staff);
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(
				new Facility());
		expect(registrarBean.isValidMotechIdCheckDigit(motechId)).andReturn(
				true);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(null);
		expect(registrarBean.isValidMotechIdCheckDigit(child1Id)).andReturn(
				true);
		expect(openmrsBean.getPatientByMotechId(child1Id.toString()))
				.andReturn(null);
		expect(registrarBean.isValidMotechIdCheckDigit(child2Id)).andReturn(
				true);
		expect(openmrsBean.getPatientByMotechId(child2Id.toString()))
				.andReturn(null);
		expect(registrarBean.isValidMotechIdCheckDigit(child3Id)).andReturn(
				true);
		expect(openmrsBean.getPatientByMotechId(child3Id.toString()))
				.andReturn(null);

		replay(registrarBean, openmrsBean);

		try {
			regWs.recordPregnancyDelivery(staffId, facilityId, date, motechId,
					method, outcome, location, deliveredBy, maleInvolved,
					complications, vvf, maternalDeath, comments,
					child1birthOutcome, child1RegType, child1Id, child1Sex,
					child1Name, child1Weight, child2birthOutcome,
					child2RegType, child2Id, child2Sex, child2Name,
					child2Weight, child3birthOutcome, child3RegType, child3Id,
					child3Sex, child3Name, child3Weight);
			fail("Expected ValidationException");
		} catch (ValidationException e) {
			assertEquals("Errors in Record Pregnancy Delivery request", e
					.getMessage());
			assertNotNull("Validation Exception FaultBean is Null", e
					.getFaultInfo());
			List<String> errors = e.getFaultInfo().getErrors();
			assertNotNull("Validation Errors is Null", errors);
			assertEquals(3, errors.size());
			String error = errors.get(0);
			assertEquals("MotechID=not found", error); // Check exists
			error = errors.get(1);
			assertEquals("Child2MotechID=in use", error); // Check conflicts
			error = errors.get(2);
			assertEquals("Child3MotechID=in use", error); // Check conflicts
		}

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void recordDeliveryNotification() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		Date date = new Date();

		User staff = new User(1);
		Location facilityLocation = new Location(1);
		Facility facility = new Facility();
		facility.setLocation(facilityLocation);
		org.openmrs.Patient patient = new org.openmrs.Patient(2);

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				staff);
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(facility);
		expect(registrarBean.isValidMotechIdCheckDigit(motechId)).andReturn(
				true);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);
		registrarBean.recordPregnancyDeliveryNotification(staff,
				facilityLocation, date, patient);

		replay(registrarBean, openmrsBean);

		regWs.recordDeliveryNotification(staffId, facilityId, date, motechId);

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void recordRecordDeliveryNotificationInvalidPatientId()
			throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		Date date = new Date();

		User staff = new User(1);

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				staff);
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(
				new Facility());
		expect(registrarBean.isValidMotechIdCheckDigit(motechId)).andReturn(
				true);
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
			List<String> errors = e.getFaultInfo().getErrors();
			assertNotNull("Validation Errors is Null", errors);
			assertEquals(1, errors.size());
			String error = errors.get(0);
			assertEquals("MotechID=not found", error);
		}

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void recordMotherPNCVisit() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		Integer visitNumber = 1, location = 1, ttDose = 1;
		Integer lochiaColour = 1;
		Double fht = 140.2, temperature = 25.3;
		String house = "House", community = "Community", comments = "Comments";
		Boolean referred = false, maleInvolved = true;
		Boolean vitaminA = true, lochiaExcess = false, lochiaFoul = false;
		Date date = new Date();

		User staff = new User(1);
		Location facilityLocation = new Location(1);
		Facility facility = new Facility();
		facility.setLocation(facilityLocation);
		org.openmrs.Patient patient = new org.openmrs.Patient(2);

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				staff);
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(facility);
		expect(registrarBean.isValidMotechIdCheckDigit(motechId)).andReturn(
				true);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);
		registrarBean.recordMotherPNCVisit(staff, facilityLocation, date,
				patient, visitNumber, location, house, community, referred,
				maleInvolved, vitaminA, ttDose, lochiaColour, lochiaExcess,
				lochiaFoul, temperature, fht, comments);

		replay(registrarBean, openmrsBean);

		regWs.recordMotherPNCVisit(staffId, facilityId, date, motechId,
				visitNumber, location, house, community, referred,
				maleInvolved, vitaminA, ttDose, lochiaColour, lochiaExcess,
				lochiaFoul, temperature, fht, comments);

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void recordMotherPNCVisitInvalidPatientId()
			throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		Integer visitNumber = 1, location = 1, ttDose = 1;
		Integer lochiaColour = 1;
		Double fht = 140.2, temperature = 25.3;
		String house = "House", community = "Community", comments = "Comments";
		Boolean referred = false, maleInvolved = true;
		Boolean vitaminA = true, lochiaExcess = false, lochiaFoul = false;
		Date date = new Date();

		User staff = new User(1);

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				staff);
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(
				new Facility());
		expect(registrarBean.isValidMotechIdCheckDigit(motechId)).andReturn(
				true);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(null);

		replay(registrarBean, openmrsBean);

		try {
			regWs.recordMotherPNCVisit(staffId, facilityId, date, motechId,
					visitNumber, location, house, community, referred,
					maleInvolved, vitaminA, ttDose, lochiaColour, lochiaExcess,
					lochiaFoul, temperature, fht, comments);
			fail("Expected ValidationException");
		} catch (ValidationException e) {
			assertEquals("Errors in Record Mother PNC Visit request", e
					.getMessage());
			assertNotNull("Validation Exception FaultBean is Null", e
					.getFaultInfo());
			List<String> errors = e.getFaultInfo().getErrors();
			assertNotNull("Validation Errors is Null", errors);
			assertEquals(1, errors.size());
			String error = errors.get(0);
			assertEquals("MotechID=not found", error);
		}

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void recordDeath() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		Date date = new Date();

		User staff = new User(1);
		Location facilityLocation = new Location(1);
		Facility facility = new Facility();
		facility.setLocation(facilityLocation);
		org.openmrs.Patient patient = new org.openmrs.Patient(2);

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				staff);
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(facility);
		expect(registrarBean.isValidMotechIdCheckDigit(motechId)).andReturn(
				true);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);
		registrarBean.recordDeath(staff, facilityLocation, date, patient);

		replay(registrarBean, openmrsBean);

		regWs.recordDeath(staffId, facilityId, date, motechId);

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void recordDeathInvalidPatientId() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		Date date = new Date();

		User staff = new User(1);

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				staff);
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(
				new Facility());
		expect(registrarBean.isValidMotechIdCheckDigit(motechId)).andReturn(
				true);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(null);

		replay(registrarBean, openmrsBean);

		try {
			regWs.recordDeath(staffId, facilityId, date, motechId);
			fail("Expected ValidationException");
		} catch (ValidationException e) {
			assertEquals("Errors in Record Death request", e.getMessage());
			assertNotNull("Validation Exception FaultBean is Null", e
					.getFaultInfo());
			List<String> errors = e.getFaultInfo().getErrors();
			assertNotNull("Validation Errors is Null", errors);
			assertEquals(1, errors.size());
			String error = errors.get(0);
			assertEquals("MotechID=not found", error);
		}

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void recordTTVisit() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		Integer ttDose = 1;
		Date date = new Date();

		User staff = new User(1);
		Location facilityLocation = new Location(1);
		Facility facility = new Facility();
		facility.setLocation(facilityLocation);
		org.openmrs.Patient patient = new org.openmrs.Patient(2);

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				staff);
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(facility);
		expect(registrarBean.isValidMotechIdCheckDigit(motechId)).andReturn(
				true);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);
		registrarBean.recordTTVisit(staff, facilityLocation, date, patient,
				ttDose);

		replay(registrarBean, openmrsBean);

		regWs.recordTTVisit(staffId, facilityId, date, motechId, ttDose);

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void recordTTVisitInvalidPatientId() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		Integer ttDose = 1;
		Date date = new Date();

		User staff = new User(1);

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				staff);
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(
				new Facility());
		expect(registrarBean.isValidMotechIdCheckDigit(motechId)).andReturn(
				true);
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
			List<String> errors = e.getFaultInfo().getErrors();
			assertNotNull("Validation Errors is Null", errors);
			assertEquals(1, errors.size());
			String error = errors.get(0);
			assertEquals("MotechID=not found", error);
		}

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void recordChildPNCVisit() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		Integer visitNumber = 1, location = 1, respiration = 60;
		String house = "House", community = "Community", comments = "Comments";
		Boolean referred = false, maleInvolved = true;
		Boolean bcg = true, opv0 = true, cordCondition = true, babyCondition = true;
		Double weight = 26.1, temperature = 25.6;
		Date date = new Date();

		User staff = new User(1);
		Location facilityLocation = new Location(1);
		Facility facility = new Facility();
		facility.setLocation(facilityLocation);
		org.openmrs.Patient patient = new org.openmrs.Patient(2);

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				staff);
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(facility);
		expect(registrarBean.isValidMotechIdCheckDigit(motechId)).andReturn(
				true);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);
		registrarBean.recordChildPNCVisit(staff, facilityLocation, date,
				patient, visitNumber, location, house, community, referred,
				maleInvolved, weight, temperature, bcg, opv0, respiration,
				cordCondition, babyCondition, comments);

		replay(registrarBean, openmrsBean);

		regWs.recordChildPNCVisit(staffId, facilityId, date, motechId,
				visitNumber, location, house, community, referred,
				maleInvolved, weight, temperature, bcg, opv0, respiration,
				cordCondition, babyCondition, comments);

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void recordChildPNCVisitInvalidPatientId()
			throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		Integer visitNumber = 1, location = 1, respiration = 60;
		String house = "House", community = "Community", comments = "Comments";
		Boolean referred = false, maleInvolved = true;
		Boolean bcg = true, opv0 = true, cordCondition = true, babyCondition = true;
		Double weight = 26.1, temperature = 25.6;
		Date date = new Date();

		User staff = new User(1);

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				staff);
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(
				new Facility());
		expect(registrarBean.isValidMotechIdCheckDigit(motechId)).andReturn(
				true);
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
			List<String> errors = e.getFaultInfo().getErrors();
			assertNotNull("Validation Errors is Null", errors);
			assertEquals(1, errors.size());
			String error = errors.get(0);
			assertEquals("MotechID=not found", error);
		}

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void recordChildCWCVisit() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		String house = "House", community = "Community", comments = "Comments";
		Integer location = 1, opvDose = 1, pentaDose = 1, iptiDose = 1;
		Boolean bcg = true, yellowFever = true, csm = true, measles = true, vitaminA = true;
		Boolean dewormer = false, maleInvolved = true;
		Double weight = 25.2, muac = 5.1, height = 37.2;
		Date date = new Date();

		User staff = new User(1);
		Location facilityLocation = new Location(1);
		Facility facility = new Facility();
		facility.setLocation(facilityLocation);
		org.openmrs.Patient patient = new org.openmrs.Patient(2);

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				staff);
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(facility);
		expect(registrarBean.isValidMotechIdCheckDigit(motechId)).andReturn(
				true);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);
		registrarBean.recordChildCWCVisit(staff, facilityLocation, date,
				patient, location, house, community, bcg, opvDose, pentaDose,
				measles, yellowFever, csm, iptiDose, vitaminA, dewormer,
				weight, muac, height, maleInvolved, comments);

		replay(registrarBean, openmrsBean);

		regWs.recordChildCWCVisit(staffId, facilityId, date, motechId,
				location, house, community, bcg, opvDose, pentaDose, measles,
				yellowFever, csm, iptiDose, vitaminA, dewormer, weight, muac,
				height, maleInvolved, comments);

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void recordChildCWCVisitInvalidPatientId()
			throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		String house = "House", community = "Community", comments = "Comments";
		Integer location = 1, opvDose = 1, pentaDose = 1, iptiDose = 1;
		Boolean bcg = true, yellowFever = true, csm = true, measles = true, vitaminA = true;
		Boolean dewormer = false, maleInvolved = true;
		Double weight = 25.2, muac = 5.1, height = 37.2;
		Date date = new Date();

		User staff = new User(1);

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				staff);
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(
				new Facility());
		expect(registrarBean.isValidMotechIdCheckDigit(motechId)).andReturn(
				true);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(null);

		replay(registrarBean, openmrsBean);

		try {
			regWs.recordChildCWCVisit(staffId, facilityId, date, motechId,
					location, house, community, bcg, opvDose, pentaDose,
					measles, yellowFever, csm, iptiDose, vitaminA, dewormer,
					weight, muac, height, maleInvolved, comments);
			fail("Expected ValidationException");
		} catch (ValidationException e) {
			assertEquals("Errors in Record Child CWC Visit request", e
					.getMessage());
			assertNotNull("Validation Exception FaultBean is Null", e
					.getFaultInfo());
			List<String> errors = e.getFaultInfo().getErrors();
			assertNotNull("Validation Errors is Null", errors);
			assertEquals(1, errors.size());
			String error = errors.get(0);
			assertEquals("MotechID=not found", error);
		}

		verify(registrarBean, openmrsBean);
	}

	@Test
    @Ignore
	public void registerPatient() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3, motherMotechId = 4;
		String firstName = "First", middleName = "Middle", lastName = "Last", prefName = "Pref";
		String nhis = "NHIS", address = "Address", language = "Language";
		Gender gender = Gender.FEMALE;
		Boolean estBirthDate = false, insured = true, delivDateConf = true, enroll = true, consent = true;
		Integer messageWeek = 5;
		String phone = "15555555";
		Integer communityId = 11111;
		Date date = new Date();
		RegistrationMode mode = RegistrationMode.USE_PREPRINTED_ID;
		RegistrantType type = RegistrantType.CHILD_UNDER_FIVE;
		ContactNumberType phoneType = ContactNumberType.PERSONAL;
		MediaType format = MediaType.VOICE;
		DayOfWeek day = DayOfWeek.MONDAY;
		InterestReason reason = InterestReason.RECENTLY_DELIVERED;
		HowLearned how = HowLearned.GHS_NURSE;
        String cwcRegNumber="1234";
        String ancRegNumber="1234";
        double height = 23;
        int gravida = 20;
        int parity = 3;
        int lastIPT = 1;
        Date lastIPTDate = new Date();
        int lastTT = 1;
        Date lastTTDate = new Date();
        Date bcgDate = new Date();
        int lastOPV = 1;
        Date lastOPVDate = new Date();
        int lastPenta = 1;
        Date lastPentaDate = new Date();
        Date measlesDate = new Date();
        Date yellowFeverDate = new Date();
        int lastIPTI = 1;
        Date lastIPTIDate = new Date();
        Date lastVitaminADate = new Date();
        int whyNoHistory = 1;

		User staff = new User(1);
		Location facilityLocation = new Location(1);
		Facility facility = new Facility();
		facility.setLocation(facilityLocation);
		org.openmrs.Patient patient = null;
		org.openmrs.Patient mother = new org.openmrs.Patient(3);
		Community comm = new Community();

		org.openmrs.Patient createdPatient = new org.openmrs.Patient(4);

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				staff);
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(facility);
		expect(registrarBean.getCommunityById(communityId)).andReturn(comm);
		expect(registrarBean.isValidMotechIdCheckDigit(motechId)).andReturn(
				true);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);
		expect(registrarBean.isValidMotechIdCheckDigit(motherMotechId))
				.andReturn(true);
		expect(openmrsBean.getPatientByMotechId(motherMotechId.toString()))
				.andReturn(mother);
		expect(
				registrarBean.registerPatient(staff, facility, date,
						mode, motechId, type, firstName, middleName, lastName,
						prefName, date, estBirthDate, gender, insured, nhis,
						date, mother, comm, address, phone, date,
						delivDateConf, enroll, consent, phoneType, format,
						language, day, date, reason, how, messageWeek))
				.andReturn(createdPatient);
		expect(patientModelConverter.patientToWebService(createdPatient, true))
				.andReturn(new Patient());

		replay(registrarBean, openmrsBean, patientModelConverter);

		Patient wsPatient = regWs.registerPatient(staffId, facilityId, date,
                mode, motechId, type, firstName, middleName, lastName,
                prefName, date, estBirthDate, gender, insured, nhis, date,
                motherMotechId, communityId, address, phone, date,
                delivDateConf, enroll, consent, phoneType, format, language,
                day, date, reason, how, messageWeek, cwcRegNumber, true, date,
                ancRegNumber, "0", date, height, gravida, parity, lastIPT, lastIPTDate,
                lastTT,lastTTDate,bcgDate,lastOPV,lastOPVDate,lastPenta,
                lastPentaDate,measlesDate,yellowFeverDate,lastIPTI,lastIPTIDate,
                lastVitaminADate, whyNoHistory);

		verify(registrarBean, openmrsBean, patientModelConverter);

		assertNotNull("Patient is null", wsPatient);
	}

	@Test
	public void registerPatientAllErrors() {
		Integer staffId = 1, facilityId = 2, motechId = 3, motherMotechId = 4;
		String firstName = "First", middleName = "Middle", lastName = "Last", prefName = "Pref";
		String nhis = "NHIS", address = "Address", language = "Language";
		Gender gender = Gender.FEMALE;
		Boolean estBirthDate = false, insured = true, delivDateConf = true, enroll = true, consent = true;
		Integer messageWeek = 5;
		String phone = "15555555";
        String cwcRegNumber="1234";
        String ancRegNumber="1234";
		Integer community = 11111;
		Date date = new Date();
		RegistrationMode mode = RegistrationMode.USE_PREPRINTED_ID;
		RegistrantType type = RegistrantType.CHILD_UNDER_FIVE;
		ContactNumberType phoneType = ContactNumberType.PERSONAL;
		MediaType format = MediaType.VOICE;
		DayOfWeek day = DayOfWeek.MONDAY;
		InterestReason reason = InterestReason.RECENTLY_DELIVERED;
		HowLearned how = HowLearned.GHS_NURSE;
        double height = 23;
        int gravida = 20;
        int parity = 3;
        int lastIPT = 1;
        Date lastIPTDate = new Date();
        int lastTT = 1;
        Date lastTTDate = new Date();
        Date bcgDate = new Date();
        int lastOPV = 1;
        Date lastOPVDate = new Date();
        int lastPenta = 1;
        Date lastPentaDate = new Date();
        Date measlesDate = new Date();
        Date yellowFeverDate = new Date();
        int lastIPTI = 1;
        Date lastIPTIDate = new Date();
        Date lastVitaminADate = new Date();
        int whyNoHistory = 1;

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, -6);
		Date childBirthDate = calendar.getTime();

		User staff = null;
		org.openmrs.Patient patient = new org.openmrs.Patient(2);
		org.openmrs.Patient mother = null;

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				staff);
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(null);
		expect(registrarBean.getCommunityById(community)).andReturn(null);
		expect(registrarBean.isValidMotechIdCheckDigit(motechId)).andReturn(
				true);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);
		expect(registrarBean.isValidMotechIdCheckDigit(motherMotechId))
				.andReturn(true);
		expect(openmrsBean.getPatientByMotechId(motherMotechId.toString()))
				.andReturn(mother);

		replay(registrarBean, openmrsBean);

		try {
			regWs.registerPatient(staffId, facilityId, date, mode, motechId,
					type, firstName, middleName, lastName, prefName,
					childBirthDate, estBirthDate, gender, insured, nhis, date,
					motherMotechId, community, address, phone, date,
					delivDateConf, enroll, consent, phoneType, format,
					language, day, date, reason, how, messageWeek, cwcRegNumber, true, date,
                    ancRegNumber, "0", date, height, gravida, parity, lastIPT, lastIPTDate,
                    lastTT,lastTTDate,bcgDate,lastOPV,lastOPVDate,lastPenta,
                    lastPentaDate,measlesDate,yellowFeverDate,lastIPTI,lastIPTIDate,
                    lastVitaminADate, whyNoHistory);
			fail("Expected ValidationException");
		} catch (ValidationException e) {
			assertEquals("Errors in Register Patient request", e.getMessage());
			assertNotNull("Validation Exception FaultBean is Null", e
					.getFaultInfo());
			List<String> errors = e.getFaultInfo().getErrors();
			assertNotNull("Validation Errors is Null", errors);
			assertEquals(8, errors.size());
			String staffError = errors.get(0);
			assertEquals("StaffID=not found", staffError);
			String facilityError = errors.get(1);
			assertEquals("FacilityID=not found", facilityError);
			String communityError = errors.get(2);
			assertEquals("Community=not found", communityError);
			String patientError = errors.get(3);
			assertEquals("MotechID=in use", patientError);
			String motherError = errors.get(4);
			assertEquals("MotherMotechID=not found", motherError);
			String dobError = errors.get(5);
			assertEquals("DOB=invalid", dobError);
		}

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void registerPregnancy() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		String language = "Language";
		Boolean enroll = true, consent = true;
		String phone = "15555555";
		Date date = new Date();
		ContactNumberType phoneType = ContactNumberType.PERSONAL;
		MediaType format = MediaType.VOICE;
		DayOfWeek day = DayOfWeek.MONDAY;
		HowLearned how = HowLearned.GHS_NURSE;

		User staff = new User(1);
		Location facilityLocation = new Location(1);
		Facility facility = new Facility();
		facility.setLocation(facilityLocation);
		org.openmrs.Patient patient = new org.openmrs.Patient(2);

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				staff);
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(facility);
		expect(registrarBean.isValidMotechIdCheckDigit(motechId)).andReturn(
				true);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);
		registrarBean.registerPregnancy(staff, facilityLocation, date, patient,
				date, enroll, consent, phoneType, phone, format, language, day,
				date, how);

		replay(registrarBean, openmrsBean);

		regWs.registerPregnancy(staffId, facilityId, date, motechId, date,
				enroll, consent, phoneType, phone, format, language, day, date,
				how);

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void registerPregnancyInvalidIds() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		String language = "Language";
		Boolean enroll = true, consent = true;
		String phone = "15555555";
		Date date = new Date();
		ContactNumberType phoneType = ContactNumberType.PERSONAL;
		MediaType format = MediaType.VOICE;
		DayOfWeek day = DayOfWeek.MONDAY;
		HowLearned how = HowLearned.GHS_NURSE;

		User staff = null;
		org.openmrs.Patient patient = null;

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				staff);
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(null);
		expect(registrarBean.isValidMotechIdCheckDigit(motechId)).andReturn(
				true);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);

		replay(registrarBean, openmrsBean);

		try {
			regWs.registerPregnancy(staffId, facilityId, date, motechId, date,
					enroll, consent, phoneType, phone, format, language, day,
					date, how);
			fail("Expected ValidationException");
		} catch (ValidationException e) {
			assertEquals("Errors in Register Pregnancy request", e.getMessage());
			assertNotNull("Validation Exception FaultBean is Null", e
					.getFaultInfo());
			List<String> errors = e.getFaultInfo().getErrors();
			assertNotNull("Validation Errors is Null", errors);
			assertEquals(3, errors.size());
			String staffError = errors.get(0);
			assertEquals("StaffID=not found", staffError);
			String facilityError = errors.get(1);
			assertEquals("FacilityID=not found", facilityError);
			String patientError = errors.get(2);
			assertEquals("MotechID=not found", patientError);
		}

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void testRegisterANCMother() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		String language = "Language", regNumber = "RegNumber";
		Boolean enroll = true, consent = true;
		Integer gravida = 0, parity = 0;
		Double height = 45.3;
		String phone = "15555555";
		Date date = new Date();
		ContactNumberType phoneType = ContactNumberType.PERSONAL;
		MediaType format = MediaType.VOICE;
		DayOfWeek day = DayOfWeek.MONDAY;
		HowLearned how = HowLearned.GHS_NURSE;

        List<Encounter> encounterList = new ArrayList<Encounter>();
        Encounter encounter1 = new Encounter();
        encounter1.setEncounterDatetime(date);
        encounterList.add(encounter1);

		User staff = new User(1);
		Location facilityLocation = new Location(1);
		Facility facility = new Facility();
		facility.setLocation(facilityLocation);
		org.openmrs.Patient patient = new org.openmrs.Patient(2);

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				staff);
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(facility);
		expect(registrarBean.isValidMotechIdCheckDigit(motechId)).andReturn(
				true);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);
        expect(registrarBean.getEncounters(patient, MotechConstants.ENCOUNTER_TYPE_PREGREGVISIT, patient.getBirthdate()))
                .andReturn(encounterList);
		registrarBean.registerANCMother(staff, facilityLocation, date, patient,
				regNumber, date, height, gravida, parity, enroll, consent,
				phoneType, phone, format, language, day, date, how);

		replay(registrarBean, openmrsBean);

		regWs.registerANCMother(staffId, facilityId, date, motechId, regNumber,
				date, height, gravida, parity, enroll, consent, phoneType,
				phone, format, language, day, date, how);

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void registerANCMotherInvalidIds() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		String language = "Language", regNumber = "RegNumber";
		Boolean enroll = true, consent = true;
		Integer gravida = 0, parity = 0;
		Double height = 45.3;
		String phone = "15555555";
		Date date = new Date();
		ContactNumberType phoneType = ContactNumberType.PERSONAL;
		MediaType format = MediaType.VOICE;
		DayOfWeek day = DayOfWeek.MONDAY;
		HowLearned how = HowLearned.GHS_NURSE;

		User staff = null;
		org.openmrs.Patient patient = null;

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				staff);
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(null);
		expect(registrarBean.isValidMotechIdCheckDigit(motechId)).andReturn(
				true);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);

		replay(registrarBean, openmrsBean);

		try {

			regWs.registerANCMother(staffId, facilityId, date, motechId,
					regNumber, date, height, gravida, parity, enroll, consent,
					phoneType, phone, format, language, day, date, how);
			fail("Expected ValidationException");
		} catch (ValidationException e) {
			assertEquals("Errors in Register ANC Mother request", e
					.getMessage());
			assertNotNull("Validation Exception FaultBean is Null", e
					.getFaultInfo());
			List<String> errors = e.getFaultInfo().getErrors();
			assertNotNull("Validation Errors is Null", errors);
			assertEquals(3, errors.size());
			String staffError = errors.get(0);
			assertEquals("StaffID=not found", staffError);
			String facilityError = errors.get(1);
			assertEquals("FacilityID=not found", facilityError);
			String patientError = errors.get(2);
			assertEquals("MotechID=not found", patientError);
		}

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void registerCWCChild() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		String language = "Language", regNumber = "RegNumber";
		Boolean enroll = true, consent = true;
		String phone = "15555555";
		Date date = new Date();
		ContactNumberType phoneType = ContactNumberType.PERSONAL;
		MediaType format = MediaType.VOICE;
		DayOfWeek day = DayOfWeek.MONDAY;
		HowLearned how = HowLearned.GHS_NURSE;

		User staff = new User(1);
		Location facilityLocation = new Location(1);
		Facility facility = new Facility();
		facility.setLocation(facilityLocation);
		org.openmrs.Patient patient = new org.openmrs.Patient(2);

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				staff);
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(facility);
		expect(registrarBean.isValidMotechIdCheckDigit(motechId)).andReturn(
				true);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);
		registrarBean.registerCWCChild(staff, facilityLocation, date, patient,
				regNumber, enroll, consent, phoneType, phone, format, language,
				day, date, how);

		replay(registrarBean, openmrsBean);

		regWs.registerCWCChild(staffId, facilityId, date, motechId, regNumber,
				enroll, consent, phoneType, phone, format, language, day, date,
				how);

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void registerCWCChildInvalidIds() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		String language = "Language", regNumber = "RegNumber";
		Boolean enroll = true, consent = true;
		String phone = "15555555";
		Date date = new Date();
		ContactNumberType phoneType = ContactNumberType.PERSONAL;
		MediaType format = MediaType.VOICE;
		DayOfWeek day = DayOfWeek.MONDAY;
		HowLearned how = HowLearned.GHS_NURSE;

		User staff = null;
		org.openmrs.Patient patient = null;

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				staff);
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(null);
		expect(registrarBean.isValidMotechIdCheckDigit(motechId)).andReturn(
				true);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);

		replay(registrarBean, openmrsBean);

		try {
			regWs.registerCWCChild(staffId, facilityId, date, motechId,
					regNumber, enroll, consent, phoneType, phone, format,
					language, day, date, how);
			fail("Expected ValidationException");
		} catch (ValidationException e) {
			assertEquals("Errors in Register CWC Child request", e.getMessage());
			assertNotNull("Validation Exception FaultBean is Null", e
					.getFaultInfo());
			List<String> errors = e.getFaultInfo().getErrors();
			assertNotNull("Validation Errors is Null", errors);
			assertEquals(3, errors.size());
			String staffError = errors.get(0);
			assertEquals("StaffID=not found", staffError);
			String facilityError = errors.get(1);
			assertEquals("FacilityID=not found", facilityError);
			String patientError = errors.get(2);
			assertEquals("MotechID=not found", patientError);
		}

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void editPatient() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3 , mothersMotechId = 4;
		String phoneNumber = "2075557894";
		String nhis = "125";
        String firstName = "Martin";
        String middleName = "";
        String lastName = "Odersky";
        String prefName = "Mart";
		ContactNumberType phoneType = ContactNumberType.PERSONAL;
		Boolean stopEnrollment = false;
		Date date = new Date();

		User staff = new User(1);
		org.openmrs.Patient patient = new org.openmrs.Patient(motechId);
        patient.setBirthdateFromAge(2,new Date());
		org.openmrs.Patient mother = new org.openmrs.Patient(mothersMotechId);

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				staff);
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(
				new Facility());
		expect(registrarBean.isValidMotechIdCheckDigit(motechId)).andReturn(
				true);
        expect(registrarBean.isValidMotechIdCheckDigit(mothersMotechId)).andReturn(
				true);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);
        expect(openmrsBean.getPatientByMotechId(mothersMotechId.toString()))
                        .andReturn(mother);


		registrarBean.editPatient(staff, date, patient, mother, phoneNumber, phoneType,
				nhis, date, date, stopEnrollment);

		replay(registrarBean, openmrsBean);

		regWs.editPatient(staffId, facilityId, date, motechId,mothersMotechId ,firstName, middleName, lastName, prefName, phoneNumber,
				phoneType, nhis, date, date, stopEnrollment);

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void editPatientAllErrors() {
		Integer staffId = 1, facilityId = 2, motechId = 3 , mothersMotechId = 4;
		String phoneNumber = "2075557894";
		String nhis = "125";
        String firstName = "Martin";
        String middleName = "";
        String lastName = "Odersky";
        String prefName = "Mart";
		ContactNumberType phoneType = ContactNumberType.PERSONAL;
		Boolean stopEnrollment = false;
		Date date = new Date();

		User staff = null;
		org.openmrs.Patient patient = null;

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				staff);
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(null);
		expect(registrarBean.isValidMotechIdCheckDigit(motechId)).andReturn(
				true);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);

		replay(registrarBean, openmrsBean);

		try {
			regWs.editPatient(staffId, facilityId, date, motechId,mothersMotechId, firstName, middleName, lastName, prefName, phoneNumber,
					phoneType, nhis, date, date, stopEnrollment);
			fail("Expected ValidationException");
		} catch (ValidationException e) {
			assertEquals("Errors in Edit Patient request", e.getMessage());
			assertNotNull("Validation Exception FaultBean is Null", e
					.getFaultInfo());
			List<String> errors = e.getFaultInfo().getErrors();
			assertNotNull("Validation Errors is Null", errors);
			assertEquals(3, errors.size());
			String staffError = errors.get(0);
			assertEquals("StaffID=not found", staffError);
			String facilityError = errors.get(1);
			assertEquals("FacilityID=not found", facilityError);
			String patientError = errors.get(2);
			assertEquals("MotechID=not found", patientError);
		}

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void generalVisit() throws ValidationException {
		Integer staffId = 1, facilityId = 2;
		String serial = "Serial", comments = "Comments";
		Integer diagnosis = 5, secondDiagnosis = 6;
		Boolean insured = true, newCase = true, newPatient = true, referred = false, rdtGiven = true, rdtPositive = false, actTreated = false;
		Date date = new Date();
		Gender gender = Gender.MALE;

		User staff = new User(1);

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				staff);
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(
				new Facility());
        expect(registrarBean.isValidOutPatientVisitEntry(2, date, "Serial", Gender.MALE, date, true, 5)).andReturn(true);
		registrarBean.recordGeneralOutpatientVisit(staffId, facilityId, date,
                serial, gender, date, insured, diagnosis, secondDiagnosis,
                rdtGiven, rdtPositive, actTreated, newCase, newPatient, referred, comments);

		replay(registrarBean, openmrsBean);

		regWs.recordGeneralVisit(staffId, facilityId, date, serial, gender,
				date, insured, diagnosis, secondDiagnosis, rdtGiven,
				rdtPositive, actTreated, newCase, newPatient, referred, comments);

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void generalVisitInvalidIds() {
		Integer staffId = 1, facilityId = 2;
		String serial = "Serial", comments = "Comments";
		Integer diagnosis = 5, secondDiagnosis = 6;
		Boolean insured = true, newCase = true, newPatient = true, referred = false, rdtGiven = true, rdtPositive = false, actTreated = false;
		Date date = new Date();
		Gender gender = Gender.MALE;

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				null);
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(null);
        expect(registrarBean.isValidOutPatientVisitEntry(2, date, "Serial", Gender.MALE, date, true, 5)).andReturn(true);


		replay(registrarBean, openmrsBean);

		try {
			regWs.recordGeneralVisit(staffId, facilityId, date, serial, gender,
					date, insured, diagnosis, secondDiagnosis, rdtGiven,
					rdtPositive, actTreated, newCase, newPatient, referred, comments);
			fail("Expected ValidationException");
		} catch (ValidationException e) {
			assertEquals("Errors in General Visit request", e.getMessage());
			assertNotNull("Validation Exception FaultBean is Null", e
					.getFaultInfo());
			List<String> errors = e.getFaultInfo().getErrors();
			assertNotNull("Validation Errors is Null", errors);
			assertEquals(2, errors.size());
			String staffError = errors.get(0);
			assertEquals("StaffID=not found", staffError);
			String facilityError = errors.get(1);
			assertEquals("FacilityID=not found", facilityError);
		}

		verify(registrarBean, openmrsBean);
	}


    @Test
    public void validateForDuplicateOutpatientVisitEntry() {

        Integer staffId = 1;
        Integer facilityId = 2;
        Date visitDate = new DateUtil().dateFor(12,6,2011);
        String serialNumber = "01/2010";

        Gender sex = Gender.MALE;
        Date dob = new Date();
        Boolean insured = false;
        Integer diagnosis = 1;
        Integer secondDiag = 2;
        Boolean rdtGiven = false;
        Boolean rdtPositive = false;
        Boolean actTreated = false;
        Boolean newCase = false;
        Boolean newPatient = false;
        Boolean referred = false;
        String comments = "";

        try {

            expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
            expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn( new User(staffId));
            expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
            Facility facility = new Facility();
            expect(registrarBean.getFacilityById(facilityId)).andReturn(facility);
            expect(registrarBean.isValidOutPatientVisitEntry(facilityId, visitDate, serialNumber, sex, dob, newCase, diagnosis)).andReturn(false);

            replay(registrarBean, openmrsBean);

            regWs.recordGeneralVisit(staffId, facilityId, visitDate, serialNumber, sex, dob, insured, diagnosis, secondDiag, rdtGiven, rdtPositive, actTreated, newCase, newPatient, referred, comments);
            fail("should have thrown ValidationException for duplicate opd visit entry");

        } catch (ValidationException e) {

            assertEquals("Errors in General Visit request", e.getMessage());
			assertNotNull("Validation Exception FaultBean is Null", e.getFaultInfo());
			List<String> errors = e.getFaultInfo().getErrors();
			assertNotNull("Validation Errors is Null", errors);
			assertEquals(1, errors.size());
            assertEquals("Duplicate visit entry", errors.get(0));
        }

        verify(registrarBean, openmrsBean);
    }

    @Test
    public void validateForDuplicateOutpatientVisitEntryShouldSaveValidEntry() {

        Integer staffId = 1;
        Integer facilityId = 2;
        Date visitDate = new DateUtil().dateFor(12,6,2011);
        String serialNumber = "01/2010";

        Gender sex = Gender.MALE;
        Date dob = new Date();
        Boolean insured = false;
        Integer diagnosis = 1;
        Integer secondDiag = 2;
        Boolean rdtGiven = false;
        Boolean rdtPositive = false;
        Boolean actTreated = false;
        Boolean newCase = false;
        Boolean newPatient = false;
        Boolean referred = false;
        String comments = "";

        try {

            expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
            expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn( new User(staffId));
            expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
            Facility facility = new Facility();
            expect(registrarBean.getFacilityById(facilityId)).andReturn(facility);
            expect(registrarBean.isValidOutPatientVisitEntry(facilityId, visitDate, serialNumber, sex, dob, newCase, diagnosis)).andReturn(true);
            registrarBean.recordGeneralOutpatientVisit(staffId, facilityId, visitDate, serialNumber, sex, dob, insured, diagnosis, secondDiag, rdtGiven, rdtPositive, actTreated, newCase, newPatient, referred, comments);
            expectLastCall();

            replay(registrarBean, openmrsBean);

            regWs.recordGeneralVisit(staffId, facilityId, visitDate, serialNumber, sex, dob, insured, diagnosis, secondDiag, rdtGiven, rdtPositive, actTreated, newCase, newPatient, referred, comments);

        } catch (ValidationException e) {
            fail("should not throw the exception on valid entry");
        }

        verify(registrarBean, openmrsBean);
    }

	@Test
	public void recordChildVisit() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		String serial = "Serial", comments = "Comments";
		Integer diagnosis = 5, secondDiagnosis = 6;
		Boolean insured = true, newCase = true, newPatient = true, referred = false, rdtGiven = true, rdtPositive = false, actTreated = false;
		Date date = new Date();

		User staff = new User(1);
		Location facilityLocation = new Location(1);
		Facility facility = new Facility();
		facility.setLocation(facilityLocation);
		org.openmrs.Patient patient = new org.openmrs.Patient(2);

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				staff);
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(facility);
		expect(registrarBean.isValidMotechIdCheckDigit(motechId)).andReturn(
				true);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);
		registrarBean.recordOutpatientVisit(staff, facilityLocation, date,
				patient, serial, insured, diagnosis, secondDiagnosis, rdtGiven,
				rdtPositive, actTreated, newCase, newPatient, referred, comments);

		replay(registrarBean, openmrsBean);

		regWs.recordChildVisit(staffId, facilityId, date, serial, motechId,
				insured, diagnosis, secondDiagnosis, rdtGiven, rdtPositive,
				actTreated, newCase, newPatient, referred, comments);

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void recordChildVisitInvalidIds() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		String serial = "Serial", comments = "Comments";
		Integer diagnosis = 5, secondDiagnosis = 6;
		Boolean insured = true, newCase = true, newPatient = true, referred = false, rdtGiven = true, rdtPositive = false, actTreated = false;
		Date date = new Date();

		User staff = new User(1);

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				staff);
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(null);
		expect(registrarBean.isValidMotechIdCheckDigit(motechId)).andReturn(
				true);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(null);

		replay(registrarBean, openmrsBean);

		try {
			regWs.recordChildVisit(staffId, facilityId, date, serial, motechId,
					insured, diagnosis, secondDiagnosis, rdtGiven, rdtPositive,
					actTreated, newCase, newPatient, referred, comments);
			fail("Expected ValidationException");
		} catch (ValidationException e) {
			assertEquals("Errors in Record Child Visit request", e.getMessage());
			assertNotNull("Validation Exception FaultBean is Null", e
					.getFaultInfo());
			List<String> errors = e.getFaultInfo().getErrors();
			assertNotNull("Validation Errors is Null", errors);
			assertEquals(2, errors.size());
			String facilityError = errors.get(0);
			assertEquals("FacilityID=not found", facilityError);
			String motechidError = errors.get(1);
			assertEquals("MotechID=not found", motechidError);
		}

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void recordMotherVisit() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		String serial = "Serial", comments = "Comments";
		Integer diagnosis = 5, secondDiagnosis = 6;
		Boolean insured = true, newCase = true, newPatient = true, referred = false, rdtGiven = true, rdtPositive = false, actTreated = false;
		Date date = new Date();

		User staff = new User(1);
		Location facilityLocation = new Location(1);
		Facility facility = new Facility();
		facility.setLocation(facilityLocation);
		org.openmrs.Patient patient = new org.openmrs.Patient(2);

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				staff);
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(facility);
		expect(registrarBean.isValidMotechIdCheckDigit(motechId)).andReturn(
				true);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);
		registrarBean.recordOutpatientVisit(staff, facilityLocation, date,
				patient, serial, insured, diagnosis, secondDiagnosis, rdtGiven,
				rdtPositive, actTreated, newCase, newPatient, referred, comments);

		replay(registrarBean, openmrsBean);

		regWs.recordMotherVisit(staffId, facilityId, date, serial, motechId,
				insured, diagnosis, secondDiagnosis, rdtGiven, rdtPositive,
				actTreated, newCase, newPatient, referred, comments);

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void recordMotherVisitInvalidIds() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;
		String serial = "Serial", comments = "Comments";
		Integer diagnosis = 5, secondDiagnosis = 6;
		Boolean insured = true, newCase = true, newPatient = true, referred = false, rdtGiven = true, rdtPositive = false, actTreated = false;
		Date date = new Date();

		User staff = new User(1);

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				staff);
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(null);
		expect(registrarBean.isValidMotechIdCheckDigit(motechId)).andReturn(
				true);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(null);

		replay(registrarBean, openmrsBean);

		try {
			regWs.recordMotherVisit(staffId, facilityId, date, serial,
					motechId, insured, diagnosis, secondDiagnosis, rdtGiven,
					rdtPositive, actTreated, newCase, newPatient, referred, comments);
			fail("Expected ValidationException");
		} catch (ValidationException e) {
			assertEquals("Errors in Record Mother Visit request", e
					.getMessage());
			assertNotNull("Validation Exception FaultBean is Null", e
					.getFaultInfo());
			List<String> errors = e.getFaultInfo().getErrors();
			assertNotNull("Validation Errors is Null", errors);
			assertEquals(2, errors.size());
			String facilityError = errors.get(0);
			assertEquals("FacilityID=not found", facilityError);
			String motechidError = errors.get(1);
			assertEquals("MotechID=not found", motechidError);
		}

		verify(registrarBean, openmrsBean);
	}

	@Test
	public void queryANCDefaulters() throws ValidationException {
		Integer staffId = 1, facilityId = 2;

		Capture<String[]> encounterGroups = new Capture<String[]>();
		Capture<String[]> obsGroups = new Capture<String[]>();

		List<ExpectedEncounter> expectedEncounters = new ArrayList<ExpectedEncounter>();
		List<ExpectedObs> expectedObs = new ArrayList<ExpectedObs>();

		Facility facility = new Facility();

		Care encounterCare = new Care();
		encounterCare.setName("EncounterCare");
		Care obsCare = new Care();
		obsCare.setName("ObsCare");
		Care[] defaultedCares = { encounterCare, obsCare };

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				new User(1));
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(facility);



        expect(registrarBean.getDefaultedExpectedEncounters(eq(facility),
						capture(encounterGroups)))
				.andReturn(expectedEncounters);
        expect(registrarBean.getDefaultedExpectedObs(eq(facility),
						capture(obsGroups))).andReturn(expectedObs);
		expect(careModelConverter.defaultedToWebServiceCares(expectedEncounters,
						expectedObs)).andReturn(defaultedCares);

		replay(registrarBean, openmrsBean, careModelConverter);

		Care[] cares = regWs.queryANCDefaulters(staffId, facilityId);

		verify(registrarBean, openmrsBean, careModelConverter);

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
	public void queryTTDefaulters() throws ValidationException {
		Integer staffId = 1, facilityId = 2;

		Capture<String[]> obsGroups = new Capture<String[]>();

		List<ExpectedObs> expectedObs = new ArrayList<ExpectedObs>();

		Facility facility = new Facility();

		Care obsCare = new Care();
		obsCare.setName("ObsCare");
		Care[] obsCares = { obsCare };

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(new User(1));
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(facility);
		expect(registrarBean.getDefaultedExpectedObs(eq(facility),capture(obsGroups))).andReturn(expectedObs);
		expect(careModelConverter.defaultedObsToWebServiceCares(expectedObs)).andReturn(obsCares);

		replay(registrarBean, careModelConverter, openmrsBean);

		Care[] cares = regWs.queryTTDefaulters(staffId, facilityId);

		verify(registrarBean, careModelConverter, openmrsBean);

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

		Facility facility = new Facility();

		Care encounterCare = new Care();
		encounterCare.setName("EncounterCare");
		Care[] encounterCares = { encounterCare };

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				new User(1));
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(facility);
		expect(
				registrarBean.getDefaultedExpectedEncounters(eq(facility),
						capture(encounterGroups)))
				.andReturn(expectedEncounters);
		expect(
				careModelConverter
						.defaultedEncountersToWebServiceCares(expectedEncounters))
				.andReturn(encounterCares);

		replay(registrarBean, careModelConverter, openmrsBean);

		Care[] cares = regWs.queryMotherPNCDefaulters(staffId, facilityId);

		verify(registrarBean, careModelConverter, openmrsBean);

		assertEquals(1, encounterGroups.getValue().length);
		assertEquals("PNC(mother)", encounterGroups.getValue()[0]);

		assertNotNull("Care result array is null", cares);
		assertEquals(1, cares.length);
		assertEquals(encounterCare.getName(), cares[0].getName());
	}

	@Test
	public void queryChildPNCDefaulters() throws ValidationException {
		Integer staffId = 1, facilityId = 2;

		Capture<String[]> encounterGroups = new Capture<String[]>();

		List<ExpectedEncounter> expectedEncounters = new ArrayList<ExpectedEncounter>();

		Facility facility = new Facility();

		Care encounterCare = new Care();
		encounterCare.setName("EncounterCare");
		Care[] encounterCares = { encounterCare };

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				new User(1));
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(facility);
		expect(
				registrarBean.getDefaultedExpectedEncounters(eq(facility),
						capture(encounterGroups)))
				.andReturn(expectedEncounters);
		expect(
				careModelConverter
						.defaultedEncountersToWebServiceCares(expectedEncounters))
				.andReturn(encounterCares);

		replay(registrarBean, careModelConverter, openmrsBean);

		Care[] cares = regWs.queryChildPNCDefaulters(staffId, facilityId);

		verify(registrarBean, careModelConverter, openmrsBean);

		assertEquals(1, encounterGroups.getValue().length);
		assertEquals("PNC(baby)", encounterGroups.getValue()[0]);

		assertNotNull("Care result array is null", cares);
		assertEquals(1, cares.length);
		assertEquals(encounterCare.getName(), cares[0].getName());
	}

	@Test
	public void queryCWCDefaulters() throws ValidationException {
		Integer staffId = 1, facilityId = 2;

		Capture<String[]> obsGroups = new Capture<String[]>();

		List<ExpectedObs> expectedObs = new ArrayList<ExpectedObs>();

		Facility facility = new Facility();

		Care obsCare = new Care();
		obsCare.setName("ObsCare");
		Care[] obsCares = { obsCare };

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				new User(1));
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(facility);
		expect(
				registrarBean.getDefaultedExpectedObs(eq(facility),
						capture(obsGroups))).andReturn(expectedObs);
		expect(careModelConverter.defaultedObsToWebServiceCares(expectedObs))
				.andReturn(obsCares);

		replay(registrarBean, careModelConverter, openmrsBean);

		Care[] cares = regWs.queryCWCDefaulters(staffId, facilityId);

		verify(registrarBean, careModelConverter, openmrsBean);

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
	public void queryUpcomingDeliveries() throws ValidationException {
		Integer staffId = 1, facilityId = 2;

		List<Obs> pregnancies = new ArrayList<Obs>();
		pregnancies.add(new Obs());

		Patient patient = new Patient();
		patient.setMotechId("MotechId");
		Patient[] result = { patient };
		Facility facility = new Facility();

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				new User(1));
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(facility);
		expect(registrarBean.getUpcomingPregnanciesDueDate(facility))
				.andReturn(pregnancies);
		expect(patientModelConverter.dueDatesToWebServicePatients(pregnancies))
				.andReturn(result);

		replay(registrarBean, patientModelConverter, openmrsBean);

		Patient[] patients = regWs.queryUpcomingDeliveries(staffId, facilityId);

		verify(registrarBean, patientModelConverter, openmrsBean);

		assertNotNull("Patient result array is null", patients);
		assertEquals(1, patients.length);
	}

	@Test
	public void queryRecentDeliveries() throws ValidationException {
		Integer staffId = 1, facilityId = 2;

		List<Encounter> deliveries = new ArrayList<Encounter>();
		deliveries.add(new Encounter());

		Patient patient = new Patient();
		patient.setMotechId("MotechId");
		Patient[] result = { patient };
		Facility facility = new Facility();

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				new User(1));
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(facility);
		expect(registrarBean.getRecentDeliveries(facility)).andReturn(
				deliveries);
		expect(patientModelConverter.deliveriesToWebServicePatients(deliveries))
				.andReturn(result);

		replay(registrarBean, patientModelConverter, openmrsBean);

		Patient[] patients = regWs.queryRecentDeliveries(staffId, facilityId);

		verify(registrarBean, patientModelConverter, openmrsBean);

		assertNotNull("Patient result array is null", patients);
		assertEquals(1, patients.length);
	}

	@Test
	public void queryOverdueDeliveries() throws ValidationException {
		Integer staffId = 1, facilityId = 2;

		List<Obs> pregnancies = new ArrayList<Obs>();
		pregnancies.add(new Obs());

		Patient patient = new Patient();
		patient.setMotechId("MotechId");
		Patient[] result = { patient };
		Facility facility = new Facility();

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				new User(1));
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(facility);
		expect(registrarBean.getOverduePregnanciesDueDate(facility)).andReturn(
				pregnancies);
		expect(patientModelConverter.dueDatesToWebServicePatients(pregnancies))
				.andReturn(result);

		replay(registrarBean, patientModelConverter, openmrsBean);

		Patient[] patients = regWs.queryOverdueDeliveries(staffId, facilityId);

		verify(registrarBean, patientModelConverter, openmrsBean);

		assertNotNull("Patient result array is null", patients);
		assertEquals(1, patients.length);
	}

	@Test
	public void queryUpcomingCare() throws ValidationException {
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

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				new User(1));
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(
				new Facility());
		expect(registrarBean.isValidMotechIdCheckDigit(motechId)).andReturn(
				true);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);
		expect(patientModelConverter.patientToWebService(eq(patient), eq(true)))
				.andReturn(new Patient());

		expect(registrarBean.getUpcomingExpectedEncounters(patient)).andReturn(
				expectedEncounters);
		expect(registrarBean.getUpcomingExpectedObs(patient)).andReturn(
				expectedObs);
		expect(
				careModelConverter.upcomingToWebServiceCares(expectedEncounters,
						expectedObs, false)).andReturn(upcomingCares);

		replay(registrarBean, patientModelConverter, openmrsBean, careModelConverter);

		Patient wsPatient = regWs.queryUpcomingCare(staffId, facilityId,
				motechId);

		verify(registrarBean, patientModelConverter, openmrsBean, careModelConverter);
		verify(registrarBean, patientModelConverter, openmrsBean, careModelConverter);

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
	public void queryMotechId() throws ValidationException {
		Integer staffId = 1, facilityId = 2;
		String firstName = "FirstName", lastName = "LastName", prefName = "PrefName";
		String nhis = "NHIS";
		String phone = "22424324";
		Date birthDate = new Date();

		List<org.openmrs.Patient> patients = new ArrayList<org.openmrs.Patient>();
		patients.add(new org.openmrs.Patient(1));

		Patient patient = new Patient();
		patient.setMotechId("MotechId");
		Patient[] result = { patient };

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				new User(1));
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(
				new Facility());
		expect(
				registrarBean.getPatients(firstName, lastName, prefName,
						birthDate, facilityId, phone, nhis, null, null))
				.andReturn(patients);
		expect(patientModelConverter.patientToWebService(patients, true)).andReturn(
				result);

		replay(registrarBean, patientModelConverter, openmrsBean);

		Patient[] wsPatients = regWs.queryMotechId(staffId, facilityId,
				firstName, lastName, prefName, birthDate, nhis, phone);

		verify(registrarBean, patientModelConverter, openmrsBean);

		assertNotNull("Patient result array is null", wsPatients);
		assertEquals(1, wsPatients.length);
	}

	@Test
	public void queryPatient() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;

		org.openmrs.Patient patient = new org.openmrs.Patient(1);

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				new User(1));
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(
				new Facility());
		expect(registrarBean.isValidMotechIdCheckDigit(motechId)).andReturn(
				true);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);
		expect(patientModelConverter.patientToWebService(eq(patient), eq(false)))
				.andReturn(new Patient());

		replay(registrarBean, patientModelConverter, openmrsBean);

		regWs.queryPatient(staffId, facilityId, motechId);

		verify(registrarBean, patientModelConverter, openmrsBean);
	}

	@Test
	public void queryPatientInvalidIds() throws ValidationException {
		Integer staffId = 1, facilityId = 2, motechId = 3;

		expect(registrarBean.isValidIdCheckDigit(staffId)).andReturn(true);
		expect(openmrsBean.getStaffBySystemId(staffId.toString())).andReturn(
				new User(1));
		expect(registrarBean.isValidIdCheckDigit(facilityId)).andReturn(true);
		expect(registrarBean.getFacilityById(facilityId)).andReturn(null);
		expect(registrarBean.isValidMotechIdCheckDigit(motechId)).andReturn(
				true);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(null);

		replay(registrarBean, patientModelConverter, openmrsBean);

		try {
			regWs.queryPatient(staffId, facilityId, motechId);

			fail("Expected ValidationException");
		} catch (ValidationException e) {
			assertEquals("Errors in Patient Query request", e.getMessage());
			assertNotNull("Validation Exception FaultBean is Null", e
					.getFaultInfo());
			List<String> errors = e.getFaultInfo().getErrors();
			assertNotNull("Validation Errors is Null", errors);
			assertEquals(2, errors.size());
			String facilityError = errors.get(0);
			assertEquals("FacilityID=not found", facilityError);
			String motechidError = errors.get(1);
			assertEquals("MotechID=not found", motechidError);
		}

		verify(registrarBean, patientModelConverter, openmrsBean);
	}

	@Test
	public void getPatientEnrollments() throws ValidationException {
		Integer motechId = 3;

		org.openmrs.Patient patient = new org.openmrs.Patient(1);
		String[] enrollments = { "Enrollment1", "Enrollment2" };

		expect(registrarBean.isValidMotechIdCheckDigit(motechId)).andReturn(
				true);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(patient);
		expect(registrarBean.getActiveMessageProgramEnrollmentNames(patient))
				.andReturn(enrollments);

		replay(registrarBean, patientModelConverter, openmrsBean);

		String[] result = regWs.getPatientEnrollments(motechId);

		verify(registrarBean, patientModelConverter, openmrsBean);

		assertArrayEquals(enrollments, result);
	}

	@Test
	public void getPatientEnrollmentsInvalidPatientId()
			throws ValidationException {
		Integer motechId = 3;

		expect(registrarBean.isValidMotechIdCheckDigit(motechId)).andReturn(
				true);
		expect(openmrsBean.getPatientByMotechId(motechId.toString()))
				.andReturn(null);

		replay(registrarBean, patientModelConverter, openmrsBean);

		try {
			regWs.getPatientEnrollments(motechId);

			fail("Expected ValidationException");
		} catch (ValidationException e) {
			assertEquals("Errors in Get Patient Enrollments request", e
					.getMessage());
			assertNotNull("Validation Exception FaultBean is Null", e
					.getFaultInfo());
			List<String> errors = e.getFaultInfo().getErrors();
			assertNotNull("Validation Errors is Null", errors);
			assertEquals(1, errors.size());
			String error = errors.get(0);
			assertEquals("MotechID=not found", error);
		}

		verify(registrarBean, patientModelConverter, openmrsBean);
	}

	@Test
	public void setMessageStatus() {
		String messageId = "12345678-1234-1234-1234-123456789012";
		Boolean success = true;

		registrarBean.setMessageStatus(messageId, success);

		replay(registrarBean);

		regWs.setMessageStatus(messageId, success);

		verify(registrarBean);
	}

	@Test
	public void registrarBeanProperty() throws SecurityException,
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
