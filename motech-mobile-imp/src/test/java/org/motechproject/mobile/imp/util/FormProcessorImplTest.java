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
import java.util.HashMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mobile.core.manager.CoreManager;
import org.motechproject.mobile.core.model.IncMessageFormStatus;
import org.motechproject.mobile.core.model.IncomingMessageFormDefinitionImpl;
import org.motechproject.mobile.core.model.IncomingMessageFormImpl;
import org.motechproject.mobile.core.model.IncomingMessageFormParameter;
import org.motechproject.mobile.omi.manager.OMIManager;
import org.motechproject.ws.BirthOutcome;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.DayOfWeek;
import org.motechproject.ws.Gender;
import org.motechproject.ws.HIVResult;
import org.motechproject.ws.HowLearned;
import org.motechproject.ws.InterestReason;
import org.motechproject.ws.MediaType;
import org.motechproject.ws.RegistrantType;
import org.motechproject.ws.RegistrationMode;
import org.motechproject.ws.server.RegistrarService;
import org.motechproject.ws.server.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

/**
 *
 * @author user
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/imp-test-config.xml"})
public class FormProcessorImplTest {

    @Autowired
    FormProcessorImpl instance;
    
    OMIManager mockOMI;
    CoreManager mockCore;
    RegistrarService mockWS;

    public FormProcessorImplTest() {
    }

    @Before
    public void setUp() throws Exception {
        mockOMI = createMock(OMIManager.class);
        mockCore = createMock(CoreManager.class);
        mockWS = createMock(RegistrarService.class);
        instance.setRegWS(mockWS);
    }

    @Test
    public void testProcessForm() throws ValidationException{
        IncomingMessageFormImpl form = new IncomingMessageFormImpl();
        form.setIncomingMsgFormParameters(new HashMap<String, IncomingMessageFormParameter>());
        form.setIncomingMsgFormDefinition(new IncomingMessageFormDefinitionImpl());

        form.setMessageFormStatus(IncMessageFormStatus.VALID);
        form.getIncomingMsgFormDefinition().setFormCode("GENERALOPD-JF");

        //Test genetal visit
        mockWS.recordGeneralVisit((Integer) anyObject(), (Integer)anyObject(), (Date)anyObject(), (String)anyObject(), (Gender)anyObject(), (Date)anyObject(), (Boolean)anyObject(), (Integer)anyObject(), (Integer)anyObject(), (Boolean)anyObject(), (Boolean)anyObject(), (Boolean)anyObject(), (Boolean)anyObject(), (Boolean)anyObject(), (String)anyObject());
        expectLastCall();

        replay(mockWS);
        String result = instance.processForm(form);
        assertEquals(form.getMessageFormStatus(), IncMessageFormStatus.SERVER_VALID);
        verify(mockWS);

        reset(mockWS);

        //Test edit patient
        form.setMessageFormStatus(IncMessageFormStatus.VALID);
        form.getIncomingMsgFormDefinition().setFormCode("EDITPATIENT-JF");

        mockWS.editPatient((Integer) anyObject(), (Integer) anyObject(), (Date) anyObject(), (Integer) anyObject(), (String) anyObject(), (ContactNumberType) anyObject(), (String) anyObject(), (Date) anyObject(), (Boolean) anyObject());
        expectLastCall();

        replay(mockWS);
        result = instance.processForm(form);
        assertEquals(form.getMessageFormStatus(), IncMessageFormStatus.SERVER_VALID);
        verify(mockWS);

        reset(mockWS);

        //Test mother ANC visit
        form.setMessageFormStatus(IncMessageFormStatus.VALID);
        form.getIncomingMsgFormDefinition().setFormCode("ANC-JF");

        mockWS.recordMotherANCVisit((Integer) anyObject(), (Integer) anyObject(), (Date) anyObject(), (Integer) anyObject(), (Integer) anyObject(), (Integer) anyObject(), (String) anyObject(), (String) anyObject(), (Date) anyObject(), (Integer) anyObject(), (Integer) anyObject(), (Double) anyObject(), (Integer) anyObject(), (Integer) anyObject(), (Boolean) anyObject(), (Boolean) anyObject(), (Double) anyObject(), (Integer) anyObject(), (Integer) anyObject(), (Integer) anyObject(), (Double) anyObject(), (Boolean) anyObject(), (Boolean) anyObject(), (Boolean) anyObject(), (Boolean) anyObject(), (Boolean) anyObject(), (Boolean) anyObject(), (HIVResult) anyObject(), (Boolean) anyObject(), (Boolean) anyObject(), (Boolean) anyObject(), (Date) anyObject(), (String) anyObject());
        expectLastCall();

        replay(mockWS);
        result = instance.processForm(form);
        assertEquals(form.getMessageFormStatus(), IncMessageFormStatus.SERVER_VALID);
        verify(mockWS);

        reset(mockWS);

        //Test pregnancy termination
        form.setMessageFormStatus(IncMessageFormStatus.VALID);
        form.getIncomingMsgFormDefinition().setFormCode("ABORTION-JF");

        mockWS.recordPregnancyTermination((Integer) anyObject(), (Integer) anyObject(), (Date) anyObject(), (Integer) anyObject(), (Integer) anyObject(), (Integer) anyObject(), (Integer[]) anyObject(), (Boolean) anyObject(), (Boolean) anyObject(), (Boolean) anyObject(), (Boolean) anyObject(), (String) anyObject());
        expectLastCall();

        replay(mockWS);
        result = instance.processForm(form);
        assertEquals(form.getMessageFormStatus(), IncMessageFormStatus.SERVER_VALID);
        verify(mockWS);

        reset(mockWS);

        //Test delivery
        form.setMessageFormStatus(IncMessageFormStatus.VALID);
        form.getIncomingMsgFormDefinition().setFormCode("DELIVERY-JF");

        expect(
                mockWS.recordPregnancyDelivery((Integer) anyObject(), (Integer) anyObject(), (Date) anyObject(), (Integer) anyObject(), (Integer) anyObject(), (Integer) anyObject(), (Integer) anyObject(), (Integer) anyObject(), (Boolean) anyObject(), (Integer[]) anyObject(), (Integer) anyObject(), (Boolean) anyObject(), (String) anyObject(), (BirthOutcome) anyObject(), (RegistrationMode) anyObject(), (Integer) anyObject(), (Gender) anyObject(), (String) anyObject(), (Double) anyObject(), (BirthOutcome) anyObject(), (RegistrationMode) anyObject(), (Integer) anyObject(), (Gender) anyObject(), (String) anyObject(), (Double) anyObject(), (BirthOutcome) anyObject(), (RegistrationMode) anyObject(), (Integer) anyObject(), (Gender) anyObject(), (String) anyObject(), (Double) anyObject())
                ).andReturn(null);

        replay(mockWS);
        result = instance.processForm(form);
        assertEquals(form.getMessageFormStatus(), IncMessageFormStatus.SERVER_VALID);
        verify(mockWS);

        reset(mockWS);

        //Test delivery notification
        form.setMessageFormStatus(IncMessageFormStatus.VALID);
        form.getIncomingMsgFormDefinition().setFormCode("DELIVERYNOTIFY-JF");

        mockWS.recordDeliveryNotification((Integer) anyObject(), (Integer) anyObject(), (Date) anyObject(), (Integer) anyObject());
        expectLastCall();

        replay(mockWS);
        result = instance.processForm(form);
        assertEquals(form.getMessageFormStatus(), IncMessageFormStatus.SERVER_VALID);
        verify(mockWS);

        reset(mockWS);

        //Test mother PNC
        form.setMessageFormStatus(IncMessageFormStatus.VALID);
        form.getIncomingMsgFormDefinition().setFormCode("MOTHERPNC-JF");

        mockWS.recordMotherPNCVisit((Integer) anyObject(), (Integer) anyObject(), (Date) anyObject(), (Integer) anyObject(), (Integer) anyObject(), (Integer) anyObject(), (String) anyObject(), (String) anyObject(), (Boolean) anyObject(), (Boolean) anyObject(), (Boolean) anyObject(), (Integer) anyObject(), (Integer) anyObject(), (Boolean) anyObject(), (Boolean) anyObject(), (Double) anyObject(), (Double) anyObject(), (String) anyObject());
        expectLastCall();

        replay(mockWS);
        result = instance.processForm(form);
        assertEquals(form.getMessageFormStatus(), IncMessageFormStatus.SERVER_VALID);
        verify(mockWS);

        reset(mockWS);

        //Test death
        form.setMessageFormStatus(IncMessageFormStatus.VALID);
        form.getIncomingMsgFormDefinition().setFormCode("DEATH-JF");

        mockWS.recordDeath((Integer) anyObject(), (Integer) anyObject(), (Date) anyObject(), (Integer) anyObject());
        expectLastCall();

        replay(mockWS);
        result = instance.processForm(form);
        assertEquals(form.getMessageFormStatus(), IncMessageFormStatus.SERVER_VALID);
        verify(mockWS);

        reset(mockWS);

        //Test TT
        form.setMessageFormStatus(IncMessageFormStatus.VALID);
        form.getIncomingMsgFormDefinition().setFormCode("TT-JF");

        mockWS.recordTTVisit((Integer) anyObject(), (Integer) anyObject(), (Date) anyObject(), (Integer) anyObject(), (Integer) anyObject());
        expectLastCall();

        replay(mockWS);
        result = instance.processForm(form);
        assertEquals(form.getMessageFormStatus(), IncMessageFormStatus.SERVER_VALID);
        verify(mockWS);

        reset(mockWS);

        //Test child PNC
        form.setMessageFormStatus(IncMessageFormStatus.VALID);
        form.getIncomingMsgFormDefinition().setFormCode("CHILDPNC-JF");

        mockWS.recordChildPNCVisit((Integer) anyObject(), (Integer) anyObject(), (Date) anyObject(), (Integer) anyObject(), (Integer) anyObject(), (Integer) anyObject(), (String) anyObject(), (String) anyObject(), (Boolean) anyObject(), (Boolean) anyObject(), (Double) anyObject(), (Double) anyObject(), (Boolean) anyObject(), (Boolean) anyObject(), (Integer) anyObject(), (Boolean) anyObject(), (Boolean) anyObject(), (String) anyObject());
        expectLastCall();

        replay(mockWS);
        result = instance.processForm(form);
        assertEquals(form.getMessageFormStatus(), IncMessageFormStatus.SERVER_VALID);
        verify(mockWS);

        reset(mockWS);

        //Test register patient
        form.setMessageFormStatus(IncMessageFormStatus.VALID);
        form.getIncomingMsgFormDefinition().setFormCode("REGISTERPATIENT-JF");

        expect(
                mockWS.registerPatient((Integer) anyObject(), (Integer) anyObject(), (Date) anyObject(), (RegistrationMode) anyObject(), (Integer) anyObject(), (RegistrantType) anyObject(), (String) anyObject(), (String) anyObject(), (String) anyObject(), (String) anyObject(), (Date) anyObject(), (Boolean) anyObject(), (Gender) anyObject(), (Boolean) anyObject(), (String) anyObject(), (Date) anyObject(), (Integer) anyObject(), (Integer) anyObject(), (String) anyObject(), (String) anyObject(), (Date) anyObject(), (Boolean) anyObject(), (Boolean) anyObject(), (Boolean) anyObject(), (ContactNumberType) anyObject(), (MediaType) anyObject(), (String) anyObject(), (DayOfWeek) anyObject(), (Date) anyObject(), (InterestReason) anyObject(), (HowLearned) anyObject(), (Integer) anyObject())
                ).andReturn(null);

        replay(mockWS);
        result = instance.processForm(form);
        assertEquals(form.getMessageFormStatus(), IncMessageFormStatus.SERVER_VALID);
        verify(mockWS);

        reset(mockWS);

        //Test register pregnancy
        form.setMessageFormStatus(IncMessageFormStatus.VALID);
        form.getIncomingMsgFormDefinition().setFormCode("REGISTERPREGNANCY-JF");

        mockWS.registerPregnancy((Integer) anyObject(), (Integer) anyObject(), (Date) anyObject(), (Integer) anyObject(), (Date) anyObject(), (Boolean) anyObject(), (Boolean) anyObject(), (ContactNumberType) anyObject(), (String) anyObject(), (MediaType) anyObject(), (String) anyObject(), (DayOfWeek) anyObject(), (Date) anyObject(), (HowLearned) anyObject());
        expectLastCall();

        replay(mockWS);
        result = instance.processForm(form);
        assertEquals(form.getMessageFormStatus(), IncMessageFormStatus.SERVER_VALID);
        verify(mockWS);

        reset(mockWS);

        //Test register ANC mother
        form.setMessageFormStatus(IncMessageFormStatus.VALID);
        form.getIncomingMsgFormDefinition().setFormCode("REGISTERANCMOTHER-JF");

        mockWS.registerANCMother((Integer) anyObject(), (Integer) anyObject(), (Date) anyObject(), (Integer) anyObject(), (String) anyObject(), (Date) anyObject(), (Double) anyObject(), (Integer) anyObject(), (Integer) anyObject(), (Boolean) anyObject(), (Boolean) anyObject(), (ContactNumberType) anyObject(), (String) anyObject(), (MediaType) anyObject(), (String) anyObject(), (DayOfWeek) anyObject(), (Date) anyObject(), (HowLearned) anyObject());
        expectLastCall();

        replay(mockWS);
        result = instance.processForm(form);
        assertEquals(form.getMessageFormStatus(), IncMessageFormStatus.SERVER_VALID);
        verify(mockWS);

        reset(mockWS);

        //Test register CWC child
        form.setMessageFormStatus(IncMessageFormStatus.VALID);
        form.getIncomingMsgFormDefinition().setFormCode("REGISTERCWCCHILD-JF");

        mockWS.registerCWCChild((Integer) anyObject(), (Integer) anyObject(), (Date) anyObject(), (Integer) anyObject(), (String) anyObject(), (Boolean) anyObject(), (Boolean) anyObject(), (ContactNumberType) anyObject(), (String) anyObject(), (MediaType) anyObject(), (String) anyObject(), (DayOfWeek) anyObject(), (Date) anyObject(), (HowLearned) anyObject());
        expectLastCall();

        replay(mockWS);
        result = instance.processForm(form);
        assertEquals(form.getMessageFormStatus(), IncMessageFormStatus.SERVER_VALID);
        verify(mockWS);

        reset(mockWS);

        //Test child OPD
        form.setMessageFormStatus(IncMessageFormStatus.VALID);
        form.getIncomingMsgFormDefinition().setFormCode("CHILDOPD-JF");

        mockWS.recordChildVisit((Integer) anyObject(), (Integer) anyObject(), (Date) anyObject(), (String) anyObject(), (Integer) anyObject(), (Boolean) anyObject(), (Integer) anyObject(), (Integer) anyObject(), (Boolean) anyObject(), (Boolean) anyObject(), (Boolean) anyObject(), (Boolean) anyObject(), (Boolean) anyObject(), (String) anyObject());
        expectLastCall();

        replay(mockWS);
        result = instance.processForm(form);
        assertEquals(form.getMessageFormStatus(), IncMessageFormStatus.SERVER_VALID);
        verify(mockWS);

        reset(mockWS);

        //Test mother OPD
        form.setMessageFormStatus(IncMessageFormStatus.VALID);
        form.getIncomingMsgFormDefinition().setFormCode("MOTHEROPD-JF");

        mockWS.recordMotherVisit((Integer) anyObject(), (Integer) anyObject(), (Date) anyObject(), (String) anyObject(), (Integer) anyObject(), (Boolean) anyObject(), (Integer) anyObject(), (Integer) anyObject(), (Boolean) anyObject(), (Boolean) anyObject(), (Boolean) anyObject(), (Boolean) anyObject(), (Boolean) anyObject(), (String) anyObject());
        expectLastCall();

        replay(mockWS);
        result = instance.processForm(form);
        assertEquals(form.getMessageFormStatus(), IncMessageFormStatus.SERVER_VALID);
        verify(mockWS);

        reset(mockWS);

        //Test ANC defaulters
        form.setMessageFormStatus(IncMessageFormStatus.VALID);
        form.getIncomingMsgFormDefinition().setFormCode("ANCDEFAULT-JF");

        expect(
                mockWS.queryANCDefaulters((Integer) anyObject(), (Integer) anyObject())
                ).andReturn(null);

        replay(mockWS);
        result = instance.processForm(form);
        assertEquals(form.getMessageFormStatus(), IncMessageFormStatus.SERVER_VALID);
        verify(mockWS);

        reset(mockWS);

        //Test TT defaulters
        form.setMessageFormStatus(IncMessageFormStatus.VALID);
        form.getIncomingMsgFormDefinition().setFormCode("TTDEFAULT-JF");

        expect(
                mockWS.queryTTDefaulters((Integer) anyObject(), (Integer) anyObject())
                ).andReturn(null);

        replay(mockWS);
        result = instance.processForm(form);
        assertEquals(form.getMessageFormStatus(), IncMessageFormStatus.SERVER_VALID);
        verify(mockWS);

        reset(mockWS);

        //Test Mother PNC defaulters
        form.setMessageFormStatus(IncMessageFormStatus.VALID);
        form.getIncomingMsgFormDefinition().setFormCode("PPCDEFAULT-JF");

        expect(
                mockWS.queryMotherPNCDefaulters((Integer) anyObject(), (Integer) anyObject())
                ).andReturn(null);

        replay(mockWS);
        result = instance.processForm(form);
        assertEquals(form.getMessageFormStatus(), IncMessageFormStatus.SERVER_VALID);
        verify(mockWS);

        reset(mockWS);

        //Test Child PNC defaulters
        form.setMessageFormStatus(IncMessageFormStatus.VALID);
        form.getIncomingMsgFormDefinition().setFormCode("PNCDEFAULT-JF");

        expect(
                mockWS.queryChildPNCDefaulters((Integer) anyObject(), (Integer) anyObject())
                ).andReturn(null);

        replay(mockWS);
        result = instance.processForm(form);
        assertEquals(form.getMessageFormStatus(), IncMessageFormStatus.SERVER_VALID);
        verify(mockWS);

        reset(mockWS);

        //Test CWC defaulters
        form.setMessageFormStatus(IncMessageFormStatus.VALID);
        form.getIncomingMsgFormDefinition().setFormCode("CWCDEFAULT-JF");

        expect(
                mockWS.queryCWCDefaulters((Integer) anyObject(), (Integer) anyObject())
                ).andReturn(null);

        replay(mockWS);
        result = instance.processForm(form);
        assertEquals(form.getMessageFormStatus(), IncMessageFormStatus.SERVER_VALID);
        verify(mockWS);

        reset(mockWS);

        //Test Upcoming Deliveries
        form.setMessageFormStatus(IncMessageFormStatus.VALID);
        form.getIncomingMsgFormDefinition().setFormCode("UPCOMINGDELIVERIES-JF");

        expect(
                mockWS.queryUpcomingDeliveries((Integer) anyObject(), (Integer) anyObject())
                ).andReturn(null);

        replay(mockWS);
        result = instance.processForm(form);
        assertEquals(form.getMessageFormStatus(), IncMessageFormStatus.SERVER_VALID);
        verify(mockWS);

        reset(mockWS);

        //Test Recent Deliveries
        form.setMessageFormStatus(IncMessageFormStatus.VALID);
        form.getIncomingMsgFormDefinition().setFormCode("RECENTDELIVERIES-JF");

        expect(
                mockWS.queryRecentDeliveries((Integer) anyObject(), (Integer) anyObject())
                ).andReturn(null);

        replay(mockWS);
        result = instance.processForm(form);
        assertEquals(form.getMessageFormStatus(), IncMessageFormStatus.SERVER_VALID);
        verify(mockWS);

        reset(mockWS);

        //Test Overdue Deliveries
        form.setMessageFormStatus(IncMessageFormStatus.VALID);
        form.getIncomingMsgFormDefinition().setFormCode("OVERDUEDELIVERIES-JF");

        expect(
                mockWS.queryOverdueDeliveries((Integer) anyObject(), (Integer) anyObject())
                ).andReturn(null);

        replay(mockWS);
        result = instance.processForm(form);
        assertEquals(form.getMessageFormStatus(), IncMessageFormStatus.SERVER_VALID);
        verify(mockWS);

        reset(mockWS);

        //Test Upcoming Care
        form.setMessageFormStatus(IncMessageFormStatus.VALID);
        form.getIncomingMsgFormDefinition().setFormCode("UPCOMINGCARE-JF");

        expect(
                mockWS.queryUpcomingCare((Integer) anyObject(), (Integer) anyObject(), (Integer) anyObject())
                ).andReturn(null);

        replay(mockWS);
        result = instance.processForm(form);
        assertEquals(form.getMessageFormStatus(), IncMessageFormStatus.SERVER_VALID);
        verify(mockWS);

        reset(mockWS);

        //Test view patient details
        form.setMessageFormStatus(IncMessageFormStatus.VALID);
        form.getIncomingMsgFormDefinition().setFormCode("VIEWPATIENT-JF");

        expect(
                mockWS.queryPatient((Integer) anyObject(), (Integer) anyObject(), (Integer) anyObject())
                ).andReturn(null);

        replay(mockWS);
        result = instance.processForm(form);
        assertEquals(form.getMessageFormStatus(), IncMessageFormStatus.SERVER_VALID);
        verify(mockWS);

        reset(mockWS);

        //Test find MoTeCHID
        form.setMessageFormStatus(IncMessageFormStatus.VALID);
        form.getIncomingMsgFormDefinition().setFormCode("FINDMOTECHID-JF");

        expect(
                mockWS.queryMotechId((Integer) anyObject(), (Integer) anyObject(), (String) anyObject(), (String) anyObject(), (String) anyObject(), (Date) anyObject(), (String) anyObject(), (String) anyObject())
                ).andReturn(null);

        replay(mockWS);
        result = instance.processForm(form);
        assertEquals(form.getMessageFormStatus(), IncMessageFormStatus.SERVER_VALID);
        verify(mockWS);

        reset(mockWS);
    }

    @Test
    public void testParseValidationErrors() {
    }
}