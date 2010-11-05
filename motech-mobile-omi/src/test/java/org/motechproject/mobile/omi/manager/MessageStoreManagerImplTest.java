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


import org.junit.runner.RunWith;
import org.motechproject.mobile.core.manager.CoreManager;
import org.motechproject.mobile.core.model.*;
import org.motechproject.mobile.core.dao.MessageTemplateDAO;

import java.util.HashSet;
import java.util.Set;
import static org.easymock.EasyMock.*;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ws.NameValuePair;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

/**
 * Unit test for the MessageStoreManagerImpl class
 *
 * @author Kofi A. Asamoah (yoofi@dreamoval.com)
 * Date Created Aug 10, 2009
 */
public class MessageStoreManagerImplTest {

    CoreManager mockCore;
    MessageTemplateDAO mockTemplateDao;
    MessageStoreManagerImpl instance;
    Language mockLang;
    MessageTemplate template;
    ApplicationContext applicationContext;

    public MessageStoreManagerImplTest() {
    }

    @Before
    public void setUp(){
        mockCore = createMock(CoreManager.class);
        mockLang = createMock(Language.class);
        applicationContext = createMock(ApplicationContext.class);
        mockLang.setId(16000000001l);
        mockLang.setCode("testing");
        mockTemplateDao = createMock(MessageTemplateDAO.class);

        instance = new MessageStoreManagerImpl();
        instance.setApplicationContext(applicationContext);
        instance.setCoreManager(mockCore);
        instance.setCharsPerSMS(160);
        instance.setConcatAllowance(7);
        instance.setMaxConcat(3);
               
        template = new MessageTemplateImpl();
        template.setTemplate("testing");
    }

    /**
     * Test of constructMessage method, of class MessageStoreManagerImpl.
     */
    @Test
    public void testConstructMessage() {
        System.out.println("consrtuctMessage");
        
        MessageRequest message = new MessageRequestImpl();
        message.setMessageType(MessageType.TEXT);
        Language defaultLang = new LanguageImpl();

        message.setPersInfos(new HashSet<NameValuePair>());
        message.setNotificationType(new NotificationTypeImpl());

        expect(
                mockCore.createMessageTemplateDAO()
                ).andReturn(mockTemplateDao);
        expect(
                mockTemplateDao.getTemplateByLangNotifMType((Language)anyObject(), (NotificationType) anyObject(), (MessageType) anyObject(), (Language) anyObject())
                ).andReturn(template);
        expect(
                applicationContext.getBean("gatewayRequest", GatewayRequest.class)
                ).andReturn(new GatewayRequestImpl());
        expect(
                applicationContext.getBean("gatewayRequestDetails", GatewayRequestDetails.class)
                ).andReturn(new GatewayRequestDetailsImpl());

        replay(mockCore, mockTemplateDao, applicationContext);

        System.out.println("111111" + instance);
        GatewayRequest result = instance.constructMessage(message, defaultLang);
        assertNotNull(result);
        verify(mockCore, mockTemplateDao, applicationContext);
    }

    /**
     * Test of parseTemplate method, of class MessageStoreManagerImpl.
     */
    @Test
    public void testParseTemplate() {
        System.out.println("parseTemplate");
        
        String tpl = "Testing the <method> method of the <class> class";
        
        Set<NameValuePair> params = new HashSet<NameValuePair>();
        params.add(new NameValuePair("method", "parseTemplate"));
        params.add(new NameValuePair("class", "MessageStoreManagerImpl"));
        
        String expResult = "Testing the parseTemplate method of the MessageStoreManagerImpl class";
        
        String result = instance.parseTemplate(tpl, params);
        assertEquals(expResult, result);
    }

    /**
     * Test of parseTemplate method, of class MessageStoreManagerImpl.
     */
    @Test
    public void testParseTemplate_MissingParam() {
        System.out.println("parseTemplate");
        
        String tpl = "Testing the <method> method of the <class> class";
        
        Set<NameValuePair> params = new HashSet<NameValuePair>();
        params.add(new NameValuePair("method", "parseTemplate"));
        
        String expResult = "Testing the parseTemplate method of the <class> class";
        
        String result = instance.parseTemplate(tpl, params);
        assertEquals(expResult, result);
    }

    /**
     * Test of parseTemplate method, of class MessageStoreManagerImpl.
     */
    @Test
    public void testParseTemplate_NULLParam() {
        System.out.println("parseTemplate");
        
        String tpl = "Testing the <method> method of the <class> class";
        
        String result = instance.parseTemplate(tpl, null);
        assertEquals(tpl, result);
    }
    
    /**
     * Test of constructMessage method, of class MessageStoreManagerImpl.
     */
    @Test
    public void testFetchTemplate() {
        System.out.println("fetchTemplate");
        
        mockTemplateDao = createMock(MessageTemplateDAO.class);
        
        MessageRequest message = new MessageRequestImpl();
        Language defaultLang = new LanguageImpl();

        message.setNotificationType(new NotificationTypeImpl());
        
        expect(
                mockCore.createMessageTemplateDAO()
                ).andReturn(mockTemplateDao);
        expect(
                mockTemplateDao.getTemplateByLangNotifMType((Language)anyObject(), (NotificationType) anyObject(), (MessageType) anyObject(), (Language) anyObject())
                ).andReturn(template);
        
        replay(mockCore, mockTemplateDao);

        String result = instance.fetchTemplate(message, defaultLang);
        assertEquals(result, "testing");
        verify(mockCore, mockTemplateDao);
    }
    
    @Test
    public void testFormatPhone() {
    	
    	((MessageStoreManagerImpl)instance).setLocalNumberExpression("0[0-9]{9}");
    	((MessageStoreManagerImpl)instance).setDefaultCountryCode("233");
    	
    	String phone = "0123456789";
    	
    	String expectedTextPhone = "233123456789";
    	String actualTextPhone = ((MessageStoreManagerImpl)instance).formatPhoneNumber(phone, MessageType.TEXT);
    	assertEquals(expectedTextPhone, actualTextPhone);
    	
    	String expectedVoicePhone = "123456789";
    	String actualVoicePhone = ((MessageStoreManagerImpl)instance).formatPhoneNumber(phone, MessageType.VOICE);
    	assertEquals(expectedVoicePhone, actualVoicePhone);
    	
    }
    
}