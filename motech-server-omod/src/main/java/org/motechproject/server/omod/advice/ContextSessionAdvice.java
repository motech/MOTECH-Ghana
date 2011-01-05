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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.motechproject.server.annotation.RunAsAdminUser;
import org.motechproject.server.annotation.RunAsUser;
import org.motechproject.server.annotation.RunWithContextSession;
import org.motechproject.server.annotation.RunWithPrivileges;
import org.motechproject.server.omod.ContextService;

public class ContextSessionAdvice implements MethodInterceptor {

	private ContextService contextService;

	public Object invoke(MethodInvocation invocation) throws Throwable {

		if (hasSessionAnnotation(invocation.getMethod())) {
			Object returnValue = null;
			boolean sessionOpened = false;
			try {
				if (!contextService.isSessionOpen()) {
					contextService.openSession();
					sessionOpened = true;
				}
				returnValue = invocation.proceed();
			} finally {
				if (sessionOpened) {
					contextService.closeSession();
				}
			}
			return returnValue;
		} else {
			return invocation.proceed();
		}
	}

	private boolean hasSessionAnnotation(Method method) {
		for (Annotation annotation : method.getAnnotations()) {
			if (annotation instanceof RunWithContextSession
					|| annotation instanceof RunWithPrivileges
					|| annotation instanceof RunAsAdminUser
					|| annotation instanceof RunAsUser) {
				return true;
			}
		}
		return false;
	}

	public void setContextService(ContextService contextService) {
		this.contextService = contextService;
	}

}
