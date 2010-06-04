package org.motechproject.server.omod.tasks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.motechproject.server.messaging.impl.MessageSchedulerImpl;
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
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

public class NotificationTaskTest extends BaseModuleContextSensitiveTest {

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

		try {
			Context.openSession();
			Context.authenticate("admin", "test");

			RegistrarBean regService = Context.getService(MotechService.class)
					.getRegistrarBean();

			// Register Mother and Child
			Date date = new Date();
			Integer motherMotechId = 1234649;
			regService.registerPatient(RegistrationMode.USE_PREPRINTED_ID,
					motherMotechId, RegistrantType.PREGNANT_MOTHER,
					"motherfirstName", "mothermiddleName", "motherlastName",
					"motherprefName", date, false, Gender.FEMALE, true,
					"mothernhis", date, null, null, "Address", "1111111111",
					date, true, true, true, ContactNumberType.PERSONAL,
					MediaType.TEXT, "language", DayOfWeek.MONDAY, date,
					InterestReason.CURRENTLY_PREGNANT, HowLearned.FRIEND, null);

			Integer childMotechId = 1234654;
			regService.registerPatient(RegistrationMode.USE_PREPRINTED_ID,
					childMotechId, RegistrantType.CHILD_UNDER_FIVE,
					"childfirstName", "childmiddleName", "childlastName",
					"childprefName", date, false, Gender.FEMALE, true,
					"childnhis", date, null, null, "Address", "1111111111",
					null, null, false, false, ContactNumberType.PERSONAL,
					MediaType.TEXT, "language", DayOfWeek.MONDAY, date,
					InterestReason.FAMILY_FRIEND_PREGNANT, HowLearned.FRIEND,
					null);

			// Check Mother and Child registered successfully
			assertEquals(4, Context.getPatientService().getAllPatients().size());

			ArrayList<PatientIdentifierType> patientIdTypeList = new ArrayList<PatientIdentifierType>();
			patientIdTypeList.add(Context.getPatientService()
					.getPatientIdentifierTypeByName(
							MotechConstants.PATIENT_IDENTIFIER_MOTECH_ID));

			List<Patient> motherMatchingPatients = Context.getPatientService()
					.getPatients("motherfirstName motherlastName",
							motherMotechId.toString(), patientIdTypeList, true);
			assertEquals(1, motherMatchingPatients.size());

			// Verify Mother's Pregnancy exists
			Patient mother = motherMatchingPatients.get(0);
			Obs pregnancyObs = regService.getActivePregnancy(mother
					.getPatientId());
			assertNotNull("Pregnancy Obs does not exist", pregnancyObs);

			List<Patient> childMatchingPatients = Context.getPatientService()
					.getPatients("childfirstName childlastName",
							childMotechId.toString(), patientIdTypeList, true);
			assertEquals(1, childMatchingPatients.size());
			Patient child = childMatchingPatients.get(0);

			// Add Test Message Definition, Enrollment and Scheduled Message
			String messageKey = "Test Definition";
			MessageDefinition messageDefinition = new MessageDefinition(
					messageKey, 2L, MessageType.INFORMATIONAL);
			messageDefinition = Context.getService(MotechService.class)
					.saveMessageDefinition(messageDefinition);

			MessageProgramEnrollment enrollment = new MessageProgramEnrollment();
			enrollment.setStartDate(new Date());
			enrollment.setProgram("Fake Program Name");
			enrollment.setPersonId(child.getPatientId());
			Context.getService(MotechService.class)
					.saveMessageProgramEnrollment(enrollment);

			// Schedule message 5 seconds in future
			Date scheduledMessageDate = new Date(
					System.currentTimeMillis() + 5 * 1000);
			MessageSchedulerImpl messageScheduler = new MessageSchedulerImpl();
			messageScheduler.setRegistrarBean(regService);
			messageScheduler.scheduleMessage(messageKey, enrollment,
					scheduledMessageDate);
		} finally {
			Context.closeSession();
		}

		NotificationTask task = new NotificationTask();
		TaskDefinition taskDef = new TaskDefinition();
		taskDef.setRepeatInterval(30L);
		task.initialize(taskDef);
		task.execute();

		try {
			Context.openSession();

			// Verify Message Status updated on Scheduled Message Attempt
			List<ScheduledMessage> scheduledMessages = Context.getService(
					MotechService.class).getAllScheduledMessages();

			assertEquals(1, scheduledMessages.size());

			ScheduledMessage retrievedScheduledMessage = scheduledMessages
					.get(0);
			List<Message> messageAttempts = retrievedScheduledMessage
					.getMessageAttempts();

			assertEquals(1, messageAttempts.size());

			Message message = messageAttempts.get(0);

			assertNotNull("Message attempt date is null", message
					.getAttemptDate());
			assertEquals(MessageStatus.ATTEMPT_PENDING, message
					.getAttemptStatus());

		} finally {
			Context.closeSession();
		}
	}

}
