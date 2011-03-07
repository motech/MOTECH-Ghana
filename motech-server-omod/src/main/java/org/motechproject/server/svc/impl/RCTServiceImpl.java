package org.motechproject.server.svc.impl;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.motechproject.server.model.db.RctDAO;
import org.motechproject.server.model.rct.PhoneOwnershipType;
import org.motechproject.server.model.rct.RCTFacility;
import org.motechproject.server.model.rct.RCTPatient;
import org.motechproject.server.model.rct.Stratum;
import org.motechproject.server.svc.RCTService;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.Patient;
import org.motechproject.ws.rct.ControlGroup;
import org.motechproject.ws.rct.PregnancyTrimester;
import org.motechproject.ws.rct.RCTRegistrationConfirmation;
import org.openmrs.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

public class RCTServiceImpl implements RCTService {

    private RctDAO dao;

    @Transactional
    public RCTRegistrationConfirmation register(Patient patient, User staff, RCTFacility facility, PregnancyTrimester pregnancyTrimester) {
        ContactNumberType contactNumberType = patient.getContactNumberType();
        Stratum stratum = stratumWith(facility, PhoneOwnershipType.mapTo(contactNumberType), pregnancyTrimester);
        ControlGroup group = stratum.groupAssigned();
        enrollPatientForRCT(patient.getMotechId(), stratum, group, staff);
        determineNextAssignment(stratum);
        return new RCTRegistrationConfirmation(patient, group);
    }

    @Transactional(readOnly = true)
    public boolean isPatientRegisteredIntoRCT(Integer motechId) {
        return dao.isPatientRegisteredIntoRCT(motechId);
    }

    @Transactional(readOnly = true)
    public RCTFacility getRCTFacilityById(Integer facilityId) {
        return dao.getRCTFacility(facilityId);
    }

    private void determineNextAssignment(Stratum stratum) {
        stratum.determineNextAssignment();
        dao.updateStratum(stratum);
    }

    private void enrollPatientForRCT(String motechId, Stratum stratum, ControlGroup controlGroup, User enrolledBy) {
        dao.saveRCTPatient(new RCTPatient(motechId, stratum, controlGroup, enrolledBy));
    }

    private Stratum stratumWith(RCTFacility facility, PhoneOwnershipType phoneOwnershipType, PregnancyTrimester trimester) {
        return dao.stratumWith(facility, phoneOwnershipType, trimester);
    }
    
    public void setDao(RctDAO dao) {
        this.dao = dao;
    }
}
