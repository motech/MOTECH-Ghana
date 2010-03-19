package org.motechproject.server.openmrs.module.sdsched;

/**
 * An interface defining operations for adjusting patient service delivery
 * schedules.
 * 
 * @author batkinson
 * 
 */
public interface ScheduleAdjuster {

	void adjustSchedule(Integer patientId);

}
