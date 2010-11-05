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

import java.util.HashMap;
import org.motechproject.mobile.core.model.IncMessageFormParameterStatus;
import org.motechproject.mobile.core.model.IncomingMessageFormParameter;
import org.motechproject.mobile.core.model.IncomingMessageFormParameterDefinitionImpl;
import org.motechproject.mobile.core.model.IncomingMessageFormParameterImpl;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test for ParamExpressionValidator class
 *
 *  Date : Jan 14, 2010
 * @author Kofi A. Asamoah (yoofi@dreamoval.com)
 */
public class ParamExpressionValidatorTest {

    public ParamExpressionValidatorTest() {
    }

    /**
     * Test of validate method, of class ParamExpressionValidator.
     */
    @Test
    public void testValidate() {
        System.out.println("validate");

        IncomingMessageFormParameter param = new IncomingMessageFormParameterImpl();
        param.setValue("2010.12.10");
        param.setMessageFormParamStatus(IncMessageFormParameterStatus.NEW);
        param.setIncomingMsgFormParamDefinition(new IncomingMessageFormParameterDefinitionImpl());
        param.getIncomingMsgFormParamDefinition().setParamType("DATE");

        ParamExpressionValidator instance = new ParamExpressionValidator();
        instance.setExpression("(0[1-9]|[12][0-9]|3[01])[\\-/. ]?(0[1-9]|1[012])[\\-/. ]?(19|20)?\\d\\d");
        instance.setDateFormat("dd/MM/yyyy");
        DateFormatRegexMap rfrm = new DateFormatRegexMap();
        rfrm.setDateFormatRegexMap(new HashMap<String, String>());
        rfrm.getDateFormatRegexMap().put("(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/(19|20)?\\d\\d", "dd/MM/yy");
        rfrm.getDateFormatRegexMap().put("\\d+", "ddMMyy");
        rfrm.getDateFormatRegexMap().put("(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[012])-(19|20)?\\d\\d", "dd-MM-yy");
        rfrm.getDateFormatRegexMap().put("(0[1-9]|[12][0-9]|3[01]).(0[1-9]|1[012]).(19|20)?\\d\\d", "dd.MM.yy");
        rfrm.getDateFormatRegexMap().put("(0[1-9]|[12][0-9]|3[01]) (0[1-9]|1[012]) (19|20)?\\d\\d", "dd MM yy");
        rfrm.getDateFormatRegexMap().put("(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[012])-(19|20)\\d\\d (0[0-9]|1[0-2]|2[0-4]):([0-5][0-9]):([0-5][0-9]) [A|P]M$", "yyyy-MM-dd hh:mm:ss a");
        instance.setDateFormateExpMap(rfrm);
        instance.setDefaultDateFormat("yyyy-MM-dd hh:mm:ss a");

        boolean expResult = false;
        boolean result = instance.validate(param);
        assertEquals(expResult, result);
        assertEquals(param.getMessageFormParamStatus(), IncMessageFormParameterStatus.INVALID);
        assertEquals(param.getErrCode(), 1);
        assertEquals(param.getErrText(), "wrong format");

        param.setMessageFormParamStatus(IncMessageFormParameterStatus.NEW);
        param.setValue("10|02|1998");

        expResult = false;
        result = instance.validate(param);
        assertEquals(expResult, result);
        assertEquals(param.getMessageFormParamStatus(), IncMessageFormParameterStatus.INVALID);
        assertEquals(param.getErrCode(), 1);
        assertEquals(param.getErrText(), "wrong format");

        param.setMessageFormParamStatus(IncMessageFormParameterStatus.NEW);
        param.setValue("10.12.2010");

        expResult = false;
        result = instance.validate(param);
        assertEquals(expResult, result);
        assertEquals(param.getMessageFormParamStatus(), IncMessageFormParameterStatus.INVALID);

        param.setMessageFormParamStatus(IncMessageFormParameterStatus.NEW);
        param.setValue("10-01-10");

        expResult = true;
        result = instance.validate(param);
        assertEquals(expResult, result);
        assertEquals(param.getMessageFormParamStatus(), IncMessageFormParameterStatus.VALID);

        param.setMessageFormParamStatus(IncMessageFormParameterStatus.NEW);
        param.setValue("10012010");

        expResult = true;
        result = instance.validate(param);
        assertEquals(expResult, result);
        assertEquals(param.getMessageFormParamStatus(), IncMessageFormParameterStatus.VALID);

        param.setValue("2010-12-10 15:04:00");
        param.setMessageFormParamStatus(IncMessageFormParameterStatus.NEW);
        param.getIncomingMsgFormParamDefinition().setParamType("DATETIME");

        instance.setDateFormat("y-M-d H:m:s");

        expResult = true;
        result = instance.validate(param);
        assertEquals(expResult, result);
        assertEquals(param.getMessageFormParamStatus(), IncMessageFormParameterStatus.VALID);
    }

}