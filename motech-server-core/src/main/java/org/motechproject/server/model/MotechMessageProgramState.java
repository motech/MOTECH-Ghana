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

import org.motechproject.server.event.MessagesCommand;
import org.motechproject.server.model.db.ProgramMessageKey;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.server.time.TimeBean;
import org.motechproject.server.time.TimePeriod;
import org.motechproject.server.time.TimeReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MotechMessageProgramState implements MessageProgramState {

    private List<MessageProgramStateTransition> transitions = new ArrayList<MessageProgramStateTransition>();
    private MessagesCommand command;
    private Integer timeValue;
    private TimePeriod timePeriod;
    private TimeReference timeReference;
    private Long id;
    private ProgramMessageKey messageKey;
    private String conceptName;
    private String conceptValue;
    private String name;
    private TimeBean timeBean;
    private MotechMessageProgramState next ;

    public void addTransition(MessageProgramStateTransition transition) {
        transitions.add(transition);
    }

    public MessageProgramStateTransition getTransition(MessageProgramEnrollment enrollment, Date currentDate, RegistrarBean registrarBean) {
        for (MessageProgramStateTransition transition : transitions) {
            if (transition.evaluate(enrollment, currentDate, registrarBean)) {
                return transition;
            }
        }
        return null;
    }

    public Date getDateOfAction(MessageProgramEnrollment enrollment, Date currentDate) {
        Date actionDate = timeBean.determineTime(timePeriod, timeReference, timeValue, null, enrollment, conceptName, conceptValue, null, null);
        return command.adjustActionDate(enrollment, actionDate, currentDate);
    }


    public MessagesCommand getCommand() {
        return command;
    }

    public void setCommand(MessagesCommand command) {
        this.command = command;
    }

    public void setTimeValue(Integer timeValue) {
        this.timeValue = timeValue;
    }

    public void setTimePeriod(TimePeriod timePeriod) {
        this.timePeriod = timePeriod;
    }

    public void setTimeReference(TimeReference timeReference) {
        this.timeReference = timeReference;
    }

    public void setTime(int timeValue, TimePeriod timePeriod,
                        TimeReference timeReference) {
        setTimeValue(timeValue);
        setTimePeriod(timePeriod);
        setTimeReference(timeReference);
    }

    public List<MessageProgramStateTransition> getTransitions() {
        return transitions;
    }

    public void setTransitions(List<MessageProgramStateTransition> transitions) {
        this.transitions = transitions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProgramMessageKey getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(ProgramMessageKey messageKey) {
        this.messageKey = messageKey;
    }

    public String getConceptName() {
        return conceptName;
    }

    public void setConceptName(String conceptName) {
        this.conceptName = conceptName;
    }

    public String getConceptValue() {
        return conceptValue;
    }

    public void setConceptValue(String conceptValue) {
        this.conceptValue = conceptValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTimeBean(TimeBean timeBean) {
        this.timeBean = timeBean;
    }

    public MotechMessageProgramState getNext() {
        return next;
    }

    public Boolean hasNext() {
        return null != next ;
    }

    public void setNext(MotechMessageProgramState previous) {
        this.next = previous;
    }

    @Override
    public boolean equals(Object object) {
        MotechMessageProgramState otherState = (MotechMessageProgramState) object;
        return name.equals(otherState.getName());
    }
}
