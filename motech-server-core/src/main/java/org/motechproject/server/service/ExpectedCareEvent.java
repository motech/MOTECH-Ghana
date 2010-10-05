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

package org.motechproject.server.service;

import org.motechproject.server.time.TimePeriod;

public class ExpectedCareEvent {

	protected String name;
	protected Integer number;

	protected Integer minValue;
	protected TimePeriod minPeriod;

	protected Integer dueValue;
	protected TimePeriod duePeriod;
	protected Boolean dueReferencePrevious;

	protected Integer lateValue;
	protected TimePeriod latePeriod;

	protected Integer maxValue;
	protected TimePeriod maxPeriod;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
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

	public Integer getDueValue() {
		return dueValue;
	}

	public void setDueValue(Integer dueValue) {
		this.dueValue = dueValue;
	}

	public TimePeriod getDuePeriod() {
		return duePeriod;
	}

	public void setDuePeriod(TimePeriod duePeriod) {
		this.duePeriod = duePeriod;
	}

	public Boolean getDueReferencePrevious() {
		return dueReferencePrevious;
	}

	public void setDueReferencePrevious(Boolean dueReferencePrevious) {
		this.dueReferencePrevious = dueReferencePrevious;
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
