package org.motechproject.server.omod.tasks;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.impl.ContextServiceImpl;
import org.motechproject.server.util.MotechConstants;
import org.openmrs.scheduler.tasks.AbstractTask;

public class NurseCareMessagingTask extends AbstractTask {

	private static Log log = LogFactory.getLog(NurseCareMessagingTask.class);

	private ContextService contextService;

	public NurseCareMessagingTask() {
		contextService = new ContextServiceImpl();
	}

	public void setContextService(ContextService contextService) {
		this.contextService = contextService;
	}

	@Override
	public void execute() {
		log.debug("Executing Task - Sending Care Messages to all Nurses");

		Boolean sendUpcoming = Boolean.valueOf(this.taskDefinition
				.getProperty(MotechConstants.TASK_PROPERTY_SEND_UPCOMING));
		String careGroupsProperty = this.taskDefinition
				.getProperty(MotechConstants.TASK_PROPERTY_CARE_GROUPS);
		String[] careGroups = StringUtils.split(careGroupsProperty,
				MotechConstants.TASK_PROPERTY_CARE_GROUPS_DELIMITER);

		String deliveryTimeOffsetString = this.taskDefinition
				.getProperty(MotechConstants.TASK_PROPERTY_DELIVERY_TIME_OFFSET);
		Long deliveryTimeOffset = 0L;
		if (deliveryTimeOffsetString != null) {
			deliveryTimeOffset = Long.valueOf(deliveryTimeOffsetString);
		}

		Date startDate = new Date();
		Date endDate = new Date(startDate.getTime()
				+ (this.taskDefinition.getRepeatInterval() * 1000));
		Date deliveryDate = new Date(startDate.getTime()
				+ (deliveryTimeOffset * 1000));

		// Session required for Task to get RegistrarBean through Context
		try {
			contextService.openSession();
			contextService.getRegistrarBean().sendNurseCareMessages(startDate,
					endDate, deliveryDate, careGroups, sendUpcoming);
		} finally {
			contextService.closeSession();
		}
	}

}
