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
import org.motechproject.server.model.ExpectedObs;
import org.motechproject.server.service.impl.ExpectedObsSchedule;
import org.motechproject.server.svc.RegistrarBean;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class PentaScheduleTest extends TestCase {

	ApplicationContext ctx;

	RegistrarBean registrarBean;
	ExpectedObsSchedule pentaSchedule;
	ExpectedCareEvent penta1Event;
	ExpectedCareEvent penta2Event;
	ExpectedCareEvent penta3Event;

	@Override
	protected void setUp() throws Exception {
		ctx = new ClassPathXmlApplicationContext(new String[] {
				"test-common-program-beans.xml",
				"services/child-penta-service.xml" });
		pentaSchedule = (ExpectedObsSchedule) ctx.getBean("childPentaSchedule");
		penta1Event = pentaSchedule.getEvents().get(0);
		penta2Event = pentaSchedule.getEvents().get(1);
		penta3Event = pentaSchedule.getEvents().get(2);

		// EasyMock setup in Spring config
		registrarBean = (RegistrarBean) ctx.getBean("registrarBean");
	}

	@Override
	protected void tearDown() throws Exception {
		ctx = null;
		pentaSchedule = null;
		penta1Event = null;
		penta2Event = null;
		penta3Event = null;
		registrarBean = null;
	}

	public void testCreateExpected() {
		Date date = new Date();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, -2); // age is 2 months

		Patient patient = new Patient();
		patient.setBirthdate(calendar.getTime());

		Capture<Date> minDateCapture = new Capture<Date>();
		Capture<Date> dueDateCapture = new Capture<Date>();
		Capture<Date> lateDateCapture = new Capture<Date>();
		Capture<Date> maxDateCapture = new Capture<Date>();

		List<Obs> obsList = new ArrayList<Obs>();
		List<ExpectedObs> expectedObsList = new ArrayList<ExpectedObs>();

		expect(
				registrarBean.getObs(patient, pentaSchedule.getConceptName(),
						pentaSchedule.getValueConceptName(), patient
								.getBirthdate())).andReturn(obsList);
		expect(registrarBean.getExpectedObs(patient, pentaSchedule.getName()))
				.andReturn(expectedObsList);
		expect(
				registrarBean.createExpectedObs(eq(patient), eq(pentaSchedule
						.getConceptName()), eq(pentaSchedule
						.getValueConceptName()), eq(penta1Event.getNumber()),
						capture(minDateCapture), capture(dueDateCapture),
						capture(lateDateCapture), capture(maxDateCapture),
						eq(penta1Event.getName()), eq(pentaSchedule.getName())))
				.andReturn(new ExpectedObs());

		replay(registrarBean);

		pentaSchedule.updateSchedule(patient, date);

		verify(registrarBean);

		Date dueDate = dueDateCapture.getValue();
		Date lateDate = lateDateCapture.getValue();

		assertNotNull("Due date is null", dueDate);
		assertNotNull("Late date is null", lateDate);
		assertTrue("Late date is not after due date", lateDate.after(dueDate));
	}

	public void testUpdateExpected() {
		Date date = new Date();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, -2); // age is 2 months

		Patient patient = new Patient();
		patient.setBirthdate(calendar.getTime());

		Capture<ExpectedObs> expectedObsCapture = new Capture<ExpectedObs>();

		List<Obs> obsList = new ArrayList<Obs>();

		List<ExpectedObs> expectedObsList = new ArrayList<ExpectedObs>();
		ExpectedObs expectedObs = new ExpectedObs();
		expectedObs.setName(penta1Event.getName());
		expectedObsList.add(expectedObs);

		expect(
				registrarBean.getObs(patient, pentaSchedule.getConceptName(),
						pentaSchedule.getValueConceptName(), patient
								.getBirthdate())).andReturn(obsList);
		expect(registrarBean.getExpectedObs(patient, pentaSchedule.getName()))
				.andReturn(expectedObsList);
		expect(registrarBean.saveExpectedObs(capture(expectedObsCapture)))
				.andReturn(new ExpectedObs());

		replay(registrarBean);

		pentaSchedule.updateSchedule(patient, date);

		verify(registrarBean);

		ExpectedObs capturedExpectedObs = expectedObsCapture.getValue();

		assertNotNull("Expected Obs due date is null", capturedExpectedObs
				.getDueObsDatetime());
		assertNotNull("Expected Obs late date is null", capturedExpectedObs
				.getLateObsDatetime());
		assertEquals(Boolean.FALSE, capturedExpectedObs.getVoided());
	}

	public void testSatisfyExpected() {
		Date date = new Date();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, -2); // age is 2 months

		Patient patient = new Patient();
		patient.setBirthdate(calendar.getTime());

		Capture<Date> minDateCapture = new Capture<Date>();
		Capture<Date> dueDateCapture = new Capture<Date>();
		Capture<Date> lateDateCapture = new Capture<Date>();
		Capture<Date> maxDateCapture = new Capture<Date>();
		Capture<ExpectedObs> expectedObsCapture = new Capture<ExpectedObs>();

		List<Obs> obsList = new ArrayList<Obs>();
		Obs obs = new Obs();
		obs.setObsDatetime(date);
		obs.setValueNumeric(new Double(1));
		obsList.add(obs);

		List<ExpectedObs> expectedObsList = new ArrayList<ExpectedObs>();
		ExpectedObs expectedObs = new ExpectedObs();
		expectedObs.setName(penta1Event.getName());
		expectedObsList.add(expectedObs);

		expect(
				registrarBean.getObs(patient, pentaSchedule.getConceptName(),
						pentaSchedule.getValueConceptName(), patient
								.getBirthdate())).andReturn(obsList);
		expect(registrarBean.getExpectedObs(patient, pentaSchedule.getName()))
				.andReturn(expectedObsList);
		expect(registrarBean.saveExpectedObs(capture(expectedObsCapture)))
				.andReturn(new ExpectedObs());
		expect(
				registrarBean.createExpectedObs(eq(patient), eq(pentaSchedule
						.getConceptName()), eq(pentaSchedule
						.getValueConceptName()), eq(penta2Event.getNumber()),
						capture(minDateCapture), capture(dueDateCapture),
						capture(lateDateCapture), capture(maxDateCapture),
						eq(penta2Event.getName()), eq(pentaSchedule.getName())))
				.andReturn(new ExpectedObs());

		replay(registrarBean);

		pentaSchedule.updateSchedule(patient, date);

		verify(registrarBean);

		ExpectedObs capturedExpectedObs = expectedObsCapture.getValue();

		assertEquals(Boolean.TRUE, capturedExpectedObs.getVoided());
		assertEquals(obs, capturedExpectedObs.getObs());

		Date dueDate = dueDateCapture.getValue();
		Date lateDate = lateDateCapture.getValue();

		assertNotNull("Due date is null", dueDate);
		assertNotNull("Late date is null", lateDate);
		assertTrue("Late date is not after due date", lateDate.after(dueDate));
	}

	public void testRemoveExpected() {
		Date date = new Date();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.YEAR, -6); // age is 6 years

		Patient patient = new Patient();
		patient.setBirthdate(calendar.getTime());

		Capture<ExpectedObs> expectedObsCapture = new Capture<ExpectedObs>();

		List<Obs> obsList = new ArrayList<Obs>();
		Obs obs = new Obs();
		obs.setObsDatetime(date);
		obsList.add(obs);

		List<ExpectedObs> expectedObsList = new ArrayList<ExpectedObs>();
		ExpectedObs expectedObs = new ExpectedObs();
		expectedObs.setName(penta1Event.getName());
		expectedObsList.add(expectedObs);

		expect(registrarBean.getExpectedObs(patient, pentaSchedule.getName()))
				.andReturn(expectedObsList);
		expect(registrarBean.saveExpectedObs(capture(expectedObsCapture)))
				.andReturn(new ExpectedObs());

		replay(registrarBean);

		pentaSchedule.updateSchedule(patient, date);

		verify(registrarBean);

		ExpectedObs capturedExpectedObs = expectedObsCapture.getValue();

		assertEquals(Boolean.TRUE, capturedExpectedObs.getVoided());
	}

	public void testNoAction() {
		Date date = new Date();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.YEAR, -6); // age is 6 years

		Patient patient = new Patient();
		patient.setBirthdate(calendar.getTime());

		List<Obs> obsList = new ArrayList<Obs>();
		Obs obs = new Obs();
		obs.setObsDatetime(date);
		obsList.add(obs);

		List<ExpectedObs> expectedObsList = new ArrayList<ExpectedObs>();

		expect(registrarBean.getExpectedObs(patient, pentaSchedule.getName()))
				.andReturn(expectedObsList);

		replay(registrarBean);

		pentaSchedule.updateSchedule(patient, date);

		verify(registrarBean);
	}

}
