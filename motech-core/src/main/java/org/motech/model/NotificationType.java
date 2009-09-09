package org.motech.model;

import com.dreamoval.motech.omi.service.MessageType;

public enum NotificationType {
	text, voice;

	public MessageType toMessageType() {
		switch (this) {
		case text:
			return MessageType.TEXT;
		case voice:
			return MessageType.VOICE;
		default:
			return null;
		}
	}

	public static NotificationType fromMessageType(MessageType messageType) {
		switch (messageType) {
		case TEXT:
			return NotificationType.text;
		case VOICE:
			return NotificationType.voice;
		default:
			return null;
		}
	}

}
