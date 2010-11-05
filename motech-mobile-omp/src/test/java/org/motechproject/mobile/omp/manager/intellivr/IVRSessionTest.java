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
import java.util.UUID;

import org.junit.Test;
import org.motechproject.mobile.core.model.GatewayRequest;
import org.motechproject.mobile.core.model.GatewayRequestImpl;
import org.motechproject.mobile.core.model.Language;
import org.motechproject.mobile.core.model.LanguageImpl;
import org.motechproject.mobile.core.model.MStatus;
import org.motechproject.mobile.core.model.MessageRequest;
import org.motechproject.mobile.core.model.MessageRequestImpl;
import org.motechproject.mobile.core.model.MessageType;
import org.motechproject.mobile.core.model.NotificationType;
import org.motechproject.mobile.core.model.NotificationTypeImpl;


public class IVRSessionTest {

	@Test
	public void testIVRSession() {
		
		String userid = "1";
		String phone = "5555555555";
		String language = "English";
				
		Language english = new LanguageImpl();
		english.setCode("en");
		english.setId(29000000001l);
		english.setName("English");
		
		NotificationType n1 = new NotificationTypeImpl();
		n1.setId(1L);
		
		MessageRequest mr1 = new MessageRequestImpl();
		mr1.setId(29000000002l);
		mr1.setLanguage(english);
		mr1.setRecipientId(userid);
		mr1.setRequestId("mr1");
		mr1.setMessageType(MessageType.VOICE);
		mr1.setNotificationType(n1);
		mr1.setPhoneNumberType("PERSONAL");
	
		GatewayRequest r1 = new GatewayRequestImpl();
		r1.setId(29000000003l);
		r1.setMessageRequest(mr1);
		r1.setMessageStatus(MStatus.PENDING);
		r1.setRecipientsNumber(phone);

		NotificationType n2 = new NotificationTypeImpl();
		n2.setId(2L);

		MessageRequest mr2 = new MessageRequestImpl();
		mr2.setId(29000000004l);
		mr2.setLanguage(english);
		mr2.setRecipientId(userid);
		mr2.setRequestId("mr2");
		mr2.setMessageType(MessageType.VOICE);
		mr2.setNotificationType(n2);
		mr2.setPhoneNumberType("PERSONAL");
			
		GatewayRequest r2 = new GatewayRequestImpl();
		r2.setId(29000000005l);
		r2.setMessageRequest(mr2);
		r2.setMessageStatus(MStatus.PENDING);
		r2.setRecipientsNumber(phone);
		
		/*
		 * test server session
		 */
		IVRSession serverSession = new IVRSession(userid, phone, language);		
		serverSession.addGatewayRequest(r1);
		serverSession.addGatewayRequest(r2);
		
		assertEquals(userid, serverSession.getUserId());
		assertEquals(phone, serverSession.getPhone());
		assertFalse(serverSession.isUserInitiated());
		
		assertEquals(0, serverSession.getAttempts());
		serverSession.setAttempts(serverSession.getAttempts() + 1);
		assertEquals(1, serverSession.getAttempts());
		
		assertTrue(serverSession.getGatewayRequests().contains(r1));
		assertTrue(serverSession.getGatewayRequests().contains(r2));

		serverSession.removeGatewayRequest(r2);
		assertFalse(serverSession.getGatewayRequests().contains(r2));
		
		/*
		 * Test user session
		 */
		IVRSession userSession = new IVRSession(userid);
		userSession.addGatewayRequest(r1);
		userSession.addGatewayRequest(r2);
		
		assertEquals(userid, userSession.getUserId());
		assertNull(userSession.getPhone());
		assertTrue(userSession.isUserInitiated());
		
		assertEquals(0, userSession.getAttempts());
		userSession.setAttempts(userSession.getAttempts() + 1);
		assertEquals(1, userSession.getAttempts());
		
		assertTrue(userSession.getGatewayRequests().contains(r1));
		assertTrue(userSession.getGatewayRequests().contains(r2));

		userSession.removeGatewayRequest(r2);
		assertFalse(userSession.getGatewayRequests().contains(r2));
		
	}
	
	@Test
	public void testIVRCallSession() {
		
		String userid = "1";
		String phone = "5555555555";
		String language = "English";
		String externalId = UUID.randomUUID().toString();
				
		Language english = new LanguageImpl();
		english.setCode("en");
		english.setId(29000000001l);
		english.setName("English");
		
		NotificationType n1 = new NotificationTypeImpl();
		n1.setId(1L);
		
		MessageRequest mr1 = new MessageRequestImpl();
		mr1.setId(29000000002l);
		mr1.setLanguage(english);
		mr1.setRecipientId(userid);
		mr1.setRequestId("mr1");
		mr1.setMessageType(MessageType.VOICE);
		mr1.setNotificationType(n1);
		mr1.setPhoneNumberType("PERSONAL");
	
		GatewayRequest r1 = new GatewayRequestImpl();
		r1.setId(29000000003l);
		r1.setMessageRequest(mr1);
		r1.setMessageStatus(MStatus.PENDING);
		r1.setRecipientsNumber(phone);

		NotificationType n2 = new NotificationTypeImpl();
		n2.setId(2L);

		MessageRequest mr2 = new MessageRequestImpl();
		mr2.setId(29000000004l);
		mr2.setLanguage(english);
		mr2.setRecipientId(userid);
		mr2.setRequestId("mr2");
		mr2.setMessageType(MessageType.VOICE);
		mr2.setNotificationType(n2);
		mr2.setPhoneNumberType("PERSONAL");
			
		GatewayRequest r2 = new GatewayRequestImpl();
		r2.setId(29000000005l);
		r2.setMessageRequest(mr2);
		r2.setMessageStatus(MStatus.PENDING);
		r2.setRecipientsNumber(phone);

		IVRCallSession serverSession = new IVRCallSession(userid,phone,language,IVRCallSession.OUTBOUND,0,0,IVRCallSession.OPEN, new Date(), new Date());
		
		assertEquals(userid, serverSession.getUserId());
		assertEquals(phone, serverSession.getPhone());
		assertEquals(language, serverSession.getLanguage());
		assertEquals(IVRCallSession.OUTBOUND, serverSession.getCallDirection());
				
		serverSession.getMessageRequests().add(mr1);
		serverSession.getMessageRequests().add(mr2);
		
		assertTrue(serverSession.getMessageRequests().contains(mr1));
		assertTrue(serverSession.getMessageRequests().contains(mr2));
		
		assertEquals(0,serverSession.getAttempts());
		serverSession.setAttempts(1);
		assertEquals(1,serverSession.getAttempts());

		assertEquals(0,serverSession.getDays());
		serverSession.setDays(1);
		assertEquals(1,serverSession.getDays());

		assertEquals(IVRCallSession.OPEN, serverSession.getState());
		
		IVRCall call = new IVRCall(new Date(), null,null,0,externalId,IVRCallStatus.REQUESTED,"API accepted request",serverSession);
		
		IVRMenu menu = new IVRMenu("menu.wav",new Date(),10,"1",null);
		call.getMenus().add(menu);
		call.getMenus().contains(menu);
		
		serverSession.getCalls().add(call);
		
		IVRCallSession userSession = new IVRCallSession(userid,null,null,IVRCallSession.INBOUND,0,0,IVRCallSession.REPORT_WAIT, new Date(), null);
		
		assertEquals(userid, userSession.getUserId());
		assertEquals(IVRCallSession.INBOUND, userSession.getCallDirection());System.out.println(userSession.toString());
				
	}
	
}
