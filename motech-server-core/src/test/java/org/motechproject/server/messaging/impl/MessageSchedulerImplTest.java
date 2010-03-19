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
		MessageProgramEnrollment enrollment = new MessageProgramEnrollment();
		Date messageDate = new Date();

		messageScheduler.setUserPreferenceBased(userPreferencedBased);

		registrarBean.scheduleMessage(messageKey, enrollment, messageDate,
				userPreferencedBased);

		replay(registrarBean);

		messageScheduler.scheduleMessage(messageKey, enrollment, messageDate);

		verify(registrarBean);
	}

	public void testScheduleNoUserPref() {
		boolean userPreferencedBased = false;
		String messageKey = "Message Key";
		MessageProgramEnrollment enrollment = new MessageProgramEnrollment();
		Date messageDate = new Date();

		messageScheduler.setUserPreferenceBased(userPreferencedBased);

		registrarBean.scheduleMessage(messageKey, enrollment, messageDate,
				userPreferencedBased);

		replay(registrarBean);

		messageScheduler.scheduleMessage(messageKey, enrollment, messageDate);

		verify(registrarBean);
	}
}
