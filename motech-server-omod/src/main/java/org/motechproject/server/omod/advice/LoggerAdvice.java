package org.motechproject.server.omod.advice;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LoggerAdvice implements MethodInterceptor {

    private static Log log = LogFactory.getLog(LoggerAdvice.class);

    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            return invocation.proceed();
        } catch (Exception ex) {
            log.fatal("Exception while invoking " + invocation.getMethod().getName(), ex);
            throw new RuntimeException(ex);
        }
    }
}
