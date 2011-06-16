package org.motechproject.server.svc;

import org.motechproject.server.annotation.LogParameterIdentifiers;
import org.motechproject.server.model.ExpectedEncounter;
import org.motechproject.server.model.ExpectedObs;
import org.motechproject.server.model.rct.RCTFacility;
import org.motechproject.server.model.rct.RCTPatient;
import org.motechproject.ws.Patient;
import org.motechproject.ws.rct.RCTRegistrationConfirmation;
import org.openmrs.User;

import java.util.List;

public interface RCTService {

    @LogParameterIdentifiers
    RCTRegistrationConfirmation register(Patient patient, User user, RCTFacility facility);

    Boolean isPatientRegisteredIntoRCT(Integer motechId);

    RCTFacility getRCTFacilityById(Integer facilityId);

    public RCTPatient getRCTPatient(Integer motechId);

    public Boolean isPatientRegisteredAndInTreatmentGroup(org.openmrs.Patient patient);

    public List<RCTPatient> getAllRCTPatients();

    boolean meetsFilteringCriteria(org.openmrs.Patient patient);

    List<ExpectedEncounter> filterRCTEncounters(List<ExpectedEncounter> allDefaulters);

    List<ExpectedObs> filterRCTObs(List<ExpectedObs> allDefaulters);
}
