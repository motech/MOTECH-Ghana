package org.motech.event.impl;

import org.motech.event.Command;
import org.motech.svc.RegistrarBean;

public class RemoveMessagesCommand implements Command {

	String messageGroup;
	Integer messageRecipientId;
	RegistrarBean registrarBean;

	public void execute() {

		registrarBean.removeAllUnsentMessages(messageRecipientId, messageGroup);
	}

	public String getMessageGroup() {
		return messageGroup;
	}

	public void setMessageGroup(String messageGroup) {
		this.messageGroup = messageGroup;
	}

	public Integer getMessageRecipientId() {
		return messageRecipientId;
	}

	public void setMessageRecipientId(Integer messageRecipientId) {
		this.messageRecipientId = messageRecipientId;
	}

	public RegistrarBean getRegistrarBean() {
		return registrarBean;
	}

	public void setRegistrarBean(RegistrarBean registrarBean) {
		this.registrarBean = registrarBean;
	}

}
