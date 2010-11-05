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

package org.motechproject.mobile.omi.ws;

import static org.easymock.EasyMock.*;

import java.util.Date;

import org.motechproject.mobile.omi.manager.OMIManager;
import org.motechproject.mobile.omi.service.OMIService;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.MediaType;
import org.motechproject.ws.MessageStatus;
import org.motechproject.ws.NameValuePair;
import org.motechproject.ws.Patient;
import static org.junit.Assert.*;

/**
 * Unit test for the MessagingServiceImpl class
 *
 * @author Kofi A. Asamoah (yoofi@dreamoval.com)
 * Date Created Aug 10, 2009
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath:META-INF/omi-config.xml"})
public class MessageServiceImplTest{

    OMIManager mockOMI;
    OMIService mockOMIService;

    MessageServiceImpl instance;

    public MessageServiceImplTest() {
    }

    @Before
    public void setUp(){
        instance = new MessageServiceImpl();
        mockOMI = createMock(OMIManager.class);
        mockOMIService = createMock(OMIService.class);
        instance.setOmiManager(mockOMI);

        expect(
               mockOMI.createOMIService()
               ).andReturn(mockOMIService);
    }

    /**
     * Test of sendPatientMessage method, of class MessageServiceImpl.
     */
    @Test
    public void testSendPatientMessage() {
        System.out.println("sendPatientMessage");
        String messageId = "tsid1";
        String clinic = "Test clinic";
        Date serviceDate = null;        
        
        NameValuePair attrib = new NameValuePair("Test", "Test");
        NameValuePair[] personalInfo = new NameValuePair[]{attrib};
        
        String patientNumber = "000000000000";
        ContactNumberType patientNumberType = ContactNumberType.PERSONAL;
        MediaType messageType = MediaType.TEXT;
        String recipientId = "123456789";

        expect(
                mockOMIService.savePatientMessageRequest((String) anyObject(), (NameValuePair[]) anyObject(), (String) anyObject(), (ContactNumberType) anyObject(), (String) anyObject(), (MediaType) anyObject(), (Long) anyObject(), (Date) anyObject(), (Date) anyObject(), (String)anyObject())
                ).andReturn(MessageStatus.QUEUED);
        replay(mockOMI, mockOMIService);
        
        MessageStatus result = instance.sendPatientMessage(messageId, personalInfo, patientNumber, patientNumberType, "db_GH", messageType, 13L, null, null, "123456789");
        assertEquals(MessageStatus.QUEUED, result);
        verify(mockOMI, mockOMIService);
    }

    /**
     * Test of sendCHPSMessage method, of class MessageServiceImpl.
     */
    @Test
    public void testSendCHPSMessage() {
        System.out.println("sendCHPSMessage");
        String messageId = "tsid2";
        String workerName = "Test worker";
        String workerNumber = "000000000000";
        MediaType messageType = MediaType.TEXT;
        Patient[] patientList = null;        
        
        NameValuePair attrib = new NameValuePair("Test", "Test");
        NameValuePair[] personalInfo = new NameValuePair[]{attrib};

        expect(
                mockOMIService.saveCHPSMessageRequest((String) anyObject(), ( NameValuePair[]) anyObject(), (String) anyObject(), (Patient[]) anyObject(), (String) anyObject(), (MediaType) anyObject(), (Long) anyObject(), (Date) anyObject(), (Date) anyObject())
                ).andReturn(MessageStatus.QUEUED);    
        replay(mockOMI, mockOMIService);
        
        MessageStatus result = instance.sendCHPSMessage(messageId, personalInfo, workerNumber, patientList, "Lang", messageType, 13L, null, null);
        assertEquals(result, MessageStatus.QUEUED);
        verify(mockOMI, mockOMIService);
    }

}
