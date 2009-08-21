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
package org.motech.tasks;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motech.model.FutureServiceDelivery;
import org.motech.model.LogType;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.motechmodule.MotechService;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.openmrs.util.OpenmrsConstants;

public class NotificationTask extends AbstractTask {
	
	private static Log log = LogFactory.getLog(NotificationTask.class);
	
	/**
	 * @see org.openmrs.scheduler.tasks.AbstractTask#execute()
	 */
	@Override
	public void execute() {
		
		Date startDate = new Date(System.currentTimeMillis() - (this.taskDefinition.getRepeatInterval() * 1000));
		Date endDate = new Date(System.currentTimeMillis() + (this.taskDefinition.getRepeatInterval() * 1000));
		
		try {
			Context.openSession();
			Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES);
			
			List<FutureServiceDelivery> futureServices = Context.getService(MotechService.class).getFutureServiceDeliveries(
			    startDate, endDate);
			
			if (log.isDebugEnabled()) {
				log.debug("Notification Task executed, Service Deliveries found: " + futureServices.size());
			}
			
			if (futureServices.size() > 0) {
				Date notificationDate = new Date();
				PersonAttributeType phoneNumberType = Context.getPersonService()
				        .getPersonAttributeTypeByName("Phone Number");
				for (FutureServiceDelivery service : futureServices) {
					
					if (service.getPatientNotifiedDate() == null) {
						String patientPhone = service.getPatient().getAttribute(phoneNumberType).getValue();
						
						org.motech.model.Log motechLog = new org.motech.model.Log();
						motechLog.setType(LogType.success);
						motechLog.setDate(notificationDate);
						motechLog.setMessage("Future Service Delivery Notification, Patient Phone: " + patientPhone);
						Context.getService(MotechService.class).saveLog(motechLog);
						
						service.setPatientNotifiedDate(notificationDate);
					}
					
					if (service.getUserNotifiedDate() == null) {
						String nursePhone = service.getUser().getAttribute(phoneNumberType).getValue();
						
						org.motech.model.Log motechLog = new org.motech.model.Log();
						motechLog.setType(LogType.success);
						motechLog.setDate(notificationDate);
						motechLog.setMessage("Future Service Delivery Notification, Nurse Phone: " + nursePhone);
						Context.getService(MotechService.class).saveLog(motechLog);
						
						service.setUserNotifiedDate(notificationDate);
					}
					Context.getService(MotechService.class).updateFutureServiceDelivery(service);
				}
			}
		}
		finally {
			Context.closeSession();
		}
	}
	
}
