/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010-11 The Trustees of Columbia University in the City of
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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.model.ExpectedEncounter;
import org.motechproject.server.time.TimePeriod;
import org.openmrs.Patient;

import java.util.Date;
import java.util.List;

public class ExpectedEDDEncounterSchedule extends ExpectedEncounterSchedule {

	private static Log log = LogFactory.getLog(ExpectedEDDEncounterSchedule.class);

	@Override
	protected void performScheduleUpdate(Patient patient, Date date) {

		Date referenceDate = getReferenceDate(patient);
		if (referenceDate == null) {
			// Handle missing reference date as failed requirement
			removeExpectedCare(patient);
			return;
		}
		log.debug("Performing " + name + " schedule update: patient: " + patient.getPatientId());

		List<ExpectedEncounter> expectedEncounterList = registrarBean.getExpectedEncounters(patient, name);

        Date minDate = calculateDate(referenceDate, -1, TimePeriod.week);
        Date lateDate = calculateDate(referenceDate, lateValue, latePeriod);
        Date maxDate = calculateDate(referenceDate, maxValue, maxPeriod);

        // Only create if there is no ExpectedEncounter already
        if (expectedEncounterList.isEmpty()) {
            registrarBean.createExpectedEncounter(patient, encounterTypeName,
                                                  minDate, referenceDate, lateDate,
                                                  maxDate, name, name);
        } else {
            ExpectedEncounter expectedEncounter = expectedEncounterList.get(0);

			boolean eventExpired = maxDate != null && date.after(maxDate);

            if (eventExpired) {
				expectedEncounter.setVoided(true);

				registrarBean.saveExpectedEncounter(expectedEncounter);
			}  else {
                expectedEncounter.setMinEncounterDatetime(minDate);
                expectedEncounter.setDueEncounterDatetime(referenceDate);
                expectedEncounter.setLateEncounterDatetime(lateDate);
                expectedEncounter.setMaxEncounterDatetime(maxDate);

                registrarBean.saveExpectedEncounter(expectedEncounter);
            }
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected ExpectedEncounter getEventExpectedEncounter(List<ExpectedEncounter> expectedEncounterList,
                                                          ExpectedEncounterPredicate expectedEncounterPredicate) {
		List<ExpectedEncounter> eventExpectedEncounter;
        eventExpectedEncounter = (List<ExpectedEncounter>) CollectionUtils.select(expectedEncounterList,
                                                                                  expectedEncounterPredicate);
		if (!eventExpectedEncounter.isEmpty()) {
			if (eventExpectedEncounter.size() > 1) {
				log.debug("Multiple matches for expected care : " + eventExpectedEncounter.size());
			}

			// List is ascending by due date, remove first match
			ExpectedEncounter expectedEncounter = eventExpectedEncounter.get(0);

			// Unlike super class, Do not remove match from list
			return expectedEncounter;
		}
		return null;
	}

	@Override
	protected Date getReferenceDate(Patient patient) {
		Date dueDate = registrarBean.getActivePregnancyDueDate(patient.getPatientId());
		return dueDate;
	}
}
