package org.motechproject.server.omod.tasks;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.impl.ContextServiceImpl;
import org.openmrs.scheduler.tasks.AbstractTask;

public class MessageProgramUpdateTask extends AbstractTask {

	private static Log log = LogFactory.getLog(MessageProgramUpdateTask.class);

	private ContextService contextService;

	public MessageProgramUpdateTask() {
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
				.debug("Message Program Task - Update Enrolled Programs for all Patients");

		// Session required for Task to get RegistrarBean through Context
		try {
			contextService.openSession();
			contextService.getRegistrarBean().updateAllMessageProgramsState();
		} finally {
			contextService.closeSession();
		}
		long end = System.currentTimeMillis();
		long runtime = (end - start) / 1000;
		log.info("executed for " + runtime + " seconds");
	}

}
