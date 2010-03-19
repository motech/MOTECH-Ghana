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
