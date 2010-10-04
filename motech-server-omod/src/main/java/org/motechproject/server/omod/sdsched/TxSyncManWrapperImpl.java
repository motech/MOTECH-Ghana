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

import java.util.List;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Implementation of the {@link TxSyncManWrapper}.
 * 
 * @author batkinson
 * 
 */
public class TxSyncManWrapperImpl implements TxSyncManWrapper {

	public void bindResource(String resourceName, Object obj) {
		TransactionSynchronizationManager.bindResource(resourceName, obj);
	}

	public Object getResource(String resourceName) {
		return TransactionSynchronizationManager.getResource(resourceName);
	}

	public boolean isSynchronizationActive() {
		return TransactionSynchronizationManager.isSynchronizationActive();
	}

	@SuppressWarnings("unchecked")
	public boolean containsSynchronization(
			Class<? extends TransactionSynchronization> syncClass) {

		if (isSynchronizationActive()) {
			List<TransactionSynchronization> syncs = (List<TransactionSynchronization>) TransactionSynchronizationManager
					.getSynchronizations();

			for (TransactionSynchronization sync : syncs)
				if (syncClass.isAssignableFrom(sync.getClass()))
					return true;
		}

		return false;
	}

	public void registerSynchronization(TransactionSynchronization sync) {
		TransactionSynchronizationManager.registerSynchronization(sync);
	}

	public void unbindResource(String resourceName) {
		TransactionSynchronizationManager.unbindResource(resourceName);
	}
}
