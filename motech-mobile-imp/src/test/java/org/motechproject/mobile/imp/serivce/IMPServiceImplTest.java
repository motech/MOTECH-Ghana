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

package org.motechproject.mobile.imp.serivce;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mobile.core.manager.CoreManager;
import org.motechproject.mobile.core.model.Duplicatable;
import org.motechproject.mobile.core.model.IncMessageFormStatus;
import org.motechproject.mobile.core.model.IncMessageStatus;
import org.motechproject.mobile.core.model.IncomingMessage;
import org.motechproject.mobile.core.model.IncomingMessageFormDefinitionImpl;
import org.motechproject.mobile.core.model.IncomingMessageFormImpl;
import org.motechproject.mobile.core.model.IncomingMessageImpl;
import org.motechproject.mobile.core.model.IncomingMessageResponse;
import org.motechproject.mobile.core.model.IncomingMessageResponseImpl;
import org.motechproject.mobile.imp.manager.IMPManager;
import org.motechproject.mobile.imp.util.CommandAction;
import org.motechproject.mobile.imp.util.IncomingMessageParser;
import org.motechproject.mobile.model.dao.imp.IncomingMessageDAO;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test for IMPServiceImpl class
 *
 *  Date : Dec 5, 2009
 * @author Kofi A.  Asamoah (yoofi@dreamoval.com)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/imp-test-config.xml"})
public class IMPServiceImplTest {
    IMPManager mockImp;
    Transaction mockTrans;
    CoreManager mockCore;
    CommandAction mockCmdAxn;
    IncomingMessageDAO mockMsgDao;
    IncomingMessageParser mockParser;
    MessageRegistry mockRegistry;

    IMPServiceImpl instance;

    public IMPServiceImplTest() {
    }

    @Before
    public void setUp() {
        mockCore = createMock(CoreManager.class);
        mockCmdAxn = createMock(CommandAction.class);
        mockParser = createMock(IncomingMessageParser.class);
        mockMsgDao = createMock(IncomingMessageDAO.class);
        mockTrans = createMock(Transaction.class);
        mockImp = createMock(IMPManager.class);
        mockRegistry = createMock(MessageRegistry.class);

        instance = new IMPServiceImpl();
        instance.setParser(mockParser);
        instance.setCoreManager(mockCore);
        instance.setImpManager(mockImp);
        instance.setCharsPerSMS(160);
        instance.setMessageRegistry(mockRegistry);
    }

    /**
     * Test of processRequest method, of class IMPServiceImpl.
     * @throws DuplicateMessageException 
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testProcessRequest() throws DuplicateMessageException {
        System.out.println("processRequest");
        String message = "Type=EditPatient\nCHPSID=123\nPatientRegNum=123";
        String requesterPhone = "000000000000";
        String expResult = "Form entry cancelled";
        Map<String, CommandAction> caMap = new HashMap<String, CommandAction>();
        caMap.put("TYPE", mockCmdAxn);
        instance.setCmdActionMap(caMap);

        IncomingMessage inMsg = new IncomingMessageImpl();
        inMsg.setIncomingMessageForm(new IncomingMessageFormImpl());
        inMsg.getIncomingMessageForm().setIncomingMsgFormDefinition(new IncomingMessageFormDefinitionImpl());
        inMsg.getIncomingMessageForm().getIncomingMsgFormDefinition().setSendResponse(false);

        IncomingMessageResponseImpl response = new IncomingMessageResponseImpl();
        response.setContent(expResult);

//        expect(
//                mockCore.createMotechContext()
//                ).andReturn(new MotechContextImpl());
        expect(mockCore.createIncomingMessageDAO()).andReturn(mockMsgDao);
        expect(
                mockCore.createIncomingMessageResponse()
                ).andReturn(new IncomingMessageResponseImpl());
        expect(mockRegistry.registerMessage(message)).andReturn(inMsg);
        expect(mockMsgDao.save(inMsg)).andReturn(inMsg);
        expect(
                mockParser.getCommand((String)anyObject())
                ).andReturn("Type");
//        expect(
//                mockMsgDao.getDBSession()
//                ).andReturn(mockSession);
//        expect(
//                mockSession.getTransaction()
//                ).andReturn(mockTrans);
        expect(
                mockCmdAxn.execute((IncomingMessage) anyObject(), (String) anyObject())
                ).andReturn(response);

//        replay(mockParser, mockCore, mockMsgDao, mockSession, mockTrans, mockImp, mockCmdAxn);
        replay(mockParser, mockCore, mockMsgDao, mockImp, mockCmdAxn, mockRegistry);
        IncomingMessageResponse result = instance.processRequest(message, requesterPhone, false);
//        verify(mockParser, mockCore, mockMsgDao, mockSession, mockTrans, mockImp, mockCmdAxn);
        verify(mockParser, mockCore, mockMsgDao, mockImp, mockCmdAxn, mockRegistry);

        assertEquals(expResult, result.getContent());

        //Test duplicate message detection
//        reset(mockParser, mockCore, mockMsgDao, mockSession, mockTrans, mockImp, mockCmdAxn);
        reset(mockParser, mockCore, mockMsgDao, mockImp, mockCmdAxn, mockRegistry);

        inMsg.setContent(message);
        inMsg.getIncomingMessageForm().getIncomingMsgFormDefinition().setDuplicatable(Duplicatable.DISALLOWED);
        inMsg.getIncomingMessageForm().setMessageFormStatus(IncMessageFormStatus.SERVER_VALID);
        
//        expect(
//                mockCore.createMotechContext()
//                ).andReturn(new MotechContextImpl());

        expect(mockRegistry.registerMessage(message)).andThrow(
				new DuplicateMessageException());
        expect(
                mockCore.createIncomingMessageResponse()
                ).andReturn(new IncomingMessageResponseImpl());
        expect(mockCore.createIncomingMessageDAO()).andReturn(mockMsgDao);

//        replay(mockParser, mockCore, mockMsgDao, mockSession, mockTrans, mockImp, mockCmdAxn);
        replay(mockParser, mockCore, mockMsgDao, mockImp, mockCmdAxn, mockRegistry);
        result = instance.processRequest(message, requesterPhone, false);
//        verify(mockParser, mockCore, mockMsgDao, mockSession, mockTrans, mockImp, mockCmdAxn);
        verify(mockParser, mockCore, mockMsgDao, mockImp, mockCmdAxn, mockRegistry);

        assertEquals(instance.getFormProcessSuccess(), result.getContent());
        
        reset(mockParser, mockCore, mockMsgDao, mockImp, mockCmdAxn, mockRegistry);

        inMsg.setContent(message);
        inMsg.setMessageStatus(IncMessageStatus.PROCESSING);
        
        expect(mockRegistry.registerMessage(message)).andThrow(
				new DuplicateProcessingException());
        expect(
                mockCore.createIncomingMessageResponse()
                ).andReturn(new IncomingMessageResponseImpl());
        expect(mockCore.createIncomingMessageDAO()).andReturn(mockMsgDao);

        replay(mockParser, mockCore, mockMsgDao, mockImp, mockCmdAxn, mockRegistry);
        result = instance.processRequest(message, requesterPhone, false);
        verify(mockParser, mockCore, mockMsgDao, mockImp, mockCmdAxn, mockRegistry);

        String uploadResponse = result.getContent();
		assertTrue(uploadResponse.startsWith("Error:")
				&& uploadResponse.contains("try again"));
    }

    @Test
    public void testFormatPhoneNumber(){
        String number = "0244000000";
        String expResult = "233244000000";

        instance.setDefaultCountryCode("233");
        instance.setLocalNumberExpression("0[0-9]{9}");
        String result = instance.formatPhoneNumber(number);        
        assertEquals(result, expResult);

        number = "1234567890";
        result = instance.formatPhoneNumber(number);
        assertEquals(number, result);

        number = "01234567890";
        result = instance.formatPhoneNumber(number);
        assertEquals(number, result);
    }
}