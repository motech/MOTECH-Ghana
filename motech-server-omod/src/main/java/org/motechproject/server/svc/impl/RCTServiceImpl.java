package org.motechproject.server.svc.impl;

import org.motechproject.server.model.Facility;
import org.motechproject.server.model.db.RctDAO;
import org.motechproject.server.model.rct.PhoneOwnershipType;
import org.motechproject.server.model.rct.RCTPatient;
import org.motechproject.server.model.rct.Stratum;
import org.motechproject.server.svc.RCTService;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.Patient;
import org.motechproject.ws.rct.ControlGroup;
import org.motechproject.ws.rct.PregnancyTrimester;
import org.motechproject.ws.rct.RCTRegistrationConfirmation;
import org.openmrs.User;

public class RCTServiceImpl implements RCTService {

    private RctDAO dao;

    public RCTRegistrationConfirmation register(Patient patient, User staff, Facility facility) {
        PregnancyTrimester trimester = patient.getPregnancyTrimester();
        ContactNumberType contactNumberType = patient.getContactNumberType();
        Stratum stratum = stratumFor(facility, PhoneOwnershipType.mapTo(contactNumberType), trimester);
        ControlGroup group = stratum.groupAssigned();
        enrollPatientForRCT(patient.getMotechId(),stratum, group, staff);
        determineNextAssignment(stratum);
        return new RCTRegistrationConfirmation(patient, group);
    }

    private void determineNextAssignment(Stratum stratum) {
        stratum.determineNextAssignment();
        dao.updateStratum(stratum);
    }

    private void enrollPatientForRCT(String motechId, Stratum stratum, ControlGroup controlGroup, User enrolledBy) {
        dao.saveRCTPatient(new RCTPatient(motechId,stratum,controlGroup,enrolledBy));
    }

    public Stratum stratumFor(Facility facility, PhoneOwnershipType phoneOwnershipType, PregnancyTrimester trimester) {
        return dao.stratumWith(facility,phoneOwnershipType,trimester);
    }

    public void setDao(RctDAO dao) {
        this.dao = dao;
    }
}
