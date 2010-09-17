package org.motechproject.server.messaging.impl;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Date;

import junit.framework.TestCase;

import org.motechproject.server.model.MessageProgramEnrollment;
import org.motechproject.server.svc.RegistrarBean;

public class MessageSchedulerImplTest extends TestCase {

	MessageSchedulerImpl messageScheduler;

	RegistrarBean registrarBean;

	@Override
	protected void setUp() throws Exception {
		registrarBean = createMock(RegistrarBean.class);

		messageScheduler = new MessageSchedulerImpl();
		messageScheduler.setRegistrarBean(registrarBean);
	}

	@Override
	protected void tearDown() throws Exception {
		messageScheduler = null;

		registrarBean = null;
	}

	public void testScheduleUserPref() {
		boolean userPreferencedBased = true;
		String messageKey = "Message Key";
		String messageKeyA = "Message Key A";
		String messageKeyB = "Message Key B";
		String messageKeyC = "Message Key C";
		MessageProgramEnrollment enrollment = new MessageProgramEnrollment();
		Date messageDate = new Date();
		Date currentDate = new Date();

		messageScheduler.setUserPreferenceBased(userPreferencedBased);

		registrarBean.scheduleInfoMessages(messageKey, messageKeyA,
				messageKeyB, messageKeyC, enrollment, messageDate,
				userPreferencedBased, currentDate);

		replay(registrarBean);

		messageScheduler.scheduleMessages(messageKey, messageKeyA, messageKeyB,
				messageKeyC, enrollment, messageDate, currentDate);

		verify(registrarBean);
	}

	public void testScheduleNoUserPref() {
		boolean userPreferencedBased = false;
		String messageKey = "Message Key";
		String messageKeyA = "Message Key A";
		String messageKeyB = "Message Key B";
		String messageKeyC = "Message Key C";
		MessageProgramEnrollment enrollment = new MessageProgramEnrollment();
		Date messageDate = new Date();
		Date currentDate = new Date();

		messageScheduler.setUserPreferenceBased(userPreferencedBased);

		registrarBean.scheduleInfoMessages(messageKey, messageKeyA,
				messageKeyB, messageKeyC, enrollment, messageDate,
				userPreferencedBased, currentDate);

		replay(registrarBean);

		messageScheduler.scheduleMessages(messageKey, messageKeyA, messageKeyB,
				messageKeyC, enrollment, messageDate, currentDate);

		verify(registrarBean);
	}
}
