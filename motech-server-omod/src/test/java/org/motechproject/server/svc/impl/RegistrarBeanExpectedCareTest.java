package org.motechproject.server.svc.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.motechproject.server.model.Community;
import org.motechproject.server.model.ExpectedEncounter;
import org.motechproject.server.model.ExpectedObs;
import org.motechproject.server.model.Facility;
import org.motechproject.server.omod.MotechModuleActivator;
import org.motechproject.server.omod.MotechService;
import org.motechproject.server.svc.OpenmrsBean;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.server.util.MotechConstants;
import org.motechproject.ws.Gender;
import org.motechproject.ws.RegistrantType;
import org.motechproject.ws.RegistrationMode;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

/**
 * BaseModuleContextSensitiveTest loads both the OpenMRS core and module spring
 * contexts and hibernate mappings, providing the OpenMRS Context for both
 * OpenMRS core and module services.
 */
public class RegistrarBeanExpectedCareTest extends
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

			MotechService motechService = Context
					.getService(MotechService.class);
			RegistrarBean regService = motechService.getRegistrarBean();
			OpenmrsBean openmrsService = motechService.getOpenmrsBean();

			Integer communityId = 11111;
			Integer facilityId = 1111;

			Community community = motechService.getCommunityById(communityId);
			assertNotNull("Community in dataset is missing", community);
			Facility facility = community.getFacility();
			assertNotNull("Facility for community in dataset is missing",
					facility);
			assertEquals(facilityId, facility.getFacilityId());

			Date date = new Date();
			Integer childId = 1234631;
			regService.registerPatient(RegistrationMode.USE_PREPRINTED_ID,
					childId, RegistrantType.CHILD_UNDER_FIVE, "childfirstName",
					"childmiddleName", "childlastName", "childprefName", date,
					false, Gender.FEMALE, true, "nhis", date, null, community,
					"Address", "1111111111", null, null, false, false, null,
					null, null, null, null, null, null, null);

			Patient patient = openmrsService.getPatientByMotechId(childId
					.toString());

			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MONTH, -2);
			Date twoMonthsPast = calendar.getTime();
			calendar.add(Calendar.MONTH, -3);
			Date threeMonthsPast = calendar.getTime();
			calendar.setTime(date);
			calendar.add(Calendar.DATE, 5);
			Date fiveDaysFuture = calendar.getTime();
			calendar.setTime(date);
			calendar.add(Calendar.MONTH, 1);
			Date oneMonthFuture = calendar.getTime();

			// Upcoming
			ExpectedObs obs = new ExpectedObs();
			obs.setPatient(patient);
			obs.setConcept(Context.getConceptService().getConcept(
					MotechConstants.CONCEPT_TETANUS_TOXOID_DOSE));
			obs.setDueObsDatetime(fiveDaysFuture);
			obs.setLateObsDatetime(fiveDaysFuture);
			obs.setMaxObsDatetime(fiveDaysFuture);
			obs.setName("TT1");
			obs.setGroup("TT");
			regService.saveExpectedObs(obs);

			// Upcoming but too far in future
			obs = new ExpectedObs();
			obs.setPatient(patient);
			obs.setConcept(Context.getConceptService().getConcept(
					MotechConstants.CONCEPT_TETANUS_TOXOID_DOSE));
			obs.setDueObsDatetime(oneMonthFuture);
			obs.setLateObsDatetime(oneMonthFuture);
			obs.setMaxObsDatetime(oneMonthFuture);
			obs.setName("TT2");
			obs.setGroup("TT");
			regService.saveExpectedObs(obs);

			// Defaulted and not beyond max
			obs = new ExpectedObs();
			obs.setPatient(patient);
			obs.setConcept(Context.getConceptService().getConcept(
					MotechConstants.CONCEPT_TETANUS_TOXOID_DOSE));
			obs.setDueObsDatetime(twoMonthsPast);
			obs.setLateObsDatetime(twoMonthsPast);
			obs.setMaxObsDatetime(fiveDaysFuture);
			obs.setName("TT3");
			obs.setGroup("TT");
			regService.saveExpectedObs(obs);

			// Second Defaulted with null max
			obs = new ExpectedObs();
			obs.setPatient(patient);
			obs.setConcept(Context.getConceptService().getConcept(
					MotechConstants.CONCEPT_TETANUS_TOXOID_DOSE));
			obs.setDueObsDatetime(threeMonthsPast);
			obs.setLateObsDatetime(threeMonthsPast);
			obs.setName("TT5");
			obs.setGroup("TT");
			regService.saveExpectedObs(obs);

			// Defaulted but beyond max
			obs = new ExpectedObs();
			obs.setPatient(patient);
			obs.setConcept(Context.getConceptService().getConcept(
					MotechConstants.CONCEPT_TETANUS_TOXOID_DOSE));
			obs.setDueObsDatetime(twoMonthsPast);
			obs.setLateObsDatetime(twoMonthsPast);
			obs.setMaxObsDatetime(twoMonthsPast);
			obs.setName("TT4");
			obs.setGroup("TT");
			regService.saveExpectedObs(obs);

			// Upcoming
			ExpectedEncounter enc = new ExpectedEncounter();
			enc.setPatient(patient);
			enc.setEncounterType(Context.getEncounterService()
					.getEncounterType(MotechConstants.ENCOUNTER_TYPE_ANCVISIT));
			enc.setDueEncounterDatetime(fiveDaysFuture);
			enc.setLateEncounterDatetime(fiveDaysFuture);
			enc.setMaxEncounterDatetime(fiveDaysFuture);
			enc.setName("ANC1");
			enc.setGroup("ANC");
			regService.saveExpectedEncounter(enc);

			// Upcoming but too far in future
			enc = new ExpectedEncounter();
			enc.setPatient(patient);
			enc.setEncounterType(Context.getEncounterService()
					.getEncounterType(MotechConstants.ENCOUNTER_TYPE_ANCVISIT));
			enc.setDueEncounterDatetime(oneMonthFuture);
			enc.setLateEncounterDatetime(oneMonthFuture);
			enc.setMaxEncounterDatetime(oneMonthFuture);
			enc.setName("ANC2");
			enc.setGroup("ANC");
			regService.saveExpectedEncounter(enc);

			// Defaulted and not beyond max
			enc = new ExpectedEncounter();
			enc.setPatient(patient);
			enc.setEncounterType(Context.getEncounterService()
					.getEncounterType(MotechConstants.ENCOUNTER_TYPE_ANCVISIT));
			enc.setDueEncounterDatetime(twoMonthsPast);
			enc.setLateEncounterDatetime(twoMonthsPast);
			enc.setMaxEncounterDatetime(fiveDaysFuture);
			enc.setName("ANC3");
			enc.setGroup("ANC");
			regService.saveExpectedEncounter(enc);

			// Second Defaulted with null max
			enc = new ExpectedEncounter();
			enc.setPatient(patient);
			enc.setEncounterType(Context.getEncounterService()
					.getEncounterType(MotechConstants.ENCOUNTER_TYPE_ANCVISIT));
			enc.setDueEncounterDatetime(threeMonthsPast);
			enc.setLateEncounterDatetime(threeMonthsPast);
			enc.setName("ANC9");
			enc.setGroup("ANC");
			regService.saveExpectedEncounter(enc);

			// Defaulted but beyond max
			enc = new ExpectedEncounter();
			enc.setPatient(patient);
			enc.setEncounterType(Context.getEncounterService()
					.getEncounterType(MotechConstants.ENCOUNTER_TYPE_ANCVISIT));
			enc.setDueEncounterDatetime(twoMonthsPast);
			enc.setLateEncounterDatetime(twoMonthsPast);
			enc.setMaxEncounterDatetime(twoMonthsPast);
			enc.setName("ANC4");
			enc.setGroup("ANC");
			regService.saveExpectedEncounter(enc);

			List<ExpectedEncounter> upcomingEnc = regService
					.getUpcomingExpectedEncounters(patient);
			assertEquals(3, upcomingEnc.size());
			assertEquals("ANC9", upcomingEnc.get(0).getName());
			assertEquals("ANC3", upcomingEnc.get(1).getName());
			assertEquals("ANC1", upcomingEnc.get(2).getName());

			List<ExpectedObs> upcomingObs = regService
					.getUpcomingExpectedObs(patient);
			assertEquals(3, upcomingObs.size());
			assertEquals("TT5", upcomingObs.get(0).getName());
			assertEquals("TT3", upcomingObs.get(1).getName());
			assertEquals("TT1", upcomingObs.get(2).getName());

			List<ExpectedEncounter> defaultedEnc = regService
					.getDefaultedExpectedEncounters(facility,
							new String[] { "ANC" });
			assertEquals(2, defaultedEnc.size());
			assertEquals("ANC3", defaultedEnc.get(0).getName());
			assertEquals("ANC9", defaultedEnc.get(1).getName());

			List<ExpectedObs> defaultedObs = regService
					.getDefaultedExpectedObs(facility, new String[] { "TT" });
			assertEquals(2, defaultedObs.size());
			assertEquals("TT3", defaultedObs.get(0).getName());
			assertEquals("TT5", defaultedObs.get(1).getName());

		} finally {
			Context.closeSession();
		}
	}

}
