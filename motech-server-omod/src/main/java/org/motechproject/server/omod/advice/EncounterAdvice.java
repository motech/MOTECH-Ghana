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
package org.motechproject.server.omod.advice;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.impl.ContextServiceImpl;
import org.motechproject.server.omod.sdsched.ScheduleMaintService;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * An OpenMRS AOP interceptor that enables us to perform various tasks upon an
 * encounter being saved, whether that operation knows about it or not.
 */
public class EncounterAdvice implements AfterReturningAdvice {

	private static Log log = LogFactory.getLog(ObsAdvice.class);

	private ContextService contextService;

	public EncounterAdvice() {
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

		String methodName = method.getName();

		if (methodName.equals("saveEncounter")
				|| methodName.equals("voidEncounter")) {

			log.debug("intercepting method invocation");

			Encounter encounter = (Encounter) returnValue;
			Patient patient = encounter.getPatient();

			ScheduleMaintService schedService = contextService
					.getScheduleMaintService();

			if (TransactionSynchronizationManager.isSynchronizationActive()) {
				schedService.addAffectedPatient(patient.getId());
				schedService.requestSynch();
			} else {
				// FIXME: Remove this when advice can exec in tx
				schedService.updateSchedule(patient.getId());
			}
		}
	}

}
