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
import java.util.ArrayList;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.motechproject.server.annotation.RunWithPrivileges;
import org.motechproject.server.omod.ContextService;

public class ProxyPrivilegesAdvice implements MethodInterceptor {

	private ContextService contextService;

	public Object invoke(MethodInvocation invocation) throws Throwable {

		String[] privileges = getPrivileges(invocation.getMethod());

		if (privileges != null) {
			Object returnValue = null;

			List<String> addedPrivileges = addPrivileges(privileges);

			returnValue = invocation.proceed();

			removePrivileges(addedPrivileges);

			return returnValue;
		} else {
			return invocation.proceed();
		}
	}

	private List<String> addPrivileges(String[] privileges) {
		List<String> addedPrivileges = new ArrayList<String>();
		for (String privilege : privileges) {
			if (!contextService.hasPrivilege(privilege)) {
				contextService.addProxyPrivilege(privilege);
				addedPrivileges.add(privilege);
			}
		}
		return addedPrivileges;
	}

	private void removePrivileges(List<String> privileges) {
		for (String privilege : privileges) {
			contextService.removeProxyPrivilege(privilege);
		}
	}

	private String[] getPrivileges(Method method) {
		for (Annotation annotation : method.getAnnotations()) {
			if (annotation instanceof RunWithPrivileges) {
				return ((RunWithPrivileges) annotation).value();
			}
		}
		return null;
	}

	public void setContextService(ContextService contextService) {
		this.contextService = contextService;
	}

}
