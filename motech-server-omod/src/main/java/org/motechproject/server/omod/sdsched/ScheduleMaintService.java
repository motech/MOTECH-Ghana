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


/**
 * Interface defining a service for performing operations required for
 * maintaining patient service delivery schedules.
 * 
 * @author batkinson
 * 
 */
public interface ScheduleMaintService {

	/**
	 * Adds an affected patient identifier to the set touched by this
	 * transaction. If no set is active, it adds the holder.
	 * 
	 * @param patientId
	 *            patient id to add
	 */
	void addAffectedPatient(Integer patientId);

	/**
	 * Removes the specified patient identifier from the set of affected patient
	 * ids for the transaction.
	 * 
	 * @param patientId
	 *            the patient id to remove.
	 */
	void removeAffectedPatient(Integer patientId);

	/**
	 * Clears the affected patient set for the transaction.
	 */
	void clearAffectedPatients();

	/**
	 * Returns the {@link AffectedPatients} object for the current transaction.
	 * If there isn't one, if create is true one will be created and bound to
	 * the transaction.
	 * 
	 * @param create
	 *            whether to create a new AffectedPatients object if
	 *            non-existent
	 * @return the AffectedPatients object bound to the current transaction or
	 *         null, if create is true, an object will always be returned
	 */
	AffectedPatients getAffectedPatients(boolean create);

	/**
	 * Flags transaction as requiring schedule adjustments.
	 */
	void requestSynch();

	/**
	 * Adjusts the specified patients schedule to reflect their current status.
	 * 
	 * @param patientId
	 */
	void updateSchedule(Integer patientId);
}
