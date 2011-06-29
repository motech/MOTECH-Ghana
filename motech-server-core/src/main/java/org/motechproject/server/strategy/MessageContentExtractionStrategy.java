package org.motechproject.server.strategy;

import org.motechproject.server.model.ExtractedMessage;
import org.motechproject.server.model.IncomingMessage;

public interface MessageContentExtractionStrategy {
    public ExtractedMessage extractFrom(IncomingMessage message);

    String extractDateFrom(String time);

    String extractPhoneNumberFrom(String phoneNumber);

    String extractSenderFrom(String text);

    String extractDescriptionFrom(String text);
}
