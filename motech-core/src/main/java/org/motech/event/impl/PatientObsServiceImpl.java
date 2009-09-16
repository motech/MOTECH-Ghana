package org.motech.event.impl;

import java.util.Date;
import java.util.List;

import org.motech.event.PatientObsService;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.context.Context;

public class PatientObsServiceImpl implements PatientObsService {

	public int getNumberOfObs(Patient patient, String conceptName) {
		Concept concept = Context.getConceptService().getConcept(conceptName);
		List<Obs> obsList = Context.getObsService()
				.getObservationsByPersonAndConcept((Person) patient, concept);
		return obsList.size();
	}

	public Date getLastObsDate(Patient patient, String conceptName) {
		Date latestObsDate = null;

		Concept concept = Context.getConceptService().getConcept(conceptName);
		List<Obs> obsList = Context.getObsService()
				.getObservationsByPersonAndConcept((Person) patient, concept);

		for (Obs obs : obsList) {
			if (obs.getConcept().equals(concept)) {
				if (latestObsDate == null
						|| obs.getObsDatetime().getTime() > latestObsDate
								.getTime()) {
					latestObsDate = obs.getObsDatetime();
				}
			}
		}
		return latestObsDate;
	}

}
