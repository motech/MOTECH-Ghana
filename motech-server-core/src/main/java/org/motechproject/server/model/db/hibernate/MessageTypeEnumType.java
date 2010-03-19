package org.motechproject.server.model.db.hibernate;

import org.motechproject.server.model.MessageType;

public class MessageTypeEnumType extends EnumUserType<MessageType> {

	public MessageTypeEnumType() {
		super(MessageType.class);
	}

}
