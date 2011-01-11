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

package org.motechproject.server.omod.advice;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.sdsched.ScheduleMaintService;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * An OpenMRS AOP interceptor that enables us to perform various tasks upon an
 * encounter being saved, whether that operation knows about it or not.
 */
public class EncounterAdvice implements AfterReturningAdvice {

	private static Log log = LogFactory.getLog(ObsAdvice.class);

    @Autowired
	private ContextService contextService;

	public EncounterAdvice() {
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
