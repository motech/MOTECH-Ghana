package org.motechproject.server.svc.impl;

import org.motechproject.server.svc.MessageSourceBean;
import org.springframework.context.MessageSource;

public class MessageSourceBeanImpl implements MessageSourceBean {

	MessageSource messageSource;

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public String getMessage(String messageCode, String fieldName) {
		return messageSource.getMessage(messageCode,
				new String[] { fieldName }, fieldName + "=in error", null);
	}
}
