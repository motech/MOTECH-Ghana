package org.motechproject.server.model.db;

import org.motechproject.server.model.Facility;
import org.motechproject.server.model.rct.PhoneOwnershipType;
import org.motechproject.server.model.rct.RCTFacility;
import org.motechproject.server.model.rct.RCTPatient;
import org.motechproject.server.model.rct.Stratum;
import org.motechproject.ws.rct.PregnancyTrimester;

public interface RctDAO {
    Stratum stratumWith(RCTFacility facility, PhoneOwnershipType phoneOwnershipType, PregnancyTrimester trimester);

    RCTPatient saveRCTPatient(RCTPatient rctPatient);

    Stratum updateStratum(Stratum object);

    Boolean isPatientRegisteredIntoRCT(Integer motechId);

    RCTFacility getRCTFacility(Integer facilityId);
}
