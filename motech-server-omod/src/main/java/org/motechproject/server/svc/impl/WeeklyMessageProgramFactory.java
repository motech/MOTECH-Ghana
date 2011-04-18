package org.motechproject.server.svc.impl;

import org.motechproject.server.model.MessageProgram;
import org.motechproject.server.event.MessagesCommand;
import org.motechproject.server.model.MotechMessageProgram;
import org.motechproject.server.model.db.hibernate.MessageProgramDAO;
import org.motechproject.server.svc.MessageProgramFactory;
import org.motechproject.server.time.TimeBean;
import org.springframework.transaction.annotation.Transactional;

public class WeeklyMessageProgramFactory implements MessageProgramFactory {

    private MessageProgram program;
    private MessagesCommand command;
    private TimeBean timeBean ;

    private MessageProgramDAO messageProgramDAO;

    private String name;
    @Transactional
    public MessageProgram program() {
       return messageProgramDAO.weeklyProgram(name);
    }

    public void setProgram(MotechMessageProgram program) {
        this.program = program;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMessageProgramDAO(MessageProgramDAO messageProgramDAO) {
        this.messageProgramDAO = messageProgramDAO;
    }
}
