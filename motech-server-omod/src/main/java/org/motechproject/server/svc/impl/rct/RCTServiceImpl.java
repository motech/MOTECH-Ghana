package org.motechproject.server.svc.impl.rct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.model.ExpectedEncounter;
import org.motechproject.server.model.ExpectedObs;
import org.motechproject.server.model.Facility;
import org.motechproject.server.model.db.RctDAO;
import org.motechproject.server.model.rct.*;
import org.motechproject.server.omod.MotechPatient;
import org.motechproject.server.service.ContextService;
import org.motechproject.server.svc.RCTService;
import org.motechproject.server.util.RCTError;
import org.motechproject.ws.Patient;
import org.motechproject.ws.rct.ControlGroup;
import org.motechproject.ws.rct.PregnancyTrimester;
import org.motechproject.ws.rct.RCTRegistrationConfirmation;
import org.openmrs.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

public class RCTServiceImpl implements RCTService {

    private static Log log = LogFactory.getLog(RCTServiceImpl.class);

    private RctDAO dao;

    @Autowired
    private ContextService contextService;

    @Transactional
    public RCTRegistrationConfirmation register(Patient patient, User staff, RCTFacility facility) {

        log.info("Starting RCT Registration for patient : " + patient.getMotechId() + " at facility Id : " + facility.getFacilityId() + " by staff : " + staff.getSystemId());

        if (!patient.isPregnancyRegistered())
            return failedRegistration(RCTError.PREGNANCY_NOT_REGISTERED);

        if (patient.isInFirstTrimesterOfPregnancy())
            return failedRegistration(RCTError.FIRST_TRIMESTER_PREGNANCY);

        Stratum stratum = stratumWith(facility, PhoneOwnershipType.mapTo(patient.getContactNumberType()), patient.pregnancyTrimester());
        if (stratum != null) {
            return assignControlGroup(patient, staff, stratum);
        }
        return failedRegistration(RCTError.RCT_STRATUM_NOT_FOUND);
    }

    private RCTRegistrationConfirmation assignControlGroup(Patient patient, User staff, Stratum stratum) {
        ControlGroup group = stratum.groupAssigned();
        if (group.isAssignable()) {
            return successfulRegistration(patient, staff, stratum, group);
        }
        return failedRegistration(RCTError.RCT_CONTROL_GROUP_NOT_FOUND);
    }

    private RCTRegistrationConfirmation successfulRegistration(Patient patient, User staff, Stratum stratum, ControlGroup group) {
        enrollPatientForRCT(patient.getMotechId(), stratum, group, staff);
        determineNextAssignment(stratum);
        return new RCTRegistrationConfirmation(new ConfirmationMessageContent(patient, group).text(), false);
    }

    private RCTRegistrationConfirmation failedRegistration(String error) {
        return new RCTRegistrationConfirmation(error, true);
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
    public Boolean isPatientRegisteredAndInTreatmentGroup(org.openmrs.Patient patient) {
        RCTPatient rctPatient = getRCTPatient(Integer.valueOf(new MotechPatient(patient).getMotechId()));
        if (rctPatient == null) {
            return false;
        }
        return rctPatient.isTreatment();
    }

    @Transactional(readOnly = true)
    public List<RCTPatient> getAllRCTPatients() {
        return dao.getAllRCTPatients();
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

    public boolean meetsFilteringCriteria(org.openmrs.Patient patient) {
        if (patient == null) return true;
        if (isPatientRegisteredAndInTreatmentGroup(patient)) return false;
        return isFromUpperEast(patient) && (patient.getId()) > 5717;
    }

     private Boolean isFromUpperEast(org.openmrs.Patient patient) {
        Facility facility = getFacilityByPatient(patient);
        return facility != null && facility.isInRegion("Upper East");
    }

    private Facility getFacilityByPatient(org.openmrs.Patient patient) {
        return contextService.getMotechService().facilityFor(patient);
    }

    public List<ExpectedEncounter> filterRCTEncounters(List<ExpectedEncounter> allDefaulters) {
        List<ExpectedEncounter> toBeRemoved = new ArrayList<ExpectedEncounter>();
        for (ExpectedEncounter allDefaulter : allDefaulters) {
            ExpectedEncounter expectedEncounter = allDefaulter;
            if (meetsFilteringCriteria(expectedEncounter.getPatient())) {
                toBeRemoved.add(expectedEncounter);
            }
        }
        allDefaulters.removeAll(toBeRemoved);
        return allDefaulters;
    }

    public List<ExpectedObs> filterRCTObs(List<ExpectedObs> allDefaulters) {
        List<ExpectedObs> toBeRemoved = new ArrayList<ExpectedObs>();
        for (ExpectedObs allDefaulter : allDefaulters) {
            ExpectedObs expectedObs = allDefaulter;
            if (meetsFilteringCriteria(expectedObs.getPatient())) {
                toBeRemoved.add(expectedObs);
            }
        }
        allDefaulters.removeAll(toBeRemoved);
        return allDefaulters;
    }
}
