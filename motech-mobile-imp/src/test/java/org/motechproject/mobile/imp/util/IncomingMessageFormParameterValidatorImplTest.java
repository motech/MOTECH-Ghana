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


import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

/**
 * Test for IncomingMessageFormParameterValidatorImpl class
 *
 *  Date : Dec 6, 2009
 * @author Kofi A. Asamoah (yoofi@dreamoval.com)
 */
//@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/imp-test-config.xml"})
public class IncomingMessageFormParameterValidatorImplTest {
    //   @Autowired
    IncomingMessageFormParameterValidator imParamValidator;

    public IncomingMessageFormParameterValidatorImplTest() {
    }

    /**
     * Test of validate method, of class IncomingMessageFormParameterValidatorImpl.
     */
    @Ignore
    @Test
    public void testValidate() {
        System.out.println("validate");
//        IncomingMessageFormParameter param = new IncomingMessageFormParameterImpl();
//        param.setMessageFormParamStatus(IncMessageFormParameterStatus.NEW);
//        param.setIncomingMsgFormParamDefinition(new IncomingMessageFormParameterDefinitionImpl());
//        param.getIncomingMsgFormParamDefinition().setParamType("ALPHA");
//        param.getIncomingMsgFormParamDefinition().setLength(30);
//        param.setName("name");
//        param.setValue("O'Test,Dream-Tester Test");
//
//        boolean expResult = true;
//        boolean result = imParamValidator.validate(param);
//
//        assertEquals(result, expResult);
//        assertEquals(param.getMessageFormParamStatus(), IncMessageFormParameterStatus.VALID);
//
//        param.setMessageFormParamStatus(IncMessageFormParameterStatus.NEW);
//        param.getIncomingMsgFormParamDefinition().setParamType("ALPHA");
//        param.getIncomingMsgFormParamDefinition().setLength(10);
//        param.setName("name");
//        param.setValue("tester1234");
//
//        result = imParamValidator.validate(param);
//        assertFalse(result);
//        assertEquals(param.getMessageFormParamStatus(), IncMessageFormParameterStatus.INVALID);
//        assertEquals(param.getErrCode(), 1);
//        assertEquals(param.getErrText(), "name=wrong format");
//
//        param.setMessageFormParamStatus(IncMessageFormParameterStatus.NEW);
//        param.getIncomingMsgFormParamDefinition().setParamType("DATE");
//        param.getIncomingMsgFormParamDefinition().setLength(10);
//        param.setName("date");
//        param.setValue("06.12.2009");
//
//        result = imParamValidator.validate(param);
//        assertTrue(result);
//        assertEquals(param.getMessageFormParamStatus(), IncMessageFormParameterStatus.VALID);
//
//        param.setMessageFormParamStatus(IncMessageFormParameterStatus.NEW);
//        param.getIncomingMsgFormParamDefinition().setParamType("DATE");
//        param.getIncomingMsgFormParamDefinition().setLength(10);
//        param.setValue("06-12-2009");
//
//        expResult = false;
//        result = imParamValidator.validate(param);
//        assertEquals(result, expResult);
//        assertEquals(param.getMessageFormParamStatus(), IncMessageFormParameterStatus.INVALID);
//        assertEquals(param.getErrCode(), 1);
//        assertEquals(param.getErrText(), "date=wrong format");
//
//        param.setMessageFormParamStatus(IncMessageFormParameterStatus.NEW);
//        param.getIncomingMsgFormParamDefinition().setParamType("NUMERIC");
//        param.getIncomingMsgFormParamDefinition().setLength(10);
//        param.setName("age");
//        param.setValue("06-2009-12");
//
//        expResult = false;
//        result = imParamValidator.validate(param);
//        assertEquals(result, expResult);
//        assertEquals(param.getMessageFormParamStatus(), IncMessageFormParameterStatus.INVALID);
//        assertEquals(param.getErrCode(), 1);
//        assertEquals(param.getErrText(), "age=wrong format");
//
//        param.setMessageFormParamStatus(IncMessageFormParameterStatus.NEW);
//        param.getIncomingMsgFormParamDefinition().setParamType("NUMERIC");
//        param.getIncomingMsgFormParamDefinition().setLength(3);
//        param.setName("age");
//        param.setValue("2009");
//
//        expResult = false;
//        result = imParamValidator.validate(param);
//        assertEquals(result, expResult);
//        assertEquals(param.getMessageFormParamStatus(), IncMessageFormParameterStatus.INVALID);
//        assertEquals(param.getErrCode(), 2);
//        assertEquals(param.getErrText(), "age=too long");
//
//        param.setMessageFormParamStatus(IncMessageFormParameterStatus.NEW);
//        param.getIncomingMsgFormParamDefinition().setParamType("NUMERIC");
//        param.getIncomingMsgFormParamDefinition().setLength(10);
//        param.setName("age");
//        param.setValue("30");
//
//        expResult = true;
//        result = imParamValidator.validate(param);
//        assertEquals(result, expResult);
//        assertEquals(param.getMessageFormParamStatus(), IncMessageFormParameterStatus.VALID);
//
//        param.setMessageFormParamStatus(IncMessageFormParameterStatus.NEW);
//        param.getIncomingMsgFormParamDefinition().setParamType("BOOLEAN");
//        param.getIncomingMsgFormParamDefinition().setLength(5);
//        param.setName("registered");
//        param.setValue("yup");
//
//        expResult = false;
//        result = imParamValidator.validate(param);
//        assertEquals(result, expResult);
//        assertEquals(param.getMessageFormParamStatus(), IncMessageFormParameterStatus.INVALID);
//        assertEquals(param.getErrCode(), 1);
//        assertEquals(param.getErrText(), "registered=wrong format");
//
//        param.setMessageFormParamStatus(IncMessageFormParameterStatus.NEW);
//        param.getIncomingMsgFormParamDefinition().setParamType("BOOLEAN");
//        param.getIncomingMsgFormParamDefinition().setLength(5);
//        param.setName("registered");
//        param.setValue("n");
//
//        expResult = true;
//        result = imParamValidator.validate(param);
//        assertEquals(result, expResult);
//        assertEquals(param.getMessageFormParamStatus(), IncMessageFormParameterStatus.VALID);
    }
}