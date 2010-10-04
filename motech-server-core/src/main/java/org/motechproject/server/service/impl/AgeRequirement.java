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

import java.util.Calendar;
import java.util.Date;

import org.motechproject.server.service.Requirement;
import org.motechproject.server.time.TimePeriod;
import org.openmrs.Patient;

public class AgeRequirement implements Requirement {

	private Integer minValue;
	private TimePeriod minPeriod;

	private Integer maxValue;
	private TimePeriod maxPeriod;

	public boolean meetsRequirement(Patient patient, Date date) {

		if (minValue != null && minPeriod != null) {
			Date minAgeDate = calculateDate(patient.getBirthdate(), minValue,
					minPeriod);
			if (!date.after(minAgeDate)) {
				return false;
			}
		}
		if (maxValue != null && maxPeriod != null) {
			Date maxAgeDate = calculateDate(patient.getBirthdate(), maxValue,
					maxPeriod);
			if (!date.before(maxAgeDate)) {
				return false;
			}
		}
		return true;
	}

	private Date calculateDate(Date birthDate, Integer value, TimePeriod period) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(birthDate);

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

	public Integer getMinValue() {
		return minValue;
	}

	public void setMinValue(Integer minValue) {
		this.minValue = minValue;
	}

	public TimePeriod getMinPeriod() {
		return minPeriod;
	}

	public void setMinPeriod(TimePeriod minPeriod) {
		this.minPeriod = minPeriod;
	}

	public Integer getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Integer maxValue) {
		this.maxValue = maxValue;
	}

	public TimePeriod getMaxPeriod() {
		return maxPeriod;
	}

	public void setMaxPeriod(TimePeriod maxPeriod) {
		this.maxPeriod = maxPeriod;
	}

}
