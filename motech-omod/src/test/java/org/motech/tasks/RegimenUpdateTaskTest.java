package org.motech.tasks;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.motech.messaging.MessageStatus;
import org.motech.messaging.ScheduledMessage;
import org.motech.openmrs.module.MotechModuleActivator;
import org.motech.openmrs.module.MotechService;
import org.motech.svc.RegistrarBean;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.DeliveryTime;
import org.motechproject.ws.Gender;
import org.motechproject.ws.MediaType;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

/**
 * BaseModuleContextSensitiveTest loads both the OpenMRS core and module spring
 * contexts and hibernate mappings, providing the OpenMRS Context for both
 * OpenMRS core and module services.
 */
public class RegimenUpdateTaskTest extends BaseModuleContextSensitiveTest {

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
	public void testRegimenUpdate() throws InterruptedException {
		RegistrarBean regService = ((RegistrarBean) applicationContext
				.getBean("registrarBean"));

		regService.registerNurse("nursename", "nursePhoneNumber",
				"Default Ghana Clinic");

		assertEquals(2, Context.getUserService().getAllUsers().size());

		regService.registerPatient("nursePhoneNumber", "serialId",
				"patientname", "community", "location", new Date(),
				Gender.FEMALE, 1, "patientphoneNumber",
				ContactNumberType.PERSONAL, "language", MediaType.TEXT,
				DeliveryTime.ANYTIME, new String[] {});

		assertEquals(1, Context.getPatientService().getAllPatients().size());

		RegimenUpdateTask task = new RegimenUpdateTask();

		List<Patient> patients = Context.getPatientService().getPatients(
				"patientname",
				"serialId",
				new ArrayList<PatientIdentifierType>(Arrays.asList(Context
						.getPatientService().getPatientIdentifierTypeByName(
								"Ghana Clinic Id"))), true);

		assertEquals(1, patients.size());

		Patient patient = patients.get(0);

		// Set patient registration date at 4 minutes in past
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -4);
		patient.setDateCreated(calendar.getTime());
		patient = Context.getPatientService().savePatient(patient);

		task.execute();

		List<ScheduledMessage> scheduledMessages = Context.getService(
				MotechService.class).getAllScheduledMessages();

		assertEquals(2, scheduledMessages.size());

		assertEquals("tetanus.info.3", scheduledMessages.get(0).getMessage()
				.getMessageKey());
		assertEquals("tetanus.1.reminder.1", scheduledMessages.get(1)
				.getMessage().getMessageKey());

		// Add tetanus immunization 4 minutes in past
		regService.recordMaternalVisit("nursePhoneNumber", calendar.getTime(),
				"serialId", true, false, false, 1, false, false, false, false,
				10.0);

		task.execute();

		scheduledMessages = Context.getService(MotechService.class)
				.getAllScheduledMessages();

		assertEquals(3, scheduledMessages.size());

		assertEquals("tetanus.info.3", scheduledMessages.get(0).getMessage()
				.getMessageKey());
		// Original reminder for first immunization, now cancelled
		assertEquals("tetanus.1.reminder.1", scheduledMessages.get(1)
				.getMessage().getMessageKey());
		assertEquals(MessageStatus.CANCELLED, scheduledMessages.get(1)
				.getMessageAttempts().get(0).getAttemptStatus());
		// New second reminder for second immunization
		// Second immunization prompt skipped since past time
		assertEquals("tetanus.2.reminder.2", scheduledMessages.get(2)
				.getMessage().getMessageKey());
	}

}
