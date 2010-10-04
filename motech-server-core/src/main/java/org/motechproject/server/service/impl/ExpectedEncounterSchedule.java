/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) ${year} The Trustees of Columbia University in the City of
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

package org.motechproject.server.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.model.ExpectedEncounter;
import org.motechproject.server.service.ExpectedCareEvent;
import org.openmrs.Encounter;
import org.openmrs.Patient;

public class ExpectedEncounterSchedule extends ExpectedCareScheduleImpl {

	private static Log log = LogFactory.getLog(ExpectedEncounterSchedule.class);

	protected String encounterTypeName;

	@Override
	protected void performScheduleUpdate(Patient patient, Date date) {

		Date referenceDate = getReferenceDate(patient);
		if (!validReferenceDate(referenceDate, date)) {
			// Handle missing reference date as failed requirement
			log.debug("Failed to meet reference date requisite: "
					+ referenceDate + ", removing events for schedule");

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
		ExpectedEncounterPredicate expectedEncounterPredicate = new ExpectedEncounterPredicate();

		for (ExpectedCareEvent event : events) {
			// Calculate dates for event
			Date minDate = getMinDate(referenceDate, event);
			Date dueDate = getDueDate(referenceDate, event);
			Date lateDate = getLateDate(dueDate, event);
			Date maxDate = getMaxDate(referenceDate, event);
			// Set due date to min date if calculated due date is before min
			// date
			if (dueDate != null && minDate != null && dueDate.before(minDate)) {
				dueDate = minDate;
			}

			// Find Encounter satisfying event
			encounterPredicate.setMinDate(minDate);
			encounterPredicate.setMaxDate(maxDate);
			Encounter eventEncounter = getEventEncounter(encounterList,
					encounterPredicate);

			// Find ExpectedEncounter previously created for event
			expectedEncounterPredicate.setName(event.getName());
			ExpectedEncounter expectedEncounter = getEventExpectedEncounter(
					expectedEncounterList, expectedEncounterPredicate);

			boolean eventExpired = maxDate != null && date.after(maxDate);

			if (expectedEncounter != null) {
				if (eventEncounter != null) {
					// Remove existing satisfied ExpectedEncounter
					expectedEncounter.setEncounter(eventEncounter);
					expectedEncounter.setVoided(true);
					registrarBean.saveExpectedEncounter(expectedEncounter);
				} else {
					// Update existing ExpectedEncounter, removing if expired
					expectedEncounter.setMinEncounterDatetime(minDate);
					if (dueDate != null) {
						expectedEncounter.setDueEncounterDatetime(dueDate);
					}
					if (lateDate != null) {
						expectedEncounter.setLateEncounterDatetime(lateDate);
					}
					expectedEncounter.setMaxEncounterDatetime(maxDate);
					if (eventExpired) {
						expectedEncounter.setVoided(true);
					}
					registrarBean.saveExpectedEncounter(expectedEncounter);
				}
			} else if (!eventExpired && eventEncounter == null
					&& dueDate != null && lateDate != null) {
				// Create new ExpectedEncounter if not expired, not satisfied,
				// and due date and late date are defined
				registrarBean.createExpectedEncounter(patient,
						encounterTypeName, minDate, dueDate, lateDate, maxDate,
						event.getName(), name);
			}
		}
		// Remove any remaining Expected Encounters not already updated
		removeExpectedEncounters(expectedEncounterList);
	}

	@SuppressWarnings("unchecked")
	protected Encounter getEventEncounter(List<Encounter> encounterList,
			EncounterPredicate encounterPredicate) {
		List<Encounter> eventEncounter = (List<Encounter>) CollectionUtils
				.select(encounterList, encounterPredicate);
		if (!eventEncounter.isEmpty()) {
			if (eventEncounter.size() > 1) {
				log.debug("Multiple matches for delivered care : "
						+ eventEncounter.size());
			}
			// List is ascending by date, remove first match
			Encounter encounter = eventEncounter.get(0);
			encounterList.remove(encounter);
			return encounter;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	protected ExpectedEncounter getEventExpectedEncounter(
			List<ExpectedEncounter> expectedEncounterList,
			ExpectedEncounterPredicate expectedEncounterPredicate) {
		List<ExpectedEncounter> eventExpectedEncounter = (List<ExpectedEncounter>) CollectionUtils
				.select(expectedEncounterList, expectedEncounterPredicate);
		if (!eventExpectedEncounter.isEmpty()) {
			if (eventExpectedEncounter.size() > 1) {
				log.debug("Multiple matches for expected care : "
						+ eventExpectedEncounter.size());
			}
			// List is ascending by due date, remove first match
			ExpectedEncounter expectedEncounter = eventExpectedEncounter.get(0);
			expectedEncounterList.remove(expectedEncounter);
			return expectedEncounter;
		}
		return null;
	}

	@Override
	protected void removeExpectedCare(Patient patient) {
		List<ExpectedEncounter> expectedEncounterList = registrarBean
				.getExpectedEncounters(patient, name);
		removeExpectedEncounters(expectedEncounterList);
	}

	protected void removeExpectedEncounters(
			List<ExpectedEncounter> expectedEncounterList) {
		for (ExpectedEncounter expectedEncounter : expectedEncounterList) {
			expectedEncounter.setVoided(true);
			registrarBean.saveExpectedEncounter(expectedEncounter);
		}
	}

	public String getEncounterTypeName() {
		return encounterTypeName;
	}

	public void setEncounterTypeName(String encounterTypeName) {
		this.encounterTypeName = encounterTypeName;
	}

}
