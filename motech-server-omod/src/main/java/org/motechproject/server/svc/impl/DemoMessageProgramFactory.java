package org.motechproject.server.svc.impl;

import org.motechproject.server.model.MessageProgram;
import org.motechproject.server.svc.MessageProgramFactory;

public class DemoMessageProgramFactory implements MessageProgramFactory {

    MessageProgram program ;

    public MessageProgram program() {
        return program ;
    }

    public void setProgram(MessageProgram program) {
        this.program = program;
    }
}
