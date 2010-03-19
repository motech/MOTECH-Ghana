package org.motechproject.server.model.db.hibernate;

import org.motechproject.server.model.MessageStatus;

public class MessageStatusEnumType extends EnumUserType<MessageStatus> {

	public MessageStatusEnumType() {
		super(MessageStatus.class);
	}
}
