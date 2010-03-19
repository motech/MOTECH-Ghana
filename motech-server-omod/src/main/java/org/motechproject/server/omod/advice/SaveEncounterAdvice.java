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
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.model.ExpectedEncounter;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.MotechService;
import org.motechproject.server.omod.impl.ContextServiceImpl;
import org.motechproject.server.omod.sdsched.ScheduleMaintService;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * An OpenMRS AOP interceptor that enables us to perform various tasks upon an
 * encounter being saved, whether that operation knows about it or not.
 */
public class SaveEncounterAdvice implements AfterReturningAdvice {

	private static Log log = LogFactory.getLog(SaveObsAdvice.class);

	private ContextService contextService;

	public SaveEncounterAdvice() {
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

		if (method.getName().equals("saveEncounter")) {

			log.debug("intercepting method invocation");

			Encounter encounter = (Encounter) returnValue;

			Patient patient = encounter.getPatient();
			EncounterType encounterType = encounter.getEncounterType();
			Date encounterDatetime = encounter.getEncounterDatetime();

			MotechService motechService = contextService.getMotechService();
			List<ExpectedEncounter> expectedEncounters = motechService
					.getExpectedEncounter(patient, encounterType,
							encounterDatetime);

			for (ExpectedEncounter expectedEncounter : expectedEncounters) {
				if (log.isDebugEnabled()) {
					log.debug("Removing: " + expectedEncounter.toString());
				}
				motechService.removeExpectedEncounter(expectedEncounter);
			}

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
