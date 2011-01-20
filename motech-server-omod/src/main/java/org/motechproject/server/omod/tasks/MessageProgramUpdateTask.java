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

		// Session required for Task to get RegistrarBean through Context
		try {
			contextService.openSession();
			TaskDefinition updatedTask = contextService.getRegistrarBean()
					.updateAllMessageProgramsState(batchSize, batchPreviousId);

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
