package org.motech.openmrs.module.tasks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.motech.model.MessageDefinition;
import org.motech.model.MessageProgramEnrollment;
import org.motech.model.MessageStatus;
import org.motech.model.MessageType;
import org.motech.model.ScheduledMessage;
import org.motech.openmrs.module.MotechModuleActivator;
import org.motech.openmrs.module.MotechService;
import org.motech.svc.RegistrarBean;
import org.motech.util.MotechConstants;
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
public class MessageProgramUpdateTaskTest extends
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
	public void testMessageProgramUpdate() throws InterruptedException {

		MessageProgramUpdateTask task = new MessageProgramUpdateTask();
		Calendar calendar = Calendar.getInstance();
		List<ScheduledMessage> scheduledMessages = null;
		Set<String> messageKeys = null;

		try {
			Context.openSession();
			Context.authenticate("admin", "test");

			RegistrarBean regService = Context.getService(MotechService.class)
					.getRegistrarBean();

			regService.registerNurse("nursename", "nurseId",
					"nursePhoneNumber", MotechConstants.LOCATION_GHANA);

			assertEquals(2, Context.getUserService().getAllUsers().size());

			String[] programs = new String[] {
					"Tetanus Immunization Message Program",
					"Tetanus Information Message Program" };
			regService.registerPatient("nursePhoneNumber", "serialId",
					"patientname", "community", "location", new Date(),
					Gender.FEMALE, 1, "patientphoneNumber",
					ContactNumberType.PERSONAL, "language", MediaType.TEXT,
					DeliveryTime.ANYTIME, programs);

			assertEquals(3, Context.getPatientService().getAllPatients().size());

			List<Patient> patients = Context
					.getPatientService()
					.getPatients(
							"patientname",
							"serialId",
							new ArrayList<PatientIdentifierType>(
									Arrays
											.asList(Context
													.getPatientService()
													.getPatientIdentifierTypeByName(
															MotechConstants.PATIENT_IDENTIFIER_GHANA_CLINIC_ID))),
							true);

			assertEquals(1, patients.size());

			Patient patient = patients.get(0);

			// Change patient enrollment date to 4 minutes in past
			calendar.add(Calendar.MINUTE, -4);
			MotechService motechService = Context
					.getService(MotechService.class);
			for (String program : programs) {
				MessageProgramEnrollment enrollment = motechService
						.getActiveMessageProgramEnrollment(patient
								.getPatientId(), program);
				enrollment.setStartDate(calendar.getTime());
				motechService.saveMessageProgramEnrollment(enrollment);
			}

			// Add all needed tetanus message definitions in sqldiff
			Context.getService(MotechService.class).saveMessageDefinition(
					new MessageDefinition("tetanus.info.1", 2L,
							MessageType.INFORMATIONAL));
			Context.getService(MotechService.class).saveMessageDefinition(
					new MessageDefinition("tetanus.info.2", 3L,
							MessageType.INFORMATIONAL));
			Context.getService(MotechService.class).saveMessageDefinition(
					new MessageDefinition("tetanus.info.3", 5L,
							MessageType.INFORMATIONAL));
			Context.getService(MotechService.class).saveMessageDefinition(
					new MessageDefinition("tetanus.info.4", 6L,
							MessageType.INFORMATIONAL));
			Context.getService(MotechService.class).saveMessageDefinition(
					new MessageDefinition("tetanus.1.prompt", 4L,
							MessageType.REMINDER));
			Context.getService(MotechService.class).saveMessageDefinition(
					new MessageDefinition("tetanus.1.reminder.1", 7L,
							MessageType.REMINDER));
			Context.getService(MotechService.class).saveMessageDefinition(
					new MessageDefinition("tetanus.1.reminder.2", 8L,
							MessageType.REMINDER));
			Context.getService(MotechService.class).saveMessageDefinition(
					new MessageDefinition("tetanus.2.prompt", 9L,
							MessageType.REMINDER));
			Context.getService(MotechService.class).saveMessageDefinition(
					new MessageDefinition("tetanus.2.reminder.1", 10L,
							MessageType.REMINDER));
			Context.getService(MotechService.class).saveMessageDefinition(
					new MessageDefinition("tetanus.2.reminder.2", 11L,
							MessageType.REMINDER));
		} finally {
			Context.closeSession();
		}

		task.execute();

		try {
			Context.openSession();

			scheduledMessages = Context.getService(MotechService.class)
					.getAllScheduledMessages();

			assertEquals(2, scheduledMessages.size());

			// Make sure messages are scheduled (in any order)
			messageKeys = new HashSet<String>();
			for (ScheduledMessage scheduledMessage : scheduledMessages) {
				messageKeys.add(scheduledMessage.getMessage().getMessageKey());
			}
			assertTrue("Message is scheduled", messageKeys
					.contains("tetanus.info.3"));
			assertTrue("Message is scheduled", messageKeys
					.contains("tetanus.1.reminder.1"));

			// Add tetanus immunization 4 minutes in past
			RegistrarBean regService = Context.getService(MotechService.class)
					.getRegistrarBean();
			regService.recordMaternalVisit("nursePhoneNumber", calendar
					.getTime(), "serialId", true, false, false, 1, false,
					false, false, false, 10.0);
		} finally {
			Context.closeSession();
		}

		task.execute();

		try {
			Context.openSession();

			scheduledMessages = Context.getService(MotechService.class)
					.getAllScheduledMessages();

			assertEquals(3, scheduledMessages.size());

			// Make sure messages are scheduled (in any order)
			messageKeys = new HashSet<String>();
			for (ScheduledMessage scheduledMessage : scheduledMessages) {
				messageKeys.add(scheduledMessage.getMessage().getMessageKey());
				if (scheduledMessage.getMessage().getMessageKey().equals(
						"tetanus.1.reminder.1")) {
					// Original reminder for first immunization, now cancelled
					assertEquals(MessageStatus.CANCELLED, scheduledMessage
							.getMessageAttempts().get(0).getAttemptStatus());
				}
			}
			assertTrue("Message is scheduled", messageKeys
					.contains("tetanus.info.3"));
			assertTrue("Message is scheduled", messageKeys
					.contains("tetanus.1.reminder.1"));
			// New second reminder for second immunization
			// Second immunization prompt skipped since past time
			assertTrue("Message is scheduled", messageKeys
					.contains("tetanus.2.reminder.2"));
		} finally {
			Context.closeSession();
		}
	}

}
