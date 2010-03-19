package org.motech.model.db.hibernate;

import org.motech.model.MessageType;

public class MessageTypeEnumType extends EnumUserType<MessageType> {

	public MessageTypeEnumType() {
		super(MessageType.class);
	}

}
