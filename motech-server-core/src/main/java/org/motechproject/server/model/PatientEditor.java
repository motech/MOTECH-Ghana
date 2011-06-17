package org.motechproject.server.model;

import org.motechproject.server.model.ghana.Community;
import org.motechproject.server.model.ghana.Facility;
import org.openmrs.Patient;

public class PatientEditor {

    private Patient patient;

    public PatientEditor(Patient patient) {
        this.patient = patient;
    }

    public PatientEditor removeFrom(Facility oldFacility) {
        oldFacility.remove(patient);
        return this;
    }

    public PatientEditor addTo(Facility newFacility) {
        newFacility.addPatient(patient);
        return this;
    }

    public PatientEditor removeFrom(Community oldCommunity) {
        if (oldCommunity != null) {
            oldCommunity.remove(patient);
        }
        return this;
    }

    public PatientEditor addTo(Community newCommunity) {
        if (newCommunity != null) {
            newCommunity.add(patient);
        }
        return this;
    }

    public Patient done() {
        return patient;
    }
}
