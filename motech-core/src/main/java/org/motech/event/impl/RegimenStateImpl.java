package org.motech.event.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.motech.event.Command;
import org.motech.event.PatientObsService;
import org.motech.event.Regimen;
import org.motech.event.RegimenState;
import org.motech.event.RegimenStateTransition;
import org.motech.event.TimePeriod;
import org.motech.event.TimeReference;
import org.openmrs.Patient;

public class RegimenStateImpl extends BaseInterfaceImpl implements RegimenState {

	private PatientObsService patientObsService;
	private List<RegimenStateTransition> transitions = new ArrayList<RegimenStateTransition>();
	private Command command;
	private Regimen regimen;
	private int timeValue;
	private TimePeriod timePeriod;
	private TimeReference timeReference;

	public void addTransition(RegimenStateTransition transition) {
		transitions.add(transition);
	}

	public RegimenStateTransition getTransition(Patient patient) {
		for (RegimenStateTransition transition : transitions) {
			if (transition.evaluate(patient)) {
				return transition;
			}
		}
		return null;
	}

	public PatientObsService getPatientObsService() {
		return patientObsService;
	}

	public void setPatientObsService(PatientObsService patientObsService) {
		this.patientObsService = patientObsService;
	}

	public Command getCommand() {
		return command;
	}

	public void setCommand(Command command) {
		this.command = command;
	}

	public Regimen getRegimen() {
		return regimen;
	}

	public void setRegimen(Regimen regimen) {
		this.regimen = regimen;
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

	public List<RegimenStateTransition> getTransitions() {
		return transitions;
	}

	public void setTransitions(List<RegimenStateTransition> transitions) {
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
				calendar.setTime(patientObsService.getLastObsDate(patient,
						regimen.getConceptName(), regimen.getConceptValue()));
				break;
			case last_obs_value:
				calendar.setTime(patientObsService.getLastObsValue(patient,
						regimen.getConceptName()));
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
