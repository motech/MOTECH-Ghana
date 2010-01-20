package org.motech.event;

import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;

import org.motech.event.impl.MessageProgramImpl;
import org.motech.event.impl.MessageProgramStateImpl;
import org.motech.event.impl.RemoveEnrollmentCommand;
import org.motech.event.impl.ScheduleMessageCommand;
import org.motech.messaging.MessageScheduler;
import org.motech.model.MessageProgramEnrollment;
import org.motech.svc.RegistrarBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TetanusInfoMessageProgramTest extends TestCase {

	ApplicationContext ctx;

	Integer patientId;

	MessageProgramImpl program;
	MessageProgramStateImpl state1;
	MessageProgramStateImpl state2;
	MessageProgramStateImpl state3;
	MessageProgramStateImpl state4;
	MessageProgramStateImpl state5;

	MessageScheduler messageScheduler;
	RegistrarBean registrarBean;

	@Override
	protected void setUp() throws Exception {
		patientId = 576;

		ctx = new ClassPathXmlApplicationContext(new String[] {
				"programs/tetanus-info-program.xml",
				"test-common-program-beans.xml" });

		program = (MessageProgramImpl) ctx.getBean("tetanusInfo");

		state1 = (MessageProgramStateImpl) ctx.getBean("tetanusInfoState1");
		state2 = (MessageProgramStateImpl) ctx.getBean("tetanusInfoState2");
		state3 = (MessageProgramStateImpl) ctx.getBean("tetanusInfoState3");
		state4 = (MessageProgramStateImpl) ctx.getBean("tetanusInfoState4");
		state5 = (MessageProgramStateImpl) ctx.getBean("tetanusInfoState5");

		// EasyMock setup in Spring config
		registrarBean = (RegistrarBean) ctx.getBean("registrarBean");

		state1.setRegistrarBean(registrarBean);
		state2.setRegistrarBean(registrarBean);
		state3.setRegistrarBean(registrarBean);
		state4.setRegistrarBean(registrarBean);

		// EasyMock setup in Spring config
		messageScheduler = (MessageScheduler) ctx.getBean("messageScheduler");

		((ScheduleMessageCommand) state1.getCommand())
				.setMessageScheduler(messageScheduler);
		((ScheduleMessageCommand) state2.getCommand())
				.setMessageScheduler(messageScheduler);
		((ScheduleMessageCommand) state3.getCommand())
				.setMessageScheduler(messageScheduler);
		((ScheduleMessageCommand) state4.getCommand())
				.setMessageScheduler(messageScheduler);
		((RemoveEnrollmentCommand) state5.getCommand())
				.setRegistrarBean(registrarBean);
	}

	@Override
	protected void tearDown() throws Exception {
		patientId = null;

		program = null;
		state1 = null;
		state2 = null;
		state3 = null;
		state4 = null;
		state5 = null;

		ctx = null;

		registrarBean = null;
		messageScheduler = null;
	}

	public void testState1() {
		// Patient enrolled 0 minutes ago, message expected in 1 minute
		Date enrollmentDate = getPatientEnrollment(0);
		Date messageDate = getMessageDate(enrollmentDate, 1);

		String messageKey = ((ScheduleMessageCommand) state1.getCommand())
				.getMessageKey();
		MessageProgramEnrollment enrollment = new MessageProgramEnrollment();
		enrollment.setStartDate(enrollmentDate);
		enrollment.setPersonId(patientId);

		messageScheduler.scheduleMessage(messageKey, enrollment, messageDate);

		replay(registrarBean, messageScheduler);

		MessageProgramState state = program.determineState(enrollment);

		verify(registrarBean, messageScheduler);

		assertEquals(state1.getName(), state.getName());
	}

	public void testState2() {
		// Patient enrolled 1 minute ago, message expected 2 minutes after
		// enrollment
		Date enrollmentDate = getPatientEnrollment(1);
		Date messageDate = getMessageDate(enrollmentDate, 2);

		String messageKey = ((ScheduleMessageCommand) state2.getCommand())
				.getMessageKey();
		MessageProgramEnrollment enrollment = new MessageProgramEnrollment();
		enrollment.setStartDate(enrollmentDate);
		enrollment.setPersonId(patientId);

		messageScheduler.scheduleMessage(messageKey, enrollment, messageDate);

		replay(registrarBean, messageScheduler);

		MessageProgramState state = program.determineState(enrollment);

		verify(registrarBean, messageScheduler);

		assertEquals(state2.getName(), state.getName());
	}

	public void testState3() {
		// Patient enrolled 4 minute ago, message expected 5 minutes after
		// enrollment
		Date enrollmentDate = getPatientEnrollment(4);
		Date messageDate = getMessageDate(enrollmentDate, 5);

		String messageKey = ((ScheduleMessageCommand) state3.getCommand())
				.getMessageKey();
		MessageProgramEnrollment enrollment = new MessageProgramEnrollment();
		enrollment.setStartDate(enrollmentDate);
		enrollment.setPersonId(patientId);

		messageScheduler.scheduleMessage(messageKey, enrollment, messageDate);

		replay(registrarBean, messageScheduler);

		MessageProgramState state = program.determineState(enrollment);

		verify(registrarBean, messageScheduler);

		assertEquals(state3.getName(), state.getName());
	}

	public void testState4() {
		// Patient enrolled 5 minute ago, message expected 6 minutes after
		// enrollment
		Date enrollmentDate = getPatientEnrollment(5);
		Date messageDate = getMessageDate(enrollmentDate, 6);

		String messageKey = ((ScheduleMessageCommand) state4.getCommand())
				.getMessageKey();
		MessageProgramEnrollment enrollment = new MessageProgramEnrollment();
		enrollment.setStartDate(enrollmentDate);
		enrollment.setPersonId(patientId);

		messageScheduler.scheduleMessage(messageKey, enrollment, messageDate);

		replay(registrarBean, messageScheduler);

		MessageProgramState state = program.determineState(enrollment);

		verify(registrarBean, messageScheduler);

		assertEquals(state4.getName(), state.getName());
	}

	public void testState5() {
		// Patient enrolled 7 minute ago, no messages expected
		Date enrollmentDate = getPatientEnrollment(7);

		MessageProgramEnrollment enrollment = new MessageProgramEnrollment();
		enrollment.setStartDate(enrollmentDate);
		enrollment.setPersonId(patientId);

		registrarBean.removeMessageProgramEnrollment(enrollment);

		replay(registrarBean, messageScheduler);

		MessageProgramState state = program.determineState(enrollment);

		verify(registrarBean, messageScheduler);

		assertEquals(state5.getName(), state.getName());
	}

	private Date getPatientEnrollment(int minutesPrior) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -minutesPrior);
		calendar.add(Calendar.SECOND, -1);
		return calendar.getTime();
	}

	private Date getMessageDate(Date enrollmentDate, int minutesAfter) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(enrollmentDate);
		calendar.add(Calendar.MINUTE, minutesAfter);
		return calendar.getTime();
	}
}
