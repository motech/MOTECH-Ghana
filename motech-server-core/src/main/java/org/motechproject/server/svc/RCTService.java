package org.motechproject.server.svc;

import org.motechproject.server.annotation.LogParameterIdentifiers;
import org.motechproject.server.model.rct.RCTFacility;
import org.motechproject.ws.Patient;
import org.motechproject.ws.rct.PregnancyTrimester;
import org.motechproject.ws.rct.RCTRegistrationConfirmation;
import org.openmrs.User;

public interface RCTService {

    @LogParameterIdentifiers
    RCTRegistrationConfirmation register(Patient patient, User user, RCTFacility facility, PregnancyTrimester pregnancyTrimester);

    boolean isPatientRegisteredIntoRCT(Integer motechId);

    RCTFacility getRCTFacilityById(Integer facilityId);
}
