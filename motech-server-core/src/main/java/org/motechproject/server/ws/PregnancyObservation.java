package org.motechproject.server.ws;

import org.motechproject.server.annotation.RunWithPrivileges;
import org.openmrs.Obs;
import org.openmrs.util.OpenmrsConstants;

import java.util.Date;

public interface PregnancyObservation {

    @RunWithPrivileges({OpenmrsConstants.PRIV_VIEW_CONCEPTS,
            OpenmrsConstants.PRIV_VIEW_OBS, OpenmrsConstants.PRIV_VIEW_PERSONS})
    public Date getActivePregnancyDueDate(Integer patientId);

    public Obs getActivePregnancy(Integer patientId);

    public Obs getActivePregnancyDueDateObs(Integer patientId, Obs pregnancy);

}

