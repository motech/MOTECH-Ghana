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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.omod.impl.ContextServiceImpl;
import org.motechproject.server.service.ContextService;
import org.motechproject.server.util.MotechConstants;
import org.openmrs.scheduler.tasks.AbstractTask;

import java.text.SimpleDateFormat;
import java.util.Date;

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

        Boolean sendUpcoming = Boolean.valueOf(taskDefinition.getProperty(MotechConstants.TASK_PROPERTY_SEND_UPCOMING));
        String careGroupsProperty = taskDefinition.getProperty(MotechConstants.TASK_PROPERTY_CARE_GROUPS);
        String[] careGroups = StringUtils.split(careGroupsProperty, MotechConstants.TASK_PROPERTY_CARE_GROUPS_DELIMITER);
        Boolean avoidBlackout = Boolean.valueOf(taskDefinition.getProperty(MotechConstants.TASK_PROPERTY_AVOID_BLACKOUT));
        String deliveryTimeString = this.taskDefinition.getProperty(MotechConstants.TASK_PROPERTY_DELIVERY_TIME);
        Boolean sendNoDefaulterAndNoUpcomingCareMessage = Boolean.valueOf(taskDefinition.getProperty(MotechConstants.TASK_PROPERTY_SEND_NO_DEFAULTER_AND_NO_UPCOMING_CARE_MESSAGE));

        Date today = new Date();
        final long repeatIntervalInMilliSeconds = taskDefinition.getRepeatInterval() * 1000;
        Date endDate = new Date(today.getTime() + repeatIntervalInMilliSeconds);
        Date deliveryDate = null;

        Date deliveryTime = null;
        if (deliveryTimeString != null) {
            SimpleDateFormat timeFormat = new SimpleDateFormat(MotechConstants.TIME_FORMAT_DELIVERY_TIME);
            try {
                deliveryTime = timeFormat.parse(deliveryTimeString);
            } catch (Exception e) {
                log.error("Error parsing staff messaging task " + "delivery time", e);
            }
        }
        // If the delivery time property is set,
        // use the current date as the delivery date
        // otherwise the delivery time is null for immediate messaging
        if (deliveryTime != null) {
            deliveryDate = today;
        }

        // Session required for Task to get RegistrarBean through Context
        try {
            contextService.openSession();
            contextService.getStaffMessageService().sendStaffCareMessages(today, endDate, deliveryDate, deliveryTime, careGroups, sendUpcoming,
                    avoidBlackout, sendNoDefaulterAndNoUpcomingCareMessage);
        } finally {
            contextService.closeSession();
        }
        long end = System.currentTimeMillis();
        long runtime = (end - start) / 1000;
        log.info("executed for " + runtime + " seconds");
    }

}
