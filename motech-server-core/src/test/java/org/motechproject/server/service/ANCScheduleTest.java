/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) ${year} The Trustees of Columbia University in the City of
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.easymock.Capture;
import org.motechproject.server.model.ExpectedEncounter;
import org.motechproject.server.service.impl.ExpectedEncounterSchedule;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.server.util.MotechConstants;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ANCScheduleTest extends TestCase {
	ApplicationContext ctx;

	RegistrarBean registrarBean;
	ExpectedEncounterSchedule ancSchedule;
	Concept nextANCDateConcept;

	@Override
	protected void setUp() throws Exception {
		ctx = new ClassPathXmlApplicationContext(new String[] {
				"test-common-program-beans.xml",
				"services/pregnancy-anc-service.xml" });
		ancSchedule = (ExpectedEncounterSchedule) ctx
				.getBean("pregnancyANCSchedule");

		// EasyMock setup in Spring config
		registrarBean = (RegistrarBean) ctx.getBean("registrarBean");

		nextANCDateConcept = new Concept(1);
		nextANCDateConcept.addName(new ConceptName(
				MotechConstants.CONCEPT_NEXT_ANC_DATE, null));
	}

	@Override
	protected void tearDown() throws Exception {
		ctx = null;
		ancSchedule = null;
		registrarBean = null;
		nextANCDateConcept = null;
	}

	public void testSatisfyExpected() {
		Date date = new Date();

		Patient patient = new Patient(1);

		Capture<ExpectedEncounter> anc1ExpectedCapture = new Capture<ExpectedEncounter>();
		Capture<ExpectedEncounter> anc2ExpectedCapture = new Capture<ExpectedEncounter>();

		List<Encounter> encounterList = new ArrayList<Encounter>();
		Encounter encounter1 = new Encounter();
		encounter1.setEncounterDatetime(date);
		encounterList.add(encounter1);
		Encounter encounter2 = new Encounter();
		encounter1.setEncounterDatetime(date);
		encounterList.add(encounter2);

		List<ExpectedEncounter> expectedEncounterList = new ArrayList<ExpectedEncounter>();
		ExpectedEncounter expectedEncounter1 = new ExpectedEncounter();
		expectedEncounter1.setName("ANC");
		expectedEncounterList.add(expectedEncounter1);
		ExpectedEncounter expectedEncounter2 = new ExpectedEncounter();
		expectedEncounter2.setName("ANC");
		expectedEncounterList.add(expectedEncounter2);
		ExpectedEncounter expectedEncounter3 = new ExpectedEncounter();
		expectedEncounter3.setName("ANC");
		expectedEncounterList.add(expectedEncounter3);

		Date pregnancyDate = new Date();

		expect(registrarBean.getActivePregnancyDueDate(patient.getPatientId()))
				.andReturn(pregnancyDate);
		expect(
				registrarBean.getEncounters(eq(patient), eq(ancSchedule
						.getEncounterTypeName()), (Date) anyObject()))
				.andReturn(encounterList);
		expect(
				registrarBean.getExpectedEncounters(patient, ancSchedule
						.getName())).andReturn(expectedEncounterList);
		expect(
				registrarBean
						.saveExpectedEncounter(capture(anc1ExpectedCapture)))
				.andReturn(new ExpectedEncounter());
		expect(
				registrarBean
						.saveExpectedEncounter(capture(anc2ExpectedCapture)))
				.andReturn(new ExpectedEncounter());

		replay(registrarBean);

		ancSchedule.updateSchedule(patient, date);

		verify(registrarBean);

		ExpectedEncounter capturedANC1Expected = anc1ExpectedCapture.getValue();
		assertEquals(Boolean.TRUE, capturedANC1Expected.getVoided());
		assertEquals(encounter1, capturedANC1Expected.getEncounter());

		ExpectedEncounter capturedANC2Expected = anc2ExpectedCapture.getValue();
		assertEquals(Boolean.TRUE, capturedANC2Expected.getVoided());
		assertEquals(encounter2, capturedANC2Expected.getEncounter());
	}

	public void testCreateExpected() {
		Date date = new Date();

		Patient patient = new Patient(1);

		Capture<Date> minDateCapture = new Capture<Date>();
		Capture<Date> dueDateCapture = new Capture<Date>();
		Capture<Date> lateDateCapture = new Capture<Date>();

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -1);
		Date nextANC1 = calendar.getTime();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, 1);
		Date nextANC2 = calendar.getTime();

		List<Encounter> encounterList = new ArrayList<Encounter>();
		Encounter encounter1 = new Encounter();
		encounter1.setEncounterDatetime(date);
		Obs nextANC1Obs = new Obs();
		nextANC1Obs.setConcept(nextANCDateConcept);
		nextANC1Obs.setValueDatetime(nextANC1);
		encounter1.addObs(nextANC1Obs);
		encounterList.add(encounter1);
		Encounter encounter2 = new Encounter();
		encounter2.setEncounterDatetime(date);
		Obs nextANC2Obs = new Obs();
		nextANC2Obs.setConcept(nextANCDateConcept);
		nextANC2Obs.setValueDatetime(nextANC2);
		encounter2.addObs(nextANC2Obs);
		encounterList.add(encounter2);

		List<ExpectedEncounter> expectedEncounterList = new ArrayList<ExpectedEncounter>();

		Date pregnancyDate = new Date();

		expect(registrarBean.getActivePregnancyDueDate(patient.getPatientId()))
				.andReturn(pregnancyDate);
		expect(
				registrarBean.getEncounters(eq(patient), eq(ancSchedule
						.getEncounterTypeName()), (Date) anyObject()))
				.andReturn(encounterList);
		expect(
				registrarBean.getExpectedEncounters(patient, ancSchedule
						.getName())).andReturn(expectedEncounterList);
		expect(
				registrarBean.createExpectedEncounter(eq(patient),
						eq(ancSchedule.getEncounterTypeName()),
						capture(minDateCapture), capture(dueDateCapture),
						capture(lateDateCapture), eq((Date) null),
						eq(ancSchedule.getName()), eq(ancSchedule.getName())))
				.andReturn(new ExpectedEncounter());

		replay(registrarBean);

		ancSchedule.updateSchedule(patient, date);

		verify(registrarBean);

		Date capturedMinDate = minDateCapture.getValue();
		assertEquals(encounter2.getEncounterDatetime(), capturedMinDate);

		Date capturedDueDate = dueDateCapture.getValue();
		assertEquals(nextANC2, capturedDueDate);

		Date capturedLateDate = lateDateCapture.getValue();
		assertTrue(capturedLateDate.after(capturedDueDate));
	}

	public void testNotCreateExpectedAlreadyExists() {
		Date date = new Date();

		Patient patient = new Patient(1);

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -1);
		Date nextANC1 = calendar.getTime();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, 1);
		Date nextANC2 = calendar.getTime();

		List<Encounter> encounterList = new ArrayList<Encounter>();
		Encounter encounter1 = new Encounter();
		encounter1.setEncounterDatetime(date);
		Obs nextANC1Obs = new Obs();
		nextANC1Obs.setConcept(nextANCDateConcept);
		nextANC1Obs.setValueDatetime(nextANC1);
		encounter1.addObs(nextANC1Obs);
		encounterList.add(encounter1);
		Encounter encounter2 = new Encounter();
		encounter2.setEncounterDatetime(date);
		Obs nextANC2Obs = new Obs();
		nextANC2Obs.setConcept(nextANCDateConcept);
		nextANC2Obs.setValueDatetime(nextANC2);
		encounter2.addObs(nextANC2Obs);
		encounterList.add(encounter2);

		calendar.add(Calendar.DATE, 7); // Expecting 1 week
		Date lateDate = calendar.getTime();

		List<ExpectedEncounter> expectedEncounterList = new ArrayList<ExpectedEncounter>();
		ExpectedEncounter expectedEncounter1 = new ExpectedEncounter();
		expectedEncounter1.setName("ANC");
		expectedEncounter1.setMinEncounterDatetime(encounter2
				.getEncounterDatetime());
		expectedEncounter1.setDueEncounterDatetime(nextANC2);
		expectedEncounter1.setLateEncounterDatetime(lateDate);
		expectedEncounterList.add(expectedEncounter1);

		Date pregnancyDate = new Date();

		expect(registrarBean.getActivePregnancyDueDate(patient.getPatientId()))
				.andReturn(pregnancyDate);
		expect(
				registrarBean.getEncounters(eq(patient), eq(ancSchedule
						.getEncounterTypeName()), (Date) anyObject()))
				.andReturn(encounterList);
		expect(
				registrarBean.getExpectedEncounters(patient, ancSchedule
						.getName())).andReturn(expectedEncounterList);

		replay(registrarBean);

		ancSchedule.updateSchedule(patient, date);

		verify(registrarBean);
	}

	public void testRemoveExpected() {
		Date date = new Date();

		Patient patient = new Patient();

		List<ExpectedEncounter> expectedEncounterList = new ArrayList<ExpectedEncounter>();
		expectedEncounterList.add(new ExpectedEncounter());
		expectedEncounterList.add(new ExpectedEncounter());
		expectedEncounterList.add(new ExpectedEncounter());

		Capture<ExpectedEncounter> anc1ExpectedCapture = new Capture<ExpectedEncounter>();
		Capture<ExpectedEncounter> anc2ExpectedCapture = new Capture<ExpectedEncounter>();
		Capture<ExpectedEncounter> anc3ExpectedCapture = new Capture<ExpectedEncounter>();

		Date pregnancyDate = null;

		expect(registrarBean.getActivePregnancyDueDate(patient.getPatientId()))
				.andReturn(pregnancyDate);
		expect(
				registrarBean.getExpectedEncounters(patient, ancSchedule
						.getName())).andReturn(expectedEncounterList);
		expect(
				registrarBean
						.saveExpectedEncounter(capture(anc1ExpectedCapture)))
				.andReturn(new ExpectedEncounter());
		expect(
				registrarBean
						.saveExpectedEncounter(capture(anc2ExpectedCapture)))
				.andReturn(new ExpectedEncounter());
		expect(
				registrarBean
						.saveExpectedEncounter(capture(anc3ExpectedCapture)))
				.andReturn(new ExpectedEncounter());

		replay(registrarBean);

		ancSchedule.updateSchedule(patient, date);

		verify(registrarBean);

		ExpectedEncounter capturedANC1Expected = anc1ExpectedCapture.getValue();
		assertEquals(Boolean.TRUE, capturedANC1Expected.getVoided());

		ExpectedEncounter capturedANC2Expected = anc2ExpectedCapture.getValue();
		assertEquals(Boolean.TRUE, capturedANC2Expected.getVoided());

		ExpectedEncounter capturedANC3Expected = anc3ExpectedCapture.getValue();
		assertEquals(Boolean.TRUE, capturedANC3Expected.getVoided());
	}

	public void testNoAction() {
		Date date = new Date();

		Patient patient = new Patient();

		List<ExpectedEncounter> expectedEncounterList = new ArrayList<ExpectedEncounter>();

		Date pregnancyDate = null;

		expect(registrarBean.getActivePregnancyDueDate(patient.getPatientId()))
				.andReturn(pregnancyDate);
		expect(
				registrarBean.getExpectedEncounters(patient, ancSchedule
						.getName())).andReturn(expectedEncounterList);

		replay(registrarBean);

		ancSchedule.updateSchedule(patient, date);

		verify(registrarBean);
	}
}
