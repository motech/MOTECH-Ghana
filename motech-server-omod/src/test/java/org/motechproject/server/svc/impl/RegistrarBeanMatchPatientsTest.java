package org.motechproject.server.svc.impl;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.motechproject.server.model.Community;
import org.motechproject.server.omod.MotechModuleActivator;
import org.motechproject.server.omod.MotechService;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.DayOfWeek;
import org.motechproject.ws.Gender;
import org.motechproject.ws.HowLearned;
import org.motechproject.ws.InterestReason;
import org.motechproject.ws.MediaType;
import org.motechproject.ws.RegistrantType;
import org.motechproject.ws.RegistrationMode;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

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
			Date date = new Date();

			Community community = regService.getCommunityById(communityId);

			regService.registerPatient(RegistrationMode.USE_PREPRINTED_ID,
					motechId, RegistrantType.PREGNANT_MOTHER, firstName,
					middleName, lastName, prefName, date, false, Gender.FEMALE,
					true, nhisNumber, date, null, community, "Address",
					phoneNumber, date, true, 0, 0, true, true,
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
					date, communityId, phoneNumber, nhisNumber, motechId
							.toString());
			assertEquals(1, matches.size());

			// Match on NHIS number (duplicate)
			matches = regService.getDuplicatePatients(null, null, null, null,
					null, null, nhisNumber, null);
			assertEquals(1, matches.size());

			// Match on NHIS number (any)
			matches = regService.getPatients(null, null, null, null, null,
					null, nhisNumber, null);
			assertEquals(1, matches.size());

			// Match on first name, last name, and birthdate (duplicate)
			matches = regService.getDuplicatePatients(firstName, lastName,
					null, date, null, null, null, null);
			assertEquals(1, matches.size());

			// Match on first name, last name, and birthdate (any)
			matches = regService.getPatients(firstName, lastName, null, date,
					null, null, null, null);
			assertEquals(1, matches.size());

			// Match on first name, last name, and community (duplicate)
			matches = regService.getDuplicatePatients(firstName, lastName,
					null, null, communityId, null, null, null);
			assertEquals(1, matches.size());

			// Match on first name, last name, and community (any)
			matches = regService.getPatients(firstName, lastName, null, null,
					communityId, null, null, null);
			assertEquals(1, matches.size());

			// Match on first name, last name, and phone (duplicate)
			matches = regService.getDuplicatePatients(firstName, lastName,
					null, null, null, phoneNumber, null, null);
			assertEquals(1, matches.size());

			// Match on first name, last name, and phone (any)
			matches = regService.getPatients(firstName, lastName, null, null,
					null, phoneNumber, null, null);
			assertEquals(1, matches.size());

			// Match on MotechID (duplicate)
			matches = regService.getDuplicatePatients(null, null, null, null,
					null, null, null, motechId.toString());
			assertEquals(1, matches.size());

			// Match on MotechID (any)
			matches = regService.getPatients(null, null, null, null, null,
					null, null, motechId.toString());
			assertEquals(1, matches.size());

			// No match on different NHIS number (duplicate)
			matches = regService.getDuplicatePatients(null, null, null, null,
					null, null, "DifferentNHISValue", null);
			assertEquals(0, matches.size());

			// No match on different NHIS number (any)
			matches = regService.getPatients(null, null, null, null, null,
					null, "DifferentNHISValue", null);
			assertEquals(0, matches.size());

			// No match on last name, birthdate, and different first name
			// (duplicate)
			matches = regService.getDuplicatePatients("DifferentFirstName",
					lastName, null, date, null, null, null, null);
			assertEquals(0, matches.size());

			// No match on last name, birthdate, and different first name (any)
			matches = regService.getPatients("DifferentFirstName", lastName,
					null, date, null, null, null, null);
			assertEquals(0, matches.size());

			// No match on first name, community, and different last name
			// (duplicate)
			matches = regService.getDuplicatePatients(firstName,
					"DifferentLastName", null, null, communityId, null, null,
					null);
			assertEquals(0, matches.size());

			// No match on first name, community, and different last name (any)
			matches = regService.getPatients(firstName, "DifferentLastName",
					null, null, communityId, null, null, null);
			assertEquals(0, matches.size());

			// No match on first name, last name, and different phone number
			// (duplicate)
			matches = regService.getDuplicatePatients(firstName, lastName,
					null, null, null, "4534656", null, null);
			assertEquals(0, matches.size());

			// No match on first name, last name, and different phone number
			// (any)
			matches = regService.getPatients(firstName, lastName, null, null,
					null, "4534656", null, null);
			assertEquals(0, matches.size());

			// No matches on empty (duplicate)
			matches = regService.getDuplicatePatients(null, null, null, null,
					null, null, null, null);
			assertEquals(0, matches.size());

			// Matches on empty, returns all patients (any)
			matches = regService.getPatients(null, null, null, null, null,
					null, null, null);
			assertEquals(3, matches.size());

			// Match on partial first name and partial last name (any)
			matches = regService.getPatients("Fir", "Name", null, null, null,
					null, null, null);
			assertEquals(1, matches.size());

			// Match on partial pref name and partial last name (any)
			matches = regService.getPatients(null, "stNa", "refNa", null, null,
					null, null, null);
			assertEquals(1, matches.size());

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
					null, null, null, null);
			assertEquals(1, matches.size());

		} finally {
			Context.closeSession();
		}
	}

}
