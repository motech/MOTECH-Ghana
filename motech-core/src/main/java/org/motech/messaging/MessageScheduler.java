package org.motech.messaging;

import java.util.Date;

public interface MessageScheduler {

	void scheduleMessage(String messageKey, String messageGroup,
			Integer messageRecipientId, Date messageDate);

}
