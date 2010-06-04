package org.motechproject.server.omod.tasks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.motechproject.server.model.Message;
import org.motechproject.server.model.MessageDefinition;
import org.motechproject.server.model.MessageProgramEnrollment;
import org.motechproject.server.model.MessageStatus;
import org.motechproject.server.model.MessageType;
import org.motechproject.server.model.ScheduledMessage;
import org.motechproject.server.omod.MotechModuleActivator;
import org.motechproject.server.omod.MotechService;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.server.util.MotechConstants;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.DayOfWeek;
import org.motechproject.ws.Gender;
import org.motechproject.ws.HowLearned;
import org.motechproject.ws.InterestReason;
import org.motechproject.ws.MediaType;
import org.motechproject.ws.RegistrantType;
import org.motechproject.ws.RegistrationMode;
import org.openmrs.Concept;
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
public class MessageProgramUpdateTaskTest extends
		BaseModuleContextSensitiveTest {

	static MotechModuleActivator activator;

	Log log = LogFactory.getLog(MessageProgramUpdateTaskTest.class);

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
		Integer patientId = null;

		try {
			Context.openSession();
			Context.authenticate("admin", "test");

			RegistrarBean regService = Context.getService(MotechService.class)
					.getRegistrarBean();

			Date date = new Date();
			Integer motechId = 1234665;
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.YEAR, -30);
			Date birthdate = calendar.getTime();
			regService.registerPatient(RegistrationMode.USE_PREPRINTED_ID,
					motechId, RegistrantType.OTHER, "firstName", "middleName",
					"lastName", "prefName", birthdate, false, Gender.MALE,
					true, "nhis", null, null, null, "Address", "1111111111",
					null, null, true, true, ContactNumberType.PERSONAL,
					MediaType.TEXT, "language", DayOfWeek.MONDAY, date,
					InterestReason.KNOW_MORE_PREGNANCY_CHILDBIRTH,
					HowLearned.FRIEND, 5);

			List<Patient> matchingPatients = regService.getPatients(
					"firstName", "lastName", "prefName", birthdate, null,
					"1111111111", "nhis", motechId.toString());
			assertEquals(1, matchingPatients.size());
			Patient patient = matchingPatients.get(0);
			patientId = patient.getPatientId();

			List<MessageProgramEnrollment> enrollments = Context.getService(
					MotechService.class).getActiveMessageProgramEnrollments(
					patientId, null, null);
			assertEquals(1, enrollments.size());
			assertEquals("Weekly Info Pregnancy Message Program", enrollments
					.get(0).getProgram());
			assertNotNull("Obs is not set on enrollment", enrollments.get(0)
					.getObsId());

			// Add needed message definitions for pregnancy program
			Context.getService(MotechService.class).saveMessageDefinition(
					new MessageDefinition("pregnancy.week.5", 18L,
							MessageType.INFORMATIONAL));
			Context.getService(MotechService.class).saveMessageDefinition(
					new MessageDefinition("pregnancy.week.6", 19L,
							MessageType.INFORMATIONAL));
			Context.getService(MotechService.class).saveMessageDefinition(
					new MessageDefinition("pregnancy.week.7", 20L,
							MessageType.INFORMATIONAL));
		} finally {
			Context.closeSession();
		}

		task.execute();

		try {
			Context.openSession();
			Context.authenticate("admin", "test");

			List<ScheduledMessage> scheduledMessages = Context.getService(
					MotechService.class).getAllScheduledMessages();

			assertEquals(1, scheduledMessages.size());

			// Make sure message is scheduled with proper message
			ScheduledMessage scheduledMessage = scheduledMessages.get(0);
			assertEquals("pregnancy.week.5", scheduledMessage.getMessage()
					.getMessageKey());

			Concept refDate = Context.getConceptService().getConcept(
					MotechConstants.CONCEPT_ENROLLMENT_REFERENCE_DATE);

			Patient patient = Context.getPatientService().getPatient(patientId);
			assertNotNull("Patient does not exist with id", patient);

			List<Obs> matchingObs = Context.getObsService()
					.getObservationsByPersonAndConcept(patient, refDate);
			assertEquals(1, matchingObs.size());

			// Change reference date to 2 weeks previous
			Obs refDateObs = matchingObs.get(0);
			Integer originalObsId = refDateObs.getObsId();
			Date date = refDateObs.getValueDatetime();

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.DATE, 2 * -7);
			refDateObs.setValueDatetime(calendar.getTime());
			refDateObs = Context.getObsService().saveObs(refDateObs,
					"New date in future");

			assertTrue("New reference date obs id is same as previous",
					!originalObsId.equals(refDateObs.getObsId()));

			// Change obs referenced by enrollment to new obs
			List<MessageProgramEnrollment> enrollments = Context.getService(
					MotechService.class).getActiveMessageProgramEnrollments(
					patient.getPatientId(), null, null);
			assertEquals(1, enrollments.size());
			MessageProgramEnrollment infoEnrollment = enrollments.get(0);

			infoEnrollment.setObsId(refDateObs.getObsId());
			Context.getService(MotechService.class)
					.saveMessageProgramEnrollment(infoEnrollment);

		} finally {
			Context.closeSession();
		}

		task.execute();

		try {
			Context.openSession();

			List<ScheduledMessage> scheduledMessages = Context.getService(
					MotechService.class).getAllScheduledMessages();

			assertEquals(2, scheduledMessages.size());

			// Make sure new message is scheduled and previous message is
			// cancelled
			for (ScheduledMessage scheduledMessage : scheduledMessages) {
				assertEquals(1, scheduledMessage.getMessageAttempts().size());
				Message message = scheduledMessage.getMessageAttempts().get(0);
				if (scheduledMessage.getMessage().getMessageKey().equals(
						"pregnancy.week.5")) {
					assertEquals(MessageStatus.CANCELLED, message
							.getAttemptStatus());
				} else if (scheduledMessage.getMessage().getMessageKey()
						.equals("pregnancy.week.7")) {
					assertEquals(MessageStatus.SHOULD_ATTEMPT, message
							.getAttemptStatus());
				} else {
					fail("Scheduled message has wrong message key");
				}
			}

		} finally {
			Context.closeSession();
		}
	}

}
