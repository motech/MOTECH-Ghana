package org.motechproject.server.omod.sdsched;

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
