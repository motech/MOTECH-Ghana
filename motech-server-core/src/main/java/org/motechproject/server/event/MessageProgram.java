package org.motechproject.server.event;

import org.motechproject.server.model.MessageProgramEnrollment;

public interface MessageProgram extends BaseInterface {

	MessageProgramState determineState(MessageProgramEnrollment enrollment);

	MessageProgramState updateState(MessageProgramEnrollment enrollment);

	MessageProgramState getStartState();

	MessageProgramState getEndState();

	String getConceptName();

	String getConceptValue();
}
