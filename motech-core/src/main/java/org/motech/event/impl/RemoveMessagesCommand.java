package org.motech.event.impl;

import org.motech.event.Command;
import org.motech.messaging.MessageScheduler;

public class RemoveMessagesCommand implements Command {

	String messageGroup;
	Integer messageRecipientId;
	MessageScheduler messageScheduler;

	public void execute() {

		messageScheduler.removeAllUnsentMessages(messageRecipientId,
				messageGroup);
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

	public MessageScheduler getMessageScheduler() {
		return messageScheduler;
	}

	public void setMessageScheduler(MessageScheduler messageScheduler) {
		this.messageScheduler = messageScheduler;
	}

}
