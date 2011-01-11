package org.motechproject.server.omod;

import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientService;

public enum PatientIdentifierTypeEnum {

    PATIENT_IDENTIFIER_FACILITY_ID("MoTeCH Facility Id"),
    PATIENT_IDENTIFIER_COMMUNITY_ID("MoTeCH Community Id"),
    PATIENT_IDENTIFIER_MOTECH_ID("MoTeCH Id"),
    PATIENT_IDENTIFIER_STAFF_ID("MoTeCH Staff Id");

    private String identifierName;

    PatientIdentifierTypeEnum(String s) {
        this.identifierName = s;
    }

    public  PatientIdentifierType getIdentifierType(PatientService patientService){
        return patientService.getPatientIdentifierTypeByName(identifierName);
    }
}
