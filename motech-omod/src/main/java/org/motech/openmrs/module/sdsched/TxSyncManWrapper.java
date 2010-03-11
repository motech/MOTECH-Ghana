package org.motech.openmrs.module.sdsched;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * A wrapper for Spring's {@link TransactionSynchronizationManager} so that we
 * can test code that interacts with the transaction manager.
 * 
 * @author batkinson
 * 
 */
public interface TxSyncManWrapper {

	Object getResource(String resourceName);

	void bindResource(String resourceName, Object object);

	void unbindResource(String resourceName);

	void registerSynchronization(TransactionSynchronization sync);

}
