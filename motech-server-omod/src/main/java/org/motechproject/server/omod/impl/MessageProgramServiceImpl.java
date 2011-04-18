package org.motechproject.server.omod.impl;

import org.motechproject.server.model.MessageProgram;
import org.motechproject.server.svc.MessageProgramService;
import org.motechproject.server.svc.MessageProgramFactory;

import java.util.Map;

public class MessageProgramServiceImpl implements MessageProgramService {

    Map<String, MessageProgramFactory> factories;

    public MessageProgram program(String programName) {
        return factories.get(programName).program();
    }

    public void setFactories(Map<String, MessageProgramFactory> factories) {
        this.factories = factories;
    }
}
