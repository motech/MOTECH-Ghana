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

package org.motechproject.mobile.omp.manager.intellivr;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mobile.core.model.Language;
import org.motechproject.mobile.core.model.LanguageImpl;
import org.motechproject.mobile.core.model.MStatus;
import org.motechproject.mobile.core.model.MessageRequest;
import org.motechproject.mobile.core.model.MessageRequestImpl;
import org.motechproject.mobile.core.model.MessageType;
import org.motechproject.mobile.core.model.NotificationType;
import org.motechproject.mobile.core.model.NotificationTypeImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( locations = {"classpath:META-INF/ivrcallstatsprovidertest-config.xml"})
public class IVRCallStatsProviderTest {

	@Resource
	IVRCallStatsProvider ivrCallStatsProvider;
	@Resource
	IntellIVRDAO ivrDao;
	
	Language english;
	NotificationType n1,n2,n3;
	long n1Id = 1;
	String n1IvrEntityName = "weekly_tree_name";
	long n2Id = 2;
	String n2IvrEntityName = "reminder_message1.wav";
	long n3Id = 3;
	String n3IvrEntityName = "reminder_message2.wav";
	String primaryInfoRecordingName = "primary_informational_message.wav";
	String secondaryInfoRecordingName = "secondary_informational_message.wav";
	String recipientId1 = "1234561";
	String phone1 = "5555551";
	
	private long nextRequestId = 0;
	
	@Before
	public void setUp() throws Exception {
		
		english = new LanguageImpl();
		english.setCode("en");
		english.setId(29000000001l);
		english.setName("English");
		
		n1 = new NotificationTypeImpl();
		n1.setId(1L);
		n1.setDescription("testing");
		n1.setName("testing");

		n2 = new NotificationTypeImpl();
		n2.setId(2L);

		n3 = new NotificationTypeImpl();
		n3.setId(3L);
		
	}

	@Test
	@Transactional
	public void testGetCountIVRCallSessions() {
		
		Date now = new Date();
		Date oneDayAgo = addToDate(now, GregorianCalendar.DAY_OF_MONTH, -1);
		Date tenDaysAgo = addToDate(now, GregorianCalendar.DAY_OF_MONTH, -10);
		
		MessageRequest mr1 = getMessageRequestTemplate();
		mr1.setNotificationType(n1);
		
		MessageRequest mr2 = getMessageRequestTemplate();
		mr2.setNotificationType(n2);
		
		IVRCallSession session1 = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, oneDayAgo, now);
		session1.getMessageRequests().add(mr1);
		session1.getMessageRequests().add(mr2);

		IVRCallSession session2 = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, tenDaysAgo, now);
		session2.getMessageRequests().add(mr1);
		session2.getMessageRequests().add(mr2);
		
		ivrDao.saveIVRCallSession(session1);
		ivrDao.saveIVRCallSession(session2);
		
		assertEquals(2, ivrCallStatsProvider.getCountIVRCallSessions());
		
	}
	
	@Test
	@Transactional
	public void testGetCountIVRCallSessionsInLastMinutes() {
		
		Date now = new Date();
		Date twoMinuteAgo = addToDate(now, GregorianCalendar.MINUTE, -2);
		Date tenMinuteAgo = addToDate(now, GregorianCalendar.MINUTE, -10);
		
		MessageRequest mr1 = getMessageRequestTemplate();
		mr1.setNotificationType(n1);
		
		MessageRequest mr2 = getMessageRequestTemplate();
		mr2.setNotificationType(n2);
		
		IVRCallSession session1 = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, twoMinuteAgo, now);
		session1.getMessageRequests().add(mr1);
		session1.getMessageRequests().add(mr2);

		IVRCallSession session2 = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, tenMinuteAgo, now);
		session2.getMessageRequests().add(mr1);
		session2.getMessageRequests().add(mr2);
		
		ivrDao.saveIVRCallSession(session1);
		ivrDao.saveIVRCallSession(session2);
		
		assertEquals(0, ivrCallStatsProvider.getCountIVRSessionsInLastMinutes(1));
		assertEquals(1, ivrCallStatsProvider.getCountIVRSessionsInLastMinutes(5));	
		assertEquals(2, ivrCallStatsProvider.getCountIVRSessionsInLastMinutes(11));	
	
	}
	
	@Test
	@Transactional
	public void testGetCountIVRCallSessionsInLastHours() {

		Date now = new Date();
		Date twoHoursAgo = addToDate(now, GregorianCalendar.HOUR, -2);
		Date tenHoursAgo = addToDate(now, GregorianCalendar.HOUR, -10);
		
		MessageRequest mr1 = getMessageRequestTemplate();
		mr1.setNotificationType(n1);
		
		MessageRequest mr2 = getMessageRequestTemplate();
		mr2.setNotificationType(n2);
		
		IVRCallSession session1 = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, twoHoursAgo, now);
		session1.getMessageRequests().add(mr1);
		session1.getMessageRequests().add(mr2);

		IVRCallSession session2 = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, tenHoursAgo, now);
		session2.getMessageRequests().add(mr1);
		session2.getMessageRequests().add(mr2);
		
		ivrDao.saveIVRCallSession(session1);
		ivrDao.saveIVRCallSession(session2);
		
		assertEquals(0, ivrCallStatsProvider.getCountIVRCallSessionsInLastHours(1));
		assertEquals(1, ivrCallStatsProvider.getCountIVRCallSessionsInLastHours(5));
		assertEquals(2, ivrCallStatsProvider.getCountIVRCallSessionsInLastHours(11));
		
	}
	
	@Test
	@Transactional
	public void testGetCountIVRCallSessionsInLastDays() {
		
		Date now = new Date();
		Date oneDayAgo = addToDate(now, GregorianCalendar.DAY_OF_MONTH, -1);
		Date tenDaysAgo = addToDate(now, GregorianCalendar.DAY_OF_MONTH, -10);
		
		MessageRequest mr1 = getMessageRequestTemplate();
		mr1.setNotificationType(n1);
		
		MessageRequest mr2 = getMessageRequestTemplate();
		mr2.setNotificationType(n2);
		
		IVRCallSession session1 = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, oneDayAgo, now);
		session1.getMessageRequests().add(mr1);
		session1.getMessageRequests().add(mr2);

		IVRCallSession session2 = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, tenDaysAgo, now);
		session2.getMessageRequests().add(mr1);
		session2.getMessageRequests().add(mr2);
		
		ivrDao.saveIVRCallSession(session1);
		ivrDao.saveIVRCallSession(session2);
		
		assertEquals(0, ivrCallStatsProvider.getCountIVRCallSessionsInLastDays(1));
		assertEquals(1, ivrCallStatsProvider.getCountIVRCallSessionsInLastDays(5));	
		assertEquals(1, ivrCallStatsProvider.getCountIVRCallSessionsInLastDays(10));
		assertEquals(2, ivrCallStatsProvider.getCountIVRCallSessionsInLastDays(11));
		
	}
	
	@Test
	@Transactional
	public void testGetCountIVRCalls() {
		
		Date now = new Date();
		Date oneDayAgo = addToDate(now, GregorianCalendar.DAY_OF_MONTH, -1);
		Date tenDaysAgo = addToDate(now, GregorianCalendar.DAY_OF_MONTH, -10);
		
		IVRCallSession session1 = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, oneDayAgo, now);
	
		IVRCallSession session2 = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, tenDaysAgo, now);

		ivrDao.saveIVRCallSession(session1);
		ivrDao.saveIVRCallSession(session2);
		
		IVRCall call1 = new IVRCall(now, null, null, 0, UUID.randomUUID().toString(), IVRCallStatus.REQUESTED, "Call request accepted", session1);
		session1.getCalls().add(call1);
		
		IVRCall call2 = new IVRCall(now, null, null, 0, UUID.randomUUID().toString(), IVRCallStatus.REQUESTED, "Call request accepted", session2);
		session2.getCalls().add(call2);

		assertEquals(2, ivrCallStatsProvider.getCountIVRCalls());
		
	}
	
	@Test
	@Transactional
	public void testGetCountIVRCallsInLastMinutes() {
		
		Date now = new Date();
		Date twoMinuteAgo = addToDate(now, GregorianCalendar.MINUTE, -2);
		Date tenMinuteAgo = addToDate(now, GregorianCalendar.MINUTE, -10);
		
		IVRCallSession session1 = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, twoMinuteAgo, now);

		IVRCallSession session2 = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, tenMinuteAgo, now);

		ivrDao.saveIVRCallSession(session1);
		ivrDao.saveIVRCallSession(session2);

		IVRCall call1 = new IVRCall(twoMinuteAgo, null, null, 0, UUID.randomUUID().toString(), IVRCallStatus.REQUESTED, "Call request accepted", session1);
		session1.getCalls().add(call1);
		
		IVRCall call2 = new IVRCall(tenMinuteAgo, null, null, 0, UUID.randomUUID().toString(), IVRCallStatus.REQUESTED, "Call request accepted", session2);
		session2.getCalls().add(call2);

		assertEquals(0, ivrCallStatsProvider.getCountIVRCallsInLastMinutes(1));
		assertEquals(1, ivrCallStatsProvider.getCountIVRCallsInLastMinutes(5));
		assertEquals(2, ivrCallStatsProvider.getCountIVRCallsInLastMinutes(11));
		
	}
	
	@Test
	@Transactional
	public void testGetCountIVRCallsInLastHour() {
		
		Date now = new Date();
		Date twoHoursAgo = addToDate(now, GregorianCalendar.HOUR, -2);
		Date tenHoursAgo = addToDate(now, GregorianCalendar.HOUR, -10);
		
		IVRCallSession session1 = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, twoHoursAgo, now);

		IVRCallSession session2 = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, tenHoursAgo, now);
		
		ivrDao.saveIVRCallSession(session1);
		ivrDao.saveIVRCallSession(session2);

		IVRCall call1 = new IVRCall(twoHoursAgo, null, null, 0, UUID.randomUUID().toString(), IVRCallStatus.REQUESTED, "Call request accepted", session1);
		session1.getCalls().add(call1);
		
		IVRCall call2 = new IVRCall(tenHoursAgo, null, null, 0, UUID.randomUUID().toString(), IVRCallStatus.REQUESTED, "Call request accepted", session2);
		session2.getCalls().add(call2);

		assertEquals(0, ivrCallStatsProvider.getCountIVRCallsInLastHours(1));
		assertEquals(1, ivrCallStatsProvider.getCountIVRCallsInLastHours(5));
		assertEquals(2, ivrCallStatsProvider.getCountIVRCallsInLastHours(11));

		
	}
	
	@Test
	@Transactional
	public void testGetCountIVRCallsInLastDays() {
		
		Date now = new Date();
		Date oneDayAgo = addToDate(now, GregorianCalendar.DAY_OF_MONTH, -1);
		Date tenDaysAgo = addToDate(now, GregorianCalendar.DAY_OF_MONTH, -10);
		
		IVRCallSession session1 = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, oneDayAgo, now);

		IVRCallSession session2 = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, tenDaysAgo, now);
		
		ivrDao.saveIVRCallSession(session1);
		ivrDao.saveIVRCallSession(session2);

		IVRCall call1 = new IVRCall(oneDayAgo, null, null, 0, UUID.randomUUID().toString(), IVRCallStatus.REQUESTED, "Call request accepted", session1);
		session1.getCalls().add(call1);
		
		IVRCall call2 = new IVRCall(tenDaysAgo, null, null, 0, UUID.randomUUID().toString(), IVRCallStatus.REQUESTED, "Call request accepted", session2);
		session2.getCalls().add(call2);

		assertEquals(0, ivrCallStatsProvider.getCountIVRCallsInLastDays(1));
		assertEquals(1, ivrCallStatsProvider.getCountIVRCallsInLastDays(5));	
		assertEquals(1, ivrCallStatsProvider.getCountIVRCallsInLastDays(10));
		assertEquals(2, ivrCallStatsProvider.getCountIVRCallsInLastDays(11));

		
	}
	
	@Test
	@Transactional
	public void testGetCountIVRCallsWithStatus() {
		
		Date now = new Date();
		Date oneDayAgo = addToDate(now, GregorianCalendar.DAY_OF_MONTH, -1);
		
		IVRCallSession session1 = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, oneDayAgo, now);
		
		ivrDao.saveIVRCallSession(session1);

		IVRCall call1 = new IVRCall(oneDayAgo, null, null, 0, UUID.randomUUID().toString(), IVRCallStatus.REQUESTED, "Call request accepted", session1);
		session1.getCalls().add(call1);

		for ( IVRCallStatus cs : IVRCallStatus.values() ) {
			if ( cs == IVRCallStatus.REQUESTED )
				assertEquals(1, ivrCallStatsProvider.getCountIVRCallsWithStatus(cs));
			else 
				assertEquals(0, ivrCallStatsProvider.getCountIVRCallsWithStatus(cs));
		}
		
	}
	
	@Test
	@Transactional
	public void testGetCountIVRCallsInLastMinutesWithStatus() {
		
		Date now = new Date();
		Date twoMinuteAgo = addToDate(now, GregorianCalendar.MINUTE, -2);
		Date tenMinuteAgo = addToDate(now, GregorianCalendar.MINUTE, -10);
		
		IVRCallSession session1 = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, twoMinuteAgo, now);

		IVRCallSession session2 = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, tenMinuteAgo, now);

		ivrDao.saveIVRCallSession(session1);
		ivrDao.saveIVRCallSession(session2);

		IVRCall call1 = new IVRCall(twoMinuteAgo, null, null, 0, UUID.randomUUID().toString(), IVRCallStatus.REQUESTED, "Call request accepted", session1);
		session1.getCalls().add(call1);
		
		IVRCall call2 = new IVRCall(tenMinuteAgo, null, null, 0, UUID.randomUUID().toString(), IVRCallStatus.REQUESTED, "Call request accepted", session2);
		session2.getCalls().add(call2);

		for ( IVRCallStatus cs : IVRCallStatus.values() ) {
			assertEquals(0, ivrCallStatsProvider.getCountIVRCallsInLastMinutesWithStatus(1, cs));
			if ( cs == IVRCallStatus.REQUESTED ) {
				assertEquals(1, ivrCallStatsProvider.getCountIVRCallsInLastMinutesWithStatus(5, cs));
				assertEquals(2, ivrCallStatsProvider.getCountIVRCallsInLastMinutesWithStatus(11,cs));
			} else { 
				assertEquals(0, ivrCallStatsProvider.getCountIVRCallsInLastMinutesWithStatus(5, cs));
				assertEquals(0, ivrCallStatsProvider.getCountIVRCallsInLastMinutesWithStatus(11,cs));
			}
			
		}

	}
	
	@Test
	@Transactional
	public void testGetCountIVRCallsInLastHoursWithStatus() {
		
		Date now = new Date();
		Date twoHoursAgo = addToDate(now, GregorianCalendar.HOUR, -2);
		Date tenHoursAgo = addToDate(now, GregorianCalendar.HOUR, -10);
		
		IVRCallSession session1 = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, twoHoursAgo, now);

		IVRCallSession session2 = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, tenHoursAgo, now);
		
		ivrDao.saveIVRCallSession(session1);
		ivrDao.saveIVRCallSession(session2);

		IVRCall call1 = new IVRCall(twoHoursAgo, null, null, 0, UUID.randomUUID().toString(), IVRCallStatus.REQUESTED, "Call request accepted", session1);
		session1.getCalls().add(call1);
		
		IVRCall call2 = new IVRCall(tenHoursAgo, null, null, 0, UUID.randomUUID().toString(), IVRCallStatus.REQUESTED, "Call request accepted", session2);
		session2.getCalls().add(call2);

		for ( IVRCallStatus cs : IVRCallStatus.values() ) {
			assertEquals(0, ivrCallStatsProvider.getCountIVRCallsInLastHoursWithStatus(1,cs));
			if ( cs == IVRCallStatus.REQUESTED ) {
				assertEquals(1, ivrCallStatsProvider.getCountIVRCallsInLastHoursWithStatus(5, cs));
				assertEquals(2, ivrCallStatsProvider.getCountIVRCallsInLastHoursWithStatus(11, cs));				
			} else {
				assertEquals(0, ivrCallStatsProvider.getCountIVRCallsInLastHoursWithStatus(5, cs));
				assertEquals(0, ivrCallStatsProvider.getCountIVRCallsInLastHoursWithStatus(11, cs));	
			}
		}
		
	}
	
	@Test
	@Transactional
	public void testGetCountIVRCallsInLastDaysWithStatus() {
		
		Date now = new Date();
		Date oneDayAgo = addToDate(now, GregorianCalendar.DAY_OF_MONTH, -1);
		Date tenDaysAgo = addToDate(now, GregorianCalendar.DAY_OF_MONTH, -10);
		
		IVRCallSession session1 = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, oneDayAgo, now);

		IVRCallSession session2 = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, tenDaysAgo, now);
		
		ivrDao.saveIVRCallSession(session1);
		ivrDao.saveIVRCallSession(session2);

		IVRCall call1 = new IVRCall(oneDayAgo, null, null, 0, UUID.randomUUID().toString(), IVRCallStatus.REQUESTED, "Call request accepted", session1);
		session1.getCalls().add(call1);
		
		IVRCall call2 = new IVRCall(tenDaysAgo, null, null, 0, UUID.randomUUID().toString(), IVRCallStatus.REQUESTED, "Call request accepted", session2);
		session2.getCalls().add(call2);

		for ( IVRCallStatus cs : IVRCallStatus.values() ) {
			assertEquals(0, ivrCallStatsProvider.getCountIVRCallsInLastDaysWithStatus(1, cs));
			if ( cs == IVRCallStatus.REQUESTED ) {
				assertEquals(1, ivrCallStatsProvider.getCountIVRCallsInLastDaysWithStatus(5, cs));	
				assertEquals(1, ivrCallStatsProvider.getCountIVRCallsInLastDaysWithStatus(10, cs));
				assertEquals(2, ivrCallStatsProvider.getCountIVRCallsInLastDaysWithStatus(11, cs));
			} else {
				assertEquals(0, ivrCallStatsProvider.getCountIVRCallsInLastDaysWithStatus(5, cs));	
				assertEquals(0, ivrCallStatsProvider.getCountIVRCallsInLastDaysWithStatus(10, cs));
				assertEquals(0, ivrCallStatsProvider.getCountIVRCallsInLastDaysWithStatus(11, cs));
			}
		}
		
	}
	
	@Test
	@Transactional
	public void testGetRecordingStats() {

		Date now = new Date();
		Date twoMinuteAgo = addToDate(now, GregorianCalendar.MINUTE, -2);
		Date tenMinuteAgo = addToDate(now, GregorianCalendar.MINUTE, -10);
		
		IVRCallSession session1 = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, twoMinuteAgo, now);

		IVRCallSession session2 = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, tenMinuteAgo, now);

		ivrDao.saveIVRCallSession(session1);
		ivrDao.saveIVRCallSession(session2);

		IVRCall call1 = new IVRCall(twoMinuteAgo, null, null, 0, UUID.randomUUID().toString(), IVRCallStatus.REQUESTED, "Call request accepted", session1);
		session1.getCalls().add(call1);
		
		IVRCall call2 = new IVRCall(tenMinuteAgo, null, null, 0, UUID.randomUUID().toString(), IVRCallStatus.REQUESTED, "Call request accepted", session2);
		session2.getCalls().add(call2);

		IVRMenu m1 = new IVRMenu(n2IvrEntityName, twoMinuteAgo, 10, "", "");
		IVRMenu m2 = new IVRMenu(n2IvrEntityName, twoMinuteAgo, 20, "", "");
		IVRMenu m3 = new IVRMenu(primaryInfoRecordingName, twoMinuteAgo, 10, "", "");
		IVRMenu m4 = new IVRMenu(primaryInfoRecordingName, twoMinuteAgo, 20, "", "");
		
		call1.getMenus().add(m1);
		call2.getMenus().add(m2);
		call1.getMenus().add(m3);
		call2.getMenus().add(m4);
		
		List<IVRRecordingStat> stats = (List<IVRRecordingStat>)ivrCallStatsProvider.getIVRRecordingStats();
		
		assertEquals(2, stats.size());
		
		for ( IVRRecordingStat s : stats ) {
			assertEquals(2, s.getTotalListens());
			assertEquals(15, s.getAverageTimeListened(),1);
		}
		
	}
	
	@Test
	@Transactional
	public void testGetIVRCallStatusStats() {
		
		Date now = new Date();
		Date twoMinuteAgo = addToDate(now, GregorianCalendar.MINUTE, -2);
		Date tenMinuteAgo = addToDate(now, GregorianCalendar.MINUTE, -10);
		
		IVRCallSession session1 = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, twoMinuteAgo, now);

		IVRCallSession session2 = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, tenMinuteAgo, now);

		ivrDao.saveIVRCallSession(session1);
		ivrDao.saveIVRCallSession(session2);

		IVRCall call1 = new IVRCall(twoMinuteAgo, null, null, 0, UUID.randomUUID().toString(), IVRCallStatus.REQUESTED, "Call request accepted", session1);
		session1.getCalls().add(call1);
		
		IVRCall call2 = new IVRCall(tenMinuteAgo, null, null, 0, UUID.randomUUID().toString(), IVRCallStatus.REQUESTED, "Call request accepted", session2);
		session2.getCalls().add(call2);

		List<IVRCallStatusStat> stats = ivrCallStatsProvider.getIVRCallStatusStats();
		
		assertEquals(IVRCallStatus.values().length, stats.size());
		
		for ( IVRCallStatusStat s : stats ) {
			if ( s.getStatus() == IVRCallStatus.REQUESTED ) 
				assertEquals(2, s.getCount());
			else 
				assertEquals(0, s.getCount());
		}
		
	}
	
	@Test
	@Transactional
	public void testGetIVRCallStatusStatsInLastMinutes() {
		
		Date now = new Date();
		Date twoMinuteAgo = addToDate(now, GregorianCalendar.MINUTE, -2);
		Date tenMinuteAgo = addToDate(now, GregorianCalendar.MINUTE, -10);
		
		IVRCallSession session1 = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, twoMinuteAgo, now);

		IVRCallSession session2 = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, tenMinuteAgo, now);

		ivrDao.saveIVRCallSession(session1);
		ivrDao.saveIVRCallSession(session2);

		IVRCall call1 = new IVRCall(twoMinuteAgo, null, null, 0, UUID.randomUUID().toString(), IVRCallStatus.REQUESTED, "Call request accepted", session1);
		session1.getCalls().add(call1);
		
		IVRCall call2 = new IVRCall(tenMinuteAgo, null, null, 0, UUID.randomUUID().toString(), IVRCallStatus.REQUESTED, "Call request accepted", session2);
		session2.getCalls().add(call2);

		List<IVRCallStatusStat> fiveMinuteStats = ivrCallStatsProvider.getIVRCallStatusStatsFromLastMinutes(5);
		
		assertEquals(IVRCallStatus.values().length, fiveMinuteStats.size());
		
		for ( IVRCallStatusStat s : fiveMinuteStats ) {
			if ( s.getStatus() == IVRCallStatus.REQUESTED ) 
				assertEquals(1, s.getCount());
			else 
				assertEquals(0, s.getCount());
		}
		
		List<IVRCallStatusStat> ellevenMinuteStats = ivrCallStatsProvider.getIVRCallStatusStatsFromLastMinutes(11);
		
		assertEquals(IVRCallStatus.values().length, ellevenMinuteStats.size());
		
		for ( IVRCallStatusStat s : ellevenMinuteStats ) {
			if ( s.getStatus() == IVRCallStatus.REQUESTED ) 
				assertEquals(2, s.getCount());
			else 
				assertEquals(0, s.getCount());
		}
		
	}
	
	@Test
	@Transactional
	public void testGetIVRCallStatusStatsInLastHours() {
		
		Date now = new Date();
		Date twoHoursAgo = addToDate(now, GregorianCalendar.HOUR, -2);
		Date tenHoursAgo = addToDate(now, GregorianCalendar.HOUR, -10);
		
		IVRCallSession session1 = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, twoHoursAgo, now);

		IVRCallSession session2 = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, tenHoursAgo, now);
		
		ivrDao.saveIVRCallSession(session1);
		ivrDao.saveIVRCallSession(session2);

		IVRCall call1 = new IVRCall(twoHoursAgo, null, null, 0, UUID.randomUUID().toString(), IVRCallStatus.REQUESTED, "Call request accepted", session1);
		session1.getCalls().add(call1);
		
		IVRCall call2 = new IVRCall(tenHoursAgo, null, null, 0, UUID.randomUUID().toString(), IVRCallStatus.REQUESTED, "Call request accepted", session2);
		session2.getCalls().add(call2);
		
		List<IVRCallStatusStat> fiveHourStats = ivrCallStatsProvider.getIVRCallStatusStatsFromLastHours(5);
		
		assertEquals(IVRCallStatus.values().length, fiveHourStats.size());
		
		for ( IVRCallStatusStat s : fiveHourStats ) {
			if ( s.getStatus() == IVRCallStatus.REQUESTED ) 
				assertEquals(1, s.getCount());
			else 
				assertEquals(0, s.getCount());
		}
		
		List<IVRCallStatusStat> ellevenHourStats = ivrCallStatsProvider.getIVRCallStatusStatsFromLastHours(11);
		
		assertEquals(IVRCallStatus.values().length, ellevenHourStats.size());
		
		for ( IVRCallStatusStat s : ellevenHourStats ) {
			if ( s.getStatus() == IVRCallStatus.REQUESTED ) 
				assertEquals(2, s.getCount());
			else 
				assertEquals(0, s.getCount());
		}
		
	}
	
	@Test
	@Transactional
	public void testGetIVRCallStatusStatsInLastDays() {
	
		Date now = new Date();
		Date oneDayAgo = addToDate(now, GregorianCalendar.DAY_OF_MONTH, -1);
		Date tenDaysAgo = addToDate(now, GregorianCalendar.DAY_OF_MONTH, -10);
		
		IVRCallSession session1 = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, oneDayAgo, now);

		IVRCallSession session2 = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, tenDaysAgo, now);
		
		ivrDao.saveIVRCallSession(session1);
		ivrDao.saveIVRCallSession(session2);

		IVRCall call1 = new IVRCall(oneDayAgo, null, null, 0, UUID.randomUUID().toString(), IVRCallStatus.REQUESTED, "Call request accepted", session1);
		session1.getCalls().add(call1);
		
		IVRCall call2 = new IVRCall(tenDaysAgo, null, null, 0, UUID.randomUUID().toString(), IVRCallStatus.REQUESTED, "Call request accepted", session2);
		session2.getCalls().add(call2);

		List<IVRCallStatusStat> fiveDayStats = ivrCallStatsProvider.getIVRCallStatusStatsFromLastDays(5);
		
		assertEquals(IVRCallStatus.values().length, fiveDayStats.size());
		
		for ( IVRCallStatusStat s : fiveDayStats ) {
			if ( s.getStatus() == IVRCallStatus.REQUESTED ) 
				assertEquals(1, s.getCount());
			else 
				assertEquals(0, s.getCount());
		}

		List<IVRCallStatusStat> ellevenDayStats = ivrCallStatsProvider.getIVRCallStatusStatsFromLastDays(11);
		
		assertEquals(IVRCallStatus.values().length, ellevenDayStats.size());
		
		for ( IVRCallStatusStat s : ellevenDayStats ) {
			if ( s.getStatus() == IVRCallStatus.REQUESTED ) 
				assertEquals(2, s.getCount());
			else 
				assertEquals(0, s.getCount());
		}

		
	}
	
	@Test
	@Transactional
	public void getIVRCallSessions() {
		
		Date now = new Date();
		Date oneDayAgo = addToDate(now, GregorianCalendar.DAY_OF_MONTH, -1);
		Date tenDaysAgo = addToDate(now, GregorianCalendar.DAY_OF_MONTH, -10);
		
		MessageRequest mr1 = getMessageRequestTemplate();
		mr1.setNotificationType(n1);
		
		MessageRequest mr2 = getMessageRequestTemplate();
		mr2.setNotificationType(n2);
		
		IVRCallSession session1 = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, oneDayAgo, now);
		session1.getMessageRequests().add(mr1);
		session1.getMessageRequests().add(mr2);

		IVRCallSession session2 = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, tenDaysAgo, now);
		session2.getMessageRequests().add(mr1);
		session2.getMessageRequests().add(mr2);
		
		ivrDao.saveIVRCallSession(session1);
		ivrDao.saveIVRCallSession(session2);
		
		List<IVRCallSession> sessions = ivrCallStatsProvider.getIVRCallSessions();

		assertEquals(2, sessions.size());
		
	}
	
	@Test
	@Transactional
	public void testGetIVRCallSessionsInLastMinutes() {
		
		Date now = new Date();
		Date twoMinuteAgo = addToDate(now, GregorianCalendar.MINUTE, -2);
		Date tenMinuteAgo = addToDate(now, GregorianCalendar.MINUTE, -10);
		
		MessageRequest mr1 = getMessageRequestTemplate();
		mr1.setNotificationType(n1);
		
		MessageRequest mr2 = getMessageRequestTemplate();
		mr2.setNotificationType(n2);
		
		IVRCallSession session1 = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, twoMinuteAgo, now);
		session1.getMessageRequests().add(mr1);
		session1.getMessageRequests().add(mr2);

		IVRCallSession session2 = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, tenMinuteAgo, now);
		session2.getMessageRequests().add(mr1);
		session2.getMessageRequests().add(mr2);
		
		ivrDao.saveIVRCallSession(session1);
		ivrDao.saveIVRCallSession(session2);

		List<IVRCallSession> sessions = ivrCallStatsProvider.getIVRCallSessionsInLastMinutes(5);

		assertEquals(1, sessions.size());
	
	}
	
	@Test
	@Transactional
	public void testGetIVRCallSessionsInLastHours() {
		
		Date now = new Date();
		Date twoHoursAgo = addToDate(now, GregorianCalendar.HOUR, -2);
		Date tenHoursAgo = addToDate(now, GregorianCalendar.HOUR, -10);
		
		MessageRequest mr1 = getMessageRequestTemplate();
		mr1.setNotificationType(n1);
		
		MessageRequest mr2 = getMessageRequestTemplate();
		mr2.setNotificationType(n2);
		
		IVRCallSession session1 = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, twoHoursAgo, now);
		session1.getMessageRequests().add(mr1);
		session1.getMessageRequests().add(mr2);

		IVRCallSession session2 = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, tenHoursAgo, now);
		session2.getMessageRequests().add(mr1);
		session2.getMessageRequests().add(mr2);
		
		ivrDao.saveIVRCallSession(session1);
		ivrDao.saveIVRCallSession(session2);

		List<IVRCallSession> sessions = ivrCallStatsProvider.getIVRCallSessionsInLastHours(5);

		assertEquals(1, sessions.size());

	}
	
	@Test
	@Transactional
	public void testGetIVRCallSessionsInLastDays() {
		
		Date now = new Date();
		Date oneDayAgo = addToDate(now, GregorianCalendar.DAY_OF_MONTH, -1);
		Date tenDaysAgo = addToDate(now, GregorianCalendar.DAY_OF_MONTH, -10);
		
		MessageRequest mr1 = getMessageRequestTemplate();
		mr1.setNotificationType(n1);
		
		MessageRequest mr2 = getMessageRequestTemplate();
		mr2.setNotificationType(n2);
		
		IVRCallSession session1 = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, oneDayAgo, now);
		session1.getMessageRequests().add(mr1);
		session1.getMessageRequests().add(mr2);

		IVRCallSession session2 = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, tenDaysAgo, now);
		session2.getMessageRequests().add(mr1);
		session2.getMessageRequests().add(mr2);
		
		ivrDao.saveIVRCallSession(session1);
		ivrDao.saveIVRCallSession(session2);

		List<IVRCallSession> sessions = ivrCallStatsProvider.getIVRCallSessionsInLastDays(5);

		assertEquals(1, sessions.size());
		
	}
	
	@Test
	@Transactional
	public void testGetIVRCallSessionForUser() {
		
		Date now = new Date();
		Date oneDayAgo = addToDate(now, GregorianCalendar.DAY_OF_MONTH, -1);
		Date tenDaysAgo = addToDate(now, GregorianCalendar.DAY_OF_MONTH, -10);
		
		MessageRequest mr1 = getMessageRequestTemplate();
		mr1.setNotificationType(n1);
		
		MessageRequest mr2 = getMessageRequestTemplate();
		mr2.setNotificationType(n2);
		
		IVRCallSession session1 = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, oneDayAgo, now);
		session1.getMessageRequests().add(mr1);
		session1.getMessageRequests().add(mr2);

		IVRCallSession session2 = new IVRCallSession("notRecip1", "notPhone1", english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, tenDaysAgo, now);
		session2.getMessageRequests().add(mr1);
		session2.getMessageRequests().add(mr2);
		
		ivrDao.saveIVRCallSession(session1);
		ivrDao.saveIVRCallSession(session2);

		List<IVRCallSession> sessions = ivrCallStatsProvider.getIVRCallSessionsForUser(recipientId1);
		
		assertEquals(1, sessions.size());
		
	}
	
	@Test
	@Transactional
	public void testGetIVRCallSessionsForPhone() {
		
		Date now = new Date();
		Date oneDayAgo = addToDate(now, GregorianCalendar.DAY_OF_MONTH, -1);
		Date tenDaysAgo = addToDate(now, GregorianCalendar.DAY_OF_MONTH, -10);
		
		MessageRequest mr1 = getMessageRequestTemplate();
		mr1.setNotificationType(n1);
		
		MessageRequest mr2 = getMessageRequestTemplate();
		mr2.setNotificationType(n2);
		
		IVRCallSession session1 = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, oneDayAgo, now);
		session1.getMessageRequests().add(mr1);
		session1.getMessageRequests().add(mr2);

		IVRCallSession session2 = new IVRCallSession("notRecip1", "notPhone1", english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, tenDaysAgo, now);
		session2.getMessageRequests().add(mr1);
		session2.getMessageRequests().add(mr2);
		
		ivrDao.saveIVRCallSession(session1);
		ivrDao.saveIVRCallSession(session2);

		List<IVRCallSession> sessions = ivrCallStatsProvider.getIVRCallSessionsForPhone(phone1);
		
		assertEquals(1, sessions.size());
		
	}
	
	private MessageRequest getMessageRequestTemplate() {
		MessageRequest mr = new MessageRequestImpl();
		mr.setDateCreated(new Date());
		mr.setDateFrom(new Date());
		mr.setDaysAttempted(1);
		mr.setId(nextRequestId);
		mr.setLanguage(english);
		mr.setMessageType(MessageType.VOICE);
		mr.setNotificationType(n1);
		mr.setPhoneNumberType("PERSONAL");
		mr.setRecipientId(recipientId1);
		mr.setRecipientNumber(phone1);
		mr.setRequestId(UUID.randomUUID().toString());
		mr.setStatus(MStatus.PENDING);
		mr.setTryNumber(1);
		mr.setVersion(0);
		return mr;
	}

	private Date addToDate(Date start, int field, int amount) {
		
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(start);
		cal.add(field, amount);
		return cal.getTime();
		
	}
	
}
