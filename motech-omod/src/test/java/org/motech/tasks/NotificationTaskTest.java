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
import org.motech.model.FutureServiceDelivery;
import org.motech.model.Gender;
import org.motech.model.NotificationType;
import org.motech.model.PhoneType;
import org.motech.openmrs.module.MotechModuleActivator;
import org.motech.openmrs.module.MotechService;
import org.motech.svc.RegistrarBean;
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
				Gender.female, 1, "patientphoneNumber", PhoneType.personal,
				"language", NotificationType.text);

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

		FutureServiceDelivery f = new FutureServiceDelivery();
		f.setDate(new Date());
		f.setPatient(patient);
		f.setUser(Context.getUserService().getUserByUsername("nursename"));
		f.setService(Context.getConceptService().getConcept(9));

		Context.getService(MotechService.class).saveFutureServiceDelivery(f);

		task.execute();

		List<FutureServiceDelivery> services = Context.getService(
				MotechService.class).getAllFutureServiceDeliveries();
		assertEquals(1, services.size());

		FutureServiceDelivery service = services.get(0);

		assertNotNull(service.getPatientNotifiedDate());
		assertNotNull(service.getUserNotifiedDate());

		assertEquals(2, Context.getService(MotechService.class).getAllLogs()
				.size());
	}

}
