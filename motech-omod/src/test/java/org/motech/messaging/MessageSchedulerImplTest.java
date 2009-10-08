package org.motech.messaging;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.easymock.Capture;
import org.motech.openmrs.module.ContextService;
import org.motech.openmrs.module.MotechService;

public class MessageSchedulerImplTest extends TestCase {

	MessageSchedulerImpl messageScheduler;

	ContextService contextService;
	MotechService motechService;

	@Override
	protected void setUp() throws Exception {
		contextService = createMock(ContextService.class);
		motechService = createMock(MotechService.class);

		messageScheduler = new MessageSchedulerImpl();
		messageScheduler.setContextService(contextService);
	}

	@Override
	protected void tearDown() throws Exception {
		messageScheduler = null;

		contextService = null;
		motechService = null;
	}

	public void testRemoveUnsentMessage() {
		Integer recipientId = 1;
		Date messageDate = new Date();
		String messageGroup = "test group";

		MessageDefinition definition = new MessageDefinition();
		definition.setId(1L);

		ScheduledMessage scheduledMessage = new ScheduledMessage();
		scheduledMessage.setMessage(definition);
		scheduledMessage.setScheduledFor(messageDate);

		Message message = new Message();
		message.setSchedule(scheduledMessage);
		message.setAttemptStatus(MessageStatus.SHOULD_ATTEMPT);

		List<Message> messages = new ArrayList<Message>();
		messages.add(message);

		Date newMessageDate = new Date(System.currentTimeMillis() + 1000);
		MessageDefinition newDefinition = new MessageDefinition();
		newDefinition.setId(2L);

		Capture<Message> messageCapture = new Capture<Message>();

		expect(contextService.getMotechService()).andReturn(motechService);
		expect(
				motechService.getMessages(recipientId, messageGroup,
						MessageStatus.SHOULD_ATTEMPT)).andReturn(messages);
		expect(motechService.saveMessage(capture(messageCapture))).andReturn(
				message);

		replay(contextService, motechService);

		messageScheduler.removeUnsentMessages(recipientId, messageGroup,
				newDefinition, newMessageDate);

		verify(contextService, motechService);

		Message capturedMessage = messageCapture.getValue();
		assertEquals(MessageStatus.CANCELLED, capturedMessage
				.getAttemptStatus());
	}

	public void testNotRemoveSameMessage() {
		Integer recipientId = 1;
		Date messageDate = new Date();
		String messageGroup = "test group";

		MessageDefinition definition = new MessageDefinition();
		definition.setId(1L);

		ScheduledMessage scheduledMessage = new ScheduledMessage();
		scheduledMessage.setMessage(definition);
		scheduledMessage.setScheduledFor(messageDate);

		Message message = new Message();
		message.setSchedule(scheduledMessage);
		message.setAttemptStatus(MessageStatus.SHOULD_ATTEMPT);

		List<Message> messages = new ArrayList<Message>();
		messages.add(message);

		expect(contextService.getMotechService()).andReturn(motechService);
		expect(
				motechService.getMessages(recipientId, messageGroup,
						MessageStatus.SHOULD_ATTEMPT)).andReturn(messages);

		replay(contextService, motechService);

		messageScheduler.removeUnsentMessages(recipientId, messageGroup,
				definition, messageDate);

		verify(contextService, motechService);
	}

}
