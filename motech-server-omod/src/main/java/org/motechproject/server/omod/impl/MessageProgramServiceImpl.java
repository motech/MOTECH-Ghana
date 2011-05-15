package org.motechproject.server.omod.impl;

import org.motechproject.server.model.MessageProgram;
import org.motechproject.server.svc.MessageProgramFactory;
import org.motechproject.server.svc.MessageProgramService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageProgramServiceImpl implements MessageProgramService {

    Map<String,MessageProgramFactory> programMap = new HashMap<String,MessageProgramFactory>();

    public MessageProgram program(String programName){
        MessageProgram program = programMap.get(programName).program();
        return program;
    }

    public void setMessageFactories(List<MessageProgramFactory> messageFactories) {
        for (MessageProgramFactory messageFactory : messageFactories) {
            programMap.put(messageFactory.name(),messageFactory);
        }
    }

}
