package org.motechproject.server.omod.advice;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.annotation.LogParameterIdentifiers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class RCTLoggingAdvice implements MethodInterceptor {

    private static final Log log = LogFactory.getLog("rct");
    private static final String SPACE = " ";
    private static final String EQUALS = "=";

    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            if (log.isInfoEnabled()) {
                log.info(messageToLog(invocation));
            }
            return invocation.proceed();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    private String messageToLog(MethodInvocation invocation) throws NoSuchFieldException, IllegalAccessException {
        Method method = invocation.getMethod();
        StringBuilder message = new StringBuilder();
        message.append(logMethodName(method));
        message.append(logParameterIds(method,invocation.getArguments()));
        return message.toString();
    }

    private String logMethodName(Method method) {
        return new StringBuilder().append("method = ").append(SPACE).append(method.getName()).append(SPACE).toString();
    }

    private String logParameterIds(Method method,Object[] parameters) throws NoSuchFieldException, IllegalAccessException {
        StringBuilder message = new StringBuilder();
        LogParameterIdentifiers annotation = method.getAnnotation(LogParameterIdentifiers.class);
        if (annotation != null) {
            for (Object parameter : parameters) {
                message.append(logParameterId(parameter, annotation.idField()));
            }
        }
        return message.toString();
    }

    private String logParameterId(Object parameter, String idFieldName) throws NoSuchFieldException, IllegalAccessException {
        StringBuilder message = new StringBuilder();
        Class<? extends Object> clazz = parameter.getClass();
        List<String> idFields = getIdFields(clazz, idFieldName);
        for (String idField : idFields) {
            Field field = clazz.getDeclaredField(idField);
            field.setAccessible(true);
            Object value = field.get(parameter);
            message.append(SPACE)
                    .append(idField).append(EQUALS).append(value)
                    .append(SPACE);
        }
        return message.toString();
    }

    private List<String> getIdFields(Class<? extends Object> clazz, String idFieldName) {
        ArrayList<String> ids = new ArrayList<String>();

        if (clazz.isPrimitive()) return ids;

        for (Field field : clazz.getDeclaredFields()) {
            String fieldName = field.getName();
            if (fieldName.equalsIgnoreCase(idFieldName) || fieldName.endsWith(idFieldName)) {
                ids.add(fieldName);
            }
        }
        return ids;
    }
}
