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

import org.junit.*;
import org.motechproject.server.model.Community;
import org.motechproject.server.model.Facility;
import org.motechproject.server.omod.MotechModuleActivator;
import org.motechproject.server.service.MotechService;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.ws.*;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * BaseModuleContextSensitiveTest loads both the OpenMRS core and module spring
 * contexts and hibernate mappings, providing the OpenMRS Context for both
 * OpenMRS core and module services.
 */
public class RegistrarBeanMatchPatientsTest extends
		BaseModuleContextSensitiveTest {

	static MotechModuleActivator activator;

	@BeforeClass
	public static void setUpClass() throws Exception {
		activator = new MotechModuleActivator();
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
		// Removed all concepts except those in sqldiff
		executeDataSet("initial-openmrs-dataset.xml");

		// Includes Motech data added in sqldiff
		executeDataSet("motech-dataset.xml");
		
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
	public void testMatchingPatients() {

		try {
			Context.openSession();
			Context.authenticate("admin", "test");

			RegistrarBean regService = Context.getService(MotechService.class)
					.getRegistrarBean();

			Integer motechId = 1234620;
			String firstName = "FirstName";
			String middleName = "MiddleName";
			String lastName = "LastName";
			String prefName = "PrefName";
			String phoneNumber = "1111111111";
			String nhisNumber = "NHISNumber";
			Integer communityId = 11111;
			Integer invalidCommunityId = 99999;
			Date date = new Date();

			Community community = regService.getCommunityById(communityId);

            Facility facility = regService.getFacilityById(11117);

            regService.registerPatient(RegistrationMode.USE_PREPRINTED_ID,
					motechId, RegistrantType.PREGNANT_MOTHER, firstName,
					middleName, lastName, prefName, date, false, Gender.FEMALE,
					true, nhisNumber, date, null, community, facility, "Address",
					phoneNumber, date, true, true, true,
					ContactNumberType.PERSONAL, MediaType.TEXT, "language",
					DayOfWeek.MONDAY, date, InterestReason.CURRENTLY_PREGNANT,
					HowLearned.FRIEND, null);

			assertEquals(3, Context.getPatientService().getAllPatients().size());

			// Match on all (duplicate)
			List<Patient> matches = regService.getDuplicatePatients(firstName,
					lastName, prefName, date, communityId, phoneNumber,
					nhisNumber, motechId.toString());
			assertEquals(1, matches.size());

			// Match on all (any)
			matches = regService.getPatients(firstName, lastName, prefName,
					date, facility.getFacilityId(), phoneNumber, nhisNumber, null, motechId
							.toString());
			assertEquals(1, matches.size());

			// Match on NHIS number (duplicate)
			matches = regService.getDuplicatePatients(null, null, null, null,
					null, null, nhisNumber, null);
			assertEquals(1, matches.size());

			// Match on NHIS number (any)
			matches = regService.getPatients(null, null, null, null, null, null,
					nhisNumber, null, null);
			assertEquals(1, matches.size());

			// Match on first name, last name, and birthdate (duplicate)
			matches = regService.getDuplicatePatients(firstName, lastName,
					null, date, null, null, null, null);
			assertEquals(1, matches.size());

			// Match on first name, last name, and birthdate (any)
			matches = regService.getPatients(firstName, lastName, null, date,
					null, null, null, null, null);
			assertEquals(1, matches.size());

			// Match on first name, last name, and community (duplicate)
			matches = regService.getDuplicatePatients(firstName, lastName,
					null, null, facility.getFacilityId(), null, null, null);
			assertEquals(1, matches.size());

			// Match on first name, last name, and community (any)
			matches = regService.getPatients(firstName, lastName, null, null,
					facility.getFacilityId(), null, null, null, null);
			assertEquals(1, matches.size());

			// Match on first name, last name, and phone (duplicate)
			matches = regService.getDuplicatePatients(firstName, lastName,
					null, null, null, phoneNumber, null, null);
			assertEquals(1, matches.size());

			// Match on first name, last name, and phone (any)
			matches = regService.getPatients(firstName, lastName, null, null,
					null, phoneNumber, null, null, null);
			assertEquals(1, matches.size());

			// Match on MotechID (duplicate)
			matches = regService.getDuplicatePatients(null, null, null, null,
					null, null, null, motechId.toString());
			assertEquals(1, matches.size());

			// Match on MotechID (any)
			matches = regService.getPatients(null, null, null, null, null,
					null, null, null, motechId.toString());
			assertEquals(1, matches.size());

			// No match on different NHIS number (duplicate)
			matches = regService.getDuplicatePatients(null, null, null, null,
					null, null, "DifferentNHISValue", null);
			assertEquals(0, matches.size());

			// No match on different NHIS number (any)
			matches = regService.getPatients(null, null, null, null, null,
					null, "DifferentNHISValue", null, null);
			assertEquals(0, matches.size());

			// No match on last name, birthdate, and different first name
			// (duplicate)
			matches = regService.getDuplicatePatients("DifferentFirstName",
					lastName, null, date, null, null, null, null);
			assertEquals(0, matches.size());

			// No match on last name, birthdate, and different first name (any)
			matches = regService.getPatients("DifferentFirstName", lastName,
					null, date, null, null, null, null, null);
			assertEquals(0, matches.size());

			// No match on first name, community, and different last name
			// (duplicate)
			matches = regService.getDuplicatePatients(firstName,
					"DifferentLastName", null, null, communityId, null, null,
					null);
			assertEquals(0, matches.size());

			// No match on first name, community, and different last name (any)
			matches = regService.getPatients(firstName, "DifferentLastName",
					null, null, communityId, null, null, null, null);
			assertEquals(0, matches.size());

			// No match on first name, last name, and different phone number
			// (duplicate)
			matches = regService.getDuplicatePatients(firstName, lastName,
					null, null, null, "4534656", null, null);
			assertEquals(0, matches.size());

			// No match on first name, last name, and different phone number
			// (any)
			matches = regService.getPatients(firstName, lastName, null, null,
					null, "4534656", null, null, null);
			assertEquals(0, matches.size());

			// No matches on empty (duplicate)
			matches = regService.getDuplicatePatients(null, null, null, null,
					null, null, null, null);
			assertEquals(0, matches.size());

			// Matches on empty, returns all patients (any)
			matches = regService.getPatients(null, null, null, null, null, null,
					null, null, null);
			assertEquals(3, matches.size());

			// Match on partial first name and partial last name (any)
			matches = regService.getPatients("Fir", "Name", null, null, null,
					null, null, null, null);
			assertEquals(1, matches.size());

			// Match on partial pref name and partial last name (any)
			matches = regService.getPatients(null, "stNa", "refNa", null, null,
					null, null, null, null);
			assertEquals(1, matches.size());

			// Match on communityId
			matches = regService.getPatients(null, null, null, null, null,
					null, null, communityId, null);
			assertEquals(1, matches.size());

			// No Match on communityId
			matches = regService.getPatients(null, null, null, null, null,
					null, null, invalidCommunityId, null);
			assertEquals(0, matches.size());

			Person person = new Person();
			person.addName(new PersonName(firstName, null, lastName));
			person.setBirthdate(date);
			person.setGender("F");
			Context.getPersonService().savePerson(person);

			// No match for Person on firstName, lastName, birthDate (duplicate)
			matches = regService.getDuplicatePatients(firstName, lastName,
					null, date, null, null, null, null);
			assertEquals(1, matches.size());

			// No match for Person on firstName, lastName, birthDate (any)
			matches = regService.getPatients(firstName, lastName, null, date,
					null, null, null, null, null);
			assertEquals(1, matches.size());

		} finally {
			Context.closeSession();
		}
	}

}
