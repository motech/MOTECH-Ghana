package org.motechproject.server.svc;

import org.motechproject.server.model.MessageProgram;

public interface MessageProgramFactory {
    MessageProgram program();
    String name();
}
