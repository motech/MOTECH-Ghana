package org.motech.openmrs.module.advice;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.motech.annotation.RunAsAdminUser;
import org.motech.annotation.RunWithContextSession;
import org.motech.annotation.RunWithPrivileges;
import org.motech.openmrs.module.ContextService;

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
					|| annotation instanceof RunAsAdminUser) {
				return true;
			}
		}
		return false;
	}

	public void setContextService(ContextService contextService) {
		this.contextService = contextService;
	}

}
