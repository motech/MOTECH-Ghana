/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010 The Trustees of Columbia University in the City of
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

package org.motechproject.mobile.omp.manager.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class InMemoryMessageStatusStoreTest {

	Log log = LogFactory.getLog(InMemoryMessageStatusStore.class);

	InMemoryMessageStatusStore store;

	String id = "123456", newStatus = "NEWSTATUS",
			updateStatus = "UPDATESTATUS";

	long newTtl = 484, newCleanup = 38948;

	@Before
	public void setUp() throws Exception {
		store = new InMemoryMessageStatusStore();
	}

	@After
	public void tearDown() {
		store = null;
	}

	@Test
	public void testUpdate() {

		String id = "test_id";
		String firstStatus = "test_status_1";
		String secondStatus = "test_status_2";

		store.updateStatus(id, firstStatus);

		assertEquals(firstStatus, store.getStatus(id));

		store.updateStatus(id, secondStatus);

		assertEquals(secondStatus, store.getStatus(id));

	}

	@Test
	public void testUpdateStatusEmpty() {
		store.updateStatus(id, newStatus);
		assertEquals(newStatus, store.getStatus(id));
	}

	@Test
	public void testUpdateStatusExisting() {
		store.updateStatus(id, newStatus);
		store.updateStatus(id, updateStatus);
		assertEquals(updateStatus, store.getStatus(id));
	}

	@Test
	public void testUpdateStatusNull() {
		store.updateStatus(id, newStatus);
		store.updateStatus(id, null);
		assertFalse("Should not contain entry " + id, store.statusMap
				.containsKey(id));
	}

	@Test
	public void testGetStatus() {
		store.updateStatus(id, newStatus);
		assertTrue("Map should contain key " + id, store.statusMap
				.containsKey(id));
		assertEquals(newStatus, store.statusMap.get(id).getStatus());
	}

	@Test
	public void testGetSetTTL() {
		store.setTtl(newTtl);
		assertEquals(newTtl, store.ttl);
		assertEquals(newTtl, store.getTtl());
	}

	@Test
	public void testGetSetInterval() {
		store.setCleanupInterval(newCleanup);
		assertEquals(newCleanup, store.cleanupInterval);
		assertEquals(newCleanup, store.getCleanupInterval());
	}

	@Test
	public void testCleanup() {
		store.setTtl(500); // Entries Only live for .5 seconds
		store.setCleanupInterval(500);

		store.updateStatus(id, newStatus);
		assertEquals(newStatus, store.getStatus(id));

		try {
			Thread.sleep(750);
		} catch (InterruptedException e) {
			log.warn("interrupted while sleeping");
			throw new RuntimeException("interrupted while sleeping", e);
		}

		assertEquals(newStatus, store.getStatus(id));
		assertEquals(null, store.getStatus(id));
	}
}
