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

package org.motechproject.server.svc;

import java.util.Date;
import java.util.List;

import org.motechproject.server.annotation.RunWithPrivileges;
import org.motechproject.server.model.ExpectedEncounter;
import org.motechproject.server.model.ExpectedObs;
import org.motechproject.server.model.Facility;
import org.openmrs.Patient;
import org.openmrs.util.OpenmrsConstants;

/**
 * The service interface for expected care in the motech server project.
 */
public interface ExpectedCareBean {

	public List<ExpectedEncounter> getUpcomingExpectedEncounters(Patient patient);

	public List<ExpectedObs> getUpcomingExpectedObs(Patient patient);

	public List<ExpectedEncounter> getDefaultedExpectedEncounters(
			Facility facility, String[] groups);

	public List<ExpectedObs> getDefaultedExpectedObs(Facility facility,
			String[] groups);

	public List<ExpectedEncounter> getUpcomingExpectedEncounters(
			Facility facility, String[] groups, Date fromDate, Date toDate);

	public List<ExpectedObs> getUpcomingExpectedObs(Facility facility,
			String[] groups, Date fromDate, Date toDate);

	public List<ExpectedEncounter> getDefaultedExpectedEncounters(
			Facility facility, String[] groups, Date forDate);

	public List<ExpectedObs> getDefaultedExpectedObs(Facility facility,
			String[] groups, Date forDate);

	public List<ExpectedEncounter> getExpectedEncounters(Patient patient);

	public List<ExpectedObs> getExpectedObs(Patient patient);

	public List<ExpectedEncounter> getExpectedEncounters(Patient patient,
			String group);

	public List<ExpectedObs> getExpectedObs(Patient patient, String group);

	public ExpectedObs createExpectedObs(Patient patient, String conceptName,
			String valueConceptName, Integer value, Date minDate, Date dueDate,
			Date lateDate, Date maxDate, String name, String group);

	public ExpectedObs saveExpectedObs(ExpectedObs expectedObs);

	public ExpectedEncounter createExpectedEncounter(Patient patient,
			String encounterTypeName, Date minDate, Date dueDate,
			Date lateDate, Date maxDate, String name, String group);

	public ExpectedEncounter saveExpectedEncounter(
			ExpectedEncounter expectedEncounter);

	@RunWithPrivileges( { OpenmrsConstants.PRIV_VIEW_PATIENTS,
			OpenmrsConstants.PRIV_VIEW_IDENTIFIER_TYPES,
			OpenmrsConstants.PRIV_VIEW_CONCEPTS,
			OpenmrsConstants.PRIV_VIEW_ENCOUNTER_TYPES,
			OpenmrsConstants.PRIV_VIEW_OBS,
			OpenmrsConstants.PRIV_VIEW_ENCOUNTERS,
			OpenmrsConstants.PRIV_VIEW_PERSONS,
			OpenmrsConstants.PRIV_VIEW_LOCATIONS,
			OpenmrsConstants.PRIV_VIEW_USERS,
			OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES,
			OpenmrsConstants.PRIV_EDIT_PATIENTS })
	public void updateAllCareSchedules();

}
