/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010-11 The Trustees of Columbia University in the City of
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

package org.motechproject.server.svc.impl;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.motechproject.server.omod.MotechService;
import org.motechproject.server.svc.MessageSourceBean;
import org.motechproject.server.svc.OpenmrsBean;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.server.util.DateUtil;
import org.motechproject.server.ws.RegistrarWebService;
import org.motechproject.ws.Gender;
import org.motechproject.ws.server.ValidationException;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;


public class RegistrarBeanRecordOutpatientVistsTest extends
        BaseModuleContextSensitiveTest {

    @Before
    public void setup() throws Exception {

        initializeInMemoryDatabase();

        executeDataSet("initial-openmrs-dataset.xml");
        executeDataSet("motech-dataset.xml");
        executeDataSet("facility-community-dataset.xml");

        authenticate();
    }


    @Test
    @SkipBaseSetup
    public void validateForDuplicateOPDVisitEntries() {
        MotechService motechService = Context.getService(MotechService.class);
        RegistrarBean registrarBean = motechService.getRegistrarBean();
        OpenmrsBean openmrsBean = Mockito.mock(RegistrarBeanImpl.class);

        int facilityId = 11117;
        Date vistDate = DateUtil.dateFor(15, 6, 2011);
        Date dob = new Date();

        User staff = registrarBean.registerStaff("Nurse", "Betty", "7777777777", "CHO", null);
        staff.setSystemId("465");

        when(openmrsBean.getStaffBySystemId("465")).thenReturn(staff);
        RegistrarWebService regService = new RegistrarWebService();
        regService.setRegistrarBean(registrarBean);
        regService.setMessageBean((MessageSourceBean)applicationContext.getBean("messageSourceBean"));
        regService.setOpenmrsBean(openmrsBean);

        Gender sex = Gender.MALE;
        boolean insured = false;
        Integer diagnosis = 1;
        Integer secondDiagnosis = 2;
        boolean rdtGiven = true;
        boolean rdtPositive = true;
        boolean actTreated = true;
        boolean newCase = true;
        boolean newPatient = true;
        boolean referred = true;
        String comments = "comments for patient entry 1";
        String serialNumber = "01/2011";

        try {
            regService.recordGeneralVisit(Integer.parseInt(staff.getSystemId()), facilityId, vistDate, serialNumber, sex, dob, insured,
                    diagnosis, secondDiagnosis, rdtGiven, rdtPositive, actTreated, newCase, newPatient, referred, comments);
        } catch (ValidationException e) {
            fail("Should not throw validation exception when registering a valid outpatient visit");
        }

        try {

            regService.recordGeneralVisit(Integer.parseInt(staff.getSystemId()), facilityId,  DateUtils.addDays(vistDate, 1), serialNumber, sex, dob, insured, diagnosis, secondDiagnosis, rdtGiven, rdtPositive, actTreated, newCase, newPatient, referred, comments);
            fail("should throw validation exception when a duplicate entry is registered");
        } catch (ValidationException e) {
            String errorMessage = e.getFaultInfo().getErrors().get(0);
            assertEquals("OPDVistEntryForm=in error", errorMessage);
        }

    }
}