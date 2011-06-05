package org.motechproject.server.omod.filters;

import org.motechproject.server.model.ExpectedEvent;
import org.motechproject.server.model.Facility;
import org.motechproject.server.svc.RCTService;
import org.motechproject.server.svc.RegistrarBean;
import org.openmrs.Patient;

import java.util.ArrayList;
import java.util.List;

public class ExpectedPatientEventFilterForRCT implements Filter<ExpectedEvent> {
    private RCTService rctService;
    private RegistrarBean registrarBean;

    public List<ExpectedEvent> filter(List<ExpectedEvent> expectedEncounters) {
        List<ExpectedEvent> toBeRemoved = new ArrayList<ExpectedEvent>();
        for (ExpectedEvent expectedEvent : expectedEncounters) {
            Patient patient = expectedEvent.getPatient();
            if (patient.getId() > 5717) {
                if (patientFromUpperEast(patient) && patientNotInTreatmentGroup(patient)) {
                    toBeRemoved.add(expectedEvent);
                }
            }

        }
        expectedEncounters.removeAll(toBeRemoved);
        return expectedEncounters;
    }


    private Boolean patientFromUpperEast(Patient patient) {
        Facility facility = registrarBean.getFacilityByPatient(patient);
        return facility.isInRegion("Upper East");
    }

    private Boolean patientNotInTreatmentGroup(Patient patient) {
        return !rctService.isPatientRegisteredAndInTreatmentGroup(patient);
    }

    public void setRctService(RCTService rctService) {
        this.rctService = rctService;
    }

    public void setRegistrarBean(RegistrarBean registrarBean) {
        this.registrarBean = registrarBean;
    }

}
