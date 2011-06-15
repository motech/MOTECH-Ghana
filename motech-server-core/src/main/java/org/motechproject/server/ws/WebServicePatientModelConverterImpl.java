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

package org.motechproject.server.ws;

import org.motechproject.server.model.ExpectedEncounter;
import org.motechproject.server.model.ExpectedObs;
import org.motechproject.server.model.PatientContactUpdates;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.ws.Care;
import org.motechproject.ws.Patient;
import org.openmrs.Encounter;
import org.openmrs.Obs;

import java.util.ArrayList;
import java.util.List;

public class WebServicePatientModelConverterImpl extends AbstractWebServiceModelConverter implements WebServicePatientModelConverter {

	RegistrarBean registrarBean;

	public void setRegistrarBean(RegistrarBean registrarBean) {
		this.registrarBean = registrarBean;
	}

    public Patient[] deliveriesToWebServicePatients(List<Encounter> deliveries) {

		List<Patient> wsPatients = new ArrayList<Patient>();

		for (Encounter deliveryEncounter : deliveries) {
			org.openmrs.Patient patient = deliveryEncounter.getPatient();
			Patient wsPatient = patientToWebService(patient, true);
			wsPatient.setDeliveryDate(deliveryEncounter.getEncounterDatetime());
			wsPatients.add(wsPatient);
		}

		return wsPatients.toArray(new Patient[wsPatients.size()]);
	}

	public Patient[] dueDatesToWebServicePatients(List<Obs> dueDates) {

		List<Patient> wsPatients = new ArrayList<Patient>();

		for (Obs dueDate : dueDates) {
			Integer patientId = dueDate.getPersonId();
			org.openmrs.Patient patient = registrarBean.getPatientById(patientId);
			if (patient != null) {
				Patient wsPatient = patientToWebService(patient, true);
				wsPatient.setEstimateDueDate(dueDate.getValueDatetime());
				wsPatients.add(wsPatient);
			}
		}

		return wsPatients.toArray(new Patient[wsPatients.size()]);
	}


	public Patient upcomingObsToWebServicePatient(ExpectedObs upcomingObs) {

		org.openmrs.Patient patient = upcomingObs.getPatient();
		Patient wsPatient = patientToWebService(patient, true);

		Care care = new Care();
		care.setName(upcomingObs.getName());
		care.setDate(upcomingObs.getDueObsDatetime());
		wsPatient.setCares(new Care[] { care });

		return wsPatient;
	}

	public Patient upcomingEncounterToWebServicePatient(
			ExpectedEncounter upcomingEncounter) {

		org.openmrs.Patient patient = upcomingEncounter.getPatient();
		Patient wsPatient = patientToWebService(patient, true);

		Care care = new Care();
		care.setName(upcomingEncounter.getName());
		care.setDate(upcomingEncounter.getDueEncounterDatetime());
		wsPatient.setCares(new Care[] { care });

		return wsPatient;
	}

    public Patient patientToWebService(org.openmrs.Patient patient, boolean minimal, PatientContactUpdates patientContactUpdates) {
        Patient wsPatient = patientToWebService(patient, minimal);
        wsPatient.setContactNumberType(patientContactUpdates.contactNumberType());
        wsPatient.setPhoneNumber(patientContactUpdates.phoneNumber());
        return wsPatient;
    }
}
