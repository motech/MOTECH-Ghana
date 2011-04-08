package org.motechproject.server.omod;

import org.motechproject.server.event.MessageProgram;
import org.motechproject.server.model.MessageProgramType;

public interface MessageProgramService {
    MessageProgram program(MessageProgramType expectedCare);
}
