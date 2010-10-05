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

package org.motechproject.server.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.model.ExpectedObs;
import org.motechproject.server.service.ExpectedCareEvent;
import org.openmrs.Obs;
import org.openmrs.Patient;

public class ExpectedObsSchedule extends ExpectedCareScheduleImpl {

	private static Log log = LogFactory.getLog(ExpectedObsSchedule.class);

	protected String conceptName;
	protected String valueConceptName;

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

		List<Obs> obsList = registrarBean.getObs(patient, conceptName,
				valueConceptName, referenceDate);
		List<ExpectedObs> expectedObsList = registrarBean.getExpectedObs(
				patient, name);

		Date previousEventObsDate = null;
		Integer largestDoseValue = getLargestDoseValue(obsList);

		ObsPredicate obsPredicate = new ObsPredicate();
		ExpectedObsPredicate expectedObsPredicate = new ExpectedObsPredicate();

		for (ExpectedCareEvent event : events) {
			// Calculate dates for event
			Date minDate = getMinDate(referenceDate, event);
			Date dueDate = null;
			// Use previous event's satisfying obs date as reference if
			// specified, clear previous obs date after
			if (Boolean.TRUE.equals(event.getDueReferencePrevious())) {
				dueDate = getDueDate(previousEventObsDate, event);
				previousEventObsDate = null;
			} else {
				dueDate = getDueDate(referenceDate, event);
			}
			// Set due date to min date if calculated due date is before min
			// date
			if (dueDate != null && minDate != null && dueDate.before(minDate)) {
				dueDate = minDate;
			}
			Date lateDate = getLateDate(dueDate, event);
			Date maxDate = getMaxDate(referenceDate, event);

			// Find Obs satisfying event
			obsPredicate.setMinDate(minDate);
			obsPredicate.setMaxDate(maxDate);
			obsPredicate.setValue(event.getNumber());
			Obs eventObs = getEventObs(obsList, obsPredicate);
			// Store satisfying Obs date for possible reference in next event
			if (eventObs != null) {
				previousEventObsDate = eventObs.getObsDatetime();
			}

			// Find ExpectedObs previously created for event
			expectedObsPredicate.setName(event.getName());
			ExpectedObs expectedObs = getEventExpectedObs(expectedObsList,
					expectedObsPredicate);

			boolean eventDoseBelowLargest = false;
			if (largestDoseValue != null && event.getNumber() != null) {
				eventDoseBelowLargest = event.getNumber() <= largestDoseValue;
			}

			boolean eventExpired = maxDate != null && date.after(maxDate);

			if (expectedObs != null) {
				if (eventObs != null) {
					// Remove existing satisfied ExpectedObs
					expectedObs.setObs(eventObs);
					expectedObs.setVoided(true);
					registrarBean.saveExpectedObs(expectedObs);
				} else {
					// Update existing ExpectedObs, removing if expired or lower
					// dose than current patient dose
					expectedObs.setMinObsDatetime(minDate);
					if (dueDate != null) {
						expectedObs.setDueObsDatetime(dueDate);
					}
					if (lateDate != null) {
						expectedObs.setLateObsDatetime(lateDate);
					}
					expectedObs.setMaxObsDatetime(maxDate);
					if (eventExpired || eventDoseBelowLargest) {
						expectedObs.setVoided(true);
					}
					registrarBean.saveExpectedObs(expectedObs);
				}
			} else if (!eventExpired && eventObs == null && dueDate != null
					&& lateDate != null && !eventDoseBelowLargest) {
				// Create new ExpectedObs if not expired, not satisfied, due
				// date and late date are defined, and
				// dose not lower than patient current dose
				registrarBean.createExpectedObs(patient, conceptName,
						valueConceptName, event.getNumber(), minDate, dueDate,
						lateDate, maxDate, event.getName(), name);
			}
		}
		// Remove any remaining Expected Obs not already updated
		removeExpectedObs(expectedObsList);
	}

	protected Integer getLargestDoseValue(List<Obs> obsList) {
		Double largestDoseValue = null;
		for (Obs obs : obsList) {
			Double obsValue = obs.getValueNumeric();
			if (obsValue != null) {
				if (largestDoseValue == null || obsValue > largestDoseValue) {
					largestDoseValue = obsValue;
				}
			}
		}
		if (largestDoseValue != null) {
			return largestDoseValue.intValue();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	protected Obs getEventObs(List<Obs> obsList, ObsPredicate obsPredicate) {
		List<Obs> eventObs = (List<Obs>) CollectionUtils.select(obsList,
				obsPredicate);
		if (!eventObs.isEmpty()) {
			if (eventObs.size() > 1) {
				log.debug("Multiple matches for delivered care : "
						+ eventObs.size());
			}
			// List is descending by date, remove match last in list
			Obs obs = eventObs.get(eventObs.size() - 1);
			obsList.remove(obs);
			return obs;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	protected ExpectedObs getEventExpectedObs(
			List<ExpectedObs> expectedObsList,
			ExpectedObsPredicate expectedObsPredicate) {
		List<ExpectedObs> eventExpectedObs = (List<ExpectedObs>) CollectionUtils
				.select(expectedObsList, expectedObsPredicate);
		if (!eventExpectedObs.isEmpty()) {
			if (eventExpectedObs.size() > 1) {
				log.debug("Multiple matches for expected care : "
						+ eventExpectedObs.size());
			}
			// List is ascending by due date, remove first match
			ExpectedObs expectedObs = eventExpectedObs.get(0);
			expectedObsList.remove(expectedObs);
			return expectedObs;
		}
		return null;
	}

	@Override
	protected void removeExpectedCare(Patient patient) {
		List<ExpectedObs> expectedObsList = registrarBean.getExpectedObs(
				patient, name);
		removeExpectedObs(expectedObsList);
	}

	protected void removeExpectedObs(List<ExpectedObs> expectedObsList) {
		for (ExpectedObs expectedObs : expectedObsList) {
			expectedObs.setVoided(true);
			registrarBean.saveExpectedObs(expectedObs);
		}
	}

	public String getConceptName() {
		return conceptName;
	}

	public void setConceptName(String conceptName) {
		this.conceptName = conceptName;
	}

	public String getValueConceptName() {
		return valueConceptName;
	}

	public void setValueConceptName(String valueConceptName) {
		this.valueConceptName = valueConceptName;
	}

}
