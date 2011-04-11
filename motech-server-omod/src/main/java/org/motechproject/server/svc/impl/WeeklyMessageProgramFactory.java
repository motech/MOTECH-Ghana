package org.motechproject.server.svc.impl;

import org.motechproject.server.event.MessageProgram;
import org.motechproject.server.svc.MessageProgramFactory;

public class WeeklyMessageProgramFactory implements MessageProgramFactory {

    //Just the skeletons for weekly messages 

    MessageProgram program;

    public MessageProgram program() {
        return program;
    }

    public void setProgram(org.motechproject.server.event.impl.MessageProgramImpl program) {
        this.program = program;
    }
}
