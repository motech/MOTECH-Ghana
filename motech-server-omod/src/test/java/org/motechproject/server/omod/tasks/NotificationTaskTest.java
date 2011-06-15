/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010-11 The Trustees of Columbia University in the City of
 * New York and Grameen Foundation USA.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Grameen Foundation USA, Columbia University, or
 * their respective contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA, COLUMBIA UNIVERSITY
 * AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION
 * USA, COLUMBIA UNIVERSITY OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.motechproject.server.omod.tasks;

import org.junit.*;
import org.motechproject.server.messaging.impl.MessageSchedulerImpl;
import org.motechproject.server.model.*;
import org.motechproject.server.model.MessageStatus;
import org.motechproject.server.omod.MotechModuleActivator;
import org.motechproject.server.service.MotechService;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.server.util.MotechConstants;
import org.motechproject.ws.*;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
		// using 1.4.4-createdb-from-scratch-with-demo-data.sql
		// Removed all empty short_name="" from concepts
		// Added missing description to relationship_type
		// Removed all patients and related patient/person info (id 2-500)
		// Removed all concepts except those in sqldiff
		executeDataSet("initial-openmrs-dataset.xml");

		// Includes Motech data added in sqldiff
		executeDataSet("motech-dataset.xml");
		
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

			RegistrarBean registrarBean = Context.getService(MotechService.class)
					.getRegistrarBean();

			// Register Mother and Child
			Date date = new Date();
			Integer motherMotechId = 1234649;
            Facility facility = registrarBean.getFacilityById(11117);
            registrarBean.registerPatient(RegistrationMode.USE_PREPRINTED_ID,
					motherMotechId, RegistrantType.PREGNANT_MOTHER,
					"motherfirstName", "mothermiddleName", "motherlastName",
					"motherprefName", date, false, Gender.FEMALE, true,
					"mothernhis", date, null, null, facility, "Address", "1111111111",
					date, true, true, true, ContactNumberType.PERSONAL,
					MediaType.TEXT, "language", DayOfWeek.MONDAY, date,
					InterestReason.CURRENTLY_PREGNANT, HowLearned.FRIEND, null);

			Integer childMotechId = 1234654;
			registrarBean.registerPatient(RegistrationMode.USE_PREPRINTED_ID,
					childMotechId, RegistrantType.CHILD_UNDER_FIVE,
					"childfirstName", "childmiddleName", "childlastName",
					"childprefName", date, false, Gender.FEMALE, true,
					"childnhis", date, null, null, facility, "Address", "1111111111",
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
			Obs pregnancyObs = registrarBean.getActivePregnancy(mother
					.getPatientId());
			assertNotNull("Pregnancy Obs does not exist", pregnancyObs);

			List<Patient> childMatchingPatients = Context.getPatientService()
					.getPatients("childfirstName childlastName",
							childMotechId.toString(), patientIdTypeList, true);
			assertEquals(1, childMatchingPatients.size());
			Patient child = childMatchingPatients.get(0);

			// Add Test Message Definition, Enrollment and Scheduled Message
			String messageKey = "Test Definition";
			String messageKeyA = "Test DefinitionA";
			String messageKeyB = "Test DefinitionB";
			String messageKeyC = "Test DefinitionC";
			MessageDefinition messageDefinition = new MessageDefinition(
					messageKey, 2L, MessageType.INFORMATIONAL);
			MessageDefinition messageDefinitionA = new MessageDefinition(
					messageKeyA, 3L, MessageType.INFORMATIONAL);
			MessageDefinition messageDefinitionB = new MessageDefinition(
					messageKeyB, 4L, MessageType.INFORMATIONAL);
			MessageDefinition messageDefinitionC = new MessageDefinition(
					messageKeyC, 5L, MessageType.INFORMATIONAL);
			Context.getService(MotechService.class).saveMessageDefinition(
					messageDefinition);
			Context.getService(MotechService.class).saveMessageDefinition(
					messageDefinitionA);
			Context.getService(MotechService.class).saveMessageDefinition(
					messageDefinitionB);
			Context.getService(MotechService.class).saveMessageDefinition(
					messageDefinitionC);

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
			messageScheduler.setRegistrarBean(registrarBean);
			messageScheduler.scheduleMessages(messageKey, messageKeyA,
					messageKeyB, messageKeyC, enrollment, scheduledMessageDate,
					date);
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

			assertEquals(3, scheduledMessages.size());

			// Results are ordered oldest first
			// Oldest/last message with current date should be pending
			// Newer/first messages with later date should not be pending
			for (int i = 0; i < 3; i++) {
				List<Message> messageAttempts = scheduledMessages.get(i)
						.getMessageAttempts();
				assertEquals(1, messageAttempts.size());
				Message message = messageAttempts.get(0);
				assertNotNull("Message attempt date is null", message
						.getAttemptDate());
				if (i == 0) {
					assertEquals(MessageStatus.ATTEMPT_PENDING, message
							.getAttemptStatus());
				} else {
					assertEquals(MessageStatus.SHOULD_ATTEMPT, message
							.getAttemptStatus());
				}
			}
		} finally {
			Context.closeSession();
		}
	}

}
