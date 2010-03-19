package org.motech.svc.impl;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.motech.model.HIVStatus;
import org.motech.model.WhoRegistered;
import org.motech.openmrs.module.MotechModuleActivator;
import org.motech.openmrs.module.MotechService;
import org.motech.svc.RegistrarBean;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.MediaType;
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
public class RegistrarBeanMatchPeopleTest extends
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

			String firstName = "FirstName";
			String middleName = "MiddleName";
			String lastName = "LastName";
			String community = "Community";
			String primaryPhone = "PrimaryPhone";
			String secondaryPhone = "SecondaryPhone";
			String regNumber = "RegNumber";
			String nhisNumber = "NHISNumber";
			Date date = new Date();

			regService.registerPregnantMother(firstName, middleName, lastName,
					"prefName", date, false, true, regNumber, true, nhisNumber,
					date, "region", "district", community, "address", 1, date,
					true, 0, 0, HIVStatus.NEGATIVE, false, primaryPhone,
					ContactNumberType.PERSONAL, secondaryPhone,
					ContactNumberType.PERSONAL, MediaType.TEXT, MediaType.TEXT,
					"languageVoice", "languageText", WhoRegistered.CHPS_STAFF,
					"religion", "occupation");

			assertEquals(3, Context.getPatientService().getAllPatients().size());

			// Match on all
			List<Person> matches = regService.getMatchingPeople(firstName,
					lastName, date, community, primaryPhone, regNumber,
					nhisNumber);
			assertEquals(1, matches.size());

			// Match on NHIS number
			matches = regService.getMatchingPeople(null, null, null, null,
					null, null, nhisNumber);
			assertEquals(1, matches.size());

			// Match on first name, last name, and birthdate
			matches = regService.getMatchingPeople(firstName, lastName, date,
					null, null, null, null);
			assertEquals(1, matches.size());

			// Match on first name, last name, and community
			matches = regService.getMatchingPeople(firstName, lastName, null,
					community, null, null, null);
			assertEquals(1, matches.size());

			// Match on first name, last name, and primaryPhone
			matches = regService.getMatchingPeople(firstName, lastName, null,
					null, primaryPhone, null, null);
			assertEquals(1, matches.size());

			// Match on first name, last name, and secondaryPhone
			matches = regService.getMatchingPeople(firstName, lastName, null,
					null, secondaryPhone, null, null);
			assertEquals(1, matches.size());

			// Match on first name, last name, and regNumber
			matches = regService.getMatchingPeople(firstName, lastName, null,
					null, null, regNumber, null);
			assertEquals(1, matches.size());

			// No match on different NHIS number
			matches = regService.getMatchingPeople(null, null, null, null,
					null, null, "DifferentNHISValue");
			assertEquals(0, matches.size());

			// No match on last name, birthdate, and different first name
			matches = regService.getMatchingPeople("DifferentFirstName",
					lastName, date, null, null, null, null);
			assertEquals(0, matches.size());

			// No match on first name, community, and different last name
			matches = regService.getMatchingPeople(firstName,
					"DifferentLastName", null, community, null, null, null);
			assertEquals(0, matches.size());

			// No match on first name, last name, and different phone number
			matches = regService.getMatchingPeople(firstName, lastName, null,
					null, "DifferentPhoneNumber", null, null);
			assertEquals(0, matches.size());

			// No match on first name, last name, and different regNumber
			matches = regService.getMatchingPeople(firstName, lastName, null,
					null, null, "DifferentPatientRegNumber", null);
			assertEquals(0, matches.size());

			// No matches on empty
			matches = regService.getMatchingPeople(null, null, null, null,
					null, null, null);
			assertEquals(0, matches.size());

			Person person = new Person();
			person.addName(new PersonName(firstName, null, lastName));
			person.setBirthdate(date);
			person.setGender("F");
			Context.getPersonService().savePerson(person);

			// Match Patient and Person on firstName, lastName, birthDate
			matches = regService.getMatchingPeople(firstName, lastName, date,
					community, primaryPhone, regNumber, nhisNumber);
			assertEquals(2, matches.size());

		} finally {
			Context.closeSession();
		}
	}

}
