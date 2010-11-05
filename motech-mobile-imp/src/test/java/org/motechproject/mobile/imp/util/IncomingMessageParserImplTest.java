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

package org.motechproject.mobile.imp.util;

import org.motechproject.mobile.core.manager.CoreManager;
import org.motechproject.mobile.core.model.IncomingMessage;
import org.motechproject.mobile.core.model.IncomingMessageFormParameter;
import org.motechproject.mobile.core.model.IncomingMessageFormParameterImpl;
import org.motechproject.mobile.core.model.IncomingMessageImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

/**
 * Test for IncomingMessageParserImpl class
 *
 *  Date : Dec 5, 2009
 * @author Kofi A. Asamoah (yoofi@dreamoval.com)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/imp-test-config.xml"})
public class IncomingMessageParserImplTest {
    CoreManager mockCore;

    @Autowired
    IncomingMessageParserImpl imParser;
    
    public IncomingMessageParserImplTest() {
    }

    @Before
    public void setup(){
        mockCore = createMock(CoreManager.class);
        imParser.setCoreManager(mockCore);
    }

    /**
     * Test of parseRequest method, of class IncomingMessageParserImpl.
     */
    @Test
    public void testParseRequest() {
        System.out.println("parseRequest");
        String message = "Type=GENERAL\naction=test\nmessage=Test,, Tester";

        expect(
                mockCore.createIncomingMessage()
                ).andReturn(new IncomingMessageImpl());

        replay(mockCore);
        IncomingMessage result = imParser.parseRequest(message);
        verify(mockCore);

        assertNotNull(result);
        assertEquals(message, result.getContent());
    }

    /**
     * Test of getComand method, of class IncomingMessageParserImpl.
     */
    @Test
    public void testGetComand() {
        System.out.println("getComand");
        String message = "Type=GENERAL\naction=test\nmessage=Test,, Tester";
        
        String expResult = "type";
        String result = imParser.getCommand(message);
        assertEquals(expResult, result);

        message = "someting=test Type";
        expResult = "";
        result = imParser.getCommand(message);
        assertEquals(expResult, result);
    }

    /**
     * Test of getFormCode method, of class IncomingMessageParserImpl.
     */
    @Test
    public void testGetFormCode() {
        System.out.println("getFormCode");
        String message = "Type=GENERAL\naction=test\nmessage=Test,, Tester";
        String expResult = "GENERAL";
        String result = imParser.getFormCode(message);
        assertEquals(expResult, result);
    }

    /**
     * Test of getParams method, of class IncomingMessageParserImpl.
     */
    @Test
    public void testGetParams() {
        System.out.println("getParams");
        String message = "Type=GENERAL\naction=test\nmessage=Test, Dream Tester\ndob = 01.01.01\ndue-date=right. now\ntest_format=all";

        List<IncomingMessageFormParameter> expResult = new ArrayList<IncomingMessageFormParameter>();

        IncomingMessageFormParameter param1 = new IncomingMessageFormParameterImpl();
        param1.setName("action");
        param1.setValue("test");
        expResult.add(param1);

        IncomingMessageFormParameter param2 = new IncomingMessageFormParameterImpl();
        param1.setName("message");
        param1.setValue("Test, Dream Tester");
        expResult.add(param2);

        IncomingMessageFormParameter param3 = new IncomingMessageFormParameterImpl();
        param1.setName("dob");
        param1.setValue("01.01.01");
        expResult.add(param3);

        IncomingMessageFormParameter param4 = new IncomingMessageFormParameterImpl();
        param1.setName("due-date");
        param1.setValue("right. now");
        expResult.add(param4);

        IncomingMessageFormParameter param5 = new IncomingMessageFormParameterImpl();
        param1.setName("test_format");
        param1.setValue("all");
        expResult.add(param5);

        expect(
                mockCore.createIncomingMessageFormParameter()
                ).andReturn(new IncomingMessageFormParameterImpl()).times(expResult.size());

        replay(mockCore);
        Map<String,IncomingMessageFormParameter> result = imParser.getParams(message);
        verify(mockCore);
        
        assertNotNull(result);
        assertEquals(result.size(), expResult.size());
    }

}