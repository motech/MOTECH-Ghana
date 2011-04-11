package org.motechproject.server.svc;

import org.motechproject.server.event.MessageProgram;
import org.motechproject.server.model.MessageProgramType;

public interface MessageProgramService {
    MessageProgram program(MessageProgramType expectedCare);
}
