package org.motech.openmrs.module.sdsched;

import org.openmrs.PatientIdentifier;

/**
 * Implementation of {@link ScheduleMaintService}.
 * 
 * @author batkinson
 * 
 */
public class ScheduleMaintServiceImpl implements ScheduleMaintService {

	public static String RESOURCE_NAME = "_DS_SCHEDULER_UTILS_RESOURCE";

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
		AffectedPatients patients = (AffectedPatients) syncManWrapper
				.getResource(RESOURCE_NAME);
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

	public void addAffectedPatient(PatientIdentifier patientId) {
		getAffectedPatients(true).getAffectedIds().add(patientId);
	}

	public void removeAffectedPatient(PatientIdentifier patientId) {
		AffectedPatients patients = getAffectedPatients();
		if (patients != null)
			patients.getAffectedIds().remove(patientId);
	}

	public void clearAffectedPatients() {
		syncManWrapper.unbindResource(RESOURCE_NAME);
	}

	public void requestSynch() {
		ScheduleMaintSynchronization schedSync = new ScheduleMaintSynchronization();
		schedSync.setSchedService(this);
		syncManWrapper.registerSynchronization(schedSync);
	}

	public void updateSchedule(PatientIdentifier patientId) {
		scheduleAdjuster.adjustSchedule(patientId);
	}
}
