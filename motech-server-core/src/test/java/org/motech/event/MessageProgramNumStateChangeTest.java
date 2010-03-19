package org.motech.event;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;

import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;

import org.motech.model.MessageProgramEnrollment;
import org.motech.svc.RegistrarBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MessageProgramNumStateChangeTest extends TestCase {

	ApplicationContext ctx;

	Integer patientId;
	MessageProgramEnrollment enrollment;
	Date obs1;
	Date obs2;
	Date obs3;
	Date obs4;
	Date obs5;
	RegistrarBean registrarBean;
	MessageProgram polioProgram;
	MessageProgramState polioState1;
	MessageProgramState polioState2;
	MessageProgramState polioState3;
	MessageProgramState polioState4;
	MessageProgramState polioState5;
	MessageProgramState currentPatientState;

	@Override
	protected void setUp() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, -1); // 1 month in past

		obs1 = calendar.getTime();

		calendar.add(Calendar.DATE, 6 * 7 + 1); // 6 weeks and 1 day
		obs2 = calendar.getTime();

		calendar.add(Calendar.DATE, 4 * 7 + 2); // 4 weeks and 2 days
		obs3 = calendar.getTime();

		calendar.add(Calendar.DATE, 4 * 7 + 3); // 4 weeks and 3 days
		obs4 = calendar.getTime();

		calendar.add(Calendar.DATE, 4 * 7 + 4); // 4 weeks and 4 days
		obs5 = calendar.getTime();

		patientId = 1;

		enrollment = new MessageProgramEnrollment();
		enrollment.setPersonId(patientId);

		ctx = new ClassPathXmlApplicationContext(new String[] {
				"test-common-program-beans.xml",
				"polio-program-test-context.xml" });
		polioProgram = (MessageProgram) ctx.getBean("polioProgram");

		polioState1 = (MessageProgramState) ctx.getBean("polioState1");
		polioState2 = (MessageProgramState) ctx.getBean("polioState2");
		polioState3 = (MessageProgramState) ctx.getBean("polioState3");
		polioState4 = (MessageProgramState) ctx.getBean("polioState4");
		polioState5 = (MessageProgramState) ctx.getBean("polioState5");

		// EasyMock setup in Spring config
		registrarBean = (RegistrarBean) ctx.getBean("registrarBean");
	}

	@Override
	protected void tearDown() throws Exception {
		enrollment = null;
		ctx = null;
		polioProgram = null;
		polioState1 = null;
		polioState2 = null;
		polioState3 = null;
		polioState4 = null;
		polioState5 = null;
		registrarBean = null;
	}

	public void testDetermineStartState() {
		expect(
				registrarBean.getNumberOfObs(patientId, polioProgram
						.getConceptName(), polioProgram.getConceptValue()))
				.andReturn(0).atLeastOnce();
		expect(registrarBean.getPatientBirthDate(patientId)).andReturn(
				new Date());

		replay(registrarBean);

		currentPatientState = polioProgram.determineState(enrollment);

		verify(registrarBean);

		assertEquals(currentPatientState.getName(), polioState1.getName());
	}

	public void testDetermineSecondState() {
		expect(
				registrarBean.getNumberOfObs(patientId, polioProgram
						.getConceptName(), polioProgram.getConceptValue()))
				.andReturn(1).atLeastOnce();
		expect(
				registrarBean.getLastObsDate(patientId, polioProgram
						.getConceptName(), polioProgram.getConceptValue()))
				.andReturn(new Date());

		replay(registrarBean);

		currentPatientState = polioProgram.determineState(enrollment);

		verify(registrarBean);

		assertEquals(currentPatientState.getName(), polioState2.getName());
	}

	public void testDetermineEndState() {
		expect(
				registrarBean.getNumberOfObs(patientId, polioProgram
						.getConceptName(), polioProgram.getConceptValue()))
				.andReturn(4).atLeastOnce();

		replay(registrarBean);

		currentPatientState = polioProgram.determineState(enrollment);

		verify(registrarBean);

		assertEquals(currentPatientState.getName(), polioState5.getName());
	}

	public void testMoveState() {
		expect(
				registrarBean.getNumberOfObs(patientId, polioProgram
						.getConceptName(), polioProgram.getConceptValue()))
				.andReturn(2).atLeastOnce();
		expect(
				registrarBean.getLastObsDate(patientId, polioProgram
						.getConceptName(), polioProgram.getConceptValue()))
				.andReturn(new Date());

		replay(registrarBean);

		currentPatientState = polioProgram.determineState(enrollment);

		verify(registrarBean);

		assertEquals(currentPatientState.getName(), polioState3.getName());

		// State will change with the number of Obs increasing
		reset(registrarBean);

		expect(
				registrarBean.getNumberOfObs(patientId, polioProgram
						.getConceptName(), polioProgram.getConceptValue()))
				.andReturn(3).atLeastOnce();
		expect(
				registrarBean.getLastObsDate(patientId, polioProgram
						.getConceptName(), polioProgram.getConceptValue()))
				.andReturn(new Date());

		replay(registrarBean);

		currentPatientState = polioProgram.updateState(enrollment);

		verify(registrarBean);

		assertEquals(currentPatientState.getName(), polioState4.getName());
	}

	public void testNotMoveState() {
		expect(
				registrarBean.getNumberOfObs(patientId, polioProgram
						.getConceptName(), polioProgram.getConceptValue()))
				.andReturn(3).atLeastOnce();
		expect(
				registrarBean.getLastObsDate(patientId, polioProgram
						.getConceptName(), polioProgram.getConceptValue()))
				.andReturn(new Date());

		replay(registrarBean);

		currentPatientState = polioProgram.determineState(enrollment);

		verify(registrarBean);

		assertEquals(currentPatientState.getName(), polioState4.getName());

		// State does not change with the same number of Obs
		reset(registrarBean);

		expect(
				registrarBean.getNumberOfObs(patientId, polioProgram
						.getConceptName(), polioProgram.getConceptValue()))
				.andReturn(3).atLeastOnce();
		expect(
				registrarBean.getLastObsDate(patientId, polioProgram
						.getConceptName(), polioProgram.getConceptValue()))
				.andReturn(new Date());

		replay(registrarBean);

		currentPatientState = polioProgram.updateState(enrollment);

		verify(registrarBean);

		assertEquals(currentPatientState.getName(), polioState4.getName());
	}

	public void testNotMoveEndState() {
		expect(
				registrarBean.getNumberOfObs(patientId, polioProgram
						.getConceptName(), polioProgram.getConceptValue()))
				.andReturn(4).atLeastOnce();

		replay(registrarBean);

		currentPatientState = polioProgram.determineState(enrollment);

		verify(registrarBean);

		assertEquals(currentPatientState.getName(), polioState5.getName());

		// Future calls to updateState return the end state with no actions
		reset(registrarBean);

		expect(
				registrarBean.getNumberOfObs(patientId, polioProgram
						.getConceptName(), polioProgram.getConceptValue()))
				.andReturn(4).atLeastOnce();

		replay(registrarBean);

		currentPatientState = polioProgram.updateState(enrollment);

		verify(registrarBean);

		assertEquals(currentPatientState.getName(), polioState5.getName());

		reset(registrarBean);
	}

}
