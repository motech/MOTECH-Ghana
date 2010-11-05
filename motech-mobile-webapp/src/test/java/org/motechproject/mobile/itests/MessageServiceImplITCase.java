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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.motechproject.mobile.itests;

import org.motechproject.mobile.imp.serivce.IMPService;
import org.motechproject.mobile.omi.service.OMIService;
import org.motechproject.mobile.omp.service.MessagingService;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import javax.annotation.Resource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mobile.core.model.IncomingMessageResponse;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.MediaType;
import org.motechproject.ws.MessageStatus;
import org.motechproject.ws.NameValuePair;
import org.motechproject.ws.Patient;
import org.motechproject.ws.PatientMessage;
import org.motechproject.ws.mobile.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;

/**
 * Integration tests for the MessageServiceImpl class
 *
 * @author Kofi A. Asamoah (yoofi@dreamoval.com)
 * Date Created Aug 10, 2009
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:WEB-INF/webapp-config.xml", "classpath:META-INF/client-config.xml"})
public class MessageServiceImplITCase {

    Properties testProps;
    @Resource
    MessageService client;
    @Autowired
    OMIService omiService;
    @Autowired
    MessagingService smsService;
    @Autowired
    IMPService impService;

    public MessageServiceImplITCase() {
    }

    @Before
    public void setUp() {
        testProps = new Properties();

        try {
            testProps.load(getClass().getResourceAsStream("/test.properties"));
        } catch (IOException ex) {
            System.out.print(ex);
        }
    }

    /**
     * Test of sendPatientMessages method, of class MessageServiceImpl.
     */
    @Test
    public void testSendPatientMessages() {
        System.out.println("sendPatientMessages");
        
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        
        PatientMessage msg1 = new PatientMessage();
        msg1.setMessageId("testId1");
        NameValuePair attrib1 = new NameValuePair("PatientFirstName", "Tester1");
        NameValuePair attrib2 = new NameValuePair("DueDate", "now");
        msg1.setPersonalInfo(new NameValuePair[]{attrib1, attrib2});
        msg1.setStartDate(date);
        cal.add(Calendar.HOUR, 1);
        msg1.setEndDate(cal.getTime());
        msg1.setPatientNumber("0900000001");
        msg1.setPatientNumberType(ContactNumberType.PERSONAL);
        msg1.setMediaType(MediaType.TEXT);
        msg1.setLangCode("en");
        msg1.setNotificationType(2L);
        msg1.setRecipientId("1234567");
        
        PatientMessage msg2 = new PatientMessage();
        msg1.setMessageId("testId1");
        NameValuePair attrib3 = new NameValuePair("PatientFirstName", "Tester2");
        NameValuePair attrib4 = new NameValuePair("DueDate", "now");
        msg1.setPersonalInfo(new NameValuePair[]{attrib3, attrib4});
        msg1.setStartDate(date);
        cal.setTime(date);
        cal.add(Calendar.HOUR, 1);
        msg1.setEndDate(cal.getTime());
        msg1.setPatientNumber("0900000002");
        msg1.setPatientNumberType(ContactNumberType.HOUSEHOLD);
        msg1.setMediaType(MediaType.VOICE);
        msg1.setLangCode("en");
        msg1.setNotificationType(2L);
        msg1.setRecipientId("1234568");
        
        PatientMessage[] messages = new PatientMessage[]{msg1,msg2};
        
        client.sendPatientMessages(messages);
    }
    
    /**
     * Test of sendPatientMessage method, of class MessageServiceImpl.
     */
    @Test
    public void testSendPatientMessage() {
        System.out.println("sendPatientMessage");
        String messageId = "testId1";

        NameValuePair attrib = new NameValuePair("PatientFirstName", "Tester");
        NameValuePair attrib2 = new NameValuePair("DueDate", "now");
        NameValuePair[] personalInfo = new NameValuePair[]{attrib, attrib2};

        Date serviceDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, 1);
        Date endDate = cal.getTime();

        String patientNumber = testProps.getProperty("patientNumber", "000000000000");
        ContactNumberType patientNumberType = ContactNumberType.PERSONAL;
        MediaType messageType = MediaType.TEXT;
        MessageStatus result = client.sendPatientMessage(messageId, personalInfo, patientNumber, patientNumberType, "en", messageType, 2L, serviceDate, endDate, "123456789");
        assertEquals(result, MessageStatus.QUEUED);
    }

    /**
     * Test of sendPatientMessage method, of class MessageServiceImpl.
     */
    @Test
    public void testSendPatientMessage_NullInfo() {
        System.out.println("sendPatientMessage with null personalInfo");
        String messageId = "testId2";

        Date serviceDate = new Date();
        String patientNumber = testProps.getProperty("patientNumber", "000000000000");
        ContactNumberType patientNumberType = ContactNumberType.PERSONAL;
        MediaType messageType = MediaType.TEXT;
        MessageStatus result = client.sendPatientMessage(messageId, null, patientNumber, patientNumberType, "nan", messageType, 3L, serviceDate, serviceDate, "123456789");
        assertEquals(result, MessageStatus.QUEUED);
    }

    /**
     * Test of sendPatientMessage method, of class MessageServiceImpl.
     */
    @Test
    public void testSendPatientMessage_NullDate() {
        System.out.println("sendPatientMessage with null dates");
        String messageId = "testId3";

        NameValuePair attrib = new NameValuePair("PatientFirstName", "Tester");
        NameValuePair attrib2 = new NameValuePair("DueDate", "now");
        NameValuePair[] personalInfo = new NameValuePair[]{attrib, attrib2};

        String patientNumber = testProps.getProperty("patientNumber", "000000000000");
        ContactNumberType patientNumberType = ContactNumberType.PERSONAL;
        MediaType messageType = MediaType.TEXT;
        MessageStatus result = client.sendPatientMessage(messageId, personalInfo, patientNumber, patientNumberType, "kas", messageType, 4L, null, null, "123456789");
        assertEquals(result, MessageStatus.PENDING);
    }

    /**
     * Test of sendCHPSMessage method of MessageServiceImpl class.
     */
    @Test
    public void testSendCHPSMessage() {
        System.out.println("sendCHPSMessage");
        String messageId = "testId4";
        String workerNumber = testProps.getProperty("workerNumber", "000000000000");
        Date serviceDate = new Date();
        MediaType messageType = MediaType.TEXT;

        NameValuePair attrib = new NameValuePair("Test", "Test");
        NameValuePair[] personalInfo = new NameValuePair[]{attrib};

        Patient patient = new Patient();
        patient.setPreferredName("Test patient");
        patient.setMotechId("TS000000001");
        Patient[] patientList = new Patient[]{patient};

        MessageStatus result = client.sendCHPSMessage(messageId, personalInfo, workerNumber, patientList, "en", messageType, 5L, serviceDate, serviceDate);
        assertEquals(result, MessageStatus.QUEUED);
    }

    /**
     * Test of sendCHPSMessage method of MessageServiceImpl class.
     */
    @Test
    public void testSendCHPSMessage_NullInfo() {
        System.out.println("sendCHPSMessage with null personalInfo");
        String messageId = "testId5";
        String workerNumber = testProps.getProperty("workerNumber", "000000000000");
        Date serviceDate = new Date();
        MediaType messageType = MediaType.TEXT;

        Patient patient = new Patient();
        patient.setPreferredName("Test patient");
        patient.setMotechId("TS000000001");
        Patient[] patientList = new Patient[]{patient};

        MessageStatus result = client.sendCHPSMessage(messageId, null, workerNumber, patientList, "kas", messageType, 6L, serviceDate, serviceDate);
        assertEquals(result, MessageStatus.QUEUED);
    }

    /**
     * Test of sendCHPSMessage method of MessageServiceImpl class.
     */
    @Test
    public void testSendCHPSMessage_NullPatient() {
        System.out.println("sendCHPSMessage with null patientList");
        String messageId = "testId6";
        String workerNumber = testProps.getProperty("workerNumber", "000000000000");
        Date serviceDate = new Date();
        MediaType messageType = MediaType.TEXT;

        NameValuePair attrib = new NameValuePair("Test", "Test");
        NameValuePair[] personalInfo = new NameValuePair[]{attrib};

        MessageStatus result = client.sendCHPSMessage(messageId, personalInfo, workerNumber, null, "nan", messageType, 7L, serviceDate, serviceDate);
        assertEquals(result, MessageStatus.QUEUED);
    }

    /**
     * Test of sendCHPSMessage method of MessageServiceImpl class.
     */
    @Test
    public void testSendCHPSMessage_NullDates() {
        System.out.println("sendCHPSMessage with null dates");
        String messageId = "testId7";
        String workerNumber = testProps.getProperty("workerNumber", "000000000000");
        Date serviceDate = null;
        MediaType messageType = MediaType.TEXT;

        NameValuePair attrib = new NameValuePair("Test", "Test");
        NameValuePair[] personalInfo = new NameValuePair[]{attrib};

        Patient patient = new Patient();
        patient.setPreferredName("Test patient");
        patient.setMotechId("TS000000001");
        Patient[] patientList = new Patient[]{patient};

        MessageStatus result = client.sendCHPSMessage(messageId, personalInfo, workerNumber, patientList, "en", messageType, 8L, serviceDate, serviceDate);
        assertEquals(result, MessageStatus.PENDING);

    }

    @Test
    public void testProcessMessageRequests() {
        System.out.println("processMessageRequests");
            omiService.processMessageRequests();
    }

    @Test
    public void testSendScheduledMessages() {
        System.out.println("sendScheduledMessages");
        smsService.sendScheduledMessages();
    }

    @Test
    public void testUpdateMessageStatuses() {
        System.out.println("updateMessageStatuses");
        smsService.updateMessageStatuses();
    }

    @Test
    public void testProcessMessageResponses() {
        System.out.println("processMessageResponses");
        omiService.processMessageResponses();
    }

    @Test
    public void testProcessMessageRetries() {
        System.out.println("processMessageRetries");
        omiService.processMessageRetries();
    }

    @Test
    public void testProcessRequest() {
        System.out.println("processRequest");

        String request = "Type=GeneralOPD\nCHPSID=123ABC\nDate=02-03-2010\nSerialNo=ANC123\nSex=f\nDoB=28/05/1981\nInsured=y\nNewCase=n\nDiagnosis=35\nSecondaryDiagnosis=14\nReferral=yup\ntime=" + new Date().getTime();
        String number = "555555555";

        String expResult = "Errors:\nReferral=wrong format";
        IncomingMessageResponse result = impService.processRequest(request, number, false);
        System.out.println("result: " + (result == null ? "NULL" : result.getContent()));
        System.out.println("expected: " + expResult);
        assertEquals(result.getContent(), expResult);

        request = "Type=GeneralOPD\nCHPSID=123ABC\nDate=02-03-2010\nSerialNo=ANC123\nSex=f\nDoB=28/05/1981\nInsured=y\nNewCase=n\nDiagnosis=35\nSecondaryDiagnosis=14\nReferral=y\ntime=" + new Date().getTime();
        expResult = "An unexpected error occurred! Please try again.";
        result = impService.processRequest(request, number, false);
        System.out.println("result: " + (result == null ? "NULL" : result.getContent()));
        System.out.println("expected: " + expResult);
        assertEquals(result.getContent(), expResult);
    }
}
