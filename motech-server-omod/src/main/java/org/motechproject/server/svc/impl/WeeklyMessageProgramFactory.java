package org.motechproject.server.svc.impl;

import org.motechproject.server.event.MotechBeanFactory;
import org.motechproject.server.model.MessageProgram;
import org.motechproject.server.model.db.hibernate.MessageProgramDAO;
import org.motechproject.server.svc.MessageProgramFactory;
import org.motechproject.server.svc.RegistrarBean;
import org.springframework.transaction.annotation.Transactional;

public class WeeklyMessageProgramFactory implements MessageProgramFactory {

    private MessageProgramDAO messageProgramDAO;
    private String name;
    private MotechBeanFactory beanFactory = new MotechBeanFactory();
    private RegistrarBean registrarBean ;


    @Transactional
    public MessageProgram program() {
        MessageProgram program = messageProgramDAO.weeklyProgram(name);
        program.setRegistrarBean(registrarBean);
        overrideEndStateCommand(program);
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

    private void overrideEndStateCommand(MessageProgram program) {
        program.getEndState().setCommand(beanFactory.createRemoveCommand());
    }

    public void setRegistrarBean(RegistrarBean registrarBean) {
        this.registrarBean = registrarBean;
    }
}
