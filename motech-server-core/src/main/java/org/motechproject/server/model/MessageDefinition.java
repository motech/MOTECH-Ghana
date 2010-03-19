package org.motechproject.server.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MessageDefinition {

	Long id;
	String messageKey;
	Long publicId;
	MessageType messageType;
	List<MessageAttribute> messageAttributes = new ArrayList<MessageAttribute>();

	public MessageDefinition() {
	}

	public MessageDefinition(String messageKey, Long publicId,
			MessageType messageType) {
		setMessageKey(messageKey);
		setPublicId(publicId);
		setMessageType(messageType);
	}

	public Message createMessage(ScheduledMessage schedMessage) {
		Message message = new Message();
		message.setPublicId(UUID.randomUUID().toString());
		message.setSchedule(schedMessage);
		message.setAttemptStatus(MessageStatus.SHOULD_ATTEMPT);
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

	public Long getPublicId() {
		return publicId;
	}

	public void setPublicId(Long publicId) {
		this.publicId = publicId;
	}

	public List<MessageAttribute> getMessageAttributes() {
		return messageAttributes;
	}

	public void setMessageAttributes(List<MessageAttribute> messageAttributes) {
		this.messageAttributes = messageAttributes;
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}

}
