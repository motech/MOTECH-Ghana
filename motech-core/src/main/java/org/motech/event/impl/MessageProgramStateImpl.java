package org.motech.event.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.motech.event.Command;
import org.motech.event.MessageProgram;
import org.motech.event.MessageProgramState;
import org.motech.event.MessageProgramStateTransition;
import org.motech.event.TimePeriod;
import org.motech.event.TimeReference;
import org.motech.svc.RegistrarBean;
import org.openmrs.Patient;

public class MessageProgramStateImpl extends BaseInterfaceImpl implements
		MessageProgramState {

	private RegistrarBean registrarBean;
	private List<MessageProgramStateTransition> transitions = new ArrayList<MessageProgramStateTransition>();
	private Command command;
	private MessageProgram program;
	private int timeValue;
	private TimePeriod timePeriod;
	private TimeReference timeReference;

	public void addTransition(MessageProgramStateTransition transition) {
		transitions.add(transition);
	}

	public MessageProgramStateTransition getTransition(Patient patient) {
		for (MessageProgramStateTransition transition : transitions) {
			if (transition.evaluate(patient)) {
				return transition;
			}
		}
		return null;
	}

	public RegistrarBean getRegistrarBean() {
		return registrarBean;
	}

	public void setRegistrarBean(RegistrarBean registrarBean) {
		this.registrarBean = registrarBean;
	}

	public Command getCommand() {
		return command;
	}

	public void setCommand(Command command) {
		this.command = command;
	}

	public MessageProgram getProgram() {
		return program;
	}

	public void setProgram(MessageProgram program) {
		this.program = program;
	}

	public int getTimeValue() {
		return timeValue;
	}

	public void setTimeValue(int timeValue) {
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

	public Date getDateOfAction(Patient patient) {

		if (timePeriod != null && timeReference != null) {

			Calendar calendar = Calendar.getInstance();
			switch (timeReference) {
			case patient_age:
				calendar.setTime(patient.getBirthdate());
				break;
			case last_obs:
				Date obsDate = registrarBean.getLastObsDate(patient, program
						.getConceptName(), program.getConceptValue());
				if (obsDate == null) {
					return null;
				}
				calendar.setTime(obsDate);
				break;
			case last_obs_value:
				Date obsValueDate = registrarBean.getLastObsValue(patient,
						program.getConceptName());
				if (obsValueDate == null) {
					return null;
				}
				calendar.setTime(obsValueDate);
				break;
			case patient_registration:
				calendar.setTime(patient.getDateCreated());
				break;
			}

			switch (timePeriod) {
			case minute:
				calendar.add(Calendar.MINUTE, timeValue);
				break;
			case day:
				calendar.add(Calendar.DATE, timeValue);
				break;
			case week:
				// Add weeks as days
				calendar.add(Calendar.DATE, timeValue * 7);
				break;
			case month:
				calendar.add(Calendar.MONTH, timeValue);
				break;
			case year:
				calendar.add(Calendar.YEAR, timeValue);
				break;
			}

			return calendar.getTime();
		}
		return null;
	}

}
