/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.motechproject.server.openmrs.module.tasks;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.openmrs.module.ContextService;
import org.motechproject.server.openmrs.module.impl.ContextServiceImpl;
import org.motechproject.server.util.MotechConstants;
import org.openmrs.scheduler.tasks.AbstractTask;

/**
 * Defines a task implementation that OpenMRS can execute using the built-in
 * task scheduler. This is how periodic notifications are handled for the
 * OpenMRS motech server implementation. It periodically runs, looks up stored
 * Message objects and constructs and sends messages to patients and nurses if
 * required.
 */
public class NotificationTask extends AbstractTask {

	private static Log log = LogFactory.getLog(NotificationTask.class);

	private ContextService contextService;

	public NotificationTask() {
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

		log.debug("executing task");
		
		String timeOffsetString = this.taskDefinition
				.getProperty(MotechConstants.TASK_PROPERTY_TIME_OFFSET);
		Boolean sendImmediate = Boolean.valueOf(taskDefinition
				.getProperty(MotechConstants.TASK_PROPERTY_SEND_IMMEDIATE));
		Long timeOffset = 0L;
		if (timeOffsetString != null) {
			timeOffset = Long.valueOf(timeOffsetString);
		}

		Date startDate = new Date(System.currentTimeMillis()
				+ (timeOffset * 1000));
		Date endDate = new Date(System.currentTimeMillis()
				+ (this.taskDefinition.getRepeatInterval() * 1000)
				+ (timeOffset * 1000));

		// Session required for Task to get RegistrarBean through Context
		try {
			contextService.openSession();
			contextService.getRegistrarBean().sendMessages(startDate, endDate,
					sendImmediate);
		} finally {
			contextService.closeSession();
		}
	}

}
