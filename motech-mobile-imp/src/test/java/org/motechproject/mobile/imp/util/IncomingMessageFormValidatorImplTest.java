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

import java.util.Date;
import java.util.ArrayList;
import org.motechproject.mobile.core.manager.CoreManager;
import org.motechproject.mobile.core.model.IncMessageFormStatus;
import org.motechproject.mobile.core.model.IncomingMessageForm;
import org.motechproject.mobile.core.model.IncomingMessageFormDefinition;
import org.motechproject.mobile.core.model.IncomingMessageFormDefinitionImpl;
import org.motechproject.mobile.core.model.IncomingMessageFormImpl;
import org.motechproject.mobile.core.model.IncomingMessageFormParameter;
import org.motechproject.mobile.core.model.IncomingMessageFormParameterDefinition;
import org.motechproject.mobile.core.model.IncomingMessageFormParameterDefinitionImpl;
import org.motechproject.mobile.core.model.IncomingMessageFormParameterImpl;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.mobile.core.model.IncMessageFormParameterStatus;
import org.motechproject.ws.server.RegistrarService;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

/**
 * Test for IncomingMessageFormValidatorImpl class
 *
 *  Date : Dec 6, 2009
 * @author Kofi A. Asamoah (yoofi@dreamoval.com)
 */
public class IncomingMessageFormValidatorImplTest {
    CoreManager mockCore;
    RegistrarService mockRegSvc;
    IncomingMessageFormParameterValidator mockParamValidator;
    Map<String, IncomingMessageFormParameterValidator> mockValidators;

    IncomingMessageFormValidatorImpl instance;

    public IncomingMessageFormValidatorImplTest() {
    }

    @Before
    public void setUp() {
        mockCore = createMock(CoreManager.class);
        mockRegSvc = createMock(RegistrarService.class);
        mockParamValidator = createMock(IncomingMessageFormParameterValidator.class);

        MethodSignature mSig = new MethodSignature();
        mSig.setMethodName("stopPregnancyProgram");
        mSig.setMethodParams(new HashMap<String, Class>(2));
        mSig.getMethodParams().put("chpsId", String.class);
        mSig.getMethodParams().put("motechId", String.class);

        Map<String, MethodSignature> signatures = new HashMap<String, MethodSignature>(1);
        signatures.put("PREGNANCYSTOP", mSig);

        //instance.setImParamValidator(mockParamValidator);
        LinkedHashMap<String, ValidatorGroup> validators = new LinkedHashMap<String, ValidatorGroup>();
        validators.put("ALPHANUM", new ValidatorGroup());
        validators.get("ALPHANUM").setValidators(new LinkedHashMap());

        instance = new IncomingMessageFormValidatorImpl();
        instance.setParamValidators(validators);
        instance.setCoreManager(mockCore);
        instance.setSubFields(new HashMap<String, List<SubField>>());
        instance.setCompositeRequirements(new HashMap<String, List<CompositeRequirementValidator>>());
    }

    /**
     * Test of validate method, of class IncomingMessageFormValidatorImpl.
     */
    @Test
    public void testValidate(){
        System.out.println("validate");

        String reqPhone = "000000000000";
        instance.setConditionalRequirements(new HashMap<String, List<SubField>>());
        instance.setConditionalValidator(new ConditionalRequirementValidator());

        IncomingMessageFormParameterDefinitionImpl pDef1 = new IncomingMessageFormParameterDefinitionImpl();
        pDef1.setRequired(true);
        pDef1.setParamType("ALPHANUM");
        pDef1.setLength(10);
        pDef1.setName("chpsId");

        IncomingMessageFormParameterDefinitionImpl pDef2 = new IncomingMessageFormParameterDefinitionImpl();
        pDef2.setRequired(true);
        pDef2.setParamType("ALPHANUM");
        pDef2.setLength(20);
        pDef2.setName("patientRegNum");

        IncomingMessageFormDefinition formDef = new IncomingMessageFormDefinitionImpl();
        formDef.setFormCode("NONE");
        formDef.setIncomingMsgParamDefinitions(new HashSet<IncomingMessageFormParameterDefinition>());
        formDef.getIncomingMsgParamDefinitions().add(pDef1);
        formDef.getIncomingMsgParamDefinitions().add(pDef2);

        IncomingMessageFormParameterImpl param1 = new IncomingMessageFormParameterImpl();
        param1.setName("chpsId");
        param1.setValue("testchps");

        IncomingMessageFormParameterImpl param2 = new IncomingMessageFormParameterImpl();
        param2.setName("patientRegNum");
        param2.setValue("testpatient");

        IncomingMessageForm form = new IncomingMessageFormImpl();
        form.setIncomingMsgFormParameters(new HashMap<String,IncomingMessageFormParameter>());
        form.setIncomingMsgFormDefinition(formDef);
        form.getIncomingMsgFormParameters().put(param1.getName().toLowerCase(),param1);
        
        //Test with required param missing
        IncMessageFormStatus expResult = IncMessageFormStatus.INVALID;

        expect(
                mockCore.createIncomingMessageFormParameter()
                ).andReturn(new IncomingMessageFormParameterImpl());


        replay(mockCore, mockParamValidator);
        IncMessageFormStatus result = instance.validate(form, reqPhone);
        verify(mockCore, mockParamValidator);
        
        assertEquals(expResult, result);
        assertEquals(param1.getIncomingMsgFormParamDefinition(), pDef1);
        assertTrue(form.getIncomingMsgFormParameters().size() == 2);
        assertEquals(form.getMessageFormStatus(), IncMessageFormStatus.INVALID);

        //Test with valid form on mobile
        form.setMessageFormStatus(IncMessageFormStatus.NEW);
        form.getIncomingMsgFormParameters().clear();
        form.getIncomingMsgFormParameters().put(param1.getName().toLowerCase(),param1);
        form.getIncomingMsgFormParameters().put(param2.getName().toLowerCase(),param2);

        expResult = IncMessageFormStatus.VALID;

        reset(mockCore, mockParamValidator);

        
        replay(mockCore, mockParamValidator);
        result = instance.validate(form, reqPhone);
        verify(mockCore, mockParamValidator);

        assertEquals(expResult, result);
        assertEquals(param2.getIncomingMsgFormParamDefinition(), pDef2);
        assertTrue(form.getIncomingMsgFormParameters().size() == 2);
        assertEquals(form.getMessageFormStatus(), IncMessageFormStatus.VALID);

        
        reset(mockCore, mockParamValidator);

        expResult = IncMessageFormStatus.INVALID;

        SubField sf = new SubField();
        sf.setFieldName("chpscompound");
        sf.setParentField("chpsId");
        sf.setReplaceOn("*");
        
//        IncomingMessageFormParameter param3 = new IncomingMessageFormParameterImpl();
//        param3.setDateCreated(new Date());
//        param3.setErrText("missing");
//        param3.setErrCode(0);
//        param3.setIncomingMsgForm(form);
//        param3.setMessageFormParamStatus(IncMessageFormParameterStatus.INVALID);
//        param3.setName("chpscompound");
        
        
        List<SubField> conditionals = new ArrayList<SubField>();
        conditionals.add(sf);
        
        expect(
                mockCore.createIncomingMessageFormParameter()
                ).andReturn(new IncomingMessageFormParameterImpl());

        instance.getConditionalRequirements().put(form.getIncomingMsgFormDefinition().getFormCode(), conditionals);

        replay(mockCore, mockParamValidator);
        result = instance.validate(form, reqPhone);
        verify(mockCore, mockParamValidator);

        assertEquals(expResult, result);
        assertEquals(param2.getIncomingMsgFormParamDefinition(), pDef2);
        assertTrue(form.getIncomingMsgFormParameters().size() == 3);
        assertEquals(form.getMessageFormStatus(), IncMessageFormStatus.INVALID);
    }
}