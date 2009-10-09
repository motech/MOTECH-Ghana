package org.motech.tasks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.motech.messaging.Message;
import org.motech.messaging.MessageSchedulerImpl;
import org.motech.messaging.MessageStatus;
import org.motech.messaging.ScheduledMessage;
import org.motech.openmrs.module.MotechModuleActivator;
import org.motech.openmrs.module.MotechService;
import org.motech.openmrs.module.impl.ContextServiceImpl;
import org.motech.svc.RegistrarBean;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.Gender;
import org.motechproject.ws.MediaType;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

/**
 * BaseModuleContextSensitiveTest loads both the OpenMRS core and module spring
 * contexts and hibernate mappings, providing the OpenMRS Context for both
 * OpenMRS core and module services.
 */
public class NotificationTaskTest extends BaseModuleContextSensitiveTest {

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
	public void testSingleNotify() {
		RegistrarBean regService = ((RegistrarBean) applicationContext
				.getBean("registrarBean"));

		regService.registerNurse("nursename", "nursePhoneNumber",
				"Default Ghana Clinic");

		assertEquals(2, Context.getUserService().getAllUsers().size());

		regService.registerPatient("nursePhoneNumber", "serialId",
				"patientname", "community", "location", new Date(),
				Gender.FEMALE, 1, "patientphoneNumber",
				ContactNumberType.PERSONAL, "language", MediaType.TEXT);

		assertEquals(1, Context.getPatientService().getAllPatients().size());

		NotificationTask task = new NotificationTask();
		TaskDefinition taskDef = new TaskDefinition();
		taskDef.setRepeatInterval(30L);
		task.initialize(taskDef);

		List<Patient> patients = Context.getPatientService().getPatients(
				"patientname",
				"serialId",
				new ArrayList<PatientIdentifierType>(Arrays.asList(Context
						.getPatientService().getPatientIdentifierTypeByName(
								"Ghana Clinic Id"))), true);

		assertEquals(1, patients.size());

		Patient patient = patients.get(0);

		MessageSchedulerImpl messageScheduler = new MessageSchedulerImpl();
		messageScheduler.setContextService(new ContextServiceImpl());
		// Schedule message 5 seconds in future
		Date messageDate = new Date(System.currentTimeMillis() + 5 * 1000);
		messageScheduler.scheduleMessage("Test Definition", 2L, "Test Group",
				patient.getPersonId(), messageDate);

		task.execute();

		List<ScheduledMessage> scheduledMessages = Context.getService(
				MotechService.class).getAllScheduledMessages();

		assertEquals(1, scheduledMessages.size());

		ScheduledMessage retrievedScheduledMessage = scheduledMessages.get(0);
		List<Message> messageAttempts = retrievedScheduledMessage
				.getMessageAttempts();

		assertEquals(1, messageAttempts.size());

		Message message = messageAttempts.get(0);

		assertNotNull("Message attempt date is null", message.getAttemptDate());
		assertEquals(MessageStatus.ATTEMPT_PENDING, message.getAttemptStatus());

		assertEquals(1, Context.getService(MotechService.class).getAllLogs()
				.size());
	}

}
