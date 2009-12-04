package org.motech.event;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;

import org.motech.svc.RegistrarBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MessageProgramDateStateChangeTest extends TestCase {

	ApplicationContext ctx;

	Integer patientId;
	Date week1;
	Date week2;
	Date week3;
	Date week41;
	MessageProgram pregnancyProgram;
	MessageProgramState pregnancyState1;
	MessageProgramState pregnancyState2;
	MessageProgramState pregnancyState3;
	MessageProgramState pregnancyState4;
	MessageProgramState pregnancyState41;
	MessageProgramState currentPatientState;
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

		patientId = 1;

		ctx = new ClassPathXmlApplicationContext(new String[] {
				"test-common-program-beans.xml",
				"pregnancy-program-test-context.xml" });
		pregnancyProgram = (MessageProgram) ctx.getBean("pregnancyProgram");

		pregnancyState1 = (MessageProgramState) ctx.getBean("pregnancyState1");
		pregnancyState2 = (MessageProgramState) ctx.getBean("pregnancyState2");
		pregnancyState3 = (MessageProgramState) ctx.getBean("pregnancyState3");
		pregnancyState4 = (MessageProgramState) ctx.getBean("pregnancyState4");
		pregnancyState41 = (MessageProgramState) ctx
				.getBean("pregnancyState41");

		// EasyMock setup in Spring config
		registrarBean = (RegistrarBean) ctx.getBean("registrarBean");
	}

	@Override
	protected void tearDown() throws Exception {
		ctx = null;
		pregnancyProgram = null;
		pregnancyState1 = null;
		pregnancyState2 = null;
		pregnancyState3 = null;
		pregnancyState4 = null;
		pregnancyState41 = null;
		registrarBean = null;
	}

	public void testDetermineWeek1() {
		expect(
				registrarBean.getLastObsDate(patientId, pregnancyProgram
						.getConceptName(), pregnancyProgram.getConceptValue()))
				.andReturn(week1).atLeastOnce();

		replay(registrarBean);

		currentPatientState = pregnancyProgram.determineState(patientId);

		verify(registrarBean);

		assertEquals(currentPatientState.getName(), pregnancyState1.getName());
	}

	public void testDetermineWeek2() {
		expect(
				registrarBean.getLastObsDate(patientId, pregnancyProgram
						.getConceptName(), pregnancyProgram.getConceptValue()))
				.andReturn(week2).atLeastOnce();

		replay(registrarBean);

		currentPatientState = pregnancyProgram.determineState(patientId);

		verify(registrarBean);

		assertEquals(currentPatientState.getName(), pregnancyState2.getName());
	}

	public void testDetermineWeek3() {
		expect(
				registrarBean.getLastObsDate(patientId, pregnancyProgram
						.getConceptName(), pregnancyProgram.getConceptValue()))
				.andReturn(week3).atLeastOnce();

		replay(registrarBean);

		currentPatientState = pregnancyProgram.determineState(patientId);

		verify(registrarBean);

		assertEquals(currentPatientState.getName(), pregnancyState3.getName());
	}

	public void testDetermineEndState() {
		expect(
				registrarBean.getLastObsDate(patientId, pregnancyProgram
						.getConceptName(), pregnancyProgram.getConceptValue()))
				.andReturn(week41).atLeastOnce();

		replay(registrarBean);

		currentPatientState = pregnancyProgram.determineState(patientId);

		verify(registrarBean);

		assertEquals(currentPatientState.getName(), pregnancyState41.getName());
	}
}
