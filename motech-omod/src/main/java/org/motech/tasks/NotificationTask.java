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
import org.motech.messaging.Message;
import org.motech.messaging.MessageStatus;
import org.motech.model.TroubledPhone;
import org.motech.openmrs.module.MotechService;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.LogType;
import org.motechproject.ws.MediaType;
import org.motechproject.ws.NameValuePair;
import org.openmrs.Patient;
import org.openmrs.PersonAttributeType;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.openmrs.util.OpenmrsConstants;

/**
 * Defines a task implementation that OpenMRS can execute using the built-in
 * task scheduler. This is how periodic notifications are handled for the
 * OpenMRS motech server implementation. It periodically runs, looks up stored
 * Message objects and constructs and sends messages to patients and nurses if
 * required.
 */
public class NotificationTask extends AbstractTask {

	private static Log log = LogFactory.getLog(NotificationTask.class);

	/**
	 * @see org.openmrs.scheduler.tasks.AbstractTask#execute()
	 */
	@Override
	public void execute() {

		Date startDate = new Date();
		Date endDate = new Date(System.currentTimeMillis()
				+ (this.taskDefinition.getRepeatInterval() * 1000));

		try {
			Context.openSession();
			Context
					.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES);
			Context
					.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_IDENTIFIER_TYPES);
			Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PATIENTS);
			Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_USERS);

			List<Message> shouldAttemptMessages = Context.getService(
					MotechService.class).getMessages(startDate, endDate,
					MessageStatus.SHOULD_ATTEMPT);

			if (log.isDebugEnabled()) {
				log
						.debug("Notification Task executed, Should Attempt Messages found: "
								+ shouldAttemptMessages.size());
			}

			if (shouldAttemptMessages.size() > 0) {
				Date notificationDate = new Date();
				PersonAttributeType phoneNumberType = Context
						.getPersonService().getPersonAttributeTypeByName(
								"Phone Number");
				PersonAttributeType phoneType = Context.getPersonService()
						.getPersonAttributeTypeByName("Phone Type");
				PersonAttributeType mediaAttrType = Context.getPersonService()
						.getPersonAttributeTypeByName("Media Type");

				for (Message shouldAttemptMessage : shouldAttemptMessages) {

					org.motech.model.Log motechLog = new org.motech.model.Log();
					motechLog.setDate(notificationDate);

					String messageId = shouldAttemptMessage.getPublicId();
					Long notificationType = shouldAttemptMessage.getSchedule()
							.getMessage().getPublicId();
					Integer recipientId = shouldAttemptMessage.getSchedule()
							.getRecipientId();
					Patient patient = Context.getPatientService().getPatient(
							recipientId);
					User nurse = Context.getUserService().getUser(recipientId);

					if (patient != null) {
						String patientFirstName = patient.getPersonName()
								.getGivenName();
						String patientPhone = patient.getAttribute(
								phoneNumberType).getValue();
						String phoneTypeString = patient
								.getAttribute(phoneType).getValue();
						String mediaTypeString = patient.getAttribute(
								mediaAttrType).getValue();
						ContactNumberType patientNumberType = ContactNumberType
								.valueOf(phoneTypeString);

						NameValuePair[] personalInfo = new NameValuePair[1];
						personalInfo[0] = new NameValuePair();
						personalInfo[0].setName("PatientFirstName");
						personalInfo[0].setValue(patientFirstName);
						String langCode = null;
						MediaType mediaType = MediaType
								.valueOf(mediaTypeString);
						Date messageStartDate = null;
						Date messageEndDate = null;

						// Cancel message if patient phone is considered
						// troubled
						TroubledPhone troubledPhone = Context.getService(
								MotechService.class).getTroubledPhone(
								patientPhone);
						Integer maxFailures = Integer
								.parseInt(Context
										.getAdministrationService()
										.getGlobalProperty(
												"motechmodule.troubled_phone_failures"));
						if (troubledPhone != null
								&& troubledPhone.getSendFailures() >= maxFailures) {
							motechLog
									.setMessage("Attempt to send to Troubled Phone, Patient Phone: "
											+ patientPhone
											+ ", Message cancelled: "
											+ notificationType);
							motechLog.setType(LogType.FAILURE);

							shouldAttemptMessage
									.setAttemptStatus(MessageStatus.CANCELLED);

						} else {
							motechLog
									.setMessage("Scheduled Message Notification, Patient Phone: "
											+ patientPhone
											+ ": "
											+ notificationType);

							try {
								Context.getService(MotechService.class)
										.getMobileService().sendPatientMessage(
												messageId, personalInfo,
												patientPhone,
												patientNumberType, langCode,
												mediaType, notificationType,
												messageStartDate,
												messageEndDate);
								shouldAttemptMessage
										.setAttemptStatus(MessageStatus.ATTEMPT_PENDING);
								motechLog.setType(LogType.SUCCESS);
							} catch (Exception e) {
								log.error("Mobile patient message failure", e);
								shouldAttemptMessage
										.setAttemptStatus(MessageStatus.ATTEMPT_FAIL);
								motechLog.setType(LogType.FAILURE);
							}
						}

					} else if (nurse != null) {
						String nursePhone = nurse.getAttribute(phoneNumberType)
								.getValue();
						String nurseFirstName = nurse.getPersonName()
								.getGivenName();

						NameValuePair[] personalInfo = new NameValuePair[1];
						personalInfo[0] = new NameValuePair();
						personalInfo[0].setName("NurseFirstName");
						personalInfo[0].setValue(nurseFirstName);
						org.motechproject.ws.Patient[] patients = new org.motechproject.ws.Patient[0];
						String langCode = null;
						MediaType mediaType = null;
						Date messageStartDate = null;
						Date messageEndDate = null;

						// Cancel message if nurse phone is considered troubled
						TroubledPhone troubledPhone = Context.getService(
								MotechService.class).getTroubledPhone(
								nursePhone);
						Integer maxFailures = Integer
								.parseInt(Context
										.getAdministrationService()
										.getGlobalProperty(
												"motechmodule.troubled_phone_failures"));
						if (troubledPhone != null
								&& troubledPhone.getSendFailures() >= maxFailures) {
							motechLog
									.setMessage("Attempt to send to Troubled Phone, Nurse Phone: "
											+ nursePhone
											+ ", Message cancelled: "
											+ notificationType);
							motechLog.setType(LogType.FAILURE);

							shouldAttemptMessage
									.setAttemptStatus(MessageStatus.CANCELLED);

						} else {
							motechLog
									.setMessage("Scheduled Message Notification, Nurse Phone: "
											+ nursePhone
											+ ": "
											+ notificationType);

							try {
								Context.getService(MotechService.class)
										.getMobileService().sendCHPSMessage(
												messageId, personalInfo,
												nursePhone, patients, langCode,
												mediaType, notificationType,
												messageStartDate,
												messageEndDate);

								shouldAttemptMessage
										.setAttemptStatus(MessageStatus.ATTEMPT_PENDING);
								motechLog.setType(LogType.SUCCESS);
							} catch (Exception e) {
								log.error("Mobile nurse message failure", e);
								shouldAttemptMessage
										.setAttemptStatus(MessageStatus.ATTEMPT_FAIL);
								motechLog.setType(LogType.FAILURE);
							}
						}

					}
					Context.getService(MotechService.class).saveLog(motechLog);

					Context.getService(MotechService.class).saveMessage(
							shouldAttemptMessage);
				}
			}
		} catch (Exception e) {
			log.error(e);
		} finally {
			Context
					.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES);
			Context
					.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_IDENTIFIER_TYPES);
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PATIENTS);
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_USERS);
			Context.closeSession();
		}
	}

}
