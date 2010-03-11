package org.motech.openmrs.module.sdsched;

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

	public void registerSynchronization(TransactionSynchronization sync) {
		TransactionSynchronizationManager.registerSynchronization(sync);
	}

	public void unbindResource(String resourceName) {
		TransactionSynchronizationManager.unbindResource(resourceName);
	}
}
