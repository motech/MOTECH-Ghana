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

package org.motechproject.server.omod.web.dwr;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.impl.ContextServiceImpl;
import org.motechproject.server.omod.web.model.WebModelConverter;
import org.motechproject.server.omod.web.model.WebModelConverterImpl;
import org.motechproject.server.omod.web.model.WebPatient;
import org.openmrs.Patient;

public class DWRMotechService {

	private static Log log = LogFactory.getLog(DWRMotechService.class);

	private ContextService contextService;
	private WebModelConverter webModelConverter;

	public DWRMotechService() {
		contextService = new ContextServiceImpl();
		WebModelConverterImpl webModelConverterImpl = new WebModelConverterImpl();
		webModelConverterImpl.setRegistrarBean(contextService
				.getRegistrarBean());
		webModelConverter = webModelConverterImpl;
	}

	public void setContextService(ContextService contextService) {
		this.contextService = contextService;
	}

	public void setWebModelConverter(WebModelConverter webModelConverter) {
		this.webModelConverter = webModelConverter;
	}

	public List<WebPatient> findMatchingPatients(String firstName,
			String lastName, String prefName, String birthDate,
			String communityId, String phoneNumber, String nhisNumber,
			String motechId) {

		if (log.isDebugEnabled()) {
			log.debug("Get Matching Patients: " + firstName + ", " + lastName
					+ ", " + prefName + ", " + birthDate + ", " + communityId
					+ ", " + phoneNumber + ", " + nhisNumber + ", " + motechId);
		}

		List<WebPatient> resultList = new ArrayList<WebPatient>();

		String datePattern = "dd/MM/yyyy";
		SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
		dateFormat.setLenient(false);

		Date parsedBirthDate = null;
		try {
			parsedBirthDate = dateFormat.parse(birthDate);
		} catch (ParseException e) {
		}

		Integer parsedCommunityId = null;
		try {
			parsedCommunityId = Integer.parseInt(communityId);
		} catch (NumberFormatException e) {
		}

		List<Patient> matchingPatients = contextService.getRegistrarBean()
				.getDuplicatePatients(firstName, lastName, prefName,
						parsedBirthDate, parsedCommunityId, phoneNumber,
						nhisNumber, motechId);

		for (Patient patient : matchingPatients) {
			WebPatient webPatient = new WebPatient();
			webModelConverter.patientToWeb(patient, webPatient);
			resultList.add(webPatient);
		}
		return resultList;
	}

}
