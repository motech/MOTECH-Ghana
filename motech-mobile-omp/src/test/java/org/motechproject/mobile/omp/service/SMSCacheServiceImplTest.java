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

import org.springframework.transaction.annotation.Transactional;

import org.motechproject.mobile.core.dao.GatewayRequestDAO;
import org.motechproject.mobile.core.dao.GatewayResponseDAO;
import static org.easymock.EasyMock.*;

import org.motechproject.mobile.core.manager.CoreManager;
import org.motechproject.mobile.core.model.GatewayRequest;
import org.motechproject.mobile.core.model.GatewayRequestDetails;
import org.motechproject.mobile.core.model.GatewayRequestImpl;
import org.motechproject.mobile.core.model.GatewayResponse;
import org.motechproject.mobile.core.model.GatewayResponseImpl;
import org.motechproject.mobile.core.model.MStatus;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.transaction.TransactionConfiguration;
import static org.junit.Assert.*;

/**
 * Unit test for the SMSCacheServiceImpl class
 *
 * @author Kofi A. Asamoah (yoofi@dreamoval.com)
 * Date Created Aug 10, 2009
 */
@TransactionConfiguration
@Transactional
public class SMSCacheServiceImplTest {

    SMSCacheServiceImpl instance;

    Transaction mockTrans;
    CoreManager mockCore;
    GatewayRequestDAO mockMessageDAO;
    GatewayResponseDAO mockResponseDAO;
    GatewayRequestDetails mockGatewayRequestDetails;

    public SMSCacheServiceImplTest() {
    }

    @Before
    public void setUp(){
        mockCore = createMock(CoreManager.class);
        mockTrans = createMock(Transaction.class);
        mockGatewayRequestDetails = createMock(GatewayRequestDetails.class);
        
        mockGatewayRequestDetails.setId(32000000000l);
        instance = new SMSCacheServiceImpl();
        instance.setCoreManager(mockCore);

    }

    /**
     * Test of saveMessage method, of class SMSCacheServiceImpl.
     */
    @Test
    public void testSaveMessage() {
        System.out.println("saveMessage");

        GatewayRequest messageDetails = new GatewayRequestImpl();
        messageDetails.setDateFrom(new Date());
        messageDetails.setMessage("a message for testing");
        messageDetails.setDateTo(new Date());
        messageDetails.setRecipientsNumber("000000000000");
        messageDetails.setGatewayRequestDetails(mockGatewayRequestDetails);
        messageDetails.setMessageStatus(MStatus.PENDING);
        
        mockMessageDAO = createMock(GatewayRequestDAO.class);

        expect(
                mockCore.createGatewayRequestDAO()
                ).andReturn(mockMessageDAO);
          
        expect(
                mockMessageDAO.save(messageDetails)
                ).andReturn(messageDetails);

        replay(mockCore, mockMessageDAO);
        instance.saveMessage(messageDetails);
        verify(mockCore, mockMessageDAO);
    }

    /**
     * Test of saveMessage method, of class SMSCacheServiceImpl.
     */
    @Test
    public void testSaveResponse() {
        System.out.println("saveResponse");

        GatewayResponse response = new GatewayResponseImpl();
        response.setGatewayMessageId("werfet54y56g645v4e");
        response.setMessageStatus(MStatus.PENDING);
        response.setRecipientNumber("000000000000");
        response.setResponseText("Some gateway response message");
        response.setId(32000000001l);
        
        mockResponseDAO = createMock(GatewayResponseDAO.class);
        
        expect(
                mockCore.createGatewayResponseDAO()
                ).andReturn(mockResponseDAO);
              
        expect(
                mockResponseDAO.merge((GatewayRequest) anyObject())
                ).andReturn(response);
        
        replay(mockCore, mockResponseDAO);
        instance.saveResponse(response);
        verify(mockCore, mockResponseDAO);
    }

    /**
     * Test of saveMessage method, of class SMSCacheServiceImpl.
     */
    @Test
    public void testGetMessages() {
        System.out.println("getMessages");

        GatewayRequest messageDetails = new GatewayRequestImpl();
        messageDetails.setDateFrom(new Date());
        messageDetails.setMessage("a message for testing");
        messageDetails.setDateTo(new Date());
        messageDetails.setRecipientsNumber("000000000000");
        messageDetails.setGatewayRequestDetails(mockGatewayRequestDetails);
        
        mockMessageDAO = createMock(GatewayRequestDAO.class);
        
        expect(
                mockCore.createGatewayRequestDAO()
                ).andReturn(mockMessageDAO);
        expect(
                mockMessageDAO.findByExample((GatewayRequest) anyObject())
                ).andReturn(new ArrayList<GatewayRequest>());
        replay(mockCore, mockMessageDAO);

        List<GatewayRequest> result = instance.getMessages(messageDetails);
        assertNotNull(result);
        
        verify(mockCore, mockMessageDAO);
    }

    /**
     * Test of saveMessage method, of class SMSCacheServiceImpl.
     */
    @Test
    public void testGetResponses() {
        System.out.println("getResponses");

        GatewayResponse response = new GatewayResponseImpl();
        response.setGatewayMessageId("werfet54y56g645v4e");
        response.setMessageStatus(MStatus.PENDING);
        response.setRecipientNumber("000000000000");
        response.setResponseText("Some gateway response message");
        
        mockResponseDAO = createMock(GatewayResponseDAO.class);
        
        expect(
                mockCore.createGatewayResponseDAO()
                ).andReturn(mockResponseDAO);
        expect(
                mockResponseDAO.findByExample((GatewayResponse) anyObject())
                ).andReturn(new ArrayList<GatewayResponse>());
        
        replay(mockCore, mockResponseDAO);

        List<GatewayResponse> result = instance.getResponses(response);
        assertNotNull(result);
        
        verify(mockCore, mockResponseDAO);
    }

    /**
     * Test of getMessagesByStatus method, of class SMSCacheServiceImpl.
     */
    @Test
    public void testGetMessagesByStatus() {
        System.out.println("getMessagesByStatus");

        mockMessageDAO = createMock(GatewayRequestDAO.class);

        expect(
                mockCore.createGatewayRequestDAO()
                ).andReturn(mockMessageDAO);
        expect(
                mockMessageDAO.getByStatus((MStatus) anyObject())
                ).andReturn(new ArrayList<GatewayRequest>());
        replay(mockCore, mockMessageDAO);

        List<GatewayRequest> result = instance.getMessagesByStatus(MStatus.CANCELLED);
        assertNotNull(result);

        verify(mockCore, mockMessageDAO);
    }

    /**
     * Test of getMessagesByStatusAndSchedule method, of class SMSCacheServiceImpl.
     */
    @Test
    public void testGetMessagesByStatusAndSchedule() {
        System.out.println("getMessagesByStatusAndSchedule");

        mockMessageDAO = createMock(GatewayRequestDAO.class);

        expect(
                mockCore.createGatewayRequestDAO()
                ).andReturn(mockMessageDAO);
        expect(
                mockMessageDAO.getByStatusAndSchedule((MStatus) anyObject(), (Date) anyObject())
                ).andReturn(new ArrayList<GatewayRequest>());
        replay(mockCore, mockMessageDAO);

        List<GatewayRequest> result = instance.getMessagesByStatusAndSchedule(MStatus.CANCELLED, new Date());
        assertNotNull(result);

        verify(mockCore, mockMessageDAO);
    }
}