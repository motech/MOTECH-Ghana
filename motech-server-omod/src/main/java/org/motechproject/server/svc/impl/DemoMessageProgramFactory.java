package org.motechproject.server.svc.impl;

import org.motechproject.server.model.MessageProgram;
import org.motechproject.server.svc.MessageProgramFactory;

public class DemoMessageProgramFactory implements MessageProgramFactory {

    MessageProgram program ;
    String name ;

    public MessageProgram program() {
        return program ;
    }

    public String name() {
        return name ;
    }

    public void setName(String name){
        this.name = name ;
    }

    public void setProgram(MessageProgram program) {
        this.program = program;
    }
}
