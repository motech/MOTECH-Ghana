package org.motech.openmrs.module.sdsched;

import org.openmrs.PatientIdentifier;

import junit.framework.TestCase;

/**
 * Tests the {@link AffectedPatients} class.
 * 
 * @author batkinson
 * 
 */
public class AffectedPatientsTest extends TestCase {

	private AffectedPatients obj;

	@Override
	protected void setUp() throws Exception {
		obj = new AffectedPatients();
	}

	@Override
	protected void tearDown() throws Exception {
		obj = null;
	}

	public void testCreate() {
		assertNotNull(obj.affectedIds);
	}

	public void testGetAffectedIds() {
		assertNotNull(obj.getAffectedIds());
		assertEquals(obj.affectedIds, obj.getAffectedIds());
	}

	public void testAddPatientId() {
		PatientIdentifier patientId = new PatientIdentifier();
		obj.getAffectedIds().add(patientId);
		assertTrue(obj.getAffectedIds().contains(patientId));
	}

	public void testRemovePatientId() {
		PatientIdentifier patientId = new PatientIdentifier();
		obj.affectedIds.add(patientId);
		assertTrue(obj.affectedIds.contains(patientId));
		obj.getAffectedIds().remove(patientId);
		assertFalse(obj.affectedIds.contains(patientId));
	}

}
