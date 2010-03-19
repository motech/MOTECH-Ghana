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
