package org.motechproject.server.svc.impl;

import org.motechproject.server.model.MessageProgram;
import org.motechproject.server.event.impl.ExpectedCareMessageProgram;
import org.motechproject.server.model.ExpectedCareMessageDetails;
import org.motechproject.server.model.db.hibernate.MessageProgramDAO;
import org.motechproject.server.svc.MessageProgramFactory;
import org.motechproject.server.svc.RegistrarBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class ExpectedCareMessageProgramFactory implements MessageProgramFactory {

    MessageProgramDAO messageProgramDAO;
    RegistrarBean registrarBean ;
    String name ;

    @Transactional
    public MessageProgram program() {
        ExpectedCareMessageProgram program = new ExpectedCareMessageProgram();
        program.setRegistrarBean(registrarBean);
        program.setCareMessageDetails(messageDetails());
        return program;
    }

    public String name() {
        return name;
    }

    public void setName(String name){
        this.name = name ;
    }


    public void setMessageProgramDAO(MessageProgramDAO messageProgramDAO) {
        this.messageProgramDAO = messageProgramDAO;
    }

    public void setRegistrarBean(RegistrarBean registrarBean) {
        this.registrarBean = registrarBean;
    }


    private List<ExpectedCareMessageDetails> messageDetails() {
        return messageProgramDAO.messageDetails();
    }


}
