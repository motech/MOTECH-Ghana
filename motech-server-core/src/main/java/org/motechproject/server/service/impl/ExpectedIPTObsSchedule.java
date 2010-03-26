package org.motechproject.server.service.impl;

import java.util.Calendar;
import java.util.Date;

import org.openmrs.Patient;

public class ExpectedIPTObsSchedule extends ExpectedObsSchedule {

	@Override
	protected Date getReferenceDate(Patient patient) {
		// Calculate estimated pregnancy start date as 9 months before estimated
		// due date
		Date dueDate = registrarBean.getActivePregnancyDueDate(patient
				.getPatientId());
		if (dueDate != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dueDate);
			calendar.add(Calendar.MONTH, -9);
			return calendar.getTime();
		}
		return null;
	}

}
