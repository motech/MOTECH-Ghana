package org.motech.openmrs.module.tasks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.motech.model.Message;
import org.motech.model.MessageDefinition;
import org.motech.model.MessageProgramEnrollment;
import org.motech.model.MessageStatus;
import org.motech.model.MessageType;
import org.motech.model.ScheduledMessage;
import org.motech.model.WhyInterested;
import org.motech.openmrs.module.MotechModuleActivator;
import org.motech.openmrs.module.MotechService;
import org.motech.svc.RegistrarBean;
import org.motech.util.MotechConstants;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.Gender;
import org.motechproject.ws.MediaType;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Person;
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
		Integer personId = null;

		try {
			Context.openSession();
			Context.authenticate("admin", "test");

			RegistrarBean regService = Context.getService(MotechService.class)
					.getRegistrarBean();

			Date date = new Date();
			regService.registerPerson("firstName", "middleName", "lastName",
					"prefName", date, false, Gender.FEMALE, "region",
					"district", "community", "address", 1, true, 4,
					"primaryPhone", ContactNumberType.PERSONAL,
					"secondaryPhone", ContactNumberType.HOUSEHOLD,
					MediaType.TEXT, MediaType.TEXT, "languageVoice",
					"languageText", "howLearned", "religion", "occupation",
					WhyInterested.OUT_HOUSEHOLD_PREGNANCY);

			List<Person> matchingPeople = regService.getMatchingPeople(
					"firstName", "lastName", date, "community", "primaryPhone",
					null, null);
			assertEquals(1, matchingPeople.size());
			Person person = matchingPeople.get(0);
			personId = person.getPersonId();

			List<MessageProgramEnrollment> enrollments = Context.getService(
					MotechService.class).getActiveMessageProgramEnrollments(
					personId);
			assertEquals(1, enrollments.size());
			MessageProgramEnrollment enrollment = enrollments.get(0);
			assertNotNull("Obs is not set on enrollment", enrollment.getObsId());

			// Add needed message definitions for pregnancy program
			Context.getService(MotechService.class).saveMessageDefinition(
					new MessageDefinition("pregnancy.week.3", 16L,
							MessageType.INFORMATIONAL));
			Context.getService(MotechService.class).saveMessageDefinition(
					new MessageDefinition("pregnancy.week.4", 17L,
							MessageType.INFORMATIONAL));
			Context.getService(MotechService.class).saveMessageDefinition(
					new MessageDefinition("pregnancy.week.5", 18L,
							MessageType.INFORMATIONAL));
			Context.getService(MotechService.class).saveMessageDefinition(
					new MessageDefinition("pregnancy.week.6", 19L,
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
			assertEquals("pregnancy.week.4", scheduledMessage.getMessage()
					.getMessageKey());

			Concept refDate = Context.getConceptService().getConcept(
					MotechConstants.CONCEPT_ENROLLMENT_REFERENCE_DATE);

			Person person = Context.getPersonService().getPerson(personId);
			assertNotNull("Person does not exist with id", person);

			List<Obs> matchingObs = Context.getObsService()
					.getObservationsByPersonAndConcept(person, refDate);
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
					person.getPersonId());
			assertEquals(1, enrollments.size());
			MessageProgramEnrollment enrollment = enrollments.get(0);

			enrollment.setObsId(refDateObs.getObsId());
			Context.getService(MotechService.class)
					.saveMessageProgramEnrollment(enrollment);

		} finally {
			Context.closeSession();
		}

		task.execute();

		try {
			Context.openSession();

			List<ScheduledMessage> scheduledMessages = Context.getService(
					MotechService.class).getAllScheduledMessages();

			assertEquals(2, scheduledMessages.size());

			for (ScheduledMessage scheduledMessage : scheduledMessages) {
				System.out.println(scheduledMessage.getScheduledFor());
			}

			// Make sure new message is scheduled and previous message is
			// cancelled
			for (ScheduledMessage scheduledMessage : scheduledMessages) {
				assertEquals(1, scheduledMessage.getMessageAttempts().size());
				Message message = scheduledMessage.getMessageAttempts().get(0);
				if (scheduledMessage.getMessage().getMessageKey().equals(
						"pregnancy.week.4")) {
					assertEquals(MessageStatus.CANCELLED, message
							.getAttemptStatus());
				} else if (scheduledMessage.getMessage().getMessageKey()
						.equals("pregnancy.week.6")) {
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
