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
import org.easymock.EasyMock;
import org.motechproject.server.model.*;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.MotechService;
import org.motechproject.server.omod.filters.ExpectedEncounterFilterChain;
import org.motechproject.server.omod.filters.ExpectedObsFilterChain;
import org.motechproject.server.omod.filters.Filter;
import org.motechproject.server.omod.web.model.KassenaNankana;
import org.motechproject.server.svc.RCTService;
import org.motechproject.server.util.MotechConstants;
import org.motechproject.ws.Care;
import org.motechproject.ws.CareMessageGroupingStrategy;
import org.motechproject.ws.DayOfWeek;
import org.motechproject.ws.MediaType;
import org.motechproject.ws.mobile.MessageService;
import org.openmrs.*;
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
    MessageService mobileService;

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
        registrarBean.setExpectedEncountersFilter(expectedEncounterFilterChain);
        registrarBean.setExpectedObsFilter(expectedObsFilterChain);
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

    public void testShouldFindOutIfMessageTimeIsDuringBlackoutPeriod() {
        Calendar calendar = getCalendarWithTime(23, 13, 54);
        Date morningMessageTime = calendar.getTime();
        calendar = getCalendarWithTime(3, 13, 54);
        Date nightMessageTime = calendar.getTime();
        calendar = getCalendarWithTime(19, 30, 30);
        Date eveningMessageTime = calendar.getTime();

        Blackout blackout = new Blackout(Time.valueOf("23:00:00"), Time.valueOf("06:00:00"));

        expect(contextService.getMotechService()).andReturn(motechService).times(3);
        expect(motechService.getBlackoutSettings()).andReturn(blackout).times(3);
        replay(contextService, adminService, motechService);

        assertTrue(registrarBean.isMessageTimeWithinBlackoutPeriod(morningMessageTime));
        assertTrue(registrarBean.isMessageTimeWithinBlackoutPeriod(nightMessageTime));
        assertFalse(registrarBean.isMessageTimeWithinBlackoutPeriod(eveningMessageTime));
        verify(contextService, adminService, motechService);

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

    public void testSendStaffCareMessagesGroupByCommunity() {

        Date forDate = new Date();
        String careGroups[] = {"ANC", "TT", "IPT"};

        Location location = new Location();
        location.setName("Test Facility");
        location.setRegion("Upper East");
        location.setCountyDistrict(new KassenaNankana().toString());

        Facility facility = new Facility();
        facility.setLocation(location);
        facility.setPhoneNumber("+1 555 123-1234");

        List<Facility> facilities = new ArrayList<Facility>();
        facilities.add(facility);


        Patient p = new Patient(5716);


        ExpectedEncounter enc = new ExpectedEncounter();
        enc.setPatient(p);

        List<ExpectedEncounter> encounters = new ArrayList<ExpectedEncounter>();
        List<ExpectedObs> emptyObs = new ArrayList<ExpectedObs>();

        encounters.add(enc);

        final DefaultedExpectedEncounterAlert defaultedExpectedEncounterAlert = anyObject();

        // To Mock
        expect(motechService.getCommunityByPatient(p)).andReturn(null);
        expect(contextService.getMotechService()).andReturn(motechService).anyTimes();
        expect(contextService.getAdministrationService()).andReturn(adminService).times(2);

        expect(adminService.getGlobalProperty(MotechConstants.GLOBAL_PROPERTY_MAX_QUERY_RESULTS)).andReturn("35").anyTimes();
        expect(motechService.getExpectedEncounter(null, facility, careGroups, null, null, forDate, forDate, 35)).andReturn(encounters);
        expect(motechService.getExpectedObs(null, facility, careGroups, null, null, forDate, forDate, 35)).andReturn(emptyObs);

        expect(motechService.getAllFacilities()).andReturn(facilities);
        expect(motechService.getCareConfigurationFor(EasyMock.<String>anyObject())).andReturn(EasyMock.<CareConfiguration>anyObject());
        expect(motechService.getDefaultedEncounterAlertFor(enc)).andReturn(defaultedExpectedEncounterAlert);


        Capture<String> capturedMessageId = new Capture<String>();
        Capture<String> capturedPhoneNumber = new Capture<String>();
        Capture<Care[]> capturedCares = new Capture<Care[]>();
        Capture<CareMessageGroupingStrategy> capturedStrategy = new Capture<CareMessageGroupingStrategy>();
        Capture<MediaType> capturedMediaType = new Capture<MediaType>();
        Capture<Date> capturedStartDate = new Capture<Date>();
        Capture<Date> capturedEndDate = new Capture<Date>();

        
        expect(mobileService.sendDefaulterMessage(capture(capturedMessageId), capture(capturedPhoneNumber),
                capture(capturedCares), capture(capturedStrategy),
                capture(capturedMediaType), capture(capturedStartDate),
                capture(capturedEndDate))).andReturn(org.motechproject.ws.MessageStatus.DELIVERED);

        motechService.saveorUpdateDefaultedEncounterAlert(EasyMock.<DefaultedExpectedEncounterAlert>anyObject());
        expectLastCall().atLeastOnce();

        replay(contextService, adminService, motechService, mobileService, rctService);

        registrarBean.sendStaffCareMessages(forDate, forDate,
                forDate, forDate,
                careGroups,
                false,
                false);

        verify(contextService, adminService, motechService, mobileService, rctService);

        assertEquals(CareMessageGroupingStrategy.COMMUNITY, capturedStrategy.getValue());
    }

    public void testShouldSendUpcomingCareMessageWhenThereAreUpcomingExpectedEncounters() {

        Date forDate = new Date();
        String careGroups[] = {"ANC", "TT", "IPT"};

        Location location = new Location();
        location.setName("Test Facility");
        location.setCountyDistrict(new KassenaNankana().toString());
        location.setRegion("Test Regiion");

        Facility facility = new Facility();
        facility.setLocation(location);
        String facilityPhoneNumber = "+1 555 123-1234";
        facility.setPhoneNumber(facilityPhoneNumber);

        List<Facility> facilities = new ArrayList<Facility>();
        facilities.add(facility);
        facility.setLocation(location);
        facility.setLocation(location);


        Patient p = new Patient(5716);
        expect(rctService.isPatientRegisteredAndInTreatmentGroup(p)).andReturn(false).times(1);

        ExpectedEncounter enc = new ExpectedEncounter();
        enc.setPatient(p);

        List<ExpectedEncounter> encounters = new ArrayList<ExpectedEncounter>();
        List<ExpectedObs> emptyObs = new ArrayList<ExpectedObs>();
        final DefaultedExpectedEncounterAlert defaultedExpectedEncounterAlert = anyObject();
        encounters.add(enc);

        // To Mock
        expect(motechService.getCommunityByPatient(p)).andReturn(null);
        expect(motechService.getCommunityByPatient(p)).andReturn(null);
        expect(contextService.getMotechService()).andReturn(motechService).anyTimes();
        expect(motechService.getAllFacilities()).andReturn(facilities);
        expect(motechService.facilityFor(p)).andReturn(facility).anyTimes();
        expect(contextService.getAdministrationService()).andReturn(adminService).anyTimes();
        expect(adminService.getGlobalProperty(MotechConstants.GLOBAL_PROPERTY_MAX_QUERY_RESULTS)).andReturn("35").anyTimes();
        expect(motechService.getExpectedEncounter(null, facility, careGroups, null,
                null, forDate, forDate, 35)).andReturn(encounters);
        expect(motechService.getExpectedEncounter(null, facility, careGroups, null,
                forDate, null, forDate, 35)).andReturn(encounters);
        expect(motechService.getExpectedObs(null, facility, careGroups, null,
                null, forDate, forDate, 35)).andReturn(emptyObs);
        expect(motechService.getExpectedObs(null, facility, careGroups, null,
                forDate, null, forDate, 35)).andReturn(emptyObs);

        expect(motechService.getCareConfigurationFor(EasyMock.<String>anyObject())).andReturn(EasyMock.<CareConfiguration>anyObject());
        expect(motechService.getDefaultedEncounterAlertFor(enc)).andReturn(defaultedExpectedEncounterAlert);




        Capture<String> capturedMessageId = new Capture<String>();
        Capture<String> capturedPhoneNumber = new Capture<String>();
        Capture<Care[]> capturedCares = new Capture<Care[]>();
        Capture<CareMessageGroupingStrategy> capturedStrategy = new Capture<CareMessageGroupingStrategy>();
        Capture<MediaType> capturedMediaType = new Capture<MediaType>();
        Capture<Date> capturedStartDate = new Capture<Date>();
        Capture<Date> capturedEndDate = new Capture<Date>();

        expect(mobileService.sendDefaulterMessage(capture(capturedMessageId), capture(capturedPhoneNumber), capture(capturedCares), capture(capturedStrategy), capture(capturedMediaType), capture(capturedStartDate),
                capture(capturedEndDate))).andReturn(org.motechproject.ws.MessageStatus.DELIVERED);

        expect(mobileService.sendBulkCaresMessage(capture(capturedMessageId), capture(capturedPhoneNumber), capture(capturedCares), capture(capturedStrategy), capture(capturedMediaType), capture(capturedStartDate),
                capture(capturedEndDate))).andReturn(org.motechproject.ws.MessageStatus.DELIVERED);

        motechService.saveorUpdateDefaultedEncounterAlert(EasyMock.<DefaultedExpectedEncounterAlert>anyObject());
        expectLastCall().atLeastOnce();

        replay(contextService, adminService, motechService, mobileService, rctService);

        registrarBean.sendStaffCareMessages(forDate, forDate,
                forDate, forDate,
                careGroups,
                true,
                false);

        verify(contextService, adminService, motechService, mobileService, rctService);

        assertEquals(CareMessageGroupingStrategy.COMMUNITY, capturedStrategy.getValue());
    }

    public void testSendStaffCareMessages_NoDefaulters() {

        Date forDate = new Date();
        String careGroups[] = {"ANC", "TT", "IPT"};

        Location location = new Location();
        location.setName("Test Facility");

        Facility facility = new Facility();
        facility.setLocation(location);
        facility.setPhoneNumber("+1 555 123-1234");

        List<Facility> facilities = new ArrayList<Facility>();
        facilities.add(facility);

        List<ExpectedEncounter> emptyEncounters = new ArrayList<ExpectedEncounter>();
        List<ExpectedObs> emptyObs = new ArrayList<ExpectedObs>();

        // To Mock
        expect(contextService.getMotechService()).andReturn(motechService).anyTimes();
        expect(motechService.getAllFacilities()).andReturn(facilities);
        expect(contextService.getAdministrationService()).andReturn(adminService).anyTimes();
        expect(adminService.getGlobalProperty(MotechConstants.GLOBAL_PROPERTY_MAX_QUERY_RESULTS)).andReturn("35").anyTimes();
        expect(motechService.getExpectedEncounter(null, facility, careGroups, null,
                null, forDate, forDate, 35)).andReturn(emptyEncounters);
        expect(motechService.getExpectedObs(null, facility, careGroups, null,
                null, forDate, forDate, 35)).andReturn(emptyObs);

        Capture<String> capturedMessage = new Capture<String>();
        Capture<String> capturedPhoneNumber = new Capture<String>();

        expect(mobileService.sendMessage(capture(capturedMessage), capture(capturedPhoneNumber))).andReturn(org.motechproject.ws.MessageStatus.DELIVERED);

        replay(contextService, adminService, motechService, mobileService);

        registrarBean.sendStaffCareMessages(forDate, forDate,
                forDate, forDate,
                careGroups,
                false,
                false);

        verify(contextService, adminService, motechService, mobileService);

        assertEquals("Test Facility has no defaulters for this week", capturedMessage.getValue());
        assertEquals("+1 555 123-1234", capturedPhoneNumber.getValue());
    }

    public void testShouldSendDefaulterAlertsForAllDefaultsOnAnEncounter() {
        int expectedNumberOfDefaultsOnAnEncounter = 50;
        for (int i = 0; i < expectedNumberOfDefaultsOnAnEncounter; i++) {
            reset(rctService, contextService, adminService, motechService, mobileService);
            testSendStaffCareMessagesGroupByCommunity();
        }
    }

    public void testShouldSendNoUpcomingCareMessagesWhenThereAreNoUpcomingExpectedEncounters() {

        Date forDate = new Date();
        String careGroups[] = {"ANC", "TT", "IPT"};

        Location location = new Location();
        location.setName("Test Facility");

        Facility facility = new Facility();
        facility.setLocation(location);
        facility.setPhoneNumber("+1 555 123-1234");

        List<Facility> facilities = new ArrayList<Facility>();
        facilities.add(facility);

        List<ExpectedEncounter> emptyEncounters = new ArrayList<ExpectedEncounter>();
        List<ExpectedObs> emptyObs = new ArrayList<ExpectedObs>();

        // To Mock
        expect(contextService.getMotechService()).andReturn(motechService).anyTimes();
        expect(motechService.getAllFacilities()).andReturn(facilities);
        expect(contextService.getAdministrationService()).andReturn(adminService).anyTimes();
        expect(adminService.getGlobalProperty(MotechConstants.GLOBAL_PROPERTY_MAX_QUERY_RESULTS)).andReturn("35").anyTimes();
        expect(motechService.getExpectedEncounter(null, facility, careGroups, null,
                null, forDate, forDate, 35)).andReturn(emptyEncounters);
        expect(motechService.getExpectedEncounter(null, facility, careGroups, null,
                forDate, null, forDate, 35)).andReturn(emptyEncounters);
        expect(motechService.getExpectedObs(null, facility, careGroups, null,
                null, forDate, forDate, 35)).andReturn(emptyObs);
        expect(motechService.getExpectedObs(null, facility, careGroups, null,
                forDate, null, forDate, 35)).andReturn(emptyObs);

        Capture<String> capturedDefaulterMessage = new Capture<String>();
        Capture<String> capturedDefaulterPhoneNumber = new Capture<String>();
        Capture<String> capturedUpcomingCareMessage = new Capture<String>();
        Capture<String> capturedUpcomingCarePhoneNumber = new Capture<String>();

        expect(mobileService.sendMessage(capture(capturedDefaulterMessage), capture(capturedDefaulterPhoneNumber))).andReturn(org.motechproject.ws.MessageStatus.DELIVERED);
        expect(mobileService.sendMessage(capture(capturedUpcomingCareMessage), capture(capturedUpcomingCarePhoneNumber))).andReturn(org.motechproject.ws.MessageStatus.DELIVERED);


        replay(contextService, adminService, motechService, mobileService);

        registrarBean.sendStaffCareMessages(forDate, forDate,
                forDate, forDate,
                careGroups,
                true,
                false);

        verify(contextService, adminService, motechService, mobileService);

        assertEquals("Test Facility has no defaulters for this week", capturedDefaulterMessage.getValue());
        assertEquals("+1 555 123-1234", capturedDefaulterPhoneNumber.getValue());

        assertEquals("Test Facility has no upcoming care for this week", capturedUpcomingCareMessage.getValue());
        assertEquals("+1 555 123-1234", capturedUpcomingCarePhoneNumber.getValue());
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
        Patient p4 = new Patient(5719);
        Patient p5 = new Patient(5720);
        Patient p6 = new Patient(5721);
        Patient p7 = new Patient(5722);

        Facility facilityInUpperEast = getFacilityWithRegion("Upper East");
        Facility facilityInCentral = getFacilityWithRegion("Central");

        expect(contextService.getMotechService()).andReturn(motechService).times(12);
        expect(rctService.isPatientRegisteredAndInTreatmentGroup(p1)).andReturn(false).times(2);
        expect(rctService.isPatientRegisteredAndInTreatmentGroup(p2)).andReturn(false).times(2);
        expect(rctService.isPatientRegisteredAndInTreatmentGroup(p3)).andReturn(false).times(2);
        expect(rctService.isPatientRegisteredAndInTreatmentGroup(p4)).andReturn(false).times(2);
        expect(rctService.isPatientRegisteredAndInTreatmentGroup(p5)).andReturn(true).times(2);
        expect(rctService.isPatientRegisteredAndInTreatmentGroup(p6)).andReturn(false).times(2);
        expect(rctService.isPatientRegisteredAndInTreatmentGroup(p7)).andReturn(false).times(2);

        expect(motechService.facilityFor(p1)).andReturn(facilityInUpperEast).times(2);
        expect(motechService.facilityFor(p2)).andReturn(facilityInUpperEast).times(2);
        expect(motechService.facilityFor(p3)).andReturn(facilityInUpperEast).times(2);
        expect(motechService.facilityFor(p4)).andReturn(facilityInUpperEast).times(2);
        expect(motechService.facilityFor(p6)).andReturn(facilityInCentral).times(2);
        expect(motechService.facilityFor(p7)).andReturn(facilityInUpperEast).times(2);


        List<ExpectedObs> expObs = expectedObservationsFor(p1, p2, p3, p4, p5, p6, p7);
        List<ExpectedEncounter> expEnc = expectedEncountersFor(p1, p2, p3, p4, p5, p6, p7);

        replay(rctService, motechService, contextService);
        List<ExpectedObs> filteredObs = registrarBean.filterRCTObs(new ArrayList(expObs));
        List<ExpectedEncounter> filteredEnc = registrarBean.filterRCTEncounters(new ArrayList(expEnc));

        verify(rctService, motechService, contextService);

        assertEquals(4, filteredObs.size());
        ExpectedObs obs1 = filteredObs.get(0);
        ExpectedObs obs2 = filteredObs.get(1);
        ExpectedObs obs3 = filteredObs.get(2);
        ExpectedObs obs4 = filteredObs.get(3);
        assertEquals(p1.getPatientId(), obs1.getPatient().getPatientId());
        assertEquals(p2.getPatientId(), obs2.getPatient().getPatientId());
        assertEquals(p5.getPatientId(), obs3.getPatient().getPatientId());
        assertEquals(p6.getPatientId(), obs4.getPatient().getPatientId());

        assertEquals(4, filteredEnc.size());
        ExpectedEncounter enc1 = filteredEnc.get(0);
        ExpectedEncounter enc2 = filteredEnc.get(1);
        ExpectedEncounter enc3 = filteredEnc.get(2);
        ExpectedEncounter enc4 = filteredEnc.get(3);

        assertEquals(p1.getPatientId(), enc1.getPatient().getPatientId());
        assertEquals(p2.getPatientId(), enc2.getPatient().getPatientId());
        assertEquals(p5.getPatientId(), enc3.getPatient().getPatientId());
        assertEquals(p6.getPatientId(), enc4.getPatient().getPatientId());

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


    private Facility getFacilityWithRegion(String region) {
        Facility facility = new Facility();
        Location location = new Location();
        location.setRegion(region);
        facility.setLocation(location);
        return facility;
    }
}
