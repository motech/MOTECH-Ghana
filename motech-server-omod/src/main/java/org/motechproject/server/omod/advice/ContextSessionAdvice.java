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
