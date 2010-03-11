package org.motech.openmrs.module.sdsched;

import org.openmrs.PatientIdentifier;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;

/**
 * Tests the {@link ScheduleMaintSynchronization} class.
 * 
 * @author batkinson
 * 
 */
public class ScheduleMaintSynchronizationTest extends TestCase {

	private ScheduleMaintService mockMaintService;
	private ScheduleMaintSynchronization obj;

	@Override
	protected void setUp() throws Exception {
		obj = new ScheduleMaintSynchronization();
		mockMaintService = createMock(ScheduleMaintService.class);
		obj.setSchedService(mockMaintService);
	}

	@Override
	protected void tearDown() throws Exception {
		mockMaintService = null;
		obj = null;
	}

	public void testSetScheduleMaintService() {
		ScheduleMaintService realMaintService = new ScheduleMaintServiceImpl();
		obj.setSchedService(realMaintService);
		assertEquals(realMaintService, obj.schedService);
	}

	public void testBeforeCommitNull() {

		expect(mockMaintService.getAffectedPatients(false)).andReturn(null);

		replay(mockMaintService);

		obj.beforeCommit(false);

		verify(mockMaintService);
	}

	public void testBeforeCommitReadOnly() {

		replay(mockMaintService);

		obj.beforeCommit(true); // Shouldn't even lookup affected patients

		verify(mockMaintService);
	}

	public void testBeforeCommitSingle() {

		AffectedPatients patients = new AffectedPatients();
		patients.getAffectedIds().add(new PatientIdentifier());

		expect(mockMaintService.getAffectedPatients(false)).andReturn(patients);
		mockMaintService.updateSchedule((PatientIdentifier) anyObject());
		expectLastCall().times(patients.getAffectedIds().size());

		replay(mockMaintService);

		obj.beforeCommit(false);

		verify(mockMaintService);

		assertEquals(0, patients.getAffectedIds().size());
	}

	public void testBeforeCommitMultiple() {

		AffectedPatients patients = new AffectedPatients();
		patients.getAffectedIds().add(new PatientIdentifier());
		patients.getAffectedIds().add(new PatientIdentifier());
		patients.getAffectedIds().add(new PatientIdentifier());

		expect(mockMaintService.getAffectedPatients(false)).andReturn(patients);
		mockMaintService.updateSchedule((PatientIdentifier) anyObject());
		expectLastCall().times(patients.getAffectedIds().size());

		replay(mockMaintService);

		obj.beforeCommit(false);

		verify(mockMaintService);

		assertEquals(0, patients.getAffectedIds().size());
	}

}
