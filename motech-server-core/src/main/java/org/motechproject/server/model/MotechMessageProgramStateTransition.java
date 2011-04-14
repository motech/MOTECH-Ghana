/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010-11 The Trustees of Columbia University in the City of
 * New York and Grameen Foundation USA.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Grameen Foundation USA, Columbia University, or
 * their respective contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA, COLUMBIA UNIVERSITY
 * AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION
 * USA, COLUMBIA UNIVERSITY OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.motechproject.server.model;

import org.motechproject.server.event.MessageProgramState;
import org.motechproject.server.event.MessagesCommand;
import org.motechproject.server.event.impl.BaseInterfaceImpl;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.server.time.TimePeriod;

import java.util.Calendar;
import java.util.Date;

public class MotechMessageProgramStateTransition extends BaseInterfaceImpl implements MessageProgramStateTransition {

    protected MessageProgramState prevState;
    protected MessageProgramState nextState;
    protected MessagesCommand command;
    protected Integer timeValue;
    protected TimePeriod timePeriod;
    private Long id;


    public Boolean evaluate(MessageProgramEnrollment enrollment, Date currentDate, RegistrarBean registrarBean) {
        // Default Transition is always taken
        return true;
    }

    private Date calculateDateBasedOnTimePeriodAndTimeValue(Date date, Integer value, TimePeriod period) {
        final boolean anyParameterNotAvailable = (date == null || value == null || period == null);

        if (anyParameterNotAvailable) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (period.equals(TimePeriod.week))
            calendar.add(period.getCalendarPeriod(), value * 7);
        else
            calendar.add(period.getCalendarPeriod(), value);
        return calendar.getTime();
    }

    protected Boolean evaluateBasedOnDates(MessageProgramEnrollment enrollment, Date currentDate) {
        if (nextState.equals(prevState)) {
            Date actionDate = nextState.getDateOfAction(enrollment, currentDate);
            return !isDateNull(actionDate) && isActionDateTodayOrInFuture(currentDate, actionDate);
        } else if (isTimeDetailDefined()) {
            Date actionDate = calculateDateBasedOnTimePeriodAndTimeValue(nextState.getDateOfAction(enrollment, currentDate), timeValue, timePeriod);
            return !isDateNull(actionDate) && isActionDateInPast(currentDate, actionDate);
        } else {
            Date actionDate = prevState.getDateOfAction(enrollment, currentDate);
            return !isDateNull(actionDate) && isActionDateInPast(currentDate, actionDate);
        }
    }

    public MessageProgramState getPrevState() {
        return prevState;
    }

    public void setPrevState(MessageProgramState prevState) {
        this.prevState = prevState;
    }

    public MessageProgramState getNextState() {
        return nextState;
    }

    public void setNextState(MessageProgramState nextState) {
        this.nextState = nextState;
    }

    public MessagesCommand getCommand() {
        return command;
    }

    public void setCommand(MessagesCommand command) {
        this.command = command;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Integer getTimeValue() {
        return timeValue;
    }

    public void setTimeValue(Integer timeValue) {
        this.timeValue = timeValue;
    }

    public TimePeriod getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(TimePeriod timePeriod) {
        this.timePeriod = timePeriod;
    }

    private boolean isActionDateInPast(Date currentDate, Date calculatedActionDate) {
        return currentDate.after(calculatedActionDate);
    }

    private boolean isTimeDetailDefined() {
        return timeValue != null && timePeriod != null;
    }

    private boolean isDateNull(Date actionDate) {
        return actionDate == null;
    }

    private boolean isActionDateTodayOrInFuture(Date currentDate, Date actionDate) {
        return currentDate.before(actionDate) || currentDate.equals(actionDate);
    }
}