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

package org.motechproject.mobile.omp.manager.orserve;

import org.motechproject.mobile.core.manager.CoreManager;
import static org.easymock.EasyMock.*;

import org.motechproject.mobile.core.model.GatewayRequest;
import org.motechproject.mobile.core.model.GatewayResponse;
import org.motechproject.mobile.core.model.MStatus;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;

/**
 * Unit test for the ORServeGatewayMessageHandlerImpl class
 * 
 * @author Kofi A. Asamoah (yoofi@dreamoval.com)
 * @author Henry Sampson (henry@dreamoval.com)
 *
 * Date Created Aug 10, 2009
 * Last Updated Dec 18, 2009
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/test-omp-config.xml"})
public class ORServeGatewayMessageHandlerImplTest {

    CoreManager mockCoreManager;
    
    @Autowired
    ORServeGatewayMessageHandlerImpl messageHandler;
    
    public ORServeGatewayMessageHandlerImplTest() {
    }

    @Before
    public void setUp(){
        mockCoreManager = createMock(CoreManager.class);
        
        messageHandler.setCoreManager(mockCoreManager);
    }

    /**
     * Test of parseMessageResponse method, of class ORServeGatewayMessageHandlerImpl.
     */
    @Test
    public void testParseMessageResponse() {
        System.out.println("parseMessageResponse");
        GatewayRequest message = null;
        String gatewayResponse = "";
        GatewayRequest expResult = null;
        Set<GatewayResponse> result = messageHandler.parseMessageResponse(message, gatewayResponse);
        assertEquals(expResult, result);
    }

    /**
     * Test of parseMessageStatus method, of class ORServeGatewayMessageHandlerImpl.
     */
    @Test
    public void testParseMessageStatus() {
        System.out.println("parseMessageStatus");
        String messageStatus = " ";
        MStatus expResult = MStatus.PENDING;
        MStatus result = messageHandler.parseMessageStatus(messageStatus);
        assertEquals(expResult, result);
    }

    /**
     * Test of lookupStatus method, of class ORServeGatewayMessageHandlerImpl.
     */
    @Test
    public void testLookupStatus() {
        System.out.println("lookupStatus");
        String messageStatus = " ";
        MStatus expResult = MStatus.PENDING;
        MStatus result = messageHandler.lookupStatus(messageStatus);
        assertEquals(expResult, result);
        
        messageStatus = "004";
        expResult = MStatus.DELIVERED;
        result = messageHandler.lookupStatus(messageStatus);
        assertEquals(expResult, result);
        
        messageStatus = "007";
        expResult = MStatus.FAILED;
        result = messageHandler.lookupStatus(messageStatus);
        assertEquals(expResult, result);
        
        messageStatus = "002";
        expResult = MStatus.PENDING;
        result = messageHandler.lookupStatus(messageStatus);
        assertEquals(expResult, result);
        
        messageStatus = "006";
        expResult = MStatus.CANCELLED;
        result = messageHandler.lookupStatus(messageStatus);
        assertEquals(expResult, result);
        
        messageStatus = "120";
        expResult = MStatus.EXPIRED;
        result = messageHandler.lookupStatus(messageStatus);
        assertEquals(expResult, result);
    }

    /**
     * Test of lookupResponse method, of class ORServeGatewayMessageHandlerImpl.
     */
    @Test
    public void testLookupResponse() {
        System.out.println("lookupResponse");
        String messageStatus = " ";
        MStatus expResult = MStatus.SCHEDULED;
        MStatus result = messageHandler.lookupResponse(messageStatus);
        assertEquals(expResult, result);
        
        messageStatus = "103";
        expResult = MStatus.RETRY;
        result = messageHandler.lookupResponse(messageStatus);
        assertEquals(expResult, result);
        
        messageStatus = "param:";
        expResult = MStatus.FAILED;
        result = messageHandler.lookupResponse(messageStatus);
        assertEquals(expResult, result);
        
        messageStatus = "116";
        expResult = MStatus.EXPIRED;
        result = messageHandler.lookupResponse(messageStatus);
        assertEquals(expResult, result);
    }

}