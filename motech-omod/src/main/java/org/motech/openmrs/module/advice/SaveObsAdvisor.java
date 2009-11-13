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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motech.openmrs.module.ContextService;
import org.motech.openmrs.module.impl.ContextServiceImpl;
import org.openmrs.Obs;
import org.springframework.aop.AfterReturningAdvice;

/**
 * An OpenMRS AOP interceptor that enables us to perform various tasks upon an
 * observation being saved, whether that operation knows about it or not.
 * Currently, this is how we are handling calling the event engine.
 */
public class SaveObsAdvisor implements AfterReturningAdvice {

	private static Log log = LogFactory.getLog(SaveObsAdvisor.class);

	private ContextService contextService;

	public SaveObsAdvisor() {
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
			Obs obs = (Obs) returnValue;

			contextService.getRegistrarBean().updateMessageProgramState(obs);
		}
	}

}
