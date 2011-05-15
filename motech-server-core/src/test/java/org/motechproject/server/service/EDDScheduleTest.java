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

package org.motechproject.server.service;

import junit.framework.TestCase;
import org.easymock.Capture;
import org.motechproject.server.model.ExpectedEncounter;
import org.motechproject.server.service.impl.ExpectedEncounterSchedule;
import org.motechproject.server.svc.RegistrarBean;
import org.openmrs.Patient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.*;

import static org.easymock.EasyMock.*;

public class EDDScheduleTest extends TestCase {
	ApplicationContext ctx;

	RegistrarBean registrarBean;
	ExpectedEncounterSchedule eddSchedule;

	@Override
	protected void setUp() throws Exception {
		ctx = new ClassPathXmlApplicationContext(new String[] {
				"test-common-program-beans.xml",
				"services/pregnancy-edd-service.xml" });
		eddSchedule = (ExpectedEncounterSchedule) ctx.getBean("pregnancyEDDSchedule");

		// EasyMock setup in Spring config
		registrarBean = (RegistrarBean) ctx.getBean("registrarBean");
	}

	@Override
	protected void tearDown() throws Exception {
		ctx = null;
		eddSchedule = null;
		registrarBean = null;
	}

	public void testNotPregnant() {
		Date date = new Date();

		Patient patient = new Patient(1);

		expect(registrarBean.getActivePregnancyDueDate(patient.getPatientId())).andReturn(null);
		expect(registrarBean.getExpectedEncounters(patient, eddSchedule.getName())).andReturn(Collections.<ExpectedEncounter>emptyList());

		replay(registrarBean);

		eddSchedule.updateSchedule(patient, date);

		verify(registrarBean);
	}

    public void testPregnant() {
        Date date = new Date();

        Patient patient = new Patient(1);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 8);
        Date edd = calendar.getTime();

        Capture<Date> minDateCapture = new Capture<Date>();
        Capture<Date> dueDateCapture = new Capture<Date>();
        Capture<Date> lateDateCapture = new Capture<Date>();
        Capture<Date> maxDateCapture = new Capture<Date>();

        expect(registrarBean.getActivePregnancyDueDate(patient.getPatientId())).andReturn(edd);
        expect(registrarBean.getExpectedEncounters(patient, eddSchedule.getName())).andReturn(Collections.<ExpectedEncounter>emptyList());

        expect( registrarBean.createExpectedEncounter(eq(patient),
                        eq(eddSchedule.getEncounterTypeName()),
                        capture(minDateCapture), capture(dueDateCapture),
                        capture(lateDateCapture), capture(maxDateCapture),
                        eq(eddSchedule.getName()), eq(eddSchedule.getName())))
                .andReturn(new ExpectedEncounter());

        replay(registrarBean);

        eddSchedule.updateSchedule(patient, date);

        verify(registrarBean);

        calendar.add(Calendar.DATE, -7);
        Date minDate = calendar.getTime();

        calendar.add(Calendar.DATE, 14);
        Date lateDate = calendar.getTime();

        calendar.add(Calendar.DATE, 7);
        Date maxDate = calendar.getTime();

        Date capturedMinDate = minDateCapture.getValue();
        assertEquals(minDate, capturedMinDate);

        Date capturedDueDate = dueDateCapture.getValue();
        assertEquals(edd, capturedDueDate);

        Date capturedLateDate = lateDateCapture.getValue();
        assertEquals(lateDate, capturedLateDate);

        Date capturedMaxDate = maxDateCapture.getValue();
        assertEquals(maxDate, capturedMaxDate);
    }

    public void testPregnantEDDUpdated() {
        Date date = new Date();

        Patient patient = new Patient(1);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 8);
        Date edd = calendar.getTime();

        List<ExpectedEncounter> encList = new ArrayList<ExpectedEncounter>();
        encList.add(new ExpectedEncounter());

        Capture<ExpectedEncounter> capturedEncounter = new Capture<ExpectedEncounter>();

        expect(registrarBean.getActivePregnancyDueDate(patient.getPatientId())).andReturn(edd);
        expect(registrarBean.getExpectedEncounters(patient, eddSchedule.getName())).andReturn(encList);

        expect(registrarBean.saveExpectedEncounter(capture(capturedEncounter))).andReturn(new ExpectedEncounter()).times(1);

        replay(registrarBean);

        eddSchedule.updateSchedule(patient, date);

        verify(registrarBean);

        calendar.add(Calendar.DATE, -7);
        Date minDate = calendar.getTime();

        calendar.add(Calendar.DATE, 14);
        Date lateDate = calendar.getTime();

        calendar.add(Calendar.DATE, 7);
        Date maxDate = calendar.getTime();

        ExpectedEncounter enc = capturedEncounter.getValue();

        assertEquals(minDate, enc.getMinEncounterDatetime());
        assertEquals(edd, enc.getDueEncounterDatetime());
        assertEquals(lateDate, enc.getLateEncounterDatetime());
        assertEquals(maxDate, enc.getMaxEncounterDatetime());
    }

    public void testPregnantEDDUpdatedExpired() {
        Date date = new Date();

        Patient patient = new Patient(1);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -2);
        Date edd = calendar.getTime();

        List<ExpectedEncounter> encList = new ArrayList<ExpectedEncounter>();
        encList.add(new ExpectedEncounter());

        Capture<ExpectedEncounter> capturedEncounter = new Capture<ExpectedEncounter>();

        expect(registrarBean.getActivePregnancyDueDate(patient.getPatientId())).andReturn(edd);
        expect(registrarBean.getExpectedEncounters(patient, eddSchedule.getName())).andReturn(encList);

        expect(registrarBean.saveExpectedEncounter(capture(capturedEncounter))).andReturn(new ExpectedEncounter()).times(1);

        replay(registrarBean);

        eddSchedule.updateSchedule(patient, date);

        verify(registrarBean);

        ExpectedEncounter enc = capturedEncounter.getValue();

        assertTrue(enc.getVoided());
    }
}
