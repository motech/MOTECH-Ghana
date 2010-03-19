package org.motechproject.server.openmrs.module.advice;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.motechproject.server.annotation.RunAsAdminUser;
import org.motechproject.server.openmrs.module.ContextService;
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
