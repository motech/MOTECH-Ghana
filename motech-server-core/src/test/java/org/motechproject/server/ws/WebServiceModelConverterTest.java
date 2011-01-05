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

package org.motechproject.server.ws;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.motechproject.server.model.Community;
import org.motechproject.server.model.ExpectedEncounter;
import org.motechproject.server.model.ExpectedObs;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.server.util.MotechConstants;
import org.motechproject.ws.Care;
import org.motechproject.ws.Gender;
import org.motechproject.ws.Patient;
import org.openmrs.Location;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;

public class WebServiceModelConverterTest extends TestCase {

	WebServiceModelConverter modelConverter;
	RegistrarBean registrarBean;
	PatientIdentifierType motechIdType;

	@Override
	protected void setUp() throws Exception {
		WebServiceModelConverterImpl modelConverterImpl = new WebServiceModelConverterImpl();
		registrarBean = createMock(RegistrarBean.class);
		modelConverterImpl.setRegistrarBean(registrarBean);
		modelConverter = modelConverterImpl;

		motechIdType = new PatientIdentifierType();
		motechIdType.setName(MotechConstants.PATIENT_IDENTIFIER_MOTECH_ID);
	}

	@Override
	protected void tearDown() throws Exception {
		registrarBean = null;
		modelConverter = null;
		motechIdType = null;
	}

	public void testDefaultedEncounters() {
		String anc1 = "ANC1", anc2 = "ANC2";
		String patient1Id = "Patient1", patient2Id = "Patient2", patient3Id = "Patient3";
		String patient1Pref = "Pref1", patient1Last = "Last1", patient1Community = "Comm1";
		String patient2Pref = "Pref2", patient2Last = "Last2", patient2Community = "Comm2";
		String patient3Pref = "Pref3", patient3Last = "Last3", patient3Community = "Comm3";
		Calendar calendar = Calendar.getInstance();
		calendar.set(1981, Calendar.JANUARY, 1);
		Date patient1Birth = calendar.getTime();
		calendar.set(1982, Calendar.FEBRUARY, 2);
		Date patient2Birth = calendar.getTime();
		calendar.set(1983, Calendar.MARCH, 3);
		Date patient3Birth = calendar.getTime();

		Community comm1 = new Community();
		comm1.setName(patient1Community);
		Community comm2 = new Community();
		comm2.setName(patient2Community);
		Community comm3 = new Community();
		comm3.setName(patient3Community);

		ExpectedEncounter encounter1 = new ExpectedEncounter();
		encounter1.setName(anc1);
		ExpectedEncounter encounter2 = new ExpectedEncounter();
		encounter2.setName(anc2);
		ExpectedEncounter encounter3 = new ExpectedEncounter();
		encounter3.setName(anc2);

		org.openmrs.Patient patient1 = new org.openmrs.Patient(1);
		patient1.addIdentifier(new PatientIdentifier(patient1Id, motechIdType,
				new Location()));
		patient1.setBirthdate(patient1Birth);
		patient1.setGender("F");
		patient1.addName(new PersonName(patient1Pref, null, patient1Last));
		PersonAddress patient1Address = new PersonAddress();
		patient1.addAddress(patient1Address);

		org.openmrs.Patient patient2 = new org.openmrs.Patient(2);
		patient2.addIdentifier(new PatientIdentifier(patient2Id, motechIdType,
				new Location()));
		patient2.setBirthdate(patient2Birth);
		patient2.setGender("M");
		patient2.addName(new PersonName(patient2Pref, null, patient2Last));
		PersonAddress patient2Address = new PersonAddress();
		patient2.addAddress(patient2Address);

		org.openmrs.Patient patient3 = new org.openmrs.Patient(3);
		patient3.addIdentifier(new PatientIdentifier(patient3Id, motechIdType,
				new Location()));
		patient3.setBirthdate(patient3Birth);
		patient3.setGender("F");
		patient3.addName(new PersonName(patient3Pref, null, patient3Last));
		PersonAddress patient3Address = new PersonAddress();
		patient3.addAddress(patient3Address);

		encounter1.setPatient(patient1);
		encounter2.setPatient(patient2);
		encounter3.setPatient(patient3);

		List<ExpectedEncounter> defaultedEncounters = new ArrayList<ExpectedEncounter>();
		defaultedEncounters.add(encounter1);
		defaultedEncounters.add(encounter2);
		defaultedEncounters.add(encounter3);

		expect(registrarBean.getCommunityByPatient(patient1)).andReturn(comm1);
		expect(registrarBean.getCommunityByPatient(patient2)).andReturn(comm2);
		expect(registrarBean.getCommunityByPatient(patient3)).andReturn(comm3);

		replay(registrarBean);

		Care[] cares = modelConverter
				.defaultedEncountersToWebServiceCares(defaultedEncounters);

		verify(registrarBean);

		assertEquals(2, cares.length);

		Care care1 = cares[0];
		assertEquals(anc1, care1.getName());
		assertEquals(1, care1.getPatients().length);

		Patient care1Patient = care1.getPatients()[0];
		assertEquals(patient1Id, care1Patient.getMotechId());
		assertEquals(patient1Pref, care1Patient.getPreferredName());
		assertEquals(patient1Last, care1Patient.getLastName());
		assertEquals(patient1Birth, care1Patient.getBirthDate());
		assertEquals(Gender.FEMALE, care1Patient.getSex());
		assertEquals(patient1Community, care1Patient.getCommunity());

		Care care2 = cares[1];
		assertEquals(anc2, care2.getName());
		assertEquals(2, care2.getPatients().length);

		Patient care2Patient1 = care2.getPatients()[0];
		assertEquals(patient2Id, care2Patient1.getMotechId());
		assertEquals(patient2Pref, care2Patient1.getPreferredName());
		assertEquals(patient2Last, care2Patient1.getLastName());
		assertEquals(patient2Birth, care2Patient1.getBirthDate());
		assertEquals(Gender.MALE, care2Patient1.getSex());
		assertEquals(patient2Community, care2Patient1.getCommunity());

		Patient care2Patient2 = care2.getPatients()[1];
		assertEquals(patient3Id, care2Patient2.getMotechId());
		assertEquals(patient3Pref, care2Patient2.getPreferredName());
		assertEquals(patient3Last, care2Patient2.getLastName());
		assertEquals(patient3Birth, care2Patient2.getBirthDate());
		assertEquals(Gender.FEMALE, care2Patient2.getSex());
		assertEquals(patient3Community, care2Patient2.getCommunity());
	}

	public void testDefaultedEncountersEmpty() {
		List<ExpectedEncounter> defaultedEncounters = new ArrayList<ExpectedEncounter>();

		Care[] cares = modelConverter
				.defaultedEncountersToWebServiceCares(defaultedEncounters);

		assertEquals(0, cares.length);
	}

	public void testDefaultedObs() {
		String tt1 = "TT1", tt2 = "TT2";
		String patient1Id = "Patient1", patient2Id = "Patient2", patient3Id = "Patient3";
		String patient1Pref = "Pref1", patient1Last = "Last1", patient1Community = "Comm1";
		String patient2Pref = "Pref2", patient2Last = "Last2", patient2Community = "Comm2";
		String patient3Pref = "Pref3", patient3Last = "Last3", patient3Community = "Comm3";
		Calendar calendar = Calendar.getInstance();
		calendar.set(1981, Calendar.JANUARY, 1);
		Date patient1Birth = calendar.getTime();
		calendar.set(1982, Calendar.FEBRUARY, 2);
		Date patient2Birth = calendar.getTime();
		calendar.set(1983, Calendar.MARCH, 3);
		Date patient3Birth = calendar.getTime();

		Community comm1 = new Community();
		comm1.setName(patient1Community);
		Community comm2 = new Community();
		comm2.setName(patient2Community);
		Community comm3 = new Community();
		comm3.setName(patient3Community);

		ExpectedObs obs1 = new ExpectedObs();
		obs1.setName(tt1);
		ExpectedObs obs2 = new ExpectedObs();
		obs2.setName(tt2);
		ExpectedObs obs3 = new ExpectedObs();
		obs3.setName(tt2);

		org.openmrs.Patient patient1 = new org.openmrs.Patient(1);
		patient1.addIdentifier(new PatientIdentifier(patient1Id, motechIdType,
				new Location()));
		patient1.setBirthdate(patient1Birth);
		patient1.setGender("F");
		patient1.addName(new PersonName(patient1Pref, null, patient1Last));
		PersonAddress patient1Address = new PersonAddress();
		patient1.addAddress(patient1Address);

		org.openmrs.Patient patient2 = new org.openmrs.Patient(2);
		patient2.addIdentifier(new PatientIdentifier(patient2Id, motechIdType,
				new Location()));
		patient2.setBirthdate(patient2Birth);
		patient2.setGender("M");
		patient2.addName(new PersonName(patient2Pref, null, patient2Last));
		PersonAddress patient2Address = new PersonAddress();
		patient2.addAddress(patient2Address);

		org.openmrs.Patient patient3 = new org.openmrs.Patient(3);
		patient3.addIdentifier(new PatientIdentifier(patient3Id, motechIdType,
				new Location()));
		patient3.setBirthdate(patient3Birth);
		patient3.setGender("F");
		patient3.addName(new PersonName(patient3Pref, null, patient3Last));
		PersonAddress patient3Address = new PersonAddress();
		patient3.addAddress(patient3Address);

		obs1.setPatient(patient1);
		obs2.setPatient(patient2);
		obs3.setPatient(patient3);

		List<ExpectedObs> defaultedObs = new ArrayList<ExpectedObs>();
		defaultedObs.add(obs1);
		defaultedObs.add(obs2);
		defaultedObs.add(obs3);

		expect(registrarBean.getCommunityByPatient(patient1)).andReturn(comm1);
		expect(registrarBean.getCommunityByPatient(patient2)).andReturn(comm2);
		expect(registrarBean.getCommunityByPatient(patient3)).andReturn(comm3);

		replay(registrarBean);

		Care[] cares = modelConverter
				.defaultedObsToWebServiceCares(defaultedObs);

		verify(registrarBean);

		assertEquals(2, cares.length);

		Care care1 = cares[0];
		assertEquals(tt1, care1.getName());
		assertEquals(1, care1.getPatients().length);

		Patient care1Patient = care1.getPatients()[0];
		assertEquals(patient1Id, care1Patient.getMotechId());
		assertEquals(patient1Pref, care1Patient.getPreferredName());
		assertEquals(patient1Last, care1Patient.getLastName());
		assertEquals(patient1Birth, care1Patient.getBirthDate());
		assertEquals(Gender.FEMALE, care1Patient.getSex());
		assertEquals(patient1Community, care1Patient.getCommunity());

		Care care2 = cares[1];
		assertEquals(tt2, care2.getName());
		assertEquals(2, care2.getPatients().length);

		Patient care2Patient1 = care2.getPatients()[0];
		assertEquals(patient2Id, care2Patient1.getMotechId());
		assertEquals(patient2Pref, care2Patient1.getPreferredName());
		assertEquals(patient2Last, care2Patient1.getLastName());
		assertEquals(patient2Birth, care2Patient1.getBirthDate());
		assertEquals(Gender.MALE, care2Patient1.getSex());
		assertEquals(patient2Community, care2Patient1.getCommunity());

		Patient care2Patient2 = care2.getPatients()[1];
		assertEquals(patient3Id, care2Patient2.getMotechId());
		assertEquals(patient3Pref, care2Patient2.getPreferredName());
		assertEquals(patient3Last, care2Patient2.getLastName());
		assertEquals(patient3Birth, care2Patient2.getBirthDate());
		assertEquals(Gender.FEMALE, care2Patient2.getSex());
		assertEquals(patient3Community, care2Patient2.getCommunity());
	}

	public void testDefaultedObsEmpty() {
		List<ExpectedObs> defaultedObs = new ArrayList<ExpectedObs>();

		Care[] cares = modelConverter
				.defaultedObsToWebServiceCares(defaultedObs);

		assertEquals(0, cares.length);
	}

	public void testDefaulted() {
		String anc1 = "ANC1", anc2 = "ANC2";
		String tt1 = "TT1", tt2 = "TT2";
		String patient1Id = "Patient1", patient2Id = "Patient2", patient3Id = "Patient3";
		String patient1Pref = "Pref1", patient1Last = "Last1", patient1Community = "Comm1";
		String patient2Pref = "Pref2", patient2Last = "Last2", patient2Community = "Comm2";
		String patient3Pref = "Pref3", patient3Last = "Last3", patient3Community = "Comm3";
		Calendar calendar = Calendar.getInstance();
		calendar.set(1981, Calendar.JANUARY, 1);
		Date patient1Birth = calendar.getTime();
		calendar.set(1982, Calendar.FEBRUARY, 2);
		Date patient2Birth = calendar.getTime();
		calendar.set(1983, Calendar.MARCH, 3);
		Date patient3Birth = calendar.getTime();

		Community comm1 = new Community();
		comm1.setName(patient1Community);
		Community comm2 = new Community();
		comm2.setName(patient2Community);
		Community comm3 = new Community();
		comm3.setName(patient3Community);

		org.openmrs.Patient patient1 = new org.openmrs.Patient(1);
		patient1.addIdentifier(new PatientIdentifier(patient1Id, motechIdType,
				new Location()));
		patient1.setBirthdate(patient1Birth);
		patient1.setGender("F");
		patient1.addName(new PersonName(patient1Pref, null, patient1Last));
		PersonAddress patient1Address = new PersonAddress();
		patient1.addAddress(patient1Address);

		org.openmrs.Patient patient2 = new org.openmrs.Patient(2);
		patient2.addIdentifier(new PatientIdentifier(patient2Id, motechIdType,
				new Location()));
		patient2.setBirthdate(patient2Birth);
		patient2.setGender("M");
		patient2.addName(new PersonName(patient2Pref, null, patient2Last));
		PersonAddress patient2Address = new PersonAddress();
		patient2.addAddress(patient2Address);

		org.openmrs.Patient patient3 = new org.openmrs.Patient(3);
		patient3.addIdentifier(new PatientIdentifier(patient3Id, motechIdType,
				new Location()));
		patient3.setBirthdate(patient3Birth);
		patient3.setGender("F");
		patient3.addName(new PersonName(patient3Pref, null, patient3Last));
		PersonAddress patient3Address = new PersonAddress();
		patient3.addAddress(patient3Address);

		ExpectedEncounter encounter1 = new ExpectedEncounter();
		encounter1.setName(anc1);
		encounter1.setPatient(patient1);
		ExpectedEncounter encounter2 = new ExpectedEncounter();
		encounter2.setName(anc2);
		encounter2.setPatient(patient2);
		ExpectedEncounter encounter3 = new ExpectedEncounter();
		encounter3.setName(anc2);
		encounter3.setPatient(patient3);

		List<ExpectedEncounter> defaultedEncounters = new ArrayList<ExpectedEncounter>();
		defaultedEncounters.add(encounter1);
		defaultedEncounters.add(encounter2);
		defaultedEncounters.add(encounter3);

		ExpectedObs obs1 = new ExpectedObs();
		obs1.setName(tt1);
		obs1.setPatient(patient1);
		ExpectedObs obs2 = new ExpectedObs();
		obs2.setName(tt2);
		obs2.setPatient(patient2);
		ExpectedObs obs3 = new ExpectedObs();
		obs3.setName(tt2);
		obs3.setPatient(patient3);

		List<ExpectedObs> defaultedObs = new ArrayList<ExpectedObs>();
		defaultedObs.add(obs1);
		defaultedObs.add(obs2);
		defaultedObs.add(obs3);

		expect(registrarBean.getCommunityByPatient(patient1)).andReturn(comm1)
				.times(2);
		expect(registrarBean.getCommunityByPatient(patient2)).andReturn(comm2)
				.times(2);
		expect(registrarBean.getCommunityByPatient(patient3)).andReturn(comm3)
				.times(2);

		replay(registrarBean);

		Care[] cares = modelConverter.defaultedToWebServiceCares(
				defaultedEncounters, defaultedObs);

		verify(registrarBean);

		assertEquals(4, cares.length);

		Care care1 = cares[0];
		assertEquals(anc1, care1.getName());
		assertEquals(1, care1.getPatients().length);

		Patient care1Patient = care1.getPatients()[0];
		assertEquals(patient1Id, care1Patient.getMotechId());
		assertEquals(patient1Pref, care1Patient.getPreferredName());
		assertEquals(patient1Last, care1Patient.getLastName());
		assertEquals(patient1Birth, care1Patient.getBirthDate());
		assertEquals(Gender.FEMALE, care1Patient.getSex());
		assertEquals(patient1Community, care1Patient.getCommunity());

		Care care2 = cares[1];
		assertEquals(anc2, care2.getName());
		assertEquals(2, care2.getPatients().length);

		Patient care2Patient1 = care2.getPatients()[0];
		assertEquals(patient2Id, care2Patient1.getMotechId());
		assertEquals(patient2Pref, care2Patient1.getPreferredName());
		assertEquals(patient2Last, care2Patient1.getLastName());
		assertEquals(patient2Birth, care2Patient1.getBirthDate());
		assertEquals(Gender.MALE, care2Patient1.getSex());
		assertEquals(patient2Community, care2Patient1.getCommunity());

		Patient care2Patient2 = care2.getPatients()[1];
		assertEquals(patient3Id, care2Patient2.getMotechId());
		assertEquals(patient3Pref, care2Patient2.getPreferredName());
		assertEquals(patient3Last, care2Patient2.getLastName());
		assertEquals(patient3Birth, care2Patient2.getBirthDate());
		assertEquals(Gender.FEMALE, care2Patient2.getSex());
		assertEquals(patient3Community, care2Patient2.getCommunity());

		Care care3 = cares[2];
		assertEquals(tt1, care3.getName());
		assertEquals(1, care3.getPatients().length);

		Patient care3Patient = care3.getPatients()[0];
		assertEquals(patient1Id, care3Patient.getMotechId());
		assertEquals(patient1Pref, care3Patient.getPreferredName());
		assertEquals(patient1Last, care3Patient.getLastName());
		assertEquals(patient1Birth, care3Patient.getBirthDate());
		assertEquals(Gender.FEMALE, care3Patient.getSex());
		assertEquals(patient1Community, care3Patient.getCommunity());

		Care care4 = cares[3];
		assertEquals(tt2, care4.getName());
		assertEquals(2, care4.getPatients().length);

		Patient care4Patient1 = care4.getPatients()[0];
		assertEquals(patient2Id, care4Patient1.getMotechId());
		assertEquals(patient2Pref, care4Patient1.getPreferredName());
		assertEquals(patient2Last, care4Patient1.getLastName());
		assertEquals(patient2Birth, care4Patient1.getBirthDate());
		assertEquals(Gender.MALE, care4Patient1.getSex());
		assertEquals(patient2Community, care4Patient1.getCommunity());

		Patient care4Patient2 = care4.getPatients()[1];
		assertEquals(patient3Id, care4Patient2.getMotechId());
		assertEquals(patient3Pref, care4Patient2.getPreferredName());
		assertEquals(patient3Last, care4Patient2.getLastName());
		assertEquals(patient3Birth, care4Patient2.getBirthDate());
		assertEquals(Gender.FEMALE, care4Patient2.getSex());
		assertEquals(patient3Community, care4Patient2.getCommunity());
	}

	public void testUpcomingEncounters() {
		String anc1 = "ANC1", pnc2 = "PNC2", pnc3 = "PNC3";
		Calendar calendar = Calendar.getInstance();
		calendar.set(2010, Calendar.JANUARY, 1);
		Date encounter1Date = calendar.getTime();
		calendar.set(2010, Calendar.FEBRUARY, 2);
		Date encounter2Date = calendar.getTime();
		calendar.set(2010, Calendar.MARCH, 3);
		Date encounter3Date = calendar.getTime();

		ExpectedEncounter encounter1 = new ExpectedEncounter();
		encounter1.setName(anc1);
		encounter1.setDueEncounterDatetime(encounter1Date);

		ExpectedEncounter encounter2 = new ExpectedEncounter();
		encounter2.setName(pnc2);
		encounter2.setDueEncounterDatetime(encounter2Date);

		ExpectedEncounter encounter3 = new ExpectedEncounter();
		encounter3.setName(pnc3);
		encounter3.setDueEncounterDatetime(encounter3Date);

		List<ExpectedEncounter> upcomingEncounters = new ArrayList<ExpectedEncounter>();
		upcomingEncounters.add(encounter1);
		upcomingEncounters.add(encounter2);
		upcomingEncounters.add(encounter3);

		Care[] cares = modelConverter
				.upcomingEncountersToWebServiceCares(upcomingEncounters);

		assertEquals(3, cares.length);

		Care care1 = cares[0];
		assertEquals(anc1, care1.getName());
		assertEquals(encounter1Date, care1.getDate());

		Care care2 = cares[1];
		assertEquals(pnc2, care2.getName());
		assertEquals(encounter2Date, care2.getDate());

		Care care3 = cares[2];
		assertEquals(pnc3, care3.getName());
		assertEquals(encounter3Date, care3.getDate());
	}

	public void testUpcomingEncountersEmpty() {
		List<ExpectedEncounter> upcomingEncounters = new ArrayList<ExpectedEncounter>();

		Care[] cares = modelConverter
				.upcomingEncountersToWebServiceCares(upcomingEncounters);

		assertEquals(0, cares.length);
	}

	public void testUpcomingObs() {
		String tt1 = "TT1", opv2 = "OPV2", penta3 = "Penta3";
		Calendar calendar = Calendar.getInstance();
		calendar.set(2010, Calendar.JANUARY, 1);
		Date obs1Date = calendar.getTime();
		calendar.set(2010, Calendar.FEBRUARY, 2);
		Date obs2Date = calendar.getTime();
		calendar.set(2010, Calendar.MARCH, 3);
		Date obs3Date = calendar.getTime();

		ExpectedObs obs1 = new ExpectedObs();
		obs1.setName(tt1);
		obs1.setDueObsDatetime(obs1Date);

		ExpectedObs obs2 = new ExpectedObs();
		obs2.setName(opv2);
		obs2.setDueObsDatetime(obs2Date);

		ExpectedObs obs3 = new ExpectedObs();
		obs3.setName(penta3);
		obs3.setDueObsDatetime(obs3Date);

		List<ExpectedObs> upcomingObs = new ArrayList<ExpectedObs>();
		upcomingObs.add(obs1);
		upcomingObs.add(obs2);
		upcomingObs.add(obs3);

		Care[] cares = modelConverter.upcomingObsToWebServiceCares(upcomingObs);

		assertEquals(3, cares.length);

		Care care1 = cares[0];
		assertEquals(tt1, care1.getName());
		assertEquals(obs1Date, care1.getDate());

		Care care2 = cares[1];
		assertEquals(opv2, care2.getName());
		assertEquals(obs2Date, care2.getDate());

		Care care3 = cares[2];
		assertEquals(penta3, care3.getName());
		assertEquals(obs3Date, care3.getDate());
	}

	public void testUpcomingObsEmpty() {
		List<ExpectedObs> upcomingObs = new ArrayList<ExpectedObs>();

		Care[] cares = modelConverter.upcomingObsToWebServiceCares(upcomingObs);

		assertEquals(0, cares.length);
	}

	public void testUpcoming() {
		String anc1 = "ANC1", pnc2 = "PNC2", pnc3 = "PNC3";
		String tt1 = "TT1", opv2 = "OPV2", penta3 = "Penta3";

		Calendar calendar = Calendar.getInstance();
		calendar.set(2010, Calendar.JANUARY, 1);
		Date encounter1Date = calendar.getTime();
		calendar.set(2010, Calendar.FEBRUARY, 2);
		Date encounter2Date = calendar.getTime();
		calendar.set(2010, Calendar.MARCH, 3);
		Date encounter3Date = calendar.getTime();

		calendar.set(2010, Calendar.JANUARY, 2);
		Date obs1Date = calendar.getTime();
		calendar.set(2010, Calendar.FEBRUARY, 3);
		Date obs2Date = calendar.getTime();
		calendar.set(2010, Calendar.MARCH, 4);
		Date obs3Date = calendar.getTime();

		ExpectedEncounter encounter1 = new ExpectedEncounter();
		encounter1.setName(anc1);
		encounter1.setDueEncounterDatetime(encounter1Date);

		ExpectedEncounter encounter2 = new ExpectedEncounter();
		encounter2.setName(pnc2);
		encounter2.setDueEncounterDatetime(encounter2Date);

		ExpectedEncounter encounter3 = new ExpectedEncounter();
		encounter3.setName(pnc3);
		encounter3.setDueEncounterDatetime(encounter3Date);

		List<ExpectedEncounter> upcomingEncounters = new ArrayList<ExpectedEncounter>();
		upcomingEncounters.add(encounter1);
		upcomingEncounters.add(encounter2);
		upcomingEncounters.add(encounter3);

		ExpectedObs obs1 = new ExpectedObs();
		obs1.setName(tt1);
		obs1.setDueObsDatetime(obs1Date);

		ExpectedObs obs2 = new ExpectedObs();
		obs2.setName(opv2);
		obs2.setDueObsDatetime(obs2Date);

		ExpectedObs obs3 = new ExpectedObs();
		obs3.setName(penta3);
		obs3.setDueObsDatetime(obs3Date);

		List<ExpectedObs> upcomingObs = new ArrayList<ExpectedObs>();
		upcomingObs.add(obs1);
		upcomingObs.add(obs2);
		upcomingObs.add(obs3);

		Care[] cares = modelConverter.upcomingToWebServiceCares(
				upcomingEncounters, upcomingObs, false);

		assertEquals(6, cares.length);

		Care care1 = cares[0];
		assertEquals(anc1, care1.getName());
		assertEquals(encounter1Date, care1.getDate());

		Care care2 = cares[1];
		assertEquals(tt1, care2.getName());
		assertEquals(obs1Date, care2.getDate());

		Care care3 = cares[2];
		assertEquals(pnc2, care3.getName());
		assertEquals(encounter2Date, care3.getDate());

		Care care4 = cares[3];
		assertEquals(opv2, care4.getName());
		assertEquals(obs2Date, care4.getDate());

		Care care5 = cares[4];
		assertEquals(pnc3, care5.getName());
		assertEquals(encounter3Date, care5.getDate());

		Care care6 = cares[5];
		assertEquals(penta3, care6.getName());
		assertEquals(obs3Date, care6.getDate());
	}
}
