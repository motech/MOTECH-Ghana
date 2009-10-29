package org.motech.tasks;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motech.openmrs.module.ContextService;
import org.motech.openmrs.module.impl.ContextServiceImpl;
import org.openmrs.scheduler.tasks.AbstractTask;

public class RegimenUpdateTask extends AbstractTask {

	private static Log log = LogFactory.getLog(RegimenUpdateTask.class);

	private ContextService contextService;

	public RegimenUpdateTask() {
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
		log.debug("Regimen Task - Update Enrolled Regimens for all Patients");

		// Session required for Task to get RegimenBean through Context
		try {
			contextService.openSession();
			contextService.getRegistrarBean().updateAllRegimenState();
		} finally {
			contextService.closeSession();
		}
	}

}
