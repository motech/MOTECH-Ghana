package org.motechproject.server.event;

import java.util.Date;

import org.motechproject.server.model.MessageProgramEnrollment;

public interface MessageProgram extends BaseInterface {

	MessageProgramState determineState(MessageProgramEnrollment enrollment,
			Date currentDate);

	MessageProgramState updateState(MessageProgramEnrollment enrollment,
			Date currentDate);

	MessageProgramState getStartState();

	MessageProgramState getEndState();

	String getConceptName();

	String getConceptValue();
}
