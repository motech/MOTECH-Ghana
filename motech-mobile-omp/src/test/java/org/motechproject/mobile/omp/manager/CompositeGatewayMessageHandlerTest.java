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

package org.motechproject.mobile.omp.manager;

import static org.easymock.EasyMock.*;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( locations = {"classpath:META-INF/test-omp-config.xml"})
public class CompositeGatewayMessageHandlerTest {

	@Resource
	CompositeGatewayMessageHandler compositeHandler;
	
	GatewayRequest voiceGatewayRequest;
	GatewayRequest textGatewayRequest;
	
	GatewayMessageHandler voiceHandler;
	GatewayMessageHandler textHandler;
	
	@Before
	public void setUp() {
		
		voiceHandler = createMock(GatewayMessageHandler.class);
		textHandler = createMock(GatewayMessageHandler.class);
		
		compositeHandler.setVoiceHandler(voiceHandler);
		compositeHandler.setTextHandler(textHandler);
		
		Language english = new LanguageImpl();
		english.setCode("en");
		english.setId(26000000001l);
		english.setName("English");
		
		NotificationType n1 = new NotificationTypeImpl();
		n1.setId(1L);
		
		MessageRequest voiceMessageRequest = new MessageRequestImpl();
		voiceMessageRequest.setId(26000000002l);
		voiceMessageRequest.setLanguage(english);
		voiceMessageRequest.setRecipientId("123456789");
		voiceMessageRequest.setRequestId("mr1");
		voiceMessageRequest.setMessageType(MessageType.VOICE);
		voiceMessageRequest.setNotificationType(n1);
		voiceMessageRequest.setPhoneNumberType("PERSONAL");
	
		voiceGatewayRequest = new GatewayRequestImpl();
		voiceGatewayRequest.setId(26000000003l);
		voiceGatewayRequest.setMessageRequest(voiceMessageRequest);
		voiceGatewayRequest.setMessageStatus(MStatus.PENDING);
		voiceGatewayRequest.setRecipientsNumber("15555555555");

		NotificationType n2 = new NotificationTypeImpl();
		n2.setId(2L);

		MessageRequest textMessageRequest = new MessageRequestImpl();
		textMessageRequest.setId(26000000004l);
		textMessageRequest.setLanguage(english);
		textMessageRequest.setRecipientId("123456789");
		textMessageRequest.setRequestId("mr2");
		textMessageRequest.setMessageType(MessageType.TEXT);
		textMessageRequest.setNotificationType(n2);
		textMessageRequest.setPhoneNumberType("PERSONAL");
			
		textGatewayRequest = new GatewayRequestImpl();
		textGatewayRequest.setId(26000000005l);
		textGatewayRequest.setMessageRequest(textMessageRequest);
		textGatewayRequest.setMessageStatus(MStatus.PENDING);
		textGatewayRequest.setRecipientsNumber("15555555555");

		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testParseMessageResponse(){
		
		
		Set<GatewayResponse> response = new HashSet<GatewayResponse>();
		
		expect(voiceHandler.parseMessageResponse(voiceGatewayRequest, "OK")).andReturn(response);
		replay(voiceHandler);
		compositeHandler.parseMessageResponse(voiceGatewayRequest, "OK");
		verify(voiceHandler);
		reset(voiceHandler);
		
		expect(textHandler.parseMessageResponse(textGatewayRequest, "OK")).andReturn(response);
		replay(textHandler);
		compositeHandler.parseMessageResponse(textGatewayRequest, "OK");
		verify(textHandler);
		reset(textHandler);
		
	}
	
}
