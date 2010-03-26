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

public class OPVScheduleTest extends TestCase {

	ApplicationContext ctx;

	RegistrarBean registrarBean;
	ExpectedObsSchedule opvSchedule;
	ExpectedCareEvent opv0Event;
	ExpectedCareEvent opv1Event;

	@Override
	protected void setUp() throws Exception {
		ctx = new ClassPathXmlApplicationContext(new String[] {
				"test-common-program-beans.xml",
				"services/child-opv-service.xml" });
		opvSchedule = (ExpectedObsSchedule) ctx.getBean("childOPVSchedule");
		opv0Event = opvSchedule.getEvents().get(0);
		opv1Event = opvSchedule.getEvents().get(1);

		// EasyMock setup in Spring config
		registrarBean = (RegistrarBean) ctx.getBean("registrarBean");
	}

	@Override
	protected void tearDown() throws Exception {
		ctx = null;
		opvSchedule = null;
		opv0Event = null;
		opv1Event = null;
		registrarBean = null;
	}

	public void testSkipExpired() {
		Date date = new Date();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, -28); // age is 4 weeks

		Patient patient = new Patient();
		patient.setBirthdate(calendar.getTime());

		Capture<Date> minDateCapture = new Capture<Date>();
		Capture<Date> dueDateCapture = new Capture<Date>();
		Capture<Date> lateDateCapture = new Capture<Date>();
		Capture<Date> maxDateCapture = new Capture<Date>();

		List<Obs> obsList = new ArrayList<Obs>();
		List<ExpectedObs> expectedObsList = new ArrayList<ExpectedObs>();

		expect(
				registrarBean.getObs(patient, opvSchedule.getConceptName(),
						opvSchedule.getValueConceptName(), patient
								.getBirthdate())).andReturn(obsList);
		expect(registrarBean.getExpectedObs(patient, opvSchedule.getName()))
				.andReturn(expectedObsList);
		expect(
				registrarBean.createExpectedObs(eq(patient), eq(opvSchedule
						.getConceptName()), eq(opvSchedule
						.getValueConceptName()), eq(opv1Event.getNumber()),
						capture(minDateCapture), capture(dueDateCapture),
						capture(lateDateCapture), capture(maxDateCapture),
						eq(opv1Event.getName()), eq(opvSchedule.getName())))
				.andReturn(new ExpectedObs());

		replay(registrarBean);

		opvSchedule.updateSchedule(patient, date);

		verify(registrarBean);

		Date dueDate = dueDateCapture.getValue();
		Date lateDate = lateDateCapture.getValue();

		assertNotNull("Due date is null", dueDate);
		assertNotNull("Late date is null", lateDate);
		assertTrue("Late date is not after due date", lateDate.after(dueDate));

	}

	public void testRemoveExpired() {
		Date date = new Date();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, -28); // age is 4 weeks

		Patient patient = new Patient();
		patient.setBirthdate(calendar.getTime());

		Capture<ExpectedObs> expectedObs0Capture = new Capture<ExpectedObs>();
		Capture<ExpectedObs> expectedObs1Capture = new Capture<ExpectedObs>();

		List<Obs> obsList = new ArrayList<Obs>();

		List<ExpectedObs> expectedObsList = new ArrayList<ExpectedObs>();
		ExpectedObs expectedObs0 = new ExpectedObs();
		expectedObs0.setName(opv0Event.getName());
		expectedObsList.add(expectedObs0);
		ExpectedObs expectedObs1 = new ExpectedObs();
		expectedObs1.setName(opv1Event.getName());
		expectedObsList.add(expectedObs1);

		expect(
				registrarBean.getObs(patient, opvSchedule.getConceptName(),
						opvSchedule.getValueConceptName(), patient
								.getBirthdate())).andReturn(obsList);
		expect(registrarBean.getExpectedObs(patient, opvSchedule.getName()))
				.andReturn(expectedObsList);
		expect(registrarBean.saveExpectedObs(capture(expectedObs0Capture)))
				.andReturn(new ExpectedObs());
		expect(registrarBean.saveExpectedObs(capture(expectedObs1Capture)))
				.andReturn(new ExpectedObs());

		replay(registrarBean);

		opvSchedule.updateSchedule(patient, date);

		verify(registrarBean);

		ExpectedObs capturedExpectedObs0 = expectedObs0Capture.getValue();
		assertEquals(opv0Event.getName(), capturedExpectedObs0.getName());
		assertEquals(Boolean.TRUE, capturedExpectedObs0.getVoided());

		ExpectedObs capturedExpectedObs1 = expectedObs1Capture.getValue();
		assertEquals(opv1Event.getName(), capturedExpectedObs1.getName());
		assertNotNull("Expected Obs due date is null", capturedExpectedObs1
				.getDueObsDatetime());
		assertNotNull("Expected Obs due date is null", capturedExpectedObs1
				.getLateObsDatetime());
		assertEquals(Boolean.FALSE, capturedExpectedObs1.getVoided());
	}

}
