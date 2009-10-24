/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.motech.openmrs.module.advice;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motech.event.Regimen;
import org.motech.openmrs.module.MotechService;
import org.motech.util.MotechConstants;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.springframework.aop.AfterReturningAdvice;

/**
 * An OpenMRS AOP interceptor that enables us to perform various tasks upon an
 * observation being saved, whether that operation knows about it or not.
 * Currently, this is how we are handling calling the event engine.
 */
public class SaveObsAdvisor implements AfterReturningAdvice {

	private static Log log = LogFactory.getLog(SaveObsAdvisor.class);

	/**
	 * @see org.springframework.aop.AfterReturningAdvice#afterReturning(java.lang.Object,
	 *      java.lang.reflect.Method, java.lang.Object[], java.lang.Object)
	 */
	public void afterReturning(Object returnValue, Method method,
			Object[] args, Object target) throws Throwable {

		if (method.getName().equals("saveObs")) {
			Obs obs = (Obs) returnValue;

			MotechService motechService = Context
					.getService(MotechService.class);
			ConceptService conceptService = Context.getConceptService();
			PatientService patientService = Context.getPatientService();

			Concept regimenStart = conceptService
					.getConcept(MotechConstants.CONCEPT_REGIMEN_START);
			Integer obsPersonId = obs.getPerson().getPersonId();
			Patient patient = patientService.getPatient(obsPersonId);

			if (regimenStart.equals(obs.getConcept())) {

				String regimenName = obs.getValueText();

				log
						.debug("Save Obs - Update State for newly enrolled Regimen: "
								+ regimenName);

				Regimen enrolledRegimen = motechService.getRegimen(regimenName);

				enrolledRegimen.determineState(patient);

			} else {
				// Only determine regimen state for enrolled regimen
				// concerned with an observed concept
				// and matching the concept of this obs

				List<String> patientRegimens = motechService
						.getRegimenEnrollment(obsPersonId);

				for (String regimenName : patientRegimens) {
					Regimen regimen = motechService.getRegimen(regimenName);

					Concept regimenConcept = null;
					if (regimen.getConceptName() != null) {
						regimenConcept = conceptService.getConcept(regimen
								.getConceptName());

						if (obs.getConcept().equals(regimenConcept)) {
							log
									.debug("Save Obs - Obs matches Regmen concept, update Regimen: "
											+ regimenName);

							regimen.determineState(patient);
						}
					}
				}
			}
		}
	}

}
