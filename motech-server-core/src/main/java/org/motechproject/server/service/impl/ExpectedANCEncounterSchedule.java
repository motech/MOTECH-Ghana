package org.motechproject.server.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.model.ExpectedEncounter;
import org.openmrs.Encounter;
import org.openmrs.Patient;

public class ExpectedANCEncounterSchedule extends ExpectedEncounterSchedule {

	private static Log log = LogFactory
			.getLog(ExpectedANCEncounterSchedule.class);

	@Override
	protected void performScheduleUpdate(Patient patient, Date date) {

		Date referenceDate = getReferenceDate(patient);
		if (referenceDate == null) {
			// Handle missing reference date as failed requirement
			removeExpectedCare(patient);
			return;
		}
		log.debug("Performing " + name + " schedule update: patient: "
				+ patient.getPatientId());

		List<Encounter> encounterList = registrarBean.getEncounters(patient,
				encounterTypeName, referenceDate);
		List<ExpectedEncounter> expectedEncounterList = registrarBean
				.getExpectedEncounters(patient, name);

		EncounterPredicate encounterPredicate = new EncounterPredicate();

		for (ExpectedEncounter expectedEncounter : expectedEncounterList) {
			Date minDate = expectedEncounter.getMinEncounterDatetime();
			Date maxDate = expectedEncounter.getMaxEncounterDatetime();

			// Find Encounter satisfying expected
			encounterPredicate.setMinDate(minDate);
			encounterPredicate.setMaxDate(maxDate);
			Encounter eventEncounter = getEventEncounter(encounterList,
					encounterPredicate);

			boolean eventExpired = maxDate != null && date.after(maxDate);

			if (eventEncounter != null) {
				// Remove existing satisfied ExpectedEncounter
				expectedEncounter.setEncounter(eventEncounter);
				expectedEncounter.setVoided(true);
				registrarBean.saveExpectedEncounter(expectedEncounter);
			} else if (eventExpired) {
				expectedEncounter.setVoided(true);
				registrarBean.saveExpectedEncounter(expectedEncounter);
			}
		}
	}

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
