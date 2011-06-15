package org.motechproject.server.ws;

import org.motechproject.server.model.Community;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.MotechService;
import org.motechproject.server.util.GenderTypeConverter;
import org.motechproject.server.util.MotechConstants;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonName;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * User: imransm, Date: 14 Jun, 2011, Time: 2:52:13 PM
 * Problem:
 */
public class AbstractWebServiceModelConverter {

    @Autowired
    private ContextService contextService;

    @Autowired
    private PregnancyObservation pregnancyObservation;


    public Patient patientToWebService(org.openmrs.Patient patient, boolean minimal) {

        if (patient == null) {
            return null;
        }

        Patient webServicePatient = new Patient();

        webServicePatient.setPreferredName(patient.getGivenName());
        webServicePatient.setLastName(patient.getFamilyName());
        webServicePatient.setBirthDate(patient.getBirthdate());
        webServicePatient.setSex(GenderTypeConverter.valueOfOpenMRS(patient.getGender()));

        Community community = motechService().getCommunityByPatient(patient);
        if (community != null) {
            webServicePatient.setCommunity(community.getName());
        }

        PatientIdentifier patientId = patient.getPatientIdentifier(MotechConstants.PATIENT_IDENTIFIER_MOTECH_ID);
        if (patientId != null) {
            webServicePatient.setMotechId(patientId.getIdentifier());
        }

        if (!minimal) {
            for (PersonName name : patient.getNames()) {
                if (!name.isPreferred() && name.getGivenName() != null) {
                    webServicePatient.setFirstName(name.getGivenName());
                    break;
                }
            }
            webServicePatient.setAge(patient.getAge());
            PersonAttribute phoneNumberAttr = patient.getAttribute(MotechConstants.PERSON_ATTRIBUTE_PHONE_NUMBER);
            if (phoneNumberAttr != null) {
                webServicePatient.setPhoneNumber(phoneNumberAttr.getValue());
            }
            PersonAttribute contactNumberType = patient.getAttribute(MotechConstants.PERSON_ATTRIBUTE_PHONE_TYPE);
            if (contactNumberType != null) {
                webServicePatient.setContactNumberType(ContactNumberType.valueOf(contactNumberType.getValue()));
            }
            webServicePatient.setEstimateDueDate(pregnancyObservation.getActivePregnancyDueDate(patient.getPatientId()));
        }
        return webServicePatient;
    }

    private MotechService motechService() {
        return contextService.getMotechService();
    }

    public void setContextService(ContextService contextService) {
        this.contextService = contextService;
    }


    public Patient[] patientToWebService(List<org.openmrs.Patient> patients, boolean minimal) {

		List<Patient> webServicePatients = new ArrayList<Patient>();
		for (org.openmrs.Patient patient : patients) {
			webServicePatients.add(patientToWebService(patient, minimal));
		}
		return webServicePatients.toArray(new Patient[webServicePatients.size()]);
	}
}
