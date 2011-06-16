package org.motechproject.server.filters.condition;

import org.motechproject.server.model.Facility;
import org.motechproject.server.service.ContextService;
import org.motechproject.server.svc.RCTService;
import org.openmrs.Patient;

public class RCTCondition implements Condition<Patient> {

    private RCTService rctService;
    private ContextService contextService;


    public Boolean metBy(Patient patient) {

        return (patient.getId() > 5717)
                && patientFromUpperEast(patient)
                && patientNotInTreatmentGroup(patient);
    }

    private Boolean patientFromUpperEast(Patient patient) {
        Facility facility = contextService.getMotechService().facilityFor(patient);
        return facility.isInRegion("Upper East");
    }

    private Boolean patientNotInTreatmentGroup(Patient patient) {
        return !rctService.isPatientRegisteredAndInTreatmentGroup(patient);
    }

    public void setRctService(RCTService rctService) {
        this.rctService = rctService;
    }

    public void setContextService(ContextService contextService) {
        this.contextService = contextService;
    }
}
