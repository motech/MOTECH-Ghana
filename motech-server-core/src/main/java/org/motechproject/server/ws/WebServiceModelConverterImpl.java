package org.motechproject.server.ws;

import java.util.ArrayList;
import java.util.List;

import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.server.util.GenderTypeConverter;
import org.motechproject.server.util.MotechConstants;
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
}
