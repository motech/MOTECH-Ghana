package org.motechproject.server.omod.sdsched;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;

/**
 * A transaction synchronization that handles tidying up patient schedules after
 * a transaction. It simply inspects the set of affected patients and executes
 * an adjustment routine to patch-up the affected patient schedule.
 * 
 * @author batkinson
 * 
 */
public class ScheduleMaintSynchronization extends
		TransactionSynchronizationAdapter implements TransactionSynchronization {

	private static Log log = LogFactory
			.getLog(ScheduleMaintSynchronization.class);

	ScheduleMaintService schedService;

	public void setSchedService(ScheduleMaintService schedService) {
		this.schedService = schedService;
	}

	/**
	 * Called before the commit, it is responsible for patch-up the schedules
	 * for patients affected during the transaction.
	 */
	public void beforeCommit(boolean readOnly) {

		if (!readOnly) {

			log.debug("running schedule sync");

			AffectedPatients patients = schedService.getAffectedPatients(false);

			if (patients != null) {

				if (log.isDebugEnabled())
					log.debug(patients.getAffectedIds().size()
							+ " affected patients, updating schedules");

				// Update each patient's schedule in turn (stubbed for now)
				Iterator<Integer> patientIter = patients.getAffectedIds()
						.iterator();
				while (patientIter.hasNext()) {
					Integer patientId = patientIter.next();
					if (log.isDebugEnabled())
						log.debug("updating schedule for patientId: "
								+ patientId);

					schedService.updateSchedule(patientId);

					patientIter.remove(); // Ensure we don't re-process
				}

			} else
				log.debug("null affected patients, no schedules updated");
		} else
			log.debug("skipping schedule sync: read-only transaction");
	}
}
