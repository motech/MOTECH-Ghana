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

import org.motechproject.server.model.ExpectedEncounter;

public class ExpectedEncounterDatePredicate extends ExpectedEncounterPredicate {

	private Date minDate;
	private Date dueDate;
	private Date lateDate;
	private Date maxDate;

	@Override
	public boolean evaluate(Object input) {
		if (input instanceof ExpectedEncounter) {
			ExpectedEncounter expectedEncounter = (ExpectedEncounter) input;

			boolean minDateMatch = true;
			if (minDate != null) {
				minDateMatch = minDate.equals(expectedEncounter
						.getMinEncounterDatetime());
			}
			boolean dueDateMatch = true;
			if (dueDate != null) {
				dueDateMatch = dueDate.equals(expectedEncounter
						.getDueEncounterDatetime());
			}
			boolean lateDateMatch = true;
			if (lateDate != null) {
				lateDateMatch = lateDate.equals(expectedEncounter
						.getLateEncounterDatetime());
			}
			boolean maxDateMatch = true;
			if (maxDate != null) {
				maxDateMatch = maxDate.equals(expectedEncounter
						.getMaxEncounterDatetime());
			}

			return minDateMatch && dueDateMatch && lateDateMatch
					&& maxDateMatch;
		}
		return false;
	}

	public Date getMinDate() {
		return minDate;
	}

	public void setMinDate(Date minDate) {
		this.minDate = minDate;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Date getLateDate() {
		return lateDate;
	}

	public void setLateDate(Date lateDate) {
		this.lateDate = lateDate;
	}

	public Date getMaxDate() {
		return maxDate;
	}

	public void setMaxDate(Date maxDate) {
		this.maxDate = maxDate;
	}

}
