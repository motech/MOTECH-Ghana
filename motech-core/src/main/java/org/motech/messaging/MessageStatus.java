package org.motech.messaging;

public enum MessageStatus {
	SHOULD_ATTEMPT, ATTEMPT_PENDING, ATTEMPT_FAIL, ATTEMPT_TIMEOUT, DELIVERED, CANCELLED
}
