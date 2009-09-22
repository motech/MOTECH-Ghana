package org.motech.messaging;

import java.util.ArrayList;
import java.util.List;

public class MessageDefinition {

	Long id;
	String messageKey;
	List<MessageAttribute> messageAttributes = new ArrayList<MessageAttribute>();

	public Message createMessage(ScheduledMessage schedMessage) {
		Message message = new Message();
		message.setSchedule(schedMessage);
		message.setDefinition(this);
		message.setLastStatus(MessageStatus.SHOULD_ATTEMPT);
		return message;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMessageKey() {
		return messageKey;
	}

	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}

	public List<MessageAttribute> getMessageAttributes() {
		return messageAttributes;
	}

	public void setMessageAttributes(List<MessageAttribute> messageAttributes) {
		this.messageAttributes = messageAttributes;
	}

}
