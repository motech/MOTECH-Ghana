/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010-11 The Trustees of Columbia University in the City of
 * New York and Grameen Foundation USA.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Grameen Foundation USA, Columbia University, or
 * their respective contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA, COLUMBIA UNIVERSITY
 * AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION
 * USA, COLUMBIA UNIVERSITY OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.motechproject.server.omod.tasks;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.service.ContextService;
import org.motechproject.server.omod.impl.ContextServiceImpl;
import org.motechproject.server.util.MotechConstants;
import org.openmrs.scheduler.tasks.AbstractTask;

import java.util.Date;

/**
 * Defines a task implementation that OpenMRS can execute using the built-in
 * task scheduler. This is how periodic notifications are handled for the
 * OpenMRS motech server implementation. It periodically runs, looks up stored
 * Message objects and constructs and sends messages to patients and staff if
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
		long start = System.currentTimeMillis();
		log.info("executing task");

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
		long end = System.currentTimeMillis();
		long runtime = (end - start) / 1000;
		log.info("executed for " + runtime + " seconds");
	}

}
