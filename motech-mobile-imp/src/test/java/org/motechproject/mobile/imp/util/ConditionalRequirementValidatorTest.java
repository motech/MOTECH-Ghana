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

import org.motechproject.mobile.core.model.IncMessageFormStatus;
import java.util.ArrayList;
import java.util.Date;
import org.motechproject.mobile.core.model.IncomingMessageFormParameter;
import java.util.HashMap;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.motechproject.mobile.core.manager.CoreManager;
import org.motechproject.mobile.core.model.IncomingMessageForm;
import org.motechproject.mobile.core.model.IncomingMessageFormImpl;
import org.motechproject.mobile.core.model.IncomingMessageFormParameterImpl;
import static org.easymock.EasyMock.*;

/**
 *
 * @author Yoofi
 */
public class ConditionalRequirementValidatorTest {
    CoreManager mockCore;
    ConditionalRequirementValidator instance;

    public ConditionalRequirementValidatorTest() {
    }

    @Before
    public void setUp() {
        mockCore = createMock(CoreManager.class);
        instance = new ConditionalRequirementValidator();
    }

    /**
     * Test of validate method, of class ConditionalRequirementValidator.
     */
    @Test
    public void testValidate() {
        System.out.println("validate");

        IncomingMessageFormParameter param1 = new IncomingMessageFormParameterImpl();
        param1.setDateCreated(new Date());
        param1.setName("parent");
        param1.setValue("value");

        IncomingMessageForm form = new IncomingMessageFormImpl();
        form.setIncomingMsgFormParameters(new HashMap<String, IncomingMessageFormParameter>());

        SubField sf = new SubField();
        sf.setFieldName("subfield");
        sf.setParentField("parent");
        sf.setReplaceOn("require");

        List<SubField> subFields = new ArrayList();
        subFields.add(sf);

        //Test with missing parent
        boolean expResult = true;
        boolean result = instance.validate(form, subFields, mockCore);
        assertEquals(expResult, result);

        
        //Test with parent having value other than required value
        form.getIncomingMsgFormParameters().put(param1.getName(), param1);

        expResult = true;
        result = instance.validate(form, subFields, mockCore);
        assertEquals(expResult, result);


        //Test with parent having required value
        param1.setValue("require");
        expResult = false;

        expect(
                mockCore.createIncomingMessageFormParameter()
                ).andReturn(new IncomingMessageFormParameterImpl());

        replay(mockCore);
        result = instance.validate(form, subFields, mockCore);
        verify(mockCore);

        assertEquals(result, expResult);
        assertEquals(form.getMessageFormStatus(), IncMessageFormStatus.INVALID);
    }

}