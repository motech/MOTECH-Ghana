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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.service.ExpectedCareEvent;
import org.motechproject.server.service.ExpectedCareSchedule;
import org.motechproject.server.service.Requirement;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.server.time.TimePeriod;
import org.openmrs.Patient;

public class ExpectedCareScheduleImpl implements ExpectedCareSchedule {

	private static Log log = LogFactory.getLog(ExpectedCareScheduleImpl.class);

	protected String name;

	protected Integer lateValue;
	protected TimePeriod latePeriod;

	protected List<ExpectedCareEvent> events = new ArrayList<ExpectedCareEvent>();
	protected List<Requirement> requirements = new ArrayList<Requirement>();

	protected RegistrarBean registrarBean;

	public void updateSchedule(Patient patient, Date date) {
		log.debug("Evaluating schedule: " + name + ", patient: "
				+ patient.getPatientId());

		if (meetsRequirements(patient, date)) {
			performScheduleUpdate(patient, date);
		} else {
			log
					.debug("Failed to meet requisites, removing events for schedule");

			removeExpectedCare(patient);
		}
	}

	public boolean meetsRequirements(Patient patient, Date date) {
		for (Requirement requirement : requirements) {
			if (!requirement.meetsRequirement(patient, date)) {
				return false;
			}
		}
		return true;
	}

	protected void performScheduleUpdate(Patient patient, Date date) {
	}

	protected void removeExpectedCare(Patient patient) {
	}

	protected Date getReferenceDate(Patient patient) {
		return patient.getBirthdate();
	}

	protected boolean validReferenceDate(Date referenceDate, Date currentDate) {
		return referenceDate != null;
	}

	protected Date getMinDate(Date date, ExpectedCareEvent event) {
		return calculateDate(date, event.getMinValue(), event.getMinPeriod());
	}

	protected Date getDueDate(Date date, ExpectedCareEvent event) {
		return calculateDate(date, event.getDueValue(), event.getDuePeriod());
	}

	protected Date getLateDate(Date date, ExpectedCareEvent event) {
		if (event.getLateValue() != null && event.getLatePeriod() != null) {
			return calculateDate(date, event.getLateValue(), event
					.getLatePeriod());
		} else {
			return calculateDate(date, lateValue, latePeriod);
		}
	}

	protected Date getMaxDate(Date date, ExpectedCareEvent event) {
		return calculateDate(date, event.getMaxValue(), event.getMaxPeriod());
	}

	protected Date calculateDate(Date date, Integer value, TimePeriod period) {
		if (date == null || value == null || period == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		switch (period) {
		case minute:
			calendar.add(Calendar.MINUTE, value);
			break;
		case hour:
			calendar.add(Calendar.HOUR, value);
			break;
		case day:
			calendar.add(Calendar.DATE, value);
			break;
		case week:
			// Add weeks as days
			calendar.add(Calendar.DATE, value * 7);
			break;
		case month:
			calendar.add(Calendar.MONTH, value);
			break;
		case year:
			calendar.add(Calendar.YEAR, value);
			break;
		}
		return calendar.getTime();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getLateValue() {
		return lateValue;
	}

	public void setLateValue(Integer lateValue) {
		this.lateValue = lateValue;
	}

	public TimePeriod getLatePeriod() {
		return latePeriod;
	}

	public void setLatePeriod(TimePeriod latePeriod) {
		this.latePeriod = latePeriod;
	}

	public List<ExpectedCareEvent> getEvents() {
		return events;
	}

	public void setEvents(List<ExpectedCareEvent> events) {
		this.events = events;
	}

	public List<Requirement> getRequirements() {
		return requirements;
	}

	public void setRequirements(List<Requirement> requirements) {
		this.requirements = requirements;
	}

	public RegistrarBean getRegistrarBean() {
		return registrarBean;
	}

	public void setRegistrarBean(RegistrarBean registrarBean) {
		this.registrarBean = registrarBean;
	}
}
