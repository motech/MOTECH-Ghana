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

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.easymock.Capture;
import org.motechproject.server.model.ExpectedObs;
import org.motechproject.server.service.impl.ExpectedObsSchedule;
import org.motechproject.server.svc.RegistrarBean;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class IPTScheduleTest extends TestCase {

	ApplicationContext ctx;

	RegistrarBean registrarBean;
	ExpectedObsSchedule iptSchedule;
	ExpectedCareEvent ipt1Event;
	ExpectedCareEvent ipt2Event;
	ExpectedCareEvent ipt3Event;

	@Override
	protected void setUp() throws Exception {
		ctx = new ClassPathXmlApplicationContext(new String[] {
				"test-common-program-beans.xml",
				"services/pregnancy-ipt-sp-service.xml" });
		iptSchedule = (ExpectedObsSchedule) ctx.getBean("pregnancyIPTSchedule");
		ipt1Event = iptSchedule.getEvents().get(0);
		ipt2Event = iptSchedule.getEvents().get(1);
		ipt3Event = iptSchedule.getEvents().get(2);

		// EasyMock setup in Spring config
		registrarBean = (RegistrarBean) ctx.getBean("registrarBean");
	}

	@Override
	protected void tearDown() throws Exception {
		ctx = null;
		iptSchedule = null;
		ipt1Event = null;
		ipt2Event = null;
		ipt3Event = null;
		registrarBean = null;
	}

	public void testCreateExpected() {
		Date date = new Date();

		Patient patient = new Patient(1);

		Capture<Date> minDateCapture = new Capture<Date>();
		Capture<Date> dueDateCapture = new Capture<Date>();
		Capture<Date> lateDateCapture = new Capture<Date>();

		List<Obs> obsList = new ArrayList<Obs>();
		List<ExpectedObs> expectedObsList = new ArrayList<ExpectedObs>();

		Date pregnancyDate = new Date();

		expect(registrarBean.getActivePregnancyDueDate(patient.getPatientId()))
				.andReturn(pregnancyDate);
		expect(
				registrarBean.getObs(eq(patient), eq(iptSchedule
						.getConceptName()), eq(iptSchedule
						.getValueConceptName()), (Date) anyObject()))
				.andReturn(obsList);
		expect(registrarBean.getExpectedObs(patient, iptSchedule.getName()))
				.andReturn(expectedObsList);
		expect(
				registrarBean.createExpectedObs(eq(patient), eq(iptSchedule
						.getConceptName()), eq(iptSchedule
						.getValueConceptName()), eq(ipt1Event.getNumber()),
						capture(minDateCapture), capture(dueDateCapture),
						capture(lateDateCapture), (Date) anyObject(),
						eq(ipt1Event.getName()), eq(iptSchedule.getName())))
				.andReturn(new ExpectedObs());

		replay(registrarBean);

		iptSchedule.updateSchedule(patient, date);

		verify(registrarBean);

		Date minDate = minDateCapture.getValue();
		Date dueDate = dueDateCapture.getValue();
		Date lateDate = lateDateCapture.getValue();

		assertNotNull("Min date is null", minDate);
		assertNotNull("Due date is null", dueDate);
		assertNotNull("Late date is null", lateDate);

		assertTrue("Min date is not before due date", minDate.before(dueDate));
		assertTrue("Late date is not after due date", lateDate.after(dueDate));
	}

	public void testNoActionAlreadySatisfied() {
		Date date = new Date();

		Patient patient = new Patient(1);

		List<Obs> obsList = new ArrayList<Obs>();
		Obs obs1 = new Obs();
		obs1.setObsDatetime(date);
		obs1.setValueNumeric(new Double(1));
		obsList.add(obs1);
		Obs obs2 = new Obs();
		obs2.setObsDatetime(date);
		obs1.setValueNumeric(new Double(2));
		obsList.add(obs2);
		Obs obs3 = new Obs();
		obs3.setObsDatetime(date);
		obs1.setValueNumeric(new Double(3));
		obsList.add(obs3);

		List<ExpectedObs> expectedObsList = new ArrayList<ExpectedObs>();

		Date pregnancyDate = new Date();

		expect(registrarBean.getActivePregnancyDueDate(patient.getPatientId()))
				.andReturn(pregnancyDate);
		expect(
				registrarBean.getObs(eq(patient), eq(iptSchedule
						.getConceptName()), eq(iptSchedule
						.getValueConceptName()), (Date) anyObject()))
				.andReturn(obsList);
		expect(registrarBean.getExpectedObs(patient, iptSchedule.getName()))
				.andReturn(expectedObsList);

		replay(registrarBean);

		iptSchedule.updateSchedule(patient, date);

		verify(registrarBean);
	}

	public void testNoAction() {
		Date date = new Date();

		Patient patient = new Patient(1);

		List<ExpectedObs> expectedObsList = new ArrayList<ExpectedObs>();

		Date pregnancyDate = null;

		expect(registrarBean.getActivePregnancyDueDate(patient.getPatientId()))
				.andReturn(pregnancyDate);
		expect(registrarBean.getExpectedObs(patient, iptSchedule.getName()))
				.andReturn(expectedObsList);

		replay(registrarBean);

		iptSchedule.updateSchedule(patient, date);

		verify(registrarBean);
	}

}
