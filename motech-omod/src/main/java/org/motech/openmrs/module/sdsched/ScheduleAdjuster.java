package org.motech.openmrs.module.sdsched;

import org.openmrs.PatientIdentifier;

/**
 * An interface defining operations for adjusting patient service delivery
 * schedules.
 * 
 * @author batkinson
 * 
 */
public interface ScheduleAdjuster {

	void adjustSchedule(PatientIdentifier patientId);

}
