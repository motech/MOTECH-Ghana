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

public class BCGScheduleTest extends TestCase {

	ApplicationContext ctx;

	RegistrarBean registrarBean;
	ExpectedObsSchedule bcgSchedule;
	ExpectedCareEvent bcgEvent;

	@Override
	protected void setUp() throws Exception {
		ctx = new ClassPathXmlApplicationContext(new String[] {
				"test-common-program-beans.xml",
				"services/child-bcg-service.xml" });
		bcgSchedule = (ExpectedObsSchedule) ctx.getBean("childBCGSchedule");
		bcgEvent = bcgSchedule.getEvents().get(0);

		// EasyMock setup in Spring config
		registrarBean = (RegistrarBean) ctx.getBean("registrarBean");
	}

	@Override
	protected void tearDown() throws Exception {
		ctx = null;
		bcgSchedule = null;
		bcgEvent = null;
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
				registrarBean.getObs(patient, bcgSchedule.getConceptName(),
						bcgSchedule.getValueConceptName(), patient
								.getBirthdate())).andReturn(obsList);
		expect(registrarBean.getExpectedObs(patient, bcgSchedule.getName()))
				.andReturn(expectedObsList);
		expect(
				registrarBean.createExpectedObs(eq(patient), eq(bcgSchedule
						.getConceptName()), eq(bcgSchedule
						.getValueConceptName()), eq(bcgEvent.getNumber()),
						capture(minDateCapture), capture(dueDateCapture),
						capture(lateDateCapture), capture(maxDateCapture),
						eq(bcgEvent.getName()), eq(bcgSchedule.getName())))
				.andReturn(new ExpectedObs());

		replay(registrarBean);

		bcgSchedule.updateSchedule(patient, date);

		verify(registrarBean);

		Date minDate = minDateCapture.getValue();
		Date dueDate = dueDateCapture.getValue();
		Date lateDate = lateDateCapture.getValue();
		Date maxDate = maxDateCapture.getValue();

		assertNotNull("Min date is null", minDate);
		assertNotNull("Due date is null", dueDate);
		assertNotNull("Late date is null", lateDate);
		assertNotNull("Max date is null", maxDate);

		assertEquals("Due date not equal min date", minDate, dueDate);
		assertTrue("Late date is not after due date", lateDate.after(dueDate));
		assertTrue("Max date is not after late date", maxDate.after(lateDate));
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
		expectedObs.setName(bcgEvent.getName());
		expectedObsList.add(expectedObs);

		expect(
				registrarBean.getObs(patient, bcgSchedule.getConceptName(),
						bcgSchedule.getValueConceptName(), patient
								.getBirthdate())).andReturn(obsList);
		expect(registrarBean.getExpectedObs(patient, bcgSchedule.getName()))
				.andReturn(expectedObsList);
		expect(registrarBean.saveExpectedObs(capture(expectedObsCapture)))
				.andReturn(new ExpectedObs());

		replay(registrarBean);

		bcgSchedule.updateSchedule(patient, date);

		verify(registrarBean);

		ExpectedObs capturedExpectedObs = expectedObsCapture.getValue();

		assertNotNull("Expected Obs min date is null", capturedExpectedObs
				.getMinObsDatetime());
		assertNotNull("Expected Obs due date is null", capturedExpectedObs
				.getDueObsDatetime());
		assertNotNull("Expected Obs late date is null", capturedExpectedObs
				.getLateObsDatetime());
		assertNotNull("Expected Obs max date is null", capturedExpectedObs
				.getMaxObsDatetime());
		assertEquals(Boolean.FALSE, capturedExpectedObs.getVoided());
	}

	public void testSatisfyExpected() {
		Date date = new Date();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, -2); // age is 2 months

		Patient patient = new Patient();
		patient.setBirthdate(calendar.getTime());

		Capture<ExpectedObs> expectedObsCapture = new Capture<ExpectedObs>();

		List<Obs> obsList = new ArrayList<Obs>();
		Obs obs = new Obs();
		obs.setObsDatetime(date);
		obsList.add(obs);

		List<ExpectedObs> expectedObsList = new ArrayList<ExpectedObs>();
		ExpectedObs expectedObs = new ExpectedObs();
		expectedObs.setName(bcgEvent.getName());
		expectedObsList.add(expectedObs);

		expect(
				registrarBean.getObs(patient, bcgSchedule.getConceptName(),
						bcgSchedule.getValueConceptName(), patient
								.getBirthdate())).andReturn(obsList);
		expect(registrarBean.getExpectedObs(patient, bcgSchedule.getName()))
				.andReturn(expectedObsList);
		expect(registrarBean.saveExpectedObs(capture(expectedObsCapture)))
				.andReturn(new ExpectedObs());

		replay(registrarBean);

		bcgSchedule.updateSchedule(patient, date);

		verify(registrarBean);

		ExpectedObs capturedExpectedObs = expectedObsCapture.getValue();

		assertEquals(Boolean.TRUE, capturedExpectedObs.getVoided());
		assertEquals(obs, capturedExpectedObs.getObs());
	}

	public void testRemoveExpected() {
		Date date = new Date();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.YEAR, -2); // age is 2 years

		Patient patient = new Patient();
		patient.setBirthdate(calendar.getTime());

		Capture<ExpectedObs> expectedObsCapture = new Capture<ExpectedObs>();

		List<Obs> obsList = new ArrayList<Obs>();
		Obs obs = new Obs();
		obs.setObsDatetime(date);
		obsList.add(obs);

		List<ExpectedObs> expectedObsList = new ArrayList<ExpectedObs>();
		ExpectedObs expectedObs = new ExpectedObs();
		expectedObs.setName(bcgEvent.getName());
		expectedObsList.add(expectedObs);

		expect(registrarBean.getExpectedObs(patient, bcgSchedule.getName()))
				.andReturn(expectedObsList);
		expect(registrarBean.saveExpectedObs(capture(expectedObsCapture)))
				.andReturn(new ExpectedObs());

		replay(registrarBean);

		bcgSchedule.updateSchedule(patient, date);

		verify(registrarBean);

		ExpectedObs capturedExpectedObs = expectedObsCapture.getValue();

		assertEquals(Boolean.TRUE, capturedExpectedObs.getVoided());
	}

	public void testNoAction() {
		Date date = new Date();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.YEAR, -2); // age is 2 years

		Patient patient = new Patient();
		patient.setBirthdate(calendar.getTime());

		List<Obs> obsList = new ArrayList<Obs>();
		Obs obs = new Obs();
		obs.setObsDatetime(date);
		obsList.add(obs);

		List<ExpectedObs> expectedObsList = new ArrayList<ExpectedObs>();

		expect(registrarBean.getExpectedObs(patient, bcgSchedule.getName()))
				.andReturn(expectedObsList);

		replay(registrarBean);

		bcgSchedule.updateSchedule(patient, date);

		verify(registrarBean);
	}

}
