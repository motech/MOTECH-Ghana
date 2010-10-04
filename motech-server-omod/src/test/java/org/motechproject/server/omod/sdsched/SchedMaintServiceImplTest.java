/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) ${year} The Trustees of Columbia University in the City of
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

package org.motechproject.server.omod.sdsched;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import junit.framework.TestCase;

import org.easymock.Capture;
import org.springframework.transaction.support.TransactionSynchronization;

/**
 * Test for {@link ScheduleMaintServiceImpl}.
 * 
 * @author batkinson
 * 
 */
public class SchedMaintServiceImplTest extends TestCase {

	private TxSyncManWrapper mockSyncMan;
	private ScheduleAdjuster mockAdjuster;
	private ScheduleMaintServiceImpl obj;

	@Override
	protected void setUp() throws Exception {
		mockSyncMan = createMock(TxSyncManWrapper.class);
		mockAdjuster = createMock(ScheduleAdjuster.class);
		obj = new ScheduleMaintServiceImpl();
		obj.setSyncManWrapper(mockSyncMan);
		obj.setScheduleAdjuster(mockAdjuster);
	}

	@Override
	protected void tearDown() throws Exception {
		mockSyncMan = null;
		obj = null;
	}

	public void testSetSyncManWrapper() {
		TxSyncManWrapper txSyncMan = new TxSyncManWrapperImpl();
		obj.setSyncManWrapper(txSyncMan);
		assertEquals(txSyncMan, obj.syncManWrapper);
	}

	public void testSetScheduleAdjuster() {
		ScheduleAdjuster adjuster = new DummyScheduleAdjuster();
		obj.setScheduleAdjuster(adjuster);
		assertEquals(adjuster, obj.scheduleAdjuster);
	}

	public void testGetAffectedPatientsUnboundNoCreate() {

		AffectedPatients result = null;

		expect(mockSyncMan.getResource(ScheduleMaintServiceImpl.RESOURCE_NAME))
				.andReturn(null);

		replay(mockSyncMan);

		result = obj.getAffectedPatients(false);

		verify(mockSyncMan);

		assertNull(result);

	}

	public void testGetAffectedPatientsUnboundCreate() {

		AffectedPatients result = null;

		Capture<Object> affectedPatientsCap = new Capture<Object>();
		expect(mockSyncMan.getResource(ScheduleMaintServiceImpl.RESOURCE_NAME))
				.andReturn(null);
		mockSyncMan.bindResource(eq(ScheduleMaintServiceImpl.RESOURCE_NAME),
				capture(affectedPatientsCap));

		replay(mockSyncMan);

		result = obj.getAffectedPatients(true);

		verify(mockSyncMan);

		assertNotNull(result);
		AffectedPatients capturedPatients = (AffectedPatients) affectedPatientsCap
				.getValue();
		assertEquals(capturedPatients, result);
		assertTrue(result.getAffectedIds().isEmpty());
	}

	public void testGetAffectedPatientsBoundCreate() {

		AffectedPatients result = null;

		AffectedPatients boundPatients = new AffectedPatients();
		boundPatients.getAffectedIds().add(1);
		boundPatients.getAffectedIds().add(2);
		boundPatients.getAffectedIds().add(3);

		expect(mockSyncMan.getResource(ScheduleMaintServiceImpl.RESOURCE_NAME))
				.andReturn(boundPatients);

		replay(mockSyncMan);

		result = obj.getAffectedPatients(true);

		verify(mockSyncMan);

		assertEquals(boundPatients, result);
		assertEquals(3, result.getAffectedIds().size());
	}

	public void testAddAffectedPatient() {

		Integer patientId = new Integer(1);
		AffectedPatients boundPatients = new AffectedPatients();

		expect(mockSyncMan.getResource(ScheduleMaintServiceImpl.RESOURCE_NAME))
				.andReturn(boundPatients);

		replay(mockSyncMan);

		obj.addAffectedPatient(patientId);

		verify(mockSyncMan);

		assertTrue(boundPatients.getAffectedIds().contains(patientId));
	}

	public void testRemoveAffectedPatient() {

		Integer patientId = new Integer(1);
		AffectedPatients boundPatients = new AffectedPatients();
		boundPatients.getAffectedIds().add(patientId);

		expect(mockSyncMan.getResource(ScheduleMaintServiceImpl.RESOURCE_NAME))
				.andReturn(boundPatients);

		replay(mockSyncMan);

		obj.removeAffectedPatient(patientId);

		verify(mockSyncMan);

		assertFalse(boundPatients.getAffectedIds().contains(patientId));
	}

	public void testClearAffectedPatients() {

		mockSyncMan.unbindResource(ScheduleMaintServiceImpl.RESOURCE_NAME);

		replay(mockSyncMan);

		obj.clearAffectedPatients();

		verify(mockSyncMan);
	}

	public void testRequestSynch() {

		Capture<TransactionSynchronization> syncCap = new Capture<TransactionSynchronization>();
		expect(
				mockSyncMan
						.containsSynchronization(ScheduleMaintSynchronization.class))
				.andReturn(false);
		mockSyncMan.registerSynchronization(capture(syncCap));

		replay(mockSyncMan);

		obj.requestSynch();

		verify(mockSyncMan);

		TransactionSynchronization txSync = syncCap.getValue();
		assertNotNull(txSync);
		assertTrue(txSync instanceof ScheduleMaintSynchronization);
		ScheduleMaintSynchronization maintSync = (ScheduleMaintSynchronization) txSync;
		assertEquals(obj, maintSync.schedService);
	}

	public void testRequestSynchAlreadyExists() {

		expect(
				mockSyncMan
						.containsSynchronization(ScheduleMaintSynchronization.class))
				.andReturn(true);

		replay(mockSyncMan);

		obj.requestSynch();

		verify(mockSyncMan);
	}

	public void testUpdateSchedule() {

		Integer patientId = new Integer(1);
		Capture<Integer> patientIdCap = new Capture<Integer>();
		mockAdjuster.adjustSchedule(capture(patientIdCap));

		replay(mockAdjuster);

		obj.updateSchedule(patientId);

		verify(mockAdjuster);

		assertEquals(patientId, patientIdCap.getValue());
	}
}
