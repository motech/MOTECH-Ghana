package org.motechproject.server.event.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.Predicate;
import org.motechproject.server.model.MessageDefinition;
import org.motechproject.server.model.ScheduledMessage;

public class ScheduledMessagePredicate implements Predicate {

	private Set<String> keys = new HashSet<String>();
	private String care;
	private String date;

	public boolean evaluate(Object input) {
		if (input instanceof ScheduledMessage) {
			ScheduledMessage scheduledMessage = (ScheduledMessage) input;
			MessageDefinition message = scheduledMessage.getMessage();
			boolean keyMatches = true;
			boolean careMatches = true;
			boolean dateMatches = true;
			if (message != null) {
				keyMatches = keys.contains(message.getMessageKey());
			}
			if (care != null) {
				careMatches = care.equals(scheduledMessage.getCare());
			}
			if (date != null) {
				dateMatches = date.equals(scheduledMessage.getScheduledFor());
			}
			return keyMatches && careMatches && dateMatches;
		}
		return false;
	}

	public Set<String> getKeys() {
		return keys;
	}

	public void setKeys(Set<String> keys) {
		this.keys = keys;
	}

	public void resetKeys(String... keys) {
		this.keys.clear();
		for (String key : keys) {
			this.keys.add(key);
		}
	}

	public String getCare() {
		return care;
	}

	public void setCare(String care) {
		this.care = care;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

}
