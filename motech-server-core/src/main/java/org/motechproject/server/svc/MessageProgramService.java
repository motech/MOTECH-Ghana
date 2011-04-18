package org.motechproject.server.svc;

import org.motechproject.server.model.MessageProgram;

public interface MessageProgramService {
    MessageProgram program(String programName);
}
