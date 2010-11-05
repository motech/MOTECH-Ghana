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
import static org.easymock.EasyMock.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mobile.core.dao.MessageRequestDAO;
import org.motechproject.mobile.core.manager.CoreManager;
import org.motechproject.mobile.core.model.GatewayRequest;
import org.motechproject.mobile.core.model.GatewayRequestImpl;
import org.motechproject.mobile.core.model.GatewayResponse;
import org.motechproject.mobile.core.model.Language;
import org.motechproject.mobile.core.model.LanguageImpl;
import org.motechproject.mobile.core.model.MStatus;
import org.motechproject.mobile.core.model.MessageRequest;
import org.motechproject.mobile.core.model.MessageRequestImpl;
import org.motechproject.mobile.core.model.MessageType;
import org.motechproject.mobile.core.model.NotificationType;
import org.motechproject.mobile.core.model.NotificationTypeImpl;
import org.motechproject.mobile.omp.manager.intellivr.RequestType.Vxml;
import org.motechproject.mobile.omp.manager.utils.MessageStatusStore;
import org.motechproject.ws.server.RegistrarService;
import org.motechproject.ws.server.ValidationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( locations = {"classpath:META-INF/test-omp-config.xml"})
public class IntellIVRBeanTest {

	@Resource
	IntellIVRBean intellivrBean;
	
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
	String recipientId1 = "1234567";
	String phone1 = "5555551";
	List<ErrorCodeType> fatalServerErrorCodes = new ArrayList<ErrorCodeType>();
	List<ReportStatusType> reportFailedStatusTypes = new ArrayList<ReportStatusType>();
	
	private long nextRequestId = 0;

	@Before
	public void setUp() throws Exception {
		
		english = new LanguageImpl();
		english.setCode("en");
		english.setId(1L);
		english.setName("English");
		
		n1 = new NotificationTypeImpl();
		n1.setId(n1Id);
		
		n2 = new NotificationTypeImpl();
		n2.setId(n2Id);
		
		n3 = new NotificationTypeImpl();
		n3.setId(n3Id);
		
		fatalServerErrorCodes.add(ErrorCodeType.IVR_BAD_REQUEST);
		fatalServerErrorCodes.add(ErrorCodeType.IVR_INVALID_API_ID);
		fatalServerErrorCodes.add(ErrorCodeType.IVR_INVALID_CALLEE);
		fatalServerErrorCodes.add(ErrorCodeType.IVR_INVALID_LANGUAGE);
		fatalServerErrorCodes.add(ErrorCodeType.IVR_INVALID_METHOD);
		fatalServerErrorCodes.add(ErrorCodeType.IVR_INVALID_SOUND_FILENAME_FORMAT);
		fatalServerErrorCodes.add(ErrorCodeType.IVR_INVALID_TREE);
		fatalServerErrorCodes.add(ErrorCodeType.IVR_INVALID_URL_FORMAT);
		fatalServerErrorCodes.add(ErrorCodeType.IVR_MALFORMED_XML);
		fatalServerErrorCodes.add(ErrorCodeType.IVR_NO_ACTION);
		fatalServerErrorCodes.add(ErrorCodeType.IVR_UNKNOWN_ERROR);
		fatalServerErrorCodes.add(ErrorCodeType.IVR_UNSUPPORTED_REPORT_TYPE);
		
		reportFailedStatusTypes.add(ReportStatusType.BUSY);
		reportFailedStatusTypes.add(ReportStatusType.CONGESTION);
		reportFailedStatusTypes.add(ReportStatusType.INTERNALERROR);
		reportFailedStatusTypes.add(ReportStatusType.NOANSWER);
		reportFailedStatusTypes.add(ReportStatusType.REJECTED);

		IVRNotificationMapping m1 = new IVRNotificationMapping();
		m1.setId(1);
		m1.setIvrEntityName(n1IvrEntityName);
		m1.setType(IVRNotificationMapping.INFORMATIONAL);

		IVRNotificationMapping m2 = new IVRNotificationMapping();
		m2.setId(2);
		m2.setIvrEntityName(n2IvrEntityName);
		m2.setType(IVRNotificationMapping.REMINDER);

		IVRNotificationMapping m3 = new IVRNotificationMapping();
		m3.setId(3);
		m3.setIvrEntityName(n3IvrEntityName);
		m3.setType(IVRNotificationMapping.REMINDER);

		Map<Long, IVRNotificationMapping> mapping = new HashMap<Long, IVRNotificationMapping>();
		mapping.put(m1.getId(), m1);
		mapping.put(m2.getId(), m2);
		mapping.put(m3.getId(), m3);
		
		Map<String, Long> mapping2 = new HashMap<String, Long>();
		mapping2.put(n1IvrEntityName, n1Id);
		mapping2.put(n2IvrEntityName, n2Id);
		mapping2.put(n3IvrEntityName, n3Id);

		intellivrBean.ivrNotificationMap = mapping;
		intellivrBean.ivrReminderIds = mapping2;
		intellivrBean.setWelcomeMessageRecordingName("welcome.wav");
		intellivrBean.setCallCompletedThreshold(15);
		intellivrBean.setBundlingDelay(-1);
		intellivrBean.setAvailableDays(1);
		intellivrBean.setPreReminderDelay(5);
		intellivrBean.setRetryDelay(-1);
		
	}

	@After
	public void tearDown() {

	}

	@Test
	public void testHandleGetIVRConfigTestData() {

		GetIVRConfigRequest request = new GetIVRConfigRequest();
		request.setUserid("123456789");

		ResponseType expected = new ResponseType();
		expected.setStatus(StatusType.OK);
		expected.setLanguage(intellivrBean.getDefaultLanguage());
		expected.setPrivate("123456789");
		expected.setReportUrl(intellivrBean.getReportURL());
		expected.setTree(intellivrBean.getDefaultTree());
		RequestType.Vxml vxml = new RequestType.Vxml();
		vxml.setPrompt(new RequestType.Vxml.Prompt());
		AudioType audio = new AudioType();
		audio.setSrc(intellivrBean.getDefaultReminder());
		vxml.getPrompt().getAudioOrBreak().add(audio);
		expected.setVxml(vxml);

		ResponseType response = intellivrBean.handleRequest(request);

		assertEquals(expected, response);

	}
	
	@Test
	public void testSendMessage() {
		
		GatewayRequest gr = getGatewayRequestTemplate();
		
		List<IVRCallSession> daoResponse = new ArrayList<IVRCallSession>();
		
		IVRDAO mockIvrDao = createMock(IVRDAO.class);
		intellivrBean.setIvrDao(mockIvrDao);
		
		Integer[] expectedStates = {IVRCallSession.OPEN};
		expect(mockIvrDao.loadIVRCallSessions(EasyMock.eq(gr.getMessageRequest().getRecipientId()), EasyMock.eq(gr.getMessageRequest().getRecipientNumber()), EasyMock.eq(gr.getMessageRequest().getLanguage().getName()), EasyMock.aryEq(expectedStates), EasyMock.eq(0), EasyMock.eq(0), EasyMock.eq(IVRCallSession.OUTBOUND)))
			.andReturn(daoResponse);
		expect(mockIvrDao.saveIVRCallSession((IVRCallSession)EasyMock.anyObject()))
			.andReturn(1L);
		replay(mockIvrDao);
		
		Set<GatewayResponse> actualGrsSet = intellivrBean.sendMessage(gr);
		assertEquals(1, actualGrsSet.size());
		
		for ( GatewayResponse r : actualGrsSet )
			assertEquals(StatusType.OK.value(), r.getResponseText());
		
		verify(mockIvrDao);	

	}
	
	@Test
	public void testSendMessageHouseholdPhone(){
		
		GatewayRequest gr = getGatewayRequestTemplate();
		gr.getMessageRequest().setPhoneNumberType("HOUSEHOLD");
		
		List<IVRCallSession> daoResponse = new ArrayList<IVRCallSession>();
		
		IVRDAO mockIvrDao = createMock(IVRDAO.class);
		intellivrBean.setIvrDao(mockIvrDao);
		
		Integer[] expectedStates = {IVRCallSession.OPEN};
		expect(mockIvrDao.loadIVRCallSessions(EasyMock.eq(gr.getMessageRequest().getRecipientId()), EasyMock.eq(gr.getMessageRequest().getRecipientNumber()), EasyMock.eq(gr.getMessageRequest().getLanguage().getName()), EasyMock.aryEq(expectedStates), EasyMock.eq(0), EasyMock.eq(0), EasyMock.eq(IVRCallSession.OUTBOUND)))
			.andReturn(daoResponse);
		expect(mockIvrDao.saveIVRCallSession((IVRCallSession)EasyMock.anyObject()))
			.andReturn(1L);
		replay(mockIvrDao);

		Set<GatewayResponse> actualGrsSet = intellivrBean.sendMessage(gr);
		assertEquals(1, actualGrsSet.size());
		
		for ( GatewayResponse r : actualGrsSet )
			assertEquals(StatusType.OK.value(), r.getResponseText());
		
		verify(mockIvrDao);	
		
	}
	
	@Test
	public void testSendMessagePublicPhone(){
		
		GatewayRequest gr = getGatewayRequestTemplate();
		gr.getMessageRequest().setPhoneNumberType("PUBLIC");

		IVRDAO mockIvrDao = createMock(IVRDAO.class);
		intellivrBean.setIvrDao(mockIvrDao);
		replay(mockIvrDao);
		
		Set<GatewayResponse> actualGrsSet = intellivrBean.sendMessage(gr);
		assertEquals(1, actualGrsSet.size());
		
		for ( GatewayResponse r : actualGrsSet )
			assertEquals(StatusType.OK.value(), r.getResponseText());
		
		verify(mockIvrDao);
		
	}
	
	@Test
	public void testSendMessageAddToExistingSession() {
		
		Date now = new Date();
		Date bundlingExpiration = addToDate(now, GregorianCalendar.MILLISECOND, (int)intellivrBean.getBundlingDelay());
		
		GatewayRequest gr1 = getGatewayRequestTemplate();
		
		IVRCallSession session = new IVRCallSession(gr1.getMessageRequest().getRecipientId(), gr1.getRecipientsNumber(), gr1.getMessageRequest().getLanguage().getName(), IVRCallSession.OUTBOUND, 0, 1, IVRCallSession.OPEN, now, bundlingExpiration);
		session.getMessageRequests().add(gr1.getMessageRequest());
		
		List<IVRCallSession> daoResponse = new ArrayList<IVRCallSession>();
		daoResponse.add(session);
		
		IVRDAO mockIvrDao = createMock(IVRDAO.class);
		intellivrBean.setIvrDao(mockIvrDao);
		
		GatewayRequest gr2 = getGatewayRequestTemplate();
		
		Integer[] openState = { IVRCallSession.OPEN };
		expect(mockIvrDao.loadIVRCallSessions(EasyMock.eq(gr1.getMessageRequest().getRecipientId()), EasyMock.eq(gr1.getMessageRequest().getRecipientNumber()), EasyMock.eq(gr1.getMessageRequest().getLanguage().getName()), EasyMock.aryEq(openState), EasyMock.eq(0), EasyMock.eq(0), EasyMock.eq(IVRCallSession.OUTBOUND)))
			.andReturn(daoResponse);
		replay(mockIvrDao);
		
		assertEquals(1, session.getMessageRequests().size());
		Set<GatewayResponse> actualGrsSet = intellivrBean.sendMessage(gr2);
		assertEquals(1, actualGrsSet.size());
		assertEquals(2, session.getMessageRequests().size());
		
		for ( GatewayResponse r : actualGrsSet )
			assertEquals(StatusType.OK.value(), r.getResponseText());
		
		verify(mockIvrDao);
		
	}
		
	@Test
	public void testSendMessageNullRecipientId() {

		GatewayRequest gr = getGatewayRequestTemplate();
		gr.getMessageRequest().setRecipientId(null);
		Set<GatewayResponse> actualGrsSet = intellivrBean.sendMessage(gr);

		for ( GatewayResponse r : actualGrsSet ) 
			assertEquals(StatusType.ERROR.value(), r.getResponseText());
		
	}
	
	@Test
	public void testSendMessageNullPhone() {
		
		GatewayRequest gr = getGatewayRequestTemplate();
		gr.setRecipientsNumber(null);
		Set<GatewayResponse> actualGrsSet = intellivrBean.sendMessage(gr);
		
		for ( GatewayResponse r : actualGrsSet ) 
			assertEquals(StatusType.ERROR.value(), r.getResponseText());
		
	}
	
	@Test
	public void testSendMessageTextMessage() {
		
		GatewayRequest gr = getGatewayRequestTemplate();
		gr.getMessageRequest().setMessageType(MessageType.TEXT);
		Set<GatewayResponse> actualGrsSet = intellivrBean.sendMessage(gr);
		
		for ( GatewayResponse r : actualGrsSet ) 
			assertEquals(StatusType.ERROR.value(), r.getResponseText());
		
	}
	
	@Test
	public void testProcessOpenSessions() {
		
		Date now = new Date();
		Date oneMinuteAgo = addToDate(now, GregorianCalendar.MINUTE, -1);
		Date oneMinuteLessBundlingDelayAgo = addToDate(now, GregorianCalendar.MINUTE, (int)intellivrBean.getBundlingDelay()*-1);
		
		IVRCallSession session = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, oneMinuteLessBundlingDelayAgo, oneMinuteAgo);
		
		List<IVRCallSession> expectedDaoResponse = new ArrayList<IVRCallSession>();
		expectedDaoResponse.add(session);
		
		IVRDAO mockDao = createMock(IVRDAO.class);
		intellivrBean.setIvrDao(mockDao);
		
		Integer[] states = { IVRCallSession.OPEN };
		
		expect(mockDao.loadIVRCallSessionsByStateNextAttemptBeforeDate(EasyMock.aryEq(states), (Date)EasyMock.anyObject())).andReturn(expectedDaoResponse);
		
		replay(mockDao);
		
		intellivrBean.processOpenSessions();
		
		assertEquals(IVRCallSession.SEND_WAIT, session.getState());
		
		verify(mockDao);
		
	}
	
	@Test
	public void testProcessWaitingSessions() {
		
		Date now = new Date();
		Date oneMinuteAgo = addToDate(now, GregorianCalendar.MINUTE, -1);
		Date oneMinuteLessBundlingDelayAgo = addToDate(now, GregorianCalendar.MINUTE, (int)intellivrBean.getBundlingDelay()*-1);
		
		IVRCallSession session = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.SEND_WAIT, oneMinuteLessBundlingDelayAgo, oneMinuteAgo);
		
		List<IVRCallSession> expectedDaoResponse = new ArrayList<IVRCallSession>();
		expectedDaoResponse.add(session);
		
		IVRDAO mockDao = createMock(IVRDAO.class);
		intellivrBean.setIvrDao(mockDao);
		
		Integer[] states = { IVRCallSession.SEND_WAIT };
		
		expect(mockDao.loadIVRCallSessionsByStateNextAttemptBeforeDate(EasyMock.aryEq(states), (Date)EasyMock.anyObject())).andReturn(expectedDaoResponse);
		
		IVRCallRequester mockRequester = createMock(IVRCallRequester.class);
		intellivrBean.setIvrCallRequester(mockRequester);
		
		mockRequester.requestCall((IVRCallSession)EasyMock.anyObject(), (String)EasyMock.anyObject());
		
		replay(mockDao,mockRequester);
		
		intellivrBean.processWaitingSessions();
		
		assertEquals(IVRCallSession.SEND_WAIT, session.getState());
		
		verify(mockDao,mockRequester);
		
	}
	
	@Test
	public void testRequestCallSuccessful() {
		
		Date now = new Date();
		Date bundlingDelayAgo = addToDate(now, GregorianCalendar.MILLISECOND, (int)intellivrBean.getBundlingDelay()*-1);
		
		MessageRequest mr1 = getMessageRequestTemplate();
		MessageRequest mr2 = getMessageRequestTemplate();
		mr2.setNotificationType(n2);
		
		String externalId = UUID.randomUUID().toString();
		
		IVRCallSession session = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, bundlingDelayAgo, now);
		session.getMessageRequests().add(mr1);
		session.getMessageRequests().add(mr2);
		
		RequestType expectedRequest = getRequestTypeTemplate();
		expectedRequest.setCallee(phone1);
		expectedRequest.setLanguage(english.getName());
		expectedRequest.setPrivate(externalId);
		expectedRequest.setTree(n1IvrEntityName);
		
		BreakType breakType = new BreakType();
		breakType.setTime(intellivrBean.getPreReminderDelay() + "s");
		
		AudioType welcome = new AudioType();
		welcome.setSrc(intellivrBean.getWelcomeMessageRecordingName());
		
		AudioType reminder = new AudioType();
		reminder.setSrc(n2IvrEntityName);
		
		expectedRequest.getVxml().getPrompt().getAudioOrBreak().add(breakType);
		expectedRequest.getVxml().getPrompt().getAudioOrBreak().add(welcome);
		expectedRequest.getVxml().getPrompt().getAudioOrBreak().add(reminder);
		
		ResponseType expectedResponse = new ResponseType();
		expectedResponse.setStatus(StatusType.OK);
		
		IntellIVRServer mockServer = createMock(IntellIVRServer.class);
		
		expect(mockServer.requestCall(expectedRequest)).andReturn(expectedResponse);
		
		MessageStatusStore mockStatusStore = createMock(MessageStatusStore.class);
		
		mockStatusStore.updateStatus(mr1.getId().toString(), StatusType.OK.value());
		mockStatusStore.updateStatus(mr2.getId().toString(), StatusType.OK.value());
		
		replay(mockServer,mockStatusStore);
		
		intellivrBean.setIvrServer(mockServer);
		intellivrBean.setStatusStore(mockStatusStore);
		
		intellivrBean.requestCall(session, externalId);
			
		verify(mockServer,mockStatusStore);
	
		assertEquals(1, session.getAttempts());
		assertEquals(IVRCallSession.REPORT_WAIT, session.getState());
		assertEquals(1, session.getCalls().size());
		
		for ( IVRCall c : session.getCalls() ) {
			assertNull(c.getConnected());
			assertNull(c.getDisconnected());
			assertEquals(IVRCallStatus.REQUESTED, c.getStatus());
			assertEquals("Call request accepted", c.getStatusReason());
		}
	
	}
	
	@Test
	public void testRequestCallFatalServerError() {
		
		for ( ErrorCodeType ect : fatalServerErrorCodes ) {
			
			Date now = new Date();
			Date bundlingDelayAgo = addToDate(now, GregorianCalendar.MILLISECOND, (int)intellivrBean.getBundlingDelay()*-1);
			
			MessageRequest mr1 = getMessageRequestTemplate();
			MessageRequest mr2 = getMessageRequestTemplate();
			mr2.setNotificationType(n2);
			
			String externalId = UUID.randomUUID().toString();
			
			IVRCallSession session = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, bundlingDelayAgo, now);
			session.getMessageRequests().add(mr1);
			session.getMessageRequests().add(mr2);
			
			RequestType expectedRequest = getRequestTypeTemplate();
			expectedRequest.setCallee(phone1);
			expectedRequest.setLanguage(english.getName());
			expectedRequest.setPrivate(externalId);
			expectedRequest.setTree(n1IvrEntityName);
			
			BreakType breakType = new BreakType();
			breakType.setTime(intellivrBean.getPreReminderDelay() + "s");
			
			AudioType welcome = new AudioType();
			welcome.setSrc(intellivrBean.getWelcomeMessageRecordingName());
			
			AudioType reminder = new AudioType();
			reminder.setSrc(n2IvrEntityName);
			
			expectedRequest.getVxml().getPrompt().getAudioOrBreak().add(breakType);
			expectedRequest.getVxml().getPrompt().getAudioOrBreak().add(welcome);
			expectedRequest.getVxml().getPrompt().getAudioOrBreak().add(reminder);

			ResponseType expectedResponse = new ResponseType();
			expectedResponse.setStatus(StatusType.ERROR);
			expectedResponse.setErrorCode(ect);

			IntellIVRServer mockServer = createMock(IntellIVRServer.class);
			
			expect(mockServer.requestCall(expectedRequest)).andReturn(expectedResponse);

			MessageStatusStore mockStatusStore = createMock(MessageStatusStore.class);
			
			mockStatusStore.updateStatus(mr1.getId().toString(), ect.value());
			mockStatusStore.updateStatus(mr2.getId().toString(), ect.value());
			
			replay(mockServer,mockStatusStore);
			
			intellivrBean.setIvrServer(mockServer);
			intellivrBean.setStatusStore(mockStatusStore);
			
			intellivrBean.requestCall(session,externalId);
			
			verify(mockServer,mockStatusStore);
			
			assertEquals(1, session.getAttempts());
			assertEquals(IVRCallSession.CLOSED, session.getState());
			assertEquals(1, session.getCalls().size());
			
			for ( IVRCall c : session.getCalls() ) {
				assertNull(c.getConnected());
				assertNull(c.getDisconnected());
				assertEquals(IVRCallStatus.APIERROR, c.getStatus());
				assertEquals(ect.name() + "(" + ect.value() + ")", c.getStatusReason());
			}
			
		}
		
	}
	
	@Test
	public void testHandleReportFromOutboundCompletedOverThreshold() throws DatatypeConfigurationException {

		Date now = new Date();
		Date fiveMinutesAgo = addToDate(now, GregorianCalendar.MINUTE, -5);
		Date fourMinutesAgo = addToDate(now, GregorianCalendar.MINUTE, -4);
		Date threeMinutesAgo = addToDate(now, GregorianCalendar.MINUTE, -3);
		Date twoMinutesAgo = addToDate(now, GregorianCalendar.MINUTE, -2);
		Date fiveMinuteLessBundlingDelayAgo = addToDate(fiveMinutesAgo, GregorianCalendar.MILLISECOND, (int)intellivrBean.getBundlingDelay()*-1);
		
		MessageRequest mr1 = getMessageRequestTemplate();
		MessageRequest mr2 = getMessageRequestTemplate();
		mr2.setNotificationType(n2);
		
		String externalId = UUID.randomUUID().toString();

		IVRCallSession session = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 1, 0, IVRCallSession.REPORT_WAIT, fiveMinuteLessBundlingDelayAgo, fiveMinutesAgo);
		session.getMessageRequests().add(mr1);
		session.getMessageRequests().add(mr2);

		IVRCall call = new IVRCall(new Date(), null, null, 0, externalId, IVRCallStatus.REQUESTED, "Call request accepted",session);
		session.getCalls().add(call);	
		
		ReportType report = getReportTypeTemplateForOutboundCall();
		report.setConnectTime(toXMLGregorianCalendar(fourMinutesAgo));
		report.setDisconnectTime(toXMLGregorianCalendar(twoMinutesAgo));
		report.setPrivate(externalId);
		report.setStatus(ReportStatusType.COMPLETED);

		IvrEntryType e1 = new IvrEntryType();
		e1.setDuration(10);
		e1.setEntrytime(toXMLGregorianCalendar(threeMinutesAgo));
		e1.setMenu(n2IvrEntityName);
		
		IvrEntryType e2 = new IvrEntryType();
		e2.setDuration(30);
		e2.setEntrytime(toXMLGregorianCalendar(addToDate(threeMinutesAgo, GregorianCalendar.SECOND, 15)));
		e2.setMenu(primaryInfoRecordingName);
		
		report.getINTELLIVREntry().add(e1);
		report.getINTELLIVREntry().add(e2);
		
		report.setINTELLIVREntryCount(report.getINTELLIVREntryCount() + 2);
		report.setDuration(report.getDuration() + e1.getDuration() + e2.getDuration());
		
		ResponseType expectedResponse = new ResponseType();
		expectedResponse.setStatus(StatusType.OK);

		IVRDAO mockDao = createMock(IVRDAO.class);

		expect(mockDao.loadIVRCallByExternalId(externalId)).andReturn(call);
		
		MessageStatusStore mockStatusStore = createMock(MessageStatusStore.class);

		mockStatusStore.updateStatus(mr1.getId().toString(), IVRCallStatus.COMPLETED.name());
		mockStatusStore.updateStatus(mr2.getId().toString(), IVRCallStatus.COMPLETED.name());
		
		replay(mockDao,mockStatusStore);

		intellivrBean.setIvrDao(mockDao);
		intellivrBean.setStatusStore(mockStatusStore);
		
		assertEquals(expectedResponse, intellivrBean.handleReport(report));
		
		assertEquals(fourMinutesAgo, call.getConnected());
		assertEquals(twoMinutesAgo, call.getDisconnected());
		assertEquals(report.getDuration(), call.getDuration());
		assertEquals(externalId, call.getExternalId());
		assertEquals(IVRCallStatus.COMPLETED, call.getStatus());
		assertEquals("", call.getStatusReason());
		
		assertEquals(4, call.getMenus().size());
		
		assertEquals(IVRCallSession.CLOSED, session.getState());
		assertEquals(1, session.getAttempts());
		assertEquals(0, session.getDays());
		
		verify(mockDao,mockStatusStore);
		
	}
	
	@Test
	public void testHandleReportFromOutboundCompletedUnderThreshold() throws DatatypeConfigurationException {
		
		Date now = new Date();
		Date fiveMinutesAgo = addToDate(now, GregorianCalendar.MINUTE, -5);
		Date fourMinutesAgo = addToDate(now, GregorianCalendar.MINUTE, -4);
		Date threeMinutesAgo = addToDate(now, GregorianCalendar.MINUTE, -3);
		Date twoMinutesAgo = addToDate(now, GregorianCalendar.MINUTE, -2);
		Date fiveMinuteLessBundlingDelayAgo = addToDate(fiveMinutesAgo, GregorianCalendar.MILLISECOND, (int)intellivrBean.getBundlingDelay()*-1);
		
		MessageRequest mr1 = getMessageRequestTemplate();
		MessageRequest mr2 = getMessageRequestTemplate();
		mr2.setNotificationType(n2);

		String externalId = UUID.randomUUID().toString();

		IVRCallSession session = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 1, 0, IVRCallSession.REPORT_WAIT, fiveMinuteLessBundlingDelayAgo, fiveMinutesAgo);
		session.getMessageRequests().add(mr1);
		session.getMessageRequests().add(mr2);

		IVRCall call = new IVRCall(new Date(), null, null, 0, externalId, IVRCallStatus.REQUESTED, "Call request accepted",session);
		session.getCalls().add(call);	
		
		ReportType report = getReportTypeTemplateForOutboundCall();
		report.setConnectTime(toXMLGregorianCalendar(fourMinutesAgo));
		report.setDisconnectTime(toXMLGregorianCalendar(twoMinutesAgo));
		report.setPrivate(externalId);
		report.setStatus(ReportStatusType.COMPLETED);

		IvrEntryType e1 = new IvrEntryType();
		e1.setDuration(10);
		e1.setEntrytime(toXMLGregorianCalendar(threeMinutesAgo));
		e1.setMenu(n2IvrEntityName);
		
		IvrEntryType e2 = new IvrEntryType();
		e2.setDuration(10);
		e2.setEntrytime(toXMLGregorianCalendar(addToDate(threeMinutesAgo, GregorianCalendar.SECOND, 15)));
		e2.setMenu(primaryInfoRecordingName);
		
		report.getINTELLIVREntry().add(e1);
		report.getINTELLIVREntry().add(e2);
		
		report.setINTELLIVREntryCount(report.getINTELLIVREntryCount() + 2);
		report.setDuration(report.getDuration() + e1.getDuration() + e2.getDuration());

		ResponseType expectedResponse = new ResponseType();
		expectedResponse.setStatus(StatusType.OK);

		IVRDAO mockDao = createMock(IVRDAO.class);

		expect(mockDao.loadIVRCallByExternalId(externalId)).andReturn(call);
		
		MessageStatusStore mockStatusStore = createMock(MessageStatusStore.class);

		mockStatusStore.updateStatus(mr1.getId().toString(), IVRCallStatus.BELOWTHRESHOLD.name());
		mockStatusStore.updateStatus(mr2.getId().toString(), IVRCallStatus.BELOWTHRESHOLD.name());
		
		replay(mockDao,mockStatusStore);

		intellivrBean.setIvrDao(mockDao);
		intellivrBean.setStatusStore(mockStatusStore);
		
		assertEquals(expectedResponse, intellivrBean.handleReport(report));

		assertEquals(fourMinutesAgo, call.getConnected());
		assertEquals(twoMinutesAgo, call.getDisconnected());
		assertEquals(report.getDuration(), call.getDuration());
		assertEquals(externalId, call.getExternalId());
		assertEquals(IVRCallStatus.BELOWTHRESHOLD, call.getStatus());
		assertEquals("", call.getStatusReason());
		
		assertEquals(4, call.getMenus().size());
		
		assertEquals(IVRCallSession.SEND_WAIT, session.getState());
		assertEquals(1, session.getAttempts());
		assertEquals(0, session.getDays());
		
		verify(mockDao,mockStatusStore);

	}
	
	@Test
	public void testHandleReportFromInboundCompletedOverThreshold() throws DatatypeConfigurationException {
		
		Date now = new Date();
		Date fourMinutesAgo = addToDate(now, GregorianCalendar.MINUTE, -4);
		Date threeMinutesAgo = addToDate(now, GregorianCalendar.MINUTE, -3);
		Date twoMinutesAgo = addToDate(now, GregorianCalendar.MINUTE, -2);

		MessageRequest mr1 = getMessageRequestTemplate();
		MessageRequest mr2 = getMessageRequestTemplate();
		mr2.setNotificationType(n2);

		String externalId = UUID.randomUUID().toString();

		IVRCallSession session = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.INBOUND, 1, 0, IVRCallSession.REPORT_WAIT, threeMinutesAgo, null);
		session.getMessageRequests().add(mr1);
		session.getMessageRequests().add(mr2);

		IVRCall call = new IVRCall(new Date(), null, null, 0, externalId, IVRCallStatus.REQUESTED, "Call request accepted", session);
		session.getCalls().add(call);	

		ReportType report = getReportTypeTemplateForInboundCall();
		report.setConnectTime(toXMLGregorianCalendar(fourMinutesAgo));
		report.setDisconnectTime(toXMLGregorianCalendar(twoMinutesAgo));
		report.setPrivate(externalId);
		report.setStatus(ReportStatusType.COMPLETED);

		IvrEntryType e1 = new IvrEntryType();
		e1.setDuration(10);
		e1.setEntrytime(toXMLGregorianCalendar(threeMinutesAgo));
		e1.setMenu(n2IvrEntityName);
		
		IvrEntryType e2 = new IvrEntryType();
		e2.setDuration(30);
		e2.setEntrytime(toXMLGregorianCalendar(addToDate(threeMinutesAgo, GregorianCalendar.SECOND, 15)));
		e2.setMenu(primaryInfoRecordingName);
		
		report.getINTELLIVREntry().add(e1);
		report.getINTELLIVREntry().add(e2);
		
		report.setINTELLIVREntryCount(report.getINTELLIVREntryCount() + 2);
		report.setDuration(report.getDuration() + e1.getDuration() + e2.getDuration());
		
		ResponseType expectedResponse = new ResponseType();
		expectedResponse.setStatus(StatusType.OK);

		IVRDAO mockDao = createMock(IVRDAO.class);

		expect(mockDao.loadIVRCallByExternalId(externalId)).andReturn(call);
		
		MessageStatusStore mockStatusStore = createMock(MessageStatusStore.class);

		mockStatusStore.updateStatus(mr1.getId().toString(), IVRCallStatus.COMPLETED.name());
		mockStatusStore.updateStatus(mr2.getId().toString(), IVRCallStatus.COMPLETED.name());
		
		replay(mockDao,mockStatusStore);

		intellivrBean.setIvrDao(mockDao);
		intellivrBean.setStatusStore(mockStatusStore);
		
		assertEquals(expectedResponse, intellivrBean.handleReport(report));
		
		assertEquals(fourMinutesAgo, call.getConnected());
		assertEquals(twoMinutesAgo, call.getDisconnected());
		assertEquals(report.getDuration(), call.getDuration());
		assertEquals(externalId, call.getExternalId());
		assertEquals(IVRCallStatus.COMPLETED, call.getStatus());
		assertEquals("", call.getStatusReason());
		
		assertEquals(6, call.getMenus().size());
		
		assertEquals(IVRCallSession.CLOSED, session.getState());
		assertEquals(1, session.getAttempts());
		assertEquals(0, session.getDays());
		
		verify(mockDao,mockStatusStore);

	}
	
	@Test
	public void testHandleReportFromInboundCompletedUnderThreshold() throws DatatypeConfigurationException {
		
		Date now = new Date();
		Date fourMinutesAgo = addToDate(now, GregorianCalendar.MINUTE, -4);
		Date threeMinutesAgo = addToDate(now, GregorianCalendar.MINUTE, -3);
		Date twoMinutesAgo = addToDate(now, GregorianCalendar.MINUTE, -2);
	
		MessageRequest mr1 = getMessageRequestTemplate();
		MessageRequest mr2 = getMessageRequestTemplate();
		mr2.setNotificationType(n2);

		String externalId = UUID.randomUUID().toString();

		IVRCallSession session = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.INBOUND, 1, 0, IVRCallSession.REPORT_WAIT, threeMinutesAgo, null);
		session.getMessageRequests().add(mr1);
		session.getMessageRequests().add(mr2);

		IVRCall call = new IVRCall(new Date(), null, null, 0, externalId, IVRCallStatus.REQUESTED, "Call request accepted", session);
		session.getCalls().add(call);	
		
		ReportType report = getReportTypeTemplateForInboundCall();
		report.setConnectTime(toXMLGregorianCalendar(fourMinutesAgo));
		report.setDisconnectTime(toXMLGregorianCalendar(twoMinutesAgo));
		report.setPrivate(externalId);
		report.setStatus(ReportStatusType.COMPLETED);

		IvrEntryType e1 = new IvrEntryType();
		e1.setDuration(10);
		e1.setEntrytime(toXMLGregorianCalendar(threeMinutesAgo));
		e1.setMenu(n2IvrEntityName);
		
		IvrEntryType e2 = new IvrEntryType();
		e2.setDuration(10);
		e2.setEntrytime(toXMLGregorianCalendar(addToDate(threeMinutesAgo, GregorianCalendar.SECOND, 15)));
		e2.setMenu(primaryInfoRecordingName);
		
		report.getINTELLIVREntry().add(e1);
		report.getINTELLIVREntry().add(e2);
		
		report.setINTELLIVREntryCount(report.getINTELLIVREntryCount() + 2);
		report.setDuration(report.getDuration() + e1.getDuration() + e2.getDuration());

		ResponseType expectedResponse = new ResponseType();
		expectedResponse.setStatus(StatusType.OK);

		IVRDAO mockDao = createMock(IVRDAO.class);

		expect(mockDao.loadIVRCallByExternalId(externalId)).andReturn(call);
		
		MessageStatusStore mockStatusStore = createMock(MessageStatusStore.class);

		replay(mockDao,mockStatusStore);

		intellivrBean.setIvrDao(mockDao);
		intellivrBean.setStatusStore(mockStatusStore);
		
		assertEquals(expectedResponse, intellivrBean.handleReport(report));

		assertEquals(fourMinutesAgo, call.getConnected());
		assertEquals(twoMinutesAgo, call.getDisconnected());
		assertEquals(report.getDuration(), call.getDuration());
		assertEquals(externalId, call.getExternalId());
		assertEquals(IVRCallStatus.COMPLETED, call.getStatus());
		assertEquals("", call.getStatusReason());
		
		assertEquals(6, call.getMenus().size());
		
		assertEquals(IVRCallSession.CLOSED, session.getState());
		assertEquals(1, session.getAttempts());
		assertEquals(0, session.getDays());
		
		verify(mockDao,mockStatusStore);

	}
	
	@Test
	public void testHandleReportFromInboundReplayDeliveredMessagesCompletedOverThreshold() throws DatatypeConfigurationException {
		
		Date now = new Date();
		Date fourMinutesAgo = addToDate(now, GregorianCalendar.MINUTE, -4);
		Date threeMinutesAgo = addToDate(now, GregorianCalendar.MINUTE, -3);
		Date twoMinutesAgo = addToDate(now, GregorianCalendar.MINUTE, -2);

		MessageRequest mr1 = getMessageRequestTemplate();
		mr1.setStatus(MStatus.DELIVERED);
		MessageRequest mr2 = getMessageRequestTemplate();
		mr2.setNotificationType(n2);
		mr2.setStatus(MStatus.DELIVERED);

		String externalId = UUID.randomUUID().toString();

		IVRCallSession session = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.INBOUND, 1, 0, IVRCallSession.REPORT_WAIT, threeMinutesAgo, null);
		session.getMessageRequests().add(mr1);
		session.getMessageRequests().add(mr2);

		IVRCall call = new IVRCall(new Date(), null, null, 0, externalId, IVRCallStatus.REQUESTED, "Call request accepted", session);
		session.getCalls().add(call);	

		ReportType report = getReportTypeTemplateForInboundCall();
		report.setConnectTime(toXMLGregorianCalendar(fourMinutesAgo));
		report.setDisconnectTime(toXMLGregorianCalendar(twoMinutesAgo));
		report.setPrivate(externalId);
		report.setStatus(ReportStatusType.COMPLETED);

		IvrEntryType e1 = new IvrEntryType();
		e1.setDuration(10);
		e1.setEntrytime(toXMLGregorianCalendar(threeMinutesAgo));
		e1.setMenu(n2IvrEntityName);
		
		IvrEntryType e2 = new IvrEntryType();
		e2.setDuration(30);
		e2.setEntrytime(toXMLGregorianCalendar(addToDate(threeMinutesAgo, GregorianCalendar.SECOND, 15)));
		e2.setMenu(primaryInfoRecordingName);
		
		report.getINTELLIVREntry().add(e1);
		report.getINTELLIVREntry().add(e2);
		
		report.setINTELLIVREntryCount(report.getINTELLIVREntryCount() + 2);
		report.setDuration(report.getDuration() + e1.getDuration() + e2.getDuration());
		
		ResponseType expectedResponse = new ResponseType();
		expectedResponse.setStatus(StatusType.OK);

		IVRDAO mockDao = createMock(IVRDAO.class);

		expect(mockDao.loadIVRCallByExternalId(externalId)).andReturn(call);
		
		MessageStatusStore mockStatusStore = createMock(MessageStatusStore.class);

		mockStatusStore.updateStatus(mr1.getId().toString(), IVRCallStatus.COMPLETED.name());
		mockStatusStore.updateStatus(mr2.getId().toString(), IVRCallStatus.COMPLETED.name());
		
		replay(mockDao,mockStatusStore);

		intellivrBean.setIvrDao(mockDao);
		intellivrBean.setStatusStore(mockStatusStore);
		
		assertEquals(expectedResponse, intellivrBean.handleReport(report));
		
		assertEquals(fourMinutesAgo, call.getConnected());
		assertEquals(twoMinutesAgo, call.getDisconnected());
		assertEquals(report.getDuration(), call.getDuration());
		assertEquals(externalId, call.getExternalId());
		assertEquals(IVRCallStatus.COMPLETED, call.getStatus());
		assertEquals("", call.getStatusReason());
		
		assertEquals(6, call.getMenus().size());
		
		assertEquals(IVRCallSession.CLOSED, session.getState());
		assertEquals(1, session.getAttempts());
		assertEquals(0, session.getDays());
		
		verify(mockDao,mockStatusStore);

	}

	
	@Test
	public void testHandleReportFromInboundReplayDeliveredMessagesCompletedUnderThreshold() throws DatatypeConfigurationException {
		
		Date now = new Date();
		Date fourMinutesAgo = addToDate(now, GregorianCalendar.MINUTE, -4);
		Date threeMinutesAgo = addToDate(now, GregorianCalendar.MINUTE, -3);
		Date twoMinutesAgo = addToDate(now, GregorianCalendar.MINUTE, -2);
	
		MessageRequest mr1 = getMessageRequestTemplate();
		mr1.setStatus(MStatus.DELIVERED);
		MessageRequest mr2 = getMessageRequestTemplate();
		mr2.setNotificationType(n2);
		mr2.setStatus(MStatus.DELIVERED);

		String externalId = UUID.randomUUID().toString();

		IVRCallSession session = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.INBOUND, 1, 0, IVRCallSession.REPORT_WAIT, threeMinutesAgo, null);
		session.getMessageRequests().add(mr1);
		session.getMessageRequests().add(mr2);

		IVRCall call = new IVRCall(new Date(), null, null, 0, externalId, IVRCallStatus.REQUESTED, "Call request accepted", session);
		session.getCalls().add(call);	
		
		ReportType report = getReportTypeTemplateForInboundCall();
		report.setConnectTime(toXMLGregorianCalendar(fourMinutesAgo));
		report.setDisconnectTime(toXMLGregorianCalendar(twoMinutesAgo));
		report.setPrivate(externalId);
		report.setStatus(ReportStatusType.COMPLETED);

		IvrEntryType e1 = new IvrEntryType();
		e1.setDuration(10);
		e1.setEntrytime(toXMLGregorianCalendar(threeMinutesAgo));
		e1.setMenu(n2IvrEntityName);
		
		IvrEntryType e2 = new IvrEntryType();
		e2.setDuration(10);
		e2.setEntrytime(toXMLGregorianCalendar(addToDate(threeMinutesAgo, GregorianCalendar.SECOND, 15)));
		e2.setMenu(primaryInfoRecordingName);
		
		report.getINTELLIVREntry().add(e1);
		report.getINTELLIVREntry().add(e2);
		
		report.setINTELLIVREntryCount(report.getINTELLIVREntryCount() + 2);
		report.setDuration(report.getDuration() + e1.getDuration() + e2.getDuration());

		ResponseType expectedResponse = new ResponseType();
		expectedResponse.setStatus(StatusType.OK);

		IVRDAO mockDao = createMock(IVRDAO.class);

		expect(mockDao.loadIVRCallByExternalId(externalId)).andReturn(call);
		
		MessageStatusStore mockStatusStore = createMock(MessageStatusStore.class);

		replay(mockDao,mockStatusStore);

		intellivrBean.setIvrDao(mockDao);
		intellivrBean.setStatusStore(mockStatusStore);
		
		assertEquals(expectedResponse, intellivrBean.handleReport(report));

		assertEquals(fourMinutesAgo, call.getConnected());
		assertEquals(twoMinutesAgo, call.getDisconnected());
		assertEquals(report.getDuration(), call.getDuration());
		assertEquals(externalId, call.getExternalId());
		assertEquals(IVRCallStatus.COMPLETED, call.getStatus());
		assertEquals("", call.getStatusReason());
		
		assertEquals(6, call.getMenus().size());
		
		assertEquals(IVRCallSession.CLOSED, session.getState());
		assertEquals(1, session.getAttempts());
		assertEquals(0, session.getDays());
		
		verify(mockDao,mockStatusStore);

	}

	
	@Test
	public void testHandleReportFromOutboundCallFailedFirstAttemptFirstDay() {
		
		for ( ReportStatusType rst : reportFailedStatusTypes ) {
	
			Date now = new Date();
			Date fourMinutesAgo = addToDate(now, GregorianCalendar.MINUTE, -4);
			Date fourMinuteLessBundlingDelayAgo = addToDate(fourMinutesAgo, GregorianCalendar.MILLISECOND, (int)intellivrBean.getBundlingDelay()*-1);
			
			MessageRequest mr1 = getMessageRequestTemplate();
			MessageRequest mr2 = getMessageRequestTemplate();
			mr2.setNotificationType(n2);

			String externalId = UUID.randomUUID().toString();

			IVRCallSession session = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 1, 0, IVRCallSession.REPORT_WAIT, fourMinuteLessBundlingDelayAgo, fourMinutesAgo);
			session.getMessageRequests().add(mr1);
			session.getMessageRequests().add(mr2);

			IVRCall call = new IVRCall(new Date(), null, null, 0, externalId, IVRCallStatus.REQUESTED, "Call request accepted", session);
			session.getCalls().add(call);	

			ReportType report = new ReportType();
			report.setConnectTime(null);
			report.setDisconnectTime(null);
			report.setDuration(0);
			report.setINTELLIVREntryCount(0);
			report.setPrivate(externalId);
			report.setStatus(rst);

			IVRDAO mockDao = createMock(IVRDAO.class);
			intellivrBean.setIvrDao(mockDao);
			
			expect(mockDao.loadIVRCallByExternalId(externalId)).andReturn(call);
			
			MessageStatusStore mockStatusStore = createMock(MessageStatusStore.class);
			intellivrBean.setStatusStore(mockStatusStore);
			
			mockStatusStore.updateStatus(mr1.getId().toString(), rst.value());
			mockStatusStore.updateStatus(mr2.getId().toString(), rst.value());
			
			replay(mockDao,mockStatusStore);
			
			ResponseType expectedResponse = new ResponseType();
			expectedResponse.setStatus(StatusType.OK);

			assertEquals(expectedResponse, intellivrBean.handleReport(report));
			
			Date expectedNextAttempt = addToDate(fourMinutesAgo, GregorianCalendar.MINUTE, intellivrBean.getRetryDelay());
			assertEquals(expectedNextAttempt, session.getNextAttempt());
			assertEquals(IVRCallSession.SEND_WAIT, session.getState());
			assertEquals(0, session.getDays());
			assertEquals(1, session.getAttempts());
						
			verify(mockDao,mockStatusStore);
			
		}
			
	}
	
	@Test
	public void testHandleReportFromOutboundCallFailedLastAttemptFirstDay() {
		
		for ( ReportStatusType rst : reportFailedStatusTypes ) {
	
			Date now = new Date();
			Date fourMinutesAgo = addToDate(now, GregorianCalendar.MINUTE, -4);
			Date fourMinuteLessBundlingDelayAgo = addToDate(fourMinutesAgo, GregorianCalendar.MILLISECOND, (int)intellivrBean.getBundlingDelay()*-1);
			
			MessageRequest mr1 = getMessageRequestTemplate();
			MessageRequest mr2 = getMessageRequestTemplate();
			mr2.setNotificationType(n2);

			String externalId = UUID.randomUUID().toString();

			IVRCallSession session = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, intellivrBean.getMaxAttempts(), 0, IVRCallSession.REPORT_WAIT, fourMinuteLessBundlingDelayAgo, fourMinutesAgo);
			session.getMessageRequests().add(mr1);
			session.getMessageRequests().add(mr2);

			IVRCall call = new IVRCall(new Date(), null, null, 0, externalId, IVRCallStatus.REQUESTED, "Call request accepted", session);
			session.getCalls().add(call);	

			ReportType report = new ReportType();
			report.setConnectTime(null);
			report.setDisconnectTime(null);
			report.setDuration(0);
			report.setINTELLIVREntryCount(0);
			report.setPrivate(externalId);
			report.setStatus(rst);

			IVRDAO mockDao = createMock(IVRDAO.class);
			intellivrBean.setIvrDao(mockDao);
			
			expect(mockDao.loadIVRCallByExternalId(externalId)).andReturn(call);
			
			CoreManager mockCoreManager = createMock(CoreManager.class);
			intellivrBean.setCoreManager(mockCoreManager);
			
			MessageStatusStore mockStatusStore = createMock(MessageStatusStore.class);
			intellivrBean.setStatusStore(mockStatusStore);
			
			mockStatusStore.updateStatus(mr1.getId().toString(), rst.value());
			mockStatusStore.updateStatus(mr2.getId().toString(), rst.value());
			
			replay(mockDao,mockCoreManager,mockStatusStore);
			
			ResponseType expectedResponse = new ResponseType();
			expectedResponse.setStatus(StatusType.OK);

			assertEquals(expectedResponse, intellivrBean.handleReport(report));
			assertEquals(IVRCallSession.SEND_WAIT, session.getState());
			assertEquals(1, session.getDays());
			assertEquals(0, session.getAttempts());
			
			Date expectedNextAttempt = addToDate(fourMinuteLessBundlingDelayAgo, GregorianCalendar.DAY_OF_MONTH, 1);
			assertEquals(expectedNextAttempt, session.getNextAttempt());
			
			verify(mockDao,mockCoreManager,mockStatusStore);
			
		}
			
	}

	@Test
	public void testHandleReportFromOutboundCallFailedLastAttemptLastDay() {
		
		for ( ReportStatusType rst : reportFailedStatusTypes ) {
	
			Date now = new Date();
			Date fourMinutesAgo = addToDate(now, GregorianCalendar.MINUTE, -4);
			Date fourMinuteLessBundlingDelayAgo = addToDate(fourMinutesAgo, GregorianCalendar.MILLISECOND, (int)intellivrBean.getBundlingDelay()*-1);
			
			MessageRequest mr1 = getMessageRequestTemplate();
			MessageRequest mr2 = getMessageRequestTemplate();
			mr2.setNotificationType(n2);
			
			String externalId = UUID.randomUUID().toString();

			IVRCallSession session = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, intellivrBean.getMaxAttempts(), intellivrBean.getMaxDays()-1, IVRCallSession.REPORT_WAIT, fourMinuteLessBundlingDelayAgo, fourMinutesAgo);
			session.getMessageRequests().add(mr1);
			session.getMessageRequests().add(mr2);

			IVRCall call = new IVRCall(new Date(), null, null, 0, externalId, IVRCallStatus.REQUESTED, "Call request accepted", session);
			session.getCalls().add(call);	

			ReportType report = new ReportType();
			report.setConnectTime(null);
			report.setDisconnectTime(null);
			report.setDuration(0);
			report.setINTELLIVREntryCount(0);
			report.setPrivate(externalId);
			report.setStatus(rst);

			IVRDAO mockDao = createMock(IVRDAO.class);
			intellivrBean.setIvrDao(mockDao);
			
			expect(mockDao.loadIVRCallByExternalId(externalId)).andReturn(call);
			
			MessageStatusStore mockStatusStore = createMock(MessageStatusStore.class);
			intellivrBean.setStatusStore(mockStatusStore);
			
			mockStatusStore.updateStatus(mr1.getId().toString(), "MAXATTEMPTS");
			mockStatusStore.updateStatus(mr2.getId().toString(), "MAXATTEMPTS");
			
			replay(mockDao,mockStatusStore);
			
			ResponseType expectedResponse = new ResponseType();
			expectedResponse.setStatus(StatusType.OK);

			assertEquals(expectedResponse, intellivrBean.handleReport(report));
			assertEquals(intellivrBean.getMaxDays(), session.getDays());
			assertEquals(0, session.getAttempts());
			assertEquals(fourMinutesAgo, session.getNextAttempt());
			assertEquals(IVRCallSession.CLOSED, session.getState());
			
			verify(mockDao,mockStatusStore);
			
		}
			
	}

	@Test
	public void testCallExceedsThresholdOneInfoCompletedOverThreshold() {

		Date now = new Date();
		Date fourMinutesAgo = addToDate(now, GregorianCalendar.MINUTE, -4);
		Date fourMinuteLessBundlingDelayAgo = addToDate(fourMinutesAgo, GregorianCalendar.MILLISECOND, (int)intellivrBean.getBundlingDelay()*-1);
		
		MessageRequest mr = getMessageRequestTemplate();
		
		IVRCallSession session = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.REPORT_WAIT, fourMinuteLessBundlingDelayAgo, fourMinutesAgo);
		session.getMessageRequests().add(mr);
		
		ReportType report = getReportTypeTemplateForOutboundCall();
		report.setPrivate(UUID.randomUUID().toString());
		report.setStatus(ReportStatusType.COMPLETED);
		
		IvrEntryType e = new IvrEntryType();
		e.setDuration(60);
		e.setMenu(primaryInfoRecordingName);
		
		List<IvrEntryType> entries = report.getINTELLIVREntry();
		entries.add(e);

		report.setDuration(report.getDuration() + e.getDuration());
		
		assertTrue(intellivrBean.callExceedsThreshold(session,report));
		
	}

	@Test
	public void testCallExceedsThresholdOneInfoCompletedUnderThreshold() {

		Date now = new Date();
		Date fourMinutesAgo = addToDate(now, GregorianCalendar.MINUTE, -4);
		Date fourMinuteLessBundlingDelayAgo = addToDate(fourMinutesAgo, GregorianCalendar.MILLISECOND, (int)intellivrBean.getBundlingDelay()*-1);
		
		MessageRequest mr = getMessageRequestTemplate();
	
		IVRCallSession session = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.REPORT_WAIT, fourMinuteLessBundlingDelayAgo, fourMinutesAgo);
		session.getMessageRequests().add(mr);
		
		ReportType report = getReportTypeTemplateForOutboundCall();
		report.setPrivate(UUID.randomUUID().toString());
		report.setStatus(ReportStatusType.COMPLETED);
		
		IvrEntryType e = new IvrEntryType();
		e.setDuration(10);
		e.setMenu(primaryInfoRecordingName);
		
		List<IvrEntryType> entries = report.getINTELLIVREntry();
		entries.add(e);

		report.setDuration(report.getDuration() + e.getDuration());
		
		assertFalse(intellivrBean.callExceedsThreshold(session,report));
		
	}
	
	@Test
	public void testCallExceedsThresholdOneReminderCompleted(){

		Date now = new Date();
		Date fourMinutesAgo = addToDate(now, GregorianCalendar.MINUTE, -4);
		Date fourMinuteLessBundlingDelayAgo = addToDate(fourMinutesAgo, GregorianCalendar.MILLISECOND, (int)intellivrBean.getBundlingDelay()*-1);
		
		MessageRequest mr = getMessageRequestTemplate();
		mr.setNotificationType(n2);
		
		IVRCallSession session = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.REPORT_WAIT, fourMinuteLessBundlingDelayAgo, fourMinutesAgo);
		session.getMessageRequests().add(mr);
		
		ReportType report = getReportTypeTemplateForOutboundCall();
		report.setPrivate(UUID.randomUUID().toString());
		report.setStatus(ReportStatusType.COMPLETED);
		
		IvrEntryType e = new IvrEntryType();
		e.setDuration(13);
		e.setMenu(n2IvrEntityName);
		
		report.getINTELLIVREntry().add(e);
		
		report.setDuration(report.getDuration() + e.getDuration());
		
		assertTrue(intellivrBean.callExceedsThreshold(session,report));
		
	}
	
	@Test
	public void testCallExceedsThresholdOneReminderCompletedRequestContainedInfoMessage(){

		Date now = new Date();
		Date fourMinutesAgo = addToDate(now, GregorianCalendar.MINUTE, -4);
		Date fourMinuteLessBundlingDelayAgo = addToDate(fourMinutesAgo, GregorianCalendar.MILLISECOND, (int)intellivrBean.getBundlingDelay()*-1);
		
		MessageRequest mr1 = getMessageRequestTemplate();
		mr1.setNotificationType(n1);
		
		MessageRequest mr2 = getMessageRequestTemplate();
		mr2.setNotificationType(n2);
		
		IVRCallSession session = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.REPORT_WAIT, fourMinuteLessBundlingDelayAgo, fourMinutesAgo);
		session.getMessageRequests().add(mr1);
		session.getMessageRequests().add(mr2);
		
		ReportType report = getReportTypeTemplateForOutboundCall();
		report.setPrivate(UUID.randomUUID().toString());
		report.setStatus(ReportStatusType.COMPLETED);
		
		IvrEntryType e = new IvrEntryType();
		e.setDuration(13);
		e.setMenu(n2IvrEntityName);
		
		report.getINTELLIVREntry().add(e);
		
		report.setDuration(report.getDuration() + e.getDuration());
		
		assertFalse(intellivrBean.callExceedsThreshold(session,report));
		
	}

	
	@Test
	public void testCallExceedsThresholdMixedCompletedOverThreshold() {
	
		Date now = new Date();
		Date fourMinutesAgo = addToDate(now, GregorianCalendar.MINUTE, -4);
		Date fourMinuteLessBundlingDelayAgo = addToDate(fourMinutesAgo, GregorianCalendar.MILLISECOND, (int)intellivrBean.getBundlingDelay()*-1);
		
		MessageRequest mr1 = getMessageRequestTemplate();
		MessageRequest mr2 = getMessageRequestTemplate();
		mr2.setNotificationType(n2);

		IVRCallSession session = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.REPORT_WAIT, fourMinuteLessBundlingDelayAgo, fourMinutesAgo);
		session.getMessageRequests().add(mr1);
		session.getMessageRequests().add(mr2);
		
		ReportType report = getReportTypeTemplateForOutboundCall();
		report.setPrivate(UUID.randomUUID().toString());
		report.setStatus(ReportStatusType.COMPLETED);

		IvrEntryType e1 = new IvrEntryType();
		e1.setDuration(10);
		e1.setMenu(n2IvrEntityName);
		
		IvrEntryType e2 = new IvrEntryType();
		e2.setDuration(30);
		e2.setMenu(primaryInfoRecordingName);
		
		report.getINTELLIVREntry().add(e1);
		report.getINTELLIVREntry().add(e2);
		
		report.setDuration(report.getDuration() + e1.getDuration() + e2.getDuration());
		
		assertTrue(intellivrBean.callExceedsThreshold(session,report));	
		
	}

	@Test
	public void testCallExceedsThresholdMixedCompletedUnderThreshold() {
		
		Date now = new Date();
		Date fourMinutesAgo = addToDate(now, GregorianCalendar.MINUTE, -4);
		Date fourMinuteLessBundlingDelayAgo = addToDate(fourMinutesAgo, GregorianCalendar.MILLISECOND, (int)intellivrBean.getBundlingDelay()*-1);
		
		MessageRequest mr1 = getMessageRequestTemplate();
		MessageRequest mr2 = getMessageRequestTemplate();
		mr2.setNotificationType(n2);

		IVRCallSession session = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.REPORT_WAIT, fourMinuteLessBundlingDelayAgo, fourMinutesAgo);
		session.getMessageRequests().add(mr1);
		session.getMessageRequests().add(mr2);

		ReportType report = getReportTypeTemplateForOutboundCall();
		report.setPrivate(UUID.randomUUID().toString());
		report.setStatus(ReportStatusType.COMPLETED);

		IvrEntryType e1 = new IvrEntryType();
		e1.setDuration(10);
		e1.setMenu(n2IvrEntityName);
		
		IvrEntryType e2 = new IvrEntryType();
		e2.setDuration(10);
		e2.setMenu(primaryInfoRecordingName);
		
		report.getINTELLIVREntry().add(e1);
		report.getINTELLIVREntry().add(e2);
		
		report.setDuration(report.getDuration() + e1.getDuration() + e2.getDuration());
		
		assertFalse(intellivrBean.callExceedsThreshold(session,report));	
		
	}

	@Test
	public void testCallExceedsThresholdMixedInboundCompletedOverThreshold() {
	
		Date now = new Date();
		Date fourMinutesAgo = addToDate(now, GregorianCalendar.MINUTE, -4);
		
		MessageRequest mr1 = getMessageRequestTemplate();
		MessageRequest mr2 = getMessageRequestTemplate();
		mr2.setNotificationType(n2);
		
		IVRCallSession session = new IVRCallSession(recipientId1, null, english.getName(), IVRCallSession.INBOUND, 0, 0, IVRCallSession.REPORT_WAIT, fourMinutesAgo, null);
		session.getMessageRequests().add(mr1);
		session.getMessageRequests().add(mr2);

		ReportType report = getReportTypeTemplateForInboundCall();
		report.setPrivate(UUID.randomUUID().toString());
		report.setStatus(ReportStatusType.COMPLETED);
		
		IvrEntryType e1 = new IvrEntryType();
		e1.setDuration(10);
		e1.setMenu(n2IvrEntityName);
		
		IvrEntryType e2 = new IvrEntryType();
		e2.setDuration(30);
		e2.setMenu(primaryInfoRecordingName);
		
		IvrEntryType e3 = new IvrEntryType();
		e3.setDuration(10);
		e3.setMenu(secondaryInfoRecordingName);
		
		report.getINTELLIVREntry().add(e1);
		report.getINTELLIVREntry().add(e2);
		report.getINTELLIVREntry().add(e3);
		
		report.setDuration(report.getDuration() + e1.getDuration() + e2.getDuration());
		
		assertTrue(intellivrBean.callExceedsThreshold(session,report));
		
	}
	
	@Test
	public void testCallExceedsThresholdMixedInboundCompletedUnderThreshold() {

		Date now = new Date();
		Date fourMinutesAgo = addToDate(now, GregorianCalendar.MINUTE, -4);
		
		MessageRequest mr1 = getMessageRequestTemplate();
		MessageRequest mr2 = getMessageRequestTemplate();
		mr2.setNotificationType(n2);
		
		IVRCallSession session = new IVRCallSession(recipientId1, null, english.getName(), IVRCallSession.INBOUND, 0, 0, IVRCallSession.REPORT_WAIT, fourMinutesAgo, null);
		session.getMessageRequests().add(mr1);
		session.getMessageRequests().add(mr2);

		ReportType report = getReportTypeTemplateForInboundCall();
		report.setPrivate(UUID.randomUUID().toString());
		report.setStatus(ReportStatusType.COMPLETED);
		
		IvrEntryType e1 = new IvrEntryType();
		e1.setDuration(10);
		e1.setMenu(n2IvrEntityName);
		
		IvrEntryType e2 = new IvrEntryType();
		e2.setDuration(10);
		e2.setMenu(primaryInfoRecordingName);
		
		IvrEntryType e3 = new IvrEntryType();
		e3.setDuration(30);
		e3.setMenu(secondaryInfoRecordingName);
		
		report.getINTELLIVREntry().add(e1);
		report.getINTELLIVREntry().add(e2);
		report.getINTELLIVREntry().add(e3);
		
		report.setDuration(report.getDuration() + e1.getDuration() + e2.getDuration());
		
		assertFalse(intellivrBean.callExceedsThreshold(session,report));
		
	}
	
	@Test
	public void testCreateRequestTypeOutboundCall() {
		
		Date now = new Date();
		Date fourMinutesAgo = addToDate(now, GregorianCalendar.MINUTE, -4);
		Date fourMinuteLessBundlingDelayAgo = addToDate(fourMinutesAgo, GregorianCalendar.MILLISECOND, (int)intellivrBean.getBundlingDelay()*-1);
		
		MessageRequest mr1 = getMessageRequestTemplate();
		MessageRequest mr2 = getMessageRequestTemplate();
		mr2.setNotificationType(n2);

		String externalId = UUID.randomUUID().toString();
		
		IVRCallSession session = new IVRCallSession(recipientId1, phone1, english.getName(), IVRCallSession.OUTBOUND, 0, 0, IVRCallSession.OPEN, fourMinuteLessBundlingDelayAgo, fourMinutesAgo);
		session.getMessageRequests().add(mr1);
		session.getMessageRequests().add(mr2);
		
		RequestType expectedRequest = getRequestTypeTemplate(); 
		expectedRequest.setCallee(phone1);
		expectedRequest.setLanguage(english.getName());
		expectedRequest.setPrivate(externalId);
		expectedRequest.setTree(n1IvrEntityName);

		BreakType breakType = new BreakType();
		breakType.setTime(intellivrBean.getPreReminderDelay() + "s");
		
		AudioType welcome = new AudioType();
		welcome.setSrc(intellivrBean.getWelcomeMessageRecordingName());
		
		AudioType reminder = new AudioType();
		reminder.setSrc(n2IvrEntityName);

		expectedRequest.getVxml().getPrompt().getAudioOrBreak().add(breakType);
		expectedRequest.getVxml().getPrompt().getAudioOrBreak().add(welcome);
		expectedRequest.getVxml().getPrompt().getAudioOrBreak().add(reminder);
		
		assertEquals(expectedRequest, intellivrBean.createIVRRequest(session,externalId));
		
	}
	
	@Test
	public void testCreateRequestTypeInboundCall() {
	
		Date now = new Date();
		
		MessageRequest mr1 = getMessageRequestTemplate();
		MessageRequest mr2 = getMessageRequestTemplate();
		mr2.setNotificationType(n2);

		String externalId = UUID.randomUUID().toString();
		
		IVRCallSession session = new IVRCallSession(recipientId1, null, english.getName(), IVRCallSession.INBOUND, 0, 0, IVRCallSession.REPORT_WAIT, now, null);
		session.getMessageRequests().add(mr1);
		session.getMessageRequests().add(mr2);
		
		RequestType expectedRequest = getRequestTypeTemplate();
		expectedRequest.setLanguage(english.getName());
		expectedRequest.setPrivate(externalId);
		expectedRequest.setTree(n1IvrEntityName);

		BreakType breakType = new BreakType();
		breakType.setTime(intellivrBean.getPreReminderDelay() + "s");
		
		AudioType reminder = new AudioType();
		reminder.setSrc(n2IvrEntityName);

		expectedRequest.getVxml().getPrompt().getAudioOrBreak().add(breakType);
		expectedRequest.getVxml().getPrompt().getAudioOrBreak().add(reminder);
		
		assertEquals(expectedRequest, intellivrBean.createIVRRequest(session,externalId));
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testHandleRequestNoPending() throws NumberFormatException, ValidationException {

		String externalId = UUID.randomUUID().toString();
		
		GetIVRConfigRequest request = new GetIVRConfigRequest();
		request.setUserid(recipientId1);
		
		ResponseType expectedResponse = new ResponseType();
		expectedResponse.setStatus(StatusType.OK);
		RequestType.Vxml vxml = new RequestType.Vxml();
		vxml.setPrompt(new RequestType.Vxml.Prompt());
		AudioType audio = new AudioType();
		audio.setSrc(intellivrBean.getNoPendingMessagesRecordingName());
		vxml.getPrompt().getAudioOrBreak().add(audio);
		expectedResponse.setVxml(vxml);
		expectedResponse.setReportUrl(intellivrBean.getReportURL());
		expectedResponse.setPrivate(externalId);

		CoreManager mockCoreManager = createMock(CoreManager.class);
		intellivrBean.setCoreManager(mockCoreManager);

		MessageRequestDAO<MessageRequest> mockMessageDao = createMock(MessageRequestDAO.class);

		RegistrarService mockRegistrarService = createMock(RegistrarService.class);
		intellivrBean.setRegistrarService(mockRegistrarService);
		
		String[] registrarResponse = { "string1" };
		expect(mockRegistrarService.getPatientEnrollments(Integer.parseInt(recipientId1))).andReturn(registrarResponse);
		
		expect(mockCoreManager.createMessageRequestDAO()).andReturn(mockMessageDao);
		
		List<MessageRequest> expectedDAOResponse = new ArrayList<MessageRequest>();
		expect(mockMessageDao.getMsgRequestByRecipientDateFromBetweenDates(EasyMock.eq(recipientId1), (Date)EasyMock.anyObject(),(Date)EasyMock.anyObject())).andReturn(expectedDAOResponse);
		
		IVRCallSession expectedSession = new IVRCallSession(recipientId1, null, null, IVRCallSession.INBOUND, 0, 0, IVRCallSession.REPORT_WAIT, new Date(), null);
		IVRCall call = new IVRCall(new Date(), null, null, 0, externalId, IVRCallStatus.REQUESTED, "Client called IVR system", expectedSession);
		expectedSession.getCalls().add(call);
		
		IVRDAO mockIvrDao = createMock(IVRDAO.class);
		intellivrBean.setIvrDao(mockIvrDao);
		
		expect(mockIvrDao.saveIVRCallSession((IVRCallSession)EasyMock.anyObject())).andReturn(1L);
		
		replay(mockCoreManager,mockMessageDao,mockRegistrarService,mockIvrDao);
		
		assertEquals(expectedResponse, intellivrBean.handleRequest(request,externalId));
		
		verify(mockCoreManager,mockMessageDao,mockRegistrarService,mockIvrDao);

	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testHandleRequestWithPending() throws NumberFormatException, ValidationException {
		
		String externalId = UUID.randomUUID().toString();
		
		GetIVRConfigRequest request = new GetIVRConfigRequest();
		request.setUserid(recipientId1);

		MessageRequest mr1 = getMessageRequestTemplate();
		MessageRequest mr2 = getMessageRequestTemplate();
		mr2.setNotificationType(n2);

		ResponseType expectedResponse = new ResponseType();
		expectedResponse.setStatus(StatusType.OK);
		RequestType.Vxml vxml = new RequestType.Vxml();
		vxml.setPrompt(new RequestType.Vxml.Prompt());
		BreakType brk = new BreakType();
		brk.setTime(intellivrBean.getPreReminderDelay() + "s");
		vxml.getPrompt().getAudioOrBreak().add(brk);
		AudioType audio = new AudioType();
		audio.setSrc(n2IvrEntityName);
		vxml.getPrompt().getAudioOrBreak().add(audio);
		expectedResponse.setVxml(vxml);
		expectedResponse.setReportUrl(intellivrBean.getReportURL());
		expectedResponse.setTree(n1IvrEntityName);
		expectedResponse.setPrivate(externalId);

		RegistrarService mockRegistrarService = createMock(RegistrarService.class);
		intellivrBean.setRegistrarService(mockRegistrarService);
		
		String[] registrarResponse = { "string1" };
		expect(mockRegistrarService.getPatientEnrollments(Integer.parseInt(recipientId1))).andReturn(registrarResponse);

		CoreManager mockCoreManager = createMock(CoreManager.class);
		intellivrBean.setCoreManager(mockCoreManager);

		MessageRequestDAO<MessageRequest> mockMessageDao = createMock(MessageRequestDAO.class);
		
		expect(mockCoreManager.createMessageRequestDAO()).andReturn(mockMessageDao);
		
		List<MessageRequest> expectedDAOResponse = new ArrayList<MessageRequest>();
		expectedDAOResponse.add(mr1);
		expectedDAOResponse.add(mr2);
		expect(mockMessageDao.getMsgRequestByRecipientDateFromBetweenDates(EasyMock.eq(recipientId1), (Date)EasyMock.anyObject(), (Date)EasyMock.anyObject())).andReturn(expectedDAOResponse);

		MessageStatusStore mockStatusStore = createMock(MessageStatusStore.class);
		intellivrBean.setStatusStore(mockStatusStore);
		
		mockStatusStore.updateStatus(mr1.getId().toString(), StatusType.OK.value());
		mockStatusStore.updateStatus(mr2.getId().toString(), StatusType.OK.value());
		
		IVRCallSession expectedSession = new IVRCallSession(recipientId1, null, null, IVRCallSession.INBOUND, 0, 0, IVRCallSession.REPORT_WAIT, new Date(), null);
		IVRCall call = new IVRCall(new Date(), null, null, 0, externalId, IVRCallStatus.REQUESTED, "Client called IVR system", expectedSession);
		expectedSession.getCalls().add(call);
		expectedSession.getMessageRequests().add(mr1);
		expectedSession.getMessageRequests().add(mr2);
		
		IVRDAO mockIvrDao = createMock(IVRDAO.class);
		intellivrBean.setIvrDao(mockIvrDao);
		
		expect(mockIvrDao.saveIVRCallSession((IVRCallSession)EasyMock.anyObject())).andReturn(1L);
		
		replay(mockCoreManager,mockMessageDao,mockRegistrarService,mockIvrDao,mockStatusStore);
		
		assertEquals(expectedResponse, intellivrBean.handleRequest(request,externalId));
		
		verify(mockCoreManager,mockMessageDao,mockRegistrarService,mockIvrDao,mockStatusStore);
		
	}
	
	@Test
	public void testHandleRequestValidationException() throws NumberFormatException, ValidationException {
		
		GetIVRConfigRequest request = new GetIVRConfigRequest();
		request.setUserid(recipientId1);
		
		ResponseType expectedResponse = new ResponseType();
		expectedResponse.setErrorCode(ErrorCodeType.MOTECH_INVALID_USER_ID);
		expectedResponse.setStatus(StatusType.ERROR);

		RegistrarService mockRegistrarService = createMock(RegistrarService.class);
		intellivrBean.setRegistrarService(mockRegistrarService);

		expect(mockRegistrarService.getPatientEnrollments(Integer.parseInt(recipientId1))).andThrow(new ValidationException());
		replay(mockRegistrarService);

		ResponseType actualResponse = intellivrBean.handleRequest(request);
		assertEquals(expectedResponse.getErrorCode(), actualResponse.getErrorCode());
		assertEquals(expectedResponse.getStatus(), actualResponse.getStatus());

		verify(mockRegistrarService);
		
	}
	
	@Test
	public void testHandleRequestUnenrolledId() throws NumberFormatException, ValidationException {
		
		GetIVRConfigRequest request = new GetIVRConfigRequest();
		request.setUserid(recipientId1);
		
		ResponseType expectedResponse = new ResponseType();
		expectedResponse.setErrorCode(ErrorCodeType.MOTECH_INVALID_USER_ID);
		expectedResponse.setStatus(StatusType.ERROR);

		String[] registrarResponse = new String[0];
		
		RegistrarService mockRegistrarService = createMock(RegistrarService.class);
		intellivrBean.setRegistrarService(mockRegistrarService);

		expect(mockRegistrarService.getPatientEnrollments(Integer.parseInt(recipientId1))).andReturn(registrarResponse);
		replay(mockRegistrarService);

		ResponseType actualResponse = intellivrBean.handleRequest(request);
		assertEquals(expectedResponse.getErrorCode(), actualResponse.getErrorCode());
		assertEquals(expectedResponse.getStatus(), actualResponse.getStatus());

		verify(mockRegistrarService);
		
	}
	
	@Test
	public void testHandleRequestNonNumericId(){

		GetIVRConfigRequest request = new GetIVRConfigRequest();
		request.setUserid("NaN");
		
		ResponseType expectedResponse = new ResponseType();
		expectedResponse.setErrorCode(ErrorCodeType.MOTECH_INVALID_USER_ID);
		expectedResponse.setStatus(StatusType.ERROR);

		ResponseType actualResponse = intellivrBean.handleRequest(request);
		assertEquals(expectedResponse.getErrorCode(), actualResponse.getErrorCode());
		assertEquals(expectedResponse.getStatus(), actualResponse.getStatus());
		
	}

	private GatewayRequest getGatewayRequestTemplate() {
		
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
		
		GatewayRequest gr = new GatewayRequestImpl();
		gr.setDateFrom(new Date());
		gr.setId(nextRequestId);
		gr.setMessageStatus(MStatus.SCHEDULED);
		gr.setMessageRequest(mr);
		gr.setRecipientsNumber(phone1);
		gr.setRequestId(mr.getRequestId());
		gr.setTryNumber(1);
		gr.setVersion(0);
		
		nextRequestId++;

		return gr;
		
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
	
	private ReportType getReportTypeTemplateForOutboundCall() {
		
		ReportType report = new ReportType();
		report.setCallee(phone1);
		
		IvrEntryType e1 = new IvrEntryType();
		e1.setDuration(3);
		e1.setMenu("break");
		
		IvrEntryType e2 = new IvrEntryType();
		e2.setDuration(10);
		e2.setMenu(intellivrBean.getWelcomeMessageRecordingName());

		report.getINTELLIVREntry().add(e1);
		report.getINTELLIVREntry().add(e2);
		
		report.setDuration(e1.getDuration() + e2.getDuration());
		
		report.setINTELLIVREntryCount(report.getINTELLIVREntry().size());
		
		return report;
		
	}

	private ReportType getReportTypeTemplateForInboundCall() {
		
		ReportType report = new ReportType();
		report.setCallee(phone1);
		
		IvrEntryType e1 = new IvrEntryType();
		e1.setDuration(10);
		e1.setMenu("start_menu");
		
		IvrEntryType e2 = new IvrEntryType();
		e2.setDuration(10);
		e2.setMenu("call_center_message");
		
		IvrEntryType e3 = new IvrEntryType();
		e3.setDuration(10);
		e3.setMenu("counter");
		
		IvrEntryType e4 = new IvrEntryType();
		e4.setDuration(3);
		e4.setMenu("break");
		
		report.getINTELLIVREntry().add(e1);
		report.getINTELLIVREntry().add(e2);
		report.getINTELLIVREntry().add(e3);
		report.getINTELLIVREntry().add(e4);
	
		report.setDuration(e1.getDuration() + e2.getDuration() + e3.getDuration() + e4.getDuration());
		
		report.setINTELLIVREntryCount(report.getINTELLIVREntry().size());
		
		return report;
		
	}
	
	private RequestType getRequestTypeTemplate() {
		
		RequestType request = new RequestType();
		
		request.setApiId(intellivrBean.getApiID());
		request.setCallee(null);
		request.setLanguage(null);
		request.setMethod(intellivrBean.getMethod());
		request.setPrivate(null);
		request.setReportUrl(intellivrBean.getReportURL());
		request.setTree(null);
		request.setVxml(new Vxml());
		request.getVxml().setPrompt(new Vxml.Prompt());
		
		return request;
		
	}
	
	private Date addToDate(Date start, int field, int amount) {
		
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(start);
		cal.add(field, amount);
		return cal.getTime();
		
	}
	
	private XMLGregorianCalendar toXMLGregorianCalendar(Date date) throws DatatypeConfigurationException {
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(date);
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
	}

}
