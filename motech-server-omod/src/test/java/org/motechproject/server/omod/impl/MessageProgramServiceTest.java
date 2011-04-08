package org.motechproject.server.omod.impl;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.server.event.impl.ExpectedCareMessageProgram;
import org.motechproject.server.model.MessageProgramType;
import org.motechproject.server.omod.MessageProgramService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MessageProgramServiceTest  extends BaseModuleContextSensitiveTest {

    @Autowired
    MessageProgramService messageProgramService ;

    @Before
    public void setUp() throws Exception {
      executeDataSet("message-program-dataset.xml");
    }

    @Test
    public void shouldGetExpectedMessageCareProgram() {
        ExpectedCareMessageProgram program = (ExpectedCareMessageProgram) messageProgramService.program(MessageProgramType.ExpectedCare);
        assertNotNull(program);
        assertTrue(program.hasMessageCareDetails());
    }
}
