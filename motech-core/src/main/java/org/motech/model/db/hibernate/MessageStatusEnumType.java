package org.motech.model.db.hibernate;

import org.motech.messaging.MessageStatus;

public class MessageStatusEnumType extends EnumUserType<MessageStatus> {

	public MessageStatusEnumType() {
		super(MessageStatus.class);
	}
}
