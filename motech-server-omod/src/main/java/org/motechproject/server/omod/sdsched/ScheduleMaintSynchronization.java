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
