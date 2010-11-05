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

package org.motechproject.mobile.imp.util;

import org.motechproject.mobile.core.model.IncMessageFormDefinitionType;
import org.motechproject.mobile.core.model.IncomingMessageSession;
import org.motechproject.mobile.core.model.IncomingMessageSessionImpl;
import org.motechproject.mobile.model.dao.imp.IncomingMessageDAO;
import org.motechproject.mobile.model.dao.imp.IncomingMessageFormDAO;
import org.motechproject.mobile.model.dao.imp.IncomingMessageFormDefinitionDAO;
import org.motechproject.mobile.model.dao.imp.IncomingMessageSessionDAO;
import org.motechproject.mobile.core.model.IncMessageFormParameterStatus;
import org.motechproject.mobile.core.model.IncMessageFormStatus;
import org.motechproject.mobile.core.model.IncomingMessage;
import org.motechproject.mobile.core.model.IncomingMessageForm;
import org.motechproject.mobile.core.model.IncomingMessageFormDefinitionImpl;
import org.motechproject.mobile.core.model.IncomingMessageFormImpl;
import org.motechproject.mobile.core.model.IncomingMessageFormParameter;
import org.motechproject.mobile.core.model.IncomingMessageFormParameterImpl;
import org.motechproject.mobile.core.model.IncomingMessageImpl;
import org.motechproject.mobile.core.model.IncomingMessageResponse;
import org.motechproject.mobile.core.model.IncomingMessageResponseImpl;
import org.motechproject.mobile.model.dao.imp.IncomingMessageResponseDAO;
import java.util.HashMap;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

/**
 * Test for FormCommandAction class
 *
 *  Date : Dec 6, 2009
 * @author Kofi A. Asamoah (yoofi@dreamoval.com)
 */
//@TransactionConfiguration
//@Transactional
public class FormCommandActionTest {

    ApplicationContext applicationContext;
    Transaction mockTrans;
    FormCommandAction instance;
    FormProcessor mockProcessor;
    IncomingMessageDAO mockMsgDao;
    IncomingMessageParser mockParser;
    IncomingMessageFormDAO mockFormDao;
    IncomingMessageSessionDAO mockSessDao;
    IncomingMessageResponseDAO mockRespDao;
    IncomingMessageFormValidator mockValidator;
    IncomingMessageFormDefinitionDAO mockFormDefDao;

    public FormCommandActionTest() {
    }

    @Before
    public void setUp() {
        applicationContext = createMock(ApplicationContext.class);
        mockParser = createMock(IncomingMessageParser.class);
        mockTrans = createMock(Transaction.class);
        mockProcessor = createMock(FormProcessor.class);

        instance = new FormCommandAction();
        instance.setParser(mockParser);
        instance.setApplicationContext(applicationContext);
        instance.setFormProcessor(mockProcessor);
    }

    /**
     * Test of execute method, of class FormCommandAction.
     */
   @Test
    public void testExecute() {
        System.out.println("execute");
        
        String requesterPhone = "000000000000";
        IncomingMessage message = new IncomingMessageImpl();
        String expResult = "An unexpected error occurred! Please try again.";
        
        IncomingMessageFormImpl msgForm = new IncomingMessageFormImpl();
        msgForm.setMessageFormStatus(IncMessageFormStatus.VALID);
        
        IncomingMessageFormDefinitionImpl formDefn = new IncomingMessageFormDefinitionImpl();
        formDefn.setType(IncMessageFormDefinitionType.ENCOUNTER);

        mockFormDao = createMock(IncomingMessageFormDAO.class);
        mockSessDao = createMock(IncomingMessageSessionDAO.class);
        mockRespDao = createMock(IncomingMessageResponseDAO.class);
        mockValidator = createMock(IncomingMessageFormValidator.class);
        mockFormDefDao = createMock(IncomingMessageFormDefinitionDAO.class);
        instance.setFormValidator(mockValidator);

        //Initialize Session
        expect(
                applicationContext.getBean("incomingMessageSession", IncomingMessageSession.class)
                ).andReturn(new IncomingMessageSessionImpl());
        expect(
                mockParser.getFormCode((String) anyObject())
                ).andReturn("GENERAL");
        expect(
                applicationContext.getBean("incomingMessageSessionDAO", IncomingMessageSessionDAO.class)
                ).andReturn(mockSessDao);
        expectLastCall();
        expect(
                mockSessDao.save((IncomingMessageSession) anyObject())
                ).andReturn(null);
        expectLastCall();
        
        expect(
                applicationContext.getBean("incomingMessageFormDefinitionDAO", IncomingMessageFormDefinitionDAO.class)
                ).andReturn(mockFormDefDao);
        expect(
                mockFormDefDao.getByCode((String) anyObject())
                ).andReturn(formDefn);
        expect(
                applicationContext.getBean("incomingMessageForm", IncomingMessageForm.class)
                ).andReturn(msgForm);
        expect(
                mockParser.getParams((String)anyObject())
                ).andReturn(new HashMap<String,IncomingMessageFormParameter>());
        expect(
                applicationContext.getBean("incomingMessageFormDAO", IncomingMessageFormDAO.class)
                ).andReturn(mockFormDao);
        expectLastCall();

        expect(
                mockFormDao.save((IncomingMessageSession) anyObject())
                ).andReturn(null);

        expectLastCall();
        
        expect(
                mockValidator.validate((IncomingMessageForm)anyObject(), (String)anyObject())
                ).andReturn(IncMessageFormStatus.VALID);

        expect(
                applicationContext.getBean("incomingMessageResponse", IncomingMessageResponse.class)
                ).andReturn(new IncomingMessageResponseImpl());
        expect(
                applicationContext.getBean("incomingMessageResponseDAO", IncomingMessageResponseDAO.class)
                ).andReturn(mockRespDao);

        expectLastCall();

        expect(
                mockRespDao.save((IncomingMessageResponse) anyObject())
                ).andReturn(null);

        expectLastCall();
        
        expect(
                applicationContext.getBean("incomingMessageSessionDAO", IncomingMessageSessionDAO.class)
                ).andReturn(mockSessDao);
        
        expectLastCall();

        expect(
                mockSessDao.save((IncomingMessageSession) anyObject())
                ).andReturn(null);

        expectLastCall();
        

        replay(applicationContext, mockParser,mockSessDao,mockFormDefDao,mockFormDao, mockValidator, mockRespDao);
        IncomingMessageResponse result = instance.execute(message, requesterPhone);
        verify(applicationContext, mockParser,mockSessDao,mockFormDefDao,mockFormDao, mockValidator, mockRespDao);

        assertNotNull(result);
        assertEquals(expResult, result.getContent());
    }

    /**
     * Test of initializeSession method, of class FormCommandAction.
     */
    @Test
    public void testInitializeSession() {
        System.out.println("initializeSession");
        IncomingMessage message = new IncomingMessageImpl();
        String requesterPhone = "000000000000";


        mockSessDao = createMock(IncomingMessageSessionDAO.class);

        expect(
                applicationContext.getBean("incomingMessageSession", IncomingMessageSession.class)
                ).andReturn(new IncomingMessageSessionImpl());
        expect(
                mockParser.getFormCode((String) anyObject())
                ).andReturn("GENERAL");
        expect(
                applicationContext.getBean("incomingMessageSessionDAO", IncomingMessageSessionDAO.class)
                ).andReturn(mockSessDao);
    
        expectLastCall();

        expect(
                mockSessDao.save((IncomingMessageSession) anyObject())
                ).andReturn(null);

        expectLastCall();

        replay(applicationContext,mockParser,mockSessDao);
        IncomingMessageSession result = instance.initializeSession(message, requesterPhone);
        verify(applicationContext,mockParser,mockSessDao);

        assertNotNull(result);
    }

    /**
     * Test of initializeForm method, of class FormCommandAction.
     */
    @Test
    public void testInitializeForm() {
        System.out.println("initializeForm");
        IncomingMessage message = new IncomingMessageImpl();
        message.setContent("test content");
        String formCode = "GENERAL";

        mockFormDefDao = createMock(IncomingMessageFormDefinitionDAO.class);
        mockFormDao = createMock(IncomingMessageFormDAO.class);

        expect(
                applicationContext.getBean("incomingMessageFormDefinitionDAO", IncomingMessageFormDefinitionDAO.class)
                ).andReturn(mockFormDefDao);
        expect(
                mockFormDefDao.getByCode((String) anyObject())
                ).andReturn(new IncomingMessageFormDefinitionImpl());
        expect(
                applicationContext.getBean("incomingMessageForm", IncomingMessageForm.class)
                ).andReturn(new IncomingMessageFormImpl());
        expect(
                mockParser.getParams((String)anyObject())
                ).andReturn(new HashMap<String,IncomingMessageFormParameter>());
        expect(
                applicationContext.getBean("incomingMessageFormDAO", IncomingMessageFormDAO.class)
                ).andReturn(mockFormDao);
        expectLastCall();

        expect(
                mockFormDao.save((IncomingMessageForm) anyObject())
                ).andReturn(null);

        expectLastCall();

        replay(applicationContext,mockFormDefDao,mockParser,mockFormDao);
        IncomingMessageForm result = instance.initializeForm(message, formCode);
        verify(applicationContext,mockFormDefDao,mockParser,mockFormDao);

        assertNotNull(result);
    }

    /**
     * Test of prepareResponse method, of class FormCommandAction.
     */
    @Test
    public void testPrepareResponse() {
        System.out.println("prepareResponse");
        IncomingMessage message = new IncomingMessageImpl();

        mockRespDao = createMock(IncomingMessageResponseDAO.class);

        //Test for empty form
        expect(
                applicationContext.getBean("incomingMessageResponse", IncomingMessageResponse.class)
                ).andReturn(new IncomingMessageResponseImpl());

        replay(applicationContext);
        IncomingMessageResponse result = instance.prepareResponse(message, null);
        verify(applicationContext);

        String expResult ="Invalid request";
        assertNotNull(result);
        assertEquals(result.getContent(), expResult);

        //Test for valid form
        message.setIncomingMessageForm(new IncomingMessageFormImpl());
        message.getIncomingMessageForm().setMessageFormStatus(IncMessageFormStatus.SERVER_VALID);
        reset(applicationContext);

        expect(
                applicationContext.getBean("incomingMessageResponse", IncomingMessageResponse.class)
                ).andReturn(new IncomingMessageResponseImpl());
        expect(
                applicationContext.getBean("incomingMessageResponseDAO", IncomingMessageResponseDAO.class)
                ).andReturn(mockRespDao);
        expectLastCall();

        expect(
                mockRespDao.save((IncomingMessageResponse) anyObject())
                ).andReturn(null);

        expectLastCall();

        replay(applicationContext,mockRespDao);
        result = instance.prepareResponse(message, null);
        verify(applicationContext,mockRespDao);

        expResult = "Data saved successfully";
        assertNotNull(result);
        assertEquals(result.getContent(), expResult);

        //Test for locally invalid form
        IncomingMessageFormParameter param1 = new IncomingMessageFormParameterImpl();
        param1.setName("name");
        param1.setErrText("wrong format");
        param1.setMessageFormParamStatus(IncMessageFormParameterStatus.INVALID);

        IncomingMessageFormParameter param2 = new IncomingMessageFormParameterImpl();
        param2.setName("age");
        param2.setErrText("too long");
        param2.setMessageFormParamStatus(IncMessageFormParameterStatus.INVALID);

        message.getIncomingMessageForm().setMessageFormStatus(IncMessageFormStatus.INVALID);
        message.getIncomingMessageForm().setIncomingMsgFormParameters(new HashMap<String,IncomingMessageFormParameter>());
        message.getIncomingMessageForm().getIncomingMsgFormParameters().put(param1.getName(),param1);
        message.getIncomingMessageForm().getIncomingMsgFormParameters().put(param2.getName(),param2);

        reset(applicationContext,mockRespDao);

        expect(
                applicationContext.getBean("incomingMessageResponse", IncomingMessageResponse.class)
                ).andReturn(new IncomingMessageResponseImpl());
        expect(
                applicationContext.getBean("incomingMessageResponseDAO", IncomingMessageResponseDAO.class)
                ).andReturn(mockRespDao);
    
        expectLastCall();

        expect(
                mockRespDao.save((IncomingMessageResponse) anyObject())
                ).andReturn(null);

        expectLastCall();

        replay(applicationContext,mockRespDao);
        result = instance.prepareResponse(message, null);
        verify(applicationContext,mockRespDao);

        expResult = "Errors:\nage=too long\nname=wrong format";
        assertNotNull(result);
        assertEquals(result.getContent(), expResult);

        param2.setName("age");
        param2.setErrText("server error");
        param2.setMessageFormParamStatus(IncMessageFormParameterStatus.SERVER_INVALID);

        message.getIncomingMessageForm().setMessageFormStatus(IncMessageFormStatus.SERVER_INVALID);
        message.getIncomingMessageForm().setIncomingMsgFormParameters(new HashMap<String,IncomingMessageFormParameter>());
        message.getIncomingMessageForm().getIncomingMsgFormParameters().put(param2.getName(),param2);

        reset(applicationContext, mockRespDao);

        expect(
                applicationContext.getBean("incomingMessageResponse", IncomingMessageResponse.class)
                ).andReturn(new IncomingMessageResponseImpl());
        expect(
                applicationContext.getBean("incomingMessageResponseDAO", IncomingMessageResponseDAO.class)
                ).andReturn(mockRespDao);
    
        expectLastCall();

        expect(
                mockRespDao.save((IncomingMessageResponse) anyObject())
                ).andReturn(null);

        expectLastCall();

        replay(applicationContext, mockRespDao);
        result = instance.prepareResponse(message, null);
        verify(applicationContext, mockRespDao);

        expResult = "Errors:\nage=server error";
        assertNotNull(result);
        assertEquals(result.getContent(), expResult);
    }
}