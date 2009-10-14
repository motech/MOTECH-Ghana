package org.motech.event;

import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;

import org.motech.event.impl.RegimenImpl;
import org.motech.event.impl.RegimenStateImpl;
import org.motech.event.impl.ScheduleMessageCommand;
import org.motech.messaging.MessageScheduler;
import org.openmrs.Patient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TetanusInfoRegimenTest extends TestCase {

	ApplicationContext ctx;

	Patient patient;
	Integer patientId;

	RegimenImpl regimen;
	RegimenStateImpl state1;
	RegimenStateImpl state2;
	RegimenStateImpl state3;
	RegimenStateImpl state4;
	RegimenStateImpl state5;

	MessageScheduler messageScheduler;
	PatientObsService patientObsService;

	@Override
	protected void setUp() throws Exception {
		patientId = 576;

		patient = new Patient();
		patient.setPatientId(patientId);

		ctx = new ClassPathXmlApplicationContext(new String[] {
				"regimen/tetanus-info-regimen.xml",
				"test-common-regimen-beans.xml" });

		regimen = (RegimenImpl) ctx.getBean("tetanusInfo");

		state1 = (RegimenStateImpl) ctx.getBean("tetanusInfoState1");
		state2 = (RegimenStateImpl) ctx.getBean("tetanusInfoState2");
		state3 = (RegimenStateImpl) ctx.getBean("tetanusInfoState3");
		state4 = (RegimenStateImpl) ctx.getBean("tetanusInfoState4");
		state5 = (RegimenStateImpl) ctx.getBean("tetanusInfoState5");

		// EasyMock setup in Spring config
		patientObsService = (PatientObsService) ctx
				.getBean("patientObsService");

		state1.setPatientObsService(patientObsService);
		state2.setPatientObsService(patientObsService);
		state3.setPatientObsService(patientObsService);
		state4.setPatientObsService(patientObsService);

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

		ctx = null;

		patientObsService = null;
		messageScheduler = null;
	}

	public void testState1() {
		// Patient registered 0 minutes ago, message expected in 1 minute
		Date registrationDate = setPatientRegistration(patient, 0);
		Date messageDate = getMessageDate(registrationDate, 1);

		String messageKey = ((ScheduleMessageCommand) state1.getCommand())
				.getMessageKey();
		Long publicId = ((ScheduleMessageCommand) state1.getCommand())
				.getPublicId();
		String groupId = regimen.getName();

		messageScheduler.scheduleMessage(messageKey, publicId, groupId,
				patientId, messageDate);

		replay(patientObsService, messageScheduler);

		RegimenState state = regimen.determineState(patient);

		verify(patientObsService, messageScheduler);

		assertEquals(state1.getName(), state.getName());
	}

	public void testState2() {
		// Patient registered 1 minute ago, message expected 2 minutes after
		// registration
		Date registrationDate = setPatientRegistration(patient, 1);
		Date messageDate = getMessageDate(registrationDate, 2);

		String messageKey = ((ScheduleMessageCommand) state2.getCommand())
				.getMessageKey();
		Long publicId = ((ScheduleMessageCommand) state2.getCommand())
				.getPublicId();
		String groupId = regimen.getName();

		messageScheduler.scheduleMessage(messageKey, publicId, groupId,
				patientId, messageDate);

		replay(patientObsService, messageScheduler);

		RegimenState state = regimen.determineState(patient);

		verify(patientObsService, messageScheduler);

		assertEquals(state2.getName(), state.getName());
	}

	public void testState3() {
		// Patient registered 4 minute ago, message expected 5 minutes after
		// registration
		Date registrationDate = setPatientRegistration(patient, 4);
		Date messageDate = getMessageDate(registrationDate, 5);

		String messageKey = ((ScheduleMessageCommand) state3.getCommand())
				.getMessageKey();
		Long publicId = ((ScheduleMessageCommand) state3.getCommand())
				.getPublicId();
		String groupId = regimen.getName();

		messageScheduler.scheduleMessage(messageKey, publicId, groupId,
				patientId, messageDate);

		replay(patientObsService, messageScheduler);

		RegimenState state = regimen.determineState(patient);

		verify(patientObsService, messageScheduler);

		assertEquals(state3.getName(), state.getName());
	}

	public void testState4() {
		// Patient registered 5 minute ago, message expected 6 minutes after
		// registration
		Date registrationDate = setPatientRegistration(patient, 5);
		Date messageDate = getMessageDate(registrationDate, 6);

		String messageKey = ((ScheduleMessageCommand) state4.getCommand())
				.getMessageKey();
		Long publicId = ((ScheduleMessageCommand) state4.getCommand())
				.getPublicId();
		String groupId = regimen.getName();

		messageScheduler.scheduleMessage(messageKey, publicId, groupId,
				patientId, messageDate);

		replay(patientObsService, messageScheduler);

		RegimenState state = regimen.determineState(patient);

		verify(patientObsService, messageScheduler);

		assertEquals(state4.getName(), state.getName());
	}

	public void testState5() {
		// Patient registered 7 minute ago, no messages expected
		setPatientRegistration(patient, 7);

		replay(patientObsService, messageScheduler);

		RegimenState state = regimen.determineState(patient);

		verify(patientObsService, messageScheduler);

		assertEquals(state5.getName(), state.getName());
	}

	private Date setPatientRegistration(Patient patient, int minutesPrior) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -minutesPrior);
		calendar.add(Calendar.SECOND, -1);
		patient.setDateCreated(calendar.getTime());
		return calendar.getTime();
	}

	private Date getMessageDate(Date registrationDate, int minutesAfter) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(registrationDate);
		calendar.add(Calendar.MINUTE, minutesAfter);
		return calendar.getTime();
	}
}
