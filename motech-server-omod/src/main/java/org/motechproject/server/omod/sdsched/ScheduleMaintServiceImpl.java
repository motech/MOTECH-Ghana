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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementation of {@link ScheduleMaintService}.
 * 
 * @author batkinson
 * 
 */
public class ScheduleMaintServiceImpl implements ScheduleMaintService {

	private static Log log = LogFactory.getLog(ScheduleMaintServiceImpl.class);
	public static final String RESOURCE_NAME = "_DS_SCHEDULER_UTILS_RESOURCE";

	TxSyncManWrapper syncManWrapper;
	ScheduleAdjuster scheduleAdjuster;

	public void setSyncManWrapper(TxSyncManWrapper txSyncManWrapper) {
		this.syncManWrapper = txSyncManWrapper;
	}

	public void setScheduleAdjuster(ScheduleAdjuster scheduleAdjuster) {
		this.scheduleAdjuster = scheduleAdjuster;
	}

	/**
	 * Returns the {@link AffectedPatients} object for the current transaction.
	 * 
	 * @return the AffectedPatients object for the current tx, or null if
	 *         non-existent
	 */
	private AffectedPatients getAffectedPatients() {
		AffectedPatients patients = null;
		try {
			patients = (AffectedPatients) syncManWrapper.getResource(RESOURCE_NAME);
		} catch (ClassCastException e) {
			log.debug("Cannot cast affected patients", e);
			clearAffectedPatients();
		}
		return patients;
	}

	public AffectedPatients getAffectedPatients(boolean create) {
		AffectedPatients patients = getAffectedPatients();
		if (patients == null && create) {
			patients = new AffectedPatients();
			syncManWrapper.bindResource(RESOURCE_NAME, patients);
		}
		return patients;
	}

	public void addAffectedPatient(Integer patientId) {
		getAffectedPatients(true).getAffectedIds().add(patientId);
	}

	public void removeAffectedPatient(Integer patientId) {
		AffectedPatients patients = getAffectedPatients();
		if (patients != null)
			patients.getAffectedIds().remove(patientId);
	}

	public void clearAffectedPatients() {
		syncManWrapper.unbindResource(RESOURCE_NAME);
	}

	public void requestSynch() {
		if (!syncManWrapper.containsSynchronization(ScheduleMaintSynchronization.class)) {
			ScheduleMaintSynchronization schedSync = new ScheduleMaintSynchronization();
			schedSync.setSchedService(this);
			syncManWrapper.registerSynchronization(schedSync);
		}
	}

	public void updateSchedule(Integer patientId) {
		scheduleAdjuster.adjustSchedule(patientId);
	}
}
