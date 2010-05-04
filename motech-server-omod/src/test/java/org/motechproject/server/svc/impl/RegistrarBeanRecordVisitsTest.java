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
import org.motechproject.server.model.HIVStatus;
import org.motechproject.server.model.WhoRegistered;
import org.motechproject.server.omod.MotechModuleActivator;
import org.motechproject.server.omod.MotechService;
import org.motechproject.server.svc.BirthOutcomeChild;
import org.motechproject.server.svc.OpenmrsBean;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.ws.BirthOutcome;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.Gender;
import org.motechproject.ws.HIVResult;
import org.motechproject.ws.MediaType;
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

			String mother1Id = "1234575";
			String mother2Id = "1234581";
			Integer child1Id = 1234599;
			Integer child2Id = 1234608;
			Integer child3Id = 1234612;
			String nurseId = "NurseId";
			Date date = new Date();

			regService.registerNurse("Nurse", nurseId, "nursePhone",
					"West Test Clinic");
			User nurse = openmrsService.getNurseByCHPSId(nurseId);
			assertNotNull("Nurse not registered", nurse);

			regService.registerPregnantMother(mother1Id, "Mother1FirstName",
					"Mother1MiddleName", "Mother1LastName", "Mother1PrefName",
					date, false, true, "Mother1GHSNum", true, "nhisNumber1",
					date, "region", "district", "community", "address", 1,
					date, true, 0, 0, HIVStatus.NEGATIVE, false,
					"primaryPhone", ContactNumberType.PERSONAL,
					"secondaryPhone", ContactNumberType.PERSONAL,
					MediaType.TEXT, MediaType.TEXT, "languageVoice",
					"languageText", WhoRegistered.CHPS_STAFF, "religion",
					"occupation");

			Patient mother1 = openmrsService.getPatientByMotechId(mother1Id);
			assertNotNull("Mother 1 not registered", mother1);

			regService.registerPregnantMother(mother2Id, "Mother2FirstName",
					"Mother2MiddleName", "Mother2LastName", "Mother2PrefName",
					date, false, true, "Mother2GHSNum", true, "nhisNumber2",
					date, "region", "district", "community", "address", 1,
					date, true, 0, 0, HIVStatus.NEGATIVE, false,
					"primaryPhone", ContactNumberType.PERSONAL,
					"secondaryPhone", ContactNumberType.PERSONAL,
					MediaType.TEXT, MediaType.TEXT, "languageVoice",
					"languageText", WhoRegistered.CHPS_STAFF, "religion",
					"occupation");

			Patient mother2 = openmrsService.getPatientByMotechId(mother2Id);
			assertNotNull("Mother 2 not registered", mother2);

			regService.registerChild(child1Id.toString(), "Child1FirstName",
					"Child1MiddleName", "Child1LastName", "Child1PrefName",
					date, false, Gender.FEMALE, mother1Id, true,
					"Child1GHSNum", true, "nhisNumber3", date, "region",
					"district", "community", "address", 1, false,
					"primaryPhone", ContactNumberType.PERSONAL,
					"secondaryPhone", ContactNumberType.PERSONAL,
					MediaType.TEXT, MediaType.TEXT, "languageVoice",
					"languageText", WhoRegistered.CHPS_STAFF);

			Patient child1 = openmrsService.getPatientByMotechId(child1Id
					.toString());
			assertNotNull("Child 1 not registered", child1);

			assertEquals("3 new patients not registered", 5, Context
					.getPatientService().getAllPatients().size());

			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MONTH, 6);
			Date newDueDate = calendar.getTime();

			// ANC Visit for Mother 1
			regService.recordMotherANCVisit(nurse, 1, date, mother1, 1, 1,
					"House", "Community", newDueDate, 1, 1, 1.0, 1, 1, false,
					true, 1, 1, false, false, 1.0, false, false, false, true,
					false, false, HIVResult.NO_TEST, false, false, false, date,
					"Comments");

			assertEquals("ANC visit not added for Mother 1", 2, Context
					.getEncounterService().getEncountersByPatient(mother1)
					.size());
			Date currentDueDate = regService.getActivePregnancyDueDate(mother1
					.getPatientId());
			assertEquals("EDD not updated in ANC visit", newDueDate,
					currentDueDate);

			// Pregnancy Delivery for Mother 1, Adding Child 2
			List<BirthOutcomeChild> outcomes = new ArrayList<BirthOutcomeChild>();
			outcomes.add(new BirthOutcomeChild(BirthOutcome.A, child2Id,
					Gender.MALE, "Child2FirstName", true, true));
			outcomes.add(new BirthOutcomeChild(BirthOutcome.FSB, child3Id,
					Gender.MALE, "Child3FirstName", true, true));
			regService.recordPregnancyDelivery(nurse, date, mother1, 1, 1, 1,
					1, false, 1, outcomes);

			assertEquals("Pregnancy delivery not added for Mother 1", 3,
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

			Patient child2 = openmrsService.getPatientByMotechId(child2Id
					.toString());
			assertNotNull("Child 2 not registered", child2);
			assertEquals("PNC visit at birth not added for Child 2", 1, Context
					.getEncounterService().getEncountersByPatient(child2)
					.size());

			Patient child3 = openmrsService.getPatientByMotechId(child3Id
					.toString());
			assertNull("Child 3 not voided", child3);

			// Pregnancy Termination for Mother 2
			regService.recordPregnancyTermination(nurse, date, mother2, 1, 1);

			assertEquals("Pregnancy termination not added for Mother 2", 2,
					Context.getEncounterService().getEncountersByPatient(
							mother2).size());
			Obs mother2Pregnancy = regService.getActivePregnancy(mother2
					.getPatientId());
			assertNull("Pregnancy is still active after termination",
					mother2Pregnancy);

			// PPC Visit for Mother 2
			regService.recordMotherPPCVisit(nurse, date, mother2, 1, true, 2);

			assertEquals("PPC visit not added for Mother 2", 3, Context
					.getEncounterService().getEncountersByPatient(mother2)
					.size());

			// General Visit for Mother 2
			regService.recordMotherVisit(nurse, date, mother2,
					"Mother2GeneralId", true, 1, 2, false);

			assertEquals("General visit not added for Mother 2", 4, Context
					.getEncounterService().getEncountersByPatient(mother2)
					.size());

			// PNC Visit for Child 2
			regService.recordChildPNCVisit(nurse, date, child2, true, 1, 1,
					true, true, true, true, true);

			assertEquals("PNC visit not added for Child 2", 2, Context
					.getEncounterService().getEncountersByPatient(child2)
					.size());

			// General Visit for Child 1
			regService.recordChildVisit(nurse, date, child1, "Child1GeneralId",
					true, 4, 5, false);

			assertEquals("General visit not added for Child 1", 1, Context
					.getEncounterService().getEncountersByPatient(child1)
					.size());

			// Record Death of Child 1
			regService.recordDeath(nurse, date, child1, 1);

			assertEquals("Deceased child 1 not voided", 5, Context
					.getPatientService().getAllPatients().size());

		} finally {
			Context.closeSession();
		}
	}
}