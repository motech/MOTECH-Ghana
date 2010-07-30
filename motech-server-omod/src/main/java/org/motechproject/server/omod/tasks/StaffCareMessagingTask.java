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

public class StaffCareMessagingTask extends AbstractTask {

	private static Log log = LogFactory.getLog(StaffCareMessagingTask.class);

	private ContextService contextService;

	public StaffCareMessagingTask() {
		contextService = new ContextServiceImpl();
	}

	public void setContextService(ContextService contextService) {
		this.contextService = contextService;
	}

	@Override
	public void execute() {
		long start = System.currentTimeMillis();
		log.debug("Executing Task - Sending Care Messages to all Staff");

		Boolean sendUpcoming = Boolean.valueOf(this.taskDefinition
				.getProperty(MotechConstants.TASK_PROPERTY_SEND_UPCOMING));
		String careGroupsProperty = this.taskDefinition
				.getProperty(MotechConstants.TASK_PROPERTY_CARE_GROUPS);
		String[] careGroups = StringUtils.split(careGroupsProperty,
				MotechConstants.TASK_PROPERTY_CARE_GROUPS_DELIMITER);
		Boolean avoidBlackout = Boolean.valueOf(this.taskDefinition
				.getProperty(MotechConstants.TASK_PROPERTY_AVOID_BLACKOUT));

		String deliveryTimeString = this.taskDefinition
				.getProperty(MotechConstants.TASK_PROPERTY_DELIVERY_TIME);

		Date currentDate = new Date();
		Date endDate = new Date(currentDate.getTime()
				+ (this.taskDefinition.getRepeatInterval() * 1000));
		Date deliveryDate = null;

		Date deliveryTime = null;
		if (deliveryTimeString != null) {
			SimpleDateFormat timeFormat = new SimpleDateFormat(
					MotechConstants.TIME_FORMAT_DELIVERY_TIME);
			try {
				deliveryTime = timeFormat.parse(deliveryTimeString);
			} catch (Exception e) {
				log.error("Error parsing staff messaging task "
						+ "delivery time", e);
			}
		}
		// If the delivery time property is set,
		// use the current date as the delivery date
		// otherwise the delivery time is null for immediate messaging
		if (deliveryTime != null) {
			deliveryDate = currentDate;
		}

		// Session required for Task to get RegistrarBean through Context
		try {
			contextService.openSession();
			contextService.getRegistrarBean().sendStaffCareMessages(
					currentDate, endDate, deliveryDate, deliveryTime,
					careGroups, sendUpcoming, avoidBlackout);
		} finally {
			contextService.closeSession();
		}
		long end = System.currentTimeMillis();
		long runtime = (end - start) / 1000;
		log.info("executed for " + runtime + " seconds");
	}

}
