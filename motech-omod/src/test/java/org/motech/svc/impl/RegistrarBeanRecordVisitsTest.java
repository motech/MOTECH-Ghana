package org.motech.svc.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.motech.model.HIVStatus;
import org.motech.model.WhoRegistered;
import org.motech.openmrs.module.MotechModuleActivator;
import org.motech.openmrs.module.MotechService;
import org.motech.svc.BirthOutcomeChild;
import org.motech.svc.RegistrarBean;
import org.motechproject.ws.BirthOutcome;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.DeliveredBy;
import org.motechproject.ws.Gender;
import org.motechproject.ws.MediaType;
import org.openmrs.Obs;
import org.openmrs.Patient;
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

			RegistrarBean regService = Context.getService(MotechService.class)
					.getRegistrarBean();

			String mother1Id = "MotherRegNumber1";
			String mother2Id = "MotherRegNumber2";
			String child1Id = "ChildRegNumber1";
			String child2Id = "ChildRegNumber2";
			String child3Id = "ChildRegNumber3";
			String facilityId = "FacilityId";
			Date date = new Date();

			regService.registerPregnantMother("Mother1FirstName",
					"Mother1MiddleName", "Mother1LastName", "Mother1PrefName",
					date, false, true, mother1Id, true, "nhisNumber1", date,
					"region", "district", "community", "address", 1, date,
					true, 0, 0, HIVStatus.NEGATIVE, false, "primaryPhone",
					ContactNumberType.PERSONAL, "secondaryPhone",
					ContactNumberType.PERSONAL, MediaType.TEXT, MediaType.TEXT,
					"languageVoice", "languageText", WhoRegistered.CHPS_STAFF,
					"religion", "occupation");

			Patient mother1 = regService.getPatientBySerial(mother1Id);
			assertNotNull("Mother 1 not registered", mother1);

			regService.registerPregnantMother("Mother2FirstName",
					"Mother2MiddleName", "Mother2LastName", "Mother2PrefName",
					date, false, true, mother2Id, true, "nhisNumber2", date,
					"region", "district", "community", "address", 1, date,
					true, 0, 0, HIVStatus.NEGATIVE, false, "primaryPhone",
					ContactNumberType.PERSONAL, "secondaryPhone",
					ContactNumberType.PERSONAL, MediaType.TEXT, MediaType.TEXT,
					"languageVoice", "languageText", WhoRegistered.CHPS_STAFF,
					"religion", "occupation");

			Patient mother2 = regService.getPatientBySerial(mother2Id);
			assertNotNull("Mother 2 not registered", mother2);

			regService.registerChild("Child1FirstName", "Child1MiddleName",
					"Child1LastName", "Child1PrefName", date, false,
					Gender.FEMALE, mother1Id, true, child1Id, true,
					"nhisNumber3", date, "region", "district", "community",
					"address", 1, false, "primaryPhone",
					ContactNumberType.PERSONAL, "secondaryPhone",
					ContactNumberType.PERSONAL, MediaType.TEXT, MediaType.TEXT,
					"languageVoice", "languageText", WhoRegistered.CHPS_STAFF);

			Patient child1 = regService.getPatientBySerial(child1Id);
			assertNotNull("Child 1 not registered", child1);

			assertEquals("3 new patients not registered", 5, Context
					.getPatientService().getAllPatients().size());

			// ANC Visit for Mother 1
			regService.recordMotherANCVisit(facilityId, date, mother1, 1, 1, 1,
					true, org.motechproject.ws.HIVStatus.N);

			assertEquals("ANC visit not added for Mother 1", 1, Context
					.getEncounterService().getEncountersByPatient(mother1)
					.size());

			// Pregnancy Delivery for Mother 1, Adding Child 2
			BirthOutcomeChild[] outcomes = new BirthOutcomeChild[] {
					new BirthOutcomeChild(BirthOutcome.A, child2Id,
							Gender.MALE, "Child2FirstName", true, true),
					new BirthOutcomeChild(BirthOutcome.FSB, child3Id,
							Gender.MALE, "Child3FirstName", true, true) };
			regService.recordPregnancyDelivery(facilityId, date, mother1, 1, 1,
					1, DeliveredBy.CHO, false, 1, outcomes);

			assertEquals("Pregnancy delivery not added for Mother 1", 2,
					Context.getEncounterService().getEncountersByPatient(
							mother1).size());
			Obs mother1Pregnancy = regService.getActivePregnancy(mother1
					.getPatientId());
			assertNull("Pregnancy is still active after delivery",
					mother1Pregnancy);
			assertEquals("Child 2 and Child 3 not added", 7, Context
					.getPatientService().getAllPatients(true).size());
			assertEquals("Child 3 not voided", 6, Context.getPatientService()
					.getAllPatients().size());

			Patient child2 = regService.getPatientBySerial(child2Id);
			assertNotNull("Child 2 not registered", child2);
			assertEquals("PNC visit at birth not added for Child 2", 1, Context
					.getEncounterService().getEncountersByPatient(child2)
					.size());

			Patient child3 = regService.getPatientBySerial(child3Id);
			assertNull("Child 3 not voided", child3);

			// Pregnancy Termination for Mother 2
			regService.recordPregnancyTermination(facilityId, date, mother2, 1,
					1);

			assertEquals("Pregnancy termination not added for Mother 2", 1,
					Context.getEncounterService().getEncountersByPatient(
							mother2).size());
			Obs mother2Pregnancy = regService.getActivePregnancy(mother2
					.getPatientId());
			assertNull("Pregnancy is still active after termination",
					mother2Pregnancy);

			// PPC Visit for Mother 2
			regService.recordMotherPPCVisit(facilityId, date, mother2, 1, true,
					2);

			assertEquals("PPC visit not added for Mother 2", 2, Context
					.getEncounterService().getEncountersByPatient(mother2)
					.size());

			// General Visit for Mother 2
			regService.recordMotherVisit(facilityId, date, mother2,
					"Mother2GeneralId", true, 1, 2, false);

			assertEquals("General visit not added for Mother 2", 3, Context
					.getEncounterService().getEncountersByPatient(mother2)
					.size());

			// PNC Visit for Child 2
			regService.recordChildPNCVisit(facilityId, date, child2, true, 1,
					1, true, true, true, true);

			assertEquals("PNC visit not added for Child 2", 2, Context
					.getEncounterService().getEncountersByPatient(child2)
					.size());

			// General Visit for Child 1
			regService.recordChildVisit(facilityId, date, child1,
					"Child1GeneralId", true, 4, 5, false);

			assertEquals("General visit not added for Child 1", 1, Context
					.getEncounterService().getEncountersByPatient(child1)
					.size());

			// Record Death of Child 1
			regService.recordDeath(facilityId, date, child1, 1);

			assertEquals("Deceased child 1 not voided", 5, Context
					.getPatientService().getAllPatients().size());

		} finally {
			Context.closeSession();
		}
	}
}