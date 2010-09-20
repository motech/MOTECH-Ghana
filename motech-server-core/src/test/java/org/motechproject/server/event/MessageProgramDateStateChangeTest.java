package org.motechproject.server.event;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;

import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;

import org.easymock.IAnswer;
import org.motechproject.server.model.MessageProgramEnrollment;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.ws.DayOfWeek;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MessageProgramDateStateChangeTest extends TestCase {

	ApplicationContext ctx;

	Integer patientId;
	Integer obsId;
	MessageProgramEnrollment enrollment;
	Date referenceDate;
	Date week5;
	Date week6Monday;
	Date week6Friday;
	Date week7Saturday;
	Date week46;
	MessageProgram pregnancyProgram;
	MessageProgramState pregnancyState5;
	MessageProgramState pregnancyState6;
	MessageProgramState pregnancyState45;
	MessageProgramState currentPatientState;
	RegistrarBean registrarBean;

	@Override
	protected void setUp() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.set(2010, 5, 23, 14, 49, 19); // Wed Jun 23 14:49:19 2010
		referenceDate = calendar.getTime();

		calendar.set(2010, 6, 27, 19, 49, 19); // Wed Jul 27 19:49:19 2010
		week5 = calendar.getTime();

		calendar.set(2010, 7, 2, 19, 49, 19); // Mon Aug 2 19:49:19 2010
		week6Monday = calendar.getTime();
		calendar.set(2010, 7, 6, 19, 49, 19); // Fri Aug 6 19:49:19 2010
		week6Friday = calendar.getTime();
		calendar.set(2010, 7, 7, 19, 49, 19); // Fri Aug 7 19:49:19 2010
		week7Saturday = calendar.getTime();

		calendar.setTime(referenceDate);
		calendar.add(Calendar.DATE, 46 * 7 + 1); // over 46 weeks
		week46 = calendar.getTime();

		patientId = 1;
		obsId = 2;

		enrollment = new MessageProgramEnrollment();
		enrollment.setPersonId(patientId);
		enrollment.setObsId(obsId);

		ctx = new ClassPathXmlApplicationContext(new String[] {
				"test-common-program-beans.xml",
				"programs/weekly-info-pregnancy-program.xml" });
		pregnancyProgram = (MessageProgram) ctx
				.getBean("weeklyInfoPregnancyProgram");

		pregnancyState5 = (MessageProgramState) ctx
				.getBean("weeklyInfoPregnancyState5");
		pregnancyState6 = (MessageProgramState) ctx
				.getBean("weeklyInfoPregnancyState6");
		pregnancyState45 = (MessageProgramState) ctx
				.getBean("weeklyInfoPregnancyState45");

		// EasyMock setup in Spring config
		registrarBean = (RegistrarBean) ctx.getBean("registrarBean");
	}

	@Override
	protected void tearDown() throws Exception {
		enrollment = null;
		ctx = null;
		pregnancyProgram = null;
		pregnancyState5 = null;
		pregnancyState6 = null;
		pregnancyState45 = null;
		registrarBean = null;
	}

	public void testDetermineWeek5Preferred() {
		int weekNumber = 5;
		String messageKey = "pregnancy.week." + weekNumber;
		String messageKeyA = messageKey + ".a";
		String messageKeyB = messageKey + ".b";
		String messageKeyC = messageKey + ".c";

		expect(registrarBean.getObsValue(obsId)).andReturn(referenceDate)
				.atLeastOnce();
		registrarBean.scheduleInfoMessages(eq(messageKey), eq(messageKeyA),
				eq(messageKeyB), eq(messageKeyC), eq(enrollment),
				(Date) anyObject(), eq(true), eq(week5));
		expect(
				registrarBean.determineUserPreferredMessageDate(eq(patientId),
						(Date) anyObject())).andAnswer(new IAnswer<Date>() {
			public Date answer() throws Throwable {
				Date messageDate = (Date) getCurrentArguments()[1];
				return determinePreferredMessageDate(messageDate, week5);
			}
		}).atLeastOnce();

		replay(registrarBean);

		currentPatientState = pregnancyProgram
				.determineState(enrollment, week5);

		verify(registrarBean);

		assertEquals(pregnancyState5.getName(), currentPatientState.getName());
	}

	public void testDetermineWeek5() {
		int weekNumber = 5;
		String messageKey = "pregnancy.week." + weekNumber;
		String messageKeyA = messageKey + ".a";
		String messageKeyB = messageKey + ".b";
		String messageKeyC = messageKey + ".c";

		expect(registrarBean.getObsValue(obsId)).andReturn(referenceDate)
				.atLeastOnce();
		registrarBean.scheduleInfoMessages(eq(messageKey), eq(messageKeyA),
				eq(messageKeyB), eq(messageKeyC), eq(enrollment),
				(Date) anyObject(), eq(true), eq(week5));
		expect(
				registrarBean.determineUserPreferredMessageDate(eq(patientId),
						(Date) anyObject())).andAnswer(new IAnswer<Date>() {
			public Date answer() throws Throwable {
				return (Date) getCurrentArguments()[1];
			}
		}).atLeastOnce();

		replay(registrarBean);

		currentPatientState = pregnancyProgram
				.determineState(enrollment, week5);

		verify(registrarBean);

		assertEquals(pregnancyState5.getName(), currentPatientState.getName());
	}

	public void testDetermineEndState() {
		expect(registrarBean.getObsValue(obsId)).andReturn(referenceDate)
				.atLeastOnce();
		expect(
				registrarBean.determineUserPreferredMessageDate(eq(patientId),
						(Date) anyObject())).andAnswer(new IAnswer<Date>() {
			public Date answer() throws Throwable {
				return (Date) getCurrentArguments()[1];
			}
		}).atLeastOnce();
		registrarBean.removeMessageProgramEnrollment(enrollment);

		replay(registrarBean);

		currentPatientState = pregnancyProgram.determineState(enrollment,
				week46);

		verify(registrarBean);

		assertEquals(pregnancyState45.getName(), currentPatientState.getName());
	}

	public void testDetermineSameWeek6() {
		int weekNumber = 6;
		String messageKey = "pregnancy.week." + weekNumber;
		String messageKeyA = messageKey + ".a";
		String messageKeyB = messageKey + ".b";
		String messageKeyC = messageKey + ".c";

		expect(registrarBean.getObsValue(obsId)).andReturn(referenceDate)
				.atLeastOnce();
		registrarBean.scheduleInfoMessages(eq(messageKey), eq(messageKeyA),
				eq(messageKeyB), eq(messageKeyC), eq(enrollment),
				(Date) anyObject(), eq(true), eq(week6Monday));
		expect(
				registrarBean.determineUserPreferredMessageDate(eq(patientId),
						(Date) anyObject())).andAnswer(new IAnswer<Date>() {
			public Date answer() throws Throwable {
				return (Date) getCurrentArguments()[1];
			}
		}).atLeastOnce();

		replay(registrarBean);

		currentPatientState = pregnancyProgram.determineState(enrollment,
				week6Monday);

		verify(registrarBean);

		assertEquals(pregnancyState6.getName(), currentPatientState.getName());

		reset(registrarBean);

		expect(registrarBean.getObsValue(obsId)).andReturn(referenceDate)
				.atLeastOnce();
		registrarBean.scheduleInfoMessages(eq(messageKey), eq(messageKeyA),
				eq(messageKeyB), eq(messageKeyC), eq(enrollment),
				(Date) anyObject(), eq(true), eq(week6Friday));
		expect(
				registrarBean.determineUserPreferredMessageDate(eq(patientId),
						(Date) anyObject())).andAnswer(new IAnswer<Date>() {
			public Date answer() throws Throwable {
				return (Date) getCurrentArguments()[1];
			}
		}).atLeastOnce();

		replay(registrarBean);

		currentPatientState = pregnancyProgram.determineState(enrollment,
				week6Friday);

		verify(registrarBean);

		assertEquals(pregnancyState6.getName(), currentPatientState.getName());
	}

	private Date determinePreferredMessageDate(Date messageDate,
			Date currentDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(messageDate);

		calendar.set(Calendar.HOUR_OF_DAY, 18);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		DayOfWeek day = DayOfWeek.MONDAY;
		if (day != null) {
			calendar.set(Calendar.DAY_OF_WEEK, day.getCalendarValue());
		}

		return calendar.getTime();
	}

}
