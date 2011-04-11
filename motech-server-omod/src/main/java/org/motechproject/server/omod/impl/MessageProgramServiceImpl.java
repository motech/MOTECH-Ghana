package org.motechproject.server.omod.impl;

import org.motechproject.server.event.MessageProgram;
import org.motechproject.server.model.MessageProgramType;
import org.motechproject.server.svc.MessageProgramService;
import org.motechproject.server.svc.MessageProgramFactory;

import java.util.Map;

public class MessageProgramServiceImpl implements MessageProgramService {

    Map<String, MessageProgramFactory> factories;

    public MessageProgram program(MessageProgramType expectedCare) {
        MessageProgramFactory factory = factories.get(expectedCare.program());
        return factory.program();
    }

    public void setFactories(Map<String, MessageProgramFactory> factories) {
        this.factories = factories;
    }
}
