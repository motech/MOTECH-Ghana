package org.motech.event.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.motech.event.MessageProgram;
import org.motech.event.MessageProgramState;
import org.motech.event.MessageProgramStateTransition;
import org.motech.event.MessagesCommand;
import org.motech.model.MessageProgramEnrollment;
import org.motech.time.TimeBean;
import org.motech.time.TimePeriod;
import org.motech.time.TimeReference;

public class MessageProgramStateImpl extends BaseInterfaceImpl implements
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
			MessageProgramEnrollment enrollment) {
		for (MessageProgramStateTransition transition : transitions) {
			if (transition.evaluate(enrollment)) {
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

	public Date getDateOfAction(MessageProgramEnrollment enrollment) {
		return timeBean.determineTime(timePeriod, timeReference, timeValue,
				null, enrollment, program.getConceptName(), program
						.getConceptValue(), null);
	}

}
