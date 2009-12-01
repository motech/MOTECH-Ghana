package org.motech.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ScheduledMessage {

	Long id;
	Date scheduledFor;
	MessageDefinition message;
	Integer recipientId;
	List<Message> messageAttempts = new ArrayList<Message>();
	Set<String> groupIds = new HashSet<String>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getScheduledFor() {
		return scheduledFor;
	}

	public void setScheduledFor(Date scheduledFor) {
		this.scheduledFor = scheduledFor;
	}

	public MessageDefinition getMessage() {
		return message;
	}

	public void setMessage(MessageDefinition message) {
		this.message = message;
	}

	public Integer getRecipientId() {
		return recipientId;
	}

	public void setRecipientId(Integer recipientId) {
		this.recipientId = recipientId;
	}

	public List<Message> getMessageAttempts() {
		return messageAttempts;
	}

	public void setMessageAttempts(List<Message> messageAttempts) {
		this.messageAttempts = messageAttempts;
	}

	public Set<String> getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(Set<String> groupIds) {
		this.groupIds = groupIds;
	}

}
