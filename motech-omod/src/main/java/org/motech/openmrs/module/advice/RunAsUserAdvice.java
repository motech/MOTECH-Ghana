package org.motech.openmrs.module.advice;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.motech.annotation.RunAsUser;
import org.motech.annotation.RunAsUserParam;
import org.motech.openmrs.module.ContextService;
import org.motech.openmrs.module.UserResolver;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.scheduler.SchedulerConstants;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * An interceptor that enables marking methods with an annotation and having the
 * method execute as a specified user. The user is specified by the value of a
 * method parameter. The method parameter used is specified by marking the
 * parameter with a parameter annotation.
 * 
 * @author batkinson
 * 
 */
public class RunAsUserAdvice implements MethodInterceptor,
		ApplicationContextAware {

	private ApplicationContext springContext;
	private ContextService contextService;

	public Object invoke(MethodInvocation invocation) throws Throwable {

		Method method = invocation.getMethod();
		Object[] args = invocation.getArguments();

		if (hasAuthAnnotation(method)) {

			Object returnValue = null;

			User origUser = contextService.getAuthenticatedUser();

			User userToBecome = getUserToBecome(method, args);

			// Check if we're already user, if so, just proceed
			if (origUser != null && origUser.equals(userToBecome))
				return invocation.proceed();

			// Become super user if we're not already
			if (origUser == null || !origUser.isSuperUser()) {
				becomeSuperUser();
			}

			// Become the new user
			becomeUser(userToBecome);

			try {
				// Invoke the annotated method as the new user
				returnValue = invocation.proceed();
				return returnValue;
			} finally { // Make sure we return to original state
				// Become the super user so we can switch users
				becomeSuperUser();
				// Become the original user
				becomeUser(origUser);
			}

		} else {
			return invocation.proceed();
		}
	}

	private void becomeSuperUser() {

		if (contextService.getAuthenticatedUser() != null)
			contextService.logout();

		AdministrationService adminService = contextService
				.getAdministrationService();
		String username = adminService
				.getGlobalProperty(SchedulerConstants.SCHEDULER_USERNAME_PROPERTY);
		String password = adminService
				.getGlobalProperty(SchedulerConstants.SCHEDULER_PASSWORD_PROPERTY);
		contextService.authenticate(username, password);
	}

	private UserResolver getResolverBean(String resolverName) {
		return (UserResolver) springContext.getBean(resolverName);
	}

	private void becomeUser(User user) {
		if (user == null) {
			contextService.logout();
		} else {
			contextService.becomeUser(user.getSystemId());
		}
	}

	private boolean hasAuthAnnotation(Method method) {
		for (Annotation annotation : method.getAnnotations()) {
			if (annotation instanceof RunAsUser) {
				return true;
			}
		}
		return false;
	}

	private User getUserToBecome(Method method, Object[] args) {

		Annotation[][] paramAnnotationsArrays = method
				.getParameterAnnotations();

		for (int i = 0; i < paramAnnotationsArrays.length; i++) {
			Annotation[] paramAnnotations = paramAnnotationsArrays[i];
			for (Annotation paramAnnotation : paramAnnotations) {
				if (paramAnnotation instanceof RunAsUserParam) {
					String userResolverName = ((RunAsUserParam) paramAnnotation)
							.resolverBean();
					UserResolver userResolver = getResolverBean(userResolverName);
					return userResolver.lookupUser(args[i]);
				}
			}
		}

		throw new IllegalArgumentException("method " + method + " has no "
				+ RunAsUserParam.class.getName() + " annotation");
	}

	public void setContextService(ContextService contextService) {
		this.contextService = contextService;
	}

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		springContext = applicationContext;
	}

}
