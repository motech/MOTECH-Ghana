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

package org.motechproject.server.svc.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.motechproject.server.model.Community;
import org.motechproject.server.model.Facility;
import org.motechproject.server.omod.MotechModuleActivator;
import org.motechproject.server.omod.MotechService;
import org.motechproject.server.svc.BirthOutcomeChild;
import org.motechproject.server.svc.OpenmrsBean;
import org.motechproject.server.svc.RegistrarBean;
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
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

/**
 * BaseModuleContextSensitiveTest loads both the OpenMRS core and module spring
 * contexts and hibernate mappings, providing the OpenMRS Context for both
 * OpenMRS core and module services.
 */
public class RegistrarBeanRecordVisitsTest extends
		BaseModuleContextSensitiveTest {

	static MotechModuleActivator activator;

	@BeforeClass
	public static void setUpClass() throws Exception {
		activator = new MotechModuleActivator(false);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		activator = null;
	}

	@Before
	public void setup() throws Exception {
		// Perform same steps as BaseSetup (initializeInMemoryDatabase,
		// executeDataSet, authenticate), except load custom XML dataset
		initializeInMemoryDatabase();

		// Created from org.openmrs.test.CreateInitialDataSet
		// without line for "concept_synonym" table (not exist)
		// using 1.4.4-createdb-from-scratch-with-demo-data.sql
		// Removed all empty short_name="" from concepts
		// Added missing description to relationship_type
		// Removed all patients and related patient/person info (id 2-500)
		executeDataSet("initial-openmrs-dataset.xml");

		// Add example Location, Facility and Community
		executeDataSet("facility-community-dataset.xml");

		authenticate();

		activator.startup();
	}

	@After
	public void tearDown() throws Exception {
		activator.shutdown();
	}

	@Test
	@SkipBaseSetup
	public void testRecordVisits() {

		try {
			Context.openSession();
			Context.authenticate("admin", "test");

			MotechService motechService = Context
					.getService(MotechService.class);
			RegistrarBean regService = motechService.getRegistrarBean();
			OpenmrsBean openmrsService = motechService.getOpenmrsBean();

			Facility facility = motechService.getFacilityById(1111);
			Location facilityLocation = facility.getLocation();
			Community community = motechService.getCommunityById(11111);

			Integer mother1Id = 1234575;
			Integer mother2Id = 1234581;
			Integer child1Id = 1234599;
			Integer child2Id = 1234608;
			Integer child3Id = 1234612;
			Calendar calendar = Calendar.getInstance();
			Date date = calendar.getTime();
			calendar.add(Calendar.DATE, 5);
			Date dueDate1 = calendar.getTime();
			calendar.add(Calendar.DATE, -20);
			Date dueDate2 = calendar.getTime();

			User staff = regService.registerStaff("Nurse", "Betty",
					"7777777777", "CHO");

			regService.registerPatient(RegistrationMode.USE_PREPRINTED_ID,
					mother1Id, RegistrantType.PREGNANT_MOTHER,
					"Mother1FirstName", "Mother1MiddleName", "Mother1LastName",
					"Mother1PrefName", date, false, Gender.FEMALE, true,
					"nhisNumber1", date, null, community, "Address",
					"1111111111", dueDate1, true, true, true,
					ContactNumberType.PERSONAL, MediaType.TEXT, "language",
					DayOfWeek.MONDAY, date, InterestReason.CURRENTLY_PREGNANT,
					HowLearned.FRIEND, null);

			Patient mother1 = openmrsService.getPatientByMotechId(mother1Id
					.toString());
			assertNotNull("Mother 1 not registered", mother1);
			assertEquals(
					"Registration or Pregnancy visit not added for Mother 1",
					2, Context.getEncounterService().getEncountersByPatient(
							mother1).size());

			regService.registerPatient(RegistrationMode.USE_PREPRINTED_ID,
					mother2Id, RegistrantType.PREGNANT_MOTHER,
					"Mother2FirstName", "Mother2MiddleName", "Mother2LastName",
					"Mother2PrefName", date, false, Gender.FEMALE, true,
					"nhisNumber2", date, null, community, "Address",
					"1111111111", dueDate2, true, true, true,
					ContactNumberType.PERSONAL, MediaType.TEXT, "language",
					DayOfWeek.MONDAY, date, InterestReason.CURRENTLY_PREGNANT,
					HowLearned.FRIEND, null);

			Patient mother2 = openmrsService.getPatientByMotechId(mother2Id
					.toString());
			assertNotNull("Mother 2 not registered", mother2);
			assertEquals(
					"Registration or Pregnancy visit not added for Mother 2",
					2, Context.getEncounterService().getEncountersByPatient(
							mother2).size());

			regService.registerPatient(RegistrationMode.USE_PREPRINTED_ID,
					child1Id, RegistrantType.CHILD_UNDER_FIVE,
					"Child1FirstName", "Child1MiddleName", "Child1LastName",
					"Child1PrefName", date, false, Gender.FEMALE, true,
					"nhisNumber3", date, null, null, "Address", "1111111111",
					null, null, false, false, ContactNumberType.PERSONAL,
					MediaType.TEXT, "language", DayOfWeek.MONDAY, date,
					InterestReason.FAMILY_FRIEND_PREGNANT, HowLearned.FRIEND,
					null);

			Patient child1 = openmrsService.getPatientByMotechId(child1Id
					.toString());
			assertNotNull("Child 1 not registered", child1);
			assertEquals("Registration visit not added for Child 1", 1, Context
					.getEncounterService().getEncountersByPatient(child1)
					.size());

			assertEquals("3 new patients not registered", 5, Context
					.getPatientService().getAllPatients().size());

			// Query for Mother 1 upcoming pregnancy due date
			List<Obs> upcomingDueDate = regService
					.getUpcomingPregnanciesDueDate(facility);
			assertEquals(1, upcomingDueDate.size());
			assertEquals(dueDate1, upcomingDueDate.get(0).getValueDatetime());
			assertEquals(mother1.getPatientId(), upcomingDueDate.get(0)
					.getPerson().getPersonId());

			// Query for Mother 2 overdue pregnancy due date
			List<Obs> overdueDueDate = regService
					.getOverduePregnanciesDueDate(facility);
			assertEquals(1, overdueDueDate.size());
			assertEquals(dueDate2, overdueDueDate.get(0).getValueDatetime());
			assertEquals(mother2.getPatientId(), overdueDueDate.get(0)
					.getPerson().getPersonId());

			calendar.setTime(date);
			calendar.add(Calendar.MONTH, 6);
			Date newDueDate = calendar.getTime();

			// ANC Visit for Mother 1
			regService.recordMotherANCVisit(staff, facilityLocation, date,
					mother1, 1, 1, "House", "Community", newDueDate, 1, 1, 1.0,
					1, 1, false, true, 1.0, 1, 0, 56464, 1.0, false, false,
					false, true, false, false, HIVResult.NO_TEST, false, false,
					false, date, "Comments");

			assertEquals("ANC visit not added for Mother 1", 3, Context
					.getEncounterService().getEncountersByPatient(mother1)
					.size());
			Date currentDueDate = regService.getActivePregnancyDueDate(mother1
					.getPatientId());
			assertEquals("EDD not updated in ANC visit", newDueDate,
					currentDueDate);

			calendar.add(Calendar.MONTH, 1);
			newDueDate = calendar.getTime();

			// ANC Registration for Mother 1
			regService.registerANCMother(staff, facilityLocation, date,
					mother1, "ANC2", newDueDate, 45.2, 1, 0, false, false,
					null, null, null, null, null, null, null);

			assertEquals("ANC registration not added for Mother 1", 4, Context
					.getEncounterService().getEncountersByPatient(mother1)
					.size());
			currentDueDate = regService.getActivePregnancyDueDate(mother1
					.getPatientId());
			assertEquals("EDD not updated in ANC registration", newDueDate,
					currentDueDate);

			// Pregnancy Delivery for Mother 1, Adding Child 2
			List<BirthOutcomeChild> outcomes = new ArrayList<BirthOutcomeChild>();
			outcomes.add(new BirthOutcomeChild(BirthOutcome.A,
					RegistrationMode.USE_PREPRINTED_ID, child2Id, Gender.MALE,
					"Child2FirstName", 2.5));
			outcomes.add(new BirthOutcomeChild(BirthOutcome.FSB,
					RegistrationMode.USE_PREPRINTED_ID, child3Id, Gender.MALE,
					"Child3FirstName", 3.0));
			List<Patient> childPatients = regService.recordPregnancyDelivery(
					staff, facilityLocation, date, mother1, 1, 1, 1, 1, true,
					new Integer[] { 1, 2, 3 }, 1, false, "Comments", outcomes);

			assertEquals("Pregnancy delivery not added for Mother 1", 5,
					Context.getEncounterService().getEncountersByPatient(
							mother1).size());
			Obs mother1Pregnancy = regService.getActivePregnancy(mother1
					.getPatientId());
			assertNull("Pregnancy is still active after delivery",
					mother1Pregnancy);
			assertEquals("Child 2 not added", 6, Context.getPatientService()
					.getAllPatients().size());

			// Confirm return value of pregnancy delivery includes alive child
			assertEquals(1, childPatients.size());
			Patient registeredChild = childPatients.get(0);
			assertEquals(child2Id.toString(), registeredChild
					.getPatientIdentifier().getIdentifier());
			assertEquals("Child2FirstName", registeredChild.getGivenName());

			Patient child2 = openmrsService.getPatientByMotechId(child2Id
					.toString());
			assertNotNull("Child 2 not registered", child2);
			assertEquals("Birth or Registration visit not added for Child 2",
					2, Context.getEncounterService().getEncountersByPatient(
							child2).size());
			String[] child2Enrollments = regService
					.getActiveMessageProgramEnrollmentNames(child2);
			assertNotNull("Enrollments do not exist for Child 2",
					child2Enrollments);
			assertEquals(2, child2Enrollments.length);

			// Query for Delivery
			List<Encounter> recentDeliveryEnc = regService
					.getRecentDeliveries(facility);
			assertEquals(1, recentDeliveryEnc.size());
			assertEquals(mother1, recentDeliveryEnc.get(0).getPatient());

			// PNC Visit for Mother 2
			regService.recordMotherPNCVisit(staff, facilityLocation, date,
					mother2, 1, 1, "House", "Community", false, true, true, 1,
					1, false, false, 36.0, 100.0, "Comments");

			assertEquals("PNC visit not added for Mother 2", 3, Context
					.getEncounterService().getEncountersByPatient(mother2)
					.size());

			// General Visit for Mother 2
			regService.recordOutpatientVisit(staff, facilityLocation, date,
					mother2, "Mother2GeneralId", true, 1, 2, true, true, true,
					false, false, "Comments");

			assertEquals("General visit not added for Mother 2", 4, Context
					.getEncounterService().getEncountersByPatient(mother2)
					.size());

			// Pregnancy Termination for Mother 2 (with maternal death)
			regService.recordPregnancyTermination(staff, facilityLocation,
					currentDueDate, mother2, 1, 1, new Integer[] { 1, 2, 3 },
					true, false, false, false, "Comments");

			assertEquals("Pregnancy termination not added for Mother 2", 5,
					Context.getEncounterService().getEncountersByPatient(
							mother2).size());
			Obs mother2Pregnancy = regService.getActivePregnancy(mother2
					.getPatientId());
			assertNull("Pregnancy is still active after termination",
					mother2Pregnancy);
			assertEquals("Mother 2 not voided", 5, Context.getPatientService()
					.getAllPatients().size());

			// CWC Visit for Child 2
			regService.recordChildCWCVisit(staff, facilityLocation, date,
					child2, 1, "House", "Community", true, 1, 1, true, true,
					true, true, true, true, 25.0, 5.0, 35.0, true, "Comments");

			assertEquals("CWC visit not added for Child 2", 3, Context
					.getEncounterService().getEncountersByPatient(child2)
					.size());

			// PNC Visit for Child 2
			regService.recordChildPNCVisit(staff, facilityLocation, date,
					child2, 1, 1, "House", "Community", false, true, 7.0, 36.0,
					true, true, 140, true, true, "Comments");

			assertEquals("PNC visit not added for Child 2", 4, Context
					.getEncounterService().getEncountersByPatient(child2)
					.size());

			// General Visit for Child 1
			regService.recordOutpatientVisit(staff, facilityLocation, date,
					child1, "Child1GeneralId", true, 4, 5, true, true, true,
					false, false, "Comments");

			assertEquals("General visit not added for Child 1", 2, Context
					.getEncounterService().getEncountersByPatient(child1)
					.size());

			calendar.set(2010, 0, 1, 0, 0, 0); // Jan 1, 2010
			date = calendar.getTime();
			calendar.set(1993, 0, 4, 0, 0, 0); // Jan 4, 1993
			Date ttDate = calendar.getTime();
			calendar.set(1994, 1, 2, 0, 0, 0); // Feb 2, 1994
			Date iptDate = calendar.getTime();
			calendar.set(2006, 11, 3, 0, 0, 0); // Dec 3, 2006
			Date bcgDate = calendar.getTime();
			calendar.set(2005, 2, 5, 0, 0, 0); // Mar 5, 2005
			Date opvDate = calendar.getTime();
			calendar.set(2004, 3, 7, 0, 0, 0); // Apr 7, 2004
			Date pentaDate = calendar.getTime();
			calendar.set(2000, 4, 10, 0, 0, 0); // May 10, 2000
			Date measlesDate = calendar.getTime();
			calendar.set(2001, 5, 15, 0, 0, 0); // Jun 15, 2001
			Date yellowDate = calendar.getTime();
			calendar.set(2002, 6, 16, 0, 0, 0); // Jul 16, 2002
			Date iptiDate = calendar.getTime();
			calendar.set(2003, 7, 17, 0, 0, 0); // Aug 17, 2003
			Date vitDate = calendar.getTime();

			regService.recordPatientHistory(staff, facilityLocation, date,
					child1, 2, iptDate, 3, ttDate, bcgDate, 6, opvDate, 7,
					pentaDate, measlesDate, yellowDate, 8, iptiDate, vitDate);

			assertEquals("No patient history added for Child 1", 3, Context
					.getEncounterService().getEncountersByPatient(child1)
					.size());

			// Record Death of Child 1
			regService.recordDeath(staff, facilityLocation, date, child1);

			assertEquals("Deceased child 1 not voided", 4, Context
					.getPatientService().getAllPatients().size());

		} finally {
			Context.closeSession();
		}
	}
}