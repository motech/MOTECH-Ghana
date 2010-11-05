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

package org.motechproject.mobile.omp.service;

import org.junit.Ignore;
import org.motechproject.mobile.core.manager.CoreManager;
import static org.easymock.EasyMock.*;

import org.motechproject.mobile.core.model.GatewayRequest;
import org.motechproject.mobile.core.model.GatewayRequestDetails;
import org.motechproject.mobile.core.model.GatewayRequestImpl;
import org.motechproject.mobile.core.model.GatewayResponse;
import org.motechproject.mobile.core.model.GatewayResponseImpl;
import org.motechproject.mobile.core.model.MStatus;
import org.motechproject.mobile.omp.manager.GatewayManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit test for the SMSMessagingServiceImpl class
 *
 * @author Kofi A. Asamoah (yoofi@dreamoval.com)
 * @author Henry Sampson (henry@dreamoval.com)
 *
 * Date Created Aug 10, 2009
 * Last Updated Dec 18, 2009
 */

public class SMSMessagingServiceImplTest {

    SMSMessagingServiceImpl instance;

    SMSMessagingServiceWorker mockWorker;
    CoreManager mockCore;
    CacheService mockCache;
    GatewayManager mockGateway;    
    GatewayRequestDetails mockGatewayRequestDetails;

    public SMSMessagingServiceImplTest() {
    }

    @Before
    public void setUp(){
        mockCache = createMock(CacheService.class);
        mockGateway = createMock(GatewayManager.class);
        mockGatewayRequestDetails = createMock(GatewayRequestDetails.class);
        mockGatewayRequestDetails.setId(33000000001l);
        mockCore = createMock(CoreManager.class);
        mockWorker = createMock(SMSMessagingServiceWorker.class);
        
        instance = new SMSMessagingServiceImpl();
        instance.setCache(mockCache);
        instance.setGatewayManager(mockGateway);
        instance.setCoreManager(mockCore);
        instance.setWorker(mockWorker);

    }

    /**
     * Test of sendTextMessage method, of class SMSMessagingServiceImpl.
     */
    @Test
    public void testScheduleMessage() {
        System.out.println("scheduleMessage");

        GatewayRequest messageDetails = new GatewayRequestImpl();
        messageDetails.setDateFrom(new Date());
        messageDetails.setMessage("a message for testing");
        messageDetails.setDateTo(new Date());
        messageDetails.setRecipientsNumber("000000000000");
        messageDetails.setGatewayRequestDetails(mockGatewayRequestDetails);
        
        mockCache.saveMessage((GatewayRequestDetails) anyObject());
        expectLastCall();

        replay(mockCache);

        instance.scheduleMessage(messageDetails);
        verify(mockCache);
    }

    /**
     * Test of sendTextMessage method, of class SMSMessagingServiceImpl.
     */
    @Test
    public void testSendScheduledMessages() {
        System.out.println("sendScheduledMessages");

        GatewayRequest messageDetails = new GatewayRequestImpl();
        messageDetails.setDateFrom(new Date());
        messageDetails.setMessage("a message for testing");
        messageDetails.setDateTo(new Date());
        messageDetails.setRecipientsNumber("000000000000");
        messageDetails.setGatewayRequestDetails(mockGatewayRequestDetails);
        
        List<GatewayRequest> messages = new ArrayList<GatewayRequest>();
        messages.add(messageDetails);


        expect(
                mockCache.getMessagesByStatusAndSchedule((MStatus) anyObject(), (Date) anyObject())
                ).andReturn(messages);
        expect(
                mockWorker.sendMessage((GatewayRequest) anyObject())
                ).andReturn(null);

        replay(mockCore, mockCache, mockWorker);

        instance.sendScheduledMessages();
        verify(mockCore, mockCache, mockWorker);
    }

    /**
     * Test of sendTextMessage method, of class SMSMessagingServiceImpl.
     */
    @Test
    public void testSendMessage() {
        System.out.println("sendMessage");

        GatewayRequest messageDetails = new GatewayRequestImpl();
        messageDetails.setDateFrom(new Date());
        messageDetails.setMessage("a message for testing");
        messageDetails.setDateTo(new Date());
        messageDetails.setRecipientsNumber("000000000000");
        messageDetails.setGatewayRequestDetails(mockGatewayRequestDetails);

        Map<Boolean, Set<GatewayResponse>> expResult = new HashMap<Boolean, Set<GatewayResponse>>();
        expResult.put(true, new HashSet<GatewayResponse>());

        expect(
                mockWorker.sendMessage((GatewayRequest) anyObject())
                ).andReturn(expResult);

        replay(mockWorker);

        
        Map<Boolean, Set<GatewayResponse>> result = instance.sendTransactionalMessage(messageDetails);
        assertEquals(expResult.get(true), result.get(true));
        verify(mockWorker);
    }

    /**
     * Test of sendTextMessage method, of class SMSMessagingServiceImpl.
     */
    @Test
    public void testUpdateMessageStatuses() {
        System.out.println("updateMessageStatuses");

        GatewayResponse response = new GatewayResponseImpl();
        response.setGatewayMessageId("werfet54y56g645v4e");
        response.setMessageStatus(MStatus.PENDING);
        response.setRecipientNumber("000000000000");
        response.setResponseText("Some gateway response message");
                
        List<GatewayResponse> responses = new ArrayList<GatewayResponse>();
        responses.add(response);
        

        expect(
                mockCore.createGatewayResponse()
                ).andReturn(new GatewayResponseImpl());
        expect(
                mockCache.getResponses((GatewayResponse) anyObject())
                ).andReturn(responses);

        mockWorker.updateMessageStatus((GatewayResponse)anyObject());
        expectLastCall();

        replay(mockCore, mockCache, mockGateway, mockWorker);

        instance.updateMessageStatuses();
        verify(mockCore, mockCache, mockGateway, mockWorker);
    }

    /**
     * Test of getMessageStatus method, of class SMSMessagingServiceImpl.
     */
    @Test
    public void testGetMessageStatus() {
        System.out.println("getMessageStatus");
        String expResult = "delivered";

        expect(
                mockGateway.getMessageStatus((GatewayResponse) anyObject())
                ).andReturn("delivered");
        replay(mockGateway);

        String result = instance.getMessageStatus(new GatewayResponseImpl());
        assertEquals(expResult, result);
        verify(mockGateway);
    }

}