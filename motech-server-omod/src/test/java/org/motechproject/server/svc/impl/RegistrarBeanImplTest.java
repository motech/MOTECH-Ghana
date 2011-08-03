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
import org.apache.commons.lang.time.DateUtils;
import org.easymock.Capture;
import org.motechproject.server.filters.ExpectedEncounterFilterChain;
import org.motechproject.server.filters.ExpectedObsFilterChain;
import org.motechproject.server.filters.Filter;
import org.motechproject.server.model.*;
import org.motechproject.server.omod.MotechUserRepository;
import org.motechproject.server.omod.PersonAttributeTypeEnum;
import org.motechproject.server.service.ContextService;
import org.motechproject.server.service.MotechService;
import org.motechproject.server.svc.RCTService;
import org.motechproject.server.util.DateUtil;
import org.motechproject.server.util.MotechConstants;
import org.motechproject.ws.DayOfWeek;
import org.motechproject.ws.Gender;
import org.motechproject.ws.MediaType;
import org.motechproject.ws.mobile.MessageService;
import org.openmrs.*;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;

import static org.easymock.EasyMock.*;

public class RegistrarBeanImplTest extends TestCase {

    private RegistrarBeanImpl registrarBean;

    private ContextService contextService;
    private AdministrationService adminService;
    private MotechService motechService;
    private PersonService personService;
    private RCTService rctService;
    private MessageService mobileService;


    @Override
    protected void setUp() throws Exception {
        contextService = createMock(ContextService.class);
        adminService = createMock(AdministrationService.class);
        motechService = createMock(MotechService.class);
        personService = createMock(PersonService.class);

        rctService = createMock(RCTService.class);
        mobileService = createMock(MessageService.class);
        registrarBean = new RegistrarBeanImpl();
        registrarBean.setContextService(contextService);
        registrarBean.setAdministrationService(adminService);
        registrarBean.setPersonService(personService);
        registrarBean.setRctService(rctService);
        registrarBean.setMobileService(mobileService);
        ExpectedEncounterFilterChain expectedEncounterFilterChain = new ExpectedEncounterFilterChain();
        expectedEncounterFilterChain.setFilters(new ArrayList<Filter<ExpectedEncounter>>());
        ExpectedObsFilterChain expectedObsFilterChain = new ExpectedObsFilterChain();
        expectedObsFilterChain.setFilters(new ArrayList<Filter<ExpectedObs>>());
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
        Calendar calendar = getCalendarWithTime(14, 13, 54);
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

    public void testAdjustDateBlackoutInTheMorning() {
        Calendar calendar = getCalendarWithTime(2, 13, 54);
        Date morningMessageTime = calendar.getTime();

        expect(contextService.getMotechService()).andReturn(motechService);
        expect(motechService.getBlackoutSettings()).andReturn(new Blackout(Time.valueOf("22:00:00"), Time.valueOf("06:00:00")));
        replay(contextService, adminService, motechService);

        Date preferredDate = registrarBean.adjustDateForBlackout(morningMessageTime);
        verify(contextService, adminService, motechService);

        Calendar preferredCalendar = getCalendar(preferredDate);

        assertFalse("Hour not updated", calendar.get(Calendar.HOUR_OF_DAY) == preferredCalendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(6, preferredCalendar.get(Calendar.HOUR_OF_DAY));
        assertFalse("Minute not updated", calendar.get(Calendar.MINUTE) == preferredCalendar.get(Calendar.MINUTE));
        assertEquals(0, preferredCalendar.get(Calendar.MINUTE));
        assertFalse("Second not updated", calendar.get(Calendar.SECOND) == preferredCalendar.get(Calendar.SECOND));
        assertEquals(0, preferredCalendar.get(Calendar.SECOND));
    }


    public void testAdjustDateBlackoutInTheNight() {
        Calendar calendar = getCalendarWithTime(22, 13, 54);
        Date eveningMessageTime = calendar.getTime();
        Blackout blackout = new Blackout(Time.valueOf("22:00:00"), Time.valueOf("06:00:00"));

        expect(contextService.getMotechService()).andReturn(motechService);
        expect(motechService.getBlackoutSettings()).andReturn(blackout);
        replay(contextService, adminService, motechService);

        Date prefDate = registrarBean.adjustDateForBlackout(eveningMessageTime);

        verify(contextService, adminService, motechService);
        Calendar preferredCalendar = getCalendar(prefDate);
        assertFalse("Hour not updated", calendar.get(Calendar.HOUR_OF_DAY) == preferredCalendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(6, preferredCalendar.get(Calendar.HOUR_OF_DAY));
        assertFalse("Minute not updated", calendar.get(Calendar.MINUTE) == preferredCalendar.get(Calendar.MINUTE));
        assertEquals(0, preferredCalendar.get(Calendar.MINUTE));
        assertFalse("Second not updated", calendar.get(Calendar.SECOND) == preferredCalendar.get(Calendar.SECOND));
        assertEquals(0, preferredCalendar.get(Calendar.SECOND));
    }

    private Calendar getCalendarWithTime(int hourOfTheDay, int minutes, int seconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfTheDay);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, seconds);
        return calendar;
    }



    public void testSchedulingInfoMessageWithExistingScheduled() {
        String messageKey = "message";
        String messageKeyA = "message.a";
        String messageKeyB = "message.b";
        String messageKeyC = "message.c";

        Date currentDate = new Date();
        Date messageDate = new Date(System.currentTimeMillis() + 5 * 1000);
        boolean userPreferenceBased = true;

        Integer personId = 1;
        Person person = new Person(personId);
        PersonAttributeType mediaTypeAttribute = new PersonAttributeType();
        mediaTypeAttribute.setName(MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE);
        person.addAttribute(new PersonAttribute(mediaTypeAttribute, MediaType.VOICE.toString()));

        MessageProgramEnrollment enrollment = new MessageProgramEnrollment();
        enrollment.setPersonId(personId);

        MessageDefinition messageDef = new MessageDefinition(messageKey, 1L, MessageType.INFORMATIONAL);
        List<Message> messagesToRemove = new ArrayList<Message>();

        List<ScheduledMessage> existingMessages = new ArrayList<ScheduledMessage>();
        ScheduledMessage scheduledMessage = new ScheduledMessage();
        existingMessages.add(scheduledMessage);

        Capture<ScheduledMessage> capturedScheduledMessage = new Capture<ScheduledMessage>();

        expect(contextService.getMotechService()).andReturn(motechService).atLeastOnce();
        expect(personService.getPerson(personId)).andReturn(person);
        expect(motechService.getMessageDefinition(messageKey)).andReturn(messageDef);
        expect(motechService.getMessages(personId, enrollment, messageDef, messageDate, MessageStatus.SHOULD_ATTEMPT)).
                andReturn(messagesToRemove);
        expect(motechService.getScheduledMessages(personId, messageDef, enrollment, messageDate)).andReturn(existingMessages);
        expect(motechService.saveScheduledMessage(capture(capturedScheduledMessage))).andReturn(new ScheduledMessage());

        replay(contextService, adminService, motechService, personService);

        registrarBean.scheduleInfoMessages(messageKey, messageKeyA, messageKeyB,
                messageKeyC, enrollment, messageDate, userPreferenceBased,
                currentDate);

        verify(contextService, adminService, motechService, personService);

        ScheduledMessage scheduledMsg = capturedScheduledMessage.getValue();
        List<Message> attempts = scheduledMsg.getMessageAttempts();
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

    public void testIsValidOutPatientVisitEntryShouldReturnTrueWhenAnEntryAlreadyDoesNotExists() {
        Integer facilityId = 2;
        Date date = new Date();
        String serialNumber = "01/2010";

        Gender sex = Gender.MALE;
        Date dob = new Date();
        Boolean insured = false;
        Integer diagnosis = 1;
        Boolean newCase = true;

        expect(contextService.getMotechService()).andReturn(motechService);

        expect(motechService.getOutPatientVisitEntryBy(facilityId, serialNumber, sex, dob, newCase, diagnosis)).andReturn(null);

        replay(motechService, contextService);
        assertTrue(registrarBean.isValidOutPatientVisitEntry(facilityId, date, serialNumber, sex, dob, newCase, diagnosis));
    }

    public void testIsValidOutPatientVisitEntryShouldReturnFalseWhenAnEntryAlreadyExists() {
        Integer facilityId = 2;
        Date visitDate = new Date();
        String serialNumber = "01/2010";

        Gender sex = Gender.MALE;
        Date dob = new Date();
        Integer diagnosis = 1;
        Boolean newCase = true;


        expect(contextService.getMotechService()).andReturn(motechService);

        GeneralOutpatientEncounter generalOutpatientEncounter = new GeneralOutpatientEncounter();
        generalOutpatientEncounter.setVisitDate(visitDate);

        expect(motechService.getOutPatientVisitEntryBy(facilityId, serialNumber, sex, dob, newCase, diagnosis))
                .andReturn(Arrays.asList(generalOutpatientEncounter));

        replay(motechService, contextService);
        assertFalse(registrarBean.isValidOutPatientVisitEntry(facilityId, visitDate, serialNumber, sex, dob, newCase, diagnosis));
    }


    public void testIsInvalid_IfTheOPDVisitEntryIsDuplicateInTheSameMonth() {

        Integer facilityId = 2;
        Date visitDate = new DateUtil().dateFor(15, 6, 2011);

        String serialNumber = "01/2010";

        Gender sex = Gender.MALE;
        Date dob = new Date();
        Integer diagnosis = 1;
        Boolean newCase = true;
        Date lastMonth = DateUtils.addMonths(visitDate, -1);

        GeneralOutpatientEncounter generalOutpatientEncounter = new GeneralOutpatientEncounter();
        generalOutpatientEncounter.setVisitDate(lastMonth);


        expect(contextService.getMotechService()).andReturn(motechService);
        expect(motechService.getOutPatientVisitEntryBy(facilityId, serialNumber, sex, dob, newCase, diagnosis)).andReturn(Arrays.asList(generalOutpatientEncounter));

        replay(motechService, contextService);

        assertTrue(registrarBean.isValidOutPatientVisitEntry(facilityId, visitDate, serialNumber, sex, dob, newCase, diagnosis));
        verify(motechService, contextService);
    }

    public void test_shouldNotChangePasswordWhenStaffIsUpdated() {
        User staff = new User(1);
        staff.addName(new PersonName("James","J","J"));

        PersonAttributeType staffAttributeType = new PersonAttributeType(1);
        staffAttributeType.setName(PersonAttributeTypeEnum.PERSON_ATTRIBUTE_STAFF_TYPE.getAttributeName());

        PersonAttributeType staffPhoneAttributeType = new PersonAttributeType(2);
        staffAttributeType.setName(PersonAttributeTypeEnum.PERSON_ATTRIBUTE_PHONE_NUMBER.getAttributeName());

        UserService userService = createMock(UserService.class);

        registrarBean.setUserService(userService);
        registrarBean.setMotechUserRepository(new MotechUserRepository(null,null,personService,null));

        expect(userService.getUserByUsername("465")).andReturn(staff);
        expect(userService.saveUser(eq(staff),isNull(String.class))).andReturn(staff);
        expect(personService.
                getPersonAttributeTypeByName(PersonAttributeTypeEnum.PERSON_ATTRIBUTE_STAFF_TYPE.getAttributeName()))
                .andReturn(staffAttributeType);
        expect(personService.
                getPersonAttributeTypeByName(PersonAttributeTypeEnum.PERSON_ATTRIBUTE_PHONE_NUMBER.getAttributeName()))
                .andReturn(staffPhoneAttributeType);

        replay(userService,personService);

        User user = registrarBean.registerStaff("James", "J", "0123456789", "CHO", "465");

        verify(userService,personService);
    }


    private List<ExpectedEncounter> expectedEncountersFor(Patient... patients) {
        List<ExpectedEncounter> expectedEncounters = new ArrayList<ExpectedEncounter>();
        Long id = 1L;
        for (Patient patient : patients) {
            ExpectedEncounter encounter = new ExpectedEncounter();
            encounter.setId(id);
            encounter.setPatient(patient);
            expectedEncounters.add(encounter);
            id++;
        }
        return expectedEncounters;
    }

    private List<ExpectedObs> expectedObservationsFor(Patient... patients) {
        List<ExpectedObs> expectedObservations = new ArrayList<ExpectedObs>();
        Long id = 1L;
        for (Patient patient : patients) {
            ExpectedObs obs = new ExpectedObs();
            obs.setId(id);
            obs.setPatient(patient);
            expectedObservations.add(obs);
            id++;
        }
        return expectedObservations;
    }


    private void createType(User user, String typeValue) {
        PersonAttributeType staffTypeAttrType = PersonAttributeTypeEnum.PERSON_ATTRIBUTE_STAFF_TYPE.
                getAttributeType(personService);
        user.addAttribute(new PersonAttribute(staffTypeAttrType, typeValue));
    }

}
