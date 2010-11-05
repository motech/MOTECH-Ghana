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

package org.motechproject.mobile.omi.manager;

import org.motechproject.mobile.core.model.GatewayResponse;
import org.motechproject.mobile.core.model.GatewayResponseImpl;
import org.motechproject.mobile.core.model.MStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static org.easymock.EasyMock.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for the ReportStatusActionImpl class
 *
 * @author Kofi A. Asamoah (yoofi@dreamoval.com)
 * Date Created Oct 02, 2009
 */
public class StatusHandlerImplTest{
    Map<MStatus, List<StatusAction>> mockRegister;
    StatusHandlerImpl instance;
    StatusAction mockAction;
    
    public StatusHandlerImplTest() {
    }

    @Before
    public void setUp(){
        mockRegister = createMock(Map.class);
        mockAction = createMock(StatusAction.class);
        
        instance = new StatusHandlerImpl();        
        instance.setActionRegister(mockRegister);
    }
    
    @Test
    public void testHandleStatus(){
        System.out.println("handleStatus");       
                
        GatewayResponse response = new GatewayResponseImpl();
        response.setGatewayMessageId("DoActionwerfet54y56g645v4e");
        response.setMessageStatus(MStatus.DELIVERED);
        response.setRecipientNumber("000000000000");
        response.setResponseText("Some gateway response message");
        response.setId(18000000001l);
        
        List<StatusAction> actionList = new ArrayList<StatusAction>();
        actionList.add(mockAction);
        
        expect(
                mockRegister.get((MStatus) anyObject())
                ).andReturn(actionList);
        mockAction.doAction(response);
        expectLastCall();
        
        replay(mockRegister, mockAction);
        instance.handleStatus(response);
        verify(mockRegister, mockAction);
    }
    
    @Test
    public void testregisterStatusAction(){
        System.out.println("registerStatusAction");       
                
        List<StatusAction> actionList = new ArrayList<StatusAction>();
        
        expect(
                mockRegister.get((MStatus)anyObject())
                ).andReturn(actionList);       
        
        
        replay(mockRegister);
        instance.registerStatusAction(MStatus.PENDING, mockAction);
        verify(mockRegister);
    }
}