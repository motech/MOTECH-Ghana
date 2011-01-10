package org.motechproject.server.omod;

import org.openmrs.User;

public interface IdentifierGenerator {

    public Integer generateFacilityId();

    public Integer generateCommunityId();

    public String generateStaffId();

    public String generateMotechId();

    public void excludeIdForGenerator(User staff, String motechIdString);
}
