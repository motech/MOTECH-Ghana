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

package org.motechproject.server.omod.sdsched;

import junit.framework.TestCase;

import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Tests {@link TxSyncManWrapperImpl} and its behavior when using spring's
 * hibernate transaction manager.
 * 
 * @author batkinson
 * 
 */
public class TxSyncManWrapperImplTest extends TestCase {

	PlatformTransactionManager txMan;
	TransactionTemplate txTempl;
	TxSyncManWrapperImpl txSyncManWrapper;

	@Override
	protected void setUp() throws Exception {
		ApplicationContext ctx = new ClassPathXmlApplicationContext(
				"classpath:TxWrapperTestContext.xml");
		txMan = (PlatformTransactionManager) ctx.getBean("txManager");
		txTempl = new TransactionTemplate(txMan);
		txSyncManWrapper = new TxSyncManWrapperImpl();
	}

	@Override
	protected void tearDown() throws Exception {
		txMan = null;
		txTempl = null;
		txSyncManWrapper = null;
	}

	public void testGetResource() {
		final String resourceName = "A Resource";
		final String resourceValue = "A Resource value";
		Object retVal = txTempl.execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus status) {
				try {
					TransactionSynchronizationManager.bindResource(
							resourceName, resourceValue);
					return txSyncManWrapper.getResource(resourceName);
				} finally {
					TransactionSynchronizationManager
							.unbindResourceIfPossible(resourceName);
				}
			}
		});
		assertEquals(resourceValue, retVal);
	}

	public void testBindResource() {
		final String resourceName = "A Resource";
		final String resourceValue = "A Resource value";
		Object retVal = txTempl.execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus status) {
				try {
					txSyncManWrapper.bindResource(resourceName, resourceValue);
					return TransactionSynchronizationManager
							.getResource(resourceName);
				} finally {
					TransactionSynchronizationManager
							.unbindResourceIfPossible(resourceName);
				}
			}
		});
		assertEquals(resourceValue, retVal);
	}

	public void testUnbindResource() {
		final String resourceName = "A Resource";
		final String resourceValue = "A Resource value";
		Object retVal = txTempl.execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus status) {
				try {
					TransactionSynchronizationManager.bindResource(
							resourceName, resourceValue);
					txSyncManWrapper.unbindResource(resourceName);
					return txSyncManWrapper.getResource(resourceName);
				} finally {
					TransactionSynchronizationManager
							.unbindResourceIfPossible(resourceName);
				}
			}
		});
		assertNull(resourceValue, retVal);
	}

	public void testIsSyncActive() {
		Object retVal = txTempl.execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus status) {
				return txSyncManWrapper.isSynchronizationActive();
			}
		});
		assertEquals(true, retVal);
	}

	public void testRegisterSynchronization() {

		// Create a synchronization to register, and call if successful
		final TrialTransactionSync txSync = new TrialTransactionSync();

		Object retVal = txTempl.execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus status) {
				txSyncManWrapper.registerSynchronization(txSync);
				return TransactionSynchronizationManager.getSynchronizations()
						.contains(txSync);
			}
		});

		assertEquals(true, retVal);
		assertTrue(txSync.wasCalled());
	}

	public void testContainsSynchronization() {

		// Create a synchronization to register, and call if successful
		final TrialTransactionSync txSync = new TrialTransactionSync();

		Object retVal = txTempl.execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus status) {
				TransactionSynchronizationManager
						.registerSynchronization(txSync);
				return txSyncManWrapper
						.containsSynchronization(TrialTransactionSync.class);
			}
		});

		assertEquals(true, retVal);
		assertTrue(txSync.wasCalled());
	}
}

/**
 * A transaction synchronization that allows us to test whether registration is
 * working and that the synchronization is called.
 * 
 * @author batkinson
 */
class TrialTransactionSync extends TransactionSynchronizationAdapter {
	boolean called = false;

	@Override
	public void beforeCommit(boolean readOnly) {
		called = true;
	}

	public boolean wasCalled() {
		return called;
	}
}