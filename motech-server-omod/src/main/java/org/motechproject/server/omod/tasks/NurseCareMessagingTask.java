package org.motechproject.server.omod.tasks;

import java.text.SimpleDateFormat;
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

		String deliveryTimeString = this.taskDefinition
				.getProperty(MotechConstants.TASK_PROPERTY_DELIVERY_TIME);

		Date currentDate = new Date();
		Date endDate = new Date(currentDate.getTime()
				+ (this.taskDefinition.getRepeatInterval() * 1000));

		Date deliveryTime = null;
		if (deliveryTimeString != null) {
			SimpleDateFormat timeFormat = new SimpleDateFormat(
					MotechConstants.TIME_FORMAT_DELIVERY_TIME);
			try {
				deliveryTime = timeFormat.parse(deliveryTimeString);
			} catch (Exception e) {
				log.error("Error parsing nurse messaging task "
						+ "delivery time", e);
			}
		}

		// Session required for Task to get RegistrarBean through Context
		try {
			contextService.openSession();
			contextService.getRegistrarBean().sendNurseCareMessages(
					currentDate, endDate, currentDate, deliveryTime,
					careGroups, sendUpcoming);
		} finally {
			contextService.closeSession();
		}
	}

}
