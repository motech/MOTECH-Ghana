package org.motechproject.mobile.imp.util;

import org.motechproject.mobile.core.model.IncomingMessage;
import org.motechproject.mobile.imp.util.exception.MotechParseException;

/**
 * Interface for parsing Motech incoming messages in serialized form
 *
 * @author  Igor Opushnyev  (iopushnyev@2paths.com)
 * Date Created: 26-Nov-2010
  */
public interface InMessageParser {


 /**
 * Parse a Motech incoming messages in serialized form
  *
  * @param   incomingMessageStringRepresentation - incoming message in a serialized form
  * @return    IInstance of ncomingMessage class populated with data from the serialized incoming message
  * @throws MotechParseException if the incoming message cannot be parsed
 */
    IncomingMessage parseIncomingMessage(String incomingMessageStringRepresentation) throws MotechParseException;
}
