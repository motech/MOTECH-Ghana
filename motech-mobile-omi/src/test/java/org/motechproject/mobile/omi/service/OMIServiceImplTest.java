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

package org.motechproject.mobile.omi.service;

import org.springframework.transaction.annotation.Transactional;
import org.motechproject.mobile.core.dao.GatewayRequestDAO;
import org.motechproject.mobile.core.dao.GatewayRequestDetailsDAO;
import org.motechproject.mobile.core.dao.GatewayResponseDAO;
import org.motechproject.mobile.core.dao.LanguageDAO;
import org.motechproject.mobile.core.dao.MessageRequestDAO;
import org.motechproject.mobile.core.dao.NotificationTypeDAO;
import org.motechproject.mobile.core.manager.CoreManager;
import org.motechproject.mobile.core.model.GatewayRequest;
import org.motechproject.mobile.core.model.GatewayRequestDetails;
import org.motechproject.mobile.core.model.GatewayRequestDetailsImpl;
import org.motechproject.mobile.core.model.GatewayRequestImpl;
import org.motechproject.mobile.core.model.GatewayResponse;
import org.motechproject.mobile.core.model.GatewayResponseImpl;
import org.motechproject.mobile.core.model.Language;
import org.motechproject.mobile.core.model.LanguageImpl;
import org.motechproject.mobile.core.model.MStatus;
import org.motechproject.mobile.core.model.MessageRequest;
import org.motechproject.mobile.core.model.MessageRequestImpl;
import org.motechproject.mobile.core.model.MessageType;
import org.motechproject.mobile.core.model.NotificationTypeImpl;
import org.motechproject.mobile.omi.manager.MessageStoreManager;
import org.motechproject.mobile.omi.manager.StatusHandler;
import org.motechproject.mobile.omp.manager.OMPManager;
import org.motechproject.mobile.omp.service.MessagingService;
import java.util.ArrayList;
import static org.easymock.EasyMock.*;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.MediaType;
import org.motechproject.ws.MessageStatus;
import org.motechproject.ws.NameValuePair;
import org.motechproject.ws.Patient;
import org.springframework.test.context.transaction.TransactionConfiguration;
import static org.junit.Assert.*;

/**
 * Unit test for the OMIServiceImpl class
 *
 * @author Kofi A. Asamoah (yoofi@dreamoval.com)
 * Date Created Aug 10, 2009
 */
@TransactionConfiguration
@Transactional
public class OMIServiceImplTest {

    OMPManager mockOMP;
    CoreManager mockCore;
    Transaction mockTrans;
    OMIServiceImpl instance;
    MessageStoreManager mockStore;
    MessageRequestDAO mockRequestDao;
    LanguageDAO mockLangDao;
    NotificationTypeDAO mockNoteDao;
    MessagingService mockMessagingService;
    GatewayRequestDAO mockGatewayDao;
    GatewayResponseDAO mockResponseDao;
    GatewayRequestDetailsDAO mockGwDetDao;
    StatusHandler mockHandler;
    OMIServiceWorker mockWorker;

    public OMIServiceImplTest() {
    }

    @Before
    public void setUp(){
        mockCore = createMock(CoreManager.class);
        mockOMP = createMock(OMPManager.class);
        mockMessagingService = createMock(MessagingService.class);
        mockStore = createMock(MessageStoreManager.class);
        mockGatewayDao = createMock(GatewayRequestDAO.class);
        mockRequestDao = createMock(MessageRequestDAO.class);
        mockNoteDao = createMock(NotificationTypeDAO.class);
        mockLangDao = createMock(LanguageDAO.class);
        mockTrans = createMock(Transaction.class);
        mockGwDetDao = createMock(GatewayRequestDetailsDAO.class);
        mockHandler = createMock(StatusHandler.class);
        mockResponseDao = createMock(GatewayResponseDAO.class);
        mockWorker = createMock(OMIServiceWorker.class);
        
        instance = new OMIServiceImpl();
        instance.setCoreManager(mockCore);
        instance.setOmpManager(mockOMP);
        instance.setStoreManager(mockStore);
        instance.setStatHandler(mockHandler);
        instance.setWorker(mockWorker);
        instance.setMaxTries(3);
        instance.setDefaultLang("en");
    }

    /**
     * Test of sendPatientMessage method, of class OMIServiceImpl.
     */
    @Test
    public void testSavePatientMessageRequest() {
        System.out.println("savePatientMessageRequest");
        
        System.out.println("sendCHPSMessage");
        String messageId = "tsid17";
        String patientNumber = "000000000000";
        List<Language> langList = new ArrayList<Language>();
        langList.add(new LanguageImpl());
        NameValuePair[] personalInfo = new NameValuePair[0];
        String recipientId = "123456789";
        
        MessageStatus expResult = MessageStatus.QUEUED;

        mockRequestDao = createMock(MessageRequestDAO.class);
        
        
        expect(
                mockCore.createMessageRequest()
                ).andReturn(new MessageRequestImpl());
        expect(
                mockCore.createNotificationTypeDAO()
                ).andReturn(mockNoteDao);
        expect(
                mockNoteDao.getById(anyLong())
                ).andReturn(new NotificationTypeImpl());
        expect(
                mockCore.createLanguageDAO()
                ).andReturn(mockLangDao);
        expect(
                mockLangDao.getByCode((String)anyObject())
                ).andReturn(new LanguageImpl());
        expect(
                mockCore.createMessageRequestDAO()
                ).andReturn(mockRequestDao);
        expectLastCall();
        
        expect(
                mockRequestDao.save((MessageRequest) anyObject())
                ).andReturn(new MessageRequestImpl());
        
        expectLastCall();

//        replay(mockCore, mockLangDao, mockRequestDao, mockSession, mockTrans);
        replay(mockCore, mockLangDao, mockRequestDao);
        
        MessageStatus result = instance.savePatientMessageRequest(messageId, personalInfo, patientNumber, ContactNumberType.PERSONAL, "language", MediaType.TEXT, 1L, new Date(), new Date(), "123456789");
        assertEquals(expResult, result);
//        verify(mockCore, mockLangDao, mockRequestDao, mockSession, mockTrans);
        verify(mockCore, mockLangDao, mockRequestDao);
    }

    /**
     * Test of sendCHPSMessage method, of class OMIServiceImpl.
     */
    @Test
    public void testSaveCHPSMessageRequest() {
        System.out.println("saveCHPSMessage");
        String messageId = "tsid17";
        String workerNumber = "000000000000";
        Patient[] patientList = null;
        List<Language> langList = new ArrayList<Language>();
        langList.add(new LanguageImpl());
        NameValuePair[] personalInfo = new NameValuePair[0];
        
        MessageStatus expResult = MessageStatus.QUEUED;
        Date testDate = new Date();

        mockRequestDao = createMock(MessageRequestDAO.class);
        

        expect(
                mockCore.createMessageRequest()
                ).andReturn(new MessageRequestImpl());
        expect(
                mockCore.createNotificationTypeDAO()
                ).andReturn(mockNoteDao);
        expect(
                mockNoteDao.getById(anyLong())
                ).andReturn(new NotificationTypeImpl());
        expect(
                mockCore.createLanguageDAO()
                ).andReturn(mockLangDao);
        expect(
                mockLangDao.getByCode((String)anyObject())
                ).andReturn(new LanguageImpl());
        expect(
                mockCore.createMessageRequestDAO()
                ).andReturn(mockRequestDao);

        expectLastCall();
        
        expect(
                mockRequestDao.save((MessageRequest) anyObject())
                ).andReturn(new MessageRequestImpl());
        

        expectLastCall();

//        replay(mockCore, mockLangDao, mockRequestDao, mockSession, mockTrans);
        replay(mockCore, mockLangDao, mockRequestDao);

        MessageStatus result = instance.saveCHPSMessageRequest(messageId, personalInfo, workerNumber, patientList, "lang", MediaType.TEXT, 1L, testDate, testDate);
        assertEquals(expResult, result);
        verify(mockCore, mockLangDao, mockRequestDao);
    }
    
    @Test
    public void testSendMessage(){
        System.out.println("sendMessage");
        
        MessageRequest msgReq1 = new MessageRequestImpl();
        msgReq1.setDateFrom(new Date());
        msgReq1.setDateTo(new Date());
        msgReq1.setId(19000000001l);
        msgReq1.setTryNumber(1);
        msgReq1.setMessageType(MessageType.TEXT);
        msgReq1.setRecipientName("Tester");
        msgReq1.setRecipientNumber("000000000000");
        msgReq1.setStatus(MStatus.QUEUED);
        
        GatewayRequestImpl gwReq = new GatewayRequestImpl();
        gwReq.setGatewayRequestDetails(new GatewayRequestDetailsImpl());
        HashMap<Boolean, Set<GatewayResponse>> respMap = new HashMap<Boolean, Set<GatewayResponse>>();
        respMap.put(new Boolean(true), new HashSet<GatewayResponse>());
        
        expect(
                mockCore.createLanguageDAO()
                ).andReturn(mockLangDao);
        expect(
                mockLangDao.getByCode((String) anyObject())
                ).andReturn(new LanguageImpl());
        expect(
                mockCore.createMessageRequestDAO()
                ).andReturn(mockRequestDao);

        expectLastCall();

        expect(
                mockRequestDao.save((MessageRequest) anyObject())
                ).andReturn(new MessageRequestImpl());
        
        expect(
                mockStore.constructMessage((MessageRequest) anyObject(), (Language) anyObject())
                ).andReturn(gwReq);
        expect(
                mockOMP.createMessagingService()
                ).andReturn(mockMessagingService);
        expect(
                mockMessagingService.sendMessage((GatewayRequest) anyObject() )
                ).andReturn(respMap);

        expectLastCall();
        
        expect(
                mockRequestDao.save((MessageRequest) anyObject())
                ).andReturn(new MessageRequestImpl());
        

        expectLastCall();
        
        replay(mockStore, mockOMP, mockMessagingService, mockCore, mockRequestDao);
        instance.sendMessage(msgReq1);
        verify(mockStore, mockOMP, mockMessagingService, mockCore, mockRequestDao);
    }

    /**
     * Test of processMessageRequests method
     */
    @Test
    public void testProcessMessageRequests(){
        System.out.println("processMessageRequests");
        List<MessageRequest> messageList = new ArrayList<MessageRequest>();
        
        MessageRequest msgReq1 = new MessageRequestImpl();
        msgReq1.setDateFrom(new Date());
        msgReq1.setDateTo(new Date());
        msgReq1.setId(19000000002l);
        msgReq1.setTryNumber(1);
        msgReq1.setMessageType(MessageType.TEXT);
        msgReq1.setRecipientName("Tester");
        msgReq1.setRecipientNumber("000000000000");
        msgReq1.setStatus(MStatus.QUEUED);
        messageList.add(msgReq1);
        
        GatewayRequest gwReq = new GatewayRequestImpl();
        gwReq.setGatewayRequestDetails(new GatewayRequestDetailsImpl());
        
     
        expect(
                mockCore.createMessageRequestDAO()
                ).andReturn(mockRequestDao);
        expect(
                mockRequestDao.getMsgByStatus((MStatus) anyObject())
                ).andReturn(messageList);
        expect(
                mockCore.createLanguageDAO()
                ).andReturn(mockLangDao);
        expect(
                mockLangDao.getByCode((String) anyObject())
                ).andReturn(new LanguageImpl());

        replay(mockCore, mockRequestDao, mockStore);
        instance.processMessageRequests();
        verify(mockCore, mockRequestDao,  mockStore);
    }
    
    /**
     * Test processMessageRetries method
     */
    @Test
    public void testProcessMessageRetries(){
        System.out.println("processMessageRetries");
        List<MessageRequest> messageList = new ArrayList<MessageRequest>();
        
        MessageRequest msgReq1 = new MessageRequestImpl();
        msgReq1.setDateFrom(new Date());
        msgReq1.setDateTo(new Date());
        msgReq1.setId(19000000003l);
        msgReq1.setTryNumber(1);
        msgReq1.setMessageType(MessageType.TEXT);
        msgReq1.setRecipientName("Tester");
        msgReq1.setRecipientNumber("000000000000");
        msgReq1.setStatus(MStatus.QUEUED);
        messageList.add(msgReq1);
        
        GatewayRequestDetails details = new GatewayRequestDetailsImpl();
        details.setId(19000000004l);
        details.setMessage("Some message");
        details.setMessageType(MessageType.TEXT);
        details.setNumberOfPages(1);
        details.setGatewayRequests(new HashSet());
        
        msgReq1.setGatewayRequestDetails(details);
      
        expect(
                mockCore.createMessageRequestDAO()
                ).andReturn(mockRequestDao);
        expect(
                mockRequestDao.getMsgRequestByStatusAndTryNumber((MStatus) anyObject(), anyInt())
                ).andReturn(messageList);
        
        replay(mockCore, mockRequestDao);
        instance.processMessageRetries();
        verify(mockCore, mockRequestDao);
    }

    @Test
    public void testProcessMessageResponses(){
        System.out.println("processMessageResponses");
        
        List<MessageRequest> msgList = new ArrayList<MessageRequest>();
        
        MessageRequestImpl request = new MessageRequestImpl();
        request.setId(19000000005l);
        request.setStatus(MStatus.PENDING);
        
        msgList.add(request);

        GatewayRequest gwReq = new GatewayRequestImpl();
        gwReq.setMessageRequest(request);
        gwReq.setId(19000000006l);

        GatewayResponseImpl response = new GatewayResponseImpl();
        response.setMessageStatus(MStatus.DELIVERED);
        response.setGatewayRequest(gwReq);
        List<GatewayResponse> responses = new ArrayList<GatewayResponse>();
        responses.add(response);

        expect(
                mockCore.createGatewayResponseDAO()
                ).andReturn(mockResponseDao);
        expect(
                mockResponseDao.getByPendingMessageAndMaxTries(anyInt())
                ).andReturn(responses);
        
        replay(mockCore, mockResponseDao);
        instance.processMessageResponses();
        verify(mockCore, mockResponseDao);
    }
}