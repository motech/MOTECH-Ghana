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
import org.motech.model.NotificationType;
import org.motech.model.PhoneType;
import org.motech.openmrs.module.MotechService;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.scheduler.tasks.AbstractTask;

import com.dreamoval.motech.omi.service.ContactNumberType;
import com.dreamoval.motech.omi.service.MessageType;

public class NotificationTask extends AbstractTask {

	private static Log log = LogFactory.getLog(NotificationTask.class);

	/**
	 * @see org.openmrs.scheduler.tasks.AbstractTask#execute()
	 */
	@Override
	public void execute() {

		Date startDate = new Date(System.currentTimeMillis()
				- (this.taskDefinition.getRepeatInterval() * 1000));
		Date endDate = new Date(System.currentTimeMillis()
				+ (this.taskDefinition.getRepeatInterval() * 1000));

		try {
			Context.openSession();
			if (!Context.isAuthenticated()) {
				authenticate();
			}

			List<FutureServiceDelivery> futureServices = Context.getService(
					MotechService.class).getFutureServiceDeliveries(startDate,
					endDate);

			if (log.isDebugEnabled()) {
				log
						.debug("Notification Task executed, Service Deliveries found: "
								+ futureServices.size());
			}

			if (futureServices.size() > 0) {
				Date notificationDate = new Date();
				PersonAttributeType phoneNumberType = Context
						.getPersonService().getPersonAttributeTypeByName(
								"Phone Number");
				PersonAttributeType phoneType = Context.getPersonService()
						.getPersonAttributeTypeByName("Phone Type");
				PersonAttributeType notificationType = Context
						.getPersonService().getPersonAttributeTypeByName(
								"Notification Type");
				PatientIdentifierType serialIdType = Context
						.getPatientService().getPatientIdentifierTypeByName(
								"Ghana Clinic Id");
				for (FutureServiceDelivery service : futureServices) {

					if (service.getPatientNotifiedDate() == null) {
						Patient patient = service.getPatient();
						String patientPhone = patient.getAttribute(
								phoneNumberType).getValue();
						String clinicName = patient.getPatientIdentifier(
								serialIdType).getLocation().getName();
						String phoneTypeString = patient
								.getAttribute(phoneType).getValue();
						String notificationTypeString = patient.getAttribute(
								notificationType).getValue();
						ContactNumberType patientNumberType = PhoneType
								.valueOf(phoneTypeString).toContactNumberType();
						MessageType messageType = NotificationType.valueOf(
								notificationTypeString).toMessageType();

						org.motech.model.Log motechLog = new org.motech.model.Log();
						motechLog.setType(LogType.success);
						motechLog.setDate(notificationDate);
						motechLog
								.setMessage("Future Service Delivery Notification, Patient Phone: "
										+ patientPhone);
						Context.getService(MotechService.class).saveLog(
								motechLog);

						Context.getService(MotechService.class)
								.getMobileService().sendPatientMessage(
										new Long(1), clinicName,
										notificationDate, patientPhone,
										patientNumberType, messageType);

						service.setPatientNotifiedDate(notificationDate);
					}

					if (service.getUserNotifiedDate() == null) {
						User nurse = service.getUser();
						String nursePhone = nurse.getAttribute(phoneNumberType)
								.getValue();
						String nurseName = nurse.getPersonName().toString();

						org.motech.model.Log motechLog = new org.motech.model.Log();
						motechLog.setType(LogType.success);
						motechLog.setDate(notificationDate);
						motechLog
								.setMessage("Future Service Delivery Notification, Nurse Phone: "
										+ nursePhone);
						Context.getService(MotechService.class).saveLog(
								motechLog);

						Context.getService(MotechService.class)
								.getMobileService().sendCHPSMessage(
										new Long(1), nurseName, nursePhone,
										null);

						service.setUserNotifiedDate(notificationDate);
					}
					Context.getService(MotechService.class)
							.updateFutureServiceDelivery(service);
				}
			}
		} catch (Exception e) {
			log.error(e);
		} finally {
			Context.closeSession();
		}
	}

}