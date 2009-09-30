package org.motech.messaging;

import java.util.Date;

public interface MessageScheduler {

	void scheduleMessage(String messageKey, Long publicId,
			Integer messageRecipientId, Date messageDate);

}
