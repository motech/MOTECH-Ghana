package org.motechproject.server.service.impl;

import org.motechproject.server.service.Requirement;
import org.motechproject.server.svc.RegistrarBean;
import org.openmrs.Patient;

import java.util.Date;

public class RegistrationRequirement implements Requirement {
    private RegistrarBean registrarBean;

    public boolean meetsRequirement(Patient patient, Date date) {
        Date childRegistrationDate = registrarBean.getChildRegistrationDate();
        return childRegistrationDate.before(patient.getDateCreated());
    }

    public RegistrarBean getRegistrarBean() {
        return registrarBean;
    }

    public void setRegistrarBean(RegistrarBean registrarBean) {
        this.registrarBean = registrarBean;
    }
}
