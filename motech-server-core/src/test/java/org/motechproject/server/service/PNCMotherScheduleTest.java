package org.motechproject.server.service;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.Calendar;
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

public class PNCMotherScheduleTest extends TestCase {
	ApplicationContext ctx;

	RegistrarBean registrarBean;
	ExpectedEncounterSchedule pncSchedule;
	ExpectedCareEvent pnc1Event;
	ExpectedCareEvent pnc2Event;
	ExpectedCareEvent pnc3Event;

	@Override
	protected void setUp() throws Exception {
		ctx = new ClassPathXmlApplicationContext(new String[] {
				"test-common-program-beans.xml",
				"services/pregnancy-pnc-service.xml" });
		pncSchedule = (ExpectedEncounterSchedule) ctx
				.getBean("pregnancyPNCSchedule");
		pnc1Event = pncSchedule.getEvents().get(0);
		pnc2Event = pncSchedule.getEvents().get(1);
		pnc3Event = pncSchedule.getEvents().get(2);

		// EasyMock setup in Spring config
		registrarBean = (RegistrarBean) ctx.getBean("registrarBean");
	}

	@Override
	protected void tearDown() throws Exception {
		ctx = null;
		pncSchedule = null;
		pnc1Event = null;
		pnc2Event = null;
		pnc3Event = null;
		registrarBean = null;
	}

	public void testCreateExpected() {
		Date date = new Date();

		Patient patient = new Patient();

		Capture<Date> minDate1Capture = new Capture<Date>();
		Capture<Date> dueDate1Capture = new Capture<Date>();
		Capture<Date> lateDate1Capture = new Capture<Date>();
		Capture<Date> maxDate1Capture = new Capture<Date>();
		Capture<Date> minDate2Capture = new Capture<Date>();
		Capture<Date> dueDate2Capture = new Capture<Date>();
		Capture<Date> lateDate2Capture = new Capture<Date>();
		Capture<Date> maxDate2Capture = new Capture<Date>();
		Capture<Date> minDate3Capture = new Capture<Date>();
		Capture<Date> dueDate3Capture = new Capture<Date>();
		Capture<Date> lateDate3Capture = new Capture<Date>();
		Capture<Date> maxDate3Capture = new Capture<Date>();

		List<Encounter> encounterList = new ArrayList<Encounter>();
		List<ExpectedEncounter> expectedEncounterList = new ArrayList<ExpectedEncounter>();

		Date deliveryDate = new Date();

		expect(registrarBean.getCurrentDeliveryDate(patient)).andReturn(
				deliveryDate);
		expect(
				registrarBean.getEncounters(patient, pncSchedule
						.getEncounterTypeName(), deliveryDate)).andReturn(
				encounterList);
		expect(
				registrarBean.getExpectedEncounters(patient, pncSchedule
						.getName())).andReturn(expectedEncounterList);
		expect(
				registrarBean.createExpectedEncounter(eq(patient),
						eq(pncSchedule.getEncounterTypeName()),
						capture(minDate1Capture), capture(dueDate1Capture),
						capture(lateDate1Capture), capture(maxDate1Capture),
						eq(pnc1Event.getName()), eq(pncSchedule.getName())))
				.andReturn(new ExpectedEncounter());
		expect(
				registrarBean.createExpectedEncounter(eq(patient),
						eq(pncSchedule.getEncounterTypeName()),
						capture(minDate2Capture), capture(dueDate2Capture),
						capture(lateDate2Capture), capture(maxDate2Capture),
						eq(pnc2Event.getName()), eq(pncSchedule.getName())))
				.andReturn(new ExpectedEncounter());
		expect(
				registrarBean.createExpectedEncounter(eq(patient),
						eq(pncSchedule.getEncounterTypeName()),
						capture(minDate3Capture), capture(dueDate3Capture),
						capture(lateDate3Capture), capture(maxDate3Capture),
						eq(pnc3Event.getName()), eq(pncSchedule.getName())))
				.andReturn(new ExpectedEncounter());

		replay(registrarBean);

		pncSchedule.updateSchedule(patient, date);

		verify(registrarBean);

		Date minDate1 = minDate1Capture.getValue();
		Date dueDate1 = dueDate1Capture.getValue();
		Date lateDate1 = lateDate1Capture.getValue();
		Date maxDate1 = maxDate1Capture.getValue();

		assertNull("Min date is not null", minDate1);
		assertNotNull("Due date is null", dueDate1);
		assertNotNull("Late date is null", lateDate1);
		assertTrue("Late date is not after due date", lateDate1.after(dueDate1));
		assertNull("Max date is not null", maxDate1);

		Date minDate2 = minDate1Capture.getValue();
		Date dueDate2 = dueDate1Capture.getValue();
		Date lateDate2 = lateDate1Capture.getValue();
		Date maxDate2 = maxDate1Capture.getValue();

		assertNull("Min date is not null", minDate2);
		assertNotNull("Due date is null", dueDate2);
		assertNotNull("Late date is null", lateDate2);
		assertTrue("Late date is not after due date", lateDate2.after(dueDate2));
		assertNull("Max date is not null", maxDate2);

		Date minDate3 = minDate1Capture.getValue();
		Date dueDate3 = dueDate1Capture.getValue();
		Date lateDate3 = lateDate1Capture.getValue();
		Date maxDate3 = maxDate1Capture.getValue();

		assertNull("Min date is not null", minDate3);
		assertNotNull("Due date is null", dueDate3);
		assertNotNull("Late date is null", lateDate3);
		assertTrue("Late date is not after due date", lateDate3.after(dueDate3));
		assertNull("Max date is not null", maxDate3);
	}

	public void testSatisfyExpected() {
		Date date = new Date();

		Patient patient = new Patient();

		Capture<ExpectedEncounter> pnc1ExpectedCapture = new Capture<ExpectedEncounter>();
		Capture<ExpectedEncounter> pnc2ExpectedCapture = new Capture<ExpectedEncounter>();
		Capture<ExpectedEncounter> pnc3ExpectedCapture = new Capture<ExpectedEncounter>();

		List<Encounter> encounterList = new ArrayList<Encounter>();
		Encounter encounter1 = new Encounter();
		encounter1.setEncounterDatetime(date);
		encounterList.add(encounter1);
		Encounter encounter2 = new Encounter();
		encounter1.setEncounterDatetime(date);
		encounterList.add(encounter2);

		List<ExpectedEncounter> expectedEncounterList = new ArrayList<ExpectedEncounter>();
		ExpectedEncounter expectedEncounter1 = new ExpectedEncounter();
		expectedEncounter1.setName(pnc1Event.getName());
		expectedEncounterList.add(expectedEncounter1);
		ExpectedEncounter expectedEncounter2 = new ExpectedEncounter();
		expectedEncounter2.setName(pnc2Event.getName());
		expectedEncounterList.add(expectedEncounter2);
		ExpectedEncounter expectedEncounter3 = new ExpectedEncounter();
		expectedEncounter3.setName(pnc3Event.getName());
		expectedEncounterList.add(expectedEncounter3);

		Date deliveryDate = new Date();

		expect(registrarBean.getCurrentDeliveryDate(patient)).andReturn(
				deliveryDate);
		expect(
				registrarBean.getEncounters(patient, pncSchedule
						.getEncounterTypeName(), deliveryDate)).andReturn(
				encounterList);
		expect(
				registrarBean.getExpectedEncounters(patient, pncSchedule
						.getName())).andReturn(expectedEncounterList);
		expect(
				registrarBean
						.saveExpectedEncounter(capture(pnc1ExpectedCapture)))
				.andReturn(new ExpectedEncounter());
		expect(
				registrarBean
						.saveExpectedEncounter(capture(pnc2ExpectedCapture)))
				.andReturn(new ExpectedEncounter());
		expect(
				registrarBean
						.saveExpectedEncounter(capture(pnc3ExpectedCapture)))
				.andReturn(new ExpectedEncounter());

		replay(registrarBean);

		pncSchedule.updateSchedule(patient, date);

		verify(registrarBean);

		ExpectedEncounter capturedPNC1Expected = pnc1ExpectedCapture.getValue();
		assertEquals(Boolean.TRUE, capturedPNC1Expected.getVoided());
		assertEquals(encounter1, capturedPNC1Expected.getEncounter());

		ExpectedEncounter capturedPNC2Expected = pnc2ExpectedCapture.getValue();
		assertEquals(Boolean.TRUE, capturedPNC2Expected.getVoided());
		assertEquals(encounter2, capturedPNC2Expected.getEncounter());

		ExpectedEncounter capturedPNC3Expected = pnc3ExpectedCapture.getValue();
		assertEquals(Boolean.FALSE, capturedPNC3Expected.getVoided());
	}

	public void testRemoveExpected() {
		Date date = new Date();

		Patient patient = new Patient();

		List<ExpectedEncounter> expectedEncounterList = new ArrayList<ExpectedEncounter>();
		expectedEncounterList.add(new ExpectedEncounter());
		expectedEncounterList.add(new ExpectedEncounter());
		expectedEncounterList.add(new ExpectedEncounter());

		Capture<ExpectedEncounter> pnc1ExpectedCapture = new Capture<ExpectedEncounter>();
		Capture<ExpectedEncounter> pnc2ExpectedCapture = new Capture<ExpectedEncounter>();
		Capture<ExpectedEncounter> pnc3ExpectedCapture = new Capture<ExpectedEncounter>();

		Date deliveryDate = null;

		expect(registrarBean.getCurrentDeliveryDate(patient)).andReturn(
				deliveryDate);
		expect(
				registrarBean.getExpectedEncounters(patient, pncSchedule
						.getName())).andReturn(expectedEncounterList);
		expect(
				registrarBean
						.saveExpectedEncounter(capture(pnc1ExpectedCapture)))
				.andReturn(new ExpectedEncounter());
		expect(
				registrarBean
						.saveExpectedEncounter(capture(pnc2ExpectedCapture)))
				.andReturn(new ExpectedEncounter());
		expect(
				registrarBean
						.saveExpectedEncounter(capture(pnc3ExpectedCapture)))
				.andReturn(new ExpectedEncounter());

		replay(registrarBean);

		pncSchedule.updateSchedule(patient, date);

		verify(registrarBean);

		ExpectedEncounter capturedPNC1Expected = pnc1ExpectedCapture.getValue();
		assertEquals(Boolean.TRUE, capturedPNC1Expected.getVoided());

		ExpectedEncounter capturedPNC2Expected = pnc2ExpectedCapture.getValue();
		assertEquals(Boolean.TRUE, capturedPNC2Expected.getVoided());

		ExpectedEncounter capturedPNC3Expected = pnc3ExpectedCapture.getValue();
		assertEquals(Boolean.TRUE, capturedPNC3Expected.getVoided());
	}

	public void testNoActionNoDelivery() {
		Date date = new Date();

		Patient patient = new Patient();

		List<ExpectedEncounter> expectedEncounterList = new ArrayList<ExpectedEncounter>();

		Date deliveryDate = null;

		expect(registrarBean.getCurrentDeliveryDate(patient)).andReturn(
				deliveryDate);
		expect(
				registrarBean.getExpectedEncounters(patient, pncSchedule
						.getName())).andReturn(expectedEncounterList);

		replay(registrarBean);

		pncSchedule.updateSchedule(patient, date);

		verify(registrarBean);
	}

	public void testNoActionPastDelivery() {
		Date date = new Date();

		Patient patient = new Patient();

		List<ExpectedEncounter> expectedEncounterList = new ArrayList<ExpectedEncounter>();

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -2);

		Date deliveryDate = calendar.getTime();

		expect(registrarBean.getCurrentDeliveryDate(patient)).andReturn(
				deliveryDate);
		expect(
				registrarBean.getExpectedEncounters(patient, pncSchedule
						.getName())).andReturn(expectedEncounterList);

		replay(registrarBean);

		pncSchedule.updateSchedule(patient, date);

		verify(registrarBean);
	}
}
