package org.motechproject.server.svc.impl;

import org.motechproject.server.model.db.RctDAO;
import org.motechproject.server.model.rct.PhoneOwnershipType;
import org.motechproject.server.model.rct.RCTFacility;
import org.motechproject.server.model.rct.RCTPatient;
import org.motechproject.server.model.rct.Stratum;
import org.motechproject.server.omod.MotechPatient;
import org.motechproject.server.svc.RCTService;
import org.motechproject.server.util.RCTError;
import org.motechproject.ws.Patient;
import org.motechproject.ws.rct.*;
import org.openmrs.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class RCTServiceImpl implements RCTService {

    private RctDAO dao;

    @Transactional
    public RCTRegistrationConfirmation register(Patient patient, User staff, RCTFacility facility) {

        if (!patient.isPregnancyRegistered())
            return failedRegistration(RCTError.PREGNANCY_NOT_REGISTERED);

        if (patient.isInFirstTrimesterOfPregnancy())
            return failedRegistration(RCTError.FIRST_TRIMESTER_PREGNANCY);

        Stratum stratum = stratumWith(facility, PhoneOwnershipType.mapTo(patient.getContactNumberType()), patient.pregnancyTrimester());
        if (stratum != null) {
            return successfulRegistration(patient, staff, stratum);
        }
        return failedRegistration(RCTError.RCT_STRATUM_NOT_FOUND);
    }

    private RCTRegistrationConfirmation failedRegistration(String error) {
        return new RCTRegistrationConfirmation(new ErrorContent(error));
    }

    private RCTRegistrationConfirmation successfulRegistration(Patient patient, User staff, Stratum stratum) {
        ControlGroup group = stratum.groupAssigned();
        enrollPatientForRCT(patient.getMotechId(), stratum, group, staff);
        determineNextAssignment(stratum);
        return new RCTRegistrationConfirmation(new MessageContent(patient, group));
    }

    @Transactional(readOnly = true)
    public Boolean isPatientRegisteredIntoRCT(Integer motechId) {
        return dao.isPatientRegisteredIntoRCT(motechId);
    }

    @Transactional(readOnly = true)
    public RCTFacility getRCTFacilityById(Integer facilityId) {
        return dao.getRCTFacility(facilityId);
    }

    @Transactional(readOnly = true)
    public RCTPatient getRCTPatient(Integer motechId) {
        return dao.getRCTPatient(motechId);
    }

    @Transactional(readOnly = true)
    public Boolean isPatientRegisteredAndInControlGroup(org.openmrs.Patient patient) {
        RCTPatient rctPatient = getRCTPatient(Integer.valueOf(new MotechPatient(patient).getMotechId()));
        if (rctPatient == null) {
            return false;
        }
        return rctPatient.isControl();
    }

    @Transactional(readOnly = true)
    public List<RCTPatient> getAllRCTPatients() {
        List<RCTPatient> rctPatients = dao.getAllRCTPatients();
        return rctPatients;
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
