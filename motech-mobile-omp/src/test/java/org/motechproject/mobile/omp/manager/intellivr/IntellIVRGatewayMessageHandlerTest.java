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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mobile.core.model.GatewayRequest;
import org.motechproject.mobile.core.model.GatewayRequestImpl;
import org.motechproject.mobile.core.model.GatewayResponse;
import org.motechproject.mobile.core.model.MStatus;
import org.motechproject.mobile.core.model.MessageRequest;
import org.motechproject.mobile.core.model.MessageRequestImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( locations = {"classpath:META-INF/test-omp-config.xml"})
public class IntellIVRGatewayMessageHandlerTest {

	@Resource
	IntellIVRGatewayMessageHandler intellIVRMessageHandler;
	
	Map<String, MStatus> statusCodes = new HashMap<String, MStatus>();
	
	@Before
	public void setUp() throws Exception {
		
		statusCodes.put("0000", MStatus.FAILED);
		statusCodes.put("0001", MStatus.FAILED);
		statusCodes.put("0002", MStatus.FAILED);
		statusCodes.put("0003", MStatus.FAILED);
		statusCodes.put("0004", MStatus.FAILED);
		statusCodes.put("0005", MStatus.FAILED);
		statusCodes.put("0006", MStatus.FAILED);
		statusCodes.put("0007", MStatus.FAILED);
		statusCodes.put("0008", MStatus.FAILED);
		statusCodes.put("0009", MStatus.FAILED);
		statusCodes.put("0010", MStatus.FAILED);
		statusCodes.put("0011", MStatus.FAILED);
		statusCodes.put("ERROR", MStatus.FAILED);
		statusCodes.put("OK", MStatus.PENDING);
		statusCodes.put("COMPLETED", MStatus.DELIVERED);
		statusCodes.put("REJECTED", MStatus.PENDING);
		statusCodes.put("BUSY", MStatus.PENDING);
		statusCodes.put("CONGESTION", MStatus.PENDING);
		statusCodes.put("NOANSWER", MStatus.PENDING);
		statusCodes.put("INTERNALERROR", MStatus.PENDING);
		
	}
	
	@Test
	public void testLookupStatus() {
		
		for ( String code : statusCodes.keySet())
			assertEquals(statusCodes.get(code), intellIVRMessageHandler.lookupStatus(code));

	}

	@Test
	public void testLookupResponse() {
		
		for ( String code : statusCodes.keySet())
			assertEquals(statusCodes.get(code), intellIVRMessageHandler.lookupResponse(code));
		
	}

	@Test
	public void testParseMessageStatus() {
		
		for ( String code : statusCodes.keySet()) 
			assertEquals(statusCodes.get(code), intellIVRMessageHandler.parseMessageStatus(code));
		
	}
	
	@Test
	public void testParseMessageResponse() {
				
		MessageRequest mr1 = new MessageRequestImpl();
		mr1.setId(31000000001l);
		mr1.setRecipientId("123456789");
		mr1.setRequestId("mr1");
	
		GatewayRequest r1 = new GatewayRequestImpl();
		r1.setId(31000000002l);
		r1.setMessageRequest(mr1);
		r1.setRequestId(mr1.getRequestId());
		r1.setMessageStatus(MStatus.PENDING);
		r1.setRecipientsNumber("15555555555");
		
		for ( String code : statusCodes.keySet()) {

			Set<GatewayResponse> responses = intellIVRMessageHandler.parseMessageResponse(r1, code);
			
			for ( GatewayResponse response : responses ) {
				assertTrue(response.getDateCreated()!= null);
				assertEquals(r1, response.getGatewayRequest());
				assertEquals(mr1.getId().toString(), response.getGatewayMessageId());
				assertEquals(statusCodes.get(code), response.getMessageStatus());
				assertEquals("15555555555", response.getRecipientNumber());
				assertEquals("mr1", response.getRequestId());
				assertEquals(code, response.getResponseText());
			}
			
		}
		
	}

	
}
