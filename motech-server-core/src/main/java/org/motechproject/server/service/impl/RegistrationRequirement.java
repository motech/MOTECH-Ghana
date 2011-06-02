package org.motechproject.server.service.impl;

import org.motechproject.server.service.Requirement;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.server.util.MotechConstants;
import org.openmrs.Encounter;
import org.openmrs.Patient;

import java.util.Date;
import java.util.List;

public class RegistrationRequirement implements Requirement {
    private RegistrarBean registrarBean;

    public boolean meetsRequirement(Patient patient, Date date) {
        Date childRegistrationDate = registrarBean.getChildRegistrationDate();
        List<Encounter> encounters = registrarBean.getEncounters(patient, MotechConstants.ENCOUNTER_TYPE_PATIENTREGVISIT, patient.getBirthdate());
        if (encounters == null || encounters.isEmpty()) return false;
        return childRegistrationDate.before(encounters.get(0).getEncounterDatetime());

    }

    public RegistrarBean getRegistrarBean() {
        return registrarBean;
    }

    public void setRegistrarBean(RegistrarBean registrarBean) {
        this.registrarBean = registrarBean;
    }
}
