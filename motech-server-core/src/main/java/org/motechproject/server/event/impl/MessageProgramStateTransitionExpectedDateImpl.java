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

package org.motechproject.server.event.impl;

import java.util.Calendar;
import java.util.Date;

import org.motechproject.server.model.MessageProgramEnrollment;
import org.motechproject.server.time.TimePeriod;

public class MessageProgramStateTransitionExpectedDateImpl extends
		MessageProgramStateTransitionImpl {

	private Integer timeValue;
	private TimePeriod timePeriod;

	@Override
	public boolean evaluate(MessageProgramEnrollment enrollment,
			Date currentDate) {
		Date actionDate;
		if (nextState.equals(prevState)) {
			actionDate = nextState.getDateOfAction(enrollment, currentDate);
			if (actionDate == null) {
				return false;
			}
			return currentDate.before(actionDate)
					|| currentDate.equals(actionDate);
		} else if (timeValue != null && timePeriod != null) {
			actionDate = nextState.getDateOfAction(enrollment, currentDate);
			if (timeValue != null && timePeriod != null) {
				actionDate = calculateDate(actionDate, timeValue, timePeriod);
			}
			if (actionDate == null) {
				return false;
			}
			return currentDate.after(actionDate);
		} else {
			actionDate = prevState.getDateOfAction(enrollment, currentDate);
			if (actionDate == null) {
				return false;
			}
			return currentDate.after(actionDate);
		}
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

	public Integer getTimeValue() {
		return timeValue;
	}

	public void setTimeValue(Integer timeValue) {
		this.timeValue = timeValue;
	}

	public TimePeriod getTimePeriod() {
		return timePeriod;
	}

	public void setTimePeriod(TimePeriod timePeriod) {
		this.timePeriod = timePeriod;
	}

}