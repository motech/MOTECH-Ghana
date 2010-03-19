package org.motechproject.server.openmrs.module.sdsched;

import junit.framework.TestCase;

/**
 * Tests the {@link AffectedPatients} class.
 * 
 * @author batkinson
 * 
 */
public class AffectedPatientsTest extends TestCase {

	private AffectedPatients obj;
	private Integer samplePatientId;

	@Override
	protected void setUp() throws Exception {
		obj = new AffectedPatients();
		samplePatientId = new Integer(3);
	}

	@Override
	protected void tearDown() throws Exception {
		obj = null;
		samplePatientId = null;
	}

	public void testCreate() {
		assertNotNull(obj.affectedIds);
	}

	public void testGetAffectedIds() {
		assertNotNull(obj.getAffectedIds());
		assertEquals(obj.affectedIds, obj.getAffectedIds());
	}

	public void testAddPatientId() {
		obj.getAffectedIds().add(samplePatientId);
		assertTrue(obj.getAffectedIds().contains(samplePatientId));
	}

	public void testRemovePatientId() {
		obj.affectedIds.add(samplePatientId);
		assertTrue(obj.affectedIds.contains(samplePatientId));
		obj.getAffectedIds().remove(samplePatientId);
		assertFalse(obj.affectedIds.contains(samplePatientId));
	}

}
