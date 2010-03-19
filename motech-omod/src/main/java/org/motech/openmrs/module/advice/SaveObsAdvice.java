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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motech.model.ExpectedObs;
import org.motech.openmrs.module.ContextService;
import org.motech.openmrs.module.MotechService;
import org.motech.openmrs.module.impl.ContextServiceImpl;
import org.motech.openmrs.module.sdsched.ScheduleMaintService;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * An OpenMRS AOP interceptor that enables us to perform various tasks upon an
 * observation being saved, whether that operation knows about it or not.
 * Currently, this is how we are handling calling the event engine.
 */
public class SaveObsAdvice implements AfterReturningAdvice {

	private static Log log = LogFactory.getLog(SaveObsAdvice.class);

	private ContextService contextService;

	public SaveObsAdvice() {
		contextService = new ContextServiceImpl();
	}

	public void setContextService(ContextService contextService) {
		this.contextService = contextService;
	}

	/**
	 * @see org.springframework.aop.AfterReturningAdvice#afterReturning(java.lang.Object,
	 *      java.lang.reflect.Method, java.lang.Object[], java.lang.Object)
	 */
	public void afterReturning(Object returnValue, Method method,
			Object[] args, Object target) throws Throwable {

		if (method.getName().equals("saveObs")) {

			log.debug("intercepting method invocation");

			Obs obs = (Obs) returnValue;

			Person person = obs.getPerson();
			Concept concept = obs.getConcept();
			Concept valueConcept = obs.getValueCoded();
			Double valueNumeric = obs.getValueNumeric();
			Date obsDatetime = obs.getObsDatetime();

			Integer personId = person.getPersonId();
			String conceptName = concept.getName().getName();

			MotechService motechService = contextService.getMotechService();
			List<ExpectedObs> expectedObservations = motechService
					.getExpectedObs(person, concept, valueConcept,
							valueNumeric, obsDatetime);

			for (ExpectedObs expectedObs : expectedObservations) {
				if (log.isDebugEnabled()) {
					log.debug("Removing: " + expectedObs.toString());
				}
				motechService.removeExpectedObs(expectedObs);
			}

			contextService.getRegistrarBean().updateMessageProgramState(
					personId, conceptName);

			ScheduleMaintService schedService = contextService
					.getScheduleMaintService();

			if (person.isPatient()) {
				if (TransactionSynchronizationManager
						.isActualTransactionActive()) {
					schedService.addAffectedPatient(person.getId());
					schedService.requestSynch();
				} else {
					// FIXME: Remove this when advice can exec in tx
					schedService.updateSchedule(person.getId());
				}
			}
		}
	}

}
