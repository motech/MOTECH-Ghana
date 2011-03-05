package org.motechproject.server.svc;

import org.motechproject.server.model.Facility;
import org.motechproject.server.model.rct.RCTFacility;
import org.motechproject.ws.Patient;
import org.motechproject.ws.rct.RCTRegistrationConfirmation;
import org.openmrs.User;

public interface RCTService {

    RCTRegistrationConfirmation register(Patient patient, User user, RCTFacility facility);

    boolean isPatientRegisteredIntoRCT(Integer motechId);

    RCTFacility getRCTFacilityById(Integer facilityId);
}
