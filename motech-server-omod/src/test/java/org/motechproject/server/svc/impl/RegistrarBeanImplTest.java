/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010 The Trustees of Columbia University in the City of
 * New York and Grameen Foundation USA.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Grameen Foundation USA, Columbia University, or
 * their respective contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA, COLUMBIA UNIVERSITY
 * AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION
 * USA, COLUMBIA UNIVERSITY OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.motechproject.server.svc.impl;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.easymock.Capture;
import org.motechproject.server.model.Blackout;
import org.motechproject.server.model.Message;
import org.motechproject.server.model.MessageDefinition;
import org.motechproject.server.model.MessageProgramEnrollment;
import org.motechproject.server.model.MessageStatus;
import org.motechproject.server.model.MessageType;
import org.motechproject.server.model.ScheduledMessage;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.MotechService;
import org.motechproject.server.util.MotechConstants;
import org.motechproject.ws.DayOfWeek;
import org.motechproject.ws.MediaType;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PersonService;

public class RegistrarBeanImplTest extends TestCase {

	RegistrarBeanImpl regBean;

	ContextService contextService;
	AdministrationService adminService;
	MotechService motechService;
	PersonService personService;

	@Override
	protected void setUp() throws Exception {
		contextService = createMock(ContextService.class);
		adminService = createMock(AdministrationService.class);
		motechService = createMock(MotechService.class);
		personService = createMock(PersonService.class);

		regBean = new RegistrarBeanImpl();
		regBean.setContextService(contextService);
	}

	@Override
	protected void tearDown() throws Exception {
		regBean = null;
		contextService = null;
		adminService = null;
		motechService = null;
		personService = null;
	}

	public void testDeterminePersonPrefDate() {
		DayOfWeek day = DayOfWeek.MONDAY;
		int hour = 9;
		int minute = 0;
		String timeAsString = "09:00";
		Date messageDate = new Date();
		Date currentDate = messageDate;

		Person person = new Person(1);
		PersonAttributeType dayType = new PersonAttributeType(1);
		dayType.setName(MotechConstants.PERSON_ATTRIBUTE_DELIVERY_DAY);
		PersonAttributeType timeType = new PersonAttributeType(2);
		timeType.setName(MotechConstants.PERSON_ATTRIBUTE_DELIVERY_TIME);
		person.addAttribute(new PersonAttribute(dayType, day.toString()));
		person.addAttribute(new PersonAttribute(timeType, timeAsString));

		Date prefDate = regBean.determinePreferredMessageDate(person,
				messageDate, currentDate, true);

		Calendar messageCal = Calendar.getInstance();
		messageCal.setTime(messageDate);
		Calendar prefCal = Calendar.getInstance();
		prefCal.setTime(prefDate);

		assertTrue(prefCal.after(messageCal));
		assertEquals(day.getCalendarValue(), prefCal.get(Calendar.DAY_OF_WEEK));
		assertEquals(hour, prefCal.get(Calendar.HOUR_OF_DAY));
		assertEquals(minute, prefCal.get(Calendar.MINUTE));
		assertEquals(0, prefCal.get(Calendar.SECOND));
	}

	public void testDetermineDefaultPrefDate() {
		DayOfWeek day = DayOfWeek.MONDAY;
		int hour = 9;
		int minute = 0;
		String timeAsString = "09:00";
		Date messageDate = new Date();
		Date currentDate = messageDate;

		Person person = new Person(1);

		expect(contextService.getAdministrationService()).andReturn(
				adminService).times(2);
		expect(
				adminService
						.getGlobalProperty(MotechConstants.GLOBAL_PROPERTY_DAY_OF_WEEK))
				.andReturn(day.toString());
		expect(
				adminService
						.getGlobalProperty(MotechConstants.GLOBAL_PROPERTY_TIME_OF_DAY))
				.andReturn(timeAsString);

		replay(contextService, adminService);

		Date prefDate = regBean.determinePreferredMessageDate(person,
				messageDate, currentDate, true);

		verify(contextService, adminService);

		Calendar messageCal = Calendar.getInstance();
		messageCal.setTime(messageDate);
		Calendar prefCal = Calendar.getInstance();
		prefCal.setTime(prefDate);

		assertTrue(prefCal.after(messageCal));
		assertEquals(day.getCalendarValue(), prefCal.get(Calendar.DAY_OF_WEEK));
		assertEquals(hour, prefCal.get(Calendar.HOUR_OF_DAY));
		assertEquals(minute, prefCal.get(Calendar.MINUTE));
		assertEquals(0, prefCal.get(Calendar.SECOND));
	}

	public void testDetermineNoPrefDate() {
		Date messageDate = new Date();
		Date currentDate = messageDate;

		Person person = new Person(1);

		expect(contextService.getAdministrationService()).andReturn(
				adminService).times(2);
		expect(
				adminService
						.getGlobalProperty(MotechConstants.GLOBAL_PROPERTY_DAY_OF_WEEK))
				.andReturn(null);
		expect(
				adminService
						.getGlobalProperty(MotechConstants.GLOBAL_PROPERTY_TIME_OF_DAY))
				.andReturn(null);

		replay(contextService, adminService);

		Date prefDate = regBean.determinePreferredMessageDate(person,
				messageDate, currentDate, true);

		verify(contextService, adminService);

		Calendar messageCal = Calendar.getInstance();
		messageCal.setTime(messageDate);
		Calendar prefCal = Calendar.getInstance();
		prefCal.setTime(prefDate);

		assertEquals(messageCal.get(Calendar.YEAR), prefCal.get(Calendar.YEAR));
		assertEquals(messageCal.get(Calendar.MONTH), prefCal
				.get(Calendar.MONTH));
		assertEquals(messageCal.get(Calendar.DATE), prefCal.get(Calendar.DATE));
		assertEquals(messageCal.get(Calendar.HOUR_OF_DAY), prefCal
				.get(Calendar.HOUR_OF_DAY));
		assertEquals(messageCal.get(Calendar.MINUTE), prefCal
				.get(Calendar.MINUTE));
		assertEquals(0, prefCal.get(Calendar.SECOND));
	}

	public void testAdjustDateTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 14);
		calendar.set(Calendar.MINUTE, 13);
		calendar.set(Calendar.SECOND, 54);
		Date messageDate = calendar.getTime();

		int hour = 15;
		int minute = 37;
		int second = 0;

		Calendar timeCalendar = Calendar.getInstance();
		timeCalendar.set(Calendar.YEAR, 1986);
		timeCalendar.set(Calendar.MONTH, 6);
		timeCalendar.set(Calendar.DAY_OF_MONTH, 4);
		timeCalendar.set(Calendar.HOUR_OF_DAY, hour);
		timeCalendar.set(Calendar.MINUTE, minute);
		timeCalendar.set(Calendar.SECOND, 34);

		Date prefDate = regBean.adjustTime(messageDate, timeCalendar.getTime());

		Calendar prefCal = Calendar.getInstance();
		prefCal.setTime(prefDate);

		assertEquals(calendar.get(Calendar.YEAR), prefCal.get(Calendar.YEAR));
		assertEquals(calendar.get(Calendar.MONTH), prefCal.get(Calendar.MONTH));
		assertEquals(calendar.get(Calendar.DATE), prefCal.get(Calendar.DATE));
		assertFalse("Hour not updated",
				calendar.get(Calendar.HOUR_OF_DAY) == prefCal
						.get(Calendar.HOUR_OF_DAY));
		assertEquals(hour, prefCal.get(Calendar.HOUR_OF_DAY));
		assertFalse("Minute not updated",
				calendar.get(Calendar.MINUTE) == prefCal.get(Calendar.MINUTE));
		assertEquals(minute, prefCal.get(Calendar.MINUTE));
		assertFalse("Second not updated",
				calendar.get(Calendar.SECOND) == prefCal.get(Calendar.SECOND));
		assertEquals(second, prefCal.get(Calendar.SECOND));
	}

	public void testAdjustDateBlackoutAM() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 2);
		calendar.set(Calendar.MINUTE, 13);
		calendar.set(Calendar.SECOND, 54);
		Date messageDate = calendar.getTime();

		Time blackoutStart = Time.valueOf("22:00:00");
		Time blackoutEnd = Time.valueOf("06:00:00");

		int hour = 6;
		int minute = 0;
		int second = 0;

		Blackout blackout = new Blackout(blackoutStart, blackoutEnd);

		expect(contextService.getMotechService()).andReturn(motechService);
		expect(motechService.getBlackoutSettings()).andReturn(blackout);

		replay(contextService, adminService, motechService);

		Date prefDate = regBean.adjustForBlackout(messageDate);

		verify(contextService, adminService, motechService);

		Calendar prefCal = Calendar.getInstance();
		prefCal.setTime(prefDate);

		assertFalse("Hour not updated",
				calendar.get(Calendar.HOUR_OF_DAY) == prefCal
						.get(Calendar.HOUR_OF_DAY));
		assertEquals(hour, prefCal.get(Calendar.HOUR_OF_DAY));
		assertFalse("Minute not updated",
				calendar.get(Calendar.MINUTE) == prefCal.get(Calendar.MINUTE));
		assertEquals(minute, prefCal.get(Calendar.MINUTE));
		assertFalse("Second not updated",
				calendar.get(Calendar.SECOND) == prefCal.get(Calendar.SECOND));
		assertEquals(second, prefCal.get(Calendar.SECOND));
	}

	public void testAdjustDateBlackoutPM() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 22);
		calendar.set(Calendar.MINUTE, 13);
		calendar.set(Calendar.SECOND, 54);
		Date messageDate = calendar.getTime();

		Time blackoutStart = Time.valueOf("22:00:00");
		Time blackoutEnd = Time.valueOf("06:00:00");

		int hour = 6;
		int minute = 0;
		int second = 0;

		Blackout blackout = new Blackout(blackoutStart, blackoutEnd);

		expect(contextService.getMotechService()).andReturn(motechService);
		expect(motechService.getBlackoutSettings()).andReturn(blackout);

		replay(contextService, adminService, motechService);

		Date prefDate = regBean.adjustForBlackout(messageDate);

		verify(contextService, adminService, motechService);

		Calendar prefCal = Calendar.getInstance();
		prefCal.setTime(prefDate);

		assertFalse("Hour not updated",
				calendar.get(Calendar.HOUR_OF_DAY) == prefCal
						.get(Calendar.HOUR_OF_DAY));
		assertEquals(hour, prefCal.get(Calendar.HOUR_OF_DAY));
		assertFalse("Minute not updated",
				calendar.get(Calendar.MINUTE) == prefCal.get(Calendar.MINUTE));
		assertEquals(minute, prefCal.get(Calendar.MINUTE));
		assertFalse("Second not updated",
				calendar.get(Calendar.SECOND) == prefCal.get(Calendar.SECOND));
		assertEquals(second, prefCal.get(Calendar.SECOND));
	}

	public void testIsDuringBlackoutPM() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 13);
		calendar.set(Calendar.SECOND, 54);
		Date messageDate = calendar.getTime();

		Time blackoutStart = Time.valueOf("23:00:00");
		Time blackoutEnd = Time.valueOf("06:00:00");

		Blackout blackout = new Blackout(blackoutStart, blackoutEnd);

		expect(contextService.getMotechService()).andReturn(motechService);
		expect(motechService.getBlackoutSettings()).andReturn(blackout);

		replay(contextService, adminService, motechService);

		boolean duringBlackout = regBean.isDuringBlackout(messageDate);

		verify(contextService, adminService, motechService);

		assertEquals(true, duringBlackout);

	}

	public void testIsDuringBlackoutAM() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 3);
		calendar.set(Calendar.MINUTE, 13);
		calendar.set(Calendar.SECOND, 54);
		Date messageDate = calendar.getTime();

		Time blackoutStart = Time.valueOf("23:00:00");
		Time blackoutEnd = Time.valueOf("06:00:00");

		Blackout blackout = new Blackout(blackoutStart, blackoutEnd);

		expect(contextService.getMotechService()).andReturn(motechService);
		expect(motechService.getBlackoutSettings()).andReturn(blackout);

		replay(contextService, adminService, motechService);

		boolean duringBlackout = regBean.isDuringBlackout(messageDate);

		verify(contextService, adminService, motechService);

		assertEquals(true, duringBlackout);

	}

	public void testSchedulingInfoMessageWithExistingScheduled() {
		String messageKey = "message", messageKeyA = "message.a", messageKeyB = "message.b", messageKeyC = "message.c";
		Date currentDate = new Date();
		Date messageDate = new Date(System.currentTimeMillis() + 5 * 1000);
		boolean userPreferenceBased = true;

		Integer personId = 1;
		Person person = new Person(personId);
		PersonAttributeType mediaTypeAttr = new PersonAttributeType();
		mediaTypeAttr.setName(MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE);
		person.addAttribute(new PersonAttribute(mediaTypeAttr, MediaType.VOICE
				.toString()));

		MessageProgramEnrollment enrollment = new MessageProgramEnrollment();
		enrollment.setPersonId(personId);

		MessageDefinition messageDef = new MessageDefinition(messageKey, 1L,
				MessageType.INFORMATIONAL);

		List<Message> messagesToRemove = new ArrayList<Message>();

		List<ScheduledMessage> existingMessages = new ArrayList<ScheduledMessage>();
		ScheduledMessage schedMessage = new ScheduledMessage();
		existingMessages.add(schedMessage);

		Capture<ScheduledMessage> capturedScheduledMessage = new Capture<ScheduledMessage>();

		expect(contextService.getPersonService()).andReturn(personService);
		expect(contextService.getMotechService()).andReturn(motechService)
				.atLeastOnce();
		expect(personService.getPerson(personId)).andReturn(person);
		expect(motechService.getMessageDefinition(messageKey)).andReturn(
				messageDef);
		expect(
				motechService.getMessages(personId, enrollment, messageDef,
						messageDate, MessageStatus.SHOULD_ATTEMPT)).andReturn(
				messagesToRemove);
		expect(
				motechService.getScheduledMessages(personId, messageDef,
						enrollment, messageDate)).andReturn(existingMessages);
		expect(
				motechService
						.saveScheduledMessage(capture(capturedScheduledMessage)))
				.andReturn(new ScheduledMessage());

		replay(contextService, adminService, motechService, personService);

		regBean.scheduleInfoMessages(messageKey, messageKeyA, messageKeyB,
				messageKeyC, enrollment, messageDate, userPreferenceBased,
				currentDate);

		verify(contextService, adminService, motechService, personService);

		ScheduledMessage scheduledMessage = capturedScheduledMessage.getValue();
		List<Message> attempts = scheduledMessage.getMessageAttempts();
		assertEquals(1, attempts.size());
		Message message = attempts.get(0);
		assertEquals(MessageStatus.SHOULD_ATTEMPT, message.getAttemptStatus());
		assertEquals(messageDate, message.getAttemptDate());
	}

	public void testSchedulingInfoMessageWithExistingMatchingMessage() {
		String messageKey = "message", messageKeyA = "message.a", messageKeyB = "message.b", messageKeyC = "message.c";
		Date currentDate = new Date();
		Date messageDate = new Date(System.currentTimeMillis() + 5 * 1000);
		boolean userPreferenceBased = true;

		Integer personId = 1;
		Person person = new Person(personId);
		PersonAttributeType mediaTypeAttr = new PersonAttributeType();
		mediaTypeAttr.setName(MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE);
		person.addAttribute(new PersonAttribute(mediaTypeAttr, MediaType.VOICE
				.toString()));

		MessageProgramEnrollment enrollment = new MessageProgramEnrollment();
		enrollment.setPersonId(personId);

		MessageDefinition messageDef = new MessageDefinition(messageKey, 1L,
				MessageType.INFORMATIONAL);

		List<Message> messagesToRemove = new ArrayList<Message>();

		List<ScheduledMessage> existingMessages = new ArrayList<ScheduledMessage>();
		ScheduledMessage schedMessage = new ScheduledMessage();
		Message msg = messageDef.createMessage(schedMessage);
		msg.setAttemptDate(new Timestamp(messageDate.getTime()));
		schedMessage.getMessageAttempts().add(msg);
		existingMessages.add(schedMessage);

		expect(contextService.getPersonService()).andReturn(personService);
		expect(contextService.getMotechService()).andReturn(motechService)
				.atLeastOnce();
		expect(personService.getPerson(personId)).andReturn(person);
		expect(motechService.getMessageDefinition(messageKey)).andReturn(
				messageDef);
		expect(
				motechService.getMessages(personId, enrollment, messageDef,
						messageDate, MessageStatus.SHOULD_ATTEMPT)).andReturn(
				messagesToRemove);
		expect(
				motechService.getScheduledMessages(personId, messageDef,
						enrollment, messageDate)).andReturn(existingMessages);

		replay(contextService, adminService, motechService, personService);

		regBean.scheduleInfoMessages(messageKey, messageKeyA, messageKeyB,
				messageKeyC, enrollment, messageDate, userPreferenceBased,
				currentDate);

		verify(contextService, adminService, motechService, personService);
	}

	public void testSchedulingInfoMessageWithExistingPendingMessage() {
		String messageKey = "message", messageKeyA = "message.a", messageKeyB = "message.b", messageKeyC = "message.c";
		Date currentDate = new Date();
		Date messageDate = new Date(System.currentTimeMillis() + 5 * 1000);
		boolean userPreferenceBased = true;

		Integer personId = 1;
		Person person = new Person(personId);
		PersonAttributeType mediaTypeAttr = new PersonAttributeType();
		mediaTypeAttr.setName(MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE);
		person.addAttribute(new PersonAttribute(mediaTypeAttr, MediaType.VOICE
				.toString()));

		MessageProgramEnrollment enrollment = new MessageProgramEnrollment();
		enrollment.setPersonId(personId);

		MessageDefinition messageDef = new MessageDefinition(messageKey, 1L,
				MessageType.INFORMATIONAL);

		List<Message> messagesToRemove = new ArrayList<Message>();

		List<ScheduledMessage> existingMessages = new ArrayList<ScheduledMessage>();
		ScheduledMessage schedMessage = new ScheduledMessage();
		Message msg = messageDef.createMessage(schedMessage);
		msg.setAttemptDate(new Timestamp(messageDate.getTime()));
		msg.setAttemptStatus(MessageStatus.ATTEMPT_PENDING);
		schedMessage.getMessageAttempts().add(msg);
		existingMessages.add(schedMessage);

		expect(contextService.getPersonService()).andReturn(personService);
		expect(contextService.getMotechService()).andReturn(motechService)
				.atLeastOnce();
		expect(personService.getPerson(personId)).andReturn(person);
		expect(motechService.getMessageDefinition(messageKey)).andReturn(
				messageDef);
		expect(
				motechService.getMessages(personId, enrollment, messageDef,
						messageDate, MessageStatus.SHOULD_ATTEMPT)).andReturn(
				messagesToRemove);
		expect(
				motechService.getScheduledMessages(personId, messageDef,
						enrollment, messageDate)).andReturn(existingMessages);

		replay(contextService, adminService, motechService, personService);

		regBean.scheduleInfoMessages(messageKey, messageKeyA, messageKeyB,
				messageKeyC, enrollment, messageDate, userPreferenceBased,
				currentDate);

		verify(contextService, adminService, motechService, personService);
	}

	public void testSchedulingInfoMessageWithExistingDeliveredMessage() {
		String messageKey = "message", messageKeyA = "message.a", messageKeyB = "message.b", messageKeyC = "message.c";
		Date currentDate = new Date();
		Date messageDate = new Date(System.currentTimeMillis() + 5 * 1000);
		boolean userPreferenceBased = true;

		Integer personId = 1;
		Person person = new Person(personId);
		PersonAttributeType mediaTypeAttr = new PersonAttributeType();
		mediaTypeAttr.setName(MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE);
		person.addAttribute(new PersonAttribute(mediaTypeAttr, MediaType.VOICE
				.toString()));

		MessageProgramEnrollment enrollment = new MessageProgramEnrollment();
		enrollment.setPersonId(personId);

		MessageDefinition messageDef = new MessageDefinition(messageKey, 1L,
				MessageType.INFORMATIONAL);

		List<Message> messagesToRemove = new ArrayList<Message>();

		List<ScheduledMessage> existingMessages = new ArrayList<ScheduledMessage>();
		ScheduledMessage schedMessage = new ScheduledMessage();
		Message msg = messageDef.createMessage(schedMessage);
		msg.setAttemptDate(new Timestamp(messageDate.getTime()));
		msg.setAttemptStatus(MessageStatus.DELIVERED);
		schedMessage.getMessageAttempts().add(msg);
		existingMessages.add(schedMessage);

		expect(contextService.getPersonService()).andReturn(personService);
		expect(contextService.getMotechService()).andReturn(motechService)
				.atLeastOnce();
		expect(personService.getPerson(personId)).andReturn(person);
		expect(motechService.getMessageDefinition(messageKey)).andReturn(
				messageDef);
		expect(
				motechService.getMessages(personId, enrollment, messageDef,
						messageDate, MessageStatus.SHOULD_ATTEMPT)).andReturn(
				messagesToRemove);
		expect(
				motechService.getScheduledMessages(personId, messageDef,
						enrollment, messageDate)).andReturn(existingMessages);

		replay(contextService, adminService, motechService, personService);

		regBean.scheduleInfoMessages(messageKey, messageKeyA, messageKeyB,
				messageKeyC, enrollment, messageDate, userPreferenceBased,
				currentDate);

		verify(contextService, adminService, motechService, personService);
	}

	public void testSchedulingInfoMessageWithExistingRejectedMessage() {
		String messageKey = "message", messageKeyA = "message.a", messageKeyB = "message.b", messageKeyC = "message.c";
		Date currentDate = new Date();
		Date messageDate = new Date(System.currentTimeMillis() + 5 * 1000);
		boolean userPreferenceBased = true;

		Integer personId = 1;
		Person person = new Person(personId);
		PersonAttributeType mediaTypeAttr = new PersonAttributeType();
		mediaTypeAttr.setName(MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE);
		person.addAttribute(new PersonAttribute(mediaTypeAttr, MediaType.VOICE
				.toString()));

		MessageProgramEnrollment enrollment = new MessageProgramEnrollment();
		enrollment.setPersonId(personId);

		MessageDefinition messageDef = new MessageDefinition(messageKey, 1L,
				MessageType.INFORMATIONAL);

		List<Message> messagesToRemove = new ArrayList<Message>();

		List<ScheduledMessage> existingMessages = new ArrayList<ScheduledMessage>();
		ScheduledMessage schedMessage = new ScheduledMessage();
		Message msg = messageDef.createMessage(schedMessage);
		msg.setAttemptDate(new Timestamp(messageDate.getTime()));
		msg.setAttemptStatus(MessageStatus.REJECTED);
		schedMessage.getMessageAttempts().add(msg);
		existingMessages.add(schedMessage);

		expect(contextService.getPersonService()).andReturn(personService);
		expect(contextService.getMotechService()).andReturn(motechService)
				.atLeastOnce();
		expect(personService.getPerson(personId)).andReturn(person);
		expect(motechService.getMessageDefinition(messageKey)).andReturn(
				messageDef);
		expect(
				motechService.getMessages(personId, enrollment, messageDef,
						messageDate, MessageStatus.SHOULD_ATTEMPT)).andReturn(
				messagesToRemove);
		expect(
				motechService.getScheduledMessages(personId, messageDef,
						enrollment, messageDate)).andReturn(existingMessages);

		replay(contextService, adminService, motechService, personService);

		regBean.scheduleInfoMessages(messageKey, messageKeyA, messageKeyB,
				messageKeyC, enrollment, messageDate, userPreferenceBased,
				currentDate);

		verify(contextService, adminService, motechService, personService);
	}

	public void testSchedulingInfoMessageWithExistingCancelledMessage() {
		String messageKey = "message", messageKeyA = "message.a", messageKeyB = "message.b", messageKeyC = "message.c";
		Date currentDate = new Date();
		Date messageDate = new Date(System.currentTimeMillis() + 5 * 1000);
		boolean userPreferenceBased = true;

		Integer personId = 1;
		Person person = new Person(personId);
		PersonAttributeType mediaTypeAttr = new PersonAttributeType();
		mediaTypeAttr.setName(MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE);
		person.addAttribute(new PersonAttribute(mediaTypeAttr, MediaType.VOICE
				.toString()));

		MessageProgramEnrollment enrollment = new MessageProgramEnrollment();
		enrollment.setPersonId(personId);

		MessageDefinition messageDef = new MessageDefinition(messageKey, 1L,
				MessageType.INFORMATIONAL);

		List<Message> messagesToRemove = new ArrayList<Message>();

		List<ScheduledMessage> existingMessages = new ArrayList<ScheduledMessage>();
		ScheduledMessage schedMessage = new ScheduledMessage();
		Message msg = messageDef.createMessage(schedMessage);
		msg.setAttemptDate(new Timestamp(messageDate.getTime()));
		msg.setAttemptStatus(MessageStatus.CANCELLED);
		schedMessage.getMessageAttempts().add(msg);
		existingMessages.add(schedMessage);

		Capture<ScheduledMessage> capturedScheduledMessage = new Capture<ScheduledMessage>();

		expect(contextService.getPersonService()).andReturn(personService);
		expect(contextService.getMotechService()).andReturn(motechService)
				.atLeastOnce();
		expect(personService.getPerson(personId)).andReturn(person);
		expect(motechService.getMessageDefinition(messageKey)).andReturn(
				messageDef);
		expect(
				motechService.getMessages(personId, enrollment, messageDef,
						messageDate, MessageStatus.SHOULD_ATTEMPT)).andReturn(
				messagesToRemove);
		expect(
				motechService.getScheduledMessages(personId, messageDef,
						enrollment, messageDate)).andReturn(existingMessages);

		expect(
				motechService
						.saveScheduledMessage(capture(capturedScheduledMessage)))
				.andReturn(new ScheduledMessage());

		replay(contextService, adminService, motechService, personService);

		regBean.scheduleInfoMessages(messageKey, messageKeyA, messageKeyB,
				messageKeyC, enrollment, messageDate, userPreferenceBased,
				currentDate);

		verify(contextService, adminService, motechService, personService);

		ScheduledMessage scheduledMessage = capturedScheduledMessage.getValue();
		List<Message> attempts = scheduledMessage.getMessageAttempts();
		assertEquals(2, attempts.size());
		Message message1 = attempts.get(0);
		assertEquals(MessageStatus.CANCELLED, message1.getAttemptStatus());
		assertEquals(messageDate, message1.getAttemptDate());
		Message message2 = attempts.get(1);
		assertEquals(MessageStatus.SHOULD_ATTEMPT, message2.getAttemptStatus());
		assertEquals(messageDate, message2.getAttemptDate());
	}

	public void testSchedulingInfoMessageWithExistingFailedMessage() {
		String messageKey = "message", messageKeyA = "message.a", messageKeyB = "message.b", messageKeyC = "message.c";
		Date currentDate = new Date();
		Date messageDate = new Date(System.currentTimeMillis() + 5 * 1000);
		boolean userPreferenceBased = true;

		Integer personId = 1;
		Person person = new Person(personId);
		PersonAttributeType mediaTypeAttr = new PersonAttributeType();
		mediaTypeAttr.setName(MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE);
		person.addAttribute(new PersonAttribute(mediaTypeAttr, MediaType.VOICE
				.toString()));

		MessageProgramEnrollment enrollment = new MessageProgramEnrollment();
		enrollment.setPersonId(personId);

		MessageDefinition messageDef = new MessageDefinition(messageKey, 1L,
				MessageType.INFORMATIONAL);

		List<Message> messagesToRemove = new ArrayList<Message>();

		List<ScheduledMessage> existingMessages = new ArrayList<ScheduledMessage>();
		ScheduledMessage schedMessage = new ScheduledMessage();
		Message msg = messageDef.createMessage(schedMessage);
		msg.setAttemptDate(new Timestamp(messageDate.getTime()));
		msg.setAttemptStatus(MessageStatus.ATTEMPT_FAIL);
		schedMessage.getMessageAttempts().add(msg);
		existingMessages.add(schedMessage);

		Capture<ScheduledMessage> capturedScheduledMessage = new Capture<ScheduledMessage>();

		expect(contextService.getPersonService()).andReturn(personService);
		expect(contextService.getMotechService()).andReturn(motechService)
				.atLeastOnce();
		expect(personService.getPerson(personId)).andReturn(person);
		expect(motechService.getMessageDefinition(messageKey)).andReturn(
				messageDef);
		expect(
				motechService.getMessages(personId, enrollment, messageDef,
						messageDate, MessageStatus.SHOULD_ATTEMPT)).andReturn(
				messagesToRemove);
		expect(
				motechService.getScheduledMessages(personId, messageDef,
						enrollment, messageDate)).andReturn(existingMessages);

		expect(
				motechService
						.saveScheduledMessage(capture(capturedScheduledMessage)))
				.andReturn(new ScheduledMessage());

		replay(contextService, adminService, motechService, personService);

		regBean.scheduleInfoMessages(messageKey, messageKeyA, messageKeyB,
				messageKeyC, enrollment, messageDate, userPreferenceBased,
				currentDate);

		verify(contextService, adminService, motechService, personService);

		ScheduledMessage scheduledMessage = capturedScheduledMessage.getValue();
		List<Message> attempts = scheduledMessage.getMessageAttempts();
		assertEquals(2, attempts.size());
		Message message1 = attempts.get(0);
		assertEquals(MessageStatus.ATTEMPT_FAIL, message1.getAttemptStatus());
		assertEquals(messageDate, message1.getAttemptDate());
		Message message2 = attempts.get(1);
		assertEquals(MessageStatus.SHOULD_ATTEMPT, message2.getAttemptStatus());
		assertEquals(messageDate, message2.getAttemptDate());
	}
}
