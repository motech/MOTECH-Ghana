package org.motech.messaging;

import java.util.Date;

public interface MessageScheduler {

	void scheduleMessage(String messageKey, Long publicId, String messageGroup,
			Integer messageRecipientId, Date messageDate);

	void removeAllUnsentMessages(Integer recipientId, String messageGroup);
}
