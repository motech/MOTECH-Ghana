package org.motech.event;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;

import org.motech.event.impl.CompositeCommand;
import org.motech.event.impl.RegimenImpl;
import org.motech.event.impl.RegimenStateImpl;
import org.motech.event.impl.RegimenStateTransitionExpectedNumImpl;
import org.motech.event.impl.RemoveRegimenEnrollmentCommand;
import org.motech.event.impl.ScheduleMessageCommand;
import org.motech.messaging.MessageScheduler;
import org.motech.svc.RegistrarBean;
import org.openmrs.Patient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TetanusImmunizRegimenTest extends TestCase {

	ApplicationContext ctx;

	Patient patient;
	Integer patientId;

	RegimenImpl regimen;
	RegimenStateImpl state1;
	RegimenStateImpl state2;
	RegimenStateImpl state3;
	RegimenStateImpl state4;
	RegimenStateImpl state5;
	RegimenStateImpl state6;
	RegimenStateImpl state7;

	MessageScheduler messageScheduler;
	RegistrarBean registrarBean;

	@Override
	protected void setUp() throws Exception {
		patientId = 572;

		patient = new Patient();
		patient.setPatientId(patientId);

		ctx = new ClassPathXmlApplicationContext(new String[] {
				"regimen/tetanus-immuniz-regimen.xml",
				"test-common-regimen-beans.xml" });

		regimen = (RegimenImpl) ctx.getBean("tetanusImmunization");

		state1 = (RegimenStateImpl) ctx.getBean("tetanusState1");
		state2 = (RegimenStateImpl) ctx.getBean("tetanusState1Reminder1");
		state3 = (RegimenStateImpl) ctx.getBean("tetanusState1Reminder2");
		state4 = (RegimenStateImpl) ctx.getBean("tetanusState2");
		state5 = (RegimenStateImpl) ctx.getBean("tetanusState2Reminder1");
		state6 = (RegimenStateImpl) ctx.getBean("tetanusState2Reminder2");
		state7 = (RegimenStateImpl) ctx.getBean("tetanusState3");

		// EasyMock setup in Spring config
		registrarBean = (RegistrarBean) ctx.getBean("registrarBean");

		state1.setRegistrarBean(registrarBean);
		for (RegimenStateTransition transition : state1.getTransitions()) {
			if (transition instanceof RegimenStateTransitionExpectedNumImpl) {
				((RegimenStateTransitionExpectedNumImpl) transition)
						.setRegistrarBean(registrarBean);
			}
		}
		state2.setRegistrarBean(registrarBean);
		for (RegimenStateTransition transition : state2.getTransitions()) {
			if (transition instanceof RegimenStateTransitionExpectedNumImpl) {
				((RegimenStateTransitionExpectedNumImpl) transition)
						.setRegistrarBean(registrarBean);
			}
		}
		state3.setRegistrarBean(registrarBean);
		for (RegimenStateTransition transition : state3.getTransitions()) {
			if (transition instanceof RegimenStateTransitionExpectedNumImpl) {
				((RegimenStateTransitionExpectedNumImpl) transition)
						.setRegistrarBean(registrarBean);
			}
		}
		state4.setRegistrarBean(registrarBean);
		for (RegimenStateTransition transition : state4.getTransitions()) {
			if (transition instanceof RegimenStateTransitionExpectedNumImpl) {
				((RegimenStateTransitionExpectedNumImpl) transition)
						.setRegistrarBean(registrarBean);
			}
		}
		state5.setRegistrarBean(registrarBean);
		for (RegimenStateTransition transition : state5.getTransitions()) {
			if (transition instanceof RegimenStateTransitionExpectedNumImpl) {
				((RegimenStateTransitionExpectedNumImpl) transition)
						.setRegistrarBean(registrarBean);
			}
		}
		state6.setRegistrarBean(registrarBean);
		for (RegimenStateTransition transition : state6.getTransitions()) {
			if (transition instanceof RegimenStateTransitionExpectedNumImpl) {
				((RegimenStateTransitionExpectedNumImpl) transition)
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
			} else if (command instanceof RemoveRegimenEnrollmentCommand) {
				((RemoveRegimenEnrollmentCommand) command)
						.setRegistrarBean(registrarBean);
			}
		}
	}

	@Override
	protected void tearDown() throws Exception {
		patient = null;
		patientId = null;

		regimen = null;
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
		RegimenState expectedState = state1;
		// No tetanus immunizations, patient registered 0 minutes ago, message
		// expected in 3 minutes
		Integer numberOfTetanusObs = 0;
		Date registrationDate = setPatientRegistration(patient, 0);
		Date messageDate = getMessageDate(registrationDate, 3);

		String messageKey = ((ScheduleMessageCommand) expectedState
				.getCommand()).getMessageKey();
		String groupId = regimen.getName();

		expect(
				registrarBean.getNumberOfObs(patient, regimen.getConceptName(),
						regimen.getConceptValue())).andReturn(
				numberOfTetanusObs);

		messageScheduler.scheduleMessage(messageKey, groupId, patientId,
				messageDate);

		replay(registrarBean, messageScheduler);

		RegimenState state = regimen.determineState(patient);

		verify(registrarBean, messageScheduler);

		assertEquals(expectedState.getName(), state.getName());
	}

	public void testState2() {
		RegimenState expectedState = state2;
		// No tetanus immunizations, patient registered 4 minutes ago, message
		// expected 6 minutes after registration
		Integer numberOfTetanusObs = 0;
		Date registrationDate = setPatientRegistration(patient, 4);
		Date messageDate = getMessageDate(registrationDate, 6);

		String messageKey = ((ScheduleMessageCommand) expectedState
				.getCommand()).getMessageKey();
		String groupId = regimen.getName();

		expect(
				registrarBean.getNumberOfObs(patient, regimen.getConceptName(),
						regimen.getConceptValue())).andReturn(
				numberOfTetanusObs).anyTimes();

		messageScheduler.scheduleMessage(messageKey, groupId, patientId,
				messageDate);

		replay(registrarBean, messageScheduler);

		RegimenState state = regimen.determineState(patient);

		verify(registrarBean, messageScheduler);

		assertEquals(expectedState.getName(), state.getName());
	}

	public void testState3() {
		RegimenState expectedState = state3;
		// No tetanus immunizations, patient registered 7 minutes ago, message
		// expected 9 minutes after registration
		Integer numberOfTetanusObs = 0;
		Date registrationDate = setPatientRegistration(patient, 7);
		Date messageDate = getMessageDate(registrationDate, 9);

		String messageKey = ((ScheduleMessageCommand) expectedState
				.getCommand()).getMessageKey();
		String groupId = regimen.getName();

		expect(
				registrarBean.getNumberOfObs(patient, regimen.getConceptName(),
						regimen.getConceptValue())).andReturn(
				numberOfTetanusObs).anyTimes();

		messageScheduler.scheduleMessage(messageKey, groupId, patientId,
				messageDate);

		replay(registrarBean, messageScheduler);

		RegimenState state = regimen.determineState(patient);

		verify(registrarBean, messageScheduler);

		assertEquals(expectedState.getName(), state.getName());
	}

	public void testState4() {
		RegimenState expectedState = state4;
		// 1 tetanus immunization, patient registered 0 minutes ago, message
		// expected 1 minutes after last tetanus, which was 0 minutes ago
		Integer numberOfTetanusObs = 1;
		setPatientRegistration(patient, 1);
		Date lastObsDate = getPreviousTetanusObsDate(0);
		Date messageDate = getMessageDate(lastObsDate, 1);

		String messageKey = ((ScheduleMessageCommand) expectedState
				.getCommand()).getMessageKey();
		String groupId = regimen.getName();

		expect(
				registrarBean.getNumberOfObs(patient, regimen.getConceptName(),
						regimen.getConceptValue())).andReturn(
				numberOfTetanusObs).anyTimes();
		expect(
				registrarBean.getLastObsDate(patient, regimen.getConceptName(),
						regimen.getConceptValue())).andReturn(lastObsDate)
				.anyTimes();

		messageScheduler.scheduleMessage(messageKey, groupId, patientId,
				messageDate);

		replay(registrarBean, messageScheduler);

		RegimenState state = regimen.determineState(patient);

		verify(registrarBean, messageScheduler);

		assertEquals(expectedState.getName(), state.getName());
	}

	public void testState5() {
		RegimenState expectedState = state5;
		// 1 tetanus immunization, patient registered 2 minutes ago, message
		// expected 3 minutes after last tetanus, which was 2 minutes ago
		Integer numberOfTetanusObs = 1;
		setPatientRegistration(patient, 2);
		Date lastObsDate = getPreviousTetanusObsDate(2);
		Date messageDate = getMessageDate(lastObsDate, 3);

		String messageKey = ((ScheduleMessageCommand) expectedState
				.getCommand()).getMessageKey();
		String groupId = regimen.getName();

		expect(
				registrarBean.getNumberOfObs(patient, regimen.getConceptName(),
						regimen.getConceptValue())).andReturn(
				numberOfTetanusObs).anyTimes();
		expect(
				registrarBean.getLastObsDate(patient, regimen.getConceptName(),
						regimen.getConceptValue())).andReturn(lastObsDate)
				.anyTimes();

		messageScheduler.scheduleMessage(messageKey, groupId, patientId,
				messageDate);

		replay(registrarBean, messageScheduler);

		RegimenState state = regimen.determineState(patient);

		verify(registrarBean, messageScheduler);

		assertEquals(expectedState.getName(), state.getName());
	}

	public void testState6() {
		RegimenState expectedState = state6;
		// 1 tetanus immunization, patient registered 4 minutes ago, message
		// expected 6 minutes after last tetanus, which was 4 minutes ago
		Integer numberOfTetanusObs = 1;
		setPatientRegistration(patient, 4);
		Date lastObsDate = getPreviousTetanusObsDate(4);
		Date messageDate = getMessageDate(lastObsDate, 6);

		String messageKey = ((ScheduleMessageCommand) expectedState
				.getCommand()).getMessageKey();
		String groupId = regimen.getName();

		expect(
				registrarBean.getNumberOfObs(patient, regimen.getConceptName(),
						regimen.getConceptValue())).andReturn(
				numberOfTetanusObs).anyTimes();
		expect(
				registrarBean.getLastObsDate(patient, regimen.getConceptName(),
						regimen.getConceptValue())).andReturn(lastObsDate)
				.anyTimes();

		messageScheduler.scheduleMessage(messageKey, groupId, patientId,
				messageDate);

		replay(registrarBean, messageScheduler);

		RegimenState state = regimen.determineState(patient);

		verify(registrarBean, messageScheduler);

		assertEquals(expectedState.getName(), state.getName());
	}

	public void testState7AllObs() {
		RegimenState expectedState = state7;
		// 2 tetanus immunizations, no messages expected
		Integer numberOfTetanusObs = 2;

		expect(
				registrarBean.getNumberOfObs(patient, regimen.getConceptName(),
						regimen.getConceptValue())).andReturn(
				numberOfTetanusObs).anyTimes();
		registrarBean.removeAllUnsentMessages(patient.getPatientId(), regimen
				.getName());
		registrarBean.removeRegimenEnrollment(patient.getPatientId(), regimen
				.getName());

		replay(registrarBean, messageScheduler);

		RegimenState state = regimen.determineState(patient);

		verify(registrarBean, messageScheduler);

		assertEquals(expectedState.getName(), state.getName());
	}

	public void testState7NoObs() {
		RegimenState expectedState = state7;
		// No tetanus immunizations, patient registered 10 minutes ago, no
		// messages expected
		Integer numberOfTetanusObs = 0;
		setPatientRegistration(patient, 10);

		expect(
				registrarBean.getNumberOfObs(patient, regimen.getConceptName(),
						regimen.getConceptValue())).andReturn(
				numberOfTetanusObs).anyTimes();
		registrarBean.removeAllUnsentMessages(patient.getPatientId(), regimen
				.getName());
		registrarBean.removeRegimenEnrollment(patient.getPatientId(), regimen
				.getName());

		replay(registrarBean, messageScheduler);

		RegimenState state = regimen.determineState(patient);

		verify(registrarBean, messageScheduler);

		assertEquals(expectedState.getName(), state.getName());
	}

	private Date setPatientRegistration(Patient patient, int minutesPrior) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -minutesPrior);
		calendar.add(Calendar.SECOND, -1);
		patient.setDateCreated(calendar.getTime());
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
