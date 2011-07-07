package org.motechproject.server.model;

import org.apache.commons.lang.StringUtils;
import org.motechproject.server.model.ghana.Community;
import org.motechproject.server.model.ghana.Facility;
import org.openmrs.Patient;
import org.openmrs.PersonName;

import java.util.Set;

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

    public PatientEditor editName(PersonName editedName) {
        Set<PersonName> personNames = patient.getNames();
        if (personNames.isEmpty()) {
            patient.addName(editedName);
            return this;
        }
        for (PersonName currentName : personNames) {

            if (currentName.isVoided()) continue;
            if (currentName.isPreferred()) continue;
            if (editedName.isPreferred()) return editPreferredName(editedName);

            updateName(editedName, currentName);
        }
        return this;
    }

    public PatientEditor editPreferredName(PersonName preferredName) {
        Boolean removePreferredName = StringUtils.isBlank(preferredName.getGivenName());
        Set<PersonName> personNames = patient.getNames();
        for (PersonName currentName : personNames) {
            if (currentName.isVoided()) continue;
            if (!currentName.isPreferred()) continue;

            if (removePreferredName) {
                patient.removeName(currentName);
                return this;
            }
            updateName(preferredName, currentName);
            return this;
        }
        preferredName.setPreferred(true);
        patient.addName(preferredName);
        return this;
    }

    private void updateName(PersonName fromName, PersonName toName) {
        toName.setGivenName(fromName.getGivenName());
        toName.setMiddleName(fromName.getMiddleName());
        toName.setFamilyName(fromName.getFamilyName());
    }
}
