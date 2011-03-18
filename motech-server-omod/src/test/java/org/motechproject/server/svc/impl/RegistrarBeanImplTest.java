/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010-11 The Trustees of Columbia University in the City of
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

import junit.framework.TestCase;
import org.easymock.Capture;
import org.motechproject.server.model.*;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.MotechService;
import org.motechproject.server.svc.RCTService;
import org.motechproject.server.util.MotechConstants;
import org.motechproject.ws.DayOfWeek;
import org.motechproject.ws.MediaType;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PersonService;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.easymock.EasyMock.*;

public class RegistrarBeanImplTest extends TestCase {

    RegistrarBeanImpl registrarBean;

    ContextService contextService;
    AdministrationService adminService;
    MotechService motechService;
    PersonService personService;
    RCTService rctService;

    @Override
    protected void setUp() throws Exception {
        contextService = createMock(ContextService.class);
        adminService = createMock(AdministrationService.class);
        motechService = createMock(MotechService.class);
        personService = createMock(PersonService.class);
        rctService = createMock(RCTService.class);

        registrarBean = new RegistrarBeanImpl();
        registrarBean.setContextService(contextService);
        registrarBean.setAdministrationService(adminService);
        registrarBean.setPersonService(personService);
        registrarBean.setRctService(rctService);
    }

    @Override
    protected void tearDown() throws Exception {
        registrarBean = null;
        contextService = null;
        adminService = null;
        motechService = null;
        personService = null;
    }

    public void testFindPersonPreferredMessageDate() {
        DayOfWeek day = DayOfWeek.MONDAY;
        String timeAsString = "09:00";
        Date messageDate = new Date();
        Person person = createPersonWithDateTimePreferences(day, timeAsString);
        Date preferredDate = registrarBean.findPreferredMessageDate(person, messageDate, messageDate, true);
        Calendar messageCalendar = getCalendar(messageDate);
        Calendar preferenceCalendar = getCalendar(preferredDate);

        assertTrue(preferenceCalendar.after(messageCalendar));
        assertEquals(day.getCalendarValue(), preferenceCalendar.get(Calendar.DAY_OF_WEEK));
        assertEquals(9, preferenceCalendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, preferenceCalendar.get(Calendar.MINUTE));
        assertEquals(0, preferenceCalendar.get(Calendar.SECOND));
    }

    private Calendar getCalendar(Date messageDate) {
        Calendar messageCalendar = Calendar.getInstance();
        messageCalendar.setTime(messageDate);
        return messageCalendar;
    }

    private Person createPersonWithDateTimePreferences(DayOfWeek day, String timeAsString) {
        Person person = new Person(1);
        PersonAttributeType dayType = new PersonAttributeType(1);
        dayType.setName(MotechConstants.PERSON_ATTRIBUTE_DELIVERY_DAY);
        PersonAttributeType timeType = new PersonAttributeType(2);
        timeType.setName(MotechConstants.PERSON_ATTRIBUTE_DELIVERY_TIME);
        person.addAttribute(new PersonAttribute(dayType, day.toString()));
        person.addAttribute(new PersonAttribute(timeType, timeAsString));
        return person;
    }

    public void testDetermineDefaultPrefDate() {
        DayOfWeek day = DayOfWeek.MONDAY;
        String timeAsString = "09:00";
        Date messageDate = new Date();

        Person person = new Person(1);

        expect(adminService.getGlobalProperty(MotechConstants.GLOBAL_PROPERTY_DAY_OF_WEEK)).andReturn(day.toString());
        expect(adminService.getGlobalProperty(MotechConstants.GLOBAL_PROPERTY_TIME_OF_DAY)).andReturn(timeAsString);
        replay(contextService, adminService);

        Date prefDate = registrarBean.findPreferredMessageDate(person, messageDate, messageDate, true);

        verify(contextService, adminService);

        Calendar messageCalendar = getCalendar(messageDate);
        Calendar preferredCalendar = getCalendar(prefDate);

        assertTrue(preferredCalendar.after(messageCalendar));
        assertEquals(day.getCalendarValue(), preferredCalendar.get(Calendar.DAY_OF_WEEK));
        assertEquals(9, preferredCalendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, preferredCalendar.get(Calendar.MINUTE));
        assertEquals(0, preferredCalendar.get(Calendar.SECOND));
    }

    public void testFindNoPreferenceDate() {
        Date messageDate = new Date();
        Person person = new Person(1);

        expect(adminService.getGlobalProperty(MotechConstants.GLOBAL_PROPERTY_DAY_OF_WEEK)).andReturn(null);
        expect(adminService.getGlobalProperty(MotechConstants.GLOBAL_PROPERTY_TIME_OF_DAY)).andReturn(null);
        replay(contextService, adminService);

        Date prefDate = registrarBean.findPreferredMessageDate(person, messageDate, messageDate, true);

        verify(contextService, adminService);

        Calendar messageCal = getCalendar(messageDate);
        Calendar prefCal = getCalendar(prefDate);

        assertEquals(messageCal.get(Calendar.YEAR), prefCal.get(Calendar.YEAR));
        assertEquals(messageCal.get(Calendar.MONTH), prefCal.get(Calendar.MONTH));
        assertEquals(messageCal.get(Calendar.DATE), prefCal.get(Calendar.DATE));
        assertEquals(messageCal.get(Calendar.HOUR_OF_DAY), prefCal.get(Calendar.HOUR_OF_DAY));
        assertEquals(messageCal.get(Calendar.MINUTE), prefCal.get(Calendar.MINUTE));
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

        Date prefDate = registrarBean.adjustTime(messageDate, timeCalendar.getTime());

        Calendar prefCal = getCalendar(prefDate);

        assertEquals(calendar.get(Calendar.YEAR), prefCal.get(Calendar.YEAR));
        assertEquals(calendar.get(Calendar.MONTH), prefCal.get(Calendar.MONTH));
        assertEquals(calendar.get(Calendar.DATE), prefCal.get(Calendar.DATE));
        assertFalse("Hour not updated", calendar.get(Calendar.HOUR_OF_DAY) == prefCal.get(Calendar.HOUR_OF_DAY));
        assertEquals(hour, prefCal.get(Calendar.HOUR_OF_DAY));
        assertFalse("Minute not updated", calendar.get(Calendar.MINUTE) == prefCal.get(Calendar.MINUTE));
        assertEquals(minute, prefCal.get(Calendar.MINUTE));
        assertFalse("Second not updated", calendar.get(Calendar.SECOND) == prefCal.get(Calendar.SECOND));
        assertEquals(second, prefCal.get(Calendar.SECOND));
    }

    public void testAdjustDateBlackoutAM() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 2);
        calendar.set(Calendar.MINUTE, 13);
        calendar.set(Calendar.SECOND, 54);

        Date messageDate = calendar.getTime();

        expect(contextService.getMotechService()).andReturn(motechService);
        expect(motechService.getBlackoutSettings()).andReturn(new Blackout(Time.valueOf("22:00:00"), Time.valueOf("06:00:00")));
        replay(contextService, adminService, motechService);

        Date prefDate = registrarBean.adjustForBlackout(messageDate);
        verify(contextService, adminService, motechService);

        Calendar prefCal = getCalendar(prefDate);

        assertFalse("Hour not updated", calendar.get(Calendar.HOUR_OF_DAY) == prefCal.get(Calendar.HOUR_OF_DAY));
        assertEquals(6, prefCal.get(Calendar.HOUR_OF_DAY));
        assertFalse("Minute not updated", calendar.get(Calendar.MINUTE) == prefCal.get(Calendar.MINUTE));
        assertEquals(0, prefCal.get(Calendar.MINUTE));
        assertFalse("Second not updated", calendar.get(Calendar.SECOND) == prefCal.get(Calendar.SECOND));
        assertEquals(0, prefCal.get(Calendar.SECOND));
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

        Date prefDate = registrarBean.adjustForBlackout(messageDate);

        verify(contextService, adminService, motechService);

        Calendar prefCal = getCalendar(prefDate);

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

        boolean duringBlackout = registrarBean.isDuringBlackout(messageDate);

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

        boolean duringBlackout = registrarBean.isDuringBlackout(messageDate);

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

        registrarBean.scheduleInfoMessages(messageKey, messageKeyA, messageKeyB,
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

        registrarBean.scheduleInfoMessages(messageKey, messageKeyA, messageKeyB,
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

        registrarBean.scheduleInfoMessages(messageKey, messageKeyA, messageKeyB,
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

        registrarBean.scheduleInfoMessages(messageKey, messageKeyA, messageKeyB,
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

        registrarBean.scheduleInfoMessages(messageKey, messageKeyA, messageKeyB,
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

        registrarBean.scheduleInfoMessages(messageKey, messageKeyA, messageKeyB,
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

        registrarBean.scheduleInfoMessages(messageKey, messageKeyA, messageKeyB,
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

    public void testFilteringStaffCareMessages() {
        Patient p1 = new Patient(5716);
        Patient p2 = new Patient(5717);
        Patient p3 = new Patient(5718);
        expect(rctService.isPatientRegisteredAndInControlGroup(p1)).andReturn(false);
        expect(rctService.isPatientRegisteredAndInControlGroup(p1)).andReturn(false);
        expect(rctService.isPatientRegisteredAndInControlGroup(p2)).andReturn(false);
        expect(rctService.isPatientRegisteredAndInControlGroup(p2)).andReturn(false);
        expect(rctService.isPatientRegisteredAndInControlGroup(p3)).andReturn(false);
        expect(rctService.isPatientRegisteredAndInControlGroup(p3)).andReturn(false);

        ExpectedObs obs1 = new ExpectedObs();
        obs1.setPatient(p1);

        ExpectedObs obs2 = new ExpectedObs();
        obs2.setPatient(p2);

        ExpectedObs obs3 = new ExpectedObs();
        obs3.setPatient(p3);

        ExpectedEncounter enc1 = new ExpectedEncounter();
        enc1.setPatient(p1);

        ExpectedEncounter enc2 = new ExpectedEncounter();
        enc2.setPatient(p2);

        ExpectedEncounter enc3 = new ExpectedEncounter();
        enc3.setPatient(p3);


        List<ExpectedObs> expObs = new ArrayList<ExpectedObs>();
        List<ExpectedEncounter> expEnc = new ArrayList<ExpectedEncounter>();

        expObs.add(obs1);
        expObs.add(obs2);
        expObs.add(obs3);

        expEnc.add(enc1);
        expEnc.add(enc2);
        expEnc.add(enc3);

        replay(rctService);
        List<ExpectedObs> filteredObs = registrarBean.filterRCTObs(new ArrayList(expObs));
        List<ExpectedEncounter> filteredEnc = registrarBean.filterRCTEncounters(new ArrayList(expEnc));

        verify(rctService);

        assertEquals(3, filteredObs.size());
        ExpectedObs obs = filteredObs.get(0);
        assertEquals(p1.getPatientId(), obs.getPatient().getPatientId());

        assertEquals(3, filteredEnc.size());
        ExpectedEncounter enc = filteredEnc.get(0);
        assertEquals(p1.getPatientId(), enc.getPatient().getPatientId());

    }
}
