package org.motechproject.server.model;

import org.motechproject.server.event.MessagesCommand;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.server.time.TimePeriod;
import org.motechproject.server.time.TimeReference;

import java.util.Date;

public interface MessageProgramState{

        MessagesCommand getCommand();

        void setCommand(MessagesCommand command);

        MessageProgramStateTransition getTransition(MessageProgramEnrollment enrollment, Date currentDate, RegistrarBean registrarBean);

        Integer getTimeValue();

        TimePeriod getTimePeriod();

        TimeReference getTimeReference();

        Date getDateOfAction(MessageProgramEnrollment enrollment, Date currentDate);

        String getConceptName();

        String getConceptValue();

        String getName();
    }