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

package org.motechproject.server.omod.sdsched;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import junit.framework.TestCase;

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
		patients.getAffectedIds().add(1);

		expect(mockMaintService.getAffectedPatients(false)).andReturn(patients);
		mockMaintService.updateSchedule((Integer) anyObject());
		expectLastCall().times(patients.getAffectedIds().size());

		replay(mockMaintService);

		obj.beforeCommit(false);

		verify(mockMaintService);

		assertEquals(0, patients.getAffectedIds().size());
	}

	public void testBeforeCommitMultiple() {

		AffectedPatients patients = new AffectedPatients();
		patients.getAffectedIds().add(1);
		patients.getAffectedIds().add(2);
		patients.getAffectedIds().add(3);

		expect(mockMaintService.getAffectedPatients(false)).andReturn(patients);
		mockMaintService.updateSchedule((Integer) anyObject());
		expectLastCall().times(patients.getAffectedIds().size());

		replay(mockMaintService);

		obj.beforeCommit(false);

		verify(mockMaintService);

		assertEquals(0, patients.getAffectedIds().size());
	}

}
