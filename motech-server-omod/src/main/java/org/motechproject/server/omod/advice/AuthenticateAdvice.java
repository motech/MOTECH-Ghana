/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) ${year} The Trustees of Columbia University in the City of
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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.motechproject.server.annotation.RunAsAdminUser;
import org.motechproject.server.omod.ContextService;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.scheduler.SchedulerConstants;

public class AuthenticateAdvice implements MethodInterceptor {

	private ContextService contextService;

	public Object invoke(MethodInvocation invocation) throws Throwable {

		if (hasAuthAnnotation(invocation.getMethod())) {
			Object returnValue = null;

			User user = contextService.getAuthenticatedUser();

			boolean authenticated = authenticate(user);

			returnValue = invocation.proceed();

			if (authenticated) {
				unauthenticate(user);
			}

			return returnValue;
		} else {
			return invocation.proceed();
		}
	}

	private boolean authenticate(User currentUser) {
		String username = null;
		String password = null;

		AdministrationService adminService = contextService
				.getAdministrationService();
		username = adminService
				.getGlobalProperty(SchedulerConstants.SCHEDULER_USERNAME_PROPERTY);
		password = adminService
				.getGlobalProperty(SchedulerConstants.SCHEDULER_PASSWORD_PROPERTY);

		if (currentUser == null || !currentUser.getSystemId().equals(username)) {
			contextService.authenticate(username, password);
			return true;
		}
		return false;
	}

	private void unauthenticate(User previousUser) {
		if (previousUser == null) {
			contextService.logout();
		} else {
			contextService.becomeUser(previousUser.getSystemId());
		}
	}

	private boolean hasAuthAnnotation(Method method) {
		for (Annotation annotation : method.getAnnotations()) {
			if (annotation instanceof RunAsAdminUser) {
				return true;
			}
		}
		return false;
	}

	public void setContextService(ContextService contextService) {
		this.contextService = contextService;
	}

}
