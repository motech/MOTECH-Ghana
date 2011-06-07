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
import org.motechproject.server.model.ExpectedObs;
import org.motechproject.server.service.impl.ExpectedObsSchedule;
import org.motechproject.server.svc.RegistrarBean;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.easymock.EasyMock.*;

public class TetanusScheduleTest extends TestCase {

	ApplicationContext ctx;

	RegistrarBean registrarBean;
	ExpectedObsSchedule ttSchedule;
	ExpectedCareEvent tt1Event;
	ExpectedCareEvent tt2Event;
	ExpectedCareEvent tt3Event;
	ExpectedCareEvent tt4Event;
	ExpectedCareEvent tt5Event;

	@Override
	protected void setUp() throws Exception {
		ctx = new ClassPathXmlApplicationContext(new String[] {
				"test-common-program-beans.xml",
				"services/pregnancy-tetanus-service.xml" });
		ttSchedule = (ExpectedObsSchedule) ctx
				.getBean("pregnancyTetanusSchedule");
		tt1Event = ttSchedule.getEvents().get(0);
		tt2Event = ttSchedule.getEvents().get(1);
		tt3Event = ttSchedule.getEvents().get(2);
		tt4Event = ttSchedule.getEvents().get(3);
		tt5Event = ttSchedule.getEvents().get(4);

		// EasyMock setup in Spring config
		registrarBean = (RegistrarBean) ctx.getBean("registrarBean");
	}

	@Override
	protected void tearDown() throws Exception {
		ctx = null;
		ttSchedule = null;
		tt1Event = null;
		tt2Event = null;
		tt3Event = null;
		tt4Event = null;
		tt5Event = null;
		registrarBean = null;
	}

	public void testCreateFirstDose() {
		Date date = new Date();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.YEAR, -30); // age is 30 years

		Patient patient = new Patient();
		patient.setGender("F");
		patient.setBirthdate(calendar.getTime());

		Capture<Date> dueDateCapture = new Capture<Date>();
		Capture<Date> lateDateCapture = new Capture<Date>();

		List<Obs> obsList = new ArrayList<Obs>();
		List<ExpectedObs> expectedObsList = new ArrayList<ExpectedObs>();

		expect(
				registrarBean.getObs(patient, ttSchedule.getConceptName(),
						ttSchedule.getValueConceptName(), patient
								.getBirthdate())).andReturn(obsList);
		expect(registrarBean.getExpectedObs(patient, ttSchedule.getName()))
				.andReturn(expectedObsList);
		expect(
				registrarBean.createExpectedObs(eq(patient), eq(ttSchedule
						.getConceptName()),
						eq(ttSchedule.getValueConceptName()), eq(tt1Event
								.getNumber()), (Date) anyObject(),
						capture(dueDateCapture), capture(lateDateCapture),
						(Date) anyObject(), eq(tt1Event.getName()),
						eq(ttSchedule.getName()))).andReturn(new ExpectedObs());

		replay(registrarBean);

		ttSchedule.updateSchedule(patient, date);

		verify(registrarBean);

		Date dueDate = dueDateCapture.getValue();
		Date lateDate = lateDateCapture.getValue();

		assertNotNull("Due date is null", dueDate);
		assertNotNull("Late date is null", lateDate);
		assertTrue("Late date not equal to due date", lateDate.equals(dueDate));
	}

	public void testCreateExpectedDose() {
		Date date = new Date();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.YEAR, -30); // age is 30 years

		Patient patient = new Patient();
		patient.setGender("F");
		patient.setBirthdate(calendar.getTime());

		Capture<ExpectedObs> expectedObsCapture = new Capture<ExpectedObs>();
		Capture<Date> dueDateCapture = new Capture<Date>();
		Capture<Date> lateDateCapture = new Capture<Date>();

		List<Obs> obsList = new ArrayList<Obs>();
		Obs obs3 = new Obs();
		obs3.setObsDatetime(date);
		obs3.setValueNumeric(new Double(3));
		obsList.add(obs3);
		Obs obs4 = new Obs();
		obs4.setObsDatetime(date);
		obs4.setValueNumeric(new Double(4));
		obsList.add(obs4);

		List<ExpectedObs> expectedObsList = new ArrayList<ExpectedObs>();
		ExpectedObs expectedObs = new ExpectedObs();
		expectedObs.setName(tt4Event.getName());
        expectedObs.setId(1L);
		expectedObsList.add(expectedObs);

		expect(
				registrarBean.getObs(patient, ttSchedule.getConceptName(),
						ttSchedule.getValueConceptName(), patient
								.getBirthdate())).andReturn(obsList);
		expect(registrarBean.getExpectedObs(patient, ttSchedule.getName()))
				.andReturn(expectedObsList);
		expect(registrarBean.saveExpectedObs(capture(expectedObsCapture)))
				.andReturn(new ExpectedObs());
		expect(
				registrarBean.createExpectedObs(eq(patient), eq(ttSchedule
						.getConceptName()),
						eq(ttSchedule.getValueConceptName()), eq(tt5Event
								.getNumber()), (Date) anyObject(),
						capture(dueDateCapture), capture(lateDateCapture),
						(Date) anyObject(), eq(tt5Event.getName()),
						eq(ttSchedule.getName()))).andReturn(new ExpectedObs());

		replay(registrarBean);

		ttSchedule.updateSchedule(patient, date);

		verify(registrarBean);

		ExpectedObs tt4ExpectedObs = expectedObsCapture.getValue();
		assertEquals(tt4Event.getName(), tt4ExpectedObs.getName());
		assertEquals(Boolean.TRUE, tt4ExpectedObs.getVoided());
		assertEquals(obs4, tt4ExpectedObs.getObs());

		Date dueDate = dueDateCapture.getValue();
		Date lateDate = lateDateCapture.getValue();

		assertNotNull("Due date is null", dueDate);
		assertNotNull("Late date is null", lateDate);
		assertTrue("Late date is not after due date", lateDate.after(dueDate));
	}

	public void testExpectedDoseUpdated() {
		Date date = new Date();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.YEAR, -30); // age is 30 years

		Patient patient = new Patient();
		patient.setGender("F");
		patient.setBirthdate(calendar.getTime());

		Capture<ExpectedObs> expectedObsCapture = new Capture<ExpectedObs>();

		List<Obs> obsList = new ArrayList<Obs>();
		Obs obs3 = new Obs();
		obs3.setObsDatetime(date);
		obs3.setValueNumeric(new Double(1));
		obsList.add(obs3);

		List<ExpectedObs> expectedObsList = new ArrayList<ExpectedObs>();
		ExpectedObs expectedObs = new ExpectedObs();
        expectedObs.setId(2L);
		expectedObs.setName(tt2Event.getName());
		expectedObsList.add(expectedObs);

		expect(
				registrarBean.getObs(patient, ttSchedule.getConceptName(),
						ttSchedule.getValueConceptName(), patient
								.getBirthdate())).andReturn(obsList);
		expect(registrarBean.getExpectedObs(patient, ttSchedule.getName()))
				.andReturn(expectedObsList);
		expect(registrarBean.saveExpectedObs(capture(expectedObsCapture)))
				.andReturn(new ExpectedObs());

		replay(registrarBean);

		ttSchedule.updateSchedule(patient, date);

		verify(registrarBean);

		ExpectedObs tt2ExpectedObs = expectedObsCapture.getValue();
		assertEquals(tt2Event.getName(), tt2ExpectedObs.getName());
		assertEquals(Boolean.FALSE, tt2ExpectedObs.getVoided());
		assertNull("ExpectedObs has associated Obs", tt2ExpectedObs.getObs());
		assertNotNull("Due date is null", tt2ExpectedObs.getDueObsDatetime());
		assertNotNull("Late date is null", tt2ExpectedObs.getLateObsDatetime());
		assertTrue("Late date not after due date", tt2ExpectedObs
				.getLateObsDatetime().after(tt2ExpectedObs.getDueObsDatetime()));
	}

	public void testNoActionDose() {
		Date date = new Date();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.YEAR, -30); // age is 30 years

		Patient patient = new Patient();
		patient.setGender("F");
		patient.setBirthdate(calendar.getTime());

		List<Obs> obsList = new ArrayList<Obs>();
		Obs obs = new Obs();
		obs.setObsDatetime(date);
		obs.setValueNumeric(new Double(5));
		obsList.add(obs);

		List<ExpectedObs> expectedObsList = new ArrayList<ExpectedObs>();

		expect(
				registrarBean.getObs(patient, ttSchedule.getConceptName(),
						ttSchedule.getValueConceptName(), patient
								.getBirthdate())).andReturn(obsList);
		expect(registrarBean.getExpectedObs(patient, ttSchedule.getName()))
				.andReturn(expectedObsList);

		replay(registrarBean);

		ttSchedule.updateSchedule(patient, date);

		verify(registrarBean);
	}

	public void testNoActionAge() {
		Date date = new Date();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.YEAR, -6); // age is 6 years

		Patient patient = new Patient();
		patient.setGender("F");
		patient.setBirthdate(calendar.getTime());

		List<ExpectedObs> expectedObsList = new ArrayList<ExpectedObs>();

		expect(registrarBean.getExpectedObs(patient, ttSchedule.getName()))
				.andReturn(expectedObsList);

		replay(registrarBean);

		ttSchedule.updateSchedule(patient, date);

		verify(registrarBean);
	}

	public void testNoActionGender() {
		Date date = new Date();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.YEAR, -30); // age is 30 years

		Patient patient = new Patient();
		patient.setGender("M"); // Male gender
		patient.setBirthdate(calendar.getTime());

		List<ExpectedObs> expectedObsList = new ArrayList<ExpectedObs>();

		expect(registrarBean.getExpectedObs(patient, ttSchedule.getName()))
				.andReturn(expectedObsList);

		replay(registrarBean);

		ttSchedule.updateSchedule(patient, date);

		verify(registrarBean);
	}

}
