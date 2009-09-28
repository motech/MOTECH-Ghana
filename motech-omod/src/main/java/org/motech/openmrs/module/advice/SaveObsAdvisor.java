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
package org.motech.openmrs.module.advice;

import java.lang.reflect.Method;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motech.messaging.Message;
import org.motech.messaging.MessageDefinition;
import org.motech.messaging.ScheduledMessage;
import org.motech.openmrs.module.MotechService;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.springframework.aop.AfterReturningAdvice;

/**
 * An OpenMRS AOP interceptor that enables us to perform various tasks upon an
 * encounter being saved, whether that operation knows about it or not.
 * Currently, this is how we are handling scheduling notifications.
 */
public class SaveObsAdvisor implements AfterReturningAdvice {

	private static Log log = LogFactory.getLog(SaveObsAdvisor.class);

	/**
	 * @see org.springframework.aop.AfterReturningAdvice#afterReturning(java.lang.Object,
	 *      java.lang.reflect.Method, java.lang.Object[], java.lang.Object)
	 */
	public void afterReturning(Object returnValue, Method method,
			Object[] args, Object target) throws Throwable {

		if (method.getName().equals("saveObs")) {
			Obs obs = (Obs) returnValue;
			Concept pregVisitConcept = Context.getConceptService().getConcept(
					"PREGNANCY VISIT NUMBER");

			if (pregVisitConcept.equals(obs.getConcept())) {

				log.debug("Scheduling ScheduledMessage");

				// Date 30 seconds in future
				long nextServiceTimeInSecs = 30;
				Date nextServiceDate = new Date(System.currentTimeMillis()
						+ (nextServiceTimeInSecs * 1000));

				MessageDefinition messageDefinition = createMessageDefinition("PREGNANCY VISIT");

				ScheduledMessage scheduledMessage = new ScheduledMessage();
				scheduledMessage.setScheduledFor(nextServiceDate);
				scheduledMessage.setRecipientId(obs.getPerson().getPersonId());
				scheduledMessage.setMessage(messageDefinition);

				Message message = messageDefinition
						.createMessage(scheduledMessage);
				message.setAttemptDate(nextServiceDate);
				scheduledMessage.getMessageAttempts().add(message);

				Context.getService(MotechService.class).saveScheduledMessage(
						scheduledMessage);
			}
		}
	}

	private MessageDefinition createMessageDefinition(String messageKey) {
		MessageDefinition messageDefinition = Context.getService(
				MotechService.class).getMessageDefinition(messageKey);
		if (messageDefinition == null) {
			log.info(messageKey
					+ " MessageDefinition Does Not Exist - Creating");
			messageDefinition = new MessageDefinition();
			messageDefinition.setMessageKey(messageKey);
			messageDefinition = Context.getService(MotechService.class)
					.saveMessageDefinition(messageDefinition);
		}
		return messageDefinition;
	}
}
