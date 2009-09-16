package org.motech.event;

import java.util.Date;

import org.openmrs.Patient;

public interface RegimenState extends BaseInterface {

	Command getCommand();

	RegimenStateTransition getTransition(Patient patient);

	Regimen getRegimen();

	int getTimeValue();

	TimePeriod getTimePeriod();

	TimeReference getTimeReference();

	Date getDateOfAction(Patient patient);
}
