/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010 The Trustees of Columbia University in the City of
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

package org.motechproject.server.svc.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.model.ExpectedEncounter;
import org.motechproject.server.model.ExpectedObs;
import org.motechproject.server.model.Facility;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.MotechService;
import org.motechproject.server.svc.ExpectedCareBean;
import org.motechproject.server.svc.OpenmrsBean;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;

/**
 * An implementation of the ExpectedCareBean interface.
 */
public class ExpectedCareBeanImpl implements ExpectedCareBean {

	private static Log log = LogFactory.getLog(ExpectedCareBeanImpl.class);

	private ContextService contextService;
	private OpenmrsBean openmrsBean;

	public void setContextService(ContextService contextService) {
		this.contextService = contextService;
	}

	public OpenmrsBean getOpenmrsBean() {
		return openmrsBean;
	}

	public void setOpenmrsBean(OpenmrsBean openmrsBean) {
		this.openmrsBean = openmrsBean;
	}

	public List<ExpectedEncounter> getUpcomingExpectedEncounters(Patient patient) {
		MotechService motechService = contextService.getMotechService();
		Calendar calendar = Calendar.getInstance();
		Date currentDate = calendar.getTime();
		calendar.add(Calendar.DATE, 7);
		Date oneWeekLaterDate = calendar.getTime();
		Integer maxResults = openmrsBean.getMaxQueryResults();
		return motechService.getExpectedEncounter(patient, null, null, null,
				oneWeekLaterDate, null, currentDate, maxResults);
	}

	public List<ExpectedObs> getUpcomingExpectedObs(Patient patient) {
		MotechService motechService = contextService.getMotechService();
		Calendar calendar = Calendar.getInstance();
		Date currentDate = calendar.getTime();
		calendar.add(Calendar.DATE, 7);
		Date oneWeekLaterDate = calendar.getTime();
		Integer maxResults = openmrsBean.getMaxQueryResults();
		return motechService.getExpectedObs(patient, null, null, null,
				oneWeekLaterDate, null, currentDate, maxResults);
	}

	public List<ExpectedEncounter> getDefaultedExpectedEncounters(
			Facility facility, String[] groups) {
		MotechService motechService = contextService.getMotechService();
		Date currentDate = new Date();
		Integer maxResults = openmrsBean.getMaxQueryResults();
		return motechService.getExpectedEncounter(null, facility, groups, null,
				null, currentDate, currentDate, maxResults);
	}

	public List<ExpectedObs> getDefaultedExpectedObs(Facility facility,
			String[] groups) {
		MotechService motechService = contextService.getMotechService();
		Date currentDate = new Date();
		Integer maxResults = openmrsBean.getMaxQueryResults();
		return motechService.getExpectedObs(null, facility, groups, null, null,
				currentDate, currentDate, maxResults);
	}

	public List<ExpectedEncounter> getUpcomingExpectedEncounters(
			Facility facility, String[] groups, Date fromDate, Date toDate) {
		MotechService motechService = contextService.getMotechService();
		Integer maxResults = openmrsBean.getMaxQueryResults();
		return motechService.getExpectedEncounter(null, facility, groups, null,
				toDate, null, fromDate, maxResults);
	}

	public List<ExpectedObs> getUpcomingExpectedObs(Facility facility,
			String[] groups, Date fromDate, Date toDate) {
		MotechService motechService = contextService.getMotechService();
		Integer maxResults = openmrsBean.getMaxQueryResults();
		return motechService.getExpectedObs(null, facility, groups, null,
				toDate, null, fromDate, maxResults);
	}

	public List<ExpectedEncounter> getDefaultedExpectedEncounters(
			Facility facility, String[] groups, Date forDate) {
		MotechService motechService = contextService.getMotechService();
		Integer maxResults = openmrsBean.getMaxQueryResults();
		return motechService.getExpectedEncounter(null, facility, groups, null,
				null, forDate, forDate, maxResults);
	}

	public List<ExpectedObs> getDefaultedExpectedObs(Facility facility,
			String[] groups, Date forDate) {
		MotechService motechService = contextService.getMotechService();
		Integer maxResults = openmrsBean.getMaxQueryResults();
		return motechService.getExpectedObs(null, facility, groups, null, null,
				forDate, forDate, maxResults);
	}

	public List<ExpectedEncounter> getExpectedEncounters(Patient patient) {
		MotechService motechService = contextService.getMotechService();
		Date currentDate = new Date();
		return motechService.getExpectedEncounter(patient, null, null, null,
				null, null, currentDate, null);
	}

	public List<ExpectedObs> getExpectedObs(Patient patient) {
		MotechService motechService = contextService.getMotechService();
		Date currentDate = new Date();
		return motechService.getExpectedObs(patient, null, null, null, null,
				null, currentDate, null);
	}

	public List<ExpectedEncounter> getExpectedEncounters(Patient patient,
			String group) {
		MotechService motechService = contextService.getMotechService();
		return motechService.getExpectedEncounter(patient, null,
				new String[] { group }, null, null, null, null, null);
	}

	public List<ExpectedObs> getExpectedObs(Patient patient, String group) {
		MotechService motechService = contextService.getMotechService();
		return motechService.getExpectedObs(patient, null,
				new String[] { group }, null, null, null, null, null);
	}

	public ExpectedObs createExpectedObs(Patient patient, String conceptName,
			String valueConceptName, Integer value, Date minDate, Date dueDate,
			Date lateDate, Date maxDate, String name, String group) {
		ConceptService conceptService = contextService.getConceptService();

		Concept concept = conceptService.getConcept(conceptName);
		Concept valueConcept = conceptService.getConcept(valueConceptName);

		ExpectedObs expectedObs = new ExpectedObs();
		expectedObs.setPatient(patient);
		expectedObs.setConcept(concept);
		expectedObs.setValueCoded(valueConcept);
		if (value != null) {
			expectedObs.setValueNumeric(new Double(value));
		}
		expectedObs.setMinObsDatetime(minDate);
		expectedObs.setDueObsDatetime(dueDate);
		expectedObs.setLateObsDatetime(lateDate);
		expectedObs.setMaxObsDatetime(maxDate);
		expectedObs.setName(name);
		expectedObs.setGroup(group);

		return saveExpectedObs(expectedObs);
	}

	public ExpectedObs saveExpectedObs(ExpectedObs expectedObs) {
		if (log.isDebugEnabled()) {
			log.debug("Saving schedule update: " + expectedObs.toString());
		}
		if (expectedObs.getDueObsDatetime() != null
				&& expectedObs.getLateObsDatetime() != null) {

			MotechService motechService = contextService.getMotechService();
			return motechService.saveExpectedObs(expectedObs);
		} else {
			log
					.error("Attempt to store ExpectedObs with null due or late date");
			return null;
		}
	}

	public ExpectedEncounter createExpectedEncounter(Patient patient,
			String encounterTypeName, Date minDate, Date dueDate,
			Date lateDate, Date maxDate, String name, String group) {
		EncounterService encounterService = contextService
				.getEncounterService();

		EncounterType encounterType = encounterService
				.getEncounterType(encounterTypeName);

		ExpectedEncounter expectedEncounter = new ExpectedEncounter();
		expectedEncounter.setPatient(patient);
		expectedEncounter.setEncounterType(encounterType);
		expectedEncounter.setMinEncounterDatetime(minDate);
		expectedEncounter.setDueEncounterDatetime(dueDate);
		expectedEncounter.setLateEncounterDatetime(lateDate);
		expectedEncounter.setMaxEncounterDatetime(maxDate);
		expectedEncounter.setName(name);
		expectedEncounter.setGroup(group);

		return saveExpectedEncounter(expectedEncounter);
	}

	public ExpectedEncounter saveExpectedEncounter(
			ExpectedEncounter expectedEncounter) {
		if (log.isDebugEnabled()) {
			log
					.debug("Saving schedule update: "
							+ expectedEncounter.toString());
		}
		if (expectedEncounter.getDueEncounterDatetime() != null
				&& expectedEncounter.getLateEncounterDatetime() != null) {

			MotechService motechService = contextService.getMotechService();
			return motechService.saveExpectedEncounter(expectedEncounter);
		} else {
			log
					.error("Attempt to store ExpectedEncounter with null due or late date");
			return null;
		}
	}

	public void updateAllCareSchedules() {
		PatientService patientService = contextService.getPatientService();
		List<Patient> patients = patientService.getAllPatients();
		log
				.info("Updating care schedules for " + patients.size()
						+ " patients");

		for (Patient patient : patients) {
			// Adds patient to transaction synchronization using advice
			patientService.savePatient(patient);
		}
	}

}
