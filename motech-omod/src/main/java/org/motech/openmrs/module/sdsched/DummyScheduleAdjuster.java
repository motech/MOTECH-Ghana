package org.motech.openmrs.module.sdsched;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.PatientIdentifier;

/**
 * A dummy implementation of a schedule adjuster, so that we can simply watch
 * the adjustment requests fly by without actually doing anything.
 * 
 * @author batkinson
 * 
 */
public class DummyScheduleAdjuster implements ScheduleAdjuster {

	private Log log = LogFactory.getLog(DummyScheduleAdjuster.class);

	public void adjustSchedule(PatientIdentifier patientId) {
		if (log.isInfoEnabled())
			log.info("adjust schedule, patientId=" + patientId);
	}

}
