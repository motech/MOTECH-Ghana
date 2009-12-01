package org.motech.messaging.impl;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Date;

import junit.framework.TestCase;

import org.motech.svc.RegistrarBean;

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
		String messageGroup = "Message Group";
		Integer messageRecipientId = 1;
		Date messageDate = new Date();

		messageScheduler.setUserPreferenceBased(userPreferencedBased);

		registrarBean.scheduleMessage(messageKey, messageGroup,
				messageRecipientId, messageDate, userPreferencedBased);

		replay(registrarBean);

		messageScheduler.scheduleMessage(messageKey, messageGroup,
				messageRecipientId, messageDate);

		verify(registrarBean);
	}

	public void testScheduleNoUserPref() {
		boolean userPreferencedBased = false;
		String messageKey = "Message Key";
		String messageGroup = "Message Group";
		Integer messageRecipientId = 1;
		Date messageDate = new Date();

		messageScheduler.setUserPreferenceBased(userPreferencedBased);

		registrarBean.scheduleMessage(messageKey, messageGroup,
				messageRecipientId, messageDate, userPreferencedBased);

		replay(registrarBean);

		messageScheduler.scheduleMessage(messageKey, messageGroup,
				messageRecipientId, messageDate);

		verify(registrarBean);
	}
}
