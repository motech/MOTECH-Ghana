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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.event.MessagesCommand;
import org.motechproject.server.svc.RegistrarBean;

import java.util.Date;

public class MotechMessageProgram implements MessageProgram {

    private static Log log = LogFactory.getLog(MotechMessageProgram.class);

    private MotechMessageProgramState startState;
    private MotechMessageProgramState endState;
    private RegistrarBean registrarBean;
    private Long id;
    private String name;

    public MessageProgramState determineState(MessageProgramEnrollment enrollment, Date currentDate) {
        MessageProgramState state = startState;
        MessageProgramStateTransition transition = state.getTransition(enrollment, currentDate, registrarBean);
        while (!transition.getNextState().equals(state)) {
            state = transition.getNextState();
            transition = state.getTransition(enrollment, currentDate, registrarBean);
        }

        if (log.isDebugEnabled()) {
            log.debug("Message Program determineState: enrollment id: "
                    + enrollment.getId() + ", state: " + state.getName());
        }

        Date actionDate = state.getDateOfAction(enrollment, currentDate);

        // Perform state action using date and enrollment
        MessagesCommand command = state.getCommand();
        command.execute(enrollment, actionDate, currentDate);

        return state;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MotechMessageProgramState getStartState() {
        return startState;
    }

    public void setStartState(MotechMessageProgramState startState) {
        this.startState = startState;
    }

    public MotechMessageProgramState getEndState() {
        return endState;
    }

    public void setEndState(MotechMessageProgramState endState) {
        this.endState = endState;
    }

    public RegistrarBean getRegistrarBean() {
        return registrarBean;
    }

    public void setRegistrarBean(RegistrarBean registrarBean) {
        this.registrarBean = registrarBean;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean equals(Object object) {
		MotechMessageProgramState otherState = (MotechMessageProgramState) object;
		return name.equals(otherState.getName());
	}
}
