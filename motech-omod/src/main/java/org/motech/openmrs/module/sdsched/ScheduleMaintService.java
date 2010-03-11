package org.motech.openmrs.module.sdsched;

import org.openmrs.PatientIdentifier;

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
	void addAffectedPatient(PatientIdentifier patientId);

	/**
	 * Removes the specified patient identifier from the set of affected patient
	 * ids for the transaction.
	 * 
	 * @param patientId
	 *            the patient id to remove.
	 */
	void removeAffectedPatient(PatientIdentifier patientId);

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
	void updateSchedule(PatientIdentifier patientId);
}
