package org.motechproject.server.factory;


import org.motechproject.server.model.ghana.KassenaNankanaWest;
import org.motechproject.server.model.ghana.District;
import org.motechproject.server.model.ghana.KassenaNankana;

public class DistrictFactory {
    public District getDistrictWithName(String countyDistrict) {
        if ("Kassena-Nankana".equalsIgnoreCase(countyDistrict))
            return new KassenaNankana();
        else if ("Kassena-Nankana West".equalsIgnoreCase(countyDistrict))
            return new KassenaNankanaWest();
        return new District("Unknown");
    }
}
