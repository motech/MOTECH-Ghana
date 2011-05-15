package org.motechproject.server.svc.impl;

import org.motechproject.server.event.impl.RemoveEnrollmentCommand;
import org.motechproject.server.event.impl.ScheduleMessageCommand;
import org.motechproject.server.messaging.MessageScheduler;
import org.motechproject.server.model.MessageProgram;
import org.motechproject.server.model.MessageProgramState;
import org.motechproject.server.model.db.hibernate.MessageProgramDAO;
import org.motechproject.server.svc.MessageProgramFactory;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.server.time.TimeBean;
import org.springframework.transaction.annotation.Transactional;

public class WeeklyMessageProgramFactory implements MessageProgramFactory {

    private MessageProgramDAO messageProgramDAO;
    private String name;
    private RegistrarBean registrarBean;
    private TimeBean timeBean;
    private MessageScheduler messageScheduler;


    @Transactional
    public MessageProgram program() {
        MessageProgram program = messageProgramDAO.weeklyProgram(name);
        program.setRegistrarBean(registrarBean);
        initializeStates(program.getStartState());
        return program;
    }

    public String name() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMessageProgramDAO(MessageProgramDAO messageProgramDAO) {
        this.messageProgramDAO = messageProgramDAO;
    }

    public void setRegistrarBean(RegistrarBean registrarBean) {
        this.registrarBean = registrarBean;
    }

    private void initializeStates(MessageProgramState state) {
        MessageProgramState current = state;
        while (current.hasNext()) {
            current.setTimeBean(timeBean);
            current.setCommand(new ScheduleMessageCommand(current.getMessageKey(),messageScheduler));
            current = current.getNext();
        }
        current.setTimeBean(timeBean);
        current.setCommand(new RemoveEnrollmentCommand(registrarBean));
    }

    public void setTimeBean(TimeBean timeBean) {
        this.timeBean = timeBean;
    }

    public void setMessageScheduler(MessageScheduler messageScheduler) {
        this.messageScheduler = messageScheduler;
    }
}
