package org.motechproject.server.omod.tasks;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.impl.ContextServiceImpl;
import org.openmrs.scheduler.tasks.AbstractTask;

public class CareScheduleUpdateTask extends AbstractTask {

	private static Log log = LogFactory.getLog(CareScheduleUpdateTask.class);

	private ContextService contextService;

	public CareScheduleUpdateTask() {
		contextService = new ContextServiceImpl();
	}

	public void setContextService(ContextService contextService) {
		this.contextService = contextService;
	}

	/**
	 * @see org.openmrs.scheduler.tasks.AbstractTask#execute()
	 */
	@Override
	public void execute() {
		long start = System.currentTimeMillis();
		log
				.debug("Care Schedule Task - Update Care Schedules for all Patients");

		// Session required for Task to get RegistrarBean through Context
		try {
			contextService.openSession();
			contextService.getRegistrarBean().updateAllCareSchedules();
		} finally {
			contextService.closeSession();
		}
		long end = System.currentTimeMillis();
		long runtime = (end - start) / 1000;
		log.info("executed for " + runtime + " seconds");
	}

}
