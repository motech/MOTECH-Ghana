package org.motechproject.server.model;

import org.motechproject.server.event.MessagesCommand;
import org.motechproject.server.model.db.ProgramMessageKey;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.server.time.TimeBean;
import org.motechproject.server.time.TimePeriod;
import org.motechproject.server.time.TimeReference;

import java.util.Date;

public interface MessageProgramState {

    MessagesCommand getCommand();

    MessageProgramStateTransition getTransition(MessageProgramEnrollment enrollment, Date currentDate, RegistrarBean registrarBean);

    Date getDateOfAction(MessageProgramEnrollment enrollment, Date currentDate);

    String getConceptName();

    String getConceptValue();

    String getName();

    MessageProgramState getNext();

    Boolean hasNext();

    ProgramMessageKey getMessageKey();

    void setCommand(MessagesCommand command);

    void setTimeBean(TimeBean timeBean);
}