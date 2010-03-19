package org.motech.openmrs.module;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

public class MotechModuleActivatorTest extends BaseModuleContextSensitiveTest {

	MotechModuleActivator activator;

	@Before
	public void setUp() throws Exception {

		activator = new MotechModuleActivator(false);

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
	}

	@After
	public void tearDown() {
		activator = null;
	}

	@Test
	@SkipBaseSetup
	public void testActivator() {
		activator.startup();

		activator.shutdown();
	}
}
