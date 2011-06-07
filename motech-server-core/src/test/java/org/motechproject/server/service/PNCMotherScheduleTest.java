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
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.easymock.EasyMock.*;

public class PNCMotherScheduleTest extends TestCase {
	ApplicationContext ctx;

	RegistrarBean registrarBean;
	ExpectedEncounterSchedule pncSchedule;
	ExpectedCareEvent pnc1Event;
	ExpectedCareEvent pnc2Event;
	ExpectedCareEvent pnc3Event;

	@Override
	protected void setUp() throws Exception {
		ctx = new ClassPathXmlApplicationContext(new String[] {
				"test-common-program-beans.xml",
				"services/pregnancy-pnc-service.xml" });
		pncSchedule = (ExpectedEncounterSchedule) ctx
				.getBean("pregnancyPNCSchedule");
		pnc1Event = pncSchedule.getEvents().get(0);
		pnc2Event = pncSchedule.getEvents().get(1);
		pnc3Event = pncSchedule.getEvents().get(2);

		// EasyMock setup in Spring config
		registrarBean = (RegistrarBean) ctx.getBean("registrarBean");
	}

	@Override
	protected void tearDown() throws Exception {
		ctx = null;
		pncSchedule = null;
		pnc1Event = null;
		pnc2Event = null;
		pnc3Event = null;
		registrarBean = null;
	}

	public void testCreateExpected() {
		Date date = new Date();

		Patient patient = new Patient();

		Capture<Date> minDate1Capture = new Capture<Date>();
		Capture<Date> dueDate1Capture = new Capture<Date>();
		Capture<Date> lateDate1Capture = new Capture<Date>();
		Capture<Date> maxDate1Capture = new Capture<Date>();
		Capture<Date> minDate2Capture = new Capture<Date>();
		Capture<Date> dueDate2Capture = new Capture<Date>();
		Capture<Date> lateDate2Capture = new Capture<Date>();
		Capture<Date> maxDate2Capture = new Capture<Date>();
		Capture<Date> minDate3Capture = new Capture<Date>();
		Capture<Date> dueDate3Capture = new Capture<Date>();
		Capture<Date> lateDate3Capture = new Capture<Date>();
		Capture<Date> maxDate3Capture = new Capture<Date>();

		List<Encounter> encounterList = new ArrayList<Encounter>();
		List<ExpectedEncounter> expectedEncounterList = new ArrayList<ExpectedEncounter>();

		Date deliveryDate = new Date();

		expect(registrarBean.getCurrentDeliveryDate(patient)).andReturn(
				deliveryDate);
		expect(
				registrarBean.getEncounters(patient, pncSchedule
						.getEncounterTypeName(), deliveryDate)).andReturn(
				encounterList);
		expect(
				registrarBean.getExpectedEncounters(patient, pncSchedule
						.getName())).andReturn(expectedEncounterList);
		expect(
				registrarBean.createExpectedEncounter(eq(patient),
						eq(pncSchedule.getEncounterTypeName()),
						capture(minDate1Capture), capture(dueDate1Capture),
						capture(lateDate1Capture), capture(maxDate1Capture),
						eq(pnc1Event.getName()), eq(pncSchedule.getName())))
				.andReturn(new ExpectedEncounter());
		expect(
				registrarBean.createExpectedEncounter(eq(patient),
						eq(pncSchedule.getEncounterTypeName()),
						capture(minDate2Capture), capture(dueDate2Capture),
						capture(lateDate2Capture), capture(maxDate2Capture),
						eq(pnc2Event.getName()), eq(pncSchedule.getName())))
				.andReturn(new ExpectedEncounter());
		expect(
				registrarBean.createExpectedEncounter(eq(patient),
						eq(pncSchedule.getEncounterTypeName()),
						capture(minDate3Capture), capture(dueDate3Capture),
						capture(lateDate3Capture), capture(maxDate3Capture),
						eq(pnc3Event.getName()), eq(pncSchedule.getName())))
				.andReturn(new ExpectedEncounter());

		replay(registrarBean);

		pncSchedule.updateSchedule(patient, date);

		verify(registrarBean);

		Date minDate1 = minDate1Capture.getValue();
		Date dueDate1 = dueDate1Capture.getValue();
		Date lateDate1 = lateDate1Capture.getValue();
		Date maxDate1 = maxDate1Capture.getValue();

		assertNotNull("Min date is null", minDate1);
		assertNotNull("Due date is null", dueDate1);
		assertNotNull("Late date is null", lateDate1);
		assertTrue("Late date is not after due date", lateDate1.after(dueDate1));
		assertNotNull("Max date is null", maxDate1);

		Date minDate2 = minDate1Capture.getValue();
		Date dueDate2 = dueDate1Capture.getValue();
		Date lateDate2 = lateDate1Capture.getValue();
		Date maxDate2 = maxDate1Capture.getValue();

		assertNotNull("Min date is null", minDate2);
		assertNotNull("Due date is null", dueDate2);
		assertNotNull("Late date is null", lateDate2);
		assertTrue("Late date is not after due date", lateDate2.after(dueDate2));
		assertNotNull("Max date is null", maxDate2);

		Date minDate3 = minDate1Capture.getValue();
		Date dueDate3 = dueDate1Capture.getValue();
		Date lateDate3 = lateDate1Capture.getValue();
		Date maxDate3 = maxDate1Capture.getValue();

		assertNotNull("Min date is null", minDate3);
		assertNotNull("Due date is null", dueDate3);
		assertNotNull("Late date is null", lateDate3);
		assertTrue("Late date is not after due date", lateDate3.after(dueDate3));
		assertNotNull("Max date is null", maxDate3);
	}

	public void testSatisfyExpected() {
		Date date = new Date();

		Patient patient = new Patient();

		Capture<ExpectedEncounter> pnc1ExpectedCapture = new Capture<ExpectedEncounter>();
		Capture<ExpectedEncounter> pnc2ExpectedCapture = new Capture<ExpectedEncounter>();
		Capture<ExpectedEncounter> pnc3ExpectedCapture = new Capture<ExpectedEncounter>();

		Date deliveryDate = new Date();
		Calendar calendar = Calendar.getInstance();

		List<Encounter> encounterList = new ArrayList<Encounter>();
		Encounter encounter1 = new Encounter();
		calendar.add(Calendar.HOUR_OF_DAY, 7);
		encounter1.setEncounterDatetime(calendar.getTime());
		encounterList.add(encounter1);
		Encounter encounter2 = new Encounter();
		calendar.add(Calendar.DATE, 7);
		encounter2.setEncounterDatetime(calendar.getTime());
		encounterList.add(encounter2);

		List<ExpectedEncounter> expectedEncounterList = new ArrayList<ExpectedEncounter>();
		ExpectedEncounter expectedEncounter1 = new ExpectedEncounter();
        expectedEncounter1.setId(1L);
		expectedEncounter1.setName(pnc1Event.getName());
        expectedEncounterList.add(expectedEncounter1);

        ExpectedEncounter expectedEncounter2 = new ExpectedEncounter();
        expectedEncounter2.setId(2L);
		expectedEncounter2.setName(pnc2Event.getName());
		expectedEncounterList.add(expectedEncounter2);

        ExpectedEncounter expectedEncounter3 = new ExpectedEncounter();
        expectedEncounter3.setId(3L);
		expectedEncounter3.setName(pnc3Event.getName());
		expectedEncounterList.add(expectedEncounter3);

		expect(registrarBean.getCurrentDeliveryDate(patient)).andReturn(
				deliveryDate);
		expect(
				registrarBean.getEncounters(patient, pncSchedule
						.getEncounterTypeName(), deliveryDate)).andReturn(
				encounterList);
		expect(
				registrarBean.getExpectedEncounters(patient, pncSchedule
						.getName())).andReturn(expectedEncounterList);
		expect(
				registrarBean
						.saveExpectedEncounter(capture(pnc1ExpectedCapture)))
				.andReturn(new ExpectedEncounter());
		expect(
				registrarBean
						.saveExpectedEncounter(capture(pnc2ExpectedCapture)))
				.andReturn(new ExpectedEncounter());
		expect(
				registrarBean
						.saveExpectedEncounter(capture(pnc3ExpectedCapture)))
				.andReturn(new ExpectedEncounter());

		replay(registrarBean);

		pncSchedule.updateSchedule(patient, date);

		verify(registrarBean);

		ExpectedEncounter capturedPNC1Expected = pnc1ExpectedCapture.getValue();
		assertEquals(Boolean.TRUE, capturedPNC1Expected.getVoided());
		assertEquals(encounter1, capturedPNC1Expected.getEncounter());

		ExpectedEncounter capturedPNC2Expected = pnc2ExpectedCapture.getValue();
		assertEquals(Boolean.TRUE, capturedPNC2Expected.getVoided());
		assertEquals(encounter2, capturedPNC2Expected.getEncounter());

		ExpectedEncounter capturedPNC3Expected = pnc3ExpectedCapture.getValue();
		assertEquals(Boolean.FALSE, capturedPNC3Expected.getVoided());
	}

	public void testRemoveExpected() {
		Date date = new Date();

		Patient patient = new Patient();

		List<ExpectedEncounter> expectedEncounterList = new ArrayList<ExpectedEncounter>();
		expectedEncounterList.add(new ExpectedEncounter());
		expectedEncounterList.add(new ExpectedEncounter());
		expectedEncounterList.add(new ExpectedEncounter());

		Capture<ExpectedEncounter> pnc1ExpectedCapture = new Capture<ExpectedEncounter>();
		Capture<ExpectedEncounter> pnc2ExpectedCapture = new Capture<ExpectedEncounter>();
		Capture<ExpectedEncounter> pnc3ExpectedCapture = new Capture<ExpectedEncounter>();

		Date deliveryDate = null;

		expect(registrarBean.getCurrentDeliveryDate(patient)).andReturn(
				deliveryDate);
		expect(
				registrarBean.getExpectedEncounters(patient, pncSchedule
						.getName())).andReturn(expectedEncounterList);
		expect(
				registrarBean
						.saveExpectedEncounter(capture(pnc1ExpectedCapture)))
				.andReturn(new ExpectedEncounter());
		expect(
				registrarBean
						.saveExpectedEncounter(capture(pnc2ExpectedCapture)))
				.andReturn(new ExpectedEncounter());
		expect(
				registrarBean
						.saveExpectedEncounter(capture(pnc3ExpectedCapture)))
				.andReturn(new ExpectedEncounter());

		replay(registrarBean);

		pncSchedule.updateSchedule(patient, date);

		verify(registrarBean);

		ExpectedEncounter capturedPNC1Expected = pnc1ExpectedCapture.getValue();
		assertEquals(Boolean.TRUE, capturedPNC1Expected.getVoided());

		ExpectedEncounter capturedPNC2Expected = pnc2ExpectedCapture.getValue();
		assertEquals(Boolean.TRUE, capturedPNC2Expected.getVoided());

		ExpectedEncounter capturedPNC3Expected = pnc3ExpectedCapture.getValue();
		assertEquals(Boolean.TRUE, capturedPNC3Expected.getVoided());
	}

	public void testNoActionNoDelivery() {
		Date date = new Date();

		Patient patient = new Patient();

		List<ExpectedEncounter> expectedEncounterList = new ArrayList<ExpectedEncounter>();

		Date deliveryDate = null;

		expect(registrarBean.getCurrentDeliveryDate(patient)).andReturn(
				deliveryDate);
		expect(
				registrarBean.getExpectedEncounters(patient, pncSchedule
						.getName())).andReturn(expectedEncounterList);

		replay(registrarBean);

		pncSchedule.updateSchedule(patient, date);

		verify(registrarBean);
	}

	public void testNoActionPastDelivery() {
		Date date = new Date();

		Patient patient = new Patient();

		List<ExpectedEncounter> expectedEncounterList = new ArrayList<ExpectedEncounter>();

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -2);

		Date deliveryDate = calendar.getTime();

		expect(registrarBean.getCurrentDeliveryDate(patient)).andReturn(
				deliveryDate);
		expect(
				registrarBean.getExpectedEncounters(patient, pncSchedule
						.getName())).andReturn(expectedEncounterList);

		replay(registrarBean);

		pncSchedule.updateSchedule(patient, date);

		verify(registrarBean);
	}
}
