package org.motech.event;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;

import org.motech.svc.RegistrarBean;
import org.openmrs.Patient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RegimenDateStateChangeTest extends TestCase {

	ApplicationContext ctx;

	Patient patient;
	Date week1;
	Date week2;
	Date week3;
	Date week41;
	Regimen pregnancyRegimen;
	RegimenState pregnancyState1;
	RegimenState pregnancyState2;
	RegimenState pregnancyState3;
	RegimenState pregnancyState4;
	RegimenState pregnancyState41;
	RegimenState currentPatientState;
	RegistrarBean registrarBean;

	@Override
	protected void setUp() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		week1 = calendar.getTime();
		calendar.add(Calendar.DATE, -2 * 7 + 1); // almost 2 weeks in past
		week2 = calendar.getTime();
		calendar.add(Calendar.DATE, -2); // over 2 weeks in past
		week3 = calendar.getTime();

		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, -41 * 7 - 1); // over 41 weeks in past
		week41 = calendar.getTime();

		patient = new Patient();

		ctx = new ClassPathXmlApplicationContext(new String[] {
				"test-common-regimen-beans.xml",
				"pregnancy-regimen-test-context.xml" });
		pregnancyRegimen = (Regimen) ctx.getBean("pregnancyRegimen");

		pregnancyState1 = (RegimenState) ctx.getBean("pregnancyState1");
		pregnancyState2 = (RegimenState) ctx.getBean("pregnancyState2");
		pregnancyState3 = (RegimenState) ctx.getBean("pregnancyState3");
		pregnancyState4 = (RegimenState) ctx.getBean("pregnancyState4");
		pregnancyState41 = (RegimenState) ctx.getBean("pregnancyState41");

		// EasyMock setup in Spring config
		registrarBean = (RegistrarBean) ctx.getBean("registrarBean");
	}

	@Override
	protected void tearDown() throws Exception {
		ctx = null;
		pregnancyRegimen = null;
		pregnancyState1 = null;
		pregnancyState2 = null;
		pregnancyState3 = null;
		pregnancyState4 = null;
		pregnancyState41 = null;
		registrarBean = null;
	}

	public void testDetermineWeek1() {
		expect(
				registrarBean.getLastObsDate(patient, pregnancyRegimen
						.getConceptName(), pregnancyRegimen.getConceptValue()))
				.andReturn(week1).atLeastOnce();

		replay(registrarBean);

		currentPatientState = pregnancyRegimen.determineState(patient);

		verify(registrarBean);

		assertEquals(currentPatientState.getName(), pregnancyState1.getName());
	}

	public void testDetermineWeek2() {
		expect(
				registrarBean.getLastObsDate(patient, pregnancyRegimen
						.getConceptName(), pregnancyRegimen.getConceptValue()))
				.andReturn(week2).atLeastOnce();

		replay(registrarBean);

		currentPatientState = pregnancyRegimen.determineState(patient);

		verify(registrarBean);

		assertEquals(currentPatientState.getName(), pregnancyState2.getName());
	}

	public void testDetermineWeek3() {
		expect(
				registrarBean.getLastObsDate(patient, pregnancyRegimen
						.getConceptName(), pregnancyRegimen.getConceptValue()))
				.andReturn(week3).atLeastOnce();

		replay(registrarBean);

		currentPatientState = pregnancyRegimen.determineState(patient);

		verify(registrarBean);

		assertEquals(currentPatientState.getName(), pregnancyState3.getName());
	}

	public void testDetermineEndState() {
		expect(
				registrarBean.getLastObsDate(patient, pregnancyRegimen
						.getConceptName(), pregnancyRegimen.getConceptValue()))
				.andReturn(week41).atLeastOnce();

		replay(registrarBean);

		currentPatientState = pregnancyRegimen.determineState(patient);

		verify(registrarBean);

		assertEquals(currentPatientState.getName(), pregnancyState41.getName());
	}
}
