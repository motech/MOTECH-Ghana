package org.motechproject.server.omod.factory;

import org.motechproject.server.omod.web.model.District;
import org.motechproject.server.omod.web.model.KassenaNankana;
import org.motechproject.server.omod.web.model.KassenaNankanaWest;


public class DistrictFactory {
    public District getDistrictWithName(String countyDistrict) {
        if ("Kassena-Nankana".equalsIgnoreCase(countyDistrict))
            return new KassenaNankana();
        else if ("Kassena-Nankana West".equalsIgnoreCase(countyDistrict))
            return new KassenaNankanaWest();
        return new District("Unknown");
    }
}
