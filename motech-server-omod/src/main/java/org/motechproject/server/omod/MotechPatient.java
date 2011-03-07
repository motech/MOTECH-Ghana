package org.motechproject.server.omod;

import org.motechproject.server.util.MotechConstants;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;

public class MotechPatient {

    private Patient patient;

    public MotechPatient(Patient patient){
        patient = patient;
    }

    public String getMotechId(){
        PatientIdentifier patientId = patient
                .getPatientIdentifier(MotechConstants.PATIENT_IDENTIFIER_MOTECH_ID);
        if (patientId != null) {
            return patientId.getIdentifier();
        }
        return null;
    }
}
