package org.motechproject.server.svc;

import org.motechproject.server.model.Facility;
import org.motechproject.server.model.rct.PhoneOwnershipType;
import org.motechproject.server.model.rct.Stratum;
import org.motechproject.ws.Patient;
import org.motechproject.ws.rct.PregnancyTrimester;
import org.motechproject.ws.rct.RCTRegistrationConfirmation;
import org.openmrs.User;

public interface RCTService {

    RCTRegistrationConfirmation register(Patient patient, User user, Facility facility);
    Stratum stratumFor(Facility facility, PhoneOwnershipType phoneOwnershipType, PregnancyTrimester trimester);
}
