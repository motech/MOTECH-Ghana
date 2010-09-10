package org.motechproject.server.omod.tasks;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.impl.ContextServiceImpl;
import org.motechproject.server.util.MotechConstants;
import org.openmrs.scheduler.TaskDefinition;
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

		Integer batchSize = null;
		String batchSizeProperty = taskDefinition
				.getProperty(MotechConstants.TASK_PROPERTY_BATCH_SIZE);
		if (batchSizeProperty != null) {
			try {
				batchSize = Integer.valueOf(batchSizeProperty);
			} catch (NumberFormatException e) {
				log.error("Invalid Integer batch size value", e);
			}
		}

		Long batchPreviousId = null;
		String batchPreviousProperty = taskDefinition
				.getProperty(MotechConstants.TASK_PROPERTY_BATCH_PREVIOUS_ID);
		if (batchPreviousProperty != null) {
			try {
				batchPreviousId = Long.valueOf(batchPreviousProperty);
			} catch (NumberFormatException e) {
				log.error("Invalid Long batch previous id value", e);
			}
		}

		Long batchMaxId = null;
		String batchMaxProperty = taskDefinition
				.getProperty(MotechConstants.TASK_PROPERTY_BATCH_MAX_ID);
		if (batchMaxProperty != null) {
			try {
				batchMaxId = Long.valueOf(batchMaxProperty);
			} catch (NumberFormatException e) {
				log.error("Invalid Long batch maximum id value", e);
			}
		}

		// Session required for Task to get RegistrarBean through Context
		try {
			contextService.openSession();
			TaskDefinition updatedTask = contextService.getRegistrarBean()
					.updateAllMessageProgramsState(batchSize, batchPreviousId,
							batchMaxId);

			if (updatedTask != null) {
				// Updates this running task to use newly stored properties
				this.initialize(updatedTask);
			}
		} finally {
			contextService.closeSession();
		}
		long end = System.currentTimeMillis();
		long runtime = (end - start) / 1000;
		log.info("executed for " + runtime + " seconds");
	}

}
