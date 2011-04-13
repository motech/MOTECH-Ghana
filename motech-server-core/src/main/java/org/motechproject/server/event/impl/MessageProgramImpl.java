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

package org.motechproject.server.event.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.event.MessageProgram;
import org.motechproject.server.event.MessageProgramState;
import org.motechproject.server.model.MessageProgramStateTransition;
import org.motechproject.server.event.MessagesCommand;
import org.motechproject.server.model.MessageProgramEnrollment;
import org.motechproject.server.svc.RegistrarBean;

import java.util.Date;

public class MessageProgramImpl extends BaseInterfaceImpl implements
		MessageProgram {

	private static Log log = LogFactory.getLog(MessageProgramImpl.class);

	private MessageProgramState startState;
	private MessageProgramState endState;
	private String conceptName;
	private String conceptValue;
    private RegistrarBean registrarBean;

	public MessageProgramState getStartState() {
		return startState;
	}

	public void setStartState(MessageProgramState startState) {
		this.startState = startState;
	}

	public MessageProgramState getEndState() {
		return endState;
	}

	public void setEndState(MessageProgramState endState) {
		this.endState = endState;
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

	public MessageProgramState updateState(MessageProgramEnrollment enrollment, Date currentDate) {
		MessageProgramState state = determineState(enrollment, currentDate);
		if (state.equals(endState)) {
			return state;
		}
		MessageProgramStateTransition transition = state.getTransition(enrollment, currentDate, registrarBean);
		Date actionDate = state.getDateOfAction(enrollment, currentDate);
		transition.getCommand().execute(enrollment, actionDate, currentDate);
		MessageProgramState newState = transition.getNextState();

		return newState;
	}

    public RegistrarBean getRegistrarBean() {
        return registrarBean;
    }

    public void setRegistrarBean(RegistrarBean registrarBean) {
        this.registrarBean = registrarBean;
    }

}
