package org.motech.event;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;

import org.motech.event.impl.CompositeCommand;
import org.motech.event.impl.MessageProgramImpl;
import org.motech.event.impl.MessageProgramStateImpl;
import org.motech.event.impl.MessageProgramStateTransitionExpectedNumImpl;
import org.motech.event.impl.RemoveEnrollmentCommand;
import org.motech.event.impl.ScheduleMessageCommand;
import org.motech.messaging.MessageScheduler;
import org.motech.svc.RegistrarBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TetanusImmunizMessageProgramTest extends TestCase {

	ApplicationContext ctx;

	Integer patientId;

	MessageProgramImpl program;
	MessageProgramStateImpl state1;
	MessageProgramStateImpl state2;
	MessageProgramStateImpl state3;
	MessageProgramStateImpl state4;
	MessageProgramStateImpl state5;
	MessageProgramStateImpl state6;
	MessageProgramStateImpl state7;

	MessageScheduler messageScheduler;
	RegistrarBean registrarBean;

	@Override
	protected void setUp() throws Exception {
		patientId = 572;

		ctx = new ClassPathXmlApplicationContext(new String[] {
				"programs/tetanus-immuniz-program.xml",
				"test-common-program-beans.xml" });

		program = (MessageProgramImpl) ctx.getBean("tetanusImmunization");

		state1 = (MessageProgramStateImpl) ctx.getBean("tetanusState1");
		state2 = (MessageProgramStateImpl) ctx
				.getBean("tetanusState1Reminder1");
		state3 = (MessageProgramStateImpl) ctx
				.getBean("tetanusState1Reminder2");
		state4 = (MessageProgramStateImpl) ctx.getBean("tetanusState2");
		state5 = (MessageProgramStateImpl) ctx
				.getBean("tetanusState2Reminder1");
		state6 = (MessageProgramStateImpl) ctx
				.getBean("tetanusState2Reminder2");
		state7 = (MessageProgramStateImpl) ctx.getBean("tetanusState3");

		// EasyMock setup in Spring config
		registrarBean = (RegistrarBean) ctx.getBean("registrarBean");

		state1.setRegistrarBean(registrarBean);
		for (MessageProgramStateTransition transition : state1.getTransitions()) {
			if (transition instanceof MessageProgramStateTransitionExpectedNumImpl) {
				((MessageProgramStateTransitionExpectedNumImpl) transition)
						.setRegistrarBean(registrarBean);
			}
		}
		state2.setRegistrarBean(registrarBean);
		for (MessageProgramStateTransition transition : state2.getTransitions()) {
			if (transition instanceof MessageProgramStateTransitionExpectedNumImpl) {
				((MessageProgramStateTransitionExpectedNumImpl) transition)
						.setRegistrarBean(registrarBean);
			}
		}
		state3.setRegistrarBean(registrarBean);
		for (MessageProgramStateTransition transition : state3.getTransitions()) {
			if (transition instanceof MessageProgramStateTransitionExpectedNumImpl) {
				((MessageProgramStateTransitionExpectedNumImpl) transition)
						.setRegistrarBean(registrarBean);
			}
		}
		state4.setRegistrarBean(registrarBean);
		for (MessageProgramStateTransition transition : state4.getTransitions()) {
			if (transition instanceof MessageProgramStateTransitionExpectedNumImpl) {
				((MessageProgramStateTransitionExpectedNumImpl) transition)
						.setRegistrarBean(registrarBean);
			}
		}
		state5.setRegistrarBean(registrarBean);
		for (MessageProgramStateTransition transition : state5.getTransitions()) {
			if (transition instanceof MessageProgramStateTransitionExpectedNumImpl) {
				((MessageProgramStateTransitionExpectedNumImpl) transition)
						.setRegistrarBean(registrarBean);
			}
		}
		state6.setRegistrarBean(registrarBean);
		for (MessageProgramStateTransition transition : state6.getTransitions()) {
			if (transition instanceof MessageProgramStateTransitionExpectedNumImpl) {
				((MessageProgramStateTransitionExpectedNumImpl) transition)
						.setRegistrarBean(registrarBean);
			}
		}

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
		((ScheduleMessageCommand) state5.getCommand())
				.setMessageScheduler(messageScheduler);
		((ScheduleMessageCommand) state6.getCommand())
				.setMessageScheduler(messageScheduler);
		for (Command command : ((CompositeCommand) state7.getCommand())
				.getCommands()) {
			if (command instanceof ScheduleMessageCommand) {
				((ScheduleMessageCommand) command)
						.setMessageScheduler(messageScheduler);
			} else if (command instanceof RemoveEnrollmentCommand) {
				((RemoveEnrollmentCommand) command)
						.setRegistrarBean(registrarBean);
			}
		}
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
		state6 = null;
		state7 = null;

		ctx = null;

		registrarBean = null;
		messageScheduler = null;
	}

	public void testState1() {
		MessageProgramState expectedState = state1;
		// No tetanus immunizations, patient registered 0 minutes ago, message
		// expected in 3 minutes
		Integer numberOfTetanusObs = 0;
		Date registrationDate = getPatientRegistration(0);
		Date messageDate = getMessageDate(registrationDate, 3);

		String messageKey = ((ScheduleMessageCommand) expectedState
				.getCommand()).getMessageKey();
		String groupId = program.getName();

		expect(
				registrarBean.getNumberOfObs(patientId, program
						.getConceptName(), program.getConceptValue()))
				.andReturn(numberOfTetanusObs);
		expect(registrarBean.getPatientRegistrationDate(patientId)).andReturn(
				registrationDate).anyTimes();

		messageScheduler.scheduleMessage(messageKey, groupId, patientId,
				messageDate);

		replay(registrarBean, messageScheduler);

		MessageProgramState state = program.determineState(patientId);

		verify(registrarBean, messageScheduler);

		assertEquals(expectedState.getName(), state.getName());
	}

	public void testState2() {
		MessageProgramState expectedState = state2;
		// No tetanus immunizations, patient registered 4 minutes ago, message
		// expected 6 minutes after registration
		Integer numberOfTetanusObs = 0;
		Date registrationDate = getPatientRegistration(4);
		Date messageDate = getMessageDate(registrationDate, 6);

		String messageKey = ((ScheduleMessageCommand) expectedState
				.getCommand()).getMessageKey();
		String groupId = program.getName();

		expect(
				registrarBean.getNumberOfObs(patientId, program
						.getConceptName(), program.getConceptValue()))
				.andReturn(numberOfTetanusObs).anyTimes();
		expect(registrarBean.getPatientRegistrationDate(patientId)).andReturn(
				registrationDate).anyTimes();

		messageScheduler.scheduleMessage(messageKey, groupId, patientId,
				messageDate);

		replay(registrarBean, messageScheduler);

		MessageProgramState state = program.determineState(patientId);

		verify(registrarBean, messageScheduler);

		assertEquals(expectedState.getName(), state.getName());
	}

	public void testState3() {
		MessageProgramState expectedState = state3;
		// No tetanus immunizations, patient registered 7 minutes ago, message
		// expected 9 minutes after registration
		Integer numberOfTetanusObs = 0;
		Date registrationDate = getPatientRegistration(7);
		Date messageDate = getMessageDate(registrationDate, 9);

		String messageKey = ((ScheduleMessageCommand) expectedState
				.getCommand()).getMessageKey();
		String groupId = program.getName();

		expect(
				registrarBean.getNumberOfObs(patientId, program
						.getConceptName(), program.getConceptValue()))
				.andReturn(numberOfTetanusObs).anyTimes();
		expect(registrarBean.getPatientRegistrationDate(patientId)).andReturn(
				registrationDate).anyTimes();

		messageScheduler.scheduleMessage(messageKey, groupId, patientId,
				messageDate);

		replay(registrarBean, messageScheduler);

		MessageProgramState state = program.determineState(patientId);

		verify(registrarBean, messageScheduler);

		assertEquals(expectedState.getName(), state.getName());
	}

	public void testState4() {
		MessageProgramState expectedState = state4;
		// 1 tetanus immunization, patient registered 0 minutes ago, message
		// expected 1 minutes after last tetanus, which was 0 minutes ago
		Integer numberOfTetanusObs = 1;
		Date lastObsDate = getPreviousTetanusObsDate(0);
		Date messageDate = getMessageDate(lastObsDate, 1);

		String messageKey = ((ScheduleMessageCommand) expectedState
				.getCommand()).getMessageKey();
		String groupId = program.getName();

		expect(
				registrarBean.getNumberOfObs(patientId, program
						.getConceptName(), program.getConceptValue()))
				.andReturn(numberOfTetanusObs).anyTimes();
		expect(
				registrarBean.getLastObsDate(patientId, program
						.getConceptName(), program.getConceptValue()))
				.andReturn(lastObsDate).anyTimes();

		messageScheduler.scheduleMessage(messageKey, groupId, patientId,
				messageDate);

		replay(registrarBean, messageScheduler);

		MessageProgramState state = program.determineState(patientId);

		verify(registrarBean, messageScheduler);

		assertEquals(expectedState.getName(), state.getName());
	}

	public void testState5() {
		MessageProgramState expectedState = state5;
		// 1 tetanus immunization, patient registered 2 minutes ago, message
		// expected 3 minutes after last tetanus, which was 2 minutes ago
		Integer numberOfTetanusObs = 1;
		Date lastObsDate = getPreviousTetanusObsDate(2);
		Date messageDate = getMessageDate(lastObsDate, 3);

		String messageKey = ((ScheduleMessageCommand) expectedState
				.getCommand()).getMessageKey();
		String groupId = program.getName();

		expect(
				registrarBean.getNumberOfObs(patientId, program
						.getConceptName(), program.getConceptValue()))
				.andReturn(numberOfTetanusObs).anyTimes();
		expect(
				registrarBean.getLastObsDate(patientId, program
						.getConceptName(), program.getConceptValue()))
				.andReturn(lastObsDate).anyTimes();

		messageScheduler.scheduleMessage(messageKey, groupId, patientId,
				messageDate);

		replay(registrarBean, messageScheduler);

		MessageProgramState state = program.determineState(patientId);

		verify(registrarBean, messageScheduler);

		assertEquals(expectedState.getName(), state.getName());
	}

	public void testState6() {
		MessageProgramState expectedState = state6;
		// 1 tetanus immunization, patient registered 4 minutes ago, message
		// expected 6 minutes after last tetanus, which was 4 minutes ago
		Integer numberOfTetanusObs = 1;
		Date lastObsDate = getPreviousTetanusObsDate(4);
		Date messageDate = getMessageDate(lastObsDate, 6);

		String messageKey = ((ScheduleMessageCommand) expectedState
				.getCommand()).getMessageKey();
		String groupId = program.getName();

		expect(
				registrarBean.getNumberOfObs(patientId, program
						.getConceptName(), program.getConceptValue()))
				.andReturn(numberOfTetanusObs).anyTimes();
		expect(
				registrarBean.getLastObsDate(patientId, program
						.getConceptName(), program.getConceptValue()))
				.andReturn(lastObsDate).anyTimes();

		messageScheduler.scheduleMessage(messageKey, groupId, patientId,
				messageDate);

		replay(registrarBean, messageScheduler);

		MessageProgramState state = program.determineState(patientId);

		verify(registrarBean, messageScheduler);

		assertEquals(expectedState.getName(), state.getName());
	}

	public void testState7AllObs() {
		MessageProgramState expectedState = state7;
		// 2 tetanus immunizations, no messages expected
		Integer numberOfTetanusObs = 2;

		expect(
				registrarBean.getNumberOfObs(patientId, program
						.getConceptName(), program.getConceptValue()))
				.andReturn(numberOfTetanusObs).anyTimes();
		registrarBean.removeAllUnsentMessages(patientId, program.getName());
		registrarBean.removeMessageProgramEnrollment(patientId, program
				.getName());

		replay(registrarBean, messageScheduler);

		MessageProgramState state = program.determineState(patientId);

		verify(registrarBean, messageScheduler);

		assertEquals(expectedState.getName(), state.getName());
	}

	public void testState7NoObs() {
		MessageProgramState expectedState = state7;
		// No tetanus immunizations, patient registered 10 minutes ago, no
		// messages expected
		Integer numberOfTetanusObs = 0;
		Date registrationDate = getPatientRegistration(10);

		expect(
				registrarBean.getNumberOfObs(patientId, program
						.getConceptName(), program.getConceptValue()))
				.andReturn(numberOfTetanusObs).anyTimes();
		expect(registrarBean.getPatientRegistrationDate(patientId)).andReturn(
				registrationDate).anyTimes();
		registrarBean.removeAllUnsentMessages(patientId, program.getName());
		registrarBean.removeMessageProgramEnrollment(patientId, program
				.getName());

		replay(registrarBean, messageScheduler);

		MessageProgramState state = program.determineState(patientId);

		verify(registrarBean, messageScheduler);

		assertEquals(expectedState.getName(), state.getName());
	}

	private Date getPatientRegistration(int minutesPrior) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -minutesPrior);
		calendar.add(Calendar.SECOND, -1);
		return calendar.getTime();
	}

	private Date getPreviousTetanusObsDate(int minutesPrior) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -minutesPrior);
		calendar.add(Calendar.SECOND, -1);
		return calendar.getTime();
	}

	private Date getMessageDate(Date registrationDate, int minutesAfter) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(registrationDate);
		calendar.add(Calendar.MINUTE, minutesAfter);
		return calendar.getTime();
	}
}
