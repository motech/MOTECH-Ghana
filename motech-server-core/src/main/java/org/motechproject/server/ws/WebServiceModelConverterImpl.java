package org.motechproject.server.ws;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.motechproject.server.model.ExpectedEncounter;
import org.motechproject.server.model.ExpectedObs;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.server.util.GenderTypeConverter;
import org.motechproject.server.util.MotechConstants;
import org.motechproject.ws.Care;
import org.motechproject.ws.Patient;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonName;

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

		PersonAddress patientAddress = patient.getPersonAddress();
		if (patientAddress != null) {
			wsPatient.setCommunity(patientAddress.getCityVillage());
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

			PersonAttribute primaryPhoneAttr = patient
					.getAttribute(MotechConstants.PERSON_ATTRIBUTE_PRIMARY_PHONE_NUMBER);
			if (primaryPhoneAttr != null) {
				wsPatient.setPhoneNumber(primaryPhoneAttr.getValue());
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
			if (!patient.isVoided()) {
				Patient wsPatient = patientToWebService(patient, true);
				wsPatient.setDeliveryDate(deliveryEncounter
						.getEncounterDatetime());
				wsPatients.add(wsPatient);
			}
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

	public Care[] defaultedObsToWebServiceCares(List<ExpectedObs> defaultedObs) {

		List<Care> cares = new ArrayList<Care>();

		LinkedHashMap<String, List<org.openmrs.Patient>> carePatientMap = new LinkedHashMap<String, List<org.openmrs.Patient>>();

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

		return cares.toArray(new Care[cares.size()]);
	}

	public Care[] defaultedEncountersToWebServiceCares(
			List<ExpectedEncounter> defaultedEncounters) {

		List<Care> cares = new ArrayList<Care>();

		LinkedHashMap<String, List<org.openmrs.Patient>> carePatientMap = new LinkedHashMap<String, List<org.openmrs.Patient>>();

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

		return cares.toArray(new Care[cares.size()]);
	}

}
