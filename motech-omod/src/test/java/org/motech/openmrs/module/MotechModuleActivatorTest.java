package org.motech.openmrs.module;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.test.context.ContextConfiguration;

/* BaseContextSensitiveTest normally allows module spring configurations,
 * But this Test is limited to the OpenMRS core spring configuration, 
 * Since the module services are not available during the Activator */
@ContextConfiguration(locations = { "classpath:applicationContext-service.xml" }, inheritLocations = false)
public class MotechModuleActivatorTest extends BaseContextSensitiveTest {

	MotechModuleActivator activator;

	@Before
	public void setUp() throws Exception {

		activator = new MotechModuleActivator();

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
