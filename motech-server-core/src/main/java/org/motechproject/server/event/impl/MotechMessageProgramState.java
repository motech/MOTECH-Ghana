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

import org.motechproject.server.event.MessageProgram;
import org.motechproject.server.event.MessageProgramState;
import org.motechproject.server.event.MessageProgramStateTransition;
import org.motechproject.server.event.MessagesCommand;
import org.motechproject.server.model.MessageProgramEnrollment;
import org.motechproject.server.time.TimeBean;
import org.motechproject.server.time.TimePeriod;
import org.motechproject.server.time.TimeReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MotechMessageProgramState extends BaseInterfaceImpl implements
		MessageProgramState {

	private List<MessageProgramStateTransition> transitions = new ArrayList<MessageProgramStateTransition>();
	private MessagesCommand command;
	private MessageProgram program;
	private Integer timeValue;
	private TimePeriod timePeriod;
	private TimeReference timeReference;
	private TimeBean timeBean;

	public void addTransition(MessageProgramStateTransition transition) {
		transitions.add(transition);
	}

	public MessageProgramStateTransition getTransition(
			MessageProgramEnrollment enrollment, Date currentDate) {
		for (MessageProgramStateTransition transition : transitions) {
			if (transition.evaluate(enrollment, currentDate)) {
				return transition;
			}
		}
		return null;
	}

	public MessagesCommand getCommand() {
		return command;
	}

	public void setCommand(MessagesCommand command) {
		this.command = command;
	}

	public MessageProgram getProgram() {
		return program;
	}

	public void setProgram(MessageProgram program) {
		this.program = program;
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

	public TimeReference getTimeReference() {
		return timeReference;
	}

	public void setTimeReference(TimeReference timeReference) {
		this.timeReference = timeReference;
	}

	public TimeBean getTimeBean() {
		return timeBean;
	}

	public void setTimeBean(TimeBean timeBean) {
		this.timeBean = timeBean;
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

	public Date getDateOfAction(MessageProgramEnrollment enrollment,
			Date currentDate) {
		Date actionDate = timeBean.determineTime(timePeriod, timeReference,
				timeValue, null, enrollment, program.getConceptName(), program
						.getConceptValue(), null, null);

		return command.adjustActionDate(enrollment, actionDate, currentDate);
	}

}
