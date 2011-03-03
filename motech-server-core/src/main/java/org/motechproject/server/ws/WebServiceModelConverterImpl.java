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

import org.motechproject.server.model.Community;
import org.motechproject.server.model.ExpectedEncounter;
import org.motechproject.server.model.ExpectedObs;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.server.util.GenderTypeConverter;
import org.motechproject.server.util.MotechConstants;
import org.motechproject.ws.Care;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.Patient;
import org.openmrs.*;

import java.util.*;

public class WebServiceModelConverterImpl implements WebServiceModelConverter {

	RegistrarBean registrarBean;

	public void setRegistrarBean(RegistrarBean registrarBean) {
		this.registrarBean = registrarBean;
	}

	public Patient patientToWebService(org.openmrs.Patient patient,
			boolean minimal) {

		if (patient == null) {
			return null;
		}

		Patient wsPatient = new Patient();

		wsPatient.setPreferredName(patient.getGivenName());
		wsPatient.setLastName(patient.getFamilyName());
		wsPatient.setBirthDate(patient.getBirthdate());
		wsPatient.setSex(GenderTypeConverter
				.valueOfOpenMRS(patient.getGender()));

		Community community = registrarBean.getCommunityByPatient(patient);
		if (community != null) {
			wsPatient.setCommunity(community.getName());
		}

		PatientIdentifier patientId = patient
				.getPatientIdentifier(MotechConstants.PATIENT_IDENTIFIER_MOTECH_ID);
		if (patientId != null) {
			wsPatient.setMotechId(patientId.getIdentifier());
		}

		if (!minimal) {
			for (PersonName name : patient.getNames()) {
				if (!name.isPreferred() && name.getGivenName() != null) {
					wsPatient.setFirstName(name.getGivenName());
					break;
				}
			}

			wsPatient.setAge(patient.getAge());

			PersonAttribute phoneNumberAttr = patient
					.getAttribute(MotechConstants.PERSON_ATTRIBUTE_PHONE_NUMBER);
			if (phoneNumberAttr != null) {
				wsPatient.setPhoneNumber(phoneNumberAttr.getValue());
			}

            PersonAttribute contactNumberType = patient
					.getAttribute(MotechConstants.PERSON_ATTRIBUTE_PHONE_TYPE);
			if (phoneNumberAttr != null) {
				wsPatient.setContactNumberType(ContactNumberType.valueOf(contactNumberType.getValue()));
			}

			wsPatient.setEstimateDueDate(registrarBean
					.getActivePregnancyDueDate(patient.getPatientId()));
		}

		return wsPatient;
	}

	public Patient[] patientToWebService(List<org.openmrs.Patient> patients,
			boolean minimal) {

		List<Patient> wsPatients = new ArrayList<Patient>();

		for (org.openmrs.Patient patient : patients) {
			wsPatients.add(patientToWebService(patient, minimal));
		}

		return wsPatients.toArray(new Patient[wsPatients.size()]);
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
			org.openmrs.Patient patient = registrarBean
					.getPatientById(patientId);
			if (patient != null) {
				Patient wsPatient = patientToWebService(patient, true);
				wsPatient.setEstimateDueDate(dueDate.getValueDatetime());
				wsPatients.add(wsPatient);
			}
		}

		return wsPatients.toArray(new Patient[wsPatients.size()]);
	}

	public Care[] upcomingObsToWebServiceCares(List<ExpectedObs> upcomingObs) {

		List<Care> cares = new ArrayList<Care>();

		for (ExpectedObs expectedObs : upcomingObs) {
			Care care = new Care();
			care.setName(expectedObs.getName());
			care.setDate(expectedObs.getDueObsDatetime());
			cares.add(care);
		}

		return cares.toArray(new Care[cares.size()]);
	}

	public Care[] upcomingEncountersToWebServiceCares(
			List<ExpectedEncounter> upcomingEncounters) {

		List<Care> cares = new ArrayList<Care>();

		for (ExpectedEncounter expectedEncounter : upcomingEncounters) {
			Care care = new Care();
			care.setName(expectedEncounter.getName());
			care.setDate(expectedEncounter.getDueEncounterDatetime());
			cares.add(care);
		}

		return cares.toArray(new Care[cares.size()]);
	}

	public Care[] upcomingToWebServiceCares(
			List<ExpectedEncounter> upcomingEncounters,
			List<ExpectedObs> upcomingObs, boolean includePatient) {

		List<Care> cares = new ArrayList<Care>();

		for (ExpectedEncounter expectedEncounter : upcomingEncounters) {
			Care care = new Care();
			care.setName(expectedEncounter.getName());
			care.setDate(expectedEncounter.getDueEncounterDatetime());
			if (includePatient) {
				Patient patient = patientToWebService(expectedEncounter
						.getPatient(), true);
				care.setPatients(new Patient[] { patient });
			}
			cares.add(care);
		}
		for (ExpectedObs expectedObs : upcomingObs) {
			Care care = new Care();
			care.setName(expectedObs.getName());
			care.setDate(expectedObs.getDueObsDatetime());
			if (includePatient) {
				Patient patient = patientToWebService(expectedObs.getPatient(),
						true);
				care.setPatients(new Patient[] { patient });
			}
			cares.add(care);
		}

		Collections.sort(cares, new CareDateComparator());

		return cares.toArray(new Care[cares.size()]);
	}

	public Care[] defaultedObsToWebServiceCares(List<ExpectedObs> defaultedObs) {

		List<Care> cares = new ArrayList<Care>();

		Map<String, List<org.openmrs.Patient>> carePatientMap = new HashMap<String, List<org.openmrs.Patient>>();

		for (ExpectedObs expectedObs : defaultedObs) {
			List<org.openmrs.Patient> patients = carePatientMap.get(expectedObs
					.getName());
			if (patients == null) {
				patients = new ArrayList<org.openmrs.Patient>();
			}
			patients.add(expectedObs.getPatient());
			carePatientMap.put(expectedObs.getName(), patients);
		}
		for (Map.Entry<String, List<org.openmrs.Patient>> entry : carePatientMap
				.entrySet()) {
			Care care = new Care();
			care.setName(entry.getKey());
			Patient[] patients = patientToWebService(entry.getValue(), true);
			care.setPatients(patients);
			cares.add(care);
		}

		Collections.sort(cares, new CareDateComparator(true));

		return cares.toArray(new Care[cares.size()]);
	}

	public Care[] defaultedEncountersToWebServiceCares(
			List<ExpectedEncounter> defaultedEncounters) {

		List<Care> cares = new ArrayList<Care>();

		Map<String, List<org.openmrs.Patient>> carePatientMap = new HashMap<String, List<org.openmrs.Patient>>();

		for (ExpectedEncounter expectedEncounter : defaultedEncounters) {
			List<org.openmrs.Patient> patients = carePatientMap
					.get(expectedEncounter.getName());
			if (patients == null) {
				patients = new ArrayList<org.openmrs.Patient>();
			}
			patients.add(expectedEncounter.getPatient());
			carePatientMap.put(expectedEncounter.getName(), patients);
		}
		for (Map.Entry<String, List<org.openmrs.Patient>> entry : carePatientMap
				.entrySet()) {
			Care care = new Care();
			care.setName(entry.getKey());
			Patient[] patients = patientToWebService(entry.getValue(), true);
			care.setPatients(patients);
			cares.add(care);
		}

		Collections.sort(cares, new CareDateComparator(true));

		return cares.toArray(new Care[cares.size()]);
	}

	public Care[] defaultedToWebServiceCares(
			List<ExpectedEncounter> defaultedEncounters,
			List<ExpectedObs> defaultedObs) {

		List<Care> cares = new ArrayList<Care>();

		Map<String, List<org.openmrs.Patient>> carePatientMap = new HashMap<String, List<org.openmrs.Patient>>();

		for (ExpectedEncounter expectedEncounter : defaultedEncounters) {
			List<org.openmrs.Patient> patients = carePatientMap
					.get(expectedEncounter.getName());
			if (patients == null) {
				patients = new ArrayList<org.openmrs.Patient>();
			}
			patients.add(expectedEncounter.getPatient());
			carePatientMap.put(expectedEncounter.getName(), patients);
		}
		for (ExpectedObs expectedObs : defaultedObs) {
			List<org.openmrs.Patient> patients = carePatientMap.get(expectedObs
					.getName());
			if (patients == null) {
				patients = new ArrayList<org.openmrs.Patient>();
			}
			patients.add(expectedObs.getPatient());
			carePatientMap.put(expectedObs.getName(), patients);
		}
		for (Map.Entry<String, List<org.openmrs.Patient>> entry : carePatientMap
				.entrySet()) {
			Care care = new Care();
			care.setName(entry.getKey());
			Patient[] patients = patientToWebService(entry.getValue(), true);
			care.setPatients(patients);
			cares.add(care);
		}

		Collections.sort(cares, new CareDateComparator(true));

		return cares.toArray(new Care[cares.size()]);
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
}
