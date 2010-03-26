package org.motechproject.server.service;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.easymock.Capture;
import org.motechproject.server.model.ExpectedEncounter;
import org.motechproject.server.service.impl.ExpectedEncounterSchedule;
import org.motechproject.server.svc.RegistrarBean;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ANCScheduleTest extends TestCase {
	ApplicationContext ctx;

	RegistrarBean registrarBean;
	ExpectedEncounterSchedule ancSchedule;

	@Override
	protected void setUp() throws Exception {
		ctx = new ClassPathXmlApplicationContext(new String[] {
				"test-common-program-beans.xml",
				"services/pregnancy-anc-service.xml" });
		ancSchedule = (ExpectedEncounterSchedule) ctx
				.getBean("pregnancyANCSchedule");

		// EasyMock setup in Spring config
		registrarBean = (RegistrarBean) ctx.getBean("registrarBean");
	}

	@Override
	protected void tearDown() throws Exception {
		ctx = null;
		ancSchedule = null;
		registrarBean = null;
	}

	public void testSatisfyExpected() {
		Date date = new Date();

		Patient patient = new Patient(1);

		Capture<ExpectedEncounter> anc1ExpectedCapture = new Capture<ExpectedEncounter>();
		Capture<ExpectedEncounter> anc2ExpectedCapture = new Capture<ExpectedEncounter>();

		List<Encounter> encounterList = new ArrayList<Encounter>();
		Encounter encounter1 = new Encounter();
		encounter1.setEncounterDatetime(date);
		encounterList.add(encounter1);
		Encounter encounter2 = new Encounter();
		encounter1.setEncounterDatetime(date);
		encounterList.add(encounter2);

		List<ExpectedEncounter> expectedEncounterList = new ArrayList<ExpectedEncounter>();
		ExpectedEncounter expectedEncounter1 = new ExpectedEncounter();
		expectedEncounter1.setName("ANC1");
		expectedEncounterList.add(expectedEncounter1);
		ExpectedEncounter expectedEncounter2 = new ExpectedEncounter();
		expectedEncounter2.setName("ANC2");
		expectedEncounterList.add(expectedEncounter2);
		ExpectedEncounter expectedEncounter3 = new ExpectedEncounter();
		expectedEncounter3.setName("ANC3");
		expectedEncounterList.add(expectedEncounter3);

		Date pregnancyDate = new Date();

		expect(registrarBean.getActivePregnancyDueDate(patient.getPatientId()))
				.andReturn(pregnancyDate);
		expect(
				registrarBean.getEncounters(eq(patient), eq(ancSchedule
						.getEncounterTypeName()), (Date) anyObject()))
				.andReturn(encounterList);
		expect(
				registrarBean.getExpectedEncounters(patient, ancSchedule
						.getName())).andReturn(expectedEncounterList);
		expect(
				registrarBean
						.saveExpectedEncounter(capture(anc1ExpectedCapture)))
				.andReturn(new ExpectedEncounter());
		expect(
				registrarBean
						.saveExpectedEncounter(capture(anc2ExpectedCapture)))
				.andReturn(new ExpectedEncounter());

		replay(registrarBean);

		ancSchedule.updateSchedule(patient, date);

		verify(registrarBean);

		ExpectedEncounter capturedANC1Expected = anc1ExpectedCapture.getValue();
		assertEquals(Boolean.TRUE, capturedANC1Expected.getVoided());
		assertEquals(encounter1, capturedANC1Expected.getEncounter());

		ExpectedEncounter capturedANC2Expected = anc2ExpectedCapture.getValue();
		assertEquals(Boolean.TRUE, capturedANC2Expected.getVoided());
		assertEquals(encounter2, capturedANC2Expected.getEncounter());
	}

	public void testRemoveExpected() {
		Date date = new Date();

		Patient patient = new Patient();

		List<ExpectedEncounter> expectedEncounterList = new ArrayList<ExpectedEncounter>();
		expectedEncounterList.add(new ExpectedEncounter());
		expectedEncounterList.add(new ExpectedEncounter());
		expectedEncounterList.add(new ExpectedEncounter());

		Capture<ExpectedEncounter> anc1ExpectedCapture = new Capture<ExpectedEncounter>();
		Capture<ExpectedEncounter> anc2ExpectedCapture = new Capture<ExpectedEncounter>();
		Capture<ExpectedEncounter> anc3ExpectedCapture = new Capture<ExpectedEncounter>();

		Date pregnancyDate = null;

		expect(registrarBean.getActivePregnancyDueDate(patient.getPatientId()))
				.andReturn(pregnancyDate);
		expect(
				registrarBean.getExpectedEncounters(patient, ancSchedule
						.getName())).andReturn(expectedEncounterList);
		expect(
				registrarBean
						.saveExpectedEncounter(capture(anc1ExpectedCapture)))
				.andReturn(new ExpectedEncounter());
		expect(
				registrarBean
						.saveExpectedEncounter(capture(anc2ExpectedCapture)))
				.andReturn(new ExpectedEncounter());
		expect(
				registrarBean
						.saveExpectedEncounter(capture(anc3ExpectedCapture)))
				.andReturn(new ExpectedEncounter());

		replay(registrarBean);

		ancSchedule.updateSchedule(patient, date);

		verify(registrarBean);

		ExpectedEncounter capturedANC1Expected = anc1ExpectedCapture.getValue();
		assertEquals(Boolean.TRUE, capturedANC1Expected.getVoided());

		ExpectedEncounter capturedANC2Expected = anc2ExpectedCapture.getValue();
		assertEquals(Boolean.TRUE, capturedANC2Expected.getVoided());

		ExpectedEncounter capturedANC3Expected = anc3ExpectedCapture.getValue();
		assertEquals(Boolean.TRUE, capturedANC3Expected.getVoided());
	}

	public void testNoAction() {
		Date date = new Date();

		Patient patient = new Patient();

		List<ExpectedEncounter> expectedEncounterList = new ArrayList<ExpectedEncounter>();

		Date pregnancyDate = null;

		expect(registrarBean.getActivePregnancyDueDate(patient.getPatientId()))
				.andReturn(pregnancyDate);
		expect(
				registrarBean.getExpectedEncounters(patient, ancSchedule
						.getName())).andReturn(expectedEncounterList);

		replay(registrarBean);

		ancSchedule.updateSchedule(patient, date);

		verify(registrarBean);
	}
}
