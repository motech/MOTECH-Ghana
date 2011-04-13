package org.motechproject.server.svc.impl;

import org.motechproject.server.event.MessageProgram;
import org.motechproject.server.event.MessagesCommand;
import org.motechproject.server.event.impl.MotechMessageProgram;
import org.motechproject.server.model.db.hibernate.MessageProgramDAO;
import org.motechproject.server.svc.MessageProgramFactory;
import org.motechproject.server.time.TimeBean;

public class WeeklyMessageProgramFactory implements MessageProgramFactory {

    MessageProgram program;
    MessagesCommand command;
    TimeBean timeBean ;
    MessageProgramDAO messageProgramDAO;
    private String programName;


    public MessageProgram program() {
        return program;
    }

    public void setProgram(MotechMessageProgram program) {
        this.program = program;
    }
}
